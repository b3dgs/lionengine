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
package com.b3dgs.lionengine.example.prototype.dungeon;

import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.game.Action;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Mirrorable;
import com.b3dgs.lionengine.game.feature.RoutineRender;
import com.b3dgs.lionengine.game.feature.RoutineUpdate;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.Sprite;

/**
 * Sword attack implementation.
 */
@FeatureInterface
public final class AttackSword extends FeatureModel implements Trigger, RoutineUpdate, RoutineRender
{
    private final Sprite surface = Drawable.loadSprite(Medias.create("item", "Sword1.png"));
    private final Viewer viewer;

    private final Mirrorable mirrorable;
    private final Transformable transformable;

    private Action callback;
    private int angle;

    /**
     * Create model.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     * @param mirrorable The mirrorable feature.
     * @param transformable The transformable feature.
     */
    public AttackSword(Services services, Setup setup, Mirrorable mirrorable, Transformable transformable)
    {
        super(services, setup);

        this.mirrorable = mirrorable;
        this.transformable = transformable;

        viewer = services.get(Viewer.class);

        surface.load();
        surface.prepare();
        surface.setOrigin(Origin.CENTER_BOTTOM);
        surface.setAngleAnchor(-8, -5);
        surface.setFrameOffsets(4, -1);
    }

    @Override
    public void start(Action callback)
    {
        this.callback = callback;
        angle = 0;
        surface.rotate(0);
    }

    @Override
    public void update(double extrp)
    {
        if (callback != null)
        {
            angle += 8;
            if (angle > 75)
            {
                angle = 75;
                callback.execute();
                callback = null;
            }
            if (mirrorable.getMirror() == Mirror.HORIZONTAL)
            {
                surface.setMirror(Mirror.NONE);
            }
            else
            {
                surface.setMirror(Mirror.HORIZONTAL);
            }
            surface.rotate(-angle);
        }
    }

    @Override
    public void render(Graphic g)
    {
        if (callback != null)
        {
            surface.setLocation(viewer, transformable);
            surface.render(g);
        }
    }
}
