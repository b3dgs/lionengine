package com.b3dgs.lionengine.example.c_platform.e_lionheart.editor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import com.b3dgs.lionengine.example.c_platform.e_lionheart.Editor;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.Entity;
import com.b3dgs.lionengine.swing.ActionCombo;
import com.b3dgs.lionengine.swing.ComboItem;
import com.b3dgs.lionengine.swing.ComboListener;
import com.b3dgs.lionengine.swing.ComboRenderer;
import com.b3dgs.lionengine.utility.UtilitySwing;

public class EntryEditor
        extends JPanel
{
    private static final long serialVersionUID = 1L;
    private static final int PATROL_MIN = 0;
    private static final int PATROL_MAX = 1;
    private static final int SPEED = 2;
    private final Editor editor;
    private final JTabbedPane tabs;
    private final JPanel playerPanel, patrolPanel;
    private final JLabel patrolMin = new JLabel();
    private final JLabel patrolMax = new JLabel();
    private final JLabel speedValue = new JLabel();
    final JComboBox<ComboItem> comboMovement;
    final JComboBox<ComboItem> comboDirection;
    Entity selectedEntry;

    public EntryEditor(final Editor editor)
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
        UtilitySwing.addButton("Start", startEnd, new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                editor.setSelectionState(ToolBar.PLAYER);
                editor.world.setPlayerSelection(ToolBar.PLAYER_PLACE_START);
            }
        });
        UtilitySwing.addButton("End", startEnd, new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                editor.setSelectionState(ToolBar.PLAYER);
                editor.world.setPlayerSelection(ToolBar.PLAYER_PLACE_END);
            }
        });
        playerValues.add(startEnd, BorderLayout.CENTER);
        final JPanel checkpoint = UtilitySwing.createBorderedPanel("Checkpoint", 1);
        UtilitySwing.addButton("Add", checkpoint, new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                editor.setSelectionState(ToolBar.PLAYER);
                editor.world.setPlayerSelection(ToolBar.PLAYER_PLACE_ADD_CHK);
            }
        });
        UtilitySwing.addButton("Remove", checkpoint, new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                editor.setSelectionState(ToolBar.PLAYER);
                editor.world.setPlayerSelection(ToolBar.PLAYER_PLACE_DEL_CHK);
            }
        });
        playerValues.add(checkpoint, BorderLayout.CENTER);
        playerPanel.add(playerValues, BorderLayout.CENTER);

        patrolPanel = new JPanel();
        patrolPanel.setLayout(new BorderLayout());
        tabs.addTab("Patrol", patrolPanel);
        final JPanel patrolValues = new JPanel();
        patrolValues.setLayout(new GridLayout(1, 3));
        addIncDec(patrolValues, "Left", patrolMin, 1, EntryEditor.PATROL_MIN);
        addIncDec(patrolValues, "Right", patrolMax, 1, EntryEditor.PATROL_MAX);
        addIncDec(patrolValues, "Speed", speedValue, 1, EntryEditor.SPEED);
        patrolPanel.add(patrolValues, BorderLayout.CENTER);

        final JPanel combos = new JPanel();
        combos.setLayout(new GridLayout(2, 1));
        patrolPanel.add(combos, BorderLayout.SOUTH);

        final String directions[] =
        {
                "None", "Horizontal", "Vertical", "Turning"
        };
        final ComboItem dirs[] = new ComboItem[directions.length];
        for (int i = 0; i < directions.length; i++)
        {
            dirs[i] = new ComboItem(directions[i]);
        }

        comboMovement = EntryEditor.addMenuCombo("Movement:", combos, dirs, new ActionCombo()
        {
            @Override
            public void action(Object item)
            {
                if (selectedEntry != null)
                {
                    selectedEntry.data.setMovement(comboMovement.getSelectedIndex());
                }
            }
        });

        final String firstMove[] =
        {
                "Left/Top", "Right/Down", "Random"
        };
        final ComboItem fm[] = new ComboItem[firstMove.length];
        for (int i = 0; i < firstMove.length; i++)
        {
            fm[i] = new ComboItem(firstMove[i]);
        }

        comboDirection = EntryEditor.addMenuCombo("Starting direction:", combos, fm, new ActionCombo()
        {

            @Override
            public void action(Object item)
            {
                if (selectedEntry != null)
                {
                    selectedEntry.data.setFirstMove(comboDirection.getSelectedIndex());
                }
            }
        });

        setEnabled(patrolPanel.getComponents(), false);
        tabs.setPreferredSize(new Dimension(204, 180));
        tabs.setMinimumSize(new Dimension(204, 180));
        tabs.setMaximumSize(new Dimension(204, 180));
    }

    private void setEnabled(Component c[], boolean enabled)
    {
        for (final Component element : c)
        {
            element.setEnabled(enabled);
            if (element instanceof JPanel)
            {
                final JPanel comp = (JPanel) element;
                setEnabled(comp.getComponents(), enabled);
            }
        }
    }

    private static void setEnabled(JComboBox<ComboItem> combo, int item, boolean enabled)
    {
        combo.getItemAt(item).setEnabled(enabled);
    }

    public static JComboBox<ComboItem> addMenuCombo(String name, JPanel panel, ComboItem[] tab, ActionCombo a)
    {
        final JComboBox<ComboItem> combo = new JComboBox<>(tab);
        combo.setRenderer(new ComboRenderer<>());
        combo.addActionListener(new ComboListener<>(combo, a));
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

    public void setEntry(Entity entry)
    {
        selectedEntry = entry;
        if (entry != null && entry.patrolEnabled())
        {
            boolean done = false;
            for (int i = 0; i < 4; i++)
            {
                final boolean enabled = entry.movementEnabled(i);
                EntryEditor.setEnabled(comboMovement, i, enabled);
                if (enabled && !done)
                {
                    if (entry.data.getMovement() == i)
                    {
                        comboMovement.setSelectedIndex(i);
                        done = true;
                    }
                }
            }
            setEnabled(patrolPanel.getComponents(), true);
            setValue(patrolMin, entry.data.getPatrolLeft());
            setValue(patrolMax, entry.data.getPatrolRight());
            setValue(speedValue, entry.data.getMoveSpeed());
            comboMovement.setSelectedIndex(entry.data.getMovement());
            comboDirection.setSelectedIndex(entry.data.getFirstMove());
        }
        else
        {
            patrolMin.setText(null);
            patrolMax.setText(null);
            speedValue.setText(null);
            setEnabled(patrolPanel.getComponents(), false);
        }
        editor.repaint();
    }

    private JPanel addIncDec(JPanel parent, String name, final JLabel label, final int step, final int order)
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
            public void actionPerformed(ActionEvent e)
            {
                if (selectedEntry != null)
                {
                    setValue(label, getValue(label) + step);
                    changeOrderValue(label, order);
                }
            }
        });
        UtilitySwing.addButton("-", incdec, new ActionListener()
        {

            @Override
            public void actionPerformed(ActionEvent e)
            {
                if (selectedEntry != null)
                {
                    setValue(label, getValue(label) - step);
                    changeOrderValue(label, order);
                }
            }
        });
        label.setHorizontalAlignment(SwingConstants.CENTER);
        incdec.add(label);

        return incdec;
    }

    void changeOrderValue(JLabel label, int order)
    {
        switch (order)
        {
            case PATROL_MIN:
                selectedEntry.data.setPatrolLeft(getValue(label));
                break;
            case PATROL_MAX:
                selectedEntry.data.setPatrolRight(getValue(label));
                break;
            case SPEED:
                selectedEntry.data.setMoveSpeed(getValue(label));
                break;
        }
    }

    int getValue(JLabel label)
    {
        return Integer.parseInt(label.getText());
    }

    void setValue(JLabel label, int value)
    {
        int v = value;
        if (v < 0)
        {
            v = 0;
        }
        label.setText(String.valueOf(v));
    }
}
