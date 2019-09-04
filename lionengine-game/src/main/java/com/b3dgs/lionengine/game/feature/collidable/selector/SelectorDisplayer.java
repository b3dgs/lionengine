/*
 * Copyright (C) 2013-2019 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.collidable.selector;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.game.feature.Displayable;
import com.b3dgs.lionengine.game.feature.FeatureAbstract;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.geom.Area;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;

/**
 * This class allows to render selection.
 */
@FeatureInterface
public class SelectorDisplayer extends FeatureAbstract implements Displayable
{
    /** Viewer reference. */
    private final Viewer viewer;
    /** Selector model reference. */
    private final SelectorModel model;
    /** Cursor selection color. */
    private ColorRgba colorSelection = ColorRgba.GRAY;

    /**
     * Create feature.
     * <p>
     * The {@link Services} must provide:
     * </p>
     * <ul>
     * <li>{@link Viewer}</li>
     * </ul>
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param model The model reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public SelectorDisplayer(Services services, SelectorModel model)
    {
        super();

        Check.notNull(services);
        Check.notNull(model);

        viewer = services.get(Viewer.class);
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
    public void render(Graphic g)
    {
        if (model.isSelecting())
        {
            final Area selectionArea = model.getSelectionArea();
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
                g.drawRect(x, y, w - 1, h - 1, false);
            }
        }
    }
}
