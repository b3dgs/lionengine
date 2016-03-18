/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.editor.validator;

import java.io.File;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.editor.project.ProjectModel;
import com.b3dgs.lionengine.game.object.Factory;

/**
 * Allows to check an input text and validate it.
 */
public class InputValidator implements IInputValidator
{
    /** Must match a name. */
    public static final String NAME_MATCH = "[a-zA-z0-9]+";
    /** Must match a double. */
    public static final String DOUBLE_MATCH = "[0-9]{1,13}(\\.[0-9]*)?";
    /** Must match an integer. */
    public static final String INTEGER_MATCH = "^-?[0-9]{0,9}";
    /** Must match a positive integer. */
    public static final String INTEGER_POSITIVE_MATCH = "[0-9]{0,9}";
    /** Must match a strict positive integer. */
    public static final String INTEGER_POSITIVE_STRICT_MATCH = "[1-9][0-9]{0,9}";

    /**
     * Get a validated file name from a default one.
     * 
     * @param parent The parent shell.
     * @param title The input title.
     * @param text The input text content.
     * @param filename The default filename value.
     * @param action The action called if file does not already exists.
     */
    public static void getFile(Shell parent, String title, String text, String filename, InputAction action)
    {
        final String value = filename.replace(Factory.FILE_DATA_DOT_EXTENSION, Constant.EMPTY_STRING);
        final Media selection = ProjectModel.INSTANCE.getSelection();
        final InputValidator validator = new InputValidator(NAME_MATCH, Messages.ErrorName);
        final InputDialog input = new InputDialog(parent, title, text, value, validator);
        final int code = input.open();
        if (code == Window.OK)
        {
            final String name = input.getValue();
            final File file = new File(selection.getFile(), name + Factory.FILE_DATA_DOT_EXTENSION);
            if (file.exists())
            {
                MessageDialog.openError(parent, Messages.ErrorTitle, Messages.ErrorText);
                getFile(parent, title, text, filename, action);
            }
            action.handle(file);
        }
    }

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
