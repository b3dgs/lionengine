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
package com.b3dgs.lionengine.editor.explorer;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.workbench.swt.modeling.EMenuService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.b3dgs.lionengine.editor.Activator;

/**
 * Represents the resources explorer, depending of the opened project.
 * 
 * @author Pierre-Alexandre
 */
public class ResourcesExplorerPart
{
    /** Main folder icon. */
    static final Image ICON_MAIN = Activator.getIcon("resources-explorer/main.png");
    /** Folder icon. */
    static final Image ICON_FOLDER = Activator.getIcon("resources-explorer/folder.png");
    /** File icon. */
    static final Image ICON_FILE = Activator.getIcon("resources-explorer/file.png");
    /** Sound file icon. */
    static final Image ICON_SOUND = Activator.getIcon("resources-explorer/sound.png");
    /** Music file icon. */
    static final Image ICON_MUSIC = Activator.getIcon("resources-explorer/music.png");
    /** Image file icon. */
    static final Image ICON_IMAGE = Activator.getIcon("resources-explorer/image.png");
    /** Data file icon. */
    static final Image ICON_DATA = Activator.getIcon("resources-explorer/data.png");
    /** Level file icon. */
    static final Image ICON_LEVEL = Activator.getIcon("resources-explorer/level.png");

    /** Tree viewer. */
    Tree tree;

    /**
     * Create the composite.
     * 
     * @param parent The parent reference.
     * @param menuService The menu service reference.
     */
    @PostConstruct
    public void createComposite(Composite parent, EMenuService menuService)
    {
        parent.setLayout(new GridLayout(1, false));

        tree = new Tree(parent, SWT.NONE);
        tree.setLayoutData(new GridData(GridData.FILL_BOTH));
        menuService.registerContextMenu(tree, Activator.PLUGIN_ID + ".resources-explorer.menu");

        parent.pack(true);
    }

    /**
     * Set the resources main folder.
     * 
     * @param root The resources main folder.
     */
    public void setInput(File root)
    {
        final Map<String, TreeItem> nodes = new HashMap<>();
        final Map<TreeItem, List<File>> children = new HashMap<>();
        final Path path = FileSystems.getDefault().getPath(root.getAbsolutePath());
        try
        {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>()
            {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                {
                    final TreeItem parent = nodes.get(dir.getParent().toString());
                    TreeItem item = null;
                    if (parent == null)
                    {
                        item = new TreeItem(tree, SWT.NONE);
                        item.setImage(ResourcesExplorerPart.ICON_MAIN);
                    }
                    else
                    {
                        item = new TreeItem(parent, SWT.NONE);
                        item.setImage(ResourcesExplorerPart.ICON_FOLDER);
                    }
                    item.setText(dir.getFileName().toString());
                    item.setData(dir.toFile());

                    nodes.put(dir.toString(), item);

                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path dir, BasicFileAttributes attrs)
                {
                    final TreeItem parent = nodes.get(dir.getParent().toString());

                    if (children.get(parent) == null)
                    {
                        children.put(parent, new ArrayList<File>());
                    }

                    children.get(parent).add(dir.toFile());

                    return FileVisitResult.CONTINUE;
                }
            });
        }
        catch (final IOException e)
        {
            e.printStackTrace();
        }

        for (final TreeItem parent : children.keySet())
        {
            for (final File child : children.get(parent))
            {
                final TreeItem item = new TreeItem(parent, SWT.NONE);
                item.setText(child.getName());
                item.setData(child);
                if (Property.SOUND.is(child))
                {
                    item.setImage(ResourcesExplorerPart.ICON_SOUND);
                }
                else if (Property.MUSIC.is(child))
                {
                    item.setImage(ResourcesExplorerPart.ICON_MUSIC);
                }
                else if (Property.IMAGE.is(child))
                {
                    item.setImage(ResourcesExplorerPart.ICON_IMAGE);
                }
                else if (Property.DATA.is(child))
                {
                    item.setImage(ResourcesExplorerPart.ICON_DATA);
                }
                else if (Property.LEVEL.is(child))
                {
                    item.setImage(ResourcesExplorerPart.ICON_LEVEL);
                }
                else
                {
                    item.setImage(ResourcesExplorerPart.ICON_FILE);
                }
            }

        }

        tree.layout();

        tree.addSelectionListener(new SelectionListener()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                final Object data = tree.getSelection()[0];
                if (data instanceof TreeItem)
                {
                    final TreeItem item = (TreeItem) data;
                    if (item.getData() instanceof File)
                    {
                        ResourcesExplorerModel.INSTANCE.setSelection((File) item.getData());
                    }
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e)
            {
                // Nothing to do
            }
        });

        tree.addMenuDetectListener(new MenuDetectListener()
        {

            @Override
            public void menuDetected(MenuDetectEvent e)
            {
                tree.getMenu().setVisible(false);
                tree.update();
            }
        });
    }

    /**
     * Set the focus.
     */
    @Focus
    public void setFocus()
    {
        tree.setFocus();
    }
}
