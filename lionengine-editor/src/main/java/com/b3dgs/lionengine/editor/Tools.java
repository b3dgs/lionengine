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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.UtilFile;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.UtilityMedia;
import com.b3dgs.lionengine.editor.project.Project;
import com.b3dgs.lionengine.editor.project.Property;
import com.b3dgs.lionengine.editor.world.WorldViewModel;
import com.b3dgs.lionengine.game.CameraGame;
import com.b3dgs.lionengine.game.FactoryObjectGame;
import com.b3dgs.lionengine.game.SetupGame;
import com.b3dgs.lionengine.game.configurable.Configurable;
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
    /** Template entity. */
    public static final String TEMPLATE_ENTITY = "entity." + Tools.TEMPLATE_EXTENSION;
    /** Create directory error. */
    private static final String CREATE_DIRECTORY_ERROR = "Unable to create the following directory: ";
    /** Buffer size. */
    private static final int BUFFER_SIZE = 4096;

    /**
     * Get a template file from it name.
     * 
     * @param template The template name.
     * @return The template file.
     */
    public static File getTemplate(String template)
    {
        return UtilEclipse.getFile(UtilFile.getPath(Tools.TEMPLATES_DIR, template));
    }

    /**
     * Get the configurable from the entity descriptor.
     * 
     * @param entity The entity descriptor.
     * @return The entity configurable reference.
     */
    public static Configurable getConfigurable(Media entity)
    {
        final FactoryObjectGame<?> factory = WorldViewModel.INSTANCE.getFactory();
        final SetupGame setup = factory.getSetup(entity);
        return setup.getConfigurable();
    }

    /**
     * Get the object class from its name.
     * 
     * @param <O> The object class.
     * @param objectType The object type.
     * @param name The entity name.
     * @return The entity class reference.
     */
    public static <O> Class<? extends O> getObjectClass(Class<O> objectType, String name)
    {
        final Project project = Project.getActive();
        final File classesPath = project.getClassesPath();
        final List<File> classNames = UtilFile.getFilesByName(classesPath, name + "." + Property.EXTENSION_CLASS);

        // TODO handle the case when there is multiple class with the same name
        if (classNames.size() == 1)
        {
            final String path = classNames.get(0).getPath();
            final Media classPath = project.getClassMedia(path);
            final Class<? extends O> type = project.getClass(objectType, classPath);
            return type;
        }
        return null;
    }

    /**
     * Get the folder type name.
     * 
     * @param path The type folder.
     * @return The type name.
     * @throws LionEngineException If get name error, and so, this is not a type folder.
     */
    public static String getObjectsFolderTypeName(File path) throws LionEngineException
    {
        final File typeFile = new File(path, "type.xml");
        final XmlNode typeNode = Stream.loadXml(UtilityMedia.get(typeFile));
        return typeNode.getChild("name").getText();
    }

    /**
     * Get the tile over the mouse location.
     * 
     * @param map The map reference.
     * @param camera The camera reference.
     * @param mx The mouse X.
     * @param my The mouse Y.
     * @return The location in tile.
     */
    public static Point getMouseTile(MapTile<?> map, CameraGame camera, int mx, int my)
    {
        final int tw = map.getTileWidth();
        final int th = map.getTileHeight();
        final int h = UtilMath.getRounded(camera.getViewHeight(), th) - map.getTileHeight();
        final int x = camera.getLocationIntX() + UtilMath.getRounded(mx, tw);
        final int y = camera.getLocationIntY() - UtilMath.getRounded(my, th) + h;
        return Geom.createPoint(x, y);
    }

    /**
     * Select a file from a dialog and return its path relative to the starting path.
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
                throw new IOException(Tools.CREATE_DIRECTORY_ERROR + destDir.toString());
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
                        throw new IOException(Tools.CREATE_DIRECTORY_ERROR + dir.toString());
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
