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
package com.b3dgs.lionengine.editor.dialog;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.core.ImageBuffer;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.core.swt.UtilityImage;
import com.b3dgs.lionengine.core.swt.UtilityMedia;
import com.b3dgs.lionengine.drawable.SpriteTiled;
import com.b3dgs.lionengine.editor.project.dialog.Messages;
import com.b3dgs.lionengine.editor.utility.UtilButton;
import com.b3dgs.lionengine.editor.utility.UtilDialog;
import com.b3dgs.lionengine.editor.utility.UtilSwt;
import com.b3dgs.lionengine.editor.world.WorldModel;
import com.b3dgs.lionengine.game.configurer.ConfigMinimap;
import com.b3dgs.lionengine.game.configurer.ConfigTileGroup;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.map.Minimap;
import com.b3dgs.lionengine.stream.Stream;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Minimap dialog.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class MinimapDialog
{
    /** Shell. */
    private final Shell shell;
    /** Minimap configuration. */
    private Text config;
    /** Automatic generation. */
    private Button automatic;
    /** GC minimap. */
    private GC gc;

    /**
     * Create the dialog.
     * 
     * @param parent The parent reference.
     */
    public MinimapDialog(Shell parent)
    {
        shell = new Shell(parent, SWT.DIALOG_TRIM);
        shell.setText("Minimap");
        final GridLayout layout = new GridLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        shell.setLayout(layout);
    }

    /**
     * Open minimap window.
     */
    public void open()
    {
        final Minimap minimap = WorldModel.INSTANCE.getMinimap();
        minimap.load();

        final Label label = new Label(shell, SWT.NONE);
        label.setLayoutData(new GridData(minimap.getWidth(), minimap.getHeight()));
        gc = new GC(label);

        final Composite composite = new Composite(shell, SWT.NONE);
        composite.setLayout(new GridLayout(2, true));
        createProjectLocationArea(composite);
        createAutomaticButton(composite);

        shell.pack();
        UtilSwt.center(shell);
        shell.setVisible(true);
    }

    /**
     * Perform an automatic color minimap resolution.
     */
    void automaticColor()
    {
        final XmlNode root = Stream.createXmlNode(ConfigMinimap.MINIMAPS);
        final Map<ColorRgba, XmlNode> colors = new HashMap<>();
        for (final Integer sheet : WorldModel.INSTANCE.getMap().getSheets())
        {
            computeSheet(root, colors, sheet);
        }
        Stream.saveXml(root, Medias.create(config.getText()));
        loadConfig();
    }

    /**
     * Load minimap configuration.
     */
    void loadConfig()
    {
        final Minimap minimap = WorldModel.INSTANCE.getMinimap();
        final Media media = Medias.create(config.getText());
        if (media.getFile().isFile())
        {
            minimap.loadPixelConfig(media);
            minimap.prepare();
            render(gc, minimap);
        }
    }

    /**
     * Create the project location area.
     * 
     * @param content The content composite.
     */
    private void createProjectLocationArea(final Composite content)
    {
        final Composite area = new Composite(content, SWT.NONE);
        area.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        area.setLayout(new GridLayout(3, false));

        final Label locationLabel = new Label(area, SWT.NONE);
        locationLabel.setText(Messages.AbstractProjectDialog_Location);

        final Text config = new Text(area, SWT.BORDER);
        config.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        final Button browse = UtilButton.create(area,
                                                com.b3dgs.lionengine.editor.dialog.Messages.AbstractDialog_Browse,
                                                AbstractDialog.ICON_BROWSE);
        browse.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent)
            {
                final File file = UtilDialog.selectResourceFile(content.getShell(), true, new String[]
                {
                    "Minimap configuration file"
                }, new String[]
                {
                    "*.xml"
                });
                if (file != null)
                {
                    config.setText(UtilityMedia.get(file).getPath());
                    loadConfig();
                }
            }
        });
        this.config = config;
    }

    /**
     * Create the automatic generation button.
     * 
     * @param parent The parent reference.
     */
    private void createAutomaticButton(Composite parent)
    {
        final Composite area = new Composite(parent, SWT.NONE);
        area.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        area.setLayout(new GridLayout(1, false));

        automatic = UtilButton.create(area, "Generate", AbstractDialog.ICON_OK);
        automatic.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent event)
            {
                automaticColor();
            }
        });
    }

    /**
     * Compute the current sheet.
     * 
     * @param root The configuration root.
     * @param colors The colors set found.
     * @param sheet The sheet number.
     */
    private void computeSheet(XmlNode root, Map<ColorRgba, XmlNode> colors, Integer sheet)
    {
        final MapTile map = WorldModel.INSTANCE.getMap();
        final SpriteTiled tiles = map.getSheet(sheet);
        final ImageBuffer surface = tiles.getSurface();
        final int tw = map.getTileWidth();
        final int th = map.getTileHeight();

        int number = 0;
        for (int x = 0; x < surface.getWidth(); x += tw)
        {
            for (int y = 0; y < surface.getHeight(); y += th)
            {
                final int h = number * tw % tiles.getWidth();
                final int v = number / tiles.getTilesHorizontal() * th;
                final ColorRgba color = getWeightedTileColor(surface, tw, th, h, v);

                if (!Minimap.NO_TILE.equals(color) && !ColorRgba.TRANSPARENT.equals(color))
                {
                    saveColor(root, colors, color, sheet, number);
                }
                number++;
            }
        }
    }

    /**
     * Save the current color.
     * 
     * @param root The configuration root.
     * @param colors The colors set found.
     * @param color The current color.
     * @param sheet The sheet number.
     * @param number The tile number.
     */
    private void saveColor(XmlNode root, Map<ColorRgba, XmlNode> colors, ColorRgba color, Integer sheet, int number)
    {
        final XmlNode node;
        if (colors.containsKey(color))
        {
            node = colors.get(color);
        }
        else
        {
            node = root.createChild(ConfigMinimap.MINIMAP);
            node.writeInteger(ConfigMinimap.R, color.getRed());
            node.writeInteger(ConfigMinimap.G, color.getGreen());
            node.writeInteger(ConfigMinimap.B, color.getBlue());
            colors.put(color, node);
        }

        final XmlNode tile = node.createChild(ConfigTileGroup.TILE);
        tile.writeInteger(ConfigTileGroup.SHEET, sheet.intValue());
        tile.writeInteger(ConfigTileGroup.NUMBER, number);
    }

    /**
     * Get the weighted color of a tile.
     * 
     * @param surface The surface reference.
     * @param tw The tile width.
     * @param th The tile height.
     * @param tx The starting horizontal location.
     * @param ty The starting vertical location.
     * @return The weighted color.
     */
    private ColorRgba getWeightedTileColor(ImageBuffer surface, int tw, int th, int tx, int ty)
    {
        int r = 0;
        int g = 0;
        int b = 0;
        int count = 0;
        for (int x = 0; x < tw; x++)
        {
            for (int y = 0; y < th; y++)
            {
                final ColorRgba color = new ColorRgba(surface.getRgb(tx + x, ty + y));
                if (!ColorRgba.TRANSPARENT.equals(color))
                {
                    r += color.getRed();
                    g += color.getGreen();
                    b += color.getBlue();
                    count++;
                }
            }
        }
        if (count == 0)
        {
            return ColorRgba.TRANSPARENT;
        }
        return new ColorRgba(r / count, g / count, b / count);
    }

    /**
     * Render the minimap.
     * 
     * @param gc The graphic context.
     * @param minimap The minimap reference.
     */
    private void render(GC gc, Minimap minimap)
    {
        shell.getDisplay().timerExec(0, () ->
        {
            gc.drawImage(UtilityImage.getBuffer(minimap.getSurface()), 0, 0);
        });
    }
}
