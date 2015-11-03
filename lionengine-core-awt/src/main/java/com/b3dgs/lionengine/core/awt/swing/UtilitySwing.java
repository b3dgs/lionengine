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

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.filechooser.FileFilter;

import com.b3dgs.lionengine.LionEngineException;

/**
 * Set of functions around swing call, in order to create easily standard JObjects.
 */
public final class UtilitySwing
{
    /**
     * Create and open a single file chooser.
     * 
     * @param title The dialog title.
     * @param dir The initial directory.
     * @param parent The parent reference.
     * @param filter The file filter to use.
     * @return The selected media if accepted, <code>null</code> if not.
     */
    public static File createOpenFileChooser(String title, String dir, Component parent, FileFilter filter)
    {
        final JFileChooser fileChooser = new JFileChooser(dir);
        fileChooser.setDialogTitle(title);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setFileFilter(filter);

        final int approve = fileChooser.showOpenDialog(parent);
        if (approve == JFileChooser.APPROVE_OPTION)
        {
            return fileChooser.getSelectedFile();
        }
        return null;
    }

    /**
     * Create and open a single directory chooser.
     * 
     * @param title The dialog title.
     * @param dir The initial directory.
     * @param parent The parent reference.
     * @param filter The file filter to use.
     * @return The selected media if accepted, <code>null</code> if not.
     */
    public static File createOpenDirectoryChooser(String title, String dir, Component parent, FileFilter filter)
    {
        final JFileChooser fileChooser = new JFileChooser(dir);
        fileChooser.setDialogTitle(title);
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setFileFilter(filter);
        final int approve = fileChooser.showOpenDialog(parent);
        if (approve == JFileChooser.APPROVE_OPTION)
        {
            return fileChooser.getSelectedFile();
        }
        return null;
    }

    /**
     * Create a bordered panel.
     * 
     * @param title The panel title.
     * @param margin The panel margin.
     * @return The created panel.
     */
    public static JPanel createBorderedPanel(String title, int margin)
    {
        final JPanel panel = new JPanel();
        final Border outside = BorderFactory.createTitledBorder(title);
        final Border inside = BorderFactory.createEmptyBorder(margin, margin, margin, margin);
        panel.setBorder(BorderFactory.createCompoundBorder(outside, inside));

        return panel;
    }

    /**
     * Set a bordered panel.
     * 
     * @param panel The panel reference.
     * @param title The panel title.
     * @param margin The panel margin.
     */
    public static void setBorderedPanel(JPanel panel, String title, int margin)
    {
        final Border outside = BorderFactory.createTitledBorder(title);
        final Border inside = BorderFactory.createEmptyBorder(margin, margin, margin, margin);
        panel.setBorder(BorderFactory.createCompoundBorder(outside, inside));
    }

    /**
     * Create a button.
     * 
     * @param name The button name.
     * @param panel The button owner.
     * @param action The button action.
     * @return The created button.
     */
    public static JButton addButton(String name, JPanel panel, ActionListener action)
    {
        final JButton button = new JButton(name);
        if (action != null)
        {
            button.addActionListener(action);
        }
        panel.add(button);
        return button;
    }

    /**
     * Create a menu combo.
     * 
     * @param name The combo name.
     * @param panel The panel owner.
     * @param tab The combo list.
     * @param actionCombo The combo action.
     * @return The created combo.
     */
    public static JComboBox addMenuCombo(String name, JPanel panel, Object[] tab, ActionCombo actionCombo)
    {
        final JComboBox combo = new JComboBox(tab);
        combo.setRenderer(new ComboRenderer());
        if (actionCombo != null)
        {
            combo.addActionListener(new ComboListener(combo, actionCombo));
        }
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
     * Create a check box.
     * 
     * @param name The check box name.
     * @param panel The panel owner.
     * @param action The check box action.
     * @return The created check box.
     */
    public static JCheckBox addCheckBox(String name, JPanel panel, ActionListener action)
    {
        final JCheckBox checkBox = new JCheckBox(name);
        if (action != null)
        {
            checkBox.addActionListener(action);
        }
        panel.add(checkBox);
        return checkBox;
    }

    /**
     * Create a dialog.
     * 
     * @param owner The dialog owner.
     * @param title The dialog title.
     * @param width The dialog width.
     * @param height The dialog height.
     * @return The created dialog.
     */
    public static JDialog createDialog(JFrame owner, String title, int width, int height)
    {
        final JDialog dialog = new JDialog(owner, title);
        dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        dialog.setPreferredSize(new Dimension(width, height));
        dialog.setResizable(false);
        dialog.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                UtilitySwing.terminateDialog(dialog);
            }
        });
        return dialog;
    }

    /**
     * Start dialog.
     * 
     * @param dialog The dialog to start.
     */
    public static void startDialog(JDialog dialog)
    {
        dialog.validate();
        dialog.pack();
        dialog.getParent().setEnabled(false);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    /**
     * Terminate dialog.
     * 
     * @param dialog The dialog to terminate.
     */
    public static void terminateDialog(JDialog dialog)
    {
        dialog.getParent().setEnabled(true);
        dialog.dispose();
    }

    /**
     * Set the enabled state of a components set.
     * 
     * @param components The components.
     * @param enabled The enabled state.
     */
    public static void setEnabled(Component[] components, boolean enabled)
    {
        for (final Component component : components)
        {
            component.setEnabled(enabled);
            if (component instanceof Container)
            {
                final Container container = (Container) component;
                UtilitySwing.setEnabled(container.getComponents(), enabled);
            }
        }
    }

    /**
     * Private constructor.
     */
    private UtilitySwing()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
