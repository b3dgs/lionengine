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
package com.b3dgs.lionengine.editor.project;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;
import java.util.Queue;

import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents the watch task for a specific folder.
 */
final class Task
{
    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(Task.class);

    /**
     * Dispose item from its name.
     * 
     * @param tree The tree reference.
     * @param name The item name.
     */
    private static void disposeItem(Tree tree, String name)
    {
        if (!tree.isDisposed())
        {
            final Object data = tree.getData(name);
            if (data instanceof final TreeItem item)
            {
                item.dispose();
            }
        }
    }

    /** New tasks. */
    private final Queue<Task> newTasks;
    /** Root folder. */
    private final Project project;
    /** Current folder. */
    private final Path folder;
    /** Creator reference. */
    private final ProjectTreeCreator creator;
    /** Watcher reference. */
    private final WatchKey watcher;
    /** Watcher service. */
    private final WatchService service;

    /**
     * Create the task.
     * 
     * @param project The project folder.
     * @param folder The current folder.
     * @param creator The creator reference.
     * @param newTasks The news tasks queue reference.
     * @throws IOException If error.
     */
    Task(Project project, Path folder, ProjectTreeCreator creator, Queue<Task> newTasks) throws IOException
    {
        this.project = project;
        this.folder = folder;
        this.creator = creator;
        this.newTasks = newTasks;
        service = folder.getFileSystem().newWatchService();
        watcher = folder.register(service, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE);
    }

    /**
     * Process the watch.
     * 
     * @param tree The tree reference.
     */
    public synchronized void proceed(Tree tree)
    {
        final List<WatchEvent<?>> events = watcher.pollEvents();
        for (final WatchEvent<?> event : events)
        {
            final Object object = event.context();
            if (object instanceof Path)
            {
                final Kind<?> kind = event.kind();
                if (StandardWatchEventKinds.ENTRY_CREATE.equals(kind))
                {
                    onCreated(new File(folder.toFile(), object.toString()), tree);
                }
                else if (StandardWatchEventKinds.ENTRY_DELETE.equals(kind))
                {
                    onDeleted(tree, object.toString());
                }
            }
        }
    }

    /**
     * Close the service.
     * 
     * @throws IOException If error.
     */
    public synchronized void close() throws IOException
    {
        service.close();
    }

    /**
     * Called on created element.
     * 
     * @param path The created element path.
     * @param tree The tree reference.
     * @param keyParent The parent key.
     */
    private void onCreated(File path, Tree tree, String keyParent)
    {
        final Object data = tree.getData(keyParent);
        if (data instanceof final TreeItem parent)
        {
            creator.checkPath(path, tree, parent);
            if (path.isDirectory())
            {
                try
                {
                    newTasks.add(new Task(project, path.toPath(), creator, newTasks));
                }
                catch (final IOException exception)
                {
                    LOGGER.error("onCreated error", exception);
                }
            }
        }
    }

    /**
     * Case of created item.
     * 
     * @param path The created item.
     * @param tree The tree reference.
     */
    private void onCreated(final File path, final Tree tree)
    {
        final File parent = path.getParentFile();
        if (parent != null)
        {
            final String keyParent = project.getResourceMedia(path).getParentPath();
            tree.getDisplay().asyncExec(() -> onCreated(path, tree, keyParent));
        }
    }

    /**
     * Case of deleted item.
     * 
     * @param tree The tree reference.
     * @param filename The deleted item.
     */
    private void onDeleted(final Tree tree, final String filename)
    {
        final Path file = folder.resolve(filename);
        final String full = file.toFile().getAbsolutePath();
        final int prefix = project.getResourcesPath().getAbsolutePath().length() + 1;
        if (full.length() > prefix)
        {
            final String simple = full.substring(prefix);
            tree.getDisplay().asyncExec(() -> disposeItem(tree, simple));
        }
    }
}
