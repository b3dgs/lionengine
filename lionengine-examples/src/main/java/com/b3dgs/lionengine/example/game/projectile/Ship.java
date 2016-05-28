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
package com.b3dgs.lionengine.example.game.projectile;

import com.b3dgs.lionengine.Localizable;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.SpriteAnimated;
import com.b3dgs.lionengine.game.collision.object.Collidable;
import com.b3dgs.lionengine.game.collision.object.CollidableListener;
import com.b3dgs.lionengine.game.collision.object.CollidableModel;
import com.b3dgs.lionengine.game.feature.Factory;
import com.b3dgs.lionengine.game.feature.FeaturableModel;
import com.b3dgs.lionengine.game.feature.FramesConfig;
import com.b3dgs.lionengine.game.feature.Service;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.SetupSurface;
import com.b3dgs.lionengine.game.feature.displayable.DisplayableModel;
import com.b3dgs.lionengine.game.feature.identifiable.Identifiable;
import com.b3dgs.lionengine.game.feature.layerable.LayerableModel;
import com.b3dgs.lionengine.game.feature.refreshable.RefreshableModel;
import com.b3dgs.lionengine.game.feature.transformable.Transformable;
import com.b3dgs.lionengine.game.feature.transformable.TransformableModel;
import com.b3dgs.lionengine.game.handler.Handler;
import com.b3dgs.lionengine.graphic.Viewer;
import com.b3dgs.lionengine.util.UtilMath;
import com.b3dgs.lionengine.util.UtilRandom;

/**
 * Ship implementation.
 */
class Ship extends FeaturableModel implements CollidableListener
{
    /** Media. */
    public static final Media MEDIA = Medias.create("Ship.xml");

    private final double speed = UtilRandom.getRandomDouble() / 1.5 + 0.75;
    private final Transformable transformable = addFeatureAndGet(new TransformableModel());
    private final SpriteAnimated sprite;

    private Weapon weapon;
    private Localizable target;
    private double location;
    private int x = 64;
    private int y = 192;

    @Service private Factory factory;
    @Service private Handler handler;
    @Service private Viewer viewer;

    /**
     * Constructor.
     * 
     * @param setup The setup reference.
     */
    public Ship(SetupSurface setup)
    {
        super();

        addFeature(new LayerableModel(1));

        final Collidable collidable = addFeatureAndGet(new CollidableModel(setup));
        collidable.setOrigin(Origin.MIDDLE);

        final FramesConfig config = FramesConfig.imports(setup);
        sprite = Drawable.loadSpriteAnimated(setup.getSurface(), config.getHorizontal(), config.getVertical());
        sprite.setFrame(3);
        sprite.setOrigin(Origin.MIDDLE);

        addFeature(new RefreshableModel(extrp ->
        {
            pointTarget();
            location += speed;
            transformable.setLocation(x + UtilMath.cos(location * 1.5) * 60, y + UtilMath.sin(location * 2) * 30);
            sprite.setLocation(viewer, transformable);
            weapon.fire(target);
            collidable.update(extrp);
        }));

        addFeature(new DisplayableModel(g ->
        {
            sprite.render(g);
            collidable.render(g);
        }));
    }

    /**
     * Mirror the object.
     */
    public void mirror()
    {
        sprite.setMirror(Mirror.VERTICAL);
        weapon.setOffset(0, 0);

        final int old = x;
        x = y;
        y = old;
        transformable.teleport(x + UtilMath.cos(location * 1.5) * 60, y + UtilMath.sin(location * 2) * 30);
    }

    /**
     * Set the target to seek.
     * 
     * @param target The target to seek.
     */
    public void setTarget(Ship target)
    {
        this.target = target.transformable;
    }

    /**
     * Point the defined target by computing the angle.
     */
    private void pointTarget()
    {
        final double grad = Math.atan((transformable.getX() - target.getX()) / (target.getY() - transformable.getY()));
        final int angle = (int) StrictMath.toDegrees(grad);
        if (sprite.getMirror() == Mirror.NONE)
        {
            sprite.rotate(-angle + 180);
        }
        else
        {
            sprite.rotate(angle + 180);
        }
    }

    @Override
    public void prepareFeatures(Services services)
    {
        super.prepareFeatures(services);

        transformable.teleport(x + UtilMath.cos(location * 1.5) * 60, y + UtilMath.sin(location * 2) * 30);

        weapon = factory.create(Weapon.PULSE_CANNON);
        weapon.setOffset(6, -6);
        handler.add(weapon);
        weapon.setOwner(this);
    }

    @Override
    public void notifyCollided(Collidable collidable)
    {
        collidable.getOwner().getFeature(Identifiable.class).destroy();
    }
}
