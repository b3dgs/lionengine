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
package com.b3dgs.lionengine.game.object.trait.actionable;

import com.b3dgs.lionengine.game.Cursor;
import com.b3dgs.lionengine.game.Services;
import com.b3dgs.lionengine.game.object.ObjectGame;
import com.b3dgs.lionengine.game.object.trait.TraitModel;
import com.b3dgs.lionengine.geom.Geom;
import com.b3dgs.lionengine.geom.Rectangle;

/**
 * Actionnable model implementation.
 * <p>
 * The {@link ObjectGame} owner must provide a valid {@link com.b3dgs.lionengine.game.Configurer} compatible
 * with {@link ActionConfig}.
 * </p>
 * <p>
 * The {@link Services} must provide the following services:
 * </p>
 * <ul>
 * <li>{@link Cursor}</li>
 * </ul>
 * <p>
 * If the {@link ObjectGame} owner is an {@link Action}, it will automatically {@link #setAction(Action)} on it.
 * </p>
 */
public class ActionableModel extends TraitModel implements Actionable
{
    /** Cursor reference. */
    private Cursor cursor;
    /** Rectangle button area. */
    private Rectangle button;
    /** Action description. */
    private String description;
    /** Mouse click number to execute action. */
    private int clickAction;
    /** Action used. */
    private Action action;

    /**
     * Create an actionable model.
     */
    public ActionableModel()
    {
        super();
    }

    /*
     * Actionable
     */

    @Override
    public void prepare(ObjectGame owner, Services services)
    {
        super.prepare(owner, services);

        final ActionConfig config = ActionConfig.imports(owner.getConfigurer().getRoot());
        button = Geom.createRectangle(config.getX(), config.getY(), config.getWidth(), config.getHeight());
        description = config.getDescription();

        cursor = services.get(Cursor.class);

        if (owner instanceof Action)
        {
            setAction((Action) owner);
        }
    }

    @Override
    public void update(double extrp)
    {
        if (action != null && isOver() && cursor.hasClickedOnce(clickAction))
        {
            action.execute();
        }
    }

    @Override
    public void setAction(Action action)
    {
        this.action = action;
    }

    @Override
    public void setClickAction(int click)
    {
        clickAction = click;
    }

    @Override
    public Rectangle getButton()
    {
        return button;
    }

    @Override
    public String getDescription()
    {
        return description;
    }

    @Override
    public boolean isOver()
    {
        return button.contains(cursor.getScreenX(), cursor.getScreenY());
    }
}
