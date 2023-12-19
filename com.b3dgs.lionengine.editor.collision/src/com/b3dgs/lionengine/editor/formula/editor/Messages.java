/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.editor.formula.editor;

import java.util.Locale;

import org.eclipse.osgi.util.NLS;

/**
 * Messages internationalization.
 */
public final class Messages extends NLS
{
    /** Edit formulas dialog title. */
    public static String Title;
    /** Edit formulas dialog title header. */
    public static String HeaderTitle;
    /** Edit formulas dialog description header. */
    public static String HeaderDesc;
    /** Edit formulas preview. */
    public static String Preview;
    /** Edit formulas template. */
    public static String Template;
    /** Edit formulas range. */
    public static String Range;
    /** Range output. */
    public static String RangeOutput;
    /** Range minimum X. */
    public static String RangeMinX;
    /** Range maximum X. */
    public static String RangeMaxX;
    /** Range minimum Y. */
    public static String RangeMinY;
    /** Range maximum Y. */
    public static String RangeMaxY;
    /** Edit formulas function. */
    public static String Function;
    /** Edit formulas function type. */
    public static String FunctionType;
    /** Edit formulas function linear A. */
    public static String FunctionLinearA;
    /** Edit formulas function linear B. */
    public static String FunctionLinearB;
    /** Edit formulas constraint. */
    public static String Constraint;
    /** Edit formulas constraint top. */
    public static String ConstraintTop;
    /** Edit formulas constraint bottom. */
    public static String ConstraintBottom;
    /** Edit formulas constraint left. */
    public static String ConstraintLeft;
    /** Edit formulas constraint right. */
    public static String ConstraintRight;

    /**
     * Initialize.
     */
    static
    {
        NLS.initializeMessages(Messages.class.getName().toLowerCase(Locale.ENGLISH), Messages.class);
    }
}
