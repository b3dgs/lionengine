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
package com.b3dgs.lionengine.editor.project;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.b3dgs.lionengine.UtilFile;
import com.b3dgs.lionengine.core.Core;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.editor.UtilEclipse;

/**
 * Generate the project tree from the project folder.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class ProjectTreeCreator
{
    /** Project icon. */
    public static final Image ICON_MAIN = UtilEclipse.getIcon("resources", "project.png");
    /** Folder icon. */
    public static final Image ICON_FOLDER = UtilEclipse.getIcon("resources", "folder.png");
    /** File icon. */
    public static final Image ICON_FILE = UtilEclipse.getIcon("resources", "file.png");
    /** Sound file icon. */
    public static final Image ICON_SOUND = UtilEclipse.getIcon("resources", "sound.png");
    /** Music file icon. */
    public static final Image ICON_MUSIC = UtilEclipse.getIcon("resources", "music.png");
    /** Image file icon. */
    public static final Image ICON_IMAGE = UtilEclipse.getIcon("resources", "image.png");
    /** Data file icon. */
    public static final Image ICON_DATA = UtilEclipse.getIcon("resources", "data.png");
    /** Level file icon. */
    public static final Image ICON_LEVEL = UtilEclipse.getIcon("resources", "level.png");
    /** Map file icon. */
    public static final Image ICON_MAP = UtilEclipse.getIcon("resources", "map-tile.png");
    /** Factory entity file icon. */
    public static final Image ICON_FACTORY_ENTITY = UtilEclipse.getIcon("resources", "factory.png");
    /** Object file icon. */
    public static final Image ICON_OBJECT = UtilEclipse.getIcon("resources", "object.png");
    /** Entity file icon. */
    public static final Image ICON_ENTITY = UtilEclipse.getIcon("resources", "entity.png");
    /** Projectile file icon. */
    public static final Image ICON_PROJECTILE = UtilEclipse.getIcon("resources", "projectile.png");
    /** Class file icon. */
    public static final Image ICON_CLASS = UtilEclipse.getIcon("resources", "class.png");
    /** Tile sheets file icon. */
    public static final Image ICON_TILESHEETS = UtilEclipse.getIcon("resources", "tilesheets.png");

    /** Classes folder. */
    private static final String FOLDER_CLASSES = "classes";
    /** Resources folder. */
    private static final String FOLDER_RESOURCES = "resources";

    /**
     * Check the path reference and create the node if necessary.
     * 
     * @param title The node title.
     * @param parent The node parent.
     * @param referencePath The reference path.
     * @param path The current path.
     * @return The item reference.
     */
    private static TreeItem checkPathReference(String title, TreeItem parent, File referencePath, File path)
    {
        if (referencePath.getPath().equals(path.getPath()))
        {
            final TreeItem folder = new TreeItem(parent, SWT.NONE);
            folder.setText(title);
            folder.setImage(ProjectTreeCreator.ICON_FOLDER);
            return folder;
        }
        return parent;
    }

    /**
     * Get the class file icon.
     * 
     * @param file The child file.
     * @return The icon image associated to the file type.
     */
    private static Image getClassIcon(Media file)
    {
        if (Property.MAP_IMPL.is(file))
        {
            return ProjectTreeCreator.ICON_MAP;
        }
        else if (Property.FACTORY_IMPL.is(file))
        {
            return ProjectTreeCreator.ICON_FACTORY_ENTITY;
        }
        return ProjectTreeCreator.ICON_CLASS;
    }

    /**
     * Get the data file icon.
     * 
     * @param file The child file.
     * @return The icon image associated to the file type.
     */
    private static Image getDataIcon(Media file)
    {
        if (ProjectilesFolderTester.isProjectileFile(file))
        {
            return ProjectTreeCreator.ICON_PROJECTILE;
        }
        else if (ObjectsFolderTester.isObjectFile(file))
        {
            return ProjectTreeCreator.ICON_OBJECT;
        }
        else if (TilesheetsFolderTester.isTilesheetsFile(file))
        {
            return ProjectTreeCreator.ICON_TILESHEETS;
        }
        return ProjectTreeCreator.ICON_DATA;
    }

    /**
     * Get the file icon.
     * 
     * @param file The child file.
     * @return The icon image associated to the file type.
     */
    private static Image getFileIcon(Media file)
    {
        if (Property.SOUND.is(file))
        {
            return ProjectTreeCreator.ICON_SOUND;
        }
        else if (Property.MUSIC.is(file))
        {
            return ProjectTreeCreator.ICON_MUSIC;
        }
        else if (Property.IMAGE.is(file))
        {
            return ProjectTreeCreator.ICON_IMAGE;
        }
        else if (Property.LEVEL.is(file))
        {
            return ProjectTreeCreator.ICON_LEVEL;
        }
        else if (Property.DATA.is(file))
        {
            return ProjectTreeCreator.getDataIcon(file);
        }
        else if (Property.CLASS.is(file))
        {
            return ProjectTreeCreator.getClassIcon(file);
        }
        return ProjectTreeCreator.ICON_FILE;
    }

    /** Project reference. */
    private final Project project;
    /** Quick access list. */
    private final List<TreeItem> quicks;
    /** Project path. */
    private final File projectPath;
    /** Classes path. */
    private final File classesPath;
    /** Resources path. */
    private final File resourcesPath;
    /** Tree reference. */
    private final Tree tree;

    /**
     * Constructor.
     * 
     * @param project The project reference.
     * @param tree The tree reference.
     */
    public ProjectTreeCreator(Project project, Tree tree)
    {
        this.project = project;
        this.tree = tree;
        quicks = new ArrayList<>();
        projectPath = project.getPath();
        classesPath = new File(projectPath, project.getClasses());
        resourcesPath = new File(projectPath, project.getResources());
    }

    /**
     * Start the tree creation.
     */
    public void start()
    {
        final TreeItem folder = new TreeItem(tree, SWT.NONE);
        folder.setText(project.getName());
        folder.setImage(ProjectTreeCreator.ICON_MAIN);
        checkPath(projectPath, folder);
    }

    /**
     * Create a tree item.
     * 
     * @param parent The item parent.
     * @param path The item path.
     * @param icon The item icon.
     * @return The created item.
     */
    public TreeItem createItem(TreeItem parent, File path, Image icon)
    {
        final Media media = getMedia(path.getPath());
        final TreeItem item = new TreeItem(parent, SWT.NONE);
        item.setText(path.getName());
        item.setImage(icon);
        item.setData(media);
        tree.setData(media.getPath(), item);
        return item;
    }

    /**
     * Get the quick access list.
     * 
     * @return The quick access list.
     */
    public List<TreeItem> getQuicks()
    {
        return quicks;
    }

    /**
     * Check the current path for its children.
     * 
     * @param path The parent path.
     * @param parent The parent tree item.
     */
    private void checkPath(File path, TreeItem parent)
    {
        if (path.isDirectory() && !path.getName().equals("META-INF"))
        {
            checkPathDirectory(path, parent);
        }
        else if (path.isFile())
        {
            final String pathName = path.getParent();
            if (pathName.startsWith(classesPath.getPath()) || pathName.startsWith(resourcesPath.getPath()))
            {
                createChild(path, parent);
            }
        }
    }

    /**
     * Check the current directory for its children.
     * 
     * @param folder The current folder.
     * @param parent The parent tree item.
     */
    private void checkPathDirectory(File folder, TreeItem parent)
    {
        final File[] children = folder.listFiles();
        final TreeItem folderItem;
        // Concatenate single folder child
        if (folder.getParentFile().listFiles().length == 1)
        {
            folderItem = parent;
            folderItem.setText(folderItem.getText() + java.io.File.separator + folder.getName());
        }
        else
        {
            folderItem = createFolder(folder, parent);
        }
        Arrays.sort(children, new DirectoryFolderComparator());
        for (final File child : children)
        {
            final String pathName = child.getPath();
            if (pathName.startsWith(classesPath.getPath()) || pathName.startsWith(resourcesPath.getPath())
                    || classesPath.getPath().startsWith(pathName) || resourcesPath.getPath().startsWith(pathName))
            {
                checkPath(child, folderItem);
            }
        }
    }

    /**
     * Create the folder item.
     * 
     * @param path The folder path.
     * @param parent The node parent.
     * @return The created folder item.
     */
    private TreeItem createFolder(File path, TreeItem parent)
    {
        if (classesPath.getPath().startsWith(path.getPath()))
        {
            return ProjectTreeCreator.checkPathReference(ProjectTreeCreator.FOLDER_CLASSES, parent, classesPath, path);
        }
        else if (resourcesPath.getPath().startsWith(path.getPath()))
        {
            return ProjectTreeCreator.checkPathReference(ProjectTreeCreator.FOLDER_RESOURCES, parent, resourcesPath,
                    path);
        }
        else
        {
            return createItem(parent, path, ProjectTreeCreator.ICON_FOLDER);
        }
    }

    /**
     * Create the child from its path.
     * 
     * @param file The child path.
     * @param parent The parent tree item.
     */
    private void createChild(File file, TreeItem parent)
    {
        final String childName = file.getName();
        final Media media = getMedia(file.getPath());
        final Image icon = ProjectTreeCreator.getFileIcon(media);
        final TreeItem item = createItem(parent, file, icon);

        if (Property.CLASS.is(media))
        {
            final int beforeExtension = childName.indexOf(UtilFile.getExtension(childName)) - 1;
            item.setText(file.getName().substring(0, beforeExtension));
        }
        else
        {
            item.setText(file.getName());
        }

        if (icon == ProjectTreeCreator.ICON_FACTORY_ENTITY || icon == ProjectTreeCreator.ICON_MAP)
        {
            quicks.add(item);
        }
    }

    /**
     * Get the media from the path.
     * 
     * @param path The media path.
     * @return The media instance.
     */
    private Media getMedia(String path)
    {
        String relative;
        if (path.startsWith(classesPath.getPath()))
        {
            relative = path.replace(classesPath.getPath(), "");
        }
        else if (path.startsWith(resourcesPath.getPath()))
        {
            relative = path.replace(resourcesPath.getPath(), "");
        }
        else
        {
            relative = path.replace(projectPath.getPath(), "");
        }
        if (!relative.isEmpty())
        {
            relative = relative.substring(1);
        }
        return Core.MEDIA.create(relative);
    }
}
