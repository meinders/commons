package com.github.meinders.common.opengl.example;

import javax.media.opengl.*;
import javax.swing.*;

public class Main
{
	public static void main(String[] args)
	{
		GLEventListener listener = new ShadowMapExample();
		SwingUtilities.invokeLater(new ExampleRunner("OpenGL Shadow Mapping",
		        listener));
	}
}
