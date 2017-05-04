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
package com.b3dgs.lionengine.editor.project;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.eclipse.core.runtime.Platform;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.editor.Activator;
import com.b3dgs.lionengine.editor.UtilNl;
import com.b3dgs.lionengine.editor.dialog.Messages;
import com.b3dgs.lionengine.util.UtilFolder;

/**
 * Test the import project.
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class ImportProjectTest
{
    private static final SWTBot BOT = new SWTBot();

    /**
     * Create the test project.
     * 
     * @param bot The bot reference.
     * @param clazz The test class.
     * @return The project folder.
     */
    public static File createProject(SWTBot bot, Class<?> clazz)
    {
        final File projectFolder = copyProjectToTemp(clazz);
        bot.menu(UtilNl.get("menu.file.import-project"), true).click();
        bot.waitUntil(Conditions.shellIsActive(UtilNl.get("menu.file.import-project")));

        final SWTBotShell shell = bot.shell(UtilNl.get("menu.file.import-project"));
        final SWTBot dialog = shell.activate().bot();

        dialog.text(1).setText(projectFolder.getAbsolutePath());
        dialog.text(2).setText("bin");
        dialog.text(3).setText("bin");
        dialog.text(4).setText("resources");

        dialog.button(Messages.Finish).click();

        bot.waitUntil(Conditions.shellCloses(shell));

        return projectFolder;
    }

    /**
     * Copy test project to temp directory.
     * 
     * @param clazz The test class.
     * @return The project folder.
     * @throws LionEngineException If unable to create files.
     */
    private static File copyProjectToTemp(Class<?> clazz)
    {
        try
        {
            final File folder = Files.createTempDirectory("project_" + clazz.getSimpleName() + "_").toFile();

            Assert.assertTrue(new File(folder, "bin").mkdir());
            Assert.assertTrue(new File(folder, "resources").mkdir());

            Files.copy(Platform.getBundle(Activator.PLUGIN_ID + ".test")
                               .getResource("resources/level.png")
                               .openConnection()
                               .getInputStream(),
                       new File(folder, UtilFolder.getPath("resources", "level.png")).toPath());

            return folder;
        }
        catch (final IOException exception)
        {
            throw new LionEngineException(exception);
        }
    }

    /**
     * Test the project import from dialog.
     */
    @Test
    public void testImportProjectFromDialog()
    {
        final File projectFolder = createProject(BOT, getClass());
        final Project project = ProjectModel.INSTANCE.getProject();

        Assert.assertEquals(projectFolder.getName(), project.getName());
        Assert.assertEquals("bin", project.getClasses());
        Assert.assertEquals("resources", project.getResources());

        BOT.tree(0).getTreeItem(projectFolder.getName()).doubleClick();

        final File properties = new File(projectFolder, ProjectFactory.PROPERTIES_FILE);

        Assert.assertTrue(properties.isFile());
    }
}
