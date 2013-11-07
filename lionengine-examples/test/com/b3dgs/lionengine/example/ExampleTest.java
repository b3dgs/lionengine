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
package com.b3dgs.lionengine.example;

import java.awt.AWTException;
import java.awt.Robot;

import org.junit.AfterClass;
import org.junit.Test;

import com.b3dgs.lionengine.core.Engine;
import com.b3dgs.lionengine.core.Key;
import com.b3dgs.lionengine.example.drawable.AppDrawable;
import com.b3dgs.lionengine.example.game.cursor.AppGameCursor;
import com.b3dgs.lionengine.example.game.effect.AppGameEffect;
import com.b3dgs.lionengine.example.game.entity.AppGameEntity;
import com.b3dgs.lionengine.example.game.factory.AppGameFactory;
import com.b3dgs.lionengine.example.game.handler.AppGameHandler;
import com.b3dgs.lionengine.example.game.map.AppGameMap;
import com.b3dgs.lionengine.example.game.platform.background.AppPlatformBackground;
import com.b3dgs.lionengine.example.game.platform.collision.AppPlatformCollision;
import com.b3dgs.lionengine.example.game.platform.entity.AppPlatformEntity;
import com.b3dgs.lionengine.example.game.platform.tile.AppPlatformTile;
import com.b3dgs.lionengine.example.game.projectile.AppGameProjectile;
import com.b3dgs.lionengine.example.game.rts.ability.AppRtsAbility;
import com.b3dgs.lionengine.example.game.rts.controlpanel.AppRtsControlPanel;
import com.b3dgs.lionengine.example.game.rts.cursor.AppRtsCursor;
import com.b3dgs.lionengine.example.game.rts.fog.AppRtsFog;
import com.b3dgs.lionengine.example.game.rts.skills.AppRtsSkills;
import com.b3dgs.lionengine.example.helloworld.AppHelloWorld;
import com.b3dgs.lionengine.example.mario.AppMario;
import com.b3dgs.lionengine.example.minimal.AppMinimal;
import com.b3dgs.lionengine.example.pong.AppPong;

/**
 * Test the examples.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class ExampleTest
{
    /**
     * Terminate the test.
     */
    @AfterClass
    public static void cleanUp()
    {
        Engine.terminate();
    }

    /**
     * Pause.
     * 
     * @param duration Duration pause.
     */
    private static void pause(long duration)
    {
        try
        {
            Thread.sleep(duration);
        }
        catch (final InterruptedException exception)
        {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Wait for the test duration and terminate.
     * 
     * @param duration The test duration.
     */
    private static void waitAndTerminate(long duration)
    {
        ExampleTest.pause(duration);
        Engine.terminate();
        ExampleTest.pause(250);
    }

    /**
     * Test the examples.
     * 
     * @throws AWTException If error.
     */
    @Test
    public void testExamples() throws AWTException
    {
        AppMinimal.main(null);
        ExampleTest.waitAndTerminate(500);

        AppHelloWorld.main(null);
        ExampleTest.waitAndTerminate(500);

        AppDrawable.main(null);
        ExampleTest.waitAndTerminate(500);

        AppPong.main(null);
        ExampleTest.waitAndTerminate(2000);

        AppGameCursor.main(null);
        ExampleTest.waitAndTerminate(500);

        AppGameEffect.main(null);
        ExampleTest.waitAndTerminate(500);

        AppGameEntity.main(null);
        ExampleTest.waitAndTerminate(750);

        AppGameFactory.main(null);
        ExampleTest.waitAndTerminate(750);

        AppGameHandler.main(null);
        ExampleTest.waitAndTerminate(750);

        AppGameMap.main(null);
        ExampleTest.waitAndTerminate(2000);

        AppGameProjectile.main(null);
        ExampleTest.waitAndTerminate(5000);

        final Robot robot = new Robot();

        AppMario.main(null);
        robot.keyPress(Key.RIGHT.intValue());
        robot.keyPress(Key.UP.intValue());
        ExampleTest.waitAndTerminate(3000);
        robot.keyRelease(Key.RIGHT.intValue());
        robot.keyRelease(Key.UP.intValue());

        robot.waitForIdle();
    }

    /**
     * Test platform examples.
     */
    @Test
    public void testPlatform()
    {
        AppPlatformBackground.main(null);
        ExampleTest.waitAndTerminate(8000);

        AppPlatformCollision.main(null);
        ExampleTest.waitAndTerminate(1000);

        AppPlatformEntity.main(null);
        ExampleTest.waitAndTerminate(1000);

        AppPlatformTile.main(null);
        ExampleTest.waitAndTerminate(500);
    }

    /**
     * Test rts examples.
     */
    @Test
    public void testRts()
    {
        AppRtsAbility.main(null);
        ExampleTest.waitAndTerminate(20000);

        AppRtsControlPanel.main(null);
        ExampleTest.waitAndTerminate(2000);

        AppRtsCursor.main(null);
        ExampleTest.waitAndTerminate(2000);

        AppRtsFog.main(null);
        ExampleTest.waitAndTerminate(3000);

        AppRtsSkills.main(null);
        ExampleTest.waitAndTerminate(1000);
    }
}
