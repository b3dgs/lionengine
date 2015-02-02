/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.b3dgs.lionengine.core.EngineCore;
import com.b3dgs.lionengine.core.awt.Theme;
import com.b3dgs.lionengine.core.awt.swing.UtilitySwing;
import com.b3dgs.lionengine.example.core.drawable.AppDrawable;
import com.b3dgs.lionengine.example.game.background.AppBackground;
import com.b3dgs.lionengine.example.game.collision.AppCollision;
import com.b3dgs.lionengine.example.game.effect.AppGameEffect;
import com.b3dgs.lionengine.example.game.gameplay.AppPlatformEntity;
import com.b3dgs.lionengine.example.game.map.AppGameMap;
import com.b3dgs.lionengine.example.game.projectile.AppGameProjectile;
import com.b3dgs.lionengine.example.game.strategy.ability.AppStrategyAbility;
import com.b3dgs.lionengine.example.game.strategy.controlpanel.AppStrategyControlPanel;
import com.b3dgs.lionengine.example.game.strategy.cursor.AppStrategyCursor;
import com.b3dgs.lionengine.example.game.strategy.fog.AppStrategyFog;
import com.b3dgs.lionengine.example.game.strategy.skills.AppStrategySkills;
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
    public static final String NAME = "LionEngine Examples";

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
        addExample(panel, "Effect", AppGameEffect.class);
        addExample(panel, "Projectile", AppGameProjectile.class);
        addExample(panel, "Map", AppGameMap.class);
        addExample(panel, "Background", AppBackground.class);
        addExample(panel, "Collision", AppCollision.class);
        addExample(panel, "Entity", AppPlatformEntity.class);
        addExample(panel, "Ability", AppStrategyAbility.class);
        addExample(panel, "Control Panel", AppStrategyControlPanel.class);
        addExample(panel, "Cursor", AppStrategyCursor.class);
        addExample(panel, "Fog of War", AppStrategyFog.class);
        addExample(panel, "Skills", AppStrategySkills.class);
        addExample(panel, "Pong", AppPong.class);

        final JButton exit = new JButton("Exit");
        exit.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent event)
            {
                frame.dispose();
            }
        });
        panel.add(exit);

        frame.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent event)
            {
                frame.dispose();
            }
        });

        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                frame.add(panel);
                frame.setResizable(false);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
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
        drawable.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent event)
            {
                UtilitySwing.setEnabled(panel.getComponents(), false);
                try
                {
                    example.getDeclaredMethod("main", String[].class).invoke(example, (Object[]) new String[1]);
                }
                catch (final ReflectiveOperationException exception)
                {
                    exception.printStackTrace();
                }
                final Thread thread = new Thread()
                {
                    @Override
                    public void run()
                    {
                        while (EngineCore.isStarted())
                        {
                            try
                            {
                                Thread.sleep(250);
                            }
                            catch (final InterruptedException exception)
                            {
                                break;
                            }
                        }
                        SwingUtilities.invokeLater(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                UtilitySwing.setEnabled(panel.getComponents(), true);
                            }
                        });
                    }
                };
                thread.start();
            }
        });
        panel.add(drawable);
    }
}
