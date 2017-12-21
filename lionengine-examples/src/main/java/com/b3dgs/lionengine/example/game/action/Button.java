/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.example.game.action;

import java.util.Arrays;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.core.drawable.Drawable;
import com.b3dgs.lionengine.game.ActionConfig;
import com.b3dgs.lionengine.game.feature.Actionable;
import com.b3dgs.lionengine.game.feature.ActionableModel;
import com.b3dgs.lionengine.game.feature.DisplayableModel;
import com.b3dgs.lionengine.game.feature.FeaturableModel;
import com.b3dgs.lionengine.game.feature.RefreshableModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.graphic.Image;
import com.b3dgs.lionengine.graphic.Text;
import com.b3dgs.lionengine.io.awt.Mouse;
import com.b3dgs.lionengine.util.UtilReflection;

/**
 * Button action implementation.
 */
class Button extends FeaturableModel
{
    /** Media buildings reference. */
    public static final Media BUILDINGS = Medias.create("Buildings.xml");
    /** Media build farm reference. */
    public static final Media BUILD_FARM = Medias.create("BuildFarm.xml");
    /** Media build barracks reference. */
    public static final Media BUILD_BARRACKS = Medias.create("BuildBarracks.xml");
    /** Media cancel reference. */
    public static final Media CANCEL = Medias.create("Cancel.xml");

    /**
     * Create build farm action.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     */
    public Button(Services services, Setup setup)
    {
        super();

        addFeature(new ButtonLink());

        final Actionable actionable = addFeatureAndGet(new ActionableModel(services, setup));
        actionable.setClickAction(Mouse.LEFT);

        final ActionFeature action = addFeatureAndGet(setup.getImplementation(ActionFeature.class,
                                                                              UtilReflection.getParamTypes(setup),
                                                                              Arrays.asList(setup),
                                                                              ActionConfig.NODE_ACTION));
        actionable.setAction(action.create(services));

        final Image image = Drawable.loadImage(setup.getSurface());
        image.setLocation(actionable.getButton().getX(), actionable.getButton().getY());

        final Text text = services.get(Text.class);

        addFeature(new RefreshableModel(extrp ->
        {
            actionable.update(extrp);
            if (actionable.isOver())
            {
                text.setText(actionable.getDescription());
            }
        }));

        addFeature(new DisplayableModel(image::render));
    }
}
