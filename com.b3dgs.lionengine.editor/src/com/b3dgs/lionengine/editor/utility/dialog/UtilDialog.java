/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.editor.utility.dialog;

import java.util.Collection;
import java.util.Optional;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.editor.dialog.ResourceDialog;
import com.b3dgs.lionengine.editor.dialog.ResourceDialog.Type;
import com.b3dgs.lionengine.game.feature.Factory;

/**
 * Series of tool functions around the editor related to dialogs.
 */
public final class UtilDialog
{
    /** Xml filter. */
    private static final String[] XML_FILTER = new String[]
    {
        Factory.FILE_DATA_EXTENSION
    };

    /** Image filter. */
    private static final String[] IMAGE_FILTER = new String[]
    {
        "bmp", "png"
    };

    /**
     * Select a media folder from dialog.
     * 
     * @param parent The shell parent.
     * @return The media folder, <code>null</code> if none.
     */
    public static Optional<Media> selectResourceFolder(Shell parent)
    {
        final ResourceDialog dialog = new ResourceDialog(parent, false, true, Type.FOLDER);
        dialog.open();
        return getResult(dialog);
    }

    /**
     * Select a media file from dialog.
     * 
     * @param parent The shell parent.
     * @param openSave <code>true</code> to open, <code>false</code> to save.
     * @param extensions The filtered extensions.
     * @return The media file, <code>null</code> if none.
     */
    public static Optional<Media> selectResourceFile(Shell parent, boolean openSave, String[] extensions)
    {
        final ResourceDialog dialog;
        if (openSave)
        {
            dialog = new ResourceDialog(parent, false, openSave, Type.FILE, extensions);
        }
        else
        {
            dialog = new ResourceDialog(parent, false, openSave, Type.FOLDER, extensions);
        }
        dialog.open();
        return getResult(dialog);
    }

    /**
     * Select multiple media files from dialog.
     * 
     * @param parent The shell parent.
     * @param extensions The filtered extensions.
     * @return The media files.
     */
    public static Collection<Media> selectResourceFiles(Shell parent, String... extensions)
    {
        final ResourceDialog dialog = new ResourceDialog(parent, true, true, Type.FILE, extensions);
        dialog.open();
        return dialog.getSelection();
    }

    /**
     * Show an info dialog.
     * 
     * @param parent The parent shell.
     * @param title The info title.
     * @param message The info message.
     */
    public static void info(Shell parent, String title, String message)
    {
        MessageDialog.openInformation(parent, title, message);
    }

    /**
     * Show an error dialog.
     * 
     * @param parent The parent shell.
     * @param title The error title.
     * @param message The error message.
     */
    public static void error(Shell parent, String title, String message)
    {
        MessageDialog.openError(parent, title, message);
    }

    /**
     * List of supported XML formats.
     * 
     * @return Supported XML formats.
     */
    public static String[] getXmlFilter()
    {
        return XML_FILTER;
    }

    /**
     * List of supported images formats.
     * 
     * @return Supported images formats.
     */
    public static String[] getImageFilter()
    {
        return IMAGE_FILTER;
    }

    /**
     * Get the dialog result for single case.
     * 
     * @param dialog The dialog reference.
     * @return The optional single result.
     */
    private static Optional<Media> getResult(ResourceDialog dialog)
    {
        final Collection<Media> selection = dialog.getSelection();
        if (selection.isEmpty())
        {
            return Optional.empty();
        }
        return Optional.of(selection.iterator().next());
    }

    /**
     * Private constructor.
     */
    private UtilDialog()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
