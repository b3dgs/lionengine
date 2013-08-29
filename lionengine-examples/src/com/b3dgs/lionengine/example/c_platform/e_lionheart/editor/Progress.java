package com.b3dgs.lionengine.example.c_platform.e_lionheart.editor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;

public class Progress
{
    private final Frame frame;
    private final int max;
    private int value;
    private final int fact;

    public Progress(int max)
    {
        frame = new Frame("Progress ...");
        frame.setPreferredSize(new Dimension(192, 48));
        frame.setSize(192, 48);
        frame.setResizable(false);
        frame.setAlwaysOnTop(true);
        this.max = max;
        value = 0;
        fact = max / frame.getWidth();
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public synchronized void increase(int v)
    {
        value += v;
        update();
    }

    private void update()
    {
        final Graphics g = frame.getGraphics();
        g.setColor(Color.RED);
        g.fillRect(0, 0, value / fact, 48);
        g.dispose();
        frame.repaint();
    }

    public void terminate()
    {
        frame.dispose();
    }
}
