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
package com.b3dgs.lionengine.example.game.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b3dgs.lionengine.awt.MouseAwt;
import com.b3dgs.lionengine.game.feature.Actionable;
import com.b3dgs.lionengine.game.feature.ActionableModel;
import com.b3dgs.lionengine.game.feature.DisplayableModel;
import com.b3dgs.lionengine.game.feature.FeaturableModel;
import com.b3dgs.lionengine.game.feature.LayerableModel;
import com.b3dgs.lionengine.game.feature.RefreshableModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.Image;
import com.b3dgs.lionengine.graphic.drawable.SpriteFont;

/**
 * Action implementation.
 */
public final class Action extends FeaturableModel
{
    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(Action.class);

    /** Actionable feature. */
    protected final Actionable actionable;

    /**
     * Create feature.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     */
    public Action(Services services, Setup setup)
    {
        super(services, setup);

        actionable = addFeature(ActionableModel.class, services, setup);
        actionable.setClickAction(MouseAwt.LEFT);
        actionable.setAction(() -> LOGGER.info(actionable.getDescription()));

        final Image image = Drawable.loadImage(setup.getSurface());
        image.setLocation(actionable.getButton().getX(), actionable.getButton().getY());

        final SpriteFont font = services.get(SpriteFont.class);

        addFeature(new LayerableModel(1, 3));
        addFeature(new RefreshableModel(extrp ->
        {
            if (actionable.isEnabled())
            {
                actionable.update(extrp);
                if (actionable.isOver())
                {
                    font.setText(actionable.getDescription());
                }
            }
        }));

        addFeature(new DisplayableModel(g ->
        {
            if (actionable.isEnabled())
            {
                image.render(g);
            }
        }));
    }
}
