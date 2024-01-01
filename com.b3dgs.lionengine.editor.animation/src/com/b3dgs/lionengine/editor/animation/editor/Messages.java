/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.b3dgs.lionengine.editor.animation.editor;

import java.util.Locale;

import org.eclipse.osgi.util.NLS;

/**
 * Messages internationalization.
 */
public final class Messages extends NLS
{
    /** Animation editor title. */
    public static String Title;
    /** Animation editor sheet. */
    public static String Sheet;
    /** Animation editor animator. */
    public static String Animator;
    /** Animation editor animation. */
    public static String Animation;
    /** Animation properties first frame. */
    public static String FirstFrame;
    /** Animation properties last frame. */
    public static String LastFrame;
    /** Animation properties anim speed. */
    public static String AnimSpeed;
    /** Animation properties reverse. */
    public static String Reverse;
    /** Animation properties repeat. */
    public static String Repeat;

    /**
     * Initialize.
     */
    static
    {
        NLS.initializeMessages(Messages.class.getName().toLowerCase(Locale.ENGLISH), Messages.class);
    }
}
