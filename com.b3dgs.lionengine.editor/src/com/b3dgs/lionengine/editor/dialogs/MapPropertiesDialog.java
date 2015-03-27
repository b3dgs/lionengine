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
package com.b3dgs.lionengine.editor.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.b3dgs.lionengine.editor.InputValidator;
import com.b3dgs.lionengine.editor.UtilEclipse;
import com.b3dgs.lionengine.editor.UtilSwt;
import com.b3dgs.lionengine.editor.world.WorldViewModel;
import com.b3dgs.lionengine.game.map.MapTile;
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

    /** Map tile width text. */
    Text tileWidthText;
    /** Map tile height text. */
    Text tileHeightText;

    /**
     * Create an import map dialog.
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
     * Update the tips label.
     */
    void updateTipsLabel()
    {
        tipsLabel.setVisible(false);
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
        tileWidthText.setText("16");
        tileWidthText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        tileWidthText.addVerifyListener(UtilSwt.createVerify(tileWidthText, InputValidator.INTEGER_POSITIVE_MATCH));

        final Label tileHeightLabel = new Label(mapSizeArea, SWT.NONE);
        tileHeightLabel.setText(Messages.MapPropertiesDialog_TileHeight);

        tileHeightText = new Text(mapSizeArea, SWT.BORDER);
        tileHeightText.setText("16");
        tileHeightText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        tileHeightText.addVerifyListener(UtilSwt.createVerify(tileHeightText, InputValidator.INTEGER_POSITIVE_MATCH));
    }

    /*
     * AbstractDialog
     */

    @Override
    protected void createContent(Composite content)
    {
        createMapSizeArea(content);
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
