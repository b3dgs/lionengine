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
package com.b3dgs.lionengine.example.c_platform.e_lionheart;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import com.b3dgs.lionengine.Version;
import com.b3dgs.lionengine.core.UtilityMath;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.editor.MenuBar;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.editor.SelectionType;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.editor.StateBar;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.editor.ToolBar;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.editor.WorldPanel;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.Entity;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.EntityType;

/**
 * Main editor frame.
 */
public class Editor
        extends JFrame
{
    /** Editor version. */
    public static final Version VERSION = Version.create(0, 1, 0);
    /** Uid. */
    private static final long serialVersionUID = -1248793737263689450L;
    /** Horizontal moving speed in tile. */
    private static final int STEP_TILE_H = 8;
    /** Vertical moving speed in tile. */
    private static final int STEP_TILE_V = 8;
    /** World panel reference. */
    public final WorldPanel world;
    /** Tool bar reference. */
    public final ToolBar toolBar;
    /** Current horizontal view offset in tile. */
    private int hOffset;
    /** Current vertical view offset in tile. */
    private int vOffset;
    /** Menu bar reference. */
    private final MenuBar menuBar;
    /** State bar reference. */
    private final StateBar stateBar;
    /** Current selected entity data. */
    private EntityType selectedEntity;
    /** Current state selection. */
    private SelectionType selectionState;

    /**
     * Constructor.
     */
    Editor()
    {
        super("Editor");
        world = new WorldPanel(this);
        menuBar = new MenuBar(this);
        toolBar = new ToolBar(this);
        stateBar = new StateBar(this);
        selectedEntity = null;
        selectionState = SelectionType.SELECT;
        init();
    }

    /**
     * Start the editor and display it.
     */
    public void start()
    {
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Close the editor.
     */
    public void terminate()
    {
        dispose();
        System.exit(0);
    }

    /**
     * Set the selected entity type.
     * 
     * @param type The selected entity type.
     */
    public void setSelectedEntity(EntityType type)
    {
        selectedEntity = type;
    }

    /**
     * Get the horizontal view offset in tile.
     * 
     * @return The horizontal view offset in tile.
     */
    public int getOffsetViewInTileH()
    {
        return hOffset;
    }

    /**
     * Get the vertical view offset in tile.
     * 
     * @return The vertical view offset in tile.
     */
    public int getOffsetViewInTileV()
    {
        return vOffset;
    }

    /**
     * Get the horizontal view offset.
     * 
     * @return The horizontal view offset.
     */
    public int getOffsetViewH()
    {
        return hOffset * world.map.getTileWidth();
    }

    /**
     * Get the vertical view offset.
     * 
     * @return The vertical view offset.
     */
    public int getOffsetViewV()
    {
        return vOffset * world.map.getTileHeight();
    }

    /**
     * Get the selected entity type.
     * 
     * @return The selected entity type.
     */
    public EntityType getSelectedEntity()
    {
        return selectedEntity;
    }

    /**
     * Get the current selection state.
     * 
     * @return The current selection state.
     */
    public SelectionType getSelectionState()
    {
        return selectionState;
    }

    /**
     * Set the current selection state.
     * 
     * @param selection The new selection state.
     */
    public void setSelectionState(SelectionType selection)
    {
        selectionState = selection;
        repaint();
    }

    /**
     * Called when a key is pressed in the editor.
     * 
     * @param event The related event.
     */
    void keyPressed(KeyEvent event)
    {
        final int hOffsetOld = hOffset;
        final int vOffsetOld = vOffset;
        switch (event.getKeyCode())
        {
            case KeyEvent.VK_LEFT:
                hOffset -= Editor.STEP_TILE_H;
                break;
            case KeyEvent.VK_RIGHT:
                hOffset += Editor.STEP_TILE_H;
                break;
            case KeyEvent.VK_UP:
                vOffset += Editor.STEP_TILE_V;
                break;
            case KeyEvent.VK_DOWN:
                vOffset -= Editor.STEP_TILE_V;
                break;
            default:
                break;
        }
        updateWorldLocation(hOffsetOld, vOffsetOld);
    }

    /**
     * Update and apply the world offset location.
     * 
     * @param hOffsetOld The old horizontal offset.
     * @param vOffsetOld The old vertical offset.
     */
    void updateWorldLocation(int hOffsetOld, int vOffsetOld)
    {
        final int tw = world.map.getTileWidth();
        final int th = world.map.getTileHeight();
        final int hOffsetMax = Math.max(world.map.getWidthInTile() - world.camera.getViewWidth() / tw - 1, 0);
        final int vOffsetMax = Math.max(world.map.getHeightInTile() - world.camera.getViewHeight() / th, 0);
        hOffset = UtilityMath.fixBetween(hOffset, 0, hOffsetMax);
        vOffset = UtilityMath.fixBetween(vOffset, 0, vOffsetMax);
        final int mh = hOffset - hOffsetOld;
        final int mv = vOffset - vOffsetOld;
        if (mh != 0 || mv != 0)
        {
            for (final Entity entity : world.handlerEntity.list())
            {
                if (entity.isSelected() && world.isClicking())
                {
                    entity.moveLocation(1.0, mh * tw, mv * th);
                }
            }
        }
        world.camera.moveLocation(1.0, mh * tw, mv * th);
        repaint();
    }

    /**
     * Initialize the editor content.
     */
    private void init()
    {
        addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent event)
            {
                terminate();
            }
        });
        addComponentListener(new ComponentAdapter()
        {
            @Override
            public void componentResized(ComponentEvent event)
            {
                final int areaX = UtilityMath.getRounded(world.getWidth(), world.map.getTileWidth());
                final int areaY = UtilityMath.getRounded(world.getHeight(), world.map.getTileHeight());
                world.camera.setView(0, 0, areaX, areaY);
                updateWorldLocation(getOffsetViewInTileH(), getOffsetViewInTileV());
            }
        });
        setLayout(new BorderLayout());
        add(menuBar, BorderLayout.NORTH);
        add(toolBar, BorderLayout.WEST);
        add(world, BorderLayout.CENTER);
        add(stateBar, BorderLayout.SOUTH);
        validate();
        repaint();
        setMinimumSize(new Dimension(640, 480));
        Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener()
        {
            @Override
            public void eventDispatched(AWTEvent event)
            {
                final KeyEvent key = (KeyEvent) event;
                if (key.getID() == KeyEvent.KEY_PRESSED)
                {
                    keyPressed(key);
                }
            }
        }, AWTEvent.KEY_EVENT_MASK);
    }
}
