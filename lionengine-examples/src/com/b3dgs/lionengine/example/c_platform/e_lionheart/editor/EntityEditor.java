package com.b3dgs.lionengine.example.c_platform.e_lionheart.editor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.Editor;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.Entity;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.patrol.Patrol;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.patrol.Patrollable;
import com.b3dgs.lionengine.swing.ActionCombo;
import com.b3dgs.lionengine.swing.ComboItem;
import com.b3dgs.lionengine.swing.ComboListener;
import com.b3dgs.lionengine.swing.ComboRenderer;
import com.b3dgs.lionengine.utility.UtilitySwing;

/**
 * Entity editor.
 */
public class EntityEditor
        extends JPanel
{
    /** Uid. */
    private static final long serialVersionUID = 103208218026146712L;

    /**
     * Add a combo menu.
     * 
     * @param name The combo name.
     * @param panel The panel owner.
     * @param tab The combo list.
     * @param action The action reference.
     * @return The combo instance.
     */
    public static JComboBox<ComboItem> addMenuCombo(String name, JPanel panel, ComboItem[] tab, ActionCombo action)
    {
        final JComboBox<ComboItem> combo = new JComboBox<>(tab);
        combo.setRenderer(new ComboRenderer<>());
        combo.addActionListener(new ComboListener<>(combo, action));
        if (name != null)
        {
            final JLabel label = new JLabel(name);
            label.setLabelFor(combo);
            final JPanel container = new JPanel();
            container.add(label);
            container.add(combo);
            panel.add(container);
        }
        else
        {
            panel.add(combo);
        }
        return combo;
    }

    /**
     * Set the combo enabled state.
     * 
     * @param combo The combo reference.
     * @param item The item index.
     * @param enabled The enabled state.
     */
    private static void setEnabled(JComboBox<ComboItem> combo, int item, boolean enabled)
    {
        combo.getItemAt(item).setEnabled(enabled);
    }

    /** Editor reference. */
    final Editor editor;
    /** Combo for movement type selection. */
    final JComboBox<ComboItem> comboMovement;
    /** Combo for direction type selection. */
    final JComboBox<ComboItem> comboDirection;
    /** Tabs. */
    private final JTabbedPane tabs;
    /** Player panel. */
    private final JPanel playerPanel;
    /** Patrol panel. */
    private final JPanel patrolPanel;
    /** Minimum patrol label. */
    private final JLabel patrolMin;
    /** Maximum patrol label. */
    private final JLabel patrolMax;
    /** Speed label. */
    private final JLabel speedValue;
    /** Selected entity. */
    Entity selectedEntity;

    /**
     * Constructor.
     * 
     * @param editor The editor reference.
     */
    public EntityEditor(final Editor editor)
    {
        super();
        this.editor = editor;
        setLayout(new BorderLayout());
        tabs = new JTabbedPane();
        add(tabs);

        playerPanel = new JPanel();
        playerPanel.setLayout(new BorderLayout());

        tabs.addTab("Player", playerPanel);
        final JPanel playerValues = new JPanel();
        playerValues.setLayout(new GridLayout(2, 1));
        final JPanel startEnd = UtilitySwing.createBorderedPanel("Starting/Ending Location", 1);
        startEnd.setLayout(new GridLayout(0, 2));
        UtilitySwing.addButton("Start", startEnd, new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent event)
            {
                editor.setSelectionState(SelectionType.PLAYER);
                editor.world.setPlayerSelection(SelectionPlayerType.PLACE_START);
            }
        });
        UtilitySwing.addButton("End", startEnd, new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent event)
            {
                editor.setSelectionState(SelectionType.PLAYER);
                editor.world.setPlayerSelection(SelectionPlayerType.PLACE_END);
            }
        });
        playerValues.add(startEnd, BorderLayout.CENTER);
        final JPanel checkpoint = UtilitySwing.createBorderedPanel("Checkpoint", 1);
        checkpoint.setLayout(new GridLayout(0, 2));
        UtilitySwing.addButton("Add", checkpoint, new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent event)
            {
                editor.setSelectionState(SelectionType.PLAYER);
                editor.world.setPlayerSelection(SelectionPlayerType.ADD_CHECKPOINT);
            }
        });
        UtilitySwing.addButton("Remove", checkpoint, new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent event)
            {
                editor.setSelectionState(SelectionType.PLAYER);
                editor.world.setPlayerSelection(SelectionPlayerType.REMOVE_CHECKPOINT);
            }
        });
        playerValues.add(checkpoint, BorderLayout.CENTER);
        playerPanel.add(playerValues, BorderLayout.CENTER);

        patrolPanel = new JPanel();
        patrolPanel.setLayout(new BorderLayout());
        tabs.addTab("Patrol", patrolPanel);
        final JPanel patrolValues = new JPanel();
        patrolValues.setLayout(new GridLayout(1, 3));
        patrolPanel.add(patrolValues, BorderLayout.CENTER);

        patrolMin = new JLabel();
        addIncDec(patrolValues, "Left", patrolMin, 1, TypeEditorEntity.PATROL_MIN);

        patrolMax = new JLabel();
        addIncDec(patrolValues, "Right", patrolMax, 1, TypeEditorEntity.PATROL_MAX);

        speedValue = new JLabel();
        addIncDec(patrolValues, "Speed", speedValue, 1, TypeEditorEntity.SPEED);

        final JPanel combos = new JPanel();
        combos.setLayout(new GridLayout(2, 2));
        patrolPanel.add(combos, BorderLayout.SOUTH);

        final String[] directions =
        {
                "None", "Horizontal", "Vertical", "Turning"
        };
        final ComboItem[] dirs = new ComboItem[directions.length];
        for (int i = 0; i < directions.length; i++)
        {
            dirs[i] = new ComboItem(directions[i]);
        }

        comboMovement = EntityEditor.addMenuCombo("Movement:", combos, dirs, new ActionCombo()
        {
            @Override
            public void action(Object item)
            {
                if (selectedEntity instanceof Patrollable)
                {
                    final Patrol type = Patrol.get(comboMovement.getSelectedIndex());
                    ((Patrollable) selectedEntity).setPatrolType(type);
                    comboDirection.setEnabled(type != Patrol.NONE);
                    UtilitySwing.setEnabled(patrolValues.getComponents(), type != Patrol.NONE);
                    editor.world.repaint();
                }
            }
        });

        final String[] firstMove =
        {
                "Go Left / Go Up", "Go Right / Go Down", "Random"
        };
        final ComboItem[] fm = new ComboItem[firstMove.length];
        for (int i = 0; i < firstMove.length; i++)
        {
            fm[i] = new ComboItem(firstMove[i]);
        }

        comboDirection = EntityEditor.addMenuCombo("Direction:", combos, fm, new ActionCombo()
        {
            @Override
            public void action(Object item)
            {
                if (selectedEntity instanceof Patrollable)
                {
                    ((Patrollable) selectedEntity).setFirstMove(comboDirection.getSelectedIndex());
                }
            }
        });

        UtilitySwing.setEnabled(patrolPanel.getComponents(), false);
        tabs.setPreferredSize(new Dimension(204, 200));
        tabs.setMinimumSize(new Dimension(204, 200));
        tabs.setMaximumSize(new Dimension(204, 200));
    }

    /**
     * Set the selected entity.
     * 
     * @param entity The selected entity.
     */
    public void setSelectedEntity(Entity entity)
    {
        selectedEntity = entity;
        if (entity instanceof Patrollable)
        {
            final Patrollable mover = (Patrollable) entity;
            for (final Patrol movement : Patrol.values())
            {
                final boolean enabled = mover.isPatrolEnabled(movement);
                EntityEditor.setEnabled(comboMovement, movement.getIndex(), enabled);
            }
            UtilitySwing.setEnabled(patrolPanel.getComponents(), true);
            setValue(patrolMin, mover.getPatrolLeft());
            setValue(patrolMax, mover.getPatrolRight());
            setValue(speedValue, mover.getMoveSpeed());
            comboMovement.setSelectedIndex(mover.getPatrolType().getIndex());
            comboDirection.setSelectedIndex(mover.getFirstMove());
        }
        else
        {
            setPatrolPanelEnabled(false);
        }
        editor.repaint();
    }

    /**
     * Set the patrol panel enabled state.
     * 
     * @param state <code>true</code> if enabled, <code>false</code> else.
     */
    public void setPatrolPanelEnabled(boolean state)
    {
        if (!state)
        {
            patrolMin.setText(null);
            patrolMax.setText(null);
            speedValue.setText(null);
            comboMovement.setSelectedIndex(0);
        }
        UtilitySwing.setEnabled(patrolPanel.getComponents(), state);
    }

    /**
     * Change the combo value.
     * 
     * @param label The label reference.
     * @param type The type.
     * @param mover The patroller.
     */
    void changeMoverValue(JLabel label, TypeEditorEntity type, Patrollable mover)
    {
        switch (type)
        {
            case PATROL_MIN:
                mover.setPatrolLeft(getValue(label));
                break;
            case PATROL_MAX:
                mover.setPatrolRight(getValue(label));
                break;
            case SPEED:
                mover.setMoveSpeed(getValue(label));
                break;
            default:
                throw new LionEngineException("Unknown type: " + type);
        }
    }

    /**
     * Get the value of the label.
     * 
     * @param label The label reference.
     * @return The value.
     */
    int getValue(JLabel label)
    {
        return Integer.parseInt(label.getText());
    }

    /**
     * Set the label value.
     * 
     * @param label The label reference.
     * @param value The value.
     */
    void setValue(JLabel label, int value)
    {
        int v = value;
        if (v < 0)
        {
            v = 0;
        }
        label.setText(String.valueOf(v));
    }

    /**
     * Add a JPanel designed to increase/decrease a value.
     * 
     * @param parent The panel owner.
     * @param name The panel name.
     * @param label The label reference.
     * @param step The step value.
     * @param type The type.
     * @return The panel instance.
     */
    private JPanel addIncDec(JPanel parent, String name, final JLabel label, final int step, final TypeEditorEntity type)
    {
        final JPanel incdec = UtilitySwing.createBorderedPanel(name, 1);
        incdec.setLayout(new GridLayout(3, 1));
        if (parent != null)
        {
            parent.add(incdec);
        }
        UtilitySwing.addButton("+", incdec, new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent event)
            {
                if (selectedEntity instanceof Patrollable)
                {
                    setValue(label, getValue(label) + step);
                    changeMoverValue(label, type, (Patrollable) selectedEntity);
                    editor.world.repaint();
                }
            }
        });
        UtilitySwing.addButton("-", incdec, new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent event)
            {
                if (selectedEntity instanceof Patrollable)
                {
                    setValue(label, getValue(label) - step);
                    changeMoverValue(label, type, (Patrollable) selectedEntity);
                    editor.world.repaint();
                }
            }
        });
        label.setHorizontalAlignment(SwingConstants.CENTER);
        incdec.add(label);

        return incdec;
    }
}
