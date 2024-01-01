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
package com.b3dgs.lionengine.example.game.projectile;

import com.b3dgs.lionengine.Localizable;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.UtilRandom;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.game.feature.DisplayableModel;
import com.b3dgs.lionengine.game.feature.Factory;
import com.b3dgs.lionengine.game.feature.FeaturableModel;
import com.b3dgs.lionengine.game.feature.Handler;
import com.b3dgs.lionengine.game.feature.Identifiable;
import com.b3dgs.lionengine.game.feature.LayerableModel;
import com.b3dgs.lionengine.game.feature.Mirrorable;
import com.b3dgs.lionengine.game.feature.MirrorableModel;
import com.b3dgs.lionengine.game.feature.RefreshableModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.TransformableModel;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.collidable.CollidableListener;
import com.b3dgs.lionengine.game.feature.collidable.CollidableModel;
import com.b3dgs.lionengine.game.feature.collidable.Collision;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.Sprite;

/**
 * Ship implementation.
 */
public final class Ship extends FeaturableModel implements CollidableListener
{
    /** Media. */
    public static final Media MEDIA = Medias.create("Ship.xml");

    private static int group = 1;

    private final double speed = UtilRandom.getRandomDouble() / 1.5 + 0.75;
    private final Transformable transformable;
    private final Sprite sprite;
    private final Weapon weapon;
    private final Collidable collidable;
    private final Mirrorable mirrorable;

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
        super(services, setup);

        addFeature(new LayerableModel(1));

        transformable = addFeature(TransformableModel.class, services, setup);
        transformable.teleport(x + UtilMath.cos(location * 1.5) * 60, y + UtilMath.sin(location * 2) * 30);

        collidable = addFeature(CollidableModel.class, services, setup);
        collidable.setOrigin(Origin.MIDDLE);
        collidable.setGroup(Integer.valueOf(group++));

        mirrorable = addFeature(MirrorableModel.class, services, setup);

        final Factory factory = services.get(Factory.class);
        weapon = factory.create(Weapon.PULSE_CANNON);
        weapon.setOwner(this);
        weapon.setOffset(0, 0);

        sprite = Drawable.loadSprite(setup.getSurface());
        sprite.setOrigin(Origin.MIDDLE);
        sprite.setFrameOffsets(0, 0);
        sprite.setAngleAnchor(-transformable.getWidth() / 2, -transformable.getHeight() / 2);

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
        mirrorable.mirror(Mirror.VERTICAL);
        sprite.setAngleAnchor(-transformable.getWidth() / 3, transformable.getHeight() / 2);

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
        collidable.addAccept(target.getFeature(Collidable.class).getGroup());
    }

    /**
     * Point the defined target by computing the angle.
     */
    private void pointTarget()
    {
        final double grad = Math.atan((transformable.getX() - target.getX()) / (target.getY() - transformable.getY()));
        final int angle = (int) StrictMath.toDegrees(grad);
        sprite.rotate(-angle + 180);
    }

    @Override
    public void notifyCollided(Collidable collidable, Collision with, Collision by)
    {
        collidable.getFeature(Identifiable.class).destroy();
    }
}
