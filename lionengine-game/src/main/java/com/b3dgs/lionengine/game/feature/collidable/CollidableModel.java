/*
 * Copyright (C) 2013-2018 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.collidable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.IdentifiableListener;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.TransformableListener;
import com.b3dgs.lionengine.geom.Rectangle;
import com.b3dgs.lionengine.graphic.Graphic;

/**
 * Box ray cast collidable model implementation.
 */
public class CollidableModel extends FeatureModel
                             implements Collidable, Recyclable, TransformableListener, IdentifiableListener
{
    /** Collision updater. */
    private final CollidableUpdater updater = new CollidableUpdater();
    /** Collision renderer. */
    private final CollidableRenderer renderer = new CollidableRenderer();
    /** The collision listener reference. */
    private final List<CollidableListener> listeners = new ArrayList<>();
    /** The collisions used. */
    private final List<Collision> collisions = new ArrayList<>();
    /** The accepted groups. */
    private final Collection<Integer> accepted = new HashSet<>();
    /** Bounding box cache for rendering. */
    private final Map<Collision, Rectangle> cacheRectRender = new HashMap<>();
    /** The viewer reference. */
    private final Viewer viewer;

    /** Associated group ID. */
    private Integer group = Integer.valueOf(0);
    /** Transformable owning this model. */
    private Transformable transformable;
    /** Origin used. */
    private Origin origin = Origin.TOP_LEFT;

    /**
     * Create a collidable model.
     * <p>
     * The {@link Services} must provide:
     * </p>
     * <ul>
     * <li>{@link Viewer}</li>
     * </ul>
     * <p>
     * The {@link Featurable} must have:
     * </p>
     * <ul>
     * <li>{@link Transformable}</li>
     * </ul>
     * <p>
     * If the {@link Featurable} is a {@link CollidableListener}, it will automatically
     * {@link #addListener(CollidableListener)} on it.
     * </p>
     * 
     * @param services The services reference.
     */
    public CollidableModel(Services services)
    {
        super();

        viewer = services.get(Viewer.class);

        recycle();
    }

    /**
     * Create a collidable model.
     * <p>
     * The {@link Services} must provide:
     * </p>
     * <ul>
     * <li>{@link Viewer}</li>
     * </ul>
     * <p>
     * The {@link Featurable} must have:
     * </p>
     * <ul>
     * <li>{@link Transformable}</li>
     * </ul>
     * <p>
     * If the {@link Featurable} is a {@link CollidableListener}, it will automatically
     * {@link #addListener(CollidableListener)} on it.
     * </p>
     * 
     * @param services The services reference.
     * @param setup The setup reference, must provide a valid {@link CollisionConfig}.
     */
    public CollidableModel(Services services, Setup setup)
    {
        super();

        viewer = services.get(Viewer.class);

        group = CollidableConfig.imports(setup);
        collisions.addAll(CollisionConfig.imports(setup).getCollisions());

        recycle();
    }

    /*
     * Collidable
     */

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        transformable = provider.getFeature(Transformable.class);
        transformable.addListener(this);

        if (provider instanceof CollidableListener)
        {
            addListener((CollidableListener) provider);
        }
    }

    @Override
    public void checkListener(Object listener)
    {
        super.checkListener(listener);

        if (listener instanceof CollidableListener)
        {
            addListener((CollidableListener) listener);
        }
    }

    @Override
    public void addListener(CollidableListener listener)
    {
        listeners.add(listener);
    }

    @Override
    public void removeListener(CollidableListener listener)
    {
        listeners.remove(listener);
    }

    @Override
    public void addCollision(Collision collision)
    {
        collisions.add(collision);
    }

    @Override
    public void addAccept(int group)
    {
        final Integer id = Integer.valueOf(group);
        accepted.add(id);
    }

    @Override
    public void removeAccept(int group)
    {
        final Integer id = Integer.valueOf(group);
        accepted.remove(id);
    }

    @Override
    public void forceUpdate()
    {
        notifyTransformed(transformable);
    }

    @Override
    public List<CollisionCouple> collide(Collidable other)
    {
        return updater.collide(origin, this, transformable, other, accepted);
    }

    @Override
    public void render(Graphic g)
    {
        if (isEnabled())
        {
            renderer.render(g, viewer, origin, transformable, updater.getCache(), cacheRectRender, updater::isEnabled);
        }
    }

    @Override
    public void setGroup(int group)
    {
        this.group = Integer.valueOf(group);
    }

    @Override
    public void setOrigin(Origin origin)
    {
        this.origin = origin;
    }

    @Override
    public void setEnabled(boolean enabled, Collision collision)
    {
        updater.setEnabled(enabled, collision);
    }

    @Override
    public void setEnabled(boolean enabled)
    {
        updater.setEnabled(enabled);
    }

    @Override
    public void setCollisionVisibility(boolean visible)
    {
        renderer.setCollisionVisibility(visible);
    }

    @Override
    public boolean isEnabled()
    {
        return updater.isEnabled();
    }

    @Override
    public List<Collision> getCollisions()
    {
        return collisions;
    }

    @Override
    public List<Rectangle> getCollisionBounds()
    {
        return updater.getCollisionBounds();
    }

    @Override
    public void notifyCollided(Collidable collidable, Collision with, Collision by)
    {
        final int length = listeners.size();
        for (int i = 0; i < length; i++)
        {
            final CollidableListener listener = listeners.get(i);
            listener.notifyCollided(collidable, with, by);
        }
    }

    @Override
    public Integer getGroup()
    {
        return group;
    }

    @Override
    public Collection<Integer> getAccepted()
    {
        return accepted;
    }

    @Override
    public int getMaxWidth()
    {
        return updater.getMaxWidth();
    }

    @Override
    public int getMaxHeight()
    {
        return updater.getMaxHeight();
    }

    @Override
    public Origin getOrigin()
    {
        return origin;
    }

    /*
     * Recyclable
     */

    @Override
    public final void recycle()
    {
        updater.setEnabled(true);
    }

    /*
     * TransformableListener
     */

    @Override
    public void notifyTransformed(Transformable transformable)
    {
        updater.notifyTransformed(origin, this, transformable, collisions, cacheRectRender);
    }

    /*
     * IdentifiableListener
     */

    @Override
    public void notifyDestroyed(Integer id)
    {
        updater.notifyDestroyed(id);
    }

    /*
     * CollidableChecker
     */

    @Override
    public boolean isEnabled(Collision collision)
    {
        return updater.isEnabled(collision);
    }
}
