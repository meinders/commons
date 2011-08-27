varying vec3 vertex;
varying vec3 normal;
uniform sampler2DShadow shadowMap;

void main()
{	
	vec4 shadowCoordinate = gl_TexCoord[7];
	
 	float shadow = 1.0;
 	if (shadowCoordinate.w > 0.0)
 	{
		shadowCoordinate.z -= 0.02;
		float offset = shadowCoordinate.w / 256.0;
		shadow += shadow2DProj(shadowMap, shadowCoordinate + vec4(-offset, -offset, 0.0, 0.0)).z;
		shadow += shadow2DProj(shadowMap, shadowCoordinate + vec4(0.0, -offset, 0.0, 0.0)).z;
		shadow += shadow2DProj(shadowMap, shadowCoordinate + vec4(offset, -offset, 0.0, 0.0)).z;
		shadow += shadow2DProj(shadowMap, shadowCoordinate + vec4(-offset, 0.0, 0.0, 0.0)).z;
		shadow += shadow2DProj(shadowMap, shadowCoordinate + vec4(0.0, 0.0, 0.0, 0.0)).z;
		shadow += shadow2DProj(shadowMap, shadowCoordinate + vec4(offset, 0.0, 0.0, 0.0)).z;
		shadow += shadow2DProj(shadowMap, shadowCoordinate + vec4(-offset, offset, 0.0, 0.0)).z;
		shadow += shadow2DProj(shadowMap, shadowCoordinate + vec4(0.0, offset, 0.0, 0.0)).z;
		shadow += shadow2DProj(shadowMap, shadowCoordinate + vec4(offset, offset, 0.0, 0.0)).z;
		shadow /= 9.0;
	}
	
    vec3 N = normalize(normal);
    vec3 E = normalize(-vertex);
    
    vec3 D = gl_LightSource[0].position.xyz - vertex;
    vec3 L = normalize(D);
    vec3 R = normalize(-reflect(L, N));
    
    vec3 ambient = gl_FrontLightProduct[0].ambient.rgb;
	vec3 diffuse = gl_LightSource[0].diffuse.rgb * max(dot(N, L), 0.0);

	gl_FragColor.rgb = ambient + diffuse * gl_FrontMaterial.diffuse.rgb * shadow + gl_FrontMaterial.ambient.rgb;
	gl_FragColor.a = 1.0;
}

#ifdef DISABLED
/*
 * Lighting for front-facing fragments.
 */
vec4 frontLighting( in vec4 color )
{
        vec3 result = vec3( 0.0 , 0.0 , 0.0 );

        vec3 N = normalize( normal );
        vec3 E = normalize( -vertex );

#ifdef MULTIPASS_LIGHTING
        const int i = 0;
#else
        const int lightCount = 3;
        for ( int i = 0; i < lightCount; i++ )
#endif
        {
                /*
                 * Note on 'gl_LightSource' vs 'gl_FrontLightProduct':
                 * For diffuse color, 'gl_FrontLightProduct.diffuse' is unsuitable,
                 * because it includes the material's diffuse color, which is also
                 * included in 'color' (multiplied with 'diffuse' below.)
                 */
                if ( gl_LightSource[ i ].position.w == 0.0 )
                {
                        /*
                         * Directional light.
                         */
                        vec3 L = normalize( gl_LightSource[ i ].position.xyz );
                        vec3 H = gl_LightSource[ i ].halfVector.xyz;
                        vec3 ambient  = gl_FrontLightProduct[ i ].ambient .rgb;
                        vec3 diffuse  = gl_LightSource      [ i ].diffuse .rgb *      max( dot( N , L ) , 0.0 );
                        vec3 specular = gl_FrontLightProduct[ i ].specular.rgb * pow( max( dot( N , H ) , 0.0 ) , gl_FrontMaterial.shininess );

                        result += ambient + ( color.rgb * diffuse + specular ) * shadow();
                }
                else
                {
                        vec3 D = gl_LightSource[ i ].position.xyz - vertex;
                        vec3 L = normalize( D );
                        vec3 R = normalize( -reflect( L , N ) );

                        float dist = length( D );
                        float attenuation = 1.0 / (
                                gl_LightSource[ i ].constantAttenuation  +
                                gl_LightSource[ i ].linearAttenuation    * dist +
                                gl_LightSource[ i ].quadraticAttenuation * dist * dist );

                        vec3 ambient = gl_FrontLightProduct[ i ].ambient.rgb;
                        vec3 diffuse;
                        vec3 specular;

                        if ( gl_LightSource[ i ].spotCutoff == 180.0 )
                        {
                                /*
                                 * Point light.
                                 */
                                diffuse  = gl_LightSource      [ i ].diffuse .rgb * attenuation *      max( dot( N , L ) , 0.0 );
                                specular = gl_FrontLightProduct[ i ].specular.rgb * attenuation * pow( max( dot( R , E ) , 0.0 ) , gl_FrontMaterial.shininess );
                        }
                        else
                        {
                                float spotEffect = dot( normalize( gl_LightSource[ i ].spotDirection ) , -L );
                                if ( spotEffect > gl_LightSource[ i ].spotCosCutoff )
                                {
                                        attenuation *= pow( spotEffect , gl_LightSource[ i ].spotExponent );

                                        diffuse   = gl_LightSource      [ i ].diffuse .rgb * attenuation *      max( dot( N , L ) , 0.0 );
                                        specular  = gl_FrontLightProduct[ i ].specular.rgb * attenuation * pow( max( dot( R , E ) , 0.0 ) , gl_FrontMaterial.shininess );
                                }
                                else
                                {
                                        diffuse   = vec3( 0.0 );
                                        specular  = vec3( 0.0 );
                                }
                        }

                        result += ambient + ( color.rgb * diffuse + specular ) * shadow();
                }
        }

        return vec4( color.rgb * gl_FrontLightModelProduct.sceneColor.rgb + result.rgb , color.a );
}
#endif