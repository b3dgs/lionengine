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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

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
import com.b3dgs.lionengine.Transparency;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.Graphics;
import com.b3dgs.lionengine.core.ImageBuffer;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.core.swt.ToolsSwt;
import com.b3dgs.lionengine.drawable.SpriteTiled;
import com.b3dgs.lionengine.editor.Focusable;
import com.b3dgs.lionengine.editor.utility.UtilButton;
import com.b3dgs.lionengine.editor.utility.UtilSwt;
import com.b3dgs.lionengine.editor.utility.UtilText;
import com.b3dgs.lionengine.editor.world.WorldModel;
import com.b3dgs.lionengine.game.Orientation;
import com.b3dgs.lionengine.game.configurer.ConfigTileConstraints;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.map.TileConstraint;
import com.b3dgs.lionengine.game.map.TileGroup;
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
    /** Tile color. */
    private final Color tileColor;
    /** Grid color. */
    private final Color gridColor;
    /** Rendering composite. */
    private final Composite composite;
    /** Available tiles. */
    private final List<TileRef> available = new ArrayList<>();
    /** GC composite. */
    private GC gc;
    /** Simple palette mode. */
    private boolean simple;
    /** Current sheet id. */
    private Integer sheetId = Integer.valueOf(0);
    /** Current number. */
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

        composite = new Composite(shell, SWT.DOUBLE_BUFFERED);
        composite.addMouseListener(this);
        gc = new GC(composite);
        tileColor = shell.getDisplay().getSystemColor(SWT.COLOR_GREEN);
        gridColor = shell.getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY);
        simple = Medias.create(map.getGroupsConfig().getParentPath(), ConfigTileConstraints.FILENAME).exists();

        createTypes();
        createBottom();
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

        final Button simplePalette = UtilButton.createCheck(Messages.SimplePalette, area);
        simplePalette.setSelection(simple);
        simplePalette.setEnabled(simple);
        UtilButton.setAction(simplePalette, () ->
        {
            simple = simplePalette.getSelection();
            render();
        });
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
     * Open minimap dialog.
     */
    private void open()
    {
        shell.pack();
        render();
        shell.open();
        if (lastLocation != null)
        {
            shell.setLocation(lastLocation);
        }
        else
        {
            UtilSwt.center(shell);
        }
        render();
    }

    /**
     * Render the active sheet.
     */
    private void render()
    {
        if (!gc.isDisposed())
        {
            final int horizontalTiles;
            final Point size = composite.getSize();
            gc.fillRectangle(0, 0, size.x, size.y);

            if (simple)
            {
                horizontalTiles = renderPaletteSimple();
            }
            else
            {
                horizontalTiles = renderPaletteFull();
            }

            renderGrid(horizontalTiles);

            final int x = number % horizontalTiles * map.getTileWidth();
            final int y = number / horizontalTiles * map.getTileHeight();

            gc.setForeground(tileColor);
            gc.drawRectangle(x, y, map.getTileWidth(), map.getTileHeight());
        }
    }

    /**
     * Render the full palette.
     * 
     * @return The number of horizontal tiles.
     */
    private int renderPaletteFull()
    {
        final SpriteTiled sheet = map.getSheet(sheetId);

        available.clear();
        for (int i = 0; i < sheet.getTilesHorizontal() * sheet.getTilesVertical(); i++)
        {
            available.add(new TileRef(sheetId, i));
        }

        gc.dispose();
        composite.setLayoutData(new GridData(sheet.getWidth(), sheet.getHeight()));
        shell.pack();
        gc = new GC(composite);
        shell.update();
        gc.drawImage(ToolsSwt.getBuffer(sheet.getSurface()), 0, 0);

        return sheet.getTilesHorizontal();
    }

    /**
     * Render the simple palette with essential tiles only.
     * 
     * @return The number of horizontal tiles.
     */
    private int renderPaletteSimple()
    {
        final Collection<TileRef> centered = getCenterTiles();
        final ImageBuffer sheet = getCenterTilesSheet(centered);

        gc.dispose();
        composite.setLayoutData(new GridData(sheet.getWidth(), sheet.getHeight()));
        shell.pack();
        gc = new GC(composite);
        shell.update();
        gc.drawImage(ToolsSwt.getBuffer(sheet), 0, 0);

        if (centered.size() < Constant.DECADE)
        {
            return centered.size();
        }
        return (int) Math.floor(Math.sqrt(centered.size()));
    }

    /**
     * Render the tile grid.
     * 
     * @param horizontalTiles Number of horizontal tiles.
     */
    private void renderGrid(int horizontalTiles)
    {
        final Point size = composite.getSize();
        gc.setForeground(gridColor);
        final int width = Math.min(size.x, horizontalTiles * map.getTileWidth());
        for (int h = 0; h < width; h += map.getTileWidth())
        {
            gc.drawLine(h, 0, h, size.y);
        }
        for (int v = 0; v < size.y; v += map.getTileHeight())
        {
            gc.drawLine(0, v, width, v);
        }
    }

    /**
     * Get the center tiles sheet.
     * 
     * @param tiles The tiles to include in sheet.
     * @return The center tiles sheet.
     */
    private ImageBuffer getCenterTilesSheet(Collection<TileRef> tiles)
    {
        available.clear();
        available.addAll(tiles);

        final int horizontalTiles = Math.max(Constant.DECADE, (int) Math.floor(Math.sqrt(tiles.size())));
        final int width = horizontalTiles * map.getTileWidth();
        final int height = horizontalTiles / Math.max(1, tiles.size()) * map.getTileHeight() - map.getTileHeight();

        final ImageBuffer buff = Graphics.createImageBuffer(width + 1, height + 1, Transparency.BITMASK);
        final Graphic g = buff.createGraphic();

        int id = 0;
        for (final TileRef tile : tiles)
        {
            final SpriteTiled sheet = map.getSheet(tile.getSheet());
            sheet.setTile(tile.getNumber());

            final int x = id % horizontalTiles * map.getTileWidth();
            final int y = id / horizontalTiles * map.getTileHeight();
            sheet.setLocation(x, y);
            sheet.render(g);

            id++;
        }
        g.dispose();
        return buff;
    }

    /**
     * Get the center tiles list.
     * 
     * @return The center tiles.
     */
    private Collection<TileRef> getCenterTiles()
    {
        final Media media = Medias.create(map.getGroupsConfig().getParentPath(), ConfigTileConstraints.FILENAME);
        final Map<TileRef, Map<Orientation, TileConstraint>> constraints = ConfigTileConstraints.create(media);
        final Collection<TileRef> centerTiles = new HashSet<>();

        for (final Map.Entry<TileRef, Map<Orientation, TileConstraint>> entry : constraints.entrySet())
        {
            final TileRef tile = entry.getKey();
            if ((isCenter(tile, entry.getValue()) || !hasCenter(tile, constraints)) && !existsGroup(tile, centerTiles))
            {
                centerTiles.add(tile);
            }
        }
        return centerTiles;
    }

    /**
     * Check if is center tile.
     * 
     * @param tile The tile.
     * @param constraints The constraints.
     * @return <code>true</code> if center, <code>false</code> else.
     */
    private boolean isCenter(TileRef tile, Map<Orientation, TileConstraint> constraints)
    {
        int check = 0;
        for (final TileConstraint constraint : constraints.values())
        {
            for (final TileRef allowed : constraint.getAllowed())
            {
                if (sameGroup(tile, allowed))
                {
                    check++;
                    break;
                }
            }
        }
        return check > constraints.size() - 1;
    }

    /**
     * Check if the group of the tile has at least one center tile.
     * 
     * @param tile The tile to check group.
     * @param constraints The associated constraints.
     * @return <code>true</code> if one tile center exists at least, <code>false</code> else.
     */
    private boolean hasCenter(TileRef tile, Map<TileRef, Map<Orientation, TileConstraint>> constraints)
    {
        final TileGroup group = map.getGroup(tile.getSheet(), tile.getNumber());
        for (final TileRef tileRef : group.getTiles())
        {
            if (isCenter(tileRef, constraints.get(tileRef)))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if tiles are in the same group.
     * 
     * @param tile1 The first tile.
     * @param tile2 The second tile.
     * @return <code>true</code> if in same group, <code>false</code> else.
     */
    private boolean sameGroup(TileRef tile1, TileRef tile2)
    {
        final String group1 = map.getGroup(tile1.getSheet(), tile1.getNumber()).getName();
        final String group2 = map.getGroup(tile2.getSheet(), tile2.getNumber()).getName();
        return group1.equals(group2);
    }

    /**
     * Check if tile has the same group of at least one tile from the list.
     * 
     * @param tile The tile to check.
     * @param tiles The tiles to compare within.
     * @return <code>true</code> if group already exists in the list, <code>false</code> else.
     */
    private boolean existsGroup(TileRef tile, Collection<TileRef> tiles)
    {
        for (final TileRef existing : tiles)
        {
            if (sameGroup(tile, existing))
            {
                return true;
            }
        }
        return false;
    }

    /*
     * MouseListener
     */

    @Override
    public void mouseDown(MouseEvent event)
    {
        final int x = (int) Math.floor(event.x / (double) map.getTileWidth());
        final int y = (int) Math.floor(event.y / (double) map.getTileHeight());
        final int n = x + y * map.getSheet(sheetId).getTilesHorizontal();
        if (n < available.size())
        {
            number = n;
            model.setSelectedTile(available.get(number));
        }
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
