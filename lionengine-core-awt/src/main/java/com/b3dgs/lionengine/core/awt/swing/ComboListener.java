/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.core.awt.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;

/**
 * Combo action listener.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @param <E> The object type.
 */
public final class ComboListener<E> implements ActionListener
{
    /** The combo reference. */
    private final JComboBox<E> combo;
    /** Combo action on item. */
    private final ActionCombo action;
    /** Current combo item. */
    private Object currentItem;

    /**
     * Create a combo listener.
     * 
     * @param combo The combo reference.
     * @param action The action reference.
     */
    public ComboListener(JComboBox<E> combo, ActionCombo action)
    {
        this.combo = combo;
        this.action = action;
        combo.setSelectedIndex(0);
        currentItem = combo.getSelectedItem();
    }

    /*
     * ActionListener
     */

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
