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
import com.b3dgs.lionengine.UtilFile;
import com.b3dgs.lionengine.Verbose;

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
        // Nothing to do
    }

    /**
     * Listen to directory modification.
     * 
     * @param root The root path.
     * @param tree The tree viewer reference.
     * @param creator The creator reference.
     * @throws LionEngineException If already started.
     */
    public synchronized void start(Path root, Tree tree, ProjectTreeCreator creator)
    {
        if (started)
        {
            throw new LionEngineException(ERROR_STARTED);
        }
        future = executor.submit(new Watcher(root, tree, creator));
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
        private final Path root;
        /** Tree reference. */
        private final Tree tree;
        /** Creator reference. */
        private final ProjectTreeCreator creator;

        /**
         * Create the watcher.
         * 
         * @param root The root folder.
         * @param tree The tree reference.
         * @param creator The creator reference.
         */
        Watcher(Path root, Tree tree, ProjectTreeCreator creator)
        {
            this.root = root;
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
            for (final File current : UtilFile.getDirectories(directory))
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
                tasks.add(new Task(root, directory, tree, creator));
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
            createTask(root);
            createWatchers(root.toFile());

            while (!Thread.currentThread().isInterrupted())
            {
                while (newTasks.peek() != null)
                {
                    tasks.add(newTasks.poll());
                }
                for (final Task task : tasks)
                {
                    task.proceed();
                }
                try
                {
                    Thread.sleep(CHECK_TIME);
                }
                catch (final InterruptedException exception)
                {
                    Thread.currentThread().interrupt();
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
        private final Path root;
        /** Current folder. */
        private final Path folder;
        /** Tree. */
        private final Tree tree;
        /** Creator reference. */
        private final ProjectTreeCreator creator;
        /** Watcher reference. */
        private final WatchKey watcher;
        /** Watcher service. */
        private final WatchService service;

        /**
         * Create the task.
         * 
         * @param root The root folder.
         * @param folder The current folder.
         * @param tree The tree reference.
         * @param creator The creator reference.
         * @throws IOException If error.
         */
        Task(Path root, Path folder, Tree tree, ProjectTreeCreator creator) throws IOException
        {
            this.root = root;
            this.folder = folder;
            this.tree = tree;
            this.creator = creator;
            service = folder.getFileSystem().newWatchService();
            watcher = folder.register(service,
                                      StandardWatchEventKinds.ENTRY_CREATE,
                                      StandardWatchEventKinds.ENTRY_DELETE);
        }

        /**
         * Process the watch.
         */
        public synchronized void proceed()
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
                        onCreated(new File(folder.toFile(), object.toString()));
                    }
                    else if (StandardWatchEventKinds.ENTRY_DELETE.equals(kind))
                    {
                        onDeleted(object.toString());
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
         * @param keyParent The parent key.
         */
        private void onCreated(File path, String keyParent)
        {
            final Object data = tree.getData(keyParent);
            if (data != null && data instanceof TreeItem)
            {
                final TreeItem parent = (TreeItem) data;
                creator.checkPath(path, parent);
                if (path.isDirectory())
                {
                    try
                    {
                        newTasks.add(new Task(root, path.toPath(), tree, creator));
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
         * @param name The item name.
         */
        private void disposeItem(String name)
        {
            if (!tree.isDisposed())
            {
                final Object data = tree.getData(name);
                if (data != null && data instanceof TreeItem)
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
         */
        private void onCreated(final File path)
        {
            final File parent = path.getParentFile();
            if (parent != null)
            {
                final String keyParent = Project.getActive().getResourceMedia(path).getParentPath();
                tree.getDisplay().asyncExec(() -> onCreated(path, keyParent));
            }
        }

        /**
         * Case of deleted item.
         * 
         * @param filename The deleted item.
         */
        private void onDeleted(final String filename)
        {
            final Path file = folder.resolve(filename);
            final String full = file.toFile().getAbsolutePath();
            final int prefix = root.toFile().getAbsolutePath().length() + 1;
            if (full.length() > prefix)
            {
                final String simple = full.substring(prefix);
                tree.getDisplay().asyncExec(() -> disposeItem(simple));
            }
        }
    }
}
