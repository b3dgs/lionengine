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
package com.b3dgs.lionengine.editor.quick;

import java.util.Collection;

import javax.annotation.PostConstruct;

import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.services.EMenuService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.editor.Activator;
import com.b3dgs.lionengine.editor.project.ProjectsModel;

/**
 * Represents the quick access explorer, depending of the opened project.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class QuickAccessPart
{
    /** ID. */
    public static final String ID = Activator.PLUGIN_ID + ".part.quick";
    /** Menu ID. */
    public static final String MENU_ID = QuickAccessPart.ID + ".menu";

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
        menuService.registerContextMenu(tree, QuickAccessPart.MENU_ID);
    }

    /**
     * Set the input items.
     * 
     * @param items The input items.
     */
    public void setInput(Collection<TreeItem> items)
    {
        tree.removeAll();
        for (final TreeItem current : items)
        {
            final TreeItem item = new TreeItem(tree, SWT.NONE);
            item.setText(current.getText());
            item.setImage(current.getImage());
            item.setData(current.getData());
        }
        tree.layout();
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
