/**
 * Copyright © 2011,2013 Konstantin Livitski
 * 
 * This file is part of n-Puzzle application. n-Puzzle application is free
 * software; you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * 
 * n-Puzzle application contains adaptations of artwork covered by the Creative
 * Commons Attribution-ShareAlike 3.0 Unported license. Please refer to the
 * NOTICE.md file at the root of this distribution or repository for licensing
 * terms that apply to that artwork.
 * 
 * n-Puzzle application is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * n-Puzzle application; if not, see the LICENSE/gpl.txt file of this distribution
 * or visit <http://www.gnu.org/licenses>.
 */
package com.liuhanyu.recipe.ui.pintu.game;

/**
 * Allows implementors to receive notifications of
 * tiles changing their {@link Tile#isOnTarget() on target}
 * status.
 */
public interface TileOnTargetListener
{
 /**
  * Receives notifications of
  * tiles changing their {@link Tile#isOnTarget() on target}
  * status.
  * @param tile the tile that changed status
  * @param onTarget the new {@link Tile#isOnTarget() on target status}
  * value
  */
 public void tileOnTargetStateChanged(Tile tile, boolean onTarget);
}
