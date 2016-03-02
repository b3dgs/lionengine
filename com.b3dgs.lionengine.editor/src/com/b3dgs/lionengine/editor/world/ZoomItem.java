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
package com.b3dgs.lionengine.editor.world;

import javax.annotation.PostConstruct;

import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.editor.InputValidator;
import com.b3dgs.lionengine.editor.utility.UtilText;
import com.b3dgs.lionengine.editor.world.updater.WorldZoom;
import com.b3dgs.lionengine.game.object.Services;

/**
 * Represents the zoom item label with its current value.
 */
public class ZoomItem
{
    /** Element ID. */
    public static final String ID = "zoom-item";
    /** Text height. */
    private static final int TEXT_HEIGHT = 8;

    /** Current zoom value. */
    private Text zoomValue;

    /**
     * Create item.
     */
    public ZoomItem()
    {
        super();
    }

    /**
     * Validate current zoom value.
     * 
     * @return The validated value.
     */
    private int validateZoomValue()
    {
        final int value = Integer.parseInt(zoomValue.getText());
        final int percent;
        if (value < WorldZoom.ZOOM_MIN || value > WorldZoom.ZOOM_MAX)
        {
            percent = UtilMath.clamp(value, WorldZoom.ZOOM_MIN, WorldZoom.ZOOM_MAX);
            zoomValue.setText(String.valueOf(percent));
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
        final Services services = WorldModel.INSTANCE.getServices();

        final WorldZoom zoom = services.get(WorldZoom.class);
        final int percent = validateZoomValue();
        zoom.setPercent(percent);

        final WorldPart part = services.get(WorldPart.class);
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
        final FontDescriptor boldDescriptor = FontDescriptor.createFrom(parent.getFont()).setHeight(TEXT_HEIGHT);
        final Font font = boldDescriptor.createFont(parent.getDisplay());

        final GridLayout layout = new GridLayout(2, false);
        layout.marginHeight = 0;
        parent.setLayout(layout);

        zoomValue = new Text(parent, SWT.SINGLE | SWT.CENTER | SWT.BORDER);
        zoomValue.setFont(font);
        UtilText.createVerify(zoomValue, InputValidator.INTEGER_POSITIVE_STRICT_MATCH);
        zoomValue.setText(String.valueOf(WorldZoom.ZOOM_DEFAULT));
        zoomValue.addTraverseListener(event ->
        {
            if (event.detail == SWT.TRAVERSE_RETURN)
            {
                chooseZoom();
            }
        });
        zoomValue.pack();

        final Label label = new Label(parent, SWT.NONE);
        label.setFont(font);
        label.setText(Constant.PERCENT);
    }
}
