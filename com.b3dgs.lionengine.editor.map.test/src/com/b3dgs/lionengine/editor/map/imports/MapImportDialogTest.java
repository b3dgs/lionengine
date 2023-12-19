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
package com.b3dgs.lionengine.editor.map.imports;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.editor.UtilEditorTests;
import com.b3dgs.lionengine.editor.map.UtilNl;
import com.b3dgs.lionengine.editor.project.ImportProjectTest;
import com.b3dgs.lionengine.editor.project.Project;
import com.b3dgs.lionengine.editor.project.ProjectModel;
import com.b3dgs.lionengine.editor.world.WorldModel;
import com.b3dgs.lionengine.helper.MapTileHelper;

/**
 * Test the map import dialog.
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class MapImportDialogTest
{
    private static final SWTBot BOT = new SWTBot();

    /**
     * Fill dialog with test values.
     * 
     * @param bot The bot used.
     */
    public static void fillDialog(SWTBot bot)
    {
        bot.waitUntil(Conditions.shellIsActive(Messages.Title));
        final SWTBotShell shellMain = bot.activeShell();

        bot.buttonWithTooltip(com.b3dgs.lionengine.editor.utility.control.Messages.Browse).click();

        bot.waitUntil(Conditions.shellIsActive(com.b3dgs.lionengine.editor.dialog.Messages.ResourceDialog_Title));
        final SWTBotShell shellResources = bot.activeShell();

        final SWTBot resources = bot.activeShell().bot();
        final Project project = ProjectModel.INSTANCE.getProject();
        resources.tree(0).getTreeItem(project.getName()).getNode(project.getResources()).getNode("level.png").select();
        resources.button(com.b3dgs.lionengine.editor.dialog.Messages.Finish).click();

        bot.waitUntil(Conditions.shellCloses(shellResources));

        bot.button(com.b3dgs.lionengine.editor.dialog.Messages.Finish).click();

        final SWTBotShell shellProgress = bot.activeShell();
        if (shellProgress.getText().equals(Messages.Title_Progress))
        {
            bot.waitWhile(Conditions.shellCloses(shellProgress));
        }

        bot.waitUntil(Conditions.shellCloses(shellMain));
    }

    /**
     * Check imported map.
     */
    private static void checkResult()
    {
        final MapTileHelper map = WorldModel.INSTANCE.getMap();

        Assert.assertNotNull(map);
        Assert.assertTrue(map.isCreated());
        Assert.assertEquals(16, map.getTileWidth());
        Assert.assertEquals(16, map.getTileHeight());
        Assert.assertEquals(1, map.getSheetsNumber());

        Assert.assertEquals(212, map.getInTileWidth());
        Assert.assertEquals(15, map.getInTileHeight());

        Assert.assertEquals(29, map.getTile(0, 0).getNumber());
    }

    /**
     * Test the map import dialog.
     */
    @Test
    public void testMapImportDialog()
    {
        Assert.assertNotNull(ImportProjectTest.createProject(BOT, getClass()));
        UtilEditorTests.copy(".map", Medias.create("sheets.xml"));
        UtilEditorTests.copy(".map", Medias.create("groups.xml"));
        UtilEditorTests.copy(".map", Medias.create("0.png"));

        BOT.menu(UtilNl.get("menu.map.import"), true).click();
        fillDialog(BOT);

        checkResult();
    }
}
