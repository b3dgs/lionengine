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
package com.b3dgs.lionengine.editor.pathfinding.project;

import java.io.File;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.b3dgs.lionengine.UtilFolder;
import com.b3dgs.lionengine.editor.UtilEditorTests;
import com.b3dgs.lionengine.editor.pathfinding.UtilNl;
import com.b3dgs.lionengine.editor.project.ImportProjectTest;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.PathfindingConfig;

/**
 * Test the pathfinding add action.
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class AddPathfindingTest
{
    private static final SWTBot BOT = new SWTBot();

    /**
     * Test the pathfinding add.
     */
    @Test
    public void testPathfindingAdd()
    {
        final File projectFolder = ImportProjectTest.createProject(BOT, getClass());

        BOT.tree(0)
           .getTreeItem(projectFolder.getName())
           .getNode("resources")
           .contextMenu(UtilNl.get("menu.pathfinding-add"))
           .click();

        BOT.waitUntil(Conditions.shellIsActive(Messages.Title));
        BOT.shell(Messages.Title).bot().button(IDialogConstants.OK_LABEL).click();

        UtilEditorTests.waitResourcesCopied(BOT, projectFolder, 2);

        final File file = new File(projectFolder, UtilFolder.getPath("resources", PathfindingConfig.FILENAME));

        Assert.assertTrue(file.getPath(), file.isFile());

        BOT.tree(0).getTreeItem(projectFolder.getName()).getNode("resources").getNode(file.getName()).doubleClick();

        BOT.waitUntil(Conditions.shellIsActive(com.b3dgs.lionengine.editor.pathfinding.editor.Messages.Title));

        Assert.assertFalse(BOT.activeShell().bot().tree(0).hasItems());

        // Add Pathfinding

        BOT.activeShell()
           .bot()
           .toolbarButtonWithTooltip(com.b3dgs.lionengine.editor.Messages.ObjectList_Add, 0)
           .click();

        BOT.waitUntil(Conditions.shellIsActive(com.b3dgs.lionengine.editor.Messages.ObjectList_AddObject_Title));
        BOT.shell(com.b3dgs.lionengine.editor.Messages.ObjectList_AddObject_Title)
           .bot()
           .button(IDialogConstants.OK_LABEL)
           .click();

        Assert.assertTrue(BOT.activeShell().bot().tree(0).hasItems());

        // Remove Pathfinding

        BOT.activeShell().bot().tree(0).select(0);

        BOT.activeShell()
           .bot()
           .toolbarButtonWithTooltip(com.b3dgs.lionengine.editor.Messages.ObjectList_Remove, 0)
           .click();

        BOT.waitUntil(Conditions.shellIsActive("*" + com.b3dgs.lionengine.editor.pathfinding.editor.Messages.Title));

        Assert.assertFalse(BOT.activeShell().bot().tree(0).hasItems());

        // Add Group

        BOT.activeShell()
           .bot()
           .toolbarButtonWithTooltip(com.b3dgs.lionengine.editor.Messages.ObjectList_Add, 1)
           .click();

        BOT.waitUntil(Conditions.shellIsActive(com.b3dgs.lionengine.editor.Messages.ObjectList_AddObject_Title));
        BOT.shell(com.b3dgs.lionengine.editor.Messages.ObjectList_AddObject_Title)
           .bot()
           .button(IDialogConstants.OK_LABEL)
           .click();

        Assert.assertTrue(BOT.activeShell().bot().tree(1).hasItems());

        // Remove Group

        BOT.activeShell().bot().tree(1).select(0);

        BOT.activeShell()
           .bot()
           .toolbarButtonWithTooltip(com.b3dgs.lionengine.editor.Messages.ObjectList_Remove, 1)
           .click();

        BOT.waitUntil(Conditions.shellIsActive("*" + com.b3dgs.lionengine.editor.pathfinding.editor.Messages.Title));
        Assert.assertFalse(BOT.activeShell().bot().tree(1).hasItems());

        BOT.activeShell().bot().button(com.b3dgs.lionengine.editor.dialog.Messages.Finish).click();
    }
}
