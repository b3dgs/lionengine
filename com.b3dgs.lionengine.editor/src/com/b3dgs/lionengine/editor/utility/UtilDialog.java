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
package com.b3dgs.lionengine.editor.utility;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.editor.project.Project;

/**
 * Series of tool functions around the editor related to dialogs.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class UtilDialog
{
    /** Xml filter. */
    private static final String[] XML = new String[]
    {
        "*.xml"
    };

    /**
     * Select a file from a dialog and returns its path relative to the starting path.
     * 
     * @param shell The shell parent.
     * @param path The starting path.
     * @param openSave <code>true</code> to open, <code>false</code> to save.
     * @param extensions The filtered extensions.
     * @return The selected file path.
     */
    public static String selectFile(Shell shell, String path, boolean openSave, String... extensions)
    {
        final FileDialog fileDialog;
        if (openSave)
        {
            fileDialog = new FileDialog(shell, SWT.OPEN);
        }
        else
        {
            fileDialog = new FileDialog(shell, SWT.SAVE);
        }
        fileDialog.setFilterPath(path);
        fileDialog.setFilterExtensions(extensions);
        final String file = fileDialog.open();
        if (file != null)
        {
            final Path reference = Paths.get(new File(path).toURI());
            final Path target = Paths.get(new File(file).toURI());
            return reference.relativize(target).toString();
        }
        return null;
    }

    /**
     * Select a media folder from dialog.
     * 
     * @param parent The shell parent.
     * @return The media folder, <code>null</code> if none.
     */
    public static File selectResourceFolder(Shell parent)
    {
        final DirectoryDialog fileDialog = new DirectoryDialog(parent, SWT.OPEN);
        fileDialog.setFilterPath(Project.getActive().getResourcesPath().getAbsolutePath());
        final String folder = fileDialog.open();
        if (folder != null)
        {
            return new File(folder);
        }
        return null;
    }

    /**
     * Select a media file from dialog.
     * 
     * @param parent The shell parent.
     * @param openSave <code>true</code> to open, <code>false</code> to save.
     * @param extensionsName The filtered extensions name.
     * @param extensions The filtered extensions.
     * @return The media file, <code>null</code> if none.
     */
    public static File selectResourceFile(Shell parent, boolean openSave, String[] extensionsName, String[] extensions)
    {
        final FileDialog fileDialog;
        if (openSave)
        {
            fileDialog = new FileDialog(parent, SWT.OPEN);
        }
        else
        {
            fileDialog = new FileDialog(parent, SWT.SAVE);
        }
        fileDialog.setFilterPath(Project.getActive().getResourcesPath().getAbsolutePath());
        fileDialog.setFilterNames(extensionsName);
        fileDialog.setFilterExtensions(extensions);
        final String file = fileDialog.open();
        if (file != null)
        {
            return new File(file);
        }
        return null;
    }

    /**
     * Select a media file from dialog.
     * 
     * @param parent The shell parent.
     * @param openSave <code>true</code> to open, <code>false</code> to save.
     * @param description The type description.
     * @return The media file, <code>null</code> if none.
     */
    public static File selectResourceXml(Shell parent, boolean openSave, String description)
    {
        final FileDialog fileDialog;
        if (openSave)
        {
            fileDialog = new FileDialog(parent, SWT.OPEN);
        }
        else
        {
            fileDialog = new FileDialog(parent, SWT.SAVE);
        }
        fileDialog.setFilterPath(Project.getActive().getResourcesPath().getAbsolutePath());
        fileDialog.setFilterNames(new String[]
        {
            description
        });
        fileDialog.setFilterExtensions(XML);
        final String file = fileDialog.open();
        if (file != null)
        {
            return new File(file);
        }
        return null;
    }

    /**
     * Select media files from dialog.
     * 
     * @param parent The shell parent.
     * @param extensionsName The filtered extensions name.
     * @param extensions The filtered extensions.
     * @return The media files.
     */
    public static File[] selectResourceFiles(Shell parent, String[] extensionsName, String[] extensions)
    {
        final FileDialog fileDialog = new FileDialog(parent, SWT.OPEN | SWT.MULTI);
        fileDialog.setFilterPath(Project.getActive().getResourcesPath().getAbsolutePath());
        fileDialog.setFilterNames(extensionsName);
        fileDialog.setFilterExtensions(extensions);
        final String selection = fileDialog.open();

        final String[] names = fileDialog.getFileNames();
        final File[] files = new File[names.length];
        for (int i = 0; i < names.length; i++)
        {
            files[i] = new File(new File(selection).getParentFile(), names[i]);
        }
        return files;
    }

    /**
     * Show an info dialog.
     * 
     * @param title The info title.
     * @param message The info message.
     */
    public static void info(String title, String message)
    {
        MessageDialog.openInformation(Display.getDefault().getActiveShell(), title, message);
    }

    /**
     * Show an error dialog.
     * 
     * @param title The error title.
     * @param message The error message.
     */
    public static void error(String title, String message)
    {
        MessageDialog.openError(Display.getDefault().getActiveShell(), title, message);
    }

    /**
     * Private constructor.
     */
    private UtilDialog()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
