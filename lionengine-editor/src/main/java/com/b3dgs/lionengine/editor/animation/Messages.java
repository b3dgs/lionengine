/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.editor.animation;

import org.eclipse.osgi.util.NLS;

/**
 * Messages internationalization.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class Messages
        extends NLS
{
    /** Bundle name. */
    private static final String BUNDLE_NAME = "com.b3dgs.lionengine.editor.messages"; //$NON-NLS-1$

    /** Animation editor title. */
    public static String AnimationEditor_Title;
    /** Animation editor sheet. */
    public static String AnimationEditor_Sheet;
    /** Animation editor animator. */
    public static String AnimationEditor_Animator;
    /** Animation editor animation. */
    public static String AnimationEditor_Animation;
    /** Animation properties first frame. */
    public static String AnimationProperties_FirstFrame;
    /** Animation properties last frame. */
    public static String AnimationProperties_LastFrame;
    /** Animation properties anim speed. */
    public static String AnimationProperties_AnimSpeed;
    /** Animation properties reverse. */
    public static String AnimationProperties_Reverse;
    /** Animation properties repeat. */
    public static String AnimationProperties_Repeat;

    /**
     * Initialize.
     */
    static
    {
        NLS.initializeMessages(Messages.BUNDLE_NAME, Messages.class);
    }

    /**
     * Constructor.
     */
    private Messages()
    {
        // Private constructor
    }
}
