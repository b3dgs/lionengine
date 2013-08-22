package com.b3dgs.lionengine.utility;

import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import com.b3dgs.lionengine.swing.ActionCombo;
import com.b3dgs.lionengine.swing.ComboListener;
import com.b3dgs.lionengine.swing.ComboRenderer;

/**
 * Set of functions around swing call, in order to create easily standard JObjects.
 */
public final class UtilitySwing
{
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
        panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(title),
                BorderFactory.createEmptyBorder(margin, margin, margin, margin)));

        return panel;
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
        button.addActionListener(action);
        panel.add(button);
        return button;
    }

    /**
     * Create a menu combo.
     * 
     * @param <T> The object type.
     * @param name The combo name.
     * @param panel The panel owner.
     * @param tab The combo list.
     * @param actionCombo The combo action.
     * @return The created combo.
     */
    public static <T> JComboBox<T> addMenuCombo(String name, JPanel panel, T[] tab, ActionCombo actionCombo)
    {
        final JComboBox<T> combo = new JComboBox<>(tab);
        combo.setRenderer(new ComboRenderer<>());
        combo.addActionListener(new ComboListener<>(combo, actionCombo));
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
     * Create a menu.
     * 
     * @param bar The menu bar owner.
     * @param name The menu name.
     * @param icon The menu icon.
     * @return The created menu.
     */
    public static JMenu addMenu(JMenuBar bar, String name, String icon)
    {
        JMenuBar menuBar = bar;
        if (menuBar == null)
        {
            menuBar = new JMenuBar();
        }
        final JMenu menu = new JMenu(name);
        if (icon != null)
        {
            menu.setIcon(new ImageIcon(icon));
        }
        menuBar.add(menu);
        return menu;
    }

    /**
     * Create a menu item.
     * 
     * @param menu The menu owner.
     * @param name The item name.
     * @param ico The item icon.
     * @param action The item action.
     * @return The created item.
     */
    public static JMenuItem addMenuItem(JMenu menu, String name, String ico, ActionListener action)
    {
        final JMenuItem item = new JMenuItem(name);
        item.addActionListener(action);
        menu.add(item);
        if (ico != null)
        {
            item.setIcon(new ImageIcon(ico));
        }
        return item;
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
        checkBox.addActionListener(action);
        panel.add(checkBox);
        return checkBox;
    }

    /**
     * Create a radio button.
     * 
     * @param name The button name.
     * @param panel The panel owner.
     * @param action The button action.
     * @return created radio button.
     */
    public static JRadioButton addRadioButton(String name, JPanel panel, ActionListener action)
    {
        return UtilitySwing.addRadioButton(name, panel, null, action);
    }

    /**
     * Create a radio button.
     * 
     * @param name The button name.
     * @param panel The panel owner.
     * @param action The button action.
     * @param tip The displayed tip.
     * @return The created radio button.
     */
    public static JRadioButton addRadioButton(String name, JPanel panel, String tip, ActionListener action)
    {
        final JRadioButton radio = new JRadioButton(name);
        radio.setToolTipText(tip);
        radio.addActionListener(action);
        panel.add(radio);
        return radio;
    }

    /**
     * Create a text field.
     * 
     * @param name The field name.
     * @param panel The panel owner.
     * @param labelWidth The label field width.
     * @param fieldWidth The field width.
     * @param height The global height.
     * @return The created text field.
     */
    public static JTextField createField(String name, JPanel panel, int labelWidth, int fieldWidth, int height)
    {
        final JPanel container = new JPanel();
        final JTextField field = new JTextField(fieldWidth);
        field.setMinimumSize(new Dimension(fieldWidth, height));
        field.setMaximumSize(new Dimension(fieldWidth, height));
        field.setEditable(false);
        if (name != null)
        {
            final JLabel label = new JLabel(name + ":");
            label.setLabelFor(field);
            label.setPreferredSize(new Dimension(labelWidth, height));
            container.add(label);
        }
        container.add(field);
        panel.add(container);
        return field;
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
     * Private constructor.
     */
    private UtilitySwing()
    {
        throw new RuntimeException();
    }
}
