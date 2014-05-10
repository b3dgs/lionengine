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
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.workbench.swt.modeling.EMenuService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.editor.Activator;

/**
 * Represents the resources explorer, depending of the opened project.
 * 
 * @author Pierre-Alexandre
 */
public class ProjectsPart
{
    /** ID. */
    public static final String ID = Activator.PLUGIN_ID + ".part.projects";
    /** Menu ID. */
    public static final String MENU_ID = ProjectsPart.ID + ".menu";
    /** File icon. */
    private static final Image ICON_FILE = Activator.getIcon("resources", "file.png");
    /** Sound file icon. */
    private static final Image ICON_SOUND = Activator.getIcon("resources", "sound.png");
    /** Music file icon. */
    private static final Image ICON_MUSIC = Activator.getIcon("resources", "music.png");
    /** Image file icon. */
    private static final Image ICON_IMAGE = Activator.getIcon("resources", "image.png");
    /** Data file icon. */
    private static final Image ICON_DATA = Activator.getIcon("resources", "data.png");
    /** Level file icon. */
    private static final Image ICON_LEVEL = Activator.getIcon("resources", "level.png");
    /** Class file icon. */
    private static final Image ICON_CLASS = Activator.getIcon("resources", "class.png");

    /**
     * Set the file icon.
     * 
     * @param file The child file.
     * @param item The child item.
     */
    private static void setFileIcon(Media file, TreeItem item)
    {
        if (Property.SOUND.is(file))
        {
            item.setImage(ProjectsPart.ICON_SOUND);
        }
        else if (Property.MUSIC.is(file))
        {
            item.setImage(ProjectsPart.ICON_MUSIC);
        }
        else if (Property.IMAGE.is(file))
        {
            item.setImage(ProjectsPart.ICON_IMAGE);
        }
        else if (Property.DATA.is(file))
        {
            item.setImage(ProjectsPart.ICON_DATA);
        }
        else if (Property.LEVEL.is(file))
        {
            item.setImage(ProjectsPart.ICON_LEVEL);
        }
        else if (Property.CLASS.is(file))
        {
            item.setText(item.getText().replaceAll(".class", ""));
            item.setImage(ProjectsPart.ICON_CLASS);
        }
        else
        {
            item.setImage(ProjectsPart.ICON_FILE);
        }
    }

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
        tree.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent)
            {
                final Object data = tree.getSelection()[0];
                if (data instanceof TreeItem)
                {
                    final TreeItem item = (TreeItem) data;
                    if (item.getData() instanceof Media)
                    {
                        ProjectsModel.INSTANCE.setSelection((Media) item.getData());
                    }
                }
            }
        });
        tree.addMenuDetectListener(new MenuDetectListener()
        {
            @Override
            public void menuDetected(MenuDetectEvent menuDetectEvent)
            {
                tree.getMenu().setVisible(false);
                tree.update();
            }
        });
        menuService.registerContextMenu(tree, ProjectsPart.MENU_ID);

        parent.pack(true);
    }

    /**
     * Get the project file children tree.
     * 
     * @param project The project reference.
     * @return The children list.
     * @throws IOException If error while reading project children.
     */
    private Map<TreeItem, List<Media>> getChildren(Project project) throws IOException
    {
        final File projectPath = project.getPath();
        final Path path = FileSystems.getDefault().getPath(projectPath.getAbsolutePath());

        final ProjectFileVisitor fileVisitor = new ProjectFileVisitor(tree, project);
        Files.walkFileTree(path, fileVisitor);

        return fileVisitor.getChildren();
    }

    /**
     * Set the project main folders.
     * 
     * @param project The project reference.
     * @throws LionEngineException If error while reading project children.
     */
    public void setInput(Project project) throws LionEngineException
    {
        try
        {
            tree.removeAll();
            final Map<TreeItem, List<Media>> children = getChildren(project);
            for (final TreeItem parent : children.keySet())
            {
                for (final Media child : children.get(parent))
                {
                    final TreeItem item = new TreeItem(parent, SWT.NONE);
                    item.setText(child.getFile().getName());
                    item.setData(child);
                    ProjectsPart.setFileIcon(child, item);
                }
            }
            tree.layout();
        }
        catch (final IOException exception)
        {
            throw new LionEngineException(exception);
        }
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
