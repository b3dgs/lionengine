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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.ScrollPaneConstants;

import com.b3dgs.lionengine.game.map.CollisionFunction;
import com.b3dgs.lionengine.game.map.CollisionTile;
import com.b3dgs.lionengine.game.map.TileGame;
import com.b3dgs.lionengine.swing.UtilitySwing;

/**
 * Tool bar implementation.
 * 
 * @param <T> The tile type used.
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class ToolBar<T extends TileGame>
        extends JToolBar
{
    /** Uid. */
    private static final long serialVersionUID = -3884748128028563357L;

    /** Collision combo. */
    final JComboBox<CollisionTile> collisionCombo;
    /** Palette panel. */
    final JPanel palettePanel;
    /** Formula panel. */
    final JScrollPane formulaScrollPane;
    /** Formula panel. */
    final JPanel formulaPanel;
    /** Editor reference. */
    private final TileCollisionEditor<T> editor;
    /** Collision type choice. */
    private final JPanel collisionTypeChoice;

    /**
     * Constructor.
     * 
     * @param editor The editor reference.
     * @param collisions The collisions list.
     */
    public ToolBar(TileCollisionEditor<T> editor, CollisionTile[] collisions)
    {
        super();
        this.editor = editor;

        palettePanel = new JPanel();
        palettePanel.setLayout(new BoxLayout(palettePanel, BoxLayout.PAGE_AXIS));

        collisionCombo = new JComboBox<>(collisions);
        collisionTypeChoice = createCollisionTypeChoice(editor, collisions);
        palettePanel.add(collisionTypeChoice);

        formulaPanel = new JPanel();
        formulaPanel.setLayout(new GridBagLayout());
        formulaScrollPane = new JScrollPane(formulaPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        formulaScrollPane.setPreferredSize(new Dimension(200, 405));
        palettePanel.add(formulaScrollPane);

        setSelectedTile(null);

        init();
    }

    /**
     * Set the selected tile.
     * 
     * @param tile The selected tile.
     */
    public void setSelectedTile(T tile)
    {
        formulaPanel.removeAll();
        if (tile != null)
        {
            UtilitySwing.setEnabled(collisionTypeChoice.getComponents(), true);
            final CollisionTile collision = tile.getCollision();
            collisionCombo.setSelectedItem(collision);
            collisionCombo.setEnabled(true);

            for (final CollisionFunction function : collision.getCollisionFunctions())
            {
                final CollisionFunctionPanel<T> panel = new CollisionFunctionPanel<>(editor, function.getName());
                panel.setSelectedFunction(function);
                addToFormulaPanel(panel);
            }
        }
        else
        {
            collisionCombo.setEnabled(false);
            UtilitySwing.setEnabled(collisionTypeChoice.getComponents(), false);
        }
        updateUI();
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
     * Add the collision function panel to the formula panel.
     * 
     * @param panel The panel to add.
     */
    void addToFormulaPanel(CollisionFunctionPanel<T> panel)
    {
        final GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        formulaPanel.add(panel, gbc);
    }

    /**
     * Remove a collision function.
     * 
     * @param panel The function to remove.
     */
    void removeCollisionFunction(CollisionFunctionPanel<T> panel)
    {
        formulaPanel.remove(panel);
        updateUI();
    }

    /**
     * Create the collision type choice.
     * 
     * @param editor The editor reference.
     * @param collisions The collisions list.
     * @return The created panel.
     */
    private JPanel createCollisionTypeChoice(TileCollisionEditor<T> editor, CollisionTile[] collisions)
    {
        final JPanel panel = UtilitySwing.createBorderedPanel("Collision", 1);
        panel.setLayout(new GridLayout(2, 1));

        panel.add(collisionCombo);
        collisionCombo.setEnabled(false);

        final JPanel buttons = new JPanel(new BorderLayout());
        final JButton assignLabel = new JButton("Assign");
        assignLabel.addActionListener(new AssignCollisionListener(editor.world));
        buttons.add(assignLabel, BorderLayout.WEST);

        UtilitySwing.addButton("Add formula", buttons, new CreateFormulaListener(editor));
        panel.add(buttons, BorderLayout.EAST);

        return panel;
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

    /**
     * Assign the collision listener.
     */
    private final class AssignCollisionListener
            implements ActionListener
    {
        /** The world panel reference. */
        private final WorldPanel<T> world;

        /**
         * Constructor.
         * 
         * @param world The world reference.
         */
        AssignCollisionListener(WorldPanel<T> world)
        {
            this.world = world;
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent)
        {
            final T tile = world.getSelectedTile();
            if (tile != null)
            {
                final Object selection = collisionCombo.getSelectedItem();
                if (selection != null && selection.getClass().isAssignableFrom(CollisionTile.class))
                {
                    final CollisionTile collision = CollisionTile.class.cast(selection);
                    tile.setCollision(collision);
                    world.repaint();
                    setSelectedTile(tile);
                }
            }
        }
    }

    /**
     * Create the formula listener.
     */
    private final class CreateFormulaListener
            implements ActionListener
    {
        /** The editor panel reference. */
        private final TileCollisionEditor<T> editor;

        /**
         * Create the formula creation listener.
         * 
         * @param editor The editor reference.
         */
        CreateFormulaListener(TileCollisionEditor<T> editor)
        {
            this.editor = editor;
        }

        @Override
        public void actionPerformed(ActionEvent event)
        {
            final String name = JOptionPane.showInputDialog("Enter the name:");
            final CollisionFunctionPanel<T> panel = new CollisionFunctionPanel<>(editor, name);
            addToFormulaPanel(panel);

            final CollisionFunction function = new CollisionFunction();
            function.setName(name);
            function.setRange(0, editor.world.map.getTileWidth() - 1);
            panel.setSelectedFunction(function);
            editor.world.map.assignCollisionFunction(CollisionTile.class.cast(collisionCombo.getSelectedItem()),
                    function);
            editor.world.map.createCollisionDraw();
            editor.world.repaint();

            updateUI();
        }
    }
}
