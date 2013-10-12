/*
 * Copyright (C) 2013 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.example.lionheart.editor;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.TreeMap;

import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.example.lionheart.Editor;
import com.b3dgs.lionengine.example.lionheart.WorldType;
import com.b3dgs.lionengine.example.lionheart.map.Map;
import com.b3dgs.lionengine.example.lionheart.map.Tile;
import com.b3dgs.lionengine.swing.ComboItem;
import com.b3dgs.lionengine.swing.UtilityMessageBox;
import com.b3dgs.lionengine.swing.UtilitySwing;
import com.b3dgs.lionengine.utility.LevelRipConverter;

/**
 * Menu bar implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class MenuBar
        extends JMenuBar
{
    /** Uid. */
    private static final long serialVersionUID = 1199844863419699405L;
    /** Editor reference. */
    private final Editor editor;
    /** Items list. */
    final TreeMap<String, JMenuItem> items;

    /**
     * Constructor.
     * 
     * @param editor The editor reference.
     */
    public MenuBar(final Editor editor)
    {
        super();
        this.editor = editor;
        items = new TreeMap<>();
        JMenu menu = addMenu("File");
        addItem(menu, "New", new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent event)
            {
                fileNew(editor);
            }
        });
        addItem(menu, "Load", new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent event)
            {
                fileLoad();
            }
        });
        addItem(menu, "Save", new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent event)
            {
                fileSave();
            }
        }).setEnabled(false);
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
        }).setEnabled(false);

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
     * New action.
     * 
     * @param editor The editor reference.
     */
    void fileNew(final Editor editor)
    {
        final JDialog dialog = UtilitySwing.createDialog(editor, "New", 200, 120);
        dialog.setLayout(new BorderLayout());

        // Center panel
        final JPanel centerPanel = new JPanel(new GridLayout(0, 1));
        dialog.add(centerPanel, BorderLayout.CENTER);

        final JPanel panel = UtilitySwing.createBorderedPanel("World", 2);
        centerPanel.add(panel);
        final JComboBox<ComboItem> combo = UtilitySwing.addMenuCombo("Choice", panel,
                ComboItem.get(WorldType.values()), null);

        // South panel
        final JPanel southPanel = new JPanel(new GridLayout());
        dialog.add(southPanel, BorderLayout.SOUTH);
        UtilitySwing.addButton("Import Map", southPanel, new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent event)
            {
                final WorldType world = (WorldType) ((ComboItem) combo.getSelectedItem()).getObject();
                editor.world.level.setWorld(world);
                UtilitySwing.setEnabled(dialog.getComponents(), false);
                if (toolsImportMap(dialog))
                {
                    editor.world.handlerEntity.removeAll();
                    editor.world.handlerEntity.update();
                    editor.toolBar.entitySelector.loadEntities(world);
                    editor.toolBar.setPaletteEnabled(true);
                    editor.toolBar.setSelectorEnabled(true);
                    editor.toolBar.setEditorEnabled(true);
                    editor.toolBar.entityEditor.setPatrolPanelEnabled(false);
                    items.get("Save").setEnabled(true);
                    items.get("Import Map").setEnabled(true);
                }
                else
                {
                    UtilitySwing.setEnabled(dialog.getComponents(), true);
                }
            }
        });

        UtilitySwing.addButton("Cancel", southPanel, new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent event)
            {
                UtilitySwing.terminateDialog(dialog);
            }
        });

        UtilitySwing.startDialog(dialog);
    }

    /**
     * Load action.
     */
    void fileLoad()
    {
        final MapFilter filter = new MapFilter("Lionheart Remake Map", "lrm");
        final Media media = Media.get(UtilitySwing.createOpenFileChooser(Media.getRessourcesDir(),
                editor.getContentPane(), filter));
        if (media != null)
        {
            try
            {
                editor.world.load(media);
                editor.toolBar.entitySelector.loadEntities(editor.world.level.getWorld());
                editor.toolBar.setPaletteEnabled(true);
                editor.toolBar.setSelectorEnabled(true);
                editor.toolBar.setEditorEnabled(true);
                editor.toolBar.entityEditor.setSelectedEntity(null);
                items.get("Save").setEnabled(true);
                items.get("Import Map").setEnabled(true);
            }
            catch (final IOException exception)
            {
                UtilityMessageBox.error("Load map", "Invalid level file !\nMap file: " + media.getPath());
            }
        }
    }

    /**
     * Save action.
     */
    void fileSave()
    {
        final Media media = Media.get("test1.lrm");
        try
        {
            editor.world.save(media);
        }
        catch (final IOException exception)
        {
            UtilityMessageBox.error("Save map", exception.getMessage() + "\nMap file: " + media.getPath());
        }
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
        final Media media = Media.get(UtilitySwing.createOpenFileChooser(Media.getRessourcesDir(),
                editor.getContentPane(), filter));
        if (media != null)
        {
            final Map map = editor.world.map;
            final LevelRipConverter<Tile> rip = new LevelRipConverter<>();
            try
            {
                rip.start(media, map, Media.get("tiles", editor.world.level.getWorld().asPathName()));
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
        }
        return false;
    }

    /**
     * About action.
     */
    void helpAbout()
    {
        final JDialog dialog = UtilitySwing.createDialog(editor, "About", 212, 96);
        final JTextArea txt = new JTextArea("LionEngine editor\nAuthor: Pierre-Alexandre\nWebsite: www.b3dgs.com");
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
