/*
 * Copyright (C) 2013-2021 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.b3dgs.lionengine.awt.graphic;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collection;
import java.util.stream.Collectors;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Engine;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.awt.Keyboard;
import com.b3dgs.lionengine.awt.Mouse;
import com.b3dgs.lionengine.graphic.ScreenListener;

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

        final java.awt.DisplayMode desktop = dev.getDisplayMode();
        if (desktop.getWidth() == config.getOutput().getWidth()
            && desktop.getHeight() == config.getOutput().getHeight())
        {
            frame.setUndecorated(true);
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
        final String title = getTitle();
        final JFrame jframe = new JFrame(title, conf);
        jframe.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        jframe.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent event)
            {
                listeners.forEach(ScreenListener::notifyClosed);
            }
        });
        jframe.setResizable(false);
        jframe.setUndecorated(false);
        jframe.setIgnoreRepaint(true);

        return jframe;
    }

    /**
     * Get screen title.
     * 
     * @return The screen title.
     */
    private static String getTitle()
    {
        final StringBuilder builder = new StringBuilder(Constant.BYTE_4);
        if (Engine.isStarted())
        {
            builder.append(Engine.getProgramName()).append(Constant.SPACE).append(Engine.getProgramVersion());
        }
        else
        {
            builder.append(Constant.ENGINE_NAME).append(Constant.SPACE).append(Constant.ENGINE_VERSION);
        }
        return builder.toString();
    }

    /*
     * Screen
     */

    @Override
    public void start()
    {
        frame.validate();
        frame.setEnabled(true);
        super.start();
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
    public void setIcons(Collection<Media> icons)
    {
        frame.setIconImages(icons.stream().map(i -> new ImageIcon(i.getUrl()).getImage()).collect(Collectors.toList()));
    }
}
