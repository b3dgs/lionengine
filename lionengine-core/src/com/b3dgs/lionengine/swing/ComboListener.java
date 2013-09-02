package com.b3dgs.lionengine.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;

/**
 * Combo action listener.
 * 
 * @param <E> The object type.
 */
public final class ComboListener<E>
        implements ActionListener
{
    /** The combo reference. */
    private final JComboBox<E> combo;
    /** Combo action on item. */
    private final ActionCombo action;
    /** Current combo item. */
    private Object currentItem;

    /**
     * Constructor.
     * 
     * @param combo The combo reference.
     * @param action The action reference.
     */
    public ComboListener(JComboBox<E> combo, ActionCombo action)
    {
        this.combo = combo;
        combo.setSelectedIndex(0);
        currentItem = combo.getSelectedItem();
        this.action = action;
    }

    @Override
    public void actionPerformed(ActionEvent event)
    {
        final Object tempItem = combo.getSelectedItem();
        if (!((CanEnable) tempItem).isEnabled())
        {
            combo.setSelectedItem(currentItem);
        }
        else
        {
            currentItem = tempItem;
        }
        action.action(((ComboItem) tempItem).getObject());
    }
}
