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

import java.util.ArrayList;
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

    /** Listeners. */
    final Collection<ObjectListListener<T>> listeners = new ArrayList<>();
    /** Class type. */
    final Class<T> type;
    /** Properties. */
    final ObjectProperties<T> properties;
    /** Objects list. */
    Tree objectsTree;
    /** Selected item. */
    TreeItem selectedItem;
    /** Selected data. */
    T selectedObject;
    /** Selected data backup. */
    T selectedObjectBackup;

    /**
     * Create an object list.
     * 
     * @param type The list class type.
     */
    public ObjectList(Class<T> type)
    {
        this(type, null);
    }

    /**
     * Create an object list.
     * 
     * @param type The list class type.
     * @param properties The properties reference.
     */
    public ObjectList(Class<T> type, ObjectProperties<T> properties)
    {
        this.type = type;
        this.properties = properties;
    }

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
     * @param name The object name.
     * @return The default object instance.
     */
    protected abstract T createObject(String name);

    /**
     * Add an object list listener.
     * 
     * @param listener The listener reference.
     */
    public void addListener(ObjectListListener<T> listener)
    {
        listeners.add(listener);
    }

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
     * Create the tool bar.
     * 
     * @param parent The composite parent.
     */
    public void createToolBar(final Composite parent)
    {
        final ToolBar toolbar = new ToolBar(parent, SWT.HORIZONTAL);
        toolbar.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, true, false));

        createAddObjectToolItem(toolbar);
        createRemoveObjectToolItem(toolbar);
    }

    /**
     * Create the object tree.
     * 
     * @param parent The composite parent.
     */
    public void createTree(final Composite parent)
    {
        objectsTree = new Tree(parent, SWT.SINGLE);
        final GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
        data.minimumWidth = 128;
        data.minimumHeight = 96;
        objectsTree.setLayoutData(data);
        objectsTree.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent)
            {
                save();
                final TreeItem[] items = objectsTree.getSelection();
                if (items.length > 0)
                {
                    selectedItem = items[0];
                    final Object data = selectedItem.getData();
                    if (instanceOf(data))
                    {
                        setSelectedObject(cast(data));
                    }
                    else
                    {
                        final T object = createObject("default");
                        selectedItem.setData(object);
                        setSelectedObject(object);
                    }
                }
            }
        });
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
     * Get the objects list handled by the tree.
     * 
     * @return The objects list.
     */
    public Collection<T> getObjects()
    {
        final Collection<T> objects = new ArrayList<>();
        for (final TreeItem item : objectsTree.getItems())
        {
            objects.add(cast(item.getData()));
        }
        return objects;
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
     * Clear all items.
     */
    public void clear()
    {
        for (final TreeItem item : objectsTree.getItems())
        {
            item.setData(null);
            item.dispose();
        }
    }

    /**
     * Save current element.
     */
    public void save()
    {
        if (properties != null && selectedItem != null && !selectedItem.isDisposed())
        {
            update(selectedItem, properties.createObject(selectedItem.getText()));
        }
    }

    /**
     * Check if the object is an instance of the handled type.
     * 
     * @param object The object to check.
     * @return <code>true</code> if instance of, <code>false</code> else.
     */
    protected boolean instanceOf(Object object)
    {
        return type.isAssignableFrom(object.getClass());
    }

    /**
     * Cast the object to the handled type.
     * 
     * @param object The object to cast.
     * @return The casted object.
     */
    protected T cast(Object object)
    {
        return type.cast(object);
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
                selectedItem = item;
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
        for (final ObjectListListener<T> listener : listeners)
        {
            listener.notifyObjectSelected(object);
        }
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
                    item.setData(createObject(name));

                    UtilSwt.setDirty(toolbar.getShell(), true);
                }
            }
        });
    }

    /**
     * Create the remove object tool item.
     * 
     * @param toolbar The tool bar reference.
     */
    private void createRemoveObjectToolItem(final ToolBar toolbar)
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
                    for (final ObjectListListener<T> listener : listeners)
                    {
                        listener.notifyObjectDeleted(type.cast(item.getData()));
                    }
                    item.dispose();
                }
                objectsTree.layout(true, true);

                UtilSwt.setDirty(toolbar.getShell(), true);
            }
        });
    }
}
