package com.b3dgs.lionengine.example.c_platform.e_lionheart.editor;

import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

import com.b3dgs.lionengine.example.c_platform.e_lionheart.Editor;

/**
 * State bar implementation (giving information on the current editor states.
 */
public class StateBar
        extends JPanel
{
    /** Uid. */
    private static final long serialVersionUID = -1306034890537499369L;
    /** Editor reference. */
    private final Editor editor;

    /**
     * Constructor.
     * 
     * @param editor The editor reference.
     */
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
        state.append(editor.getOffsetViewInTileV());
        state.append(" | ");
        state.append(editor.getOffsetViewInTileH());
        state.append("]");
        gd.drawString(state.toString(), 16, 12);

        state = new StringBuilder("Pointer: ");
        state.append(editor.getSelectionState().getDescription());
        gd.drawString(state.toString(), 128, 12);

        state = new StringBuilder("Selection: ");
        if (editor.getSelectedEntity() != null)
        {
            state.append(editor.getSelectedEntity().toString());
        }
        else
        {
            state.append("none");
        }
        gd.drawString(state.toString(), 240, 12);
        gd.drawString(Editor.VERSION.toString(), editor.getWidth() - 45, 12);
    }
}
