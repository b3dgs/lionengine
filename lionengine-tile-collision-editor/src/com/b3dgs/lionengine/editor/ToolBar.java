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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import com.b3dgs.lionengine.game.map.MapTileGame;
import com.b3dgs.lionengine.game.map.TileGame;
import com.b3dgs.lionengine.swing.UtilitySwing;

/**
 * Tool bar implementation.
 * 
 * @param <C> The collision type used.
 * @param <T> The tile type used.
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class ToolBar<C extends Enum<C>, T extends TileGame<C>>
        extends JToolBar
{
    /** Uid. */
    private static final long serialVersionUID = -3884748128028563357L;

    /**
     * Create the palette panel.
     * 
     * @return The panel instance.
     */
    private static JPanel createPalettePanel()
    {
        final JPanel palettePanel = UtilitySwing.createBorderedPanel("Collisions", 1);
        palettePanel.setLayout(new GridLayout(3, 1));

        final JLabel comboLabel = new JLabel("Choice");
        palettePanel.add(comboLabel);

        return palettePanel;
    }

    /** Collision combo. */
    final JComboBox<C> collisionCombo;
    /** Palette panel. */
    private final JPanel palettePanel;

    /**
     * Constructor.
     * 
     * @param collisions The collisions list.
     * @param collisionClass The collision class.
     * @param editor The editor reference.
     */
    public ToolBar(final TileCollisionEditor<C, T> editor, final Class<C> collisionClass, C[] collisions)
    {
        super();
        palettePanel = ToolBar.createPalettePanel();

        collisionCombo = new JComboBox<>(collisions);
        palettePanel.add(collisionCombo);
        collisionCombo.setEnabled(false);

        final JButton assignLabel = new JButton("Assign");
        assignLabel.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent actionEvent)
            {
                final T tile = editor.world.getSelectedTile();
                if (tile != null)
                {
                    final Object selection = collisionCombo.getSelectedItem();
                    if (selection.getClass().isAssignableFrom(collisionClass))
                    {
                        final C collision = collisionClass.cast(selection);
                        tile.setCollision(collision);

                        final Integer pattern = tile.getPattern();
                        final int number = tile.getNumber();
                        final MapTileGame<C, T> map = editor.world.map;
                        for (int ty = 0; ty < map.getHeightInTile(); ty++)
                        {
                            for (int tx = 0; tx < map.getWidthInTile(); tx++)
                            {
                                final T next = map.getTile(tx, ty);
                                if (next != null && next.getPattern().equals(pattern) && next.getNumber() == number)
                                {
                                    next.setCollision(collision);
                                }
                            }
                        }
                        editor.world.repaint();
                    }
                }
            }
        });
        palettePanel.add(assignLabel);

        init();
    }

    /**
     * Set the selected tile.
     * 
     * @param tile The selected tile.
     */
    public void setSelectedTile(T tile)
    {
        if (tile != null)
        {
            collisionCombo.setSelectedItem(tile.getCollision());
            collisionCombo.setEnabled(true);
        }
        else
        {
            collisionCombo.setEnabled(false);
        }
    }

    /**
     * Set the palette panel enabled state.
     * 
     * @param enabled <code>true</code> to enable, <code>false</code> else.
     */
    public void setPaletteEnabled(boolean enabled)
    {
        UtilitySwing.setEnabled(palettePanel.getComponents(), enabled);
    }

    /**
     * Initialize the tool bar content.
     */
    private void init()
    {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        final JPanel entityPanel = new JPanel();
        entityPanel.setLayout(new BorderLayout());

        final JPanel editPanel = new JPanel();
        editPanel.setLayout(new BorderLayout());

        add(entityPanel);
        add(editPanel);

        entityPanel.add(palettePanel, BorderLayout.NORTH);

        setPreferredSize(new Dimension(204, 480));
        setMinimumSize(new Dimension(204, 480));
    }
}
