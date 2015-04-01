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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.b3dgs.lionengine.editor.InputValidator;
import com.b3dgs.lionengine.editor.ObjectList;
import com.b3dgs.lionengine.editor.UtilEclipse;
import com.b3dgs.lionengine.editor.UtilSwt;
import com.b3dgs.lionengine.editor.dialogs.AbstractDialog;
import com.b3dgs.lionengine.editor.world.WorldViewModel;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.map.MapTileFeature;
import com.b3dgs.lionengine.game.map.MapTileGame;

/**
 * Represents the map properties dialog.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class MapPropertiesDialog
        extends AbstractDialog
{
    /** Icon. */
    private static final Image ICON = UtilEclipse.getIcon("dialog", "edit-tilesheets.png");
    /** Default tile size. */
    private static final String DEFAULT_TILE_SIZE = "16";

    /**
     * Get the tree selection.
     * 
     * @param tree The tree reference.
     * @return The tree selection.
     */
    static Collection<Class<? extends MapTileFeature>> getSelection(Tree tree)
    {
        final Collection<Class<? extends MapTileFeature>> collection = new ArrayList<>();
        for (final TreeItem item : tree.getItems())
        {
            final Object data = item.getData();
            if (data instanceof Class)
            {
                final Class<?> clazz = (Class<?>) data;
                if (MapTileFeature.class.isAssignableFrom(clazz))
                {
                    collection.add(clazz.asSubclass(MapTileFeature.class));
                }
            }
        }
        return collection;
    }

    /** Map tile width text. */
    Text tileWidthText;
    /** Map tile height text. */
    Text tileHeightText;
    /** Features list. */
    Tree features;

    /**
     * Create a map properties dialog.
     * 
     * @param parent The shell parent.
     */
    public MapPropertiesDialog(Shell parent)
    {
        super(parent, Messages.MapPropertiesDialog_Title, Messages.MapPropertiesDialog_HeaderTitle,
                Messages.MapPropertiesDialog_HeaderDesc, MapPropertiesDialog.ICON);
        createDialog();
        finish.setEnabled(true);
        finish.forceFocus();
    }

    /**
     * Create the map size area.
     * 
     * @param content The content composite.
     */
    private void createMapSizeArea(Composite content)
    {
        final Composite mapSizeArea = new Composite(content, SWT.NONE);
        mapSizeArea.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        mapSizeArea.setLayout(new GridLayout(2, false));

        final Label tileWidthLabel = new Label(mapSizeArea, SWT.NONE);
        tileWidthLabel.setText(Messages.MapPropertiesDialog_TileWidth);

        tileWidthText = new Text(mapSizeArea, SWT.BORDER);
        tileWidthText.setText(DEFAULT_TILE_SIZE);
        tileWidthText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        tileWidthText.addVerifyListener(UtilSwt.createVerify(tileWidthText, InputValidator.INTEGER_POSITIVE_MATCH));

        final Label tileHeightLabel = new Label(mapSizeArea, SWT.NONE);
        tileHeightLabel.setText(Messages.MapPropertiesDialog_TileHeight);

        tileHeightText = new Text(mapSizeArea, SWT.BORDER);
        tileHeightText.setText(DEFAULT_TILE_SIZE);
        tileHeightText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        tileHeightText.addVerifyListener(UtilSwt.createVerify(tileHeightText, InputValidator.INTEGER_POSITIVE_MATCH));
    }

    /**
     * Create the map features list.
     * 
     * @param content The content composite.
     */
    private void createFeaturesList(Composite content)
    {
        final Composite featuresArea = new Composite(content, SWT.NONE);
        featuresArea.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        featuresArea.setLayout(new GridLayout(1, false));

        final ToolBar toolBar = new ToolBar(featuresArea, SWT.NONE);
        toolBar.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        toolBar.setLayout(new FillLayout(SWT.HORIZONTAL));

        createAddFeatureButton(toolBar);
        createRemoveFeatureButton(toolBar);

        features = new Tree(featuresArea, SWT.NONE);
    }

    /**
     * Create the add features button.
     * 
     * @param toolBar The tool bar reference.
     */
    private void createAddFeatureButton(ToolBar toolBar)
    {
        final ToolItem add = new ToolItem(toolBar, SWT.PUSH);
        add.setText(Messages.MapPropertiesDialog_AddFeature);
        add.setImage(ObjectList.ICON_ADD);
        add.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent event)
            {
                final Collection<Class<? extends MapTileFeature>> current = getSelection(features);
                final MapFeaturesSelectionDialog dialog = new MapFeaturesSelectionDialog(getParent(), current);
                dialog.open();

                for (final Class<? extends MapTileFeature> feature : dialog.getSelection())
                {
                    final TreeItem item = new TreeItem(features, SWT.NONE);
                    item.setText(feature.getName());
                    item.setData(feature);
                }
                features.pack();
            }
        });
    }

    /**
     * Create the remove features button.
     * 
     * @param toolBar The tool bar reference.
     */
    private void createRemoveFeatureButton(ToolBar toolBar)
    {
        final ToolItem remove = new ToolItem(toolBar, SWT.PUSH);
        remove.setText(Messages.MapPropertiesDialog_RemoveFeature);
        remove.setImage(ObjectList.ICON_REMOVE);
        remove.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent event)
            {
                for (final TreeItem item : features.getSelection())
                {
                    item.dispose();
                }
            }
        });
    }

    /*
     * AbstractDialog
     */

    @Override
    protected void createContent(Composite content)
    {
        createMapSizeArea(content);
        createFeaturesList(content);
    }

    @Override
    protected void onFinish()
    {
        WorldViewModel.INSTANCE.getMap().clear();
        final MapTile map = new MapTileGame(Integer.parseInt(tileWidthText.getText()), Integer.parseInt(tileHeightText
                .getText()), WorldViewModel.INSTANCE.getCamera());
        WorldViewModel.INSTANCE.setMap(map);
    }
}
