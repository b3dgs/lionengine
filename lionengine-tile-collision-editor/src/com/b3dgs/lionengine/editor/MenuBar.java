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
import java.util.TreeMap;

import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileFilter;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.UtilityMedia;
import com.b3dgs.lionengine.game.map.MapTileGame;
import com.b3dgs.lionengine.game.map.TileGame;
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
public class MenuBar<C extends Enum<C>, T extends TileGame<C>>
        extends JMenuBar
{
    /** Uid. */
    private static final long serialVersionUID = 1199844863419699405L;
    /** Editor reference. */
    private final TileCollisionEditor<C, T> editor;
    /** Items list. */
    final TreeMap<String, JMenuItem> items;

    /**
     * Constructor.
     * 
     * @param editor The editor reference.
     */
    public MenuBar(final TileCollisionEditor<C, T> editor)
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
                fileSave();
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

        addMenu("Edit");

        menu = addMenu("Tools");
        addItem(menu, "Import Map", new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent event)
            {
                toolsImportMap(null);
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
     */
    void fileSave()
    {
        // Nothing for the moment
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
     * @return <code>true</code> if imported, <code>false</code> else.
     */
    boolean toolsImportMap(JDialog dialog)
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
            rip.start(media, map, UtilityMedia.get(mediaTiles.getPath()));
            final int errors = rip.getErrors();
            if (errors == 0)
            {
                if (dialog != null)
                {
                    UtilitySwing.terminateDialog(dialog);
                }
                editor.world.camera.setLimits(map);
                editor.repaint();
                items.get("Import Map").setEnabled(false);
                return true;
            }
            UtilityMessageBox.error("Import Map", errors + " tiles were not found.\nLevelrip: " + media.getPath()
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
}
