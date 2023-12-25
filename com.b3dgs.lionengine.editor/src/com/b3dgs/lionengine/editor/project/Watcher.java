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
package com.b3dgs.lionengine.editor.project;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.eclipse.swt.widgets.Tree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b3dgs.lionengine.UtilFolder;

/**
 * Folders modification watcher.
 */
final class Watcher implements Runnable
{
    /** Default check time in milliseconds. */
    private static final int CHECK_TIME = 500;
    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(Watcher.class);

    /** Tasks. */
    private final Collection<Task> tasks = new HashSet<>();
    /** New tasks. */
    private final Queue<Task> newTasks = new ConcurrentLinkedQueue<>();
    /** Root folder. */
    private final Project project;
    /** Tree reference. */
    private final Tree tree;
    /** Creator reference. */
    private final ProjectTreeCreator creator;

    /**
     * Create the watcher.
     * 
     * @param project The project reference.
     * @param tree The tree reference.
     * @param creator The creator reference.
     */
    Watcher(Project project, Tree tree, ProjectTreeCreator creator)
    {
        this.project = project;
        this.tree = tree;
        this.creator = creator;
    }

    /**
     * Create all sub watchers.
     * 
     * @param directory The directory root folder.
     */
    private void createWatchers(File directory)
    {
        for (final File current : UtilFolder.getDirectories(directory))
        {
            createTask(current.toPath());
            createWatchers(current);
        }
    }

    /**
     * Create the task for the specified directory.
     * 
     * @param directory The directory reference.
     */
    private void createTask(Path directory)
    {
        try
        {
            tasks.add(new Task(project, directory, creator, newTasks));
        }
        catch (final IOException exception)
        {
            LOGGER.error("createTask error", exception);
        }
    }

    /**
     * Close all task.
     */
    private void closeTasks()
    {
        for (final Task task : tasks)
        {
            try
            {
                task.close();
            }
            catch (final IOException exception)
            {
                LOGGER.error("closeTasks error", exception);
            }
        }
    }

    @Override
    public void run()
    {
        final File resourcesPath = project.getResourcesPath();
        createTask(resourcesPath.toPath());
        createWatchers(resourcesPath);

        while (!Thread.currentThread().isInterrupted())
        {
            while (newTasks.peek() != null)
            {
                tasks.add(newTasks.poll());
            }
            for (final Task task : tasks)
            {
                task.proceed(tree);
            }
            try
            {
                Thread.sleep(CHECK_TIME);
            }
            catch (@SuppressWarnings("unused") final InterruptedException exception)
            {
                Thread.currentThread().interrupt();
                break;
            }
        }
        closeTasks();
        tasks.clear();
    }
}
