/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.core.drawable.Drawable;
import com.b3dgs.lionengine.game.FramesConfig;
import com.b3dgs.lionengine.game.feature.DisplayableModel;
import com.b3dgs.lionengine.game.feature.Factory;
import com.b3dgs.lionengine.game.feature.FeaturableModel;
import com.b3dgs.lionengine.game.feature.Handler;
import com.b3dgs.lionengine.game.feature.Identifiable;
import com.b3dgs.lionengine.game.feature.LayerableModel;
import com.b3dgs.lionengine.game.feature.RefreshableModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.TransformableModel;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.collidable.CollidableListener;
import com.b3dgs.lionengine.game.feature.collidable.CollidableModel;
import com.b3dgs.lionengine.graphic.SpriteAnimated;
import com.b3dgs.lionengine.util.UtilMath;
import com.b3dgs.lionengine.util.UtilRandom;

/**
 * Ship implementation.
 */
class Ship extends FeaturableModel implements CollidableListener
{
    /** Media. */
    public static final Media MEDIA = Medias.create("Ship.xml");
    private static int group = 1;

    private final double speed = UtilRandom.getRandomDouble() / 1.5 + 0.75;
    private final Transformable transformable = addFeatureAndGet(new TransformableModel());
    private final SpriteAnimated sprite;
    private final Weapon weapon;
    private final Collidable collidable;

    private Localizable target;
    private double location;
    private int x = 64;
    private int y = 192;

    /**
     * Constructor.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     */
    public Ship(Services services, Setup setup)
    {
        super();

        addFeature(new LayerableModel(1));

        final FramesConfig config = FramesConfig.imports(setup);
        sprite = Drawable.loadSpriteAnimated(setup.getSurface(), config.getHorizontal(), config.getVertical());
        sprite.setFrame(3);
        sprite.setOrigin(Origin.MIDDLE);

        transformable.teleport(x + UtilMath.cos(location * 1.5) * 60, y + UtilMath.sin(location * 2) * 30);

        collidable = addFeatureAndGet(new CollidableModel(services, setup));
        collidable.setOrigin(Origin.MIDDLE);
        collidable.setGroup(group++);

        final Factory factory = services.get(Factory.class);
        weapon = factory.create(Weapon.PULSE_CANNON);
        weapon.setOffset(6, -6);
        weapon.setOwner(this);

        final Handler handler = services.get(Handler.class);
        handler.add(weapon);

        final Viewer viewer = services.get(Viewer.class);

        addFeature(new RefreshableModel(extrp ->
        {
            pointTarget();
            location += speed;
            transformable.setLocation(x + UtilMath.cos(location * 1.5) * 60, y + UtilMath.sin(location * 2) * 30);
            sprite.setLocation(viewer, transformable);
            weapon.fire(target);
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
        collidable.addAccept(target.getFeature(Collidable.class).getGroup().intValue());
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
    public void notifyCollided(Collidable collidable)
    {
        collidable.getFeature(Identifiable.class).destroy();
    }
}
