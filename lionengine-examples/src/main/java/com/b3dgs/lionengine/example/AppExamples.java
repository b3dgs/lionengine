/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.example;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.core.Engine;
import com.b3dgs.lionengine.core.awt.Theme;
import com.b3dgs.lionengine.core.awt.swing.UtilitySwing;
import com.b3dgs.lionengine.example.core.drawable.AppDrawable;
import com.b3dgs.lionengine.example.game.action.AppAction;
import com.b3dgs.lionengine.example.game.assign.AppAssign;
import com.b3dgs.lionengine.example.game.attack.AppAttack;
import com.b3dgs.lionengine.example.game.background.AppBackground;
import com.b3dgs.lionengine.example.game.collision.AppCollision;
import com.b3dgs.lionengine.example.game.cursor.AppCursor;
import com.b3dgs.lionengine.example.game.effect.AppEffect;
import com.b3dgs.lionengine.example.game.extraction.AppExtraction;
import com.b3dgs.lionengine.example.game.fog.AppFog;
import com.b3dgs.lionengine.example.game.map.AppMap;
import com.b3dgs.lionengine.example.game.pathfinding.AppPathfinding;
import com.b3dgs.lionengine.example.game.production.AppProduction;
import com.b3dgs.lionengine.example.game.projectile.AppProjectile;
import com.b3dgs.lionengine.example.game.raster.AppRaster;
import com.b3dgs.lionengine.example.game.selector.AppSelector;
import com.b3dgs.lionengine.example.game.state.AppState;
import com.b3dgs.lionengine.example.helloworld.AppHelloWorld;
import com.b3dgs.lionengine.example.pong.AppPong;

/**
 * Program starts here.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class AppExamples
{
    /** Application name. */
    public static final String NAME = Engine.NAME + " Examples";
    /** Executor. */
    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();

    /**
     * Main function.
     * 
     * @param args The arguments (none).
     */
    public static void main(final String[] args)
    {
        Theme.set(Theme.SYSTEM);

        final JFrame frame = new JFrame(NAME);
        frame.setPreferredSize(new Dimension(576, 256));

        final JPanel panel = new JPanel(true);
        panel.setLayout(new GridLayout(4, 4));

        addExample(panel, "Hello World", AppHelloWorld.class);
        addExample(panel, "Drawable", AppDrawable.class);
        addExample(panel, "Action", AppAction.class);
        addExample(panel, "Assign", AppAssign.class);
        addExample(panel, "Attack", AppAttack.class);
        addExample(panel, "Background", AppBackground.class);
        addExample(panel, "Collision", AppCollision.class);
        addExample(panel, "Cursor", AppCursor.class);
        addExample(panel, "Effect", AppEffect.class);
        addExample(panel, "Fog", AppFog.class);
        addExample(panel, "Map", AppMap.class);
        addExample(panel, "Pathfinding", AppPathfinding.class);
        addExample(panel, "Production", AppProduction.class);
        addExample(panel, "Extraction", AppExtraction.class);
        addExample(panel, "Projectile", AppProjectile.class);
        addExample(panel, "Raster", AppRaster.class);
        addExample(panel, "Selector", AppSelector.class);
        addExample(panel, "State", AppState.class);
        addExample(panel, "Pong", AppPong.class);

        final JButton exit = new JButton("Exit");
        exit.addActionListener(event -> terminate(frame));
        panel.add(exit);

        frame.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent event)
            {
                terminate(frame);
            }
        });

        SwingUtilities.invokeLater(() ->
        {
            frame.add(panel);
            frame.setResizable(false);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    /**
     * Terminate example.
     * 
     * @param frame The main frame reference.
     */
    static void terminate(JFrame frame)
    {
        EXECUTOR.shutdownNow();
        frame.dispose();
    }

    /**
     * Add a example with its button and action.
     * 
     * @param panel The panel reference.
     * @param name The example name.
     * @param example The example class.
     */
    private static void addExample(final JPanel panel, String name, final Class<?> example)
    {
        final JButton drawable = new JButton(name);
        drawable.addActionListener(event ->
        {
            UtilitySwing.setEnabled(panel.getComponents(), false);
            try
            {
                example.getDeclaredMethod("main", String[].class).invoke(example, (Object[]) new String[1]);
                final Runnable runnable = () ->
                {
                    while (Engine.isStarted())
                    {
                        try
                        {
                            Thread.sleep(250);
                        }
                        catch (final InterruptedException exception)
                        {
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }
                    SwingUtilities.invokeLater(() -> UtilitySwing.setEnabled(panel.getComponents(), true));
                };
                EXECUTOR.execute(runnable);
            }
            catch (final Exception exception)
            {
                Verbose.exception(exception);
                SwingUtilities.invokeLater(() -> UtilitySwing.setEnabled(panel.getComponents(), true));
            }
        });
        panel.add(drawable);
    }
}
