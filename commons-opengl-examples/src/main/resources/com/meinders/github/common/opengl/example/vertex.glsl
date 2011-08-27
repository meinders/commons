varying vec3 vertex;
varying vec3 normal;
void main()
{
	gl_TexCoord[7] = gl_TextureMatrix[7] * gl_ModelViewMatrix * gl_Vertex;
  	gl_Position = ftransform();
	gl_FrontColor = gl_Color;

	vertex = (gl_ModelViewMatrix * gl_Vertex).xyz;
	normal = gl_NormalMatrix * gl_Normal;
}
