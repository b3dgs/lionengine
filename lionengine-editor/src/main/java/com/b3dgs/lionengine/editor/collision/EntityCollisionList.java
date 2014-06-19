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
package com.b3dgs.lionengine.editor.collision;

import java.util.Map;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.b3dgs.lionengine.editor.Tools;
import com.b3dgs.lionengine.game.Collision;
import com.b3dgs.lionengine.game.configurable.Configurable;

/**
 * Represents the collisions list, allowing to add and remove collisions.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class EntityCollisionList
{
    /** Stop icon. */
    private static final Image ICON_COLLISION_ADD = Tools.getIcon("collision-editor", "collision-add.png");
    /** Stop icon. */
    private static final Image ICON_COLLISION_REMOVE = Tools.getIcon("collision-editor", "collision-remove.png");

    /** Configurable reference. */
    private final Configurable configurable;
    /** Animation properties. */
    final EntityCollisionProperties entityCollisionProperties;
    /** Collision list. */
    Tree collisionTree;
    /** Selected data. */
    Collision selectedCollision;
    /** Selected data backup. */
    Collision selectedCollisionBackup;

    /**
     * Constructor.
     * 
     * @param configurable The configurable reference.
     * @param entityCollisionProperties The collision properties reference.
     */
    public EntityCollisionList(Configurable configurable, EntityCollisionProperties entityCollisionProperties)
    {
        this.configurable = configurable;
        this.entityCollisionProperties = entityCollisionProperties;
    }

    /**
     * Create the collisions list area.
     * 
     * @param parent The composite parent.
     */
    public void createEntityCollisionList(final Composite parent)
    {
        final Group collisions = new Group(parent, SWT.NONE);
        collisions.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        final GridLayout gridLayout = new GridLayout(1, false);
        gridLayout.verticalSpacing = 1;
        collisions.setLayout(gridLayout);
        collisions.setText("List");

        createToolBar(collisions);
        createTree(collisions);
    }

    /**
     * Update the selected collision with its new values.
     * 
     * @param selection The selected item.
     * @param collision The new collision.
     */
    public void updateCollision(TreeItem selection, Collision collision)
    {
        selection.setData(collision);
        setSelectedCollision(collision);
    }

    /**
     * Restore the selected collision with the previous one.
     */
    public void restoreSelectedCollision()
    {
        setSelectedCollision(selectedCollisionBackup);
    }

    /**
     * Set the next selected item from the current one.
     * 
     * @param side -1 for the previous, +1 for the next one.
     */
    public void setNextSelection(int side)
    {
        TreeItem[] items = collisionTree.getSelection();
        if (items.length == 0)
        {
            collisionTree.forceFocus();
            collisionTree.selectAll();
            collisionTree.update();
        }
        items = collisionTree.getSelection();
        if (items.length > 0)
        {
            final int index = Tools.getItemIndex(collisionTree, items[0]) + side;
            final int next = Math.max(0, Math.min(index, collisionTree.getItemCount() - 1));
            final TreeItem previous = collisionTree.getItem(next);
            collisionTree.setSelection(previous);
            collisionTree.forceFocus();
            final Object collision = previous.getData();
            if (collision instanceof Collision)
            {
                setSelectedCollision((Collision) collision);
            }
        }
    }

    /**
     * Get the selected collision.
     * 
     * @return The selected collision.
     */
    public Collision getSelectedCollision()
    {
        return selectedCollision;
    }

    /**
     * Get the collision tree reference.
     * 
     * @return The collision tree reference.
     */
    public Tree getTree()
    {
        return collisionTree;
    }

    /**
     * Set the selected collision, and update the properties fields.
     * 
     * @param collision The selected collision, <code>null</code> if none.
     */
    void setSelectedCollision(Collision collision)
    {
        selectedCollision = collision;
        selectedCollisionBackup = new Collision(collision.getOffsetX(), collision.getOffsetY(), collision.getWidth(),
                collision.getHeight(), collision.hasMirror());
        entityCollisionProperties.setSelectedCollision(collision);
    }

    /**
     * Load the existing collisions from the entity configurable.
     */
    public void loadCollisions()
    {
        final Map<String, Collision> collisions = configurable.getCollisions();
        boolean selected = false;
        for (final String name : collisions.keySet())
        {
            final Collision collision = collisions.get(name);
            final TreeItem item = new TreeItem(collisionTree, SWT.NONE);
            item.setText(name);
            item.setData(collision);
            if (!selected)
            {
                collisionTree.setSelection(item);
                collisionTree.forceFocus();
                setSelectedCollision(collision);
                selected = true;
            }
        }
    }

    /**
     * Create the tool bar.
     * 
     * @param parent The composite parent.
     */
    private void createToolBar(final Composite parent)
    {
        final ToolBar toolbar = new ToolBar(parent, SWT.HORIZONTAL);
        toolbar.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, true, false));

        createAddCollisionToolItem(toolbar);
        createRemoveCollisionToolItem(toolbar);
    }

    /**
     * Create the add collision tool item.
     * 
     * @param toolbar The tool bar reference.
     */
    private void createAddCollisionToolItem(final ToolBar toolbar)
    {
        final ToolItem addCollision = new ToolItem(toolbar, SWT.PUSH);
        addCollision.setImage(EntityCollisionList.ICON_COLLISION_ADD);
        addCollision.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent)
            {
                final InputDialog inputDialog = new InputDialog(toolbar.getShell(), "Collision name",
                        "Enter the collision name", "newCollision", null);
                if (inputDialog.open() == Window.OK)
                {
                    final String name = inputDialog.getValue();
                    final Collision collision = new Collision(0, 0, 0, 0, false);
                    final TreeItem item = new TreeItem(collisionTree, SWT.NONE);
                    item.setText(name);
                    item.setData(collision);

                    entityCollisionProperties.setSelectedCollision(collision);
                    collisionTree.setSelection(item);
                    collisionTree.forceFocus();
                }
            }
        });
    }

    /**
     * Create the remove collision tool item.
     * 
     * @param toolbar The tool bar reference.
     */
    private void createRemoveCollisionToolItem(ToolBar toolbar)
    {
        final ToolItem removeCollision = new ToolItem(toolbar, SWT.PUSH);
        removeCollision.setImage(EntityCollisionList.ICON_COLLISION_REMOVE);
        removeCollision.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent)
            {
                for (final TreeItem item : collisionTree.getSelection())
                {
                    item.dispose();
                }
                collisionTree.layout(true, true);
            }
        });
    }

    /**
     * Create the collision tree.
     * 
     * @param parent The composite parent.
     */
    private void createTree(final Composite parent)
    {
        collisionTree = new Tree(parent, SWT.SINGLE);
        final GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
        data.minimumWidth = 144;
        collisionTree.setLayoutData(data);
        collisionTree.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent)
            {
                final TreeItem[] items = collisionTree.getSelection();
                if (items.length > 0)
                {
                    final TreeItem selection = items[0];
                    final Object data = selection.getData();
                    if (data instanceof Collision)
                    {
                        setSelectedCollision((Collision) data);
                    }
                    else
                    {
                        final Collision collision = new Collision(0, 0, 0, 0, false);
                        selection.setData(collision);
                        setSelectedCollision(collision);
                    }
                }
            }
        });
    }
}
