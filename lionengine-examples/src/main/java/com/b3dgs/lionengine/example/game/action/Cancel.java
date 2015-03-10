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
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.Renderable;
import com.b3dgs.lionengine.core.Text;
import com.b3dgs.lionengine.core.Updatable;
import com.b3dgs.lionengine.core.awt.Mouse;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.Image;
import com.b3dgs.lionengine.game.object.Factory;
import com.b3dgs.lionengine.game.object.Handler;
import com.b3dgs.lionengine.game.object.ObjectGame;
import com.b3dgs.lionengine.game.object.Services;
import com.b3dgs.lionengine.game.object.SetupSurface;
import com.b3dgs.lionengine.game.trait.Action;
import com.b3dgs.lionengine.game.trait.Actionable;
import com.b3dgs.lionengine.game.trait.ActionableModel;

/**
 * Cancel action.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class Cancel
        extends ObjectGame
        implements Action, Updatable, Renderable
{
    /** Media reference. */
    public static final Media MEDIA = Core.MEDIA.create("action", "Cancel.xml");

    /** Actionable model. */
    private final Actionable actionable;
    /** Button image. */
    private final Image image;
    /** Text reference. */
    private final Text text;
    /** Factory reference. */
    private final Factory factory;
    /** Handler reference. */
    private final Handler handler;

    /**
     * Create cancel action.
     * 
     * @param setup The setup reference.
     * @param services The services reference.
     */
    public Cancel(SetupSurface setup, Services services)
    {
        super(setup, services);
        text = services.get(Text.class);
        factory = services.get(Factory.class);
        handler = services.get(Handler.class);
        image = Drawable.loadImage(setup.surface);
        actionable = new ActionableModel(this, setup.getConfigurer(), services);
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
        final ObjectGame buildings = factory.create(Buildings.MEDIA);
        handler.add(buildings);
        destroy();
    }

    /*
     * Updatable
     */

    @Override
    public void update(double extrp)
    {
        actionable.update(extrp);
        if (actionable.isOver())
        {
            text.setText(actionable.getDescription());
        }
    }

    /*
     * Renderable
     */

    @Override
    public void render(Graphic g)
    {
        image.render(g);
    }
}
