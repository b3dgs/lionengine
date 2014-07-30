/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.example.game.effect;

import com.b3dgs.lionengine.UtilRandom;
import com.b3dgs.lionengine.anim.AnimState;
import com.b3dgs.lionengine.anim.Animation;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.SpriteAnimated;
import com.b3dgs.lionengine.game.CameraGame;
import com.b3dgs.lionengine.game.ObjectGame;
import com.b3dgs.lionengine.game.SetupSurfaceGame;
import com.b3dgs.lionengine.game.configurable.Configurable;
import com.b3dgs.lionengine.game.configurable.FramesData;

/**
 * Effect base implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
abstract class Effect
        extends ObjectGame
{
    /** Surface. */
    private final SpriteAnimated sprite;
    /** Explode animation. */
    private final Animation animExplode;

    /**
     * Constructor.
     * 
     * @param setup the setup reference.
     */
    protected Effect(SetupSurfaceGame setup)
    {
        super(setup);
        // Data are loaded from the XML file, depending of the type
        final Configurable configurable = setup.getConfigurable();
        final FramesData framesData = configurable.getFrames();
        animExplode = configurable.getAnimation("explode");
        sprite = Drawable.loadSpriteAnimated(setup.surface, framesData.getHorizontal(), framesData.getVertical());
        sprite.load(false);
        sprite.scale(UtilRandom.getRandomInteger(75) + 50);
        setSize(sprite.getFrameWidth(), sprite.getFrameHeight());
    }

    /**
     * Start the effect at the specified location.
     * 
     * @param x The horizontal location.
     * @param y The vertical location.
     */
    public void start(int x, int y)
    {
        setLocation(x - getWidth() / 2, y + getHeight() / 2);
        sprite.play(animExplode);
    }

    /*
     * Effect
     */

    @Override
    public void update(double extrp)
    {
        sprite.updateAnimation(extrp);
        // Destroy the effect when the animation is done
        if (sprite.getAnimState() == AnimState.FINISHED)
        {
            destroy();
        }
    }

    @Override
    public void render(Graphic g, CameraGame camera)
    {
        sprite.render(g, camera.getViewpointX(getLocationIntX()), camera.getViewpointY(getLocationIntY()));
    }
}
