package com.github.meinders.common.opengl.example;

import java.awt.*;
import java.awt.event.*;
import javax.media.opengl.*;
import javax.media.opengl.awt.*;
import javax.swing.*;

import com.jogamp.opengl.util.*;

class ExampleRunner implements Runnable
{
	private String title;

	private GLEventListener listener;

	public ExampleRunner(String title, GLEventListener listener)
	{
		super();
		this.title = title;
		this.listener = listener;
	}

	public void run()
	{
		GLCapabilities capabilities = new GLCapabilities( GLProfile.getGL2GL3() );
		capabilities.setAccumRedBits(16);
		capabilities.setAccumGreenBits(16);
		capabilities.setAccumBlueBits(16);
		// capabilities.setAccumAlphaBits(16);
		// capabilities.setStencilBits(8);

		GLCanvas viewComponent = new GLCanvas(capabilities);
		viewComponent.addGLEventListener(listener);
		viewComponent.setPreferredSize(new Dimension(1024, 768));

		final AnimatorBase animator = new FPSAnimator(60);
		animator.add(viewComponent);
		animator.start();

		JFrame frame = new JFrame(title);
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		frame.add(viewComponent);
		// frame.add(new JLabel(new ImageIcon(createBumpMapImage())),
		// BorderLayout.WEST);
		// frame.add(new JLabel(new ImageIcon(
		// createNormalMapFromBumpMap(createBumpMapImage()))));
		frame.pack();
		frame.setVisible(true);

		viewComponent.requestFocus();

		frame.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosed(WindowEvent e)
			{
				animator.stop();
			}
		});
	}
}