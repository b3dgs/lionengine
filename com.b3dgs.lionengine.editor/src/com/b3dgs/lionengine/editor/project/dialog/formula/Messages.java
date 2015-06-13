/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.editor.project.dialog.formula;

import org.eclipse.osgi.util.NLS;

import com.b3dgs.lionengine.editor.Activator;

/**
 * Messages internationalization.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class Messages
        extends NLS
{
    /** Bundle name. */
    private static final String BUNDLE_NAME = Activator.PLUGIN_ID + ".project.dialog.formula.messages"; //$NON-NLS-1$

    /** Edit formulas dialog title. */
    public static String EditFormulasDialog_Title;
    /** Edit formulas dialog title header. */
    public static String EditFormulasDialog_HeaderTitle;
    /** Edit formulas dialog description header. */
    public static String EditFormulasDialog_HeaderDesc;
    /** Edit formulas range. */
    public static String EditFormulasDialog_Range;
    /** Range output. */
    public static String EditFormulasDialog_RangeOutput;
    /** Range minimum X. */
    public static String EditFormulasDialog_RangeMinX;
    /** Range maximum X. */
    public static String EditFormulasDialog_RangeMaxX;
    /** Range minimum Y. */
    public static String EditFormulasDialog_RangeMinY;
    /** Range maximum Y. */
    public static String EditFormulasDialog_RangeMaxY;
    /** Edit formulas function. */
    public static String EditFormulasDialog_Function;
    /** Edit formulas function type. */
    public static String EditFormulasDialog_FunctionType;
    /** Edit formulas function linear A. */
    public static String EditFormulasDialog_FunctionLinearA;
    /** Edit formulas function linear B. */
    public static String EditFormulasDialog_FunctionLinearB;
    /** Edit formulas constraint. */
    public static String EditFormulasDialog_Constraint;
    /** Edit formulas constraint top. */
    public static String EditFormulasDialog_ConstraintTop;
    /** Edit formulas constraint bottom. */
    public static String EditFormulasDialog_ConstraintBottom;
    /** Edit formulas constraint left. */
    public static String EditFormulasDialog_ConstraintLeft;
    /** Edit formulas constraint right. */
    public static String EditFormulasDialog_ConstraintRight;

    /**
     * Initialize.
     */
    static
    {
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    /**
     * Private constructor.
     */
    private Messages()
    {
        throw new RuntimeException();
    }
}
