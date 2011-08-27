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

package com.github.meinders.common.test;

import java.net.*;

/**
 * TODO: javadoc
 *
 * @version 0.8 (2004.12.28)
 * @author Gerrit Meinders
 */
public class Bootstrap extends com.github.meinders.common.Bootstrap
{
    public static void main(String[] args) {
        new Bootstrap(args).run();
    }

    protected Bootstrap(String[] args) {
        super(args);
    }

    protected String getMainClassName() {
        return "com.github.meinders.common.test.Main";
    }

    protected String getSplashTitle() {
        return "Frixus Commons";
    }

    protected URL getSplashImage() {
        return getClass().getResource("/images/splash.png");
    }
}
