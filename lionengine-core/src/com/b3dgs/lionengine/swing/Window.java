/*
 * Copyright (C) 2013 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Abstract window, mainly used by Options and Launcher.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public abstract class Window
        extends JFrame
{
    /** Serial uid. */
    private static final long serialVersionUID = -1567341421280721456L;

    /** Window owner reference. */
    protected final JFrame parent;
    /** Main panel reference. */
    protected final JPanel mainPanel;

    /**
     * Constructor.
     * 
     * @param title The window title.
     * @param width The window width.
     * @param height The window height.
     * @param parent The window owner.
     */
    public Window(String title, int width, int height, JFrame parent)
    {
        super(title);
        setResizable(false);
        setPreferredSize(new Dimension(width, height));
        addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                terminate();
            }
        });
        this.parent = parent;
        mainPanel = new JPanel(new BorderLayout());
        add(mainPanel);
    }

    /**
     * Start window.
     */
    public final void start()
    {
        if (parent != null)
        {
            parent.setEnabled(false);
        }
        pack();
        validate();
        setLocationRelativeTo(null);
        repaint();
        setEnabled(true);
        setVisible(true);
        requestFocus();
    }

    /**
     * Terminate window.
     */
    public final void terminate()
    {
        if (parent != null)
        {
            parent.setEnabled(true);
            parent.repaint();
            parent.requestFocus();
        }
        dispose();
    }

    /**
     * Set window icon.
     * 
     * @param filename The icon filename.
     */
    protected final void setIcon(String filename)
    {
        setIconImage(new ImageIcon(filename).getImage());
    }
}
