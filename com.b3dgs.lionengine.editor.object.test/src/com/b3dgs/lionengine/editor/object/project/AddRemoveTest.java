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
package com.b3dgs.lionengine.editor.object.project;

import java.io.File;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.keyboard.Keystrokes;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.b3dgs.lionengine.editor.UtilTests;
import com.b3dgs.lionengine.editor.object.UtilNl;
import com.b3dgs.lionengine.editor.project.ImportProjectTest;
import com.b3dgs.lionengine.game.FeaturableConfig;
import com.b3dgs.lionengine.util.UtilFolder;

/**
 * Test the object add and remove action.
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class AddRemoveTest
{
    private static final SWTBot BOT = new SWTBot();

    /**
     * Test the object add and remove.
     */
    @Test
    public void testObjectAddRemove()
    {
        final File projectFolder = ImportProjectTest.createProject(BOT, getClass());

        BOT.tree(0)
           .getTreeItem(projectFolder.getName())
           .getNode("resources")
           .contextMenu(UtilNl.get("menu.object-add"))
           .click();

        BOT.activeShell().pressShortcut(Keystrokes.CR);
        UtilTests.waitResourcesCopied(BOT, projectFolder, 2);

        final File file = new File(projectFolder, UtilFolder.getPath("resources", FeaturableConfig.DEFAULT_FILENAME));

        Assert.assertTrue(file.getPath(), file.isFile());

        BOT.tree(0)
           .getTreeItem(projectFolder.getName())
           .getNode("resources")
           .getNode(file.getName())
           .contextMenu(UtilNl.get("menu.object-delete"))
           .click();

        UtilTests.waitResourcesCopied(BOT, projectFolder, 2);

        Assert.assertFalse(file.getPath(), file.isFile());
    }
}
