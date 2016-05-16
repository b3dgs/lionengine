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
package com.b3dgs.lionengine.game.object.feature.assignable;

import com.b3dgs.lionengine.game.Cursor;
import com.b3dgs.lionengine.game.handler.FeatureModel;
import com.b3dgs.lionengine.game.handler.Handlable;
import com.b3dgs.lionengine.game.handler.Services;
import com.b3dgs.lionengine.game.object.ObjectGame;
import com.b3dgs.lionengine.graphic.Viewer;
import com.b3dgs.lionengine.util.UtilMath;

/**
 * Assignable implementation.
 */
public class AssignableModel extends FeatureModel implements Assignable
{
    /** Cursor reference. */
    private Cursor cursor;
    /** Viewer reference. */
    private Viewer viewer;
    /** Mouse click number to assign action. */
    private int clickAssign;
    /** Assign used. */
    private Assign assign;

    /**
     * Create an assignable model.
     * 
     * <p>
     * The {@link Services} must provide the following services:
     * </p>
     * <ul>
     * <li>{@link Cursor}</li>
     * <li>{@link Viewer}</li>
     * </ul>
     * <p>
     * If the {@link ObjectGame} is an {@link Assign}, it will automatically {@link #setAssign(Assign)} on it.
     * </p>
     */
    public AssignableModel()
    {
        super();
    }

    /*
     * Assignable
     */

    @Override
    public void prepare(Handlable owner, Services services)
    {
        super.prepare(owner, services);

        cursor = services.get(Cursor.class);
        viewer = services.get(Viewer.class);

        if (owner instanceof Assign)
        {
            setAssign((Assign) owner);
        }
    }

    @Override
    public void update(double extrp)
    {
        if (assign != null
            && UtilMath.isBetween(cursor.getScreenX(),
                                  viewer.getViewX(),
                                  viewer.getViewX() + (double) viewer.getWidth())
            && UtilMath.isBetween(cursor.getScreenY(),
                                  viewer.getViewY(),
                                  viewer.getViewY() + (double) viewer.getHeight())
            && cursor.hasClickedOnce(clickAssign))
        {
            assign.assign();
        }
    }

    @Override
    public void setAssign(Assign assign)
    {
        this.assign = assign;
    }

    @Override
    public void setClickAssign(int click)
    {
        clickAssign = click;
    }
}
