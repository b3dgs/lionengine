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
package com.b3dgs.lionengine.editor.dialog;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.editor.project.Project;
import com.b3dgs.lionengine.editor.utility.UtilButton;
import com.b3dgs.lionengine.editor.utility.UtilDialog;

/**
 * Widget that allows to browse a project media and get its path.
 */
public class BrowseWidget
{
    /** Listeners. */
    private final Collection<BrowseWidgetListener> listeners = new HashSet<>();
    /** Selected media. */
    private Media media;

    /**
     * Create the widget.
     * 
     * @param parent The parent composite.
     * @param label The browse label text.
     */
    public BrowseWidget(Composite parent, String label)
    {
        this(parent, label, null, true);
    }

    /**
     * Create the widget.
     * 
     * @param parent The parent composite.
     * @param label The browse label text.
     * @param filter The file filter description.
     * @param open <code>true</code> to open, <code>false</code> to save.
     */
    public BrowseWidget(Composite parent, String label, String filter, boolean open)
    {
        final Composite area = new Composite(parent, SWT.NONE);
        area.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        area.setLayout(new GridLayout(3, false));
        final Label locationLabel = new Label(area, SWT.NONE);
        locationLabel.setText(label);

        final Text location = new Text(area, SWT.BORDER);
        location.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        location.setEditable(false);

        final Button browse = UtilButton.createBrowse(area);
        UtilButton.setAction(browse, () -> onBrowse(location, filter, open));
    }

    /**
     * Add a browse listener.
     * 
     * @param listener The listener reference.
     */
    public void addListener(BrowseWidgetListener listener)
    {
        listeners.add(listener);
    }

    /**
     * Remove a browse listener.
     * 
     * @param listener The listener reference.
     */
    public void removeListener(BrowseWidgetListener listener)
    {
        listeners.remove(listener);
    }

    /**
     * Get the selected media.
     * 
     * @return The selected media, <code>null</code> if none.
     */
    public Media getMedia()
    {
        return media;
    }

    /**
     * Browse the location.
     * 
     * @param location The location text reference.
     * @param filter The file filter description.
     * @param open <code>true</code> to open, <code>false</code> to save.
     */
    private void onBrowse(Text location, String filter, boolean open)
    {
        final File file;
        if (filter == null)
        {
            file = UtilDialog.selectResourceFolder(location.getShell());
        }
        else
        {
            file = UtilDialog.selectResourceXml(location.getShell(), open, filter);
        }
        if (file != null)
        {
            final Project project = Project.getActive();
            media = project.getResourceMedia(file);
            location.setText(media.getPath());
            location.setData(media);

            for (final BrowseWidgetListener listener : listeners)
            {
                listener.notifyMediaSelected(media);
            }
        }
    }

    /**
     * Listen to {@link BrowseWidgetListener} events.
     */
    public interface BrowseWidgetListener
    {
        /**
         * Called when a media has been selected.
         * 
         * @param media The selected media.
         */
        void notifyMediaSelected(Media media);
    }
}
