/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.editor;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.b3dgs.lionengine.Engine;

/**
 * Test the editor activator.
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class ActivatorTest
{
    private static final SWTBot BOT = new SWTBot();

    /**
     * Test the editor start.
     */
    @Test
    public void testActivator()
    {
        Assert.assertTrue(Engine.isStarted());
        Assert.assertEquals(Engine.getProgramName(), Activator.PLUGIN_NAME);
        Assert.assertEquals(Engine.getProgramVersion(), Activator.PLUGIN_VERSION);
        Assert.assertEquals(Activator.PLUGIN_NAME, BOT.activeShell().getText());
    }
}
