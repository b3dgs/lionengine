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
package com.b3dgs.lionengine.example.game.production;

import com.b3dgs.lionengine.Localizable;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.XmlReader;
import com.b3dgs.lionengine.awt.MouseAwt;
import com.b3dgs.lionengine.game.Cursor;
import com.b3dgs.lionengine.game.SizeConfig;
import com.b3dgs.lionengine.game.feature.Actionable;
import com.b3dgs.lionengine.game.feature.ActionableModel;
import com.b3dgs.lionengine.game.feature.DisplayableModel;
import com.b3dgs.lionengine.game.feature.Factory;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.FeaturableModel;
import com.b3dgs.lionengine.game.feature.Handler;
import com.b3dgs.lionengine.game.feature.LayerableModel;
import com.b3dgs.lionengine.game.feature.RefreshableModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.assignable.Assignable;
import com.b3dgs.lionengine.game.feature.assignable.AssignableModel;
import com.b3dgs.lionengine.game.feature.producible.Producer;
import com.b3dgs.lionengine.game.feature.producible.Producible;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.Pathfindable;
import com.b3dgs.lionengine.geom.Rectangle;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Text;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.Image;

/**
 * Build button action.
 */
public final class BuildButton extends FeaturableModel
{
    /** Build farm media. */
    public static final Media FARM = Medias.create("BuildFarm.xml");
    /** Build barracks media. */
    public static final Media BARRACKS = Medias.create("BuildBarracks.xml");

    private Updatable state;
    private Rectangle area;

    /**
     * Create build button action.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     */
    public BuildButton(Services services, Setup setup)
    {
        super(services, setup);

        addFeature(new LayerableModel(3));

        final Actionable actionable = addFeature(ActionableModel.class, services, setup);
        actionable.setClickAction(MouseAwt.LEFT);
        state = actionable;

        final Assignable assignable = addFeature(AssignableModel.class, services, setup);
        assignable.setClickAssign(MouseAwt.LEFT);

        final Media target = Medias.create(setup.getText("media"));
        final Cursor cursor = services.get(Cursor.class);

        actionable.setAction(() ->
        {
            state = assignable;
            final SizeConfig size = SizeConfig.imports(new XmlReader(target));
            area = new Rectangle(0, 0, size.getWidth(), size.getHeight());
            cursor.setVisible(false);
        });

        final Factory factory = services.get(Factory.class);
        final Handler handler = services.get(Handler.class);

        assignable.setAssign(() ->
        {
            for (final Producer producer : handler.get(Producer.class))
            {
                final Featurable building = factory.create(target);
                final Producible producible = building.getFeature(Producible.class);
                producible.setLocation(area.getX(), area.getY() - 16);
                producer.addToProductionQueue(building);
                producer.getFeature(Pathfindable.class).setDestination(area);
            }
            area = null;
            state = actionable;
            cursor.setVisible(true);
        });

        final Image image = Drawable.loadImage(setup.getSurface());
        final Text text = services.get(Text.class);

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
                         area.getWidthReal(),
                         area.getHeightReal());
            }
        }));

        final Viewer viewer = services.get(Viewer.class);

        addFeature(new DisplayableModel(g ->
        {
            image.render(g);
            if (area != null && viewer.isViewable((Localizable) cursor, 0, 0))
            {
                g.setColor(ColorRgba.GREEN);
                g.drawRect(viewer, Origin.TOP_LEFT, area, false);
            }
        }));
    }
}
