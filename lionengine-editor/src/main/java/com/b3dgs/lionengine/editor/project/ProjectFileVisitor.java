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

    /** Tree viewer. */
    private final Tree tree;
    /** The nodes list. */
    private final Map<String, TreeItem> nodes = new HashMap<>();
    /** The children list. */
    private final Map<TreeItem, List<File>> children = new HashMap<>();
    /** The project reference. */
    private final Project project;

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
    public Map<TreeItem, List<File>> getChildren()
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
        final File resourcesPath = new File(projectPath, project.getResources());
        if (dir.getParent().toFile().equals(projectPath) && !path.contains(resourcesPath.getPath()))
        {
            return FileVisitResult.SKIP_SUBTREE;
        }
        final TreeItem parent = nodes.get(dir.getParent().toString());
        TreeItem item = null;
        if (parent == null)
        {
            item = new TreeItem(tree, SWT.NONE);
            item.setImage(ProjectFileVisitor.ICON_MAIN);
        }
        else
        {
            item = new TreeItem(parent, SWT.NONE);
            item.setImage(ProjectFileVisitor.ICON_FOLDER);
        }
        item.setText(dir.getFileName().toString());
        item.setData(dir.toFile());

        nodes.put(dir.toString(), item);

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
        if (!dir.getParent().toFile().equals(project.getPath()))
        {
            final TreeItem parent = nodes.get(dir.getParent().toString());

            if (children.get(parent) == null)
            {
                children.put(parent, new ArrayList<File>());
            }

            children.get(parent).add(dir.toFile());
        }
        return FileVisitResult.CONTINUE;
    }
}
