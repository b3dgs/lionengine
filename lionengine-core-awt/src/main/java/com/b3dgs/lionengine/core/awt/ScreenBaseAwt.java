/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.core.awt;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.core.Engine;
import com.b3dgs.lionengine.graphic.ScreenListener;
import com.b3dgs.lionengine.io.awt.Keyboard;
import com.b3dgs.lionengine.io.awt.Mouse;

/**
 * Screen base implementation.
 * 
 * @see Keyboard
 * @see Mouse
 */
class ScreenBaseAwt extends ScreenAwtAbstract
{
    /** Graphics device reference. */
    protected final GraphicsDevice dev;
    /** Graphic configuration reference. */
    protected final GraphicsConfiguration conf;
    /** Frame reference. */
    protected final JFrame frame;

    /**
     * Internal constructor.
     * 
     * @param config The config reference.
     * @throws LionEngineException If renderer is <code>null</code> or no available display.
     */
    ScreenBaseAwt(Config config)
    {
        super(config);

        final GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        dev = env.getDefaultScreenDevice();
        conf = dev.getDefaultConfiguration();
        frame = initMainFrame();
    }

    /**
     * Called when screen is disposed.
     */
    void onDisposed()
    {
        for (final ScreenListener listener : listeners)
        {
            listener.notifyClosed();
        }
    }

    /**
     * Initialize the main frame.
     * 
     * @return The created main frame.
     * @throws LionEngineException If the engine has not been started.
     */
    private JFrame initMainFrame()
    {
        final String title = new StringBuilder().append(Engine.getProgramName())
                                                .append(Constant.SPACE)
                                                .append(Engine.getProgramVersion())
                                                .toString();
        final JFrame jframe = new JFrame(title, conf);
        jframe.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        jframe.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent event)
            {
                onDisposed();
            }
        });
        jframe.setResizable(false);
        jframe.setUndecorated(false);
        jframe.setIgnoreRepaint(true);

        return jframe;
    }

    /*
     * Screen
     */

    @Override
    public void start()
    {
        super.start();
        frame.validate();
        frame.setEnabled(true);
        frame.setVisible(true);
    }

    @Override
    public void dispose()
    {
        super.dispose();
        buf.dispose();
        frame.dispose();
    }

    @Override
    public void requestFocus()
    {
        frame.requestFocus();
        super.requestFocus();
    }

    @Override
    public void setIcon(String filename)
    {
        final ImageIcon icon = new ImageIcon(filename);
        frame.setIconImage(icon.getImage());
    }
}
