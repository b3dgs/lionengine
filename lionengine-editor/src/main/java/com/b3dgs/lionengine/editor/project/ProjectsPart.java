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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;

import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.services.EMenuService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
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
import com.b3dgs.lionengine.UtilFile;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.editor.Activator;
import com.b3dgs.lionengine.editor.Tools;
import com.b3dgs.lionengine.editor.quick.QuickAccessPart;

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
    private static final Image ICON_FILE = Tools.getIcon("resources", "file.png");
    /** Sound file icon. */
    private static final Image ICON_SOUND = Tools.getIcon("resources", "sound.png");
    /** Music file icon. */
    private static final Image ICON_MUSIC = Tools.getIcon("resources", "music.png");
    /** Image file icon. */
    private static final Image ICON_IMAGE = Tools.getIcon("resources", "image.png");
    /** Data file icon. */
    private static final Image ICON_DATA = Tools.getIcon("resources", "data.png");
    /** Level file icon. */
    private static final Image ICON_LEVEL = Tools.getIcon("resources", "level.png");
    /** Map file icon. */
    private static final Image ICON_MAP = Tools.getIcon("resources", "assign-map-impl.png");
    /** Factory entity file icon. */
    private static final Image ICON_FACTORY_ENTITTY = Tools.getIcon("resources", "assign-factory-entity-impl.png");
    /** Entity file icon. */
    private static final Image ICON_ENTITTY = Tools.getIcon("resources", "entity.png");
    /** Class file icon. */
    private static final Image ICON_CLASS = Tools.getIcon("resources", "class.png");

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
            return ProjectsPart.ICON_SOUND;
        }
        else if (Property.MUSIC.is(file))
        {
            return ProjectsPart.ICON_MUSIC;
        }
        else if (Property.IMAGE.is(file))
        {
            return ProjectsPart.ICON_IMAGE;
        }
        else if (Property.LEVEL.is(file))
        {
            return ProjectsPart.ICON_LEVEL;
        }
        else if (Property.DATA.is(file))
        {
            if (EntitiesFolderTester.isEntityFile(file.getFile()))
            {
                return ProjectsPart.ICON_ENTITTY;
            }
            return ProjectsPart.ICON_DATA;
        }
        else if (Property.CLASS.is(file))
        {
            if (Property.MAP_IMPL.is(file))
            {
                return ProjectsPart.ICON_MAP;
            }
            else if (Property.FACTORY_ENTITY_IMPL.is(file))
            {
                return ProjectsPart.ICON_FACTORY_ENTITTY;
            }
            return ProjectsPart.ICON_CLASS;
        }
        else
        {
            return ProjectsPart.ICON_FILE;
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
        final GridLayout layout = new GridLayout(1, false);
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        parent.setLayout(layout);

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
     * @param partService The part service reference.
     * @throws LionEngineException If error while reading project children.
     */
    public void setInput(Project project, EPartService partService) throws LionEngineException
    {
        try
        {
            tree.removeAll();
            final Map<TreeItem, List<Media>> children = getChildren(project);
            final List<TreeItem> quicks = new ArrayList<>();
            for (final Entry<TreeItem, List<Media>> parent : children.entrySet())
            {
                for (final Media child : parent.getValue())
                {
                    final TreeItem item = new TreeItem(parent.getKey(), SWT.NONE);
                    final String childName = child.getFile().getName();
                    final Image icon = ProjectsPart.getFileIcon(child);
                    item.setImage(icon);
                    item.setData(child);

                    if (Property.CLASS.is(child))
                    {
                        final int beforeExtension = childName.indexOf(UtilFile.getExtension(childName)) - 1;
                        item.setText(child.getFile().getName().substring(0, beforeExtension));
                    }
                    else
                    {
                        item.setText(child.getFile().getName());
                    }

                    if (icon == ProjectsPart.ICON_FACTORY_ENTITTY || icon == ProjectsPart.ICON_MAP)
                    {
                        quicks.add(item);
                    }
                }
            }
            tree.layout();

            final QuickAccessPart part = Tools.getPart(partService, QuickAccessPart.ID, QuickAccessPart.class);
            part.setInput(quicks);
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
