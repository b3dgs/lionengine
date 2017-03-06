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
package com.b3dgs.lionengine.game.feature.collidable.selector;

import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.Services;
import com.b3dgs.lionengine.game.feature.Displayable;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.geom.Rectangle;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;

/**
 * This class allows to render selection.
 */
public class SelectorDisplayer extends FeatureModel implements Displayable
{
    /** Selector model reference. */
    private final SelectorModel model;
    /** Cursor selection color. */
    private ColorRgba colorSelection = ColorRgba.GRAY;
    /** Viewer reference. */
    private Viewer viewer;

    /**
     * Create a selector.
     * <p>
     * The {@link Services} must provide:
     * </p>
     * <ul>
     * <li>{@link Viewer}</li>
     * </ul>
     * 
     * @param model The model reference.
     */
    public SelectorDisplayer(SelectorModel model)
    {
        super();

        this.model = model;
    }

    /**
     * Set the selection color.
     * 
     * @param color The selection color.
     */
    public void setSelectionColor(ColorRgba color)
    {
        colorSelection = color;
    }

    /*
     * Displayable
     */

    @Override
    public void prepare(FeatureProvider provider, Services services)
    {
        super.prepare(provider, services);

        viewer = services.get(Viewer.class);
    }

    @Override
    public void render(Graphic g)
    {
        if (model.isSelecting())
        {
            final Rectangle selectionArea = model.getSelectionArea();
            final int x = (int) viewer.getViewpointX(selectionArea.getX());
            final int w = selectionArea.getWidth();
            int y = (int) viewer.getViewpointY(model.getSelectRawY());
            int h = (int) model.getSelectRawH();
            if (h < 0)
            {
                y += h;
                h = -h;
            }
            if (y < 0)
            {
                h += y;
                y = 0;
            }
            if (w > 0 && h > 0)
            {
                g.setColor(colorSelection);
                g.drawRect(x, y, w, h, false);
            }
        }
    }
}
