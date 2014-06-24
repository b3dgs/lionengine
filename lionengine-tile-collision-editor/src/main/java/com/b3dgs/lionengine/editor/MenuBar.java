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
package com.b3dgs.lionengine.editor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileFilter;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.core.Core;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.UtilityMedia;
import com.b3dgs.lionengine.game.map.CollisionFunction;
import com.b3dgs.lionengine.game.map.CollisionTile;
import com.b3dgs.lionengine.game.map.MapTileGame;
import com.b3dgs.lionengine.game.map.TileGame;
import com.b3dgs.lionengine.stream.Stream;
import com.b3dgs.lionengine.stream.XmlNode;
import com.b3dgs.lionengine.swing.UtilityMessageBox;
import com.b3dgs.lionengine.swing.UtilitySwing;
import com.b3dgs.lionengine.utility.LevelRipConverter;

/**
 * Menu bar implementation.
 * 
 * @param <C> The collision type used.
 * @param <T> The tile type used.
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class MenuBar<C extends Enum<C> & CollisionTile, T extends TileGame<C>>
        extends JMenuBar
{
    /** Uid. */
    private static final long serialVersionUID = 1199844863419699405L;

    /**
     * Save the tile node depending of their consecutiveness.
     * 
     * @param node The XML node.
     * @param pattern The pattern number.
     * @param numbers The numbers list.
     * @return <code>true</code> if stored, <code>false</code> else.
     */
    private static boolean saveTileNode(XmlNode node, Integer pattern, List<Integer> numbers)
    {
        final boolean added;
        if (numbers.size() == 1)
        {
            final XmlNode tile = Stream.createXmlNode("tile");
            node.add(tile);
            tile.writeInteger("pattern", pattern.intValue());
            tile.writeInteger("number", numbers.get(0).intValue());
            added = true;
        }
        else if (numbers.size() > 1)
        {
            final XmlNode tile = Stream.createXmlNode("tiles");
            node.add(tile);
            tile.writeInteger("pattern", pattern.intValue());
            tile.writeInteger("start", numbers.get(0).intValue());
            tile.writeInteger("end", numbers.get(numbers.size() - 1).intValue());
            added = true;
        }
        else
        {
            added = false;
        }
        return added;
    }

    /**
     * Split non consecutive numbers per pattern into multiple list of numbers.
     * 
     * @param patterns The pattern set.
     * @param pattern The current pattern.
     * @return The splited numbers list.
     */
    private static List<List<Integer>> splitNonConsecutiveNumbers(Map<Integer, SortedSet<Integer>> patterns,
            Integer pattern)
    {
        final SortedSet<Integer> numbers = patterns.get(pattern);
        final List<List<Integer>> series = new ArrayList<>(8);

        int lastValue = -2;
        List<Integer> currentSerie = null;
        for (final Integer number : numbers)
        {
            final int newValue = number.intValue();
            if (newValue - lastValue != 1)
            {
                currentSerie = new ArrayList<>(8);
                series.add(currentSerie);
            }
            lastValue = newValue;
            if (currentSerie != null)
            {
                currentSerie.add(number);
            }
        }
        return series;
    }

    /** Items list. */
    final TreeMap<String, JMenuItem> items;
    /** Editor reference. */
    private final TileCollisionEditor<C, T> editor;

    /**
     * Constructor.
     * 
     * @param editor The editor reference.
     * @param collisionClass The collision enum type.
     * @param collisions The collisions list.
     */
    public MenuBar(final TileCollisionEditor<C, T> editor, final Class<C> collisionClass, final C[] collisions)
    {
        super();
        this.editor = editor;
        items = new TreeMap<>();
        JMenu menu = addMenu("File");
        addItem(menu, "Save", new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent event)
            {
                fileSave(collisions);
            }
        });
        addItem(menu, "Exit", new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent event)
            {
                fileExit();
            }
        });

        menu = addMenu("Tools");
        addItem(menu, "Import Map", new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent event)
            {
                toolsImportMap(null, collisionClass);
            }
        });

        menu = addMenu("Help");
        addItem(menu, "About", new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent event)
            {
                helpAbout();
            }
        });
    }

    /**
     * Save action.
     * 
     * @param collisions The collisions list.
     */
    void fileSave(C[] collisions)
    {
        final MapTileGame<C, T> map = editor.world.map;
        final XmlNode root = Stream.createXmlNode("collisions");
        for (final C collision : collisions)
        {
            final XmlNode node = Stream.createXmlNode("collision");
            node.writeString("name", collision.name());
            if (saveTilesCollisions(map, node, collision))
            {
                root.add(node);
            }
            final Set<CollisionFunction> functions = collision.getCollisionFunctions();
            if (functions != null)
            {
                for (final CollisionFunction function : functions)
                {
                    final XmlNode functionNode = Stream.createXmlNode("function");
                    functionNode.writeString("name", function.getName());
                    functionNode.writeString("axis", function.getAxis().name());
                    functionNode.writeString("input", function.getInput().name());
                    functionNode.writeDouble("value", function.getValue());
                    functionNode.writeInteger("offset", function.getOffset());
                    functionNode.writeInteger("min", function.getRange().getMin());
                    functionNode.writeInteger("max", function.getRange().getMax());
                    node.add(functionNode);
                }
            }
        }

        Stream.saveXml(root, Core.MEDIA.create("collisions.xml"));
    }

    /**
     * Exit action.
     */
    void fileExit()
    {
        editor.terminate();
    }

    /**
     * Import map action.
     * 
     * @param dialog The dialog reference (<code>null</code> else).
     * @param collisionClass The collision enum type.
     * @return <code>true</code> if imported, <code>false</code> else.
     */
    boolean toolsImportMap(JDialog dialog, Class<C> collisionClass)
    {
        final MapFilter filter = new MapFilter("Map Image Rip", "png", "bmp");
        final File file = UtilitySwing.createOpenFileChooser("Select level rip", UtilityMedia.getRessourcesDir(),
                editor.getContentPane(), filter);
        if (file == null)
        {
            return false;
        }
        final Media media = UtilityMedia.get(file);
        if (media == null)
        {
            return false;
        }

        final File tilesDir = UtilitySwing.createOpenDirectoryChooser("Select tiles directory",
                UtilityMedia.getRessourcesDir(), editor.getContentPane(), new FileFilter()
                {
                    @Override
                    public String getDescription()
                    {
                        return "Tile sheet directory";
                    }

                    @Override
                    public boolean accept(File file)
                    {
                        return file.isDirectory();
                    }
                });
        if (tilesDir == null)
        {
            return false;
        }
        final Media mediaTiles = UtilityMedia.get(tilesDir);
        if (mediaTiles == null)
        {
            return false;
        }

        final MapTileGame<C, T> map = editor.world.map;
        final LevelRipConverter<T> rip = new LevelRipConverter<>();
        try
        {
            rip.start(media, Core.MEDIA.create(mediaTiles.getPath()), map);
            final int errors = rip.getErrors();
            if (errors == 0)
            {
                if (dialog != null)
                {
                    UtilitySwing.terminateDialog(dialog);
                }
                map.loadCollisions(Core.MEDIA.create(mediaTiles.getPath(), "collisions.xml"));
                map.createCollisionDraw(collisionClass);
                editor.world.camera.setLimits(map);
                editor.repaint();
                items.get("Import Map").setEnabled(false);
                return true;
            }
            UtilityMessageBox.error("Import Map", errors + " tiles were not found.\nLevelrip: " + Core.MEDIA.create()
                    + "\nImport interrupted !");
        }
        catch (final LionEngineException exception)
        {
            UtilityMessageBox.error("Import Map", exception.getMessage() + "\nImport interrupted !");
        }
        return false;
    }

    /**
     * About action.
     */
    void helpAbout()
    {
        final JDialog dialog = UtilitySwing.createDialog(editor, "About", 212, 96);
        final JTextArea txt = new JTextArea("Tile collision editor\nAuthor: Pierre-Alexandre\nWebsite: www.b3dgs.com");
        txt.setEditable(false);
        dialog.add(txt);
        UtilitySwing.startDialog(dialog);
    }

    /**
     * Add a menu to the menu bar.
     * 
     * @param name The menu name.
     * @return The menu added instance.
     */
    private JMenu addMenu(String name)
    {
        final JMenu menu = new JMenu(name);
        add(menu);
        return menu;
    }

    /**
     * Add an item to a menu.
     * 
     * @param menu The menu which will receive the item.
     * @param name The item name.
     * @param action The action listener.
     * @return The item added instance.
     */
    private JMenuItem addItem(JMenu menu, String name, ActionListener action)
    {
        final JMenuItem item = new JMenuItem(name);
        item.addActionListener(action);
        menu.add(item);
        items.put(name, item);
        return item;
    }

    /**
     * Save tiles collisions for all of the map tile.
     * 
     * @param map The map reference.
     * @param node The XML node.
     * @param collision The current collision.
     * @return <code>true</code> if at least on tile stored, <code>false</code> else.
     */
    private boolean saveTilesCollisions(MapTileGame<C, T> map, XmlNode node, C collision)
    {
        final Map<Integer, SortedSet<Integer>> patterns = getCollisionsPattern(map, node, collision);
        boolean added = false;
        for (final Integer pattern : patterns.keySet())
        {
            final List<List<Integer>> elements = MenuBar.splitNonConsecutiveNumbers(patterns, pattern);
            for (final List<Integer> numbers : elements)
            {
                added = MenuBar.saveTileNode(node, pattern, numbers);
            }
        }
        return added;
    }

    /**
     * Sort each tile number for each pattern for each collision in a map.
     * 
     * @param map The map reference.
     * @param node The current node.
     * @param collision The current collision.
     * @return The values.
     */
    private Map<Integer, SortedSet<Integer>> getCollisionsPattern(MapTileGame<C, T> map, XmlNode node, C collision)
    {
        final Map<Integer, SortedSet<Integer>> patterns = new HashMap<>(8);
        for (int ty = 0; ty < map.getHeightInTile(); ty++)
        {
            for (int tx = 0; tx < map.getWidthInTile(); tx++)
            {
                final T tile = map.getTile(tx, ty);
                if (tile != null && tile.getCollision() == collision)
                {
                    final Integer pattern = tile.getPattern();
                    final SortedSet<Integer> numbers;

                    if (!patterns.containsKey(pattern))
                    {
                        numbers = new TreeSet<>();
                        patterns.put(pattern, numbers);
                    }
                    else
                    {
                        numbers = patterns.get(pattern);
                    }
                    numbers.add(Integer.valueOf(tile.getNumber() + 1));
                }
            }
        }
        return patterns;
    }
}
