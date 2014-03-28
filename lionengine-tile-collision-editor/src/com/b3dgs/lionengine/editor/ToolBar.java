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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.ScrollPaneConstants;

import com.b3dgs.lionengine.game.map.MapTileGame;
import com.b3dgs.lionengine.game.platform.CollisionFunction;
import com.b3dgs.lionengine.game.platform.map.TilePlatform;
import com.b3dgs.lionengine.swing.UtilitySwing;

/**
 * Tool bar implementation.
 * 
 * @param <C> The collision type used.
 * @param <T> The tile type used.
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class ToolBar<C extends Enum<C>, T extends TilePlatform<C>>
        extends JToolBar
{
    /** Uid. */
    private static final long serialVersionUID = -3884748128028563357L;

    /** Collision combo. */
    final JComboBox<C> collisionCombo;
    /** Palette panel. */
    final JPanel palettePanel;
    /** Formula panel. */
    final JScrollPane formulaScrollPane;
    /** Formula panel. */
    final JPanel formulaPanel;
    /** Editor reference. */
    private final TileCollisionEditor<C, T> editor;
    /** Collision class used. */
    private final Class<C> collisionClass;
    /** Collision type choice. */
    private final JPanel collisionTypeChoice;

    /**
     * Constructor.
     * 
     * @param collisionClass The collision class.
     * @param editor The editor reference.
     * @param collisions The collisions list.
     */
    public ToolBar(TileCollisionEditor<C, T> editor, Class<C> collisionClass, C[] collisions)
    {
        super();
        this.editor = editor;
        this.collisionClass = collisionClass;

        palettePanel = new JPanel();
        palettePanel.setLayout(new BoxLayout(palettePanel, BoxLayout.PAGE_AXIS));

        collisionCombo = new JComboBox<>(collisions);
        collisionTypeChoice = createCollisionTypeChoice(editor, collisionClass, collisions);
        palettePanel.add(collisionTypeChoice);

        UtilitySwing.addButton("Add formula", palettePanel, new CreateFormulaListener(editor, collisionClass))
                .setAlignmentX(Component.CENTER_ALIGNMENT);

        formulaPanel = new JPanel();
        formulaPanel.setLayout(new GridBagLayout());
        formulaScrollPane = new JScrollPane(formulaPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        formulaScrollPane.setPreferredSize(new Dimension(200, 300));
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
            collisionCombo.setSelectedItem(tile.getCollision());
            collisionCombo.setEnabled(true);

            for (final CollisionFunction function : tile.getCollisionFunctions())
            {
                final CollisionFunctionPanel<C, T> panel = new CollisionFunctionPanel<>(editor, collisionClass,
                        function.getName());
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
    void addToFormulaPanel(CollisionFunctionPanel<C, T> panel)
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
    void removeCollisionFunction(CollisionFunctionPanel<C, T> panel)
    {
        formulaPanel.remove(panel);
        updateUI();
    }

    /**
     * Create the collision type choice.
     * 
     * @param editor The editor reference.
     * @param collisionClass The collision class.
     * @param collisions The collisions list.
     * @return The created panel.
     */
    private JPanel createCollisionTypeChoice(TileCollisionEditor<C, T> editor, Class<C> collisionClass, C[] collisions)
    {
        final JPanel panel = UtilitySwing.createBorderedPanel("Collision", 1);
        panel.setLayout(new GridLayout(2, 1));

        panel.add(collisionCombo);
        collisionCombo.setEnabled(false);

        final JButton assignLabel = new JButton("Assign");
        assignLabel.addActionListener(new AssignCollisionListener(editor.world, collisionClass));
        panel.add(assignLabel);

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
        private final WorldPanel<C, T> world;
        /** Collision class. */
        private final Class<C> collisionClass;

        /**
         * Constructor.
         * 
         * @param world The world reference.
         * @param collisionClass The collision class reference.
         */
        AssignCollisionListener(WorldPanel<C, T> world, Class<C> collisionClass)
        {
            this.world = world;
            this.collisionClass = collisionClass;
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent)
        {
            final T tile = world.getSelectedTile();
            if (tile != null)
            {
                final Object selection = collisionCombo.getSelectedItem();
                if (selection != null && selection.getClass().isAssignableFrom(collisionClass))
                {
                    final C collision = collisionClass.cast(selection);
                    final Set<CollisionFunction> functions = world.map.searchCollisionFunctions(collision);
                    if (functions != null)
                    {
                        tile.setCollision(collision);

                        final Integer pattern = tile.getPattern();
                        final int number = tile.getNumber();
                        final MapTileGame<C, T> map = world.map;
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
                        for (final CollisionFunction function : functions)
                        {
                            world.map.assignCollisionFunction(collision, function);
                        }
                        world.repaint();
                        setSelectedTile(tile);
                    }
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
        private final TileCollisionEditor<C, T> editor;
        /** The collision class. */
        private final Class<C> collisionClass;

        /**
         * Create the formula creation listener.
         * 
         * @param editor The editor reference.
         * @param collisionClass The collision class.
         */
        CreateFormulaListener(TileCollisionEditor<C, T> editor, Class<C> collisionClass)
        {
            this.editor = editor;
            this.collisionClass = collisionClass;
        }

        @Override
        public void actionPerformed(ActionEvent event)
        {
            final String name = JOptionPane.showInputDialog("Enter the name:");
            final CollisionFunctionPanel<C, T> panel = new CollisionFunctionPanel<>(editor, collisionClass, name);
            addToFormulaPanel(panel);

            final CollisionFunction function = new CollisionFunction();
            function.setName(name);
            panel.setSelectedFunction(function);
            editor.world.map.assignCollisionFunction((C) collisionCombo.getSelectedItem(), function);
            editor.world.map.createCollisionDraw(collisionClass);
            editor.world.repaint();

            updateUI();
        }
    }
}
