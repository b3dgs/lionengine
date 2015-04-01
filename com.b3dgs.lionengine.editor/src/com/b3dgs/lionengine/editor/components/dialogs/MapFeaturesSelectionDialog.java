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
package com.b3dgs.lionengine.editor.components.dialogs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.b3dgs.lionengine.editor.UtilEclipse;
import com.b3dgs.lionengine.editor.dialogs.AbstractDialog;
import com.b3dgs.lionengine.game.map.MapTileFeature;

/**
 * Represents the map features selection dialog.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class MapFeaturesSelectionDialog
        extends AbstractDialog
{
    /** Icon. */
    private static final Image ICON = UtilEclipse.getIcon("dialog", "edit-tilesheets.png");
    /** Class data. */
    private static final String CLASS_DATA = "class";

    /**
     * Get the tree selection.
     * 
     * @param tree The tree reference.
     * @return The tree selection.
     */
    private static Collection<Class<? extends MapTileFeature>> getSelection(Tree tree)
    {
        final Collection<Class<? extends MapTileFeature>> collection = new ArrayList<>();
        for (final TreeItem item : tree.getItems())
        {
            final TreeItem[] items = item.getItems();
            if (item.getChecked() && items != null)
            {
                for (final TreeItem current : items)
                {
                    final Object data = current.getData(CLASS_DATA);
                    if (current.getChecked() && data instanceof Class)
                    {
                        final Class<?> clazz = (Class<?>) data;
                        if (MapTileFeature.class.isAssignableFrom(clazz))
                        {
                            collection.add(clazz.asSubclass(MapTileFeature.class));
                        }
                    }
                }
            }
        }
        return collection;
    }

    /** Selected features. */
    private final Collection<Class<? extends MapTileFeature>> selection = new ArrayList<>();
    /** Current features. */
    private final Collection<Class<? extends MapTileFeature>> current;
    /** Selectable features list. */
    Tree features;

    /**
     * Create a map feature selection dialog.
     * 
     * @param parent The shell parent.
     * @param current The current selected features.
     */
    public MapFeaturesSelectionDialog(Shell parent, Collection<Class<? extends MapTileFeature>> current)
    {
        super(parent, Messages.MapFeatureSelectionDialog_Title, Messages.MapFeatureSelectionDialog_HeaderTitle,
                Messages.MapFeatureSelectionDialog_HeaderDesc, MapFeaturesSelectionDialog.ICON);
        this.current = current;
        createDialog();
        finish.setEnabled(true);
        finish.forceFocus();
    }

    /**
     * Get the selected features.
     * 
     * @return The selected features (read-only).
     */
    public Collection<Class<? extends MapTileFeature>> getSelection()
    {
        return Collections.unmodifiableCollection(selection);
    }

    /**
     * Create the available map features list.
     * 
     * @param content The content composite.
     */
    private void createFeaturesList(Composite content)
    {
        final Composite featuresArea = new Composite(content, SWT.NONE);
        featuresArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        featuresArea.setLayout(new GridLayout(1, false));

        features = new Tree(featuresArea, SWT.SINGLE | SWT.CHECK);
        features.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        final CheckboxTreeViewer viewer = new CheckboxTreeViewer(features);
        viewer.addCheckStateListener(new ICheckStateListener()
        {
            @Override
            public void checkStateChanged(CheckStateChangedEvent event)
            {
                final Object object = event.getElement();
                if (object instanceof TreeItem)
                {
                    final TreeItem item = (TreeItem) object;
                    final TreeItem parent = item.getParentItem();
                    if (parent != null)
                    {
                        for (final TreeItem other : parent.getItems())
                        {
                            if (!other.equals(object))
                            {
                                other.setChecked(false);
                            }
                        }
                        parent.setChecked(item.getChecked());
                    }
                    else
                    {
                        item.setChecked(false);
                        for (final TreeItem current : item.getItems())
                        {
                            current.setChecked(false);
                        }
                    }
                }
            }
        });

        createTree();
    }

    /**
     * Create the tree with its features and their implementations.
     */
    private void createTree()
    {
        final Collection<Class<? extends MapTileFeature>> available = UtilEclipse.getImplementing(MapTileFeature.class);
        for (final Class<? extends MapTileFeature> feature : available)
        {
            if (feature.isInterface() && !current.contains(feature))
            {
                final TreeItem item = new TreeItem(features, SWT.NONE);
                item.setText(feature.getName());
                item.setData(item);
                item.setData(CLASS_DATA, feature);

                for (final Class<? extends MapTileFeature> featureImpl : UtilEclipse.getImplementing(feature))
                {
                    final TreeItem child = new TreeItem(item, SWT.NONE);
                    child.setText(featureImpl.getName());
                    child.setData(child);
                    child.setData(CLASS_DATA, featureImpl);
                }
                item.setExpanded(true);
            }
        }
    }

    /*
     * AbstractDialog
     */

    @Override
    protected void createContent(Composite content)
    {
        createFeaturesList(content);
    }

    @Override
    protected void onFinish()
    {
        selection.clear();
        selection.addAll(getSelection(features));
    }
}
