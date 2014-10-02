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

import javax.annotation.PostConstruct;

import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.services.EMenuService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.editor.Activator;
import com.b3dgs.lionengine.editor.UtilEclipse;
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

    /** Tree viewer. */
    private Tree tree;
    /** Tree creator. */
    private ProjectTreeCreator projectTreeCreator;

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
        tree.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseDoubleClick(MouseEvent mouseEvent)
            {
                expandOnDoubleClick();
            }
        });
        tree.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent)
            {
                updateSelection();
            }
        });
        tree.addMenuDetectListener(new MenuDetectListener()
        {
            @Override
            public void menuDetected(MenuDetectEvent menuDetectEvent)
            {
                updateMenu();
            }
        });
        menuService.registerContextMenu(tree, ProjectsPart.MENU_ID);
    }

    /**
     * Add an item to the project tree.
     * 
     * @param media The media item.
     * @param item The item file.
     * @param icon The media icon.
     */
    public void addTreeItem(Media media, File item, Image icon)
    {
        final TreeItem parent = (TreeItem) tree.getData(media.getPath());
        projectTreeCreator.createItem(parent, item, icon);
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
        tree.removeAll();

        projectTreeCreator = new ProjectTreeCreator(project, tree);
        projectTreeCreator.start();

        tree.layout();

        final QuickAccessPart part = UtilEclipse.getPart(partService, QuickAccessPart.ID, QuickAccessPart.class);
        part.setInput(projectTreeCreator.getQuicks());
    }

    /**
     * Set the focus.
     */
    @Focus
    public void setFocus()
    {
        tree.setFocus();
    }

    /**
     * Auto expand selected item on double click.
     */
    void expandOnDoubleClick()
    {
        if (!tree.isDisposed())
        {
            for (final TreeItem item : tree.getSelection())
            {
                item.setExpanded(!item.getExpanded());
            }
        }
    }

    /**
     * Update tree selection by storing it.
     */
    void updateSelection()
    {
        if (!tree.isDisposed())
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
    }

    /**
     * Update the menu on detection.
     */
    void updateMenu()
    {
        if (!tree.isDisposed())
        {
            final Menu menu = tree.getMenu();
            if (menu != null)
            {
                menu.setVisible(false);
                tree.update();
            }
        }
    }
}
