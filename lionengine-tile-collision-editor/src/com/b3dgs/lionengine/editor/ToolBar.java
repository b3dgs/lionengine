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

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;

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
     * @param collisions The collisions list.
     * @return The panel instance.
     */
    private static JPanel createPalettePanel(Object[] collisions)
    {
        final JPanel palettePanel = UtilitySwing.createBorderedPanel("Collisions", 1);
        palettePanel.setLayout(new GridLayout(2, 1));

        final JLabel comboLabel = new JLabel("Choice");
        palettePanel.add(comboLabel);

        final JComboBox<Object> combo = new JComboBox<>(collisions);
        combo.setEnabled(true);
        palettePanel.add(combo);

        return palettePanel;
    }

    /** Palette panel. */
    private final JPanel palettePanel;

    /**
     * Constructor.
     * 
     * @param collisions The collisions list.
     * @param editor The editor reference.
     */
    public ToolBar(final TileCollisionEditor<C, T> editor, C[] collisions)
    {
        super();
        palettePanel = ToolBar.createPalettePanel(collisions);
        setPaletteEnabled(true);
        init();
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
