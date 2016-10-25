/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.util.UtilFolder;

/**
 * File modification watcher for project tree.
 */
public final class FolderModificationWatcher
{
    /** Default check time in milliseconds. */
    private static final int CHECK_TIME = 1000;
    /** Watcher already started error. */
    private static final String ERROR_STARTED = "Watcher already started !";

    /** New tasks. */
    final Queue<Task> newTasks = new ConcurrentLinkedQueue<>();
    /** Thread executor service. */
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    /** Future task. */
    private Future<?> future;
    /** Started flag. */
    private boolean started;

    /**
     * Create the watcher.
     */
    public FolderModificationWatcher()
    {
        super();
    }

    /**
     * Listen to directory modification.
     * 
     * @param project The project to watch.
     * @param tree The tree viewer reference.
     * @param creator The creator reference.
     * @throws LionEngineException If already started.
     */
    public synchronized void start(Project project, Tree tree, ProjectTreeCreator creator)
    {
        if (started)
        {
            throw new LionEngineException(ERROR_STARTED);
        }
        future = executor.submit(new Watcher(project, tree, creator));
        started = true;
    }

    /**
     * Stop the watcher.
     */
    public synchronized void stop()
    {
        if (future != null)
        {
            future.cancel(true);
        }
        future = null;
        started = false;
    }

    /**
     * Terminate definitely the watcher.
     */
    public synchronized void terminate()
    {
        if (started)
        {
            stop();
        }
        executor.shutdownNow();
    }

    /**
     * Folders modification watcher.
     */
    private final class Watcher implements Runnable
    {
        /** Tasks. */
        private final Collection<Task> tasks = new HashSet<>();
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
                tasks.add(new Task(project, directory, creator));
            }
            catch (final IOException exception)
            {
                Verbose.exception(exception);
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
                    Verbose.exception(exception);
                    continue;
                }
            }
        }

        /*
         * Runnable
         */

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
                catch (final InterruptedException exception)
                {
                    Thread.currentThread().interrupt();
                    Verbose.exception(exception);
                    break;
                }
            }
            closeTasks();
            tasks.clear();
        }
    }

    /**
     * Represents the watch task for a specific folder.
     */
    private final class Task
    {
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
         * @throws IOException If error.
         */
        Task(Project project, Path folder, ProjectTreeCreator creator) throws IOException
        {
            this.project = project;
            this.folder = folder;
            this.creator = creator;
            service = folder.getFileSystem().newWatchService();
            watcher = folder.register(service,
                                      StandardWatchEventKinds.ENTRY_CREATE,
                                      StandardWatchEventKinds.ENTRY_DELETE);
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
            if (data instanceof TreeItem)
            {
                final TreeItem parent = (TreeItem) data;
                creator.checkPath(path, tree, parent);
                if (path.isDirectory())
                {
                    try
                    {
                        newTasks.add(new Task(project, path.toPath(), creator));
                    }
                    catch (final IOException exception)
                    {
                        Verbose.exception(exception);
                    }
                }
            }
        }

        /**
         * Dispose item from its name.
         * 
         * @param tree The tree reference.
         * @param name The item name.
         */
        private void disposeItem(Tree tree, String name)
        {
            if (!tree.isDisposed())
            {
                final Object data = tree.getData(name);
                if (data instanceof TreeItem)
                {
                    final TreeItem item = (TreeItem) data;
                    item.dispose();
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
}
