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
package com.b3dgs.lionengine.editor.map.world;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.keyboard.Keystrokes;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCanvas;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.editor.UtilEditorTests;
import com.b3dgs.lionengine.editor.map.UtilNl;
import com.b3dgs.lionengine.editor.map.imports.MapImportDialogTest;
import com.b3dgs.lionengine.editor.project.ImportProjectTest;
import com.b3dgs.lionengine.editor.world.WorldModel;
import com.b3dgs.lionengine.editor.world.updater.WorldNavigation;
import com.b3dgs.lionengine.game.feature.Camera;

/**
 * Test the world navigation with imported map.
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class WorldNavigationTest
{
    private static final SWTBot BOT = new SWTBot();

    /**
     * Test the world navigation.
     */
    @Test
    public void testWorldNavigation()
    {
        Assert.assertNotNull(ImportProjectTest.createProject(BOT, getClass()));
        UtilEditorTests.copy(".map", Medias.create("sheets.xml"));
        UtilEditorTests.copy(".map", Medias.create("groups.xml"));
        UtilEditorTests.copy(".map", Medias.create("0.png"));

        BOT.menu(UtilNl.get("menu.map.import"), true).click();
        MapImportDialogTest.fillDialog(BOT);

        final Camera camera = WorldModel.INSTANCE.getCamera();

        Assert.assertEquals(0.0, camera.getX(), 0.0);
        Assert.assertEquals(0.0, camera.getY(), 0.0);

        final SWTBotCanvas canvas = BOT.canvas(1);
        canvas.setFocus();
        canvas.pressShortcut(Keystrokes.RIGHT);

        BOT.sleep(500);

        Assert.assertEquals(WorldNavigation.GRID_MOVEMENT_SENSIBILITY * 16.0, camera.getX(), 0.0);
        Assert.assertEquals(0.0, camera.getY(), 0.0);

        canvas.pressShortcut(Keystrokes.LEFT);

        BOT.sleep(500);

        Assert.assertEquals(0.0, camera.getX(), 0.0);
        Assert.assertEquals(0.0, camera.getY(), 0.0);
    }
}
