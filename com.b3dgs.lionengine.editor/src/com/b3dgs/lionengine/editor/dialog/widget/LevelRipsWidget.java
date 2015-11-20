/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.editor.dialog.widget;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.editor.ObjectList;
import com.b3dgs.lionengine.editor.dialog.map.sheets.extract.Messages;
import com.b3dgs.lionengine.editor.project.Project;
import com.b3dgs.lionengine.editor.utility.UtilButton;
import com.b3dgs.lionengine.editor.utility.UtilDialog;

/**
 * Widget that allows to select a list of level rips.
 */
public class LevelRipsWidget
{
    /** Level rip filter. */
    public static final String[] LEVEL_RIP_FILTER = new String[]
    {
        "*.bmp;*.png"
    };

    /** Listeners. */
    private final Collection<LevelRipsWidgetListener> listeners = new HashSet<>();
    /** Level rip medias lock. */
    private final Object mediasLock = new Object();
    /** Level rip medias (guarded by {@link #mediasLock}). */
    private final Collection<Media> medias = new HashSet<>();
    /** Level rips list. */
    private final Tree levelRips;
    /** Add level rip. */
    private Button addLevelRip;
    /** Remove level rip. */
    private Button removeLevelRip;

    /**
     * Create the widget.
     * 
     * @param parent The composite parent.
     */
    public LevelRipsWidget(Composite parent)
    {
        final Group area = new Group(parent, SWT.NONE);
        area.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        area.setLayout(new GridLayout(1, false));
        area.setText(Messages.RipsList);

        levelRips = new Tree(area, SWT.SINGLE);
        levelRips.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        levelRips.addDisposeListener(event -> listeners.clear());

        final Composite buttons = new Composite(area, SWT.NONE);
        buttons.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        buttons.setLayout(new GridLayout(3, false));

        final Label label = new Label(buttons, SWT.NONE);
        label.setText(Messages.AddRemoveRip);

        createButtonAdd(buttons);
        createButtonRemove(buttons);
    }

    /**
     * Add a level rip listener.
     * 
     * @param listener The listener reference.
     */
    public void addListener(LevelRipsWidgetListener listener)
    {
        listeners.add(listener);
    }

    /**
     * Remove a level rip listener.
     * 
     * @param listener The listener reference.
     */
    public void removeListener(LevelRipsWidgetListener listener)
    {
        listeners.remove(listener);
    }

    /**
     * Get the current level rips list as array of media.
     * 
     * @return The level rip medias.
     */
    public Media[] getLevelRips()
    {
        synchronized (mediasLock)
        {
            return medias.toArray(new Media[medias.size()]);
        }
    }

    /**
     * Create the add level rip button.
     * 
     * @param parent The composite parent.
     */
    private void createButtonAdd(Composite parent)
    {
        addLevelRip = new Button(parent, SWT.PUSH);
        addLevelRip.setImage(ObjectList.ICON_ADD);
        addLevelRip.setToolTipText(Messages.AddLevelRip);
        UtilButton.setAction(addLevelRip, () -> onAddLevelRip());
    }

    /**
     * Create the remove level rip button.
     * 
     * @param parent The composite parent.
     */
    private void createButtonRemove(Composite parent)
    {
        removeLevelRip = new Button(parent, SWT.PUSH);
        removeLevelRip.setImage(ObjectList.ICON_REMOVE);
        removeLevelRip.setToolTipText(Messages.RemoveLevelRip);
        UtilButton.setAction(removeLevelRip, () -> onRemoveLevelRip());
    }

    /**
     * Called on add level rip action.
     */
    private void onAddLevelRip()
    {
        final File[] files = UtilDialog.selectResourceFiles(levelRips.getShell(), new String[]
        {
            Messages.LevelRipFileFilter
        }, LEVEL_RIP_FILTER);
        final Project project = Project.getActive();
        for (final File file : files)
        {
            final Media media = project.getResourceMedia(file);
            final String path = media.getPath();
            if (!containsItem(path))
            {
                final TreeItem item = new TreeItem(levelRips, SWT.NONE);
                item.setText(path);
                item.setData(media);
                synchronized (mediasLock)
                {
                    medias.add(media);
                }

                for (final LevelRipsWidgetListener listener : listeners)
                {
                    listener.notifyLevelRipAdded(media);
                }
            }
        }
    }

    /**
     * Check if tree contains item value.
     * 
     * @param value The value to check.
     * @return <code>true</code> if value is contained by tree, <code>false</code> else.
     */
    private boolean containsItem(String value)
    {
        for (final TreeItem item : levelRips.getItems())
        {
            if (item.getText().equals(value))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Called on remove a level rip action.
     */
    private void onRemoveLevelRip()
    {
        for (final TreeItem item : levelRips.getSelection())
        {
            final Media media = (Media) item.getData();
            item.setData(null);
            item.dispose();
            synchronized (mediasLock)
            {
                medias.remove(media);
            }
            for (final LevelRipsWidgetListener listener : listeners)
            {
                listener.notifyLevelRipRemoved(media);
            }
        }
    }

    /**
     * Listen to {@link LevelRipsWidget} events.
     */
    public interface LevelRipsWidgetListener
    {
        /**
         * Called when a level rip has been added.
         * 
         * @param media The added level rip.
         */
        void notifyLevelRipAdded(Media media);

        /**
         * Called when a level rip has been removed.
         * 
         * @param media The removed level rip.
         */
        void notifyLevelRipRemoved(Media media);
    }
}
