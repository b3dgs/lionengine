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
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.keyboard.Keystrokes;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCanvas;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.b3dgs.lionengine.editor.UtilEditorTests;
import com.b3dgs.lionengine.editor.object.UtilNl;
import com.b3dgs.lionengine.editor.project.ImportProjectTest;
import com.b3dgs.lionengine.editor.utility.UtilPart;
import com.b3dgs.lionengine.editor.world.view.WorldPart;
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

        BOT.waitUntil(Conditions.shellIsActive(Messages.Title));
        BOT.activeShell().pressShortcut(Keystrokes.CR);

        UtilEditorTests.waitResourcesCopied(BOT, projectFolder, 2);

        final File file = new File(projectFolder, UtilFolder.getPath("resources", FeaturableConfig.DEFAULT_FILENAME));

        Assert.assertTrue(file.getPath(), file.isFile());

        BOT.tree(0)
           .getTreeItem(projectFolder.getName())
           .getNode("resources")
           .getNode(file.getName())
           .contextMenu(UtilNl.get("menu.object-delete"))
           .click();

        BOT.sleep(500);
        BOT.activeShell().pressShortcut(Keystrokes.CR);
        UtilEditorTests.waitResourcesCopied(BOT, projectFolder, 1);

        Assert.assertFalse(file.getPath(), file.isFile());
    }

    /**
     * Test the object add on world, move and remove.
     */
    @Test
    public void testObjectWorld()
    {
        final File projectFolder = ImportProjectTest.createProject(BOT, getClass());

        BOT.tree(0)
           .getTreeItem(projectFolder.getName())
           .getNode("resources")
           .contextMenu(UtilNl.get("menu.object-add"))
           .click();

        BOT.waitUntil(Conditions.shellIsActive(Messages.Title));
        BOT.activeShell().pressShortcut(Keystrokes.CR);
        UtilEditorTests.waitResourcesCopied(BOT, projectFolder, 2);

        final File file = new File(projectFolder, UtilFolder.getPath("resources", FeaturableConfig.DEFAULT_FILENAME));

        Assert.assertTrue(file.getPath(), file.isFile());

        BOT.tree(0).getTreeItem(projectFolder.getName()).getNode("resources").getNode(file.getName()).click();

        final WorldPart world = UtilPart.getPart(WorldPart.ID, WorldPart.class);
        final AtomicReference<Rectangle> view = new AtomicReference<>();
        BOT.activeShell().display.syncExec(() -> view.set(world.getView()));

        final SWTBotCanvas canvas = BOT.canvas(1);

        // Place object clicking inside world
        canvas.click(view.get().x, view.get().y);

        // Select placed object (bottom left)
        canvas.click(view.get().x + 10, view.get().y + view.get().height - 10);

        openAndCloseProperty(BOT, com.b3dgs.lionengine.editor.object.properties.clazz.Messages.Class);
        openAndCloseProperty(BOT, com.b3dgs.lionengine.editor.object.properties.setup.Messages.Setup);
    }

    /**
     * Open property dialog and close.
     * 
     * @param bot The bot used.
     * @param property The property key.
     */
    private void openAndCloseProperty(SWTBot bot, String property)
    {
        bot.tree(1).getTreeItem(property).doubleClick();
        bot.waitUntil(Conditions.shellIsActive(com.b3dgs.lionengine.editor.dialog.combo.Messages.Title));
        final SWTBotShell shellProperties = bot.activeShell();
        shellProperties.bot().button(com.b3dgs.lionengine.editor.dialog.Messages.Finish).click();
        bot.waitUntil(Conditions.shellCloses(shellProperties));
    }
}
