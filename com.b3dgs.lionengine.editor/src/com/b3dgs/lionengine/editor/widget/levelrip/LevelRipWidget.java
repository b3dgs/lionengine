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
package com.b3dgs.lionengine.editor.widget.levelrip;

import java.util.Collection;
import java.util.Collections;
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
import com.b3dgs.lionengine.editor.ObjectListAbstract;
import com.b3dgs.lionengine.editor.utility.control.UtilButton;
import com.b3dgs.lionengine.editor.utility.dialog.UtilDialog;

/**
 * Widget that allows to select a list of level rips.
 */
// CHECKSTYLE IGNORE LINE: DataAbstractionCoupling
public class LevelRipWidget
{
    /** Listeners. */
    private final Collection<LevelRipsWidgetListener> listeners = new HashSet<>();
    /** Level rip medias. */
    private final Collection<Media> medias = new HashSet<>();
    /** Level rips list. */
    private final Tree levelRips;

    /**
     * Create the widget.
     * 
     * @param parent The composite parent.
     */
    public LevelRipWidget(Composite parent)
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
        final int items = 3;
        buttons.setLayout(new GridLayout(items, false));

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
    public Collection<Media> getLevelRips()
    {
        return Collections.unmodifiableCollection(medias);
    }

    /**
     * Create the add level rip button.
     * 
     * @param parent The composite parent.
     */
    private void createButtonAdd(Composite parent)
    {
        final Button addLevelRip = new Button(parent, SWT.PUSH);
        addLevelRip.setImage(ObjectListAbstract.ICON_ADD);
        addLevelRip.setToolTipText(Messages.AddLevelRip);
        UtilButton.setAction(addLevelRip, this::onAddLevelRip);
    }

    /**
     * Create the remove level rip button.
     * 
     * @param parent The composite parent.
     */
    private void createButtonRemove(Composite parent)
    {
        final Button removeLevelRip = new Button(parent, SWT.PUSH);
        removeLevelRip.setImage(ObjectListAbstract.ICON_REMOVE);
        removeLevelRip.setToolTipText(Messages.RemoveLevelRip);
        UtilButton.setAction(removeLevelRip, this::onRemoveLevelRip);
    }

    /**
     * Called on add level rip action.
     */
    private void onAddLevelRip()
    {
        for (final Media media : UtilDialog.selectResourceFiles(levelRips.getShell(), UtilDialog.getImageFilter()))
        {
            final String path = media.getPath();
            if (!containsItem(path))
            {
                final TreeItem item = new TreeItem(levelRips, SWT.NONE);
                item.setText(path);
                item.setData(media);
                medias.add(media);

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
            medias.remove(media);
            for (final LevelRipsWidgetListener listener : listeners)
            {
                listener.notifyLevelRipRemoved(media);
            }
        }
    }

    /**
     * Listen to {@link LevelRipWidget} events.
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
