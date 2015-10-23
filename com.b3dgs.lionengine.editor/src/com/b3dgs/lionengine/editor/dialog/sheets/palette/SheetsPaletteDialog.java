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
import java.util.HashMap;
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

import com.b3dgs.lionengine.ColorRgba;
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
import com.b3dgs.lionengine.game.configurer.ConfigTileConstraint;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.map.Minimap;
import com.b3dgs.lionengine.game.map.TileConstraint;
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

    /**
     * Compare color delta against other colors.
     * 
     * @param colors The colors to compare to.
     * @param color The color to check.
     * @param minimum The minimum delta value to return <code>true</code>.
     * @return <code>true</code> if delta is far enough, <code>false</code> else.
     */
    private static boolean compareDelta(Collection<ColorRgba> colors, ColorRgba color, double minimum)
    {
        for (final ColorRgba current : colors)
        {
            final double delta = ColorRgba.getDelta(color, current);
            if (delta < minimum)
            {
                return false;
            }
        }
        return true;
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
        simple = Medias.create(map.getGroupsConfig().getParentPath(), ConfigTileConstraint.FILENAME).exists();

        createTypes();
        createBottom();
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
     * Render the active sheet.
     */
    private void render()
    {
        if (!gc.isDisposed())
        {
            final int horizontalTiles;
            if (simple)
            {
                horizontalTiles = renderPaletteSimple();
            }
            else
            {
                horizontalTiles = renderPaletteFull();
            }

            renderGrid();

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
        final ImageBuffer sheet = getCenterTilesSheet();

        gc.dispose();
        composite.setLayoutData(new GridData(sheet.getWidth(), sheet.getHeight()));
        shell.pack();
        gc = new GC(composite);
        shell.update();
        gc.drawImage(ToolsSwt.getBuffer(sheet), 0, 0);

        return sheet.getWidth() / map.getTileWidth() * (sheet.getHeight() / map.getTileHeight());
    }

    /**
     * Get the center tiles sheet.
     * 
     * @return The center tiles sheet.
     */
    private ImageBuffer getCenterTilesSheet()
    {
        final Collection<TileRef> centered = getCenterTiles();

        available.clear();
        available.addAll(centered);

        final int horizontalTiles = Math.max(Constant.DECADE, (int) Math.floor(Math.sqrt(centered.size())));
        final int width = horizontalTiles * map.getTileWidth();
        final int height = horizontalTiles / centered.size() * map.getTileHeight();

        final ImageBuffer buff = Graphics.createImageBuffer(width + 1, height + 1, Transparency.BITMASK);
        final Graphic g = buff.createGraphic();

        int id = 0;
        for (final TileRef tile : centered)
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
        final Media media = Medias.create(map.getGroupsConfig().getParentPath(), ConfigTileConstraint.FILENAME);
        final Map<TileRef, Map<Orientation, TileConstraint>> constraints = ConfigTileConstraint.create(media);
        final Collection<TileRef> centerTiles = new HashSet<>();
        final Map<String, Collection<ColorRgba>> deltas = new HashMap<>();

        for (final Map.Entry<TileRef, Map<Orientation, TileConstraint>> entry : constraints.entrySet())
        {
            final TileRef tile = entry.getKey();
            if (isCenter(tile, entry.getValue()) && checkCenterTile(tile, deltas))
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
        final String group = map.getGroup(tile.getSheet(), tile.getNumber()).getName();

        int check = 0;
        for (final TileConstraint constraint : constraints.values())
        {
            for (final TileRef allowed : constraint.getAllowed())
            {
                if (group.equals(map.getGroup(allowed.getSheet(), allowed.getNumber()).getName()))
                {
                    check++;
                    break;
                }
            }
        }
        return check > constraints.size() - 1;
    }

    /**
     * Check if center tile is not redundant by checking its weighted color.
     * 
     * @param tile The tile to check.
     * @param deltas The color deltas.
     * @return <code>true</code> if can be considered as unique, <code>false</code> else.
     */
    private boolean checkCenterTile(TileRef tile, Map<String, Collection<ColorRgba>> deltas)
    {
        final ColorRgba color = getWeightedColor(tile);
        final String group = map.getGroup(tile.getSheet(), tile.getNumber()).getName();
        final Collection<ColorRgba> colors;
        final boolean unique;
        if (!deltas.containsKey(group))
        {
            colors = new HashSet<>();
            deltas.put(group, colors);
            unique = true;
        }
        else
        {
            colors = deltas.get(group);
            unique = compareDelta(colors, color, 10);
        }
        if (unique)
        {
            colors.add(color);
        }
        return unique;
    }

    /**
     * Get the weighted color of the tile.
     * 
     * @param tile The tile to check.
     * @return The weighted tile color.
     */
    private ColorRgba getWeightedColor(TileRef tile)
    {
        final SpriteTiled sheet = map.getSheet(tile.getSheet());
        final int tw = map.getTileWidth();
        final int th = map.getTileHeight();
        final int x = tile.getNumber() % sheet.getTilesHorizontal() * tw;
        final int y = tile.getNumber() / sheet.getTilesHorizontal() * th;
        final ColorRgba color = Minimap.getWeightedColor(sheet.getSurface(), x, y, tw, th);
        return color;
    }

    /**
     * Render the tile grid.
     */
    private void renderGrid()
    {
        final Point size = composite.getSize();
        gc.setForeground(gridColor);
        for (int h = 0; h < size.x; h += map.getTileWidth())
        {
            gc.drawLine(h, 0, h, size.y);
        }
        for (int v = 0; v < size.y; v += map.getTileHeight())
        {
            gc.drawLine(0, v, size.x, v);
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
