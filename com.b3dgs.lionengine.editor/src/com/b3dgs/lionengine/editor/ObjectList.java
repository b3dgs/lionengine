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
package com.b3dgs.lionengine.editor;

import java.util.Collection;

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

import com.b3dgs.lionengine.Nameable;

/**
 * Represents the object list, allowing to add and remove objects.
 * 
 * @param <T> The object type handled by the list.
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public abstract class ObjectList<T extends Nameable>
{
    /** Icon add. */
    public static final Image ICON_ADD = UtilEclipse.getIcon("add.png");
    /** Icon remove. */
    public static final Image ICON_REMOVE = UtilEclipse.getIcon("remove.png");
    /** Icon save. */
    public static final Image ICON_SAVE = UtilEclipse.getIcon("save.png");
    /** Default new object name. */
    private static final String DEFAULT_NEW_OBJECT_NAME = "new";

    /** Objects list. */
    Tree objectsTree;
    /** Selected data. */
    T selectedObject;
    /** Selected data backup. */
    T selectedObjectBackup;

    /**
     * Create an object list.
     */
    public ObjectList()
    {
        // Nothing to do
    }

    /**
     * Check if the object is an instance of the handled type.
     * 
     * @param object The object to check.
     * @return <code>true</code> if instance of, <code>false</code> else.
     */
    protected abstract boolean instanceOf(Object object);

    /**
     * Cast the object to the handled type.
     * 
     * @param object The object to cast.
     * @return The casted object.
     */
    protected abstract T cast(Object object);

    /**
     * Return a copy of the object.
     * 
     * @param object The object to copy.
     * @return The copied object.
     */
    protected abstract T copyObject(T object);

    /**
     * Create a default object (called when clicked on add object from tool bar).
     * 
     * @return The default object instance.
     */
    protected abstract T createDefaultObject();

    /**
     * Create the objects list area.
     * 
     * @param parent The composite parent.
     */
    public void create(Composite parent)
    {
        final Group objects = new Group(parent, SWT.NONE);
        objects.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        final GridLayout gridLayout = new GridLayout(1, false);
        gridLayout.verticalSpacing = 1;
        objects.setLayout(gridLayout);
        objects.setText(Messages.ObjectList_List);

        createToolBar(objects);
        createTree(objects);
    }

    /**
     * Update the selected object with its new values.
     * 
     * @param selection The selected item.
     * @param object The new object.
     */
    public void update(TreeItem selection, T object)
    {
        if (!selection.isDisposed())
        {
            selection.setData(object);
        }
        setSelectedObject(object);
    }

    /**
     * Restore the selected object with the previous one.
     */
    public void restoreSelectedObject()
    {
        setSelectedObject(selectedObjectBackup);
    }

    /**
     * Set the next selected item from the current one.
     * 
     * @param side -1 for the previous, +1 for the next one.
     */
    public void setNextSelection(int side)
    {
        TreeItem[] items = objectsTree.getSelection();
        if (items.length == 0)
        {
            objectsTree.forceFocus();
            objectsTree.selectAll();
            objectsTree.update();
        }
        items = objectsTree.getSelection();
        if (items.length > 0)
        {
            final int index = UtilSwt.getItemIndex(objectsTree, items[0]) + side;
            final int next = Math.max(0, Math.min(index, objectsTree.getItemCount() - 1));
            final TreeItem previous = objectsTree.getItem(next);
            objectsTree.setSelection(previous);
            objectsTree.forceFocus();
            final Object object = previous.getData();
            if (instanceOf(object))
            {
                setSelectedObject(cast(object));
            }
        }
    }

    /**
     * Get the selected object.
     * 
     * @return The selected object.
     */
    public T getSelectedObject()
    {
        return selectedObject;
    }

    /**
     * Get the objects tree reference.
     * 
     * @return The objects tree reference.
     */
    public Tree getTree()
    {
        return objectsTree;
    }

    /**
     * Load a map of object, and store them by using their name.
     * 
     * @param objects The object collection.
     */
    protected void loadObjects(Collection<T> objects)
    {
        boolean selected = false;
        for (final T object : objects)
        {
            final TreeItem item = new TreeItem(objectsTree, SWT.NONE);
            item.setText(object.getName());
            item.setData(object);
            if (!selected)
            {
                objectsTree.setSelection(item);
                objectsTree.forceFocus();
                setSelectedObject(object);
                selected = true;
            }
        }
    }

    /**
     * Set the selected object.
     * 
     * @param object The selected object, <code>null</code> if none.
     */
    protected void setSelectedObject(T object)
    {
        selectedObject = object;
        selectedObjectBackup = copyObject(object);
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

        createAddObjectToolItem(toolbar);
        createRemoveObjectToolItem(toolbar);
    }

    /**
     * Create the add object tool item.
     * 
     * @param toolbar The tool bar reference.
     */
    private void createAddObjectToolItem(final ToolBar toolbar)
    {
        final ToolItem addObject = new ToolItem(toolbar, SWT.PUSH);
        addObject.setImage(ObjectList.ICON_ADD);
        addObject.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent)
            {
                final InputDialog inputDialog = new InputDialog(toolbar.getShell(),
                        Messages.ObjectList_AddObject_Title, Messages.ObjectList_AddObject_Text,
                        ObjectList.DEFAULT_NEW_OBJECT_NAME, new InputValidator(InputValidator.NAME_MATCH,
                                Messages.InputValidator_Error_Name));
                if (inputDialog.open() == Window.OK)
                {
                    final String name = inputDialog.getValue();
                    final TreeItem item = new TreeItem(objectsTree, SWT.NONE);
                    item.setText(name);
                    item.setData(createDefaultObject());
                }
            }
        });
    }

    /**
     * Create the remove object tool item.
     * 
     * @param toolbar The tool bar reference.
     */
    private void createRemoveObjectToolItem(ToolBar toolbar)
    {
        final ToolItem removeObject = new ToolItem(toolbar, SWT.PUSH);
        removeObject.setImage(ObjectList.ICON_REMOVE);
        removeObject.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent)
            {
                for (final TreeItem item : objectsTree.getSelection())
                {
                    item.dispose();
                }
                objectsTree.layout(true, true);
            }
        });
    }

    /**
     * Create the object tree.
     * 
     * @param parent The composite parent.
     */
    private void createTree(final Composite parent)
    {
        objectsTree = new Tree(parent, SWT.SINGLE);
        objectsTree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        objectsTree.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent)
            {
                final TreeItem[] items = objectsTree.getSelection();
                if (items.length > 0)
                {
                    final TreeItem selection = items[0];
                    final Object data = selection.getData();
                    if (instanceOf(data))
                    {
                        setSelectedObject(cast(data));
                    }
                    else
                    {
                        final T object = createDefaultObject();
                        selection.setData(object);
                        setSelectedObject(object);
                    }
                }
            }
        });
    }
}
