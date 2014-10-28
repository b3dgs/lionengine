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
package com.b3dgs.lionengine.editor;

import org.eclipse.jface.dialogs.IInputValidator;

/**
 * Allows to check an input text and validate it.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class InputValidator
        implements IInputValidator
{
    /** Must match a name. */
    public static final String NAME_MATCH = "[a-zA-z0-9]+";
    /** Must match a double. */
    public static final String DOUBLE_MATCH = "[0-9]{1,13}(\\.[0-9]*)?";

    /** Expected matches. */
    private final String matches;
    /** Error message. */
    private final String errorText;

    /**
     * Create an input validator.
     * 
     * @param matches The expected matches.
     * @param errorText The text error.
     */
    public InputValidator(String matches, String errorText)
    {
        this.matches = matches;
        this.errorText = errorText;
    }

    /*
     * IInputValidator
     */

    @Override
    public String isValid(String newText)
    {
        if (!newText.matches(matches))
        {
            return errorText;
        }
        return null;
    }
}
