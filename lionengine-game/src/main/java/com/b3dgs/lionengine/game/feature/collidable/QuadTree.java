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
package com.b3dgs.lionengine.game.feature.collidable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.game.TextGame;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.geom.Area;
import com.b3dgs.lionengine.geom.Rectangle;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Renderable;
import com.b3dgs.lionengine.graphic.TextStyle;

/**
 * Quad tree dedicated to collisions.
 */
public class QuadTree implements Renderable
{
    private static final TextGame TEXT = new TextGame("System", 9, TextStyle.NORMAL);
    private static final int MAX_NODES = 4;
    private static final int MIN_SIZE = 64;
    private static final int LEFT = 0;
    private static final int RIGHT = 1;
    private static final int BOTTOM = 0;
    private static final int TOP = 1;

    private final List<Collidable> refs = new ArrayList<>();
    private final Viewer viewer;
    private final int x;
    private final int y;
    private final int size;

    /** Children, can be <code>null</code> if none. */
    private QuadTree[][] children;
    /** Current number of references. */
    private int count;

    /**
     * Create tree.
     * 
     * @param viewer The viewer reference (must not be <code>null</code>).
     * @param size The maximum size (must be strictly positive, power of 2 recommended).
     */
    public QuadTree(Viewer viewer, int size)
    {
        this(viewer, size / 2, size / 2, size);
    }

    /**
     * Create internal tree.
     * 
     * @param viewer The viewer reference (must not be <code>null</code>).
     * @param x The horizontal center location.
     * @param y The vertical center location.
     * @param size The maximum size (must be strictly positive).
     */
    private QuadTree(Viewer viewer, int x, int y, int size)
    {
        super();

        Check.notNull(viewer);
        Check.superiorOrEqual(size, 0);

        this.viewer = viewer;
        this.x = x;
        this.y = y;
        this.size = size;
    }

    /**
     * Add collidable.
     * 
     * @param ref The collidable reference.
     */
    public void add(Collidable ref)
    {
        if (count < MAX_NODES || size < MIN_SIZE)
        {
            if (!refs.contains(ref))
            {
                refs.add(ref);
                count++;
            }
        }
        else
        {
            if (children == null)
            {
                createSplit();
            }

            final int n = refs.size();
            for (int i = 0; i < n; i++)
            {
                addInternal(refs.get(i));
            }
            addInternal(ref);
            refs.clear();
        }
    }

    /**
     * Remove collidable from tree.
     * 
     * @param transformable The transformable reference.
     * @param ref The collidable reference.
     */
    public void remove(Transformable transformable, Collidable ref)
    {
        if (count > 0)
        {
            if (refs.remove(ref))
            {
                count--;
            }

            if (children != null)
            {
                final int minX = getIndexX(transformable.getOldX() - ref.getMaxWidth() * 2);
                final int minY = getIndexY(transformable.getOldY() + ref.getMinHeight() * 2);
                final int maxX = getIndexX(transformable.getOldX() + ref.getMaxWidth() * 2);
                final int maxY = getIndexY(transformable.getOldY() + ref.getMaxHeight() * 2);

                for (int iy = minY; iy <= maxY; iy++)
                {
                    final QuadTree[] trees = children[iy];

                    for (int ix = minX; ix <= maxX; ix++)
                    {
                        trees[ix].remove(transformable, ref);
                    }
                }
            }
        }
        if (children != null)
        {
            checkMerge();
        }
    }

    /**
     * Update index.
     * 
     * @param transformable The transformable reference.
     * @param ref The collidable reference.
     */
    public void move(Transformable transformable, Collidable ref)
    {
        remove(transformable, ref);
        add(ref);
    }

    /**
     * Compute collision with all elements.
     */
    public void compute()
    {
        final int n = refs.size();
        if (n > 1)
        {
            for (int i = 0; i < n - 1; i++)
            {
                final Collidable a = refs.get(i);
                for (int j = i + 1; j < n; j++)
                {
                    final Collidable b = refs.get(j);
                    if (a != b)
                    {
                        a.collide(b);
                        b.collide(a);
                    }
                }
            }
        }

        if (children != null)
        {
            children[BOTTOM][LEFT].compute();
            children[BOTTOM][RIGHT].compute();
            children[TOP][LEFT].compute();
            children[TOP][RIGHT].compute();
        }
    }

    /**
     * Get all collidables inside.
     * 
     * @param area The area used.
     * @return The collidable inside.
     */
    public List<Collidable> getInside(Area area)
    {
        final Set<Collidable> result = new HashSet<>();
        checkInside(area, result);

        if (children != null)
        {
            final int minX = getIndexX(area.getX() - area.getWidth());
            final int minY = getIndexY(area.getY() - area.getHeight());
            final int maxX = getIndexX(area.getX() + area.getWidth());
            final int maxY = getIndexY(area.getY() + area.getHeight());

            for (int iy = minY; iy <= maxY; iy++)
            {
                final QuadTree[] trees = children[iy];

                for (int ix = minX; ix <= maxX; ix++)
                {
                    final QuadTree tree = trees[ix];

                    tree.checkInside(area, result);
                    result.addAll(tree.getInside(area));
                }
            }
        }
        return new ArrayList<>(result);
    }

    @Override
    public void render(Graphic g)
    {
        final int n = refs.size();
        if (n > 0)
        {
            g.drawRect(viewer, Origin.MIDDLE, x, y, n, n, false);

            TEXT.update(viewer);
            TEXT.draw(g, x, y, Align.CENTER, Integer.toString(n));
        }

        if (children != null)
        {
            children[BOTTOM][LEFT].render(g);
            children[BOTTOM][RIGHT].render(g);
            children[TOP][LEFT].render(g);
            children[TOP][RIGHT].render(g);
        }
    }

    /**
     * Get the horizontal index.
     * 
     * @param value The horizontal element location.
     * @return The horizontal tree index (0 if left, 1 if right).
     */
    private int getIndexX(double value)
    {
        if (value < x)
        {
            return 0;
        }
        return 1;
    }

    /**
     * Get the vertical index.
     * 
     * @param value The vertical element location.
     * @return The vertical tree index (0 if top, 1 if bottom).
     */
    private int getIndexY(double value)
    {
        if (value < y)
        {
            return 0;
        }
        return 1;
    }

    /**
     * Create split children.
     */
    private void createSplit()
    {
        children = new QuadTree[2][2];

        final int half = size / 2;
        children[BOTTOM][LEFT] = new QuadTree(viewer, x - half / 2, y - half / 2, half);
        children[BOTTOM][RIGHT] = new QuadTree(viewer, x + half / 2, y - half / 2, half);
        children[TOP][LEFT] = new QuadTree(viewer, x - half / 2, y + half / 2, half);
        children[TOP][RIGHT] = new QuadTree(viewer, x + half / 2, y + half / 2, half);
    }

    /**
     * Add collidable in tree depending of its indexes.
     * 
     * @param ref The collidable reference.
     */
    private void addInternal(Collidable ref)
    {
        final int minX = getIndexX(ref.getX() - ref.getMaxWidth() / 2);
        final int minY = getIndexY(ref.getY() + ref.getMinHeight());
        final int maxX = getIndexX(ref.getX() + ref.getMaxWidth() / 2);
        final int maxY = getIndexY(ref.getY() + ref.getMaxHeight());

        for (int iy = minY; iy <= maxY; iy++)
        {
            final QuadTree[] trees = children[iy];

            for (int ix = minX; ix <= maxX; ix++)
            {
                trees[ix].add(ref);
            }
        }
    }

    /**
     * Check what is inside area.
     * 
     * @param area The area used.
     * @param result The elements found.
     */
    private void checkInside(Area area, Set<Collidable> result)
    {
        final int n = refs.size();
        for (int i = 0; i < n; i++)
        {
            final Collidable current = refs.get(i);
            final List<Rectangle> bounds = current.getCollisionBounds();

            final int k = bounds.size();
            for (int j = 0; j < k; j++)
            {
                final Rectangle bound = bounds.get(j);
                if (area.contains(bound) || area.intersects(bound))
                {
                    result.add(current);
                }
            }
        }
    }

    /**
     * Check merge nodes if all empty.
     */
    private void checkMerge()
    {
        if (children[BOTTOM][LEFT].count == 0
            && children[BOTTOM][RIGHT].count == 0
            && children[TOP][LEFT].count == 0
            && children[TOP][RIGHT].count == 0)
        {
            children[BOTTOM][LEFT] = null;
            children[BOTTOM][RIGHT] = null;
            children[TOP][LEFT] = null;
            children[TOP][RIGHT] = null;
            children = null;
            refs.clear();
            count = 0;
        }
    }
}
