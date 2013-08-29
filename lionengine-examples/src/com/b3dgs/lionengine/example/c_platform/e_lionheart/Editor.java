package com.b3dgs.lionengine.example.c_platform.e_lionheart;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.editor.MenuBar;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.editor.StateBar;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.editor.ToolBar;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.editor.WorldPanel;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.Entity;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.TypeEntityCategory;
import com.b3dgs.lionengine.utility.UtilityMath;

public class Editor
        extends JFrame
{
    public class SelectedEntry
    {
        public TypeWorld theme;
        public TypeEntityCategory category;
        public Enum<?> id;
    }

    private static final long serialVersionUID = 1L;
    private static final int V_TILE_STEP = 6;
    private static final int H_TILE_STEP = 6;
    public final Config config;
    public final WorldPanel world;
    public final ToolBar toolBar;
    public final SelectedEntry selection;
    private int vOffset;
    private int hOffset;
    private int state;

    public Editor(Config config)
    {
        super("Editor");
        this.config = config;
        selection = new SelectedEntry();
        world = new WorldPanel(this);
        toolBar = new ToolBar(this);
        hOffset = 0;
        vOffset = 0;
        init();
    }

    private void init()
    {
        addWindowListener(new WindowAdapter()
        {

            @Override
            public void windowClosing(WindowEvent e)
            {
                terminate();
            }
        });
        setLayout(new BorderLayout());
        add(new MenuBar(this), BorderLayout.NORTH);
        add(toolBar, BorderLayout.WEST);
        add(world, BorderLayout.CENTER);
        add(new StateBar(this), BorderLayout.SOUTH);
        state = ToolBar.SELECT;
        validate();
        repaint();
        setMinimumSize(new Dimension(640, 480));

        Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener()
        {

            @Override
            public void eventDispatched(AWTEvent event)
            {
                final KeyEvent ke = (KeyEvent) event;
                if (ke.getID() == KeyEvent.KEY_PRESSED)
                {
                    keyPressed(ke);
                }
            }
        }, AWTEvent.KEY_EVENT_MASK);
    }

    public void start()
    {
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void terminate()
    {
        dispose();
    }

    public int getVOffset()
    {
        return vOffset;
    }

    public int getHOffset()
    {
        return hOffset;
    }

    public int getHRealOffset()
    {
        return hOffset * world.map.getTileWidth();
    }

    public int getVRealOffset()
    {
        return vOffset * world.map.getTileHeight();
    }

    public int getSelectionState()
    {
        return state;
    }

    public void setSelectionState(int state)
    {
        this.state = state;
        repaint();
    }

    public void keyPressed(KeyEvent e)
    {
        final int oh = hOffset;
        final int ov = vOffset;
        switch (e.getKeyCode())
        {
            case KeyEvent.VK_LEFT:
                hOffset -= Editor.H_TILE_STEP;
                break;
            case KeyEvent.VK_RIGHT:
                hOffset += Editor.H_TILE_STEP;
                break;
            case KeyEvent.VK_UP:
                vOffset += Editor.V_TILE_STEP;
                break;
            case KeyEvent.VK_DOWN:
                vOffset -= Editor.V_TILE_STEP;
                break;
        }
        vOffset = UtilityMath.fixBetween(vOffset, 0, world.map.getHeightInTile());
        hOffset = UtilityMath.fixBetween(hOffset, 0, world.map.getWidthInTile());
        final int mh = hOffset - oh;
        final int mv = vOffset - ov;
        if (mh != 0 || mv != 0)
        {
            for (final Entity ent : world.entrys.list())
            {
                if (ent.data.isSelected() && world.isClicking())
                {
                    ent.moveLocation(1.0, mh * world.map.getTileWidth(), mv * world.map.getTileHeight());
                }
            }
        }
        world.camera.moveLocation(1.0, mh * world.map.getTileWidth(), mv * world.map.getTileHeight());
        repaint();
    }
}
