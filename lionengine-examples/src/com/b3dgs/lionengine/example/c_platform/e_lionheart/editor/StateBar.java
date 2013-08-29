package com.b3dgs.lionengine.example.c_platform.e_lionheart.editor;

import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

import com.b3dgs.lionengine.example.c_platform.e_lionheart.AppLionheart;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.Editor;

public class StateBar
        extends JPanel
{
    private static final long serialVersionUID = 1L;
    private final Editor editor;

    public StateBar(final Editor editor)
    {
        this.editor = editor;
        final Dimension size = new Dimension(getWidth(), 16);
        setPreferredSize(size);
        setMinimumSize(size);
    }

    @Override
    public void paintComponent(Graphics gd)
    {
        StringBuilder state = new StringBuilder("Location: [");
        state.append(editor.getVOffset());
        state.append(" | ");
        state.append(editor.getHOffset());
        state.append("]");
        gd.drawString(state.toString(), 16, 12);

        state = new StringBuilder("Pointer: ");
        final int sel = editor.getSelectionState();
        if (sel == ToolBar.SELECT)
        {
            state.append("Selecting");
        }
        else if (sel == ToolBar.PLACE)
        {
            state.append("Placing");
        }
        else if (sel == ToolBar.DELETE)
        {
            state.append("Deleting");
        }
        gd.drawString(state.toString(), 128, 12);

        state = new StringBuilder("Selection: ");
        if (editor.selection.id != null)
        {
            state.append(editor.selection.id.toString());
        }
        else
        {
            state.append("none");
        }
        gd.drawString(state.toString(), 240, 12);
        gd.drawString(AppLionheart.VERSION.toString(), editor.getWidth() - 45, 12);
    }
}
