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
package com.b3dgs.lionengine.editor.map;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.editor.UtilEditorTests;
import com.b3dgs.lionengine.editor.map.sheet.extract.Messages;
import com.b3dgs.lionengine.editor.project.ImportProjectTest;
import com.b3dgs.lionengine.editor.project.Project;
import com.b3dgs.lionengine.editor.project.ProjectModel;
import com.b3dgs.lionengine.game.feature.tile.map.TileSheetsConfig;

/**
 * Test the sheets extraction dialog.
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class SheetsExtractTest
{
    private static final SWTBot BOT = new SWTBot();

    /**
     * Fill extract dialog with test values.
     * 
     * @param bot The bot used.
     * @return The main shell.
     */
    static SWTBotShell fillDialog(SWTBot bot)
    {
        bot.waitUntil(Conditions.shellIsActive(Messages.Title));
        final SWTBotShell shellMain = bot.shell(Messages.Title);

        bot.buttonWithTooltip(com.b3dgs.lionengine.editor.widget.levelrip.Messages.AddLevelRip).click();

        bot.waitUntil(Conditions.shellIsActive(com.b3dgs.lionengine.editor.dialog.Messages.ResourceDialog_Title));
        final SWTBotShell shellResources = bot.shell(com.b3dgs.lionengine.editor.dialog.Messages.ResourceDialog_Title);

        final SWTBot levelRips = shellResources.bot();
        final Project project = ProjectModel.INSTANCE.getProject();
        levelRips.tree(0).getTreeItem(project.getName()).getNode(project.getResources()).getNode("level.png").select();
        levelRips.button(com.b3dgs.lionengine.editor.dialog.Messages.Finish).click();

        bot.waitUntil(Conditions.shellCloses(shellResources));

        bot.textWithTooltip(Messages.TileWidth).setText("16");
        bot.textWithTooltip(Messages.TileHeight).setText("16");
        bot.button(com.b3dgs.lionengine.editor.dialog.Messages.Finish).click();

        return shellMain;
    }

    /**
     * Check produced files.
     */
    static void checkResult()
    {
        final Media media = UtilEditorTests.getMedia(TileSheetsConfig.FILENAME);
        final TileSheetsConfig config = TileSheetsConfig.imports(media);

        Assert.assertEquals(16, config.getTileWidth());
        Assert.assertEquals(16, config.getTileHeight());
        Assert.assertTrue(config.getSheets().size() > 0);
        for (final String sheet : config.getSheets())
        {
            Assert.assertTrue(UtilEditorTests.getMedia(sheet).exists());
        }
    }

    /**
     * Test the sheets extraction.
     */
    @Test
    public void testSheetsExtractDialog()
    {
        Assert.assertNotNull(ImportProjectTest.createProject(BOT, getClass()));

        BOT.menu(UtilNl.get("menu.map.extract-sheets"), true).click();
        fillDialog(BOT);

        checkResult();
    }
}
