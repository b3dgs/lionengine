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

import java.util.ArrayList;
import java.util.Collection;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.core.awt.Mouse;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.Image;
import com.b3dgs.lionengine.game.feature.FeaturableModel;
import com.b3dgs.lionengine.game.feature.Service;
import com.b3dgs.lionengine.game.feature.displayable.DisplayableModel;
import com.b3dgs.lionengine.game.feature.identifiable.Identifiable;
import com.b3dgs.lionengine.game.feature.refreshable.RefreshableModel;
import com.b3dgs.lionengine.game.object.Setup;
import com.b3dgs.lionengine.game.object.SetupSurface;
import com.b3dgs.lionengine.game.object.feature.actionable.ActionConfig;
import com.b3dgs.lionengine.game.object.feature.actionable.Actionable;
import com.b3dgs.lionengine.game.object.feature.actionable.ActionableModel;
import com.b3dgs.lionengine.graphic.Text;

/**
 * Button action implementation.
 */
class Button extends FeaturableModel
{
    /** Media buildings reference. */
    public static final Media BUILDINGS = Medias.create("action", "Buildings.xml");
    /** Media build farm reference. */
    public static final Media BUILD_FARM = Medias.create("action", "BuildFarm.xml");
    /** Media build barracks reference. */
    public static final Media BUILD_BARRACKS = Medias.create("action", "BuildBarracks.xml");
    /** Media cancel reference. */
    public static final Media CANCEL = Medias.create("action", "Cancel.xml");

    private final Collection<Button> toDelete = new ArrayList<>();

    @Service private Text text;

    /**
     * Create build farm action.
     * 
     * @param setup The setup reference.
     */
    public Button(SetupSurface setup)
    {
        super();

        final Actionable actionable = addFeatureAndGet(new ActionableModel(setup));
        actionable.setClickAction(Mouse.LEFT);

        final ActionFeature action = setup.getConfigurer().getImplementation(ActionFeature.class,
                                                                             Setup.class,
                                                                             setup,
                                                                             ActionConfig.NODE_ACTION);
        actionable.setAction(action);
        addFeature(action);

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

        addFeature(new DisplayableModel(image::render));
    }

    /**
     * Add an action to delete on click.
     * 
     * @param action The action to delete.
     */
    public void addToDelete(Button action)
    {
        toDelete.add(action);
    }

    /**
     * Terminate button.
     */
    public void terminate()
    {
        for (final Button button : toDelete)
        {
            button.getFeature(Identifiable.class).destroy();
        }
        getFeature(Identifiable.class).destroy();
    }
}
