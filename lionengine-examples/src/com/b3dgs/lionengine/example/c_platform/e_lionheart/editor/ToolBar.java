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

public class ToolBar
        extends JToolBar
{
    private static final long serialVersionUID = 1L;
    public static final int SELECT = 0;
    public static final int PLACE = 1;
    public static final int DELETE = 2;
    public static final int PLAYER = 3;
    public static final int PLAYER_PLACE_START = 0;
    public static final int PLAYER_PLACE_END = 1;
    public static final int PLAYER_PLACE_ADD_CHK = 2;
    public static final int PLAYER_PLACE_DEL_CHK = 3;
    private final Editor editor;
    public final EntrySelector entrySelector;
    public final EntryEditor entryEditor;

    public ToolBar(final Editor editor)
    {
        super();
        this.editor = editor;
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        final JPanel entrysPanel = new JPanel();
        entrysPanel.setLayout(new BorderLayout());
        final JPanel palettePanel = UtilitySwing.createBorderedPanel("Pointer", 1);
        palettePanel.setLayout(new GridLayout(2, 2));
        UtilitySwing.addButton("Select", palettePanel, new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                editor.setSelectionState(ToolBar.SELECT);
            }
        });
        UtilitySwing.addButton("Place", palettePanel, new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                editor.setSelectionState(ToolBar.PLACE);
            }
        });
        UtilitySwing.addButton("Delete", palettePanel, new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                editor.setSelectionState(ToolBar.DELETE);
            }
        });
        entrysPanel.add(palettePanel, BorderLayout.NORTH);
        entrySelector = new EntrySelector(editor);
        entrysPanel.add(entrySelector, BorderLayout.CENTER);

        final JPanel editPanel = new JPanel();
        editPanel.setLayout(new BorderLayout());
        entryEditor = new EntryEditor(editor);
        editPanel.add(entryEditor, BorderLayout.CENTER);

        add(entrysPanel);
        add(editPanel);

        editPanel.setMinimumSize(new Dimension(entryEditor.getPreferredSize()));
        editPanel.setMaximumSize(new Dimension(entryEditor.getPreferredSize()));
        entrysPanel.setMinimumSize(new Dimension(entrySelector.getPreferredSize()));

        setPreferredSize(new Dimension(204, 480));
        setMinimumSize(new Dimension(204, 480));
    }
}
