package com.b3dgs.lionengine.example.c_platform.e_lionheart.editor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import com.b3dgs.lionengine.example.c_platform.e_lionheart.Editor;
import com.b3dgs.lionengine.utility.UtilitySwing;

/**
 * Tool bar implementation.
 */
public class ToolBar
        extends JToolBar
{
    /** Uid. */
    private static final long serialVersionUID = -3884748128028563357L;
    /** Entity selector reference. */
    public final EntitySelector entitySelector;
    /** Entity editor reference. */
    public final EntityEditor entityEditor;

    /**
     * Constructor.
     * 
     * @param editor The editor reference.
     */
    public ToolBar(final Editor editor)
    {
        super();
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        final JPanel entityPanel = new JPanel();
        entityPanel.setLayout(new BorderLayout());
        final JPanel palettePanel = UtilitySwing.createBorderedPanel("Pointer", 1);
        palettePanel.setLayout(new GridLayout(2, 2));
        UtilitySwing.addButton("Select", palettePanel, new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                editor.setSelectionState(TypeSelection.SELECT);
            }
        });
        UtilitySwing.addButton("Place", palettePanel, new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                editor.setSelectionState(TypeSelection.PLACE);
            }
        });
        UtilitySwing.addButton("Delete", palettePanel, new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                editor.setSelectionState(TypeSelection.DELETE);
            }
        });
        entityPanel.add(palettePanel, BorderLayout.NORTH);
        entitySelector = new EntitySelector(editor);
        entityPanel.add(entitySelector, BorderLayout.CENTER);

        final JPanel editPanel = new JPanel();
        editPanel.setLayout(new BorderLayout());
        entityEditor = new EntityEditor(editor);
        editPanel.add(entityEditor, BorderLayout.CENTER);

        add(entityPanel);
        add(editPanel);

        editPanel.setMinimumSize(new Dimension(entityEditor.getPreferredSize()));
        editPanel.setMaximumSize(new Dimension(entityEditor.getPreferredSize()));
        entityPanel.setMinimumSize(new Dimension(entitySelector.getPreferredSize()));

        setPreferredSize(new Dimension(204, 480));
        setMinimumSize(new Dimension(204, 480));
    }
}
