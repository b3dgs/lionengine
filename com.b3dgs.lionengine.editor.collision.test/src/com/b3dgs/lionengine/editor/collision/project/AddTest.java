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
package com.b3dgs.lionengine.editor.collision.project;

import java.io.File;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.b3dgs.lionengine.editor.UtilEditorTests;
import com.b3dgs.lionengine.editor.collision.UtilNl;
import com.b3dgs.lionengine.editor.project.ImportProjectTest;

/**
 * Test the collision add action.
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class AddTest
{
    private static final SWTBot BOT = new SWTBot();

    /**
     * Test the collision add.
     */
    @Test
    public void testCollisionAdd()
    {
        final File projectFolder = ImportProjectTest.createProject(BOT, getClass());

        BOT.tree(0)
           .getTreeItem(projectFolder.getName())
           .getNode("resources")
           .contextMenu(UtilNl.get("menu.collisions-add"))
           .click();

        BOT.waitUntil(Conditions.shellIsActive(Messages.Title));
        BOT.shell(Messages.Title).bot().button(IDialogConstants.OK_LABEL).click();

        UtilEditorTests.waitResourcesCopied(BOT, projectFolder, 2);
    }
}
