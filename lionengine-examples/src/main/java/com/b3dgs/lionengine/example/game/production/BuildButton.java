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
package com.b3dgs.lionengine.example.game.production;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.core.awt.Mouse;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.Image;
import com.b3dgs.lionengine.game.Cursor;
import com.b3dgs.lionengine.game.handler.DisplayableModel;
import com.b3dgs.lionengine.game.handler.Handler;
import com.b3dgs.lionengine.game.handler.RefreshableModel;
import com.b3dgs.lionengine.game.handler.Service;
import com.b3dgs.lionengine.game.layer.Layerable;
import com.b3dgs.lionengine.game.layer.LayerableModel;
import com.b3dgs.lionengine.game.object.Factory;
import com.b3dgs.lionengine.game.object.ObjectGame;
import com.b3dgs.lionengine.game.object.SetupSurface;
import com.b3dgs.lionengine.game.object.SizeConfig;
import com.b3dgs.lionengine.game.object.feature.actionable.Actionable;
import com.b3dgs.lionengine.game.object.feature.actionable.ActionableModel;
import com.b3dgs.lionengine.game.object.feature.assignable.Assignable;
import com.b3dgs.lionengine.game.object.feature.assignable.AssignableModel;
import com.b3dgs.lionengine.game.object.feature.producible.Producer;
import com.b3dgs.lionengine.game.object.feature.producible.Producible;
import com.b3dgs.lionengine.game.pathfinding.Pathfindable;
import com.b3dgs.lionengine.geom.Geom;
import com.b3dgs.lionengine.geom.Rectangle;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Text;
import com.b3dgs.lionengine.graphic.Viewer;
import com.b3dgs.lionengine.stream.Xml;
import com.b3dgs.lionengine.util.UtilMath;

/**
 * Build button action.
 */
class BuildButton extends ObjectGame
{
    /** Build farm media. */
    public static final Media FARM = Medias.create("BuildFarm.xml");
    /** Build barracks media. */
    public static final Media BARRACKS = Medias.create("BuildBarracks.xml");

    private Updatable state;
    private Rectangle area;

    @Service private Text text;
    @Service private Viewer viewer;
    @Service private Cursor cursor;
    @Service private Factory factory;
    @Service private Handler handler;

    /**
     * Create build button action.
     * 
     * @param setup The setup reference.
     */
    public BuildButton(SetupSurface setup)
    {
        super(setup);

        final Layerable layerable = addFeatureAndGet(new LayerableModel());
        layerable.setLayer(Integer.valueOf(3));

        final Actionable actionable = addFeatureAndGet(new ActionableModel(setup));
        actionable.setClickAction(Mouse.LEFT);
        state = actionable;

        final Assignable assignable = addFeatureAndGet(new AssignableModel());
        assignable.setClickAssign(Mouse.LEFT);

        final Media target = Medias.create(setup.getConfigurer().getText("media"));

        actionable.setAction(() ->
        {
            state = assignable;
            final SizeConfig size = SizeConfig.imports(Xml.load(target));
            area = Geom.createRectangle(UtilMath.getRounded(cursor.getX(), cursor.getWidth()),
                                        UtilMath.getRounded(cursor.getY(), cursor.getHeight()),
                                        size.getWidth(),
                                        size.getHeight());
        });

        assignable.setAssign(() ->
        {
            for (final Producer producer : handler.get(Producer.class))
            {
                final ObjectGame farm = factory.create(target);
                final Producible producible = farm.getFeature(Producible.class);
                producible.setLocation(UtilMath.getRounded(cursor.getX(), cursor.getWidth()),
                                       UtilMath.getRounded(cursor.getY(), cursor.getHeight()));
                producer.addToProductionQueue(producible);

                final int x = (int) (producible.getX() + producible.getWidth() / 2) / cursor.getWidth();
                final int y = (int) (producible.getY() - producible.getHeight() / 2) / cursor.getHeight();
                producer.getOwner().getFeature(Pathfindable.class).setDestination(x, y);
            }
            area = null;
            state = actionable;
        });

        final Image image = Drawable.loadImage(setup.getSurface());

        addFeature(new RefreshableModel(extrp ->
        {
            image.setLocation(actionable.getButton().getX(), actionable.getButton().getY());
            if (actionable.isOver())
            {
                text.setText(actionable.getDescription());
            }
            state.update(extrp);
            if (area != null)
            {
                area.set(UtilMath.getRounded(cursor.getX(), cursor.getWidth()),
                         UtilMath.getRounded(cursor.getY(), cursor.getHeight()),
                         area.getWidth(),
                         area.getHeight());
            }
        }));

        addFeature(new DisplayableModel(g ->
        {
            image.render(g);
            if (area != null)
            {
                g.setColor(ColorRgba.GREEN);
                g.drawRect(viewer,
                           Origin.TOP_LEFT,
                           area.getX(),
                           area.getY(),
                           (int) area.getWidth(),
                           (int) area.getHeight(),
                           false);
            }
        }));
    }
}
