package com.b3dgs.lionengine.swing;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

/**
 * Disactivable combo box implementation.
 * 
 * @param <E> The object type.
 */
public final class DisactivableComboBox<E>
        extends JLabel
        implements ListCellRenderer<E>
{
    /** Serial uid. */
    private static final long serialVersionUID = -5285387364457225845L;

    /**
     * Constructor.
     */
    public DisactivableComboBox()
    {
        setOpaque(true);
        setBorder(new EmptyBorder(1, 1, 1, 1));
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends E> list, E value, int index, boolean isSelected,
            boolean cellHasFocus)
    {
        if (isSelected)
        {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        }
        else
        {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }
        if (!((CanEnable) value).isEnabled())
        {
            setBackground(list.getBackground());
            setForeground(UIManager.getColor("Label.disabledForeground"));
        }
        setFont(list.getFont());
        setText(value.toString());
        return this;
    }
}
