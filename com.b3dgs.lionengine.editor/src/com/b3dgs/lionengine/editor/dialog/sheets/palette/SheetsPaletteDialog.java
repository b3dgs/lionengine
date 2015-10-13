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
package com.b3dgs.lionengine.editor.dialog.sheets.palette;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.core.swt.ToolsSwt;
import com.b3dgs.lionengine.drawable.SpriteTiled;
import com.b3dgs.lionengine.editor.Focusable;
import com.b3dgs.lionengine.editor.utility.UtilButton;
import com.b3dgs.lionengine.editor.utility.UtilSwt;
import com.b3dgs.lionengine.editor.utility.UtilText;
import com.b3dgs.lionengine.editor.world.WorldModel;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.map.TileRef;

/**
 * Sheets palette dialog.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class SheetsPaletteDialog implements MouseListener, Focusable
{
    /** Dialog instance. */
    private static SheetsPaletteDialog instance;
    /** Last location. */
    private static Point lastLocation;

    /**
     * Open the dialog.
     * 
     * @param parent The parent shell.
     */
    public static void open(Shell parent)
    {
        if (instance == null)
        {
            instance = new SheetsPaletteDialog(parent);
            instance.open();
        }
    }

    /** Model reference. */
    private final SheetsPaletteModel model = SheetsPaletteModel.INSTANCE;
    /** Map reference. */
    private final MapTile map = WorldModel.INSTANCE.getMap();
    /** Shell dialog. */
    private final Shell shell;
    /** GC minimap. */
    private final GC gc;
    /** Tile color. */
    private final Color tileColor;
    /** Grid color. */
    private final Color gridColor;
    /** Current sheet id. */
    private Integer sheetId = Integer.valueOf(0);
    /** Current tile number. */
    private int number;

    /**
     * Create the dialog.
     * 
     * @param parent The parent reference.
     */
    private SheetsPaletteDialog(Shell parent)
    {
        shell = new Shell(parent, SWT.DIALOG_TRIM);
        shell.setText(Messages.Title);
        shell.setLayout(UtilSwt.borderless());
        shell.addDisposeListener(event ->
        {
            lastLocation = shell.getLocation();
            instance = null;
        });

        int width = 0;
        int height = 0;
        for (final Integer id : map.getSheets())
        {
            final SpriteTiled sheet = map.getSheet(id);
            width = Math.max(width, sheet.getWidth());
            height = Math.max(height, sheet.getHeight());
        }

        final Composite composite = new Composite(shell, SWT.DOUBLE_BUFFERED);
        composite.setLayoutData(new GridData(width, height));
        composite.addMouseListener(this);
        gc = new GC(composite);
        tileColor = shell.getDisplay().getSystemColor(SWT.COLOR_GREEN);
        gridColor = shell.getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY);

        createTypes();
        createBottom();
    }

    /**
     * Open minimap dialog.
     */
    private void open()
    {
        UtilSwt.open(shell);
        if (lastLocation != null)
        {
            shell.setLocation(lastLocation);
        }
        render();
    }

    /**
     * Create the palette type part.
     */
    private void createTypes()
    {
        final Group area = new Group(shell, SWT.NONE);
        area.setText(Messages.TileType);
        area.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        area.setLayout(new GridLayout(2, false));

        final Button typeSelection = UtilButton.createRadio(Messages.TileSelection, area);
        typeSelection.setSelection(true);
        UtilButton.setAction(typeSelection, () -> model.setSheetPaletteType(SheetPaletteType.SELECTION));
        final Button typeEdit = UtilButton.createRadio(Messages.TileEdition, area);
        UtilButton.setAction(typeEdit, () -> model.setSheetPaletteType(SheetPaletteType.EDITION));
    }

    /**
     * Create the bottom part.
     */
    private void createBottom()
    {
        final Group area = new Group(shell, SWT.NONE);
        area.setText(Messages.Sheet);
        area.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        area.setLayout(new GridLayout(3, false));

        final int sheetsNumber = map.getSheets().size() - 1;

        final Button previous = UtilButton.create(area, Messages.Decrease, null);
        final Text sheetIdText = UtilText.create(Messages.CurrentSheet, area);
        sheetIdText.setEditable(false);
        sheetIdText.setText(String.valueOf(sheetId) + Constant.SLASH + sheetsNumber);
        final Button next = UtilButton.create(area, Messages.Increase, null);

        UtilButton.setAction(previous, () ->
        {
            sheetId = Integer.valueOf(Math.max(sheetId.intValue() - 1, 0));
            sheetIdText.setText(sheetId.toString() + Constant.SLASH + sheetsNumber);
            render();
        });
        UtilButton.setAction(next, () ->
        {
            sheetId = Integer.valueOf(Math.min(sheetId.intValue() + 1, sheetsNumber));
            sheetIdText.setText(sheetId.toString() + Constant.SLASH + sheetsNumber);
            render();
        });
    }

    /**
     * Render the active sheet.
     */
    private void render()
    {
        if (!gc.isDisposed())
        {
            final SpriteTiled sheet = map.getSheet(sheetId);
            gc.drawImage(ToolsSwt.getBuffer(sheet.getSurface()), 0, 0);

            renderGrid();

            final int x = number % sheet.getTilesHorizontal() * map.getTileWidth();
            final int y = number / sheet.getTilesHorizontal() * map.getTileHeight();

            gc.setForeground(tileColor);
            gc.drawRectangle(x, y, map.getTileWidth(), map.getTileHeight());
        }
    }

    /**
     * Render the tile grid.
     */
    private void renderGrid()
    {
        final SpriteTiled sheet = map.getSheet(sheetId);
        gc.setForeground(gridColor);
        for (int h = 0; h < sheet.getWidth(); h += map.getTileWidth())
        {
            gc.drawLine(h, 0, h, sheet.getHeight());
        }
        for (int v = 0; v < sheet.getHeight(); v += map.getTileHeight())
        {
            gc.drawLine(0, v, sheet.getWidth(), v);
        }
    }

    /*
     * MouseListener
     */

    @Override
    public void mouseDown(MouseEvent event)
    {
        final int x = (int) Math.floor(event.x / (double) map.getTileWidth());
        final int y = (int) Math.floor(event.y / (double) map.getTileHeight());
        number = x + y * map.getSheet(sheetId).getTilesHorizontal();
        model.setSelectedTile(new TileRef(sheetId, number));
        render();
    }

    @Override
    public void mouseUp(MouseEvent event)
    {
        // Nothing to do
    }

    @Override
    public void mouseDoubleClick(MouseEvent event)
    {
        // Nothing to do
    }

    /*
     * Focusable
     */

    @Override
    public void focus()
    {
        shell.forceFocus();
    }
}
