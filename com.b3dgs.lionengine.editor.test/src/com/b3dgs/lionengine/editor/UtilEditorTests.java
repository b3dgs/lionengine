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
package com.b3dgs.lionengine.editor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.eclipse.core.runtime.Platform;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.editor.project.Project;
import com.b3dgs.lionengine.editor.project.ProjectModel;

/**
 * Utility related to editor test.
 */
public class UtilEditorTests
{
    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(UtilEditorTests.class);

    /**
     * Copy test resource file.
     * 
     * @param plugin The plugin name.
     * @param media The media to copy.
     * @return The copied file.
     * @throws LionEngineException If unable to copy file.
     */
    public static File copy(String plugin, Media media)
    {
        try
        {
            final File file = new File(ProjectModel.INSTANCE.getProject().getResourcesPath(), media.getPath());
            if (!file.exists())
            {
                Files.copy(Platform.getBundle(Activator.PLUGIN_ID + plugin + ".test")
                                   .getResource(ProjectModel.INSTANCE.getProject().getResources()
                                                + Constant.SLASH
                                                + media.getPath())
                                   .openConnection()
                                   .getInputStream(),
                           file.toPath());
                LOGGER.info("Created: {}", file);
            }
            return file;
        }
        catch (final IOException | NullPointerException exception)
        {
            throw new LionEngineException(exception, media);
        }
    }

    /**
     * Get media from current project.
     * 
     * @param path The media path from resource folder.
     * @return The media found.
     */
    public static Media getMedia(String path)
    {
        final Project project = ProjectModel.INSTANCE.getProject();
        final File file = new File(project.getResourcesPath(), path);
        return project.getResourceMedia(file);
    }

    /**
     * Wait for resources to be copied.
     * 
     * @param bot The bot reference.
     * @param projectFolder The project folder.
     * @param resources The expected resources count.
     */
    public static void waitResourcesCopied(SWTBot bot, File projectFolder, int resources)
    {
        bot.waitUntil(new DefaultCondition()
        {
            private int count;

            @Override
            public boolean test() throws Exception
            {
                count = bot.tree(0).getTreeItem(projectFolder.getName()).getNode("resources").rowCount();
                return count == resources;
            }

            @Override
            public String getFailureMessage()
            {
                return "Missing tree items: " + count + " / " + resources;
            }
        });
    }
}
