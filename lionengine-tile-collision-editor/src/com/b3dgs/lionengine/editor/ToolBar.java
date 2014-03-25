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
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import com.b3dgs.lionengine.game.map.MapTileGame;
import com.b3dgs.lionengine.game.platform.CollisionFunction;
import com.b3dgs.lionengine.game.platform.CollisionInput;
import com.b3dgs.lionengine.game.platform.CollisionOperation;
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
    /** Parameter combo. */
    final JComboBox<CollisionInput> inputCombo;
    /** Operation combo. */
    final JComboBox<CollisionOperation> operationCombo;
    /** Value field. */
    final JTextField valueField;
    /** Operation offset combo. */
    final JComboBox<CollisionOperation> operationOffsetCombo;
    /** Value offset field. */
    final JTextField valueOffsetField;
    /** Palette panel. */
    private final JPanel palettePanel;
    /** Collision type choice. */
    private final JPanel collisionTypeChoice;
    /** Formula handler. */
    private final JPanel formulaHandler;

    /**
     * Constructor.
     * 
     * @param collisions The collisions list.
     * @param collisionClass The collision class.
     * @param editor The editor reference.
     */
    public ToolBar(TileCollisionEditor<C, T> editor, Class<C> collisionClass, C[] collisions)
    {
        super();
        palettePanel = new JPanel();
        palettePanel.setLayout(new GridLayout(2, 1));

        collisionCombo = new JComboBox<>(collisions);
        collisionTypeChoice = createCollisionTypeChoice(editor, collisionClass, collisions);
        palettePanel.add(collisionTypeChoice);

        inputCombo = new JComboBox<>(CollisionInput.values());
        operationCombo = new JComboBox<>(CollisionOperation.values());
        valueField = new JTextField("1");
        operationOffsetCombo = new JComboBox<>(CollisionOperation.values());
        valueOffsetField = new JTextField("0");
        formulaHandler = createFormulaHandler(editor, collisionClass);
        palettePanel.add(formulaHandler);

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
        if (tile != null)
        {
            UtilitySwing.setEnabled(collisionTypeChoice.getComponents(), true);
            collisionCombo.setSelectedItem(tile.getCollision());
            collisionCombo.setEnabled(true);

            UtilitySwing.setEnabled(formulaHandler.getComponents(), true);
            final CollisionFunction function = tile.getCollisionFunction();
            inputCombo.setSelectedItem(function.getInput());
            operationCombo.setSelectedItem(function.getOperation());
            valueField.setText(String.valueOf(function.getValue()));
            operationOffsetCombo.setSelectedItem(function.getOperationOffset());
            valueOffsetField.setText(String.valueOf(function.getOffset()));
        }
        else
        {
            collisionCombo.setEnabled(false);
            UtilitySwing.setEnabled(collisionTypeChoice.getComponents(), false);
            UtilitySwing.setEnabled(formulaHandler.getComponents(), false);
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
     * Create the collision type choice.
     * 
     * @param editor The editor reference.
     * @param collisionClass The collision class.
     * @param collisions The collisions list.
     * @return The created panel.
     */
    private JPanel createCollisionTypeChoice(TileCollisionEditor<C, T> editor, Class<C> collisionClass, C[] collisions)
    {
        final JPanel panel = UtilitySwing.createBorderedPanel("Choice", 1);
        panel.setLayout(new GridLayout(2, 1));

        panel.add(collisionCombo);
        collisionCombo.setEnabled(false);

        final JButton assignLabel = new JButton("Assign");
        assignLabel.addActionListener(new AssignCollisionListener(editor.world, collisionClass));
        panel.add(assignLabel);

        return panel;
    }

    /**
     * Create the collision type choice.
     * 
     * @param editor The editor reference.
     * @param collisionClass The collision class.
     * @return The created panel.
     */
    private JPanel createFormulaHandler(TileCollisionEditor<C, T> editor, Class<C> collisionClass)
    {
        final JPanel panel = UtilitySwing.createBorderedPanel("Formula", 1);
        panel.setLayout(new GridLayout(2, 1));

        final JPanel formulaPanel = new JPanel();
        formulaPanel.setLayout(new GridLayout(1, 5));
        panel.add(formulaPanel);

        final JButton applyLabel = new JButton("Apply");
        applyLabel.addActionListener(new AssignFormulaListener(editor.world));

        formulaPanel.add(inputCombo);
        formulaPanel.add(operationCombo);
        formulaPanel.add(valueField);
        formulaPanel.add(operationOffsetCombo);
        formulaPanel.add(valueOffsetField);
        panel.add(applyLabel);

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
                    world.repaint();
                }
            }
        }
    }

    /**
     * Assign the formula listener.
     */
    private final class AssignFormulaListener
            implements ActionListener
    {
        /** The world panel reference. */
        private final WorldPanel<C, T> world;

        /**
         * Constructor.
         * 
         * @param world The world reference.
         */
        AssignFormulaListener(WorldPanel<C, T> world)
        {
            this.world = world;
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent)
        {
            final T tile = world.getSelectedTile();
            if (tile != null)
            {
                final CollisionInput input = (CollisionInput) inputCombo.getSelectedItem();
                final CollisionOperation operation = (CollisionOperation) operationCombo.getSelectedItem();
                final int value = Integer.parseInt(valueField.getText());
                final CollisionOperation operationOffset = (CollisionOperation) operationOffsetCombo.getSelectedItem();
                final int offset = Integer.parseInt(valueOffsetField.getText());

                final CollisionFunction function = new CollisionFunction();
                function.setInput(input);
                function.setOperation(operation);
                function.setValue(value);
                function.setOperationOffset(operationOffset);
                function.setOffset(offset);

                tile.setCollisionFunction(function);

                final MapTileGame<C, T> map = world.map;
                for (int ty = 0; ty < map.getHeightInTile(); ty++)
                {
                    for (int tx = 0; tx < map.getWidthInTile(); tx++)
                    {
                        final T next = map.getTile(tx, ty);
                        if (next != null && next.getCollision() == tile.getCollision())
                        {
                            next.setCollisionFunction(function);
                        }
                    }
                }
                world.repaint();
            }
        }
    }
}
