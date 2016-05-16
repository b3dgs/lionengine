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
package com.b3dgs.lionengine.example.game.action;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.core.awt.Mouse;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.Image;
import com.b3dgs.lionengine.game.handler.DisplayableModel;
import com.b3dgs.lionengine.game.handler.RefreshableModel;
import com.b3dgs.lionengine.game.handler.Service;
import com.b3dgs.lionengine.game.object.ObjectGame;
import com.b3dgs.lionengine.game.object.SetupSurface;
import com.b3dgs.lionengine.game.object.feature.actionable.ActionConfig;
import com.b3dgs.lionengine.game.object.feature.actionable.Actionable;
import com.b3dgs.lionengine.game.object.feature.actionable.ActionableModel;
import com.b3dgs.lionengine.graphic.Text;

/**
 * Abstract button action.
 */
class Button extends ObjectGame
{
    /** Media buildings reference. */
    public static final Media BUILDINGS = Medias.create("action", "Buildings.xml");
    /** Media build farm reference. */
    public static final Media BUILD_FARM = Medias.create("action", "BuildFarm.xml");
    /** Media build barracks reference. */
    public static final Media BUILD_BARRACKS = Medias.create("action", "BuildBarracks.xml");
    /** Media cancel reference. */
    public static final Media CANCEL = Medias.create("action", "Cancel.xml");

    @Service private Text text;

    /**
     * Create build farm action.
     * 
     * @param setup The setup reference.
     */
    public Button(SetupSurface setup)
    {
        super(setup);

        final String name = ActionConfig.imports(getConfigurer().getRoot()).getName();

        final Actionable actionable = addFeatureAndGet(new ActionableModel(setup));
        actionable.setAction(() -> Verbose.info(name));
        actionable.setClickAction(Mouse.LEFT);

        final Image image = Drawable.loadImage(setup.getSurface());
        image.setLocation(actionable.getButton().getX(), actionable.getButton().getY());

        addFeature(new RefreshableModel(extrp ->
        {
            actionable.update(extrp);
            if (actionable.isOver())
            {
                text.setText(actionable.getDescription());
            }
        }));

        addFeature(new DisplayableModel(g -> image.render(g)));
    }
}
