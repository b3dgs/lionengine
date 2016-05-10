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
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.core.awt.Mouse;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.Image;
import com.b3dgs.lionengine.game.Services;
import com.b3dgs.lionengine.game.object.ObjectGame;
import com.b3dgs.lionengine.game.object.SetupSurface;
import com.b3dgs.lionengine.game.object.feature.actionable.Action;
import com.b3dgs.lionengine.game.object.feature.actionable.ActionConfig;
import com.b3dgs.lionengine.game.object.feature.actionable.Actionable;
import com.b3dgs.lionengine.game.object.feature.actionable.ActionableModel;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Renderable;
import com.b3dgs.lionengine.graphic.Text;

/**
 * Abstract button action.
 */
class Button extends ObjectGame implements Action, Updatable, Renderable
{
    /** Media buildings reference. */
    public static final Media BUILDINGS = Medias.create("action", "Buildings.xml");
    /** Media build farm reference. */
    public static final Media BUILD_FARM = Medias.create("action", "BuildFarm.xml");
    /** Media build barracks reference. */
    public static final Media BUILD_BARRACKS = Medias.create("action", "BuildBarracks.xml");
    /** Media cancel reference. */
    public static final Media CANCEL = Medias.create("action", "Cancel.xml");

    /** Actionable model. */
    private final Actionable actionable;
    /** Button image. */
    private final Image image;
    /** Text reference. */
    private final Text text;
    /** Action name. */
    private final String name;

    /**
     * Create build farm action.
     * 
     * @param setup The setup reference.
     * @param services The services reference.
     */
    public Button(SetupSurface setup, Services services)
    {
        super(setup, services);
        text = services.get(Text.class);
        image = Drawable.loadImage(setup.getSurface());
        name = ActionConfig.imports(getConfigurer().getRoot()).getName();

        actionable = addFeatureAndGet(new ActionableModel(setup.getConfigurer()));
        actionable.setClickAction(Mouse.LEFT);
    }

    @Override
    protected void onPrepared()
    {
        image.setLocation(actionable.getButton().getX(), actionable.getButton().getY());
    }

    @Override
    public void execute()
    {
        Verbose.info(name);
    }

    @Override
    public void update(double extrp)
    {
        actionable.update(extrp);
        if (actionable.isOver())
        {
            text.setText(actionable.getDescription());
        }
    }

    @Override
    public void render(Graphic g)
    {
        image.render(g);
    }
}
