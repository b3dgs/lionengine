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
package com.b3dgs.lionengine.example.game.action;

import com.b3dgs.lionengine.core.Core;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.Renderable;
import com.b3dgs.lionengine.core.Text;
import com.b3dgs.lionengine.core.Updatable;
import com.b3dgs.lionengine.core.awt.Mouse;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.Image;
import com.b3dgs.lionengine.game.object.ObjectGame;
import com.b3dgs.lionengine.game.object.Services;
import com.b3dgs.lionengine.game.object.Setup;
import com.b3dgs.lionengine.game.trait.Action;
import com.b3dgs.lionengine.game.trait.Actionable;
import com.b3dgs.lionengine.game.trait.ActionableModel;

/**
 * Build farm action.
 *
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class BuildFarm
        extends ObjectGame
        implements Action, Updatable, Renderable
{
    /** Setup reference. */
    private static final Setup SETUP = new Setup(Core.MEDIA.create("action", "BuildFarm.xml"));

    /** Actionable model. */
    private final Actionable actionable;
    /** Button image. */
    private final Image image;
    /** Text reference. */
    private final Text text;

    /**
     * Create build farm action.
     *
     * @param services The services reference.
     */
    public BuildFarm(Services services)
    {
        super(SETUP, services);
        text = services.get(Text.class);
        image = Drawable.loadImage(Core.MEDIA.create("action", SETUP.getConfigurer().getText("icon")));
        image.load(false);
        actionable = new ActionableModel(this, SETUP.getConfigurer(), services);
        actionable.setClickAction(Mouse.LEFT);
        actionable.setAction(this);
        image.setLocation(actionable.getButton().getX(), actionable.getButton().getY());
    }

    /*
     * Action
     */

    @Override
    public void execute()
    {

    }

    /*
     * Updatable
     */

    @Override
    public void update(double extrp)
    {
        actionable.update(extrp);
    }

    /*
     * Renderable
     */

    @Override
    public void render(Graphic g)
    {
        image.render(g);
        if (actionable.isOver())
        {
            text.draw(g, 74, 192, actionable.getDescription());
        }
    }
}
