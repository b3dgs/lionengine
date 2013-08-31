package com.b3dgs.lionengine.example.c_platform.e_lionheart.editor;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.TreeMap;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import com.b3dgs.lionengine.ImageInfo;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.Editor;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.map.Map;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.map.Tile;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.map.TypeTileCollision;
import com.b3dgs.lionengine.file.File;
import com.b3dgs.lionengine.file.FileReading;
import com.b3dgs.lionengine.file.FileWriting;
import com.b3dgs.lionengine.utility.LevelRipConverter;
import com.b3dgs.lionengine.utility.UtilitySwing;

/**
 * Menu bar implementation.
 */
public class MenuBar
        extends JMenuBar
{
    /** Uid. */
    private static final long serialVersionUID = 1199844863419699405L;
    /** Editor reference. */
    private final Editor editor;
    /** Items list. */
    private final TreeMap<String, JMenuItem> items;

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
            public void actionPerformed(ActionEvent e)
            {
                fileNew();
            }
        });
        addItem(menu, "Load", new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                fileLoad();
            }
        });
        addItem(menu, "Save", new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                fileSave();
            }
        });
        addItem(menu, "Exit", new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                fileExit();
            }
        });

        addMenu("Edit");

        menu = addMenu("Tools");
        addItem(menu, "Import Map", new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                toolsImportMap();
            }
        });

        menu = addMenu("Help");
        addItem(menu, "About", new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                helpAbout();
            }
        });
    }

    /**
     * New action.
     */
    void fileNew()
    {
        final JDialog dialog = UtilitySwing.createDialog(editor, "New", 320, 240);
        dialog.setLayout(new BorderLayout());

        // Center panel
        final JPanel centerPanel = new JPanel(new GridLayout(0, 2));
        dialog.add(centerPanel, BorderLayout.CENTER);

        JPanel panel = UtilitySwing.createBorderedPanel("Background", 2);
        centerPanel.add(panel);

        panel = UtilitySwing.createBorderedPanel("Foreground", 2);
        centerPanel.add(panel);

        panel = UtilitySwing.createBorderedPanel("Map", 2);
        centerPanel.add(panel);

        panel = UtilitySwing.createBorderedPanel("Entities", 2);
        centerPanel.add(panel);

        // South panel
        final JPanel southPanel = new JPanel(new GridLayout());
        dialog.add(southPanel, BorderLayout.SOUTH);
        UtilitySwing.addButton("Accept", southPanel, new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                // Nothing for the moment
            }
        });

        UtilitySwing.addButton("Cancel", southPanel, new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
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
        final JFileChooser fc = new JFileChooser(Media.getRessourcesDir());
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setDialogType(JFileChooser.OPEN_DIALOG);
        fc.setMultiSelectionEnabled(false);
        fc.addChoosableFileFilter(new MapFilter("lrm", "Lionheart Remake Map"));
        final int approve = fc.showOpenDialog(editor.getContentPane());
        if (approve == JFileChooser.APPROVE_OPTION)
        {
            final String filename = fc.getSelectedFile().getPath();
            try (final FileReading file = File.createFileReading(Media.get(filename));)
            {
                file.readString();
                editor.world.map.load(file);
                editor.world.loadEntities(file);
                file.close();
            }
            catch (final IOException ex)
            {
                Verbose.critical(MenuBar.class, "fileLoad", "An error occured while loading map:", filename);
            }
        }
    }

    /**
     * Save action.
     */
    void fileSave()
    {
        final String name = "test1.lrm";
        try (final FileWriting file = File.createFileWriting(Media.get(name));)
        {
            file.writeString("LRM");
            editor.world.map.save(file);
            editor.world.saveEntities(file);
            file.close();
        }
        catch (final IOException ex)
        {
            Verbose.critical(MenuBar.class, "Map save", "An error occured while saving map:", name);
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
     */
    void toolsImportMap()
    {
        final JFileChooser fc = new JFileChooser(Media.getRessourcesDir());
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setDialogType(JFileChooser.OPEN_DIALOG);
        fc.setMultiSelectionEnabled(false);
        fc.addChoosableFileFilter(new MapFilter("bmp", "Map Image Rip"));
        fc.addChoosableFileFilter(new MapFilter("png", "Map Image Rip"));
        final int approve = fc.showOpenDialog(editor.getContentPane());
        if (approve == JFileChooser.APPROVE_OPTION)
        {
            final String file = fc.getSelectedFile().getPath();
            final String localFile = file.substring(Media.getRessourcesDir().length()
                    + file.lastIndexOf(Media.getRessourcesDir()));
            final Media filename = Media.get(localFile);

            // Cut image in tiles
            final Map map = editor.world.map;
            ImageInfo.get(filename);

            final LevelRipConverter<TypeTileCollision, Tile> rip = new LevelRipConverter<>();
            rip.start(filename, map, Media.get("tiles", editor.toolBar.entitySelector.selectedWorld.asPathName()), true);
            editor.world.camera.setLimits(map);

            final DecimalFormat df = new DecimalFormat();
            df.setMaximumFractionDigits(2);
            editor.repaint();
        }
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
