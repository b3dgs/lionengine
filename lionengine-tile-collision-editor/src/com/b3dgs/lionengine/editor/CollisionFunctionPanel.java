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

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.b3dgs.lionengine.game.map.MapTileGame;
import com.b3dgs.lionengine.game.platform.CollisionFunction;
import com.b3dgs.lionengine.game.platform.CollisionInput;
import com.b3dgs.lionengine.game.platform.map.TilePlatform;
import com.b3dgs.lionengine.swing.UtilitySwing;

/**
 * Represents the collision function panel control.
 * 
 * @param <C> The collision type used.
 * @param <T> The tile type used.
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class CollisionFunctionPanel<C extends Enum<C>, T extends TilePlatform<C>>
        extends JPanel
{
    /** Serial UID. */
    private static final long serialVersionUID = 1L;

    /** Collision class. */
    final Class<C> collisionClass;
    /** Parameter combo. */
    final JComboBox<CollisionInput> inputCombo;
    /** Value field. */
    final JTextField valueField;
    /** Value offset field. */
    final JTextField valueOffsetField;
    /** Selected function. */
    CollisionFunction selectedFunction;

    /**
     * Constructor.
     * 
     * @param editor The editor reference.
     * @param collisionClass The collision class.
     * @param title The formula title.
     */
    public CollisionFunctionPanel(TileCollisionEditor<C, T> editor, Class<C> collisionClass, String title)
    {
        this.collisionClass = collisionClass;
        inputCombo = new JComboBox<>(CollisionInput.values());
        valueField = new JTextField("1");
        valueOffsetField = new JTextField("0");
        createFormulaHandler(editor, title);
    }

    /**
     * Set the selected tile.
     * 
     * @param function The selected function.
     */
    public void setSelectedFunction(CollisionFunction function)
    {
        if (function != null)
        {
            inputCombo.setSelectedItem(function.getInput());
            valueField.setText(String.valueOf(function.getValue()));
            valueOffsetField.setText(String.valueOf(function.getOffset()));
            UtilitySwing.setEnabled(getComponents(), true);
        }
        else
        {
            UtilitySwing.setEnabled(getComponents(), false);
        }
        selectedFunction = function;
    }

    /**
     * Create the collision type choice.
     * 
     * @param editor The editor reference.
     * @param title The formula title.
     */
    private void createFormulaHandler(TileCollisionEditor<C, T> editor, String title)
    {
        UtilitySwing.setBorderedPanel(this, "Formula: " + title, 1);
        final JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 1));
        add(panel);

        final JPanel formulaPanel = new JPanel();
        formulaPanel.setLayout(new GridLayout(1, 5));
        panel.add(formulaPanel);

        final JButton applyLabel = new JButton("Apply");
        applyLabel.addActionListener(new AssignFormulaListener(editor.world));
        final JButton deleteLabel = new JButton("Delete");
        deleteLabel.addActionListener(new DeleteFormulaListener(editor, this));

        formulaPanel.add(inputCombo);
        formulaPanel.add(new JLabel("*", null, SwingConstants.CENTER));
        formulaPanel.add(valueField);
        formulaPanel.add(new JLabel("+", null, SwingConstants.CENTER));
        formulaPanel.add(valueOffsetField);

        final JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 2));
        buttonPanel.add(applyLabel);
        buttonPanel.add(deleteLabel);
        panel.add(buttonPanel);
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
                final double value = Double.parseDouble(valueField.getText());
                final int offset = Integer.parseInt(valueOffsetField.getText());

                selectedFunction.setInput(input);
                selectedFunction.setValue(value);
                selectedFunction.setOffset(offset);

                tile.addCollisionFunction(selectedFunction);

                final MapTileGame<C, T> map = world.map;
                for (int ty = 0; ty < map.getHeightInTile(); ty++)
                {
                    for (int tx = 0; tx < map.getWidthInTile(); tx++)
                    {
                        final T next = map.getTile(tx, ty);
                        if (next != null && next.getCollision() == tile.getCollision())
                        {
                            next.addCollisionFunction(selectedFunction);
                        }
                    }
                }
                world.map.createCollisionDraw(collisionClass);
                world.repaint();
            }
        }
    }

    /**
     * Delete the formula listener.
     */
    private final class DeleteFormulaListener
            implements ActionListener
    {
        /** Editor reference. */
        private final TileCollisionEditor<C, T> editor;
        /** Panel reference. */
        private final CollisionFunctionPanel<C, T> panel;

        /**
         * Constructor.
         * 
         * @param editor The editor reference.
         * @param panel The panel reference.
         */
        DeleteFormulaListener(TileCollisionEditor<C, T> editor, CollisionFunctionPanel<C, T> panel)
        {
            this.editor = editor;
            this.panel = panel;
        }

        @Override
        public void actionPerformed(ActionEvent event)
        {
            editor.world.map.removeCollisionFunction(panel.selectedFunction);
            editor.toolBar.removeCollisionFunction(panel);
            editor.world.map.createCollisionDraw(collisionClass);
            editor.world.repaint();
        }
    }
}
