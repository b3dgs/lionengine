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
package com.b3dgs.lionengine.example.game.selector;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.core.drawable.Drawable;
import com.b3dgs.lionengine.game.FeaturableModel;
import com.b3dgs.lionengine.game.Service;
import com.b3dgs.lionengine.game.SetupSurface;
import com.b3dgs.lionengine.game.feature.DisplayableModel;
import com.b3dgs.lionengine.game.feature.LayerableModel;
import com.b3dgs.lionengine.game.feature.RefreshableModel;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.TransformableModel;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.collidable.CollidableModel;
import com.b3dgs.lionengine.game.feature.selector.SelectorListener;
import com.b3dgs.lionengine.geom.Rectangle;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.SpriteAnimated;

/**
 * Peon entity implementation.
 */
class Peon extends FeaturableModel implements SelectorListener
{
    /** Media reference. */
    public static final Media MEDIA = Medias.create("Peon.xml");

    private final Collidable collidable;
    private boolean selected;

    @Service private Viewer viewer;

    /**
     * Create a peon.
     * 
     * @param setup The setup reference.
     */
    public Peon(SetupSurface setup)
    {
        super();

        addFeature(new LayerableModel(1));

        final Transformable transformable = addFeatureAndGet(new TransformableModel(setup));
        collidable = addFeatureAndGet(new CollidableModel(setup));

        final SpriteAnimated surface = Drawable.loadSpriteAnimated(setup.getSurface(), 15, 9);
        surface.setOrigin(Origin.MIDDLE);
        surface.setFrameOffsets(-8, -8);

        transformable.teleport(240, 160);
        collidable.setOrigin(Origin.MIDDLE);

        addFeature(new RefreshableModel(extrp ->
        {
            surface.setLocation(viewer, transformable);
        }));

        addFeature(new DisplayableModel(g ->
        {
            surface.render(g);
            if (selected)
            {
                g.setColor(ColorRgba.GREEN);
                g.drawRect(viewer,
                           Origin.MIDDLE,
                           transformable.getX() + 8,
                           transformable.getY() + 8,
                           transformable.getWidth(),
                           transformable.getHeight(),
                           false);
            }
        }));
    }

    /*
     * SelectorListener
     */

    @Override
    public void notifySelectionStarted(Rectangle selection)
    {
        selected = false;
    }

    @Override
    public void notifySelectionDone(Rectangle selection)
    {
        for (final Rectangle rectangle : collidable.getCollisionBounds())
        {
            if (selection.contains(rectangle) || selection.intersects(rectangle))
            {
                selected = true;
                break;
            }
        }
    }
}
