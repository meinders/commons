/*
 * Copyright 2018 Gerrit Meinders
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.meinders.common.animation;

import java.awt.*;
import javax.swing.*;

public class TestCubic1D {
	// TODO: Write unit test. For now confirmed visually.
	public static void main(String[] args) {
		Cubic1D cubic1 = new Cubic1D(0.0, 0.0, 0.0, 15.0, 0.0, 1.0);

		final Cubic1D[] cubics = { cubic1,
		        Cubic1D.branch(cubic1, 0.3, 0.0, 0.0, 1.3),
		        Cubic1D.branch(cubic1, 0.3, 0.0, 0.0, 1.8),
		        Cubic1D.branch(cubic1, 0.3, 0.0, 0.0, 0.5),

		// new Cubic1D(0.0, 5.0, 0.0, 2.0, 5.0, 2.0),
		// new Cubic1D(0.0, 10.0, 0.0, 20.0, 10.0, 2.0)
		};

		JFrame frame = new JFrame();
		frame.setContentPane(new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);

				double tscale = 1.0;
				double yscale = 10.0;
				double yoffset = 0.0;

				for (Cubic1D cubic : cubics) {
					double y1 = cubic.get(-0.25);
					for (int i = 1; i < getWidth() / tscale; i++) {
						double t1 = (double) (i - 1) / (double) getWidth();
						double t2 = (double) i / (double) getWidth();

						double y2 = cubic.get(t2 * 2.0 - 0.25);

						g.drawLine((int) (t1 * tscale * getWidth()), (int) (y1
						        * yscale + yoffset),
						        (int) (t2 * tscale * getWidth()), (int) (y2
						                * yscale + yoffset));

						y1 = y2;
					}
				}
			}
		});
		frame.setBounds(100, 100, 800, 600);
		frame.setVisible(true);
	}
}
