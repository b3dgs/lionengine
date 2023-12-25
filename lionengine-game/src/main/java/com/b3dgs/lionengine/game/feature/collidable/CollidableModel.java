/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.collidable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.ListenableModel;
import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.OriginConfig;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.IdentifiableListener;
import com.b3dgs.lionengine.game.feature.MirrorableListener;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.TransformableListener;
import com.b3dgs.lionengine.geom.Rectangle;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;

/**
 * Box ray cast collidable model implementation.
 */
public class CollidableModel extends FeatureModel implements Collidable, Recyclable, TransformableListener,
                             MirrorableListener, IdentifiableListener
{
    /** Collision updater. */
    private final CollidableUpdater updater = new CollidableUpdater();
    /** The collision listener reference. */
    private final ListenableModel<CollidableListener> listenable = new ListenableModel<>();
    /** The collisions used. */
    private final List<Collision> collisions = new ArrayList<>();
    /** The accepted groups. */
    private final Set<Integer> accepted = new HashSet<>();
    /** Bounding box cache for rendering. */
    private final Map<Collision, Rectangle> cacheRectRender = new HashMap<>();
    /** The viewer reference. */
    private final Viewer viewer = services.get(Viewer.class);

    /** Associated group Id. */
    private Integer group = Integer.valueOf(0);
    /** Transformable owning this model. */
    private final Transformable transformable;
    /** Origin used. */
    private Origin origin;
    /** Min offset X. */
    private int minWidth;
    /** Min width. */
    private int minHeight;
    /** Max offset X. */
    private int maxWidth;
    /** Max width. */
    private int maxHeight;
    /** Show collision flag. */
    private boolean showCollision;

    /**
     * Create feature.
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
     * The {@link Configurer} can provide a valid {@link CollidableConfig} and {@link CollisionConfig}.
     * </p>
     * <p>
     * If the {@link Featurable} is a {@link CollidableListener}, it will automatically
     * {@link #addListener(CollidableListener)} on it.
     * </p>
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @param transformable The transformable feature.
     * @throws LionEngineException If invalid argument.
     */
    public CollidableModel(Services services, Setup setup, Transformable transformable)
    {
        super(services, setup);

        this.transformable = transformable;

        final CollidableConfig config = CollidableConfig.imports(setup);
        group = config.getGroup();
        accepted.addAll(config.getAccepted());
        collisions.addAll(CollisionConfig.imports(setup).getCollisions());
        updater.setEnabled(!collisions.isEmpty());
        origin = OriginConfig.imports(setup);

        final int n = collisions.size();
        for (int i = 0; i < n; i++)
        {
            computeMinMax(collisions.get(i));
        }
    }

    private void computeMinMax(Collision collision)
    {
        maxWidth = Math.max(maxWidth, collision.getWidth() + collision.getOffsetX() * 2);
        minWidth = maxWidth;
        minHeight = Math.min(minHeight, collision.getOffsetY());
        maxHeight = Math.max(maxHeight, collision.getOffsetY() + collision.getHeight());
    }

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        transformable.addListener(this);

        if (provider instanceof final CollidableListener l)
        {
            addListener(l);
        }
    }

    @Override
    public void checkListener(Object listener)
    {
        super.checkListener(listener);

        if (listener instanceof final CollidableListener l)
        {
            addListener(l);
        }
    }

    @Override
    public void addListener(CollidableListener listener)
    {
        listenable.addListener(listener);
    }

    @Override
    public void removeListener(CollidableListener listener)
    {
        listenable.removeListener(listener);
    }

    @Override
    public void clearListeners()
    {
        listenable.clear();
    }

    @Override
    public void addCollision(Collision collision)
    {
        if (!collisions.contains(collision))
        {
            collisions.add(collision);
            computeMinMax(collision);
        }
    }

    @Override
    public void addAccept(Integer group)
    {
        accepted.add(group);
    }

    @Override
    public void removeAccept(Integer group)
    {
        accepted.remove(group);
    }

    @Override
    public void forceUpdate()
    {
        notifyTransformed(transformable);
    }

    @Override
    public boolean collide(Collidable other)
    {
        return updater.collide(origin, this, transformable, other, accepted);
    }

    @Override
    public void render(Graphic g)
    {
        if (showCollision && isEnabled())
        {
            g.setColor(ColorRgba.RED);
            CollidableRenderer.render(g,
                                      viewer,
                                      origin,
                                      transformable,
                                      updater.getCache(),
                                      cacheRectRender,
                                      updater::isEnabled);

            g.setColor(ColorRgba.GREEN);
            CollidableRenderer.renderMax(g, viewer, origin, transformable, maxWidth, minHeight, maxHeight);
        }
    }

    @Override
    public void setGroup(Integer group)
    {
        this.group = group;
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
        showCollision = visible;
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
        final int n = listenable.size();
        for (int i = 0; i < n; i++)
        {
            listenable.get(i).notifyCollided(collidable, with, by);
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
    public int getMinWidth()
    {
        return minWidth;
    }

    @Override
    public int getMinHeight()
    {
        return minHeight;
    }

    @Override
    public int getMaxWidth()
    {
        return maxWidth;
    }

    @Override
    public int getMaxHeight()
    {
        return maxHeight;
    }

    @Override
    public Origin getOrigin()
    {
        return origin;
    }

    @Override
    public double getX()
    {
        return transformable.getX();
    }

    @Override
    public double getY()
    {
        return transformable.getY();
    }

    @Override
    public int getWidth()
    {
        return transformable.getWidth();
    }

    @Override
    public int getHeight()
    {
        return transformable.getHeight();
    }

    @Override
    public void recycle()
    {
        setEnabled(!collisions.isEmpty());
    }

    @Override
    public void notifyTransformed(Transformable transformable)
    {
        updater.notifyTransformed(origin, this, transformable, collisions, cacheRectRender);
    }

    @Override
    public void notifyMirrored(Mirror old, Mirror next)
    {
        updater.notifyTransformed(origin, this, transformable, collisions, cacheRectRender);
    }

    @Override
    public void notifyDestroyed(Integer id)
    {
        updater.notifyDestroyed(id);
    }

    @Override
    public boolean isEnabled(Collision collision)
    {
        return updater.isEnabled(collision);
    }
}
