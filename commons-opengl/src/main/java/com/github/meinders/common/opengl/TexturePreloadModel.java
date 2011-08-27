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

package com.github.meinders.common.opengl;

import java.io.*;
import java.util.*;

public interface TexturePreloadModel
{
	/**
	 * Returns a collection of one or more files that should be preloaded, given
	 * a file that was just scheduled to be loaded first.
	 *
	 * @param current The file that was just scheduled to be loaded.
	 * @param count The maximum number of files to be returned.
	 *
	 * @return Collection of files to preload next.
	 */
	Collection<File> getPreloadFiles(File current, int count);
}
