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
package com.b3dgs.lionengine.editor.world;

import javax.annotation.PostConstruct;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.editor.InputValidator;
import com.b3dgs.lionengine.editor.UtilEclipse;
import com.b3dgs.lionengine.editor.UtilSwt;

/**
 * Represents the zoom item label with its current value.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class ZoomItem
{
    /** Element ID. */
    public static final String ID = "zoom-item";

    /** Current zoom value. */
    private Text zoom;

    /**
     * Create item.
     */
    public ZoomItem()
    {
        // Nothing to do
    }

    /**
     * Validate current zoom value.
     * 
     * @return The validated value.
     */
    private int validateZoomValue()
    {
        final int value = Integer.parseInt(zoom.getText());
        final int percent;
        if (value < WorldViewUpdater.ZOOM_MIN || value > WorldViewUpdater.ZOOM_MAX)
        {
            percent = UtilMath.fixBetween(value, WorldViewUpdater.ZOOM_MIN, WorldViewUpdater.ZOOM_MAX);
            zoom.setText(String.valueOf(percent));
        }
        else
        {
            percent = value;
        }
        return percent;
    }

    /**
     * Choose zoom value.
     */
    void chooseZoom()
    {
        final WorldViewPart part = UtilEclipse.getPart(WorldViewPart.ID, WorldViewPart.class);
        final int percent = validateZoomValue();
        part.getUpdater().setZoomPercent(percent);
        part.update();
    }

    /**
     * Construct the item.
     * 
     * @param parent The parent reference.
     */
    @PostConstruct
    public void create(Composite parent)
    {
        final GridLayout layout = new GridLayout(2, false);
        layout.marginHeight = 1;
        parent.setLayout(layout);

        zoom = new Text(parent, SWT.SINGLE | SWT.CENTER | SWT.BORDER);
        zoom.setLayoutData(new GridData(20, 13));
        UtilSwt.createVerify(zoom, InputValidator.INTEGER_POSITIVE_STRICT_MATCH);
        zoom.setText(String.valueOf(WorldViewUpdater.ZOOM_DEFAULT));
        zoom.addTraverseListener(new TraverseListener()
        {
            @Override
            public void keyTraversed(TraverseEvent event)
            {
                if (event.detail == SWT.TRAVERSE_RETURN)
                {
                    chooseZoom();
                }
            }
        });

        final Label label = new Label(parent, SWT.NONE);
        label.setText(Constant.PERCENT);
    }
}
