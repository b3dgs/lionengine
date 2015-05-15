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
package com.b3dgs.lionengine.editor;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.UtilFile;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.swt.UtilityMedia;
import com.b3dgs.lionengine.editor.project.Project;
import com.b3dgs.lionengine.editor.project.Property;
import com.b3dgs.lionengine.editor.project.tester.FolderTypeTester;
import com.b3dgs.lionengine.game.Camera;
import com.b3dgs.lionengine.game.configurer.ConfigObject;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.geom.Geom;
import com.b3dgs.lionengine.geom.Point;
import com.b3dgs.lionengine.stream.Stream;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Series of tool functions around the editor.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class Tools
{
    /** Templates extension. */
    public static final String TEMPLATE_EXTENSION = "template";
    /** Templates directory. */
    public static final String TEMPLATES_DIR = "templates";
    /** Template object. */
    public static final String TEMPLATE_OBJECT = "object." + Tools.TEMPLATE_EXTENSION;
    /** Template sheets. */
    public static final String TEMPLATE_SHEETS = "sheets." + Tools.TEMPLATE_EXTENSION;
    /** Template sheets. */
    public static final String TEMPLATE_GROUPS = "groups." + Tools.TEMPLATE_EXTENSION;
    /** Template class area. */
    public static final String TEMPLATE_CLASS_AREA = "%CLASS%";
    /** Template setup area. */
    public static final String TEMPLATE_SETUP_AREA = "%SETUP%";
    /** Template sheets tile width area. */
    public static final String TEMPLATE_SHEETS_WIDTH = "%WIDTH%";
    /** Template sheets tile height area. */
    public static final String TEMPLATE_SHEETS_HEIGHT = "%HEIGHT%";
    /** Folder type name node. */
    private static final String NODE_FOLDER_TYPE_NAME = "lionengine:name";
    /** Create directory error. */
    private static final String ERROR_CREATE_DIRECTORY = "Unable to create the following directory: ";
    /** Folder type directory error. */
    private static final String ERROR_FOLDER_TYPE = "Path is not a folder type: ";
    /** Buffer size. */
    private static final int BUFFER_SIZE = 4096;

    /**
     * Get a template file from it name. The template must be in {@link #TEMPLATES_DIR} folder.
     * 
     * @param template The template name.
     * @return The template file.
     */
    public static File getTemplate(String template)
    {
        return UtilEclipse.getFile(UtilFile.getPath(Tools.TEMPLATES_DIR, template));
    }

    /**
     * Get the class from its file. The file must be in {@link Project#getClassesPath()} folder.
     * 
     * @param file The class file.
     * @return The class reference.
     * @throws LionEngineException If not able to load the class.
     */
    public static Class<?> getClass(File file) throws LionEngineException
    {
        final Project project = Project.getActive();
        final Media media = project.getClassMedia(file);
        final String name = media.getPath().replace("." + Property.EXTENSION_CLASS, "").replace(File.separator, ".");
        return project.getClass(name);
    }

    /**
     * Get the class from media file, by reading the attribute {@link ConfigObject#CLASS} attribute.
     * 
     * @param media The media descriptor.
     * @return The class reference.
     * @throws LionEngineException If not able to create the class.
     */
    public static Class<?> getClass(Media media) throws LionEngineException
    {
        final XmlNode root = Stream.loadXml(media);
        final String className = root.getChild(ConfigObject.CLASS).getText();
        return Project.getActive().getClass(className);
    }

    /**
     * Get the folder type file.
     * 
     * @param path The type folder.
     * @return The type file.
     * @throws FileNotFoundException If not a type folder.
     */
    public static File getFolderTypeFile(File path) throws FileNotFoundException
    {
        if (path.isDirectory())
        {
            final File[] files = path.listFiles();
            if (files != null)
            {
                for (final File file : files)
                {
                    if (FolderTypeTester.isFolderTypeFile(file))
                    {
                        return file;
                    }
                }
            }
        }
        throw new FileNotFoundException(Tools.ERROR_FOLDER_TYPE + path.getPath());
    }

    /**
     * Get the folder type name.
     * 
     * @param path The type folder.
     * @return The type name.
     */
    public static String getObjectsFolderTypeName(File path)
    {
        try
        {
            final File type = Tools.getFolderTypeFile(path);
            final XmlNode typeNode = Stream.loadXml(UtilityMedia.get(type));
            return typeNode.getChild(Tools.NODE_FOLDER_TYPE_NAME).getText();
        }
        catch (final FileNotFoundException exception)
        {
            return path.getName();
        }
    }

    /**
     * Get the tile location over the mouse.
     * 
     * @param map The map reference.
     * @param camera The camera reference.
     * @param mx The mouse X.
     * @param my The mouse Y.
     * @return The tile location in absolute location.
     */
    public static Point getMouseTile(MapTile map, Camera camera, int mx, int my)
    {
        final int tw = map.getTileWidth();
        final int th = map.getTileHeight();
        final int h = UtilMath.getRounded(camera.getHeight(), th) - map.getTileHeight();
        final int x = (int) camera.getX() + UtilMath.getRounded(mx, tw);
        final int y = (int) camera.getY() - UtilMath.getRounded(my, th) + h;
        return Geom.createPoint(x, y);
    }

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
        final FileDialog fileDialog = new FileDialog(shell, openSave ? SWT.OPEN : SWT.SAVE);
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
     * Select a class file from dialog.
     * 
     * @param parent The shell parent.
     * @return The class file, <code>null</code> if none.
     */
    public static File selectClassFile(Shell parent)
    {
        final FileDialog fileDialog = new FileDialog(parent, SWT.OPEN);
        fileDialog.setFilterPath(Project.getActive().getClassesPath().getAbsolutePath());
        fileDialog.setFilterExtensions(new String[]
        {
            "*.class"
        });
        final String file = fileDialog.open();
        if (file != null)
        {
            return new File(file);
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
        final FileDialog fileDialog = new FileDialog(parent, openSave ? SWT.OPEN : SWT.SAVE);
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
     * Extracts ZIP content to specified directory.
     * 
     * @param zipPath Path of the ZIP file.
     * @param destinationPath Destination path to extract files.
     * @throws IOException If an error occurred.
     */
    public static void unzip(String zipPath, String destinationPath) throws IOException
    {
        final File destDir = new File(destinationPath);
        if (!destDir.exists())
        {
            if (!destDir.exists() && !destDir.mkdir())
            {
                throw new IOException(Tools.ERROR_CREATE_DIRECTORY + destDir.toString());
            }
        }
        try (ZipInputStream zip = new ZipInputStream(new FileInputStream(zipPath)))
        {
            ZipEntry entry = zip.getNextEntry();
            while (entry != null)
            {
                final String filePath = destinationPath + File.separator + entry.getName();
                if (!entry.isDirectory())
                {
                    Tools.extractFile(zip, filePath);
                }
                else
                {
                    final File dir = new File(filePath);
                    if (!dir.exists() && !dir.mkdir())
                    {
                        throw new IOException(Tools.ERROR_CREATE_DIRECTORY + dir.toString());
                    }
                }
                zip.closeEntry();
                entry = zip.getNextEntry();
            }
        }
    }

    /**
     * Extracts a ZIP entry.
     * 
     * @param zip The ZIP stream.
     * @param filePath The output file path.
     * @throws IOException If an error occurred.
     */
    private static void extractFile(ZipInputStream zip, String filePath) throws IOException
    {
        try (BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(filePath)))
        {
            final byte[] bytesIn = new byte[Tools.BUFFER_SIZE];
            int read = 0;
            while ((read = zip.read(bytesIn)) != -1)
            {
                output.write(bytesIn, 0, read);
            }
        }
    }

    /**
     * Private constructor.
     */
    private Tools()
    {
        throw new RuntimeException();
    }
}
