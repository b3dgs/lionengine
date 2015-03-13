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
package com.b3dgs.lionengine.example.game.assign;

import com.b3dgs.lionengine.core.Core;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.Renderable;
import com.b3dgs.lionengine.core.Text;
import com.b3dgs.lionengine.core.Updatable;
import com.b3dgs.lionengine.core.awt.Mouse;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.Image;
import com.b3dgs.lionengine.game.Cursor;
import com.b3dgs.lionengine.game.object.ObjectGame;
import com.b3dgs.lionengine.game.object.Services;
import com.b3dgs.lionengine.game.object.SetupSurface;
import com.b3dgs.lionengine.game.trait.Action;
import com.b3dgs.lionengine.game.trait.Actionable;
import com.b3dgs.lionengine.game.trait.ActionableModel;
import com.b3dgs.lionengine.game.trait.Assign;
import com.b3dgs.lionengine.game.trait.Assignable;
import com.b3dgs.lionengine.game.trait.AssignableModel;
import com.b3dgs.lionengine.game.trait.Pathfindable;

/**
 * Move action.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
class Move
        extends ObjectGame
        implements Action, Assign, Updatable, Renderable
{
    /** Media reference. */
    public static final Media MEDIA = Core.MEDIA.create("Move.xml");

    /** Actionable model. */
    private final Actionable actionable;
    /** Assignable model. */
    private final Assignable assignable;
    /** Button image. */
    private final Image image;
    /** Text reference. */
    private final Text text;
    /** Cursor reference. */
    private final Cursor cursor;
    /** Executed flag. */
    private boolean executed;
    /** Pathfindable reference. */
    private Pathfindable pathfindable;

    /**
     * Create move action.
     * 
     * @param setup The setup reference.
     * @param services The services reference.
     */
    public Move(SetupSurface setup, Services services)
    {
        super(setup, services);
        text = services.get(Text.class);
        cursor = services.get(Cursor.class);
        image = Drawable.loadImage(setup.surface);
        actionable = new ActionableModel(this, setup.getConfigurer(), services);
        actionable.setClickAction(Mouse.LEFT);
        actionable.setAction(this);
        assignable = new AssignableModel(this, setup.getConfigurer(), services);
        assignable.setClickAssign(Mouse.LEFT);
        assignable.setAssign(this);
        image.setLocation(actionable.getButton().getX(), actionable.getButton().getY());
    }

    /**
     * Set the pathfindable reference.
     * 
     * @param pathfindable The pathfindable reference.
     */
    public void setPathfindable(Pathfindable pathfindable)
    {
        this.pathfindable = pathfindable;
    }

    @Override
    public void execute()
    {
        executed = true;
    }

    @Override
    public void assign()
    {
        pathfindable.setDestination(cursor.getInTileX(), cursor.getInTileY());
        executed = false;
    }

    @Override
    public void update(double extrp)
    {
        if (actionable.isOver())
        {
            text.setText(actionable.getDescription());
        }
        if (executed)
        {
            assignable.update(extrp);
        }
        else
        {
            actionable.update(extrp);
        }
    }

    @Override
    public void render(Graphic g)
    {
        image.render(g);
    }
}
