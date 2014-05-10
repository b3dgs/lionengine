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
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.b3dgs.lionengine.core.Core;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.editor.Activator;

/**
 * File visitor implementation for project content analysis.
 * 
 * @author Pierre-Alexandre
 */
public final class ProjectFileVisitor
        extends SimpleFileVisitor<Path>
{
    /** Project icon. */
    private static final Image ICON_MAIN = Activator.getIcon("resources", "project.png");
    /** Folder icon. */
    private static final Image ICON_FOLDER = Activator.getIcon("resources", "folder.png");

    /**
     * Get the media from the path.
     * 
     * @param projectPath The project path.
     * @param sourcesPath The sources path.
     * @param resourcesPath The resources path.
     * @param dir The directory.
     * @return The media instance.
     */
    private static Media getMedia(File projectPath, File sourcesPath, File resourcesPath, Path dir)
    {
        String relative;
        final String dirPath = dir.toString();
        if (dirPath.startsWith(sourcesPath.getPath()))
        {
            relative = dirPath.replace(sourcesPath.getPath(), "");
        }
        else if (dirPath.startsWith(resourcesPath.getPath()))
        {
            relative = dirPath.replace(resourcesPath.getPath(), "");
        }
        else
        {
            relative = dirPath.replace(projectPath.getPath(), "");
        }
        if (!relative.isEmpty())
        {
            relative = relative.substring(1);
        }
        return Core.MEDIA.create(relative);
    }

    /** Tree viewer. */
    private final Tree tree;
    /** The nodes list. */
    private final Map<String, TreeItem> nodes = new HashMap<>();
    /** The children list. */
    private final Map<TreeItem, List<Media>> children = new HashMap<>();
    /** The project reference. */
    private final Project project;
    /** Main item. */
    private TreeItem main;
    /** Solo child. */
    private int soloChildCount;
    /** Solo text. */
    private String soloChildText;

    /**
     * Constructor.
     * 
     * @param tree The tree viewer.
     * @param project The project reference.
     */
    public ProjectFileVisitor(Tree tree, Project project)
    {
        this.tree = tree;
        this.project = project;
    }

    /**
     * Get the children list.
     * 
     * @return The children list.
     */
    public Map<TreeItem, List<Media>> getChildren()
    {
        return children;
    }

    /**
     * Filter the folders when reading the project children.
     * 
     * @param dir The current directory to check.
     * @param project The project reference.
     * @return The file result.
     */
    private FileVisitResult filterFolder(Path dir, Project project)
    {
        final String path = dir.toFile().getPath();
        final File projectPath = project.getPath();
        final File classesPath = new File(projectPath, project.getClasses());
        final File resourcesPath = new File(projectPath, project.getResources());
        final String dirName = dir.getFileName().toString();

        if (!path.contains("META-INF")
                && (path.endsWith(project.getName()) || path.startsWith(classesPath.getPath()) || path
                        .startsWith(resourcesPath.getPath())))
        {
            TreeItem parent;
            final File[] content = dir.getParent().toFile().listFiles();
            if (content != null && content.length == 1 && content[0].isDirectory())
            {
                Path parentPath = dir.getParent();
                soloChildText = dirName;
                for (int i = 0; i <= soloChildCount; i++)
                {
                    nodes.get(parentPath.toString()).dispose();
                    soloChildText = parentPath.getFileName().toString() + "/" + soloChildText;
                    parentPath = parentPath.getParent();
                }

                parent = nodes.get(parentPath.toString());
                soloChildCount++;
            }
            else
            {
                parent = nodes.get(dir.getParent().toString());
                soloChildText = dirName;
                soloChildCount = 0;
            }

            TreeItem item = null;
            if (main != null && parent == null)
            {
                parent = main;
            }
            if (main == null)
            {
                item = new TreeItem(tree, SWT.NONE);
                item.setImage(ProjectFileVisitor.ICON_MAIN);
                main = item;
                parent = item;
            }
            else if (!dirName.equals(project.getName()))
            {
                item = new TreeItem(parent, SWT.NONE);
                item.setImage(ProjectFileVisitor.ICON_FOLDER);
            }
            if (item != null)
            {
                if (classesPath.getPath().endsWith(dirName))
                {
                    item.setText("classes");
                }
                else if (resourcesPath.getPath().endsWith(dirName))
                {
                    item.setText("resources");
                }
                else
                {
                    item.setText(soloChildText);
                }

                item.setData(ProjectFileVisitor.getMedia(projectPath, classesPath, resourcesPath, dir));
            }

            nodes.put(dir.toString(), item);
        }

        return FileVisitResult.CONTINUE;
    }

    /*
     * SimpleFileVisitor
     */

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
    {
        return filterFolder(dir, project);
    }

    @Override
    public FileVisitResult visitFile(Path dir, BasicFileAttributes attrs)
    {
        final String path = dir.getParent().toFile().getPath();
        final File projectPath = project.getPath();
        final File sourcesPath = new File(projectPath, project.getClasses());
        final File resourcesPath = new File(projectPath, project.getResources());

        if (path.startsWith(sourcesPath.getPath()) || path.startsWith(resourcesPath.getPath()))
        {
            final TreeItem parent = nodes.get(dir.getParent().toString());
            if (parent != null && parent.getText() != project.getName())
            {
                if (children.get(parent) == null)
                {
                    children.put(parent, new ArrayList<Media>());
                }
                children.get(parent).add(ProjectFileVisitor.getMedia(projectPath, sourcesPath, resourcesPath, dir));
            }
        }
        return FileVisitResult.CONTINUE;
    }
}
