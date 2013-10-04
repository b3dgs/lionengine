/*
 * Copyright (C) 2013 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.b3dgs.lionengine.core;

import java.awt.event.KeyEvent;

/**
 * Some standard java key.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public interface Key
{
    /** Arrow up key. */
    Integer UP = Integer.valueOf(KeyEvent.VK_UP);
    /** Arrow down key. */
    Integer DOWN = Integer.valueOf(KeyEvent.VK_DOWN);
    /** Arrow right key. */
    Integer RIGHT = Integer.valueOf(KeyEvent.VK_RIGHT);
    /** Arrow left key. */
    Integer LEFT = Integer.valueOf(KeyEvent.VK_LEFT);
    /** CTRL key. */
    Integer CONTROL = Integer.valueOf(KeyEvent.VK_CONTROL);
    /** ALT key. */
    Integer ALT = Integer.valueOf(KeyEvent.VK_ALT);
    /** Escape key. */
    Integer ESCAPE = Integer.valueOf(KeyEvent.VK_ESCAPE);
    /** Space key. */
    Integer SPACE = Integer.valueOf(KeyEvent.VK_SPACE);
    /** Tab key. */
    Integer TAB = Integer.valueOf(KeyEvent.VK_TAB);
    /** Enter key. */
    Integer ENTER = Integer.valueOf(KeyEvent.VK_ENTER);
}
