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
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.b3dgs.lionengine.game.map.CollisionFunction;
import com.b3dgs.lionengine.game.map.CollisionRefential;
import com.b3dgs.lionengine.game.map.CollisionTile;
import com.b3dgs.lionengine.game.map.TileGame;
import com.b3dgs.lionengine.swing.UtilitySwing;

/**
 * Represents the collision function panel control.
 * 
 * @param <C> The collision type used.
 * @param <T> The tile type used.
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class CollisionFunctionPanel<C extends Enum<C> & CollisionTile, T extends TileGame<C>>
        extends JPanel
{
    /** Serial UID. */
    private static final long serialVersionUID = 1L;

    /** Collision class. */
    final Class<C> collisionClass;
    /** Axis combo. */
    final JComboBox<CollisionRefential> axisCombo;
    /** Input combo. */
    final JComboBox<CollisionRefential> inputCombo;
    /** Value field. */
    final JTextField valueField;
    /** Value offset field. */
    final JTextField valueOffsetField;
    /** Range min. */
    final JTextField rangeMinField;
    /** Range max. */
    final JTextField rangeMaxField;
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
        axisCombo = new JComboBox<>(CollisionRefential.values());
        inputCombo = new JComboBox<>(CollisionRefential.values());
        valueField = new JTextField("1");
        valueOffsetField = new JTextField("0");
        rangeMinField = new JTextField("0");
        rangeMaxField = new JTextField("0");
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
            axisCombo.setSelectedItem(function.getAxis());
            inputCombo.setSelectedItem(function.getInput());
            valueField.setText(String.valueOf(function.getValue()));
            valueOffsetField.setText(String.valueOf(function.getOffset()));
            rangeMinField.setText(String.valueOf(function.getRange().getMin()));
            rangeMaxField.setText(String.valueOf(function.getRange().getMax()));
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
        panel.setLayout(new BorderLayout());
        add(panel);

        final JPanel referentialPanel = new JPanel();
        final JLabel referentialLabel = new JLabel("Axis: ");
        referentialPanel.add(referentialLabel);
        referentialPanel.add(axisCombo);
        panel.add(referentialPanel, BorderLayout.NORTH);

        final JPanel formulaPanel = new JPanel();
        formulaPanel.setLayout(new GridLayout(2, 5));
        panel.add(formulaPanel, BorderLayout.CENTER);

        final JButton applyLabel = new JButton("Apply");
        applyLabel.addActionListener(new AssignFormulaListener(editor.world));
        final JButton deleteLabel = new JButton("Delete");
        deleteLabel.addActionListener(new DeleteFormulaListener(editor, this));

        formulaPanel.add(inputCombo);
        formulaPanel.add(new JLabel("*", null, SwingConstants.CENTER));
        formulaPanel.add(valueField);
        formulaPanel.add(new JLabel("+", null, SwingConstants.CENTER));
        formulaPanel.add(valueOffsetField);

        formulaPanel.add(new JLabel("range", null, SwingConstants.CENTER));
        formulaPanel.add(new JLabel("min", null, SwingConstants.CENTER));
        formulaPanel.add(rangeMinField);
        formulaPanel.add(new JLabel("max", null, SwingConstants.CENTER));
        formulaPanel.add(rangeMaxField);

        final JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 2));
        buttonPanel.add(applyLabel);
        buttonPanel.add(deleteLabel);
        panel.add(buttonPanel, BorderLayout.SOUTH);
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
                final CollisionRefential axis = (CollisionRefential) axisCombo.getSelectedItem();
                final CollisionRefential input = (CollisionRefential) inputCombo.getSelectedItem();
                final double value = Double.parseDouble(valueField.getText());
                final int offset = Integer.parseInt(valueOffsetField.getText());
                final int rangeMin = Integer.parseInt(rangeMinField.getText());
                final int rangeMax = Integer.parseInt(rangeMaxField.getText());

                selectedFunction.setAxis(axis);
                selectedFunction.setInput(input);
                selectedFunction.setValue(value);
                selectedFunction.setOffset(offset);
                selectedFunction.setRange(rangeMin, rangeMax);

                tile.getCollision().addCollisionFunction(selectedFunction);

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
            editor.world.map.removeCollisionFunction(collisionClass, panel.selectedFunction);
            editor.toolBar.removeCollisionFunction(panel);
            editor.world.map.createCollisionDraw(collisionClass);
            editor.world.repaint();
        }
    }
}
