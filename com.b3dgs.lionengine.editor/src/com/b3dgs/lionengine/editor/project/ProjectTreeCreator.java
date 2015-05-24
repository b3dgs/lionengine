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
package com.b3dgs.lionengine.editor.project;

import java.io.File;
import java.util.Arrays;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.b3dgs.lionengine.UtilFile;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.editor.UtilEclipse;
import com.b3dgs.lionengine.editor.project.tester.GroupsTester;
import com.b3dgs.lionengine.editor.project.tester.ObjectsTester;
import com.b3dgs.lionengine.editor.project.tester.SheetsTester;

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
    /** Object file icon. */
    public static final Image ICON_OBJECT = UtilEclipse.getIcon("resources", "object.png");
    /** Sheets file icon. */
    public static final Image ICON_SHEETS = UtilEclipse.getIcon("resources", "sheets.png");
    /** Groups file icon. */
    public static final Image ICON_GROUPS = UtilEclipse.getIcon("resources", "groups.png");

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
            folder.setImage(ICON_FOLDER);
            return folder;
        }
        return parent;
    }

    /**
     * Get the data file icon.
     * 
     * @param file The child file.
     * @return The icon image associated to the file type.
     */
    private static Image getDataIcon(Media file)
    {
        if (ObjectsTester.isObjectFile(file))
        {
            return ICON_OBJECT;
        }
        else if (SheetsTester.isSheetsFile(file))
        {
            return ICON_SHEETS;
        }
        else if (GroupsTester.isGroupsFile(file))
        {
            return ICON_GROUPS;
        }
        return ICON_DATA;
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
            return ICON_SOUND;
        }
        else if (Property.MUSIC.is(file))
        {
            return ICON_MUSIC;
        }
        else if (Property.IMAGE.is(file))
        {
            return ICON_IMAGE;
        }
        else if (Property.DATA.is(file))
        {
            return getDataIcon(file);
        }
        return ICON_FILE;
    }

    /** Project reference. */
    private final Project project;
    /** Project path. */
    private final File projectPath;
    /** Resources path. */
    private final File resourcesPath;
    /** Tree reference. */
    private final Tree tree;

    /**
     * Create a project tree creator from a project and its tree.
     * 
     * @param project The project reference.
     * @param tree The tree reference.
     */
    public ProjectTreeCreator(Project project, Tree tree)
    {
        this.project = project;
        this.tree = tree;
        projectPath = project.getPath();
        resourcesPath = new File(projectPath, project.getResources());
    }

    /**
     * Start the tree creation.
     */
    public void start()
    {
        final TreeItem folder = new TreeItem(tree, SWT.NONE);
        folder.setText(project.getName());
        folder.setImage(ICON_MAIN);
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
     * Check the current path for its children.
     * 
     * @param path The parent path.
     * @param parent The parent tree item.
     */
    public void checkPath(File path, TreeItem parent)
    {
        if (path.isDirectory() && !path.getName().equals("META-INF"))
        {
            checkPathDirectory(path, parent);
        }
        else if (path.isFile())
        {
            final String pathName = path.getParent();
            if (pathName.startsWith(resourcesPath.getPath()))
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
        final File parentFile = folder.getParentFile();
        if (parentFile != null)
        {
            final File[] parentFiles = parentFile.listFiles();
            if (parentFiles != null && parentFiles.length == 1)
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
                if (pathName.startsWith(resourcesPath.getPath()) || resourcesPath.getPath().startsWith(pathName))
                {
                    checkPath(child, folderItem);
                }
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
        if (resourcesPath.getPath().startsWith(path.getPath()))
        {
            return checkPathReference(FOLDER_RESOURCES, parent, resourcesPath, path);
        }
        return createItem(parent, path, ICON_FOLDER);
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
        final Image icon = getFileIcon(media);
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
        if (path.startsWith(resourcesPath.getPath()))
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
        return Medias.create(relative);
    }
}
