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
package com.b3dgs.lionengine.editor.project;

import java.io.File;
import java.util.Arrays;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.editor.utility.UtilExtension;
import com.b3dgs.lionengine.editor.utility.UtilIcon;
import com.b3dgs.lionengine.util.UtilFile;

/**
 * Generate the project tree from the project folder.
 */
public class ProjectTreeCreator
{
    /** Resources folder. */
    public static final String RESOURCES_FOLDER = "resources";
    /** Project icon. */
    public static final Image ICON_MAIN = UtilIcon.get(RESOURCES_FOLDER, "project.png");
    /** Folder icon. */
    public static final Image ICON_FOLDER = UtilIcon.get(RESOURCES_FOLDER, "folder.png");
    /** File icon. */
    public static final Image ICON_FILE = UtilIcon.get(RESOURCES_FOLDER, "file.png");
    /** Sound file icon. */
    public static final Image ICON_SOUND = UtilIcon.get(RESOURCES_FOLDER, "sound.png");
    /** Music file icon. */
    public static final Image ICON_MUSIC = UtilIcon.get(RESOURCES_FOLDER, "music.png");
    /** Image file icon. */
    public static final Image ICON_IMAGE = UtilIcon.get(RESOURCES_FOLDER, "image.png");
    /** Data file icon. */
    public static final Image ICON_DATA = UtilIcon.get(RESOURCES_FOLDER, "data.png");
    /** META-INF folder. */
    private static final String FOLDER_METAINF = "META-INF";

    /**
     * Get the data file icon.
     * 
     * @param file The child file.
     * @return The icon image associated to the file type.
     */
    private static Image getDataIcon(Media file)
    {
        for (final ResourceChecker checker : UtilExtension.get(ResourceChecker.class, ResourceChecker.EXTENSION_ID))
        {
            final Image icon = checker.getIcon(file);
            if (icon != null)
            {
                return icon;
            }
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
        final Image image;
        if (Property.SOUND.is(file))
        {
            image = ICON_SOUND;
        }
        else if (Property.MUSIC.is(file))
        {
            image = ICON_MUSIC;
        }
        else if (Property.IMAGE.is(file))
        {
            image = ICON_IMAGE;
        }
        else if (Property.DATA.is(file))
        {
            image = getDataIcon(file);
        }
        else
        {
            image = ICON_FILE;
        }
        return image;
    }

    /** Project reference. */
    private final Project project;
    /** Project path. */
    private final File projectPath;
    /** Resources path. */
    private final File resourcesPath;

    /**
     * Create a project tree creator from a project and its tree.
     * 
     * @param project The project reference.
     */
    public ProjectTreeCreator(Project project)
    {
        this.project = project;
        projectPath = project.getPath();
        resourcesPath = new File(projectPath, project.getResources());
    }

    /**
     * Start the tree creation.
     * 
     * @param tree The tree reference.
     */
    public void create(Tree tree)
    {
        final TreeItem folder = new TreeItem(tree, SWT.NONE);
        folder.setText(project.getName());
        folder.setImage(ICON_MAIN);
        checkPath(projectPath, tree, folder);
    }

    /**
     * Create a tree item.
     * 
     * @param tree The tree reference.
     * @param parent The item parent.
     * @param path The item path.
     * @param icon The item icon.
     * @return The created item.
     */
    TreeItem createItem(Tree tree, TreeItem parent, File path, Image icon)
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
     * @param tree The tree reference.
     * @param parent The parent tree item.
     */
    void checkPath(File path, Tree tree, TreeItem parent)
    {
        if (path.isDirectory() && !FOLDER_METAINF.equals(path.getName()))
        {
            checkPathDirectory(path, tree, parent);
        }
        else if (path.isFile())
        {
            final String pathName = path.getParent();
            if (pathName.startsWith(resourcesPath.getPath()))
            {
                createChild(path, tree, parent);
            }
        }
    }

    /**
     * Check the current directory for its children.
     * 
     * @param folder The current folder.
     * @param tree The tree reference.
     * @param parent The parent tree item.
     */
    private void checkPathDirectory(File folder, Tree tree, TreeItem parent)
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
                folderItem.setText(folderItem.getText() + File.separator + folder.getName());
            }
            else
            {
                folderItem = createFolder(folder, tree, parent);
            }
            Arrays.sort(children, new DirectoryFolderComparator());
            for (final File child : children)
            {
                final String pathName = child.getPath();
                if (pathName.startsWith(resourcesPath.getPath()) || resourcesPath.getPath().startsWith(pathName))
                {
                    checkPath(child, tree, folderItem);
                }
            }
        }
    }

    /**
     * Check the path reference and create the node if necessary.
     * 
     * @param title The node title.
     * @param tree The tree reference.
     * @param parent The node parent.
     * @param referencePath The reference path.
     * @param path The current path.
     * @return The item reference.
     */
    private TreeItem checkPathReference(String title, Tree tree, TreeItem parent, File referencePath, File path)
    {
        if (referencePath.getPath().equals(path.getPath()))
        {
            final TreeItem folder = new TreeItem(parent, SWT.NONE);
            folder.setText(title);
            folder.setImage(ICON_FOLDER);
            folder.setData(getMedia(path.getPath()));
            tree.setData(title, folder);
            if (RESOURCES_FOLDER.equals(path.getName()))
            {
                tree.setData("", folder);
            }
            return folder;
        }
        return parent;
    }

    /**
     * Create the folder item.
     * 
     * @param path The folder path.
     * @param tree The tree reference.
     * @param parent The node parent.
     * @return The created folder item.
     */
    private TreeItem createFolder(File path, Tree tree, TreeItem parent)
    {
        if (resourcesPath.getPath().startsWith(path.getPath()))
        {
            return checkPathReference(RESOURCES_FOLDER, tree, parent, resourcesPath, path);
        }
        return createItem(tree, parent, path, ICON_FOLDER);
    }

    /**
     * Create the child from its path.
     * 
     * @param file The child path.
     * @param tree The tree reference.
     * @param parent The parent tree item.
     */
    private void createChild(File file, Tree tree, TreeItem parent)
    {
        final String childName = file.getName();
        final Media media = getMedia(file.getPath());
        final Image icon = getFileIcon(media);
        final TreeItem item = createItem(tree, parent, file, icon);

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
        final String relative;
        if (path.startsWith(resourcesPath.getPath()))
        {
            relative = path.replace(resourcesPath.getPath(), Constant.EMPTY_STRING);
        }
        else
        {
            relative = path.replace(projectPath.getPath(), Constant.EMPTY_STRING);
        }
        if (!relative.isEmpty())
        {
            return Medias.create(relative.substring(1));
        }
        return Medias.create(relative);
    }
}
