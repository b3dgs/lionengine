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

    /**
     * Create the palette panel.
     * 
     * @param editor The editor reference.
     * @return The panel instance.
     */
    private static JPanel createPalettePanel(final Editor editor)
    {
        final JPanel palettePanel = UtilitySwing.createBorderedPanel("Pointer", 1);
        palettePanel.setLayout(new GridLayout(2, 2));
        UtilitySwing.addButton("Select", palettePanel, new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent event)
            {
                editor.setSelectionState(TypeSelection.SELECT);
            }
        }).setToolTipText("Enable the selection mode. Allow to select entities on map.");
        UtilitySwing.addButton("Place", palettePanel, new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent event)
            {
                editor.setSelectionState(TypeSelection.PLACE);
            }
        }).setToolTipText("Enable the place mode. Allow to place new entities.");
        UtilitySwing.addButton("Delete", palettePanel, new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent event)
            {
                editor.setSelectionState(TypeSelection.DELETE);
            }
        }).setToolTipText("Enable the delete mode. Allow to delete entities by clicking on them");
        return palettePanel;
    }

    /** Entity selector reference. */
    public final EntitySelector entitySelector;
    /** Entity editor reference. */
    public final EntityEditor entityEditor;
    /** Palette panel. */
    private final JPanel palettePanel;

    /**
     * Constructor.
     * 
     * @param editor The editor reference.
     */
    public ToolBar(Editor editor)
    {
        super();
        palettePanel = ToolBar.createPalettePanel(editor);
        entitySelector = new EntitySelector(editor);
        entityEditor = new EntityEditor(editor);
        setPaletteEnabled(false);
        setSelectorEnabled(false);
        setEditorEnabled(false);
        init();
    }

    /**
     * Set the palette panel enabled state.
     * 
     * @param enabled <code>true</code> to enable, <code>false</code> else.
     */
    public void setPaletteEnabled(boolean enabled)
    {
        UtilitySwing.setEnabled(palettePanel.getComponents(), enabled);
    }

    /**
     * Set the entity selector enabled state.
     * 
     * @param enabled <code>true</code> to enable, <code>false</code> else.
     */
    public void setSelectorEnabled(boolean enabled)
    {
        UtilitySwing.setEnabled(entitySelector.getComponents(), enabled);
    }

    /**
     * Set the entity editor enabled state.
     * 
     * @param enabled <code>true</code> to enable, <code>false</code> else.
     */
    public void setEditorEnabled(boolean enabled)
    {
        entityEditor.setEnabled(enabled);
    }

    /**
     * Initialize the tool bar content.
     */
    private void init()
    {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        final JPanel entityPanel = new JPanel();
        entityPanel.setLayout(new BorderLayout());

        final JPanel editPanel = new JPanel();
        editPanel.setLayout(new BorderLayout());

        add(entityPanel);
        add(editPanel);

        editPanel.setMinimumSize(new Dimension(entityEditor.getPreferredSize()));
        editPanel.setMaximumSize(new Dimension(entityEditor.getPreferredSize()));
        entityPanel.setMinimumSize(new Dimension(entitySelector.getPreferredSize()));

        entityPanel.add(palettePanel, BorderLayout.NORTH);
        entityPanel.add(entitySelector, BorderLayout.CENTER);
        editPanel.add(entityEditor, BorderLayout.CENTER);

        setPreferredSize(new Dimension(204, 480));
        setMinimumSize(new Dimension(204, 480));
    }
}
