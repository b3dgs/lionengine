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
package com.b3dgs.lionengine.editor.collision.properties;

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
import com.b3dgs.lionengine.editor.collision.UtilNl;
import com.b3dgs.lionengine.editor.project.ImportProjectTest;
import com.b3dgs.lionengine.game.feature.FeaturableConfig;

/**
 * Test the enable collision action.
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class EnableCollisionTest
{
    private static final SWTBot BOT = new SWTBot();

    /**
     * Test the collision enable.
     */
    @Test
    public void testCollisionEnable()
    {
        final File projectFolder = ImportProjectTest.createProject(BOT, getClass());

        BOT.tree(0)
           .getTreeItem(projectFolder.getName())
           .getNode("resources")
           .contextMenu(com.b3dgs.lionengine.editor.object.UtilNl.get("menu.object-add"))
           .click();

        BOT.waitUntil(Conditions.shellIsActive(com.b3dgs.lionengine.editor.object.project.Messages.Title));
        BOT.shell(com.b3dgs.lionengine.editor.object.project.Messages.Title)
           .bot()
           .button(IDialogConstants.OK_LABEL)
           .click();

        UtilEditorTests.waitResourcesCopied(BOT, projectFolder, 2);

        final File file = new File(projectFolder, UtilFolder.getPath("resources", FeaturableConfig.DEFAULT_FILENAME));

        Assert.assertTrue(file.getPath(), file.isFile());

        BOT.tree(0)
           .getTreeItem(projectFolder.getName())
           .getNode("resources")
           .getNode(FeaturableConfig.DEFAULT_FILENAME)
           .click();

        BOT.tree(1).contextMenu(UtilNl.get("menu.collisions-enable")).click();
        BOT.tree(1).getTreeItem(Messages.Collisions).doubleClick();

        BOT.waitUntil(Conditions.shellIsActive(com.b3dgs.lionengine.editor.collision.object.Messages.Title));

        Assert.assertTrue(BOT.activeShell().bot().tree().hasItems());

        // Add Collision

        BOT.activeShell().bot().toolbarButtonWithTooltip(com.b3dgs.lionengine.editor.Messages.ObjectList_Add).click();

        BOT.waitUntil(Conditions.shellIsActive(com.b3dgs.lionengine.editor.Messages.ObjectList_AddObject_Title));
        BOT.shell(com.b3dgs.lionengine.editor.Messages.ObjectList_AddObject_Title)
           .bot()
           .button(IDialogConstants.OK_LABEL)
           .click();

        Assert.assertTrue(BOT.activeShell().bot().tree().hasItems());

        // Remove Collision

        BOT.activeShell().bot().tree().select(1);

        BOT.activeShell()
           .bot()
           .toolbarButtonWithTooltip(com.b3dgs.lionengine.editor.Messages.ObjectList_Remove)
           .click();

        BOT.waitUntil(Conditions.shellIsActive("*" + com.b3dgs.lionengine.editor.collision.object.Messages.Title));
        Assert.assertTrue(BOT.activeShell().bot().tree().hasItems());

        BOT.activeShell().bot().button(com.b3dgs.lionengine.editor.dialog.Messages.Exit).click();
    }
}
