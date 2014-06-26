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

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.UtilFile;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.UtilityMedia;
import com.b3dgs.lionengine.editor.project.Project;
import com.b3dgs.lionengine.editor.project.Property;
import com.b3dgs.lionengine.editor.world.WorldViewModel;
import com.b3dgs.lionengine.game.FactoryObjectGame;
import com.b3dgs.lionengine.game.ObjectGame;
import com.b3dgs.lionengine.game.SetupGame;
import com.b3dgs.lionengine.game.configurable.Configurable;
import com.b3dgs.lionengine.game.entity.EntityGame;
import com.b3dgs.lionengine.stream.Stream;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Series of tool functions around the editor.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class Tools
{
    /** Part error. */
    private static final String ERROR_PART = "Unable to find part: ";

    /**
     * Get the icon from its name.
     * 
     * @param icon The icon name.
     * @return The icon instance.
     */
    public static Image getIcon(String icon)
    {
        try
        {
            final ImageDescriptor image = ImageDescriptor.createFromURL(FileLocator.toFileURL(Activator.getContext()
                    .getBundle().getEntry(UtilFile.getPath("icons", icon))));
            return image.createImage();
        }
        catch (final IOException exception)
        {
            throw new LionEngineException(exception);
        }
    }

    /**
     * Get the icon from its name.
     * 
     * @param root The icon root.
     * @param icon The icon name.
     * @return The icon instance.
     */
    public static Image getIcon(String root, String icon)
    {
        try
        {
            final ImageDescriptor image = ImageDescriptor.createFromURL(FileLocator.toFileURL(Activator.getContext()
                    .getBundle().getEntry(UtilFile.getPath("icons", root, icon))));
            return image.createImage();
        }
        catch (final IOException exception)
        {
            throw new LionEngineException(exception);
        }
    }

    /**
     * Center the shell on screen.
     * 
     * @param shell The shell to center.
     */
    public static void center(Shell shell)
    {
        final Monitor primary = shell.getMonitor();
        final Rectangle bounds = primary.getBounds();
        final Rectangle rect = shell.getBounds();
        final int x = bounds.x + (bounds.width - rect.width) / 2;
        final int y = bounds.y + (bounds.height - rect.height) / 2;
        shell.setLocation(x, y);
    }

    /**
     * Get the file from its name, relative to the plugin path.
     * 
     * @param file The file name.
     * @return The file instance.
     */
    public static File getFile(String file)
    {
        try
        {
            final File root = FileLocator.getBundleFile(Activator.getContext().getBundle());
            return new File(root, file);
        }
        catch (final IOException exception)
        {
            throw new LionEngineException(exception);
        }
    }

    /**
     * Get the configurable from the entity descriptor.
     * 
     * @param entity The entity descriptor.
     * @return The entity configurable reference.
     */
    public static Configurable getConfigurable(Media entity)
    {
        final String entityName = entity.getFile().getName().replace("." + FactoryObjectGame.FILE_DATA_EXTENSION, "");
        final Class<?> entityClass = Tools.getEntityClass(entityName);
        final FactoryObjectGame<?, ?> factory = WorldViewModel.INSTANCE.getFactoryEntity();
        final SetupGame setup = factory.getSetup(entityClass, ObjectGame.class);
        return setup.getConfigurable();
    }

    /**
     * Get the entity class from its name.
     * 
     * @param name The entity name.
     * @return The entity class reference.
     */
    public static Class<? extends EntityGame> getEntityClass(String name)
    {
        final Project project = Project.getActive();
        final File classesPath = project.getClassesPath();
        final List<File> classNames = UtilFile.getFilesByName(classesPath, name + "." + Property.EXTENSION_CLASS);

        // TODO handle the case when there is multiple class with the same name
        if (classNames.size() == 1)
        {
            final String path = classNames.get(0).getPath();
            final Media classPath = project.getClassMedia(path);
            final Class<? extends EntityGame> type = project.getClass(EntityGame.class, classPath);
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
    public static String getEntitiesFolderTypeName(File path) throws LionEngineException
    {
        final File typeFile = new File(path, "type.xml");
        final XmlNode typeNode = Stream.loadXml(UtilityMedia.get(typeFile));
        return typeNode.getChild("name").getText();
    }

    /**
     * Create a button with a text and an icon at a fixed width.
     * 
     * @param parent The composite parent.
     * @param name The button name.
     * @param icon The button icon.
     * @return The button instance.
     */
    public static Button createButton(Composite parent, String name, Image icon)
    {
        final Button button = new Button(parent, SWT.PUSH);
        button.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
        if (name != null)
        {
            button.setText(name);
        }
        button.setImage(icon);
        return button;
    }

    /**
     * Get the selected item number from the tree.
     * 
     * @param tree The tree reference.
     * @param item The item to search.
     * @return The item index.
     */
    public static int getItemIndex(Tree tree, TreeItem item)
    {
        int i = 0;
        for (final TreeItem current : tree.getItems())
        {
            if (current.equals(item))
            {
                break;
            }
            i++;
        }
        return i;
    }

    /**
     * Get a part from its id.
     * 
     * @param partService The part service.
     * @param id The part id.
     * @param clazz The part class type.
     * @return The part class instance.
     * @throws LionEngineException If part can not be found.
     */
    public static <C> C getPart(EPartService partService, String id, Class<C> clazz) throws LionEngineException
    {
        final MPart part = partService.findPart(id);
        if (part != null)
        {
            partService.bringToTop(part);
            final Object object = part.getObject();
            if (object != null && object.getClass().isAssignableFrom(clazz))
            {
                return clazz.cast(part.getObject());
            }
        }
        throw new LionEngineException(Tools.ERROR_PART, id);
    }

    /**
     * Private constructor.
     */
    private Tools()
    {
        throw new RuntimeException();
    }
}
