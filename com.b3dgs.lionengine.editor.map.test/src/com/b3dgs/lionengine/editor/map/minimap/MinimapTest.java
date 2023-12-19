/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.editor.map.minimap;

import java.io.File;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.keyboard.Keystrokes;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.editor.UtilEditorTests;
import com.b3dgs.lionengine.editor.map.UtilNl;
import com.b3dgs.lionengine.editor.map.imports.MapImportDialogTest;
import com.b3dgs.lionengine.editor.project.ImportProjectTest;
import com.b3dgs.lionengine.editor.world.WorldModel;
import com.b3dgs.lionengine.game.feature.tile.map.MinimapConfig;

/**
 * Test the minimap generation.
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class MinimapTest
{
    private static final SWTBot BOT = new SWTBot();
    private static final double PRECISION = 0.000000001;

    /**
     * Test the minimap generation.
     */
    @Test
    public void testMinimap()
    {
        final File projectFolder = ImportProjectTest.createProject(BOT, getClass());
        Assert.assertNotNull(projectFolder);
        UtilEditorTests.copy(".map", Medias.create("sheets.xml"));
        UtilEditorTests.copy(".map", Medias.create("groups.xml"));
        UtilEditorTests.copy(".map", Medias.create("0.png"));

        UtilEditorTests.waitResourcesCopied(BOT, projectFolder, 4);

        BOT.menu(UtilNl.get("menu.map.import"), true).click();
        MapImportDialogTest.fillDialog(BOT);

        Assert.assertEquals(0.0, WorldModel.INSTANCE.getCamera().getX(), PRECISION);

        BOT.tree(0)
           .getTreeItem(projectFolder.getName())
           .getNode("resources")
           .contextMenu(UtilNl.get("menu.minimap-add"))
           .click();
        BOT.sleep(500);

        BOT.activeShell().pressShortcut(Keystrokes.CR);

        UtilEditorTests.waitResourcesCopied(BOT, projectFolder, 5);

        BOT.tree(0)
           .getTreeItem(projectFolder.getName())
           .getNode("resources")
           .getNode(MinimapConfig.FILENAME)
           .doubleClick();
        BOT.waitUntil(Conditions.shellIsActive(com.b3dgs.lionengine.editor.map.minimap.editor.Messages.Title));
        final SWTBotShell shellEdit = BOT.activeShell();
        BOT.button(com.b3dgs.lionengine.editor.dialog.Messages.Finish).click();
        BOT.waitUntil(Conditions.shellCloses(shellEdit));

        BOT.menu(UtilNl.get("menu.view.minimap"), true).click();
        BOT.waitUntil(Conditions.shellIsActive(com.b3dgs.lionengine.editor.map.minimap.menu.Messages.Title));

        final SWTBotShell shell = BOT.activeShell();
        BOT.button(com.b3dgs.lionengine.editor.map.minimap.menu.Messages.Generate).click();
        BOT.button(com.b3dgs.lionengine.editor.dialog.Messages.Finish).click();
        BOT.waitUntil(Conditions.shellCloses(shell));

        BOT.waitUntil(Conditions.shellIsActive(com.b3dgs.lionengine.editor.map.minimap.menu.Messages.Title));

        final SWTBotShell minimap = BOT.activeShell();

        // Click on minimap
        BOT.canvasWithTooltip(com.b3dgs.lionengine.editor.map.minimap.menu.Messages.Title).click(10, 1);
        BOT.sleep(500);

        Assert.assertEquals(160.0, WorldModel.INSTANCE.getCamera().getX(), PRECISION);

        BOT.canvasWithTooltip(com.b3dgs.lionengine.editor.map.minimap.menu.Messages.Title).click(0, 0);
        BOT.sleep(500);

        Assert.assertEquals(0.0, WorldModel.INSTANCE.getCamera().getX(), PRECISION);

        minimap.pressShortcut(Keystrokes.ESC);
        BOT.waitUntil(Conditions.shellCloses(minimap));
    }
}
