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
package com.b3dgs.lionengine.example.game.selector;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.RoutineRender;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.collidable.Collision;
import com.b3dgs.lionengine.game.feature.collidable.selector.Selectable;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;

/**
 * Soldier entity implementation.
 */
@FeatureInterface
public final class Soldier extends FeatureModel implements RoutineRender
{
    /** Media reference. */
    public static final Media MEDIA = Medias.create("OrcSoldier.xml");

    /** Viewer reference. */
    private final Viewer viewer = services.get(Viewer.class);

    private final Transformable transformable;
    private final Selectable selectable;

    /**
     * Create a soldier.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     * @param transformable The transformable feature.
     * @param collidable The collidable feature.
     * @param selectable The selectable feature.
     */
    public Soldier(Services services,
                   Setup setup,
                   Transformable transformable,
                   Collidable collidable,
                   Selectable selectable)
    {
        super(services, setup);

        this.transformable = transformable;
        this.selectable = selectable;

        collidable.setGroup(Integer.valueOf(0));
        collidable.addCollision(Collision.AUTOMATIC);
        collidable.setOrigin(Origin.BOTTOM_LEFT);
        collidable.setEnabled(true);
    }

    @Override
    public void render(Graphic g)
    {
        if (selectable.isSelected())
        {
            g.setColor(ColorRgba.GREEN);
            g.drawRect(viewer, Origin.BOTTOM_LEFT, transformable, false);
        }
    }
}
