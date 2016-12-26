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

import com.b3dgs.lionengine.Localizable;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.core.drawable.Drawable;
import com.b3dgs.lionengine.game.Cursor;
import com.b3dgs.lionengine.game.FeaturableModel;
import com.b3dgs.lionengine.game.Service;
import com.b3dgs.lionengine.game.SetupSurface;
import com.b3dgs.lionengine.game.SizeConfig;
import com.b3dgs.lionengine.game.feature.DisplayableModel;
import com.b3dgs.lionengine.game.feature.Factory;
import com.b3dgs.lionengine.game.feature.Handler;
import com.b3dgs.lionengine.game.feature.LayerableModel;
import com.b3dgs.lionengine.game.feature.RefreshableModel;
import com.b3dgs.lionengine.game.feature.actionable.Actionable;
import com.b3dgs.lionengine.game.feature.actionable.ActionableModel;
import com.b3dgs.lionengine.game.feature.assignable.Assignable;
import com.b3dgs.lionengine.game.feature.assignable.AssignableModel;
import com.b3dgs.lionengine.game.feature.producible.Producer;
import com.b3dgs.lionengine.game.feature.producible.Producible;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.Pathfindable;
import com.b3dgs.lionengine.geom.Rectangle;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Image;
import com.b3dgs.lionengine.graphic.Text;
import com.b3dgs.lionengine.io.Mouse;
import com.b3dgs.lionengine.io.Xml;
import com.b3dgs.lionengine.util.UtilMath;

/**
 * Build button action.
 */
class BuildButton extends FeaturableModel
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
        super();

        addFeature(new LayerableModel(3));

        final Actionable actionable = addFeatureAndGet(new ActionableModel(setup));
        actionable.setClickAction(Mouse.LEFT);
        state = actionable;

        final Assignable assignable = addFeatureAndGet(new AssignableModel());
        assignable.setClickAssign(Mouse.LEFT);

        final Media target = Medias.create(setup.getText("media"));

        actionable.setAction(() ->
        {
            state = assignable;
            final SizeConfig size = SizeConfig.imports(new Xml(target));
            area = new Rectangle(0, 0, size.getWidth(), size.getHeight());
            cursor.setVisible(false);
        });

        assignable.setAssign(() ->
        {
            for (final Producer producer : handler.get(Producer.class))
            {
                final Building building = factory.create(target);
                final Producible producible = building.getFeature(Producible.class);
                producible.setLocation(area.getX(), area.getY());
                producer.addToProductionQueue(building);
                producer.getFeature(Pathfindable.class).setDestination(area);
            }
            area = null;
            state = actionable;
            cursor.setVisible(true);
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
                         UtilMath.getRoundedC(cursor.getY(), cursor.getHeight()),
                         area.getWidthReal(),
                         area.getHeightReal());
            }
        }));

        addFeature(new DisplayableModel(g ->
        {
            image.render(g);
            if (area != null && viewer.isViewable((Localizable) cursor, 0, 0))
            {
                g.setColor(ColorRgba.GREEN);
                g.drawRect(viewer, Origin.TOP_LEFT, area.getX(), area.getY(), area.getWidth(), area.getHeight(), false);
            }
        }));
    }
}
