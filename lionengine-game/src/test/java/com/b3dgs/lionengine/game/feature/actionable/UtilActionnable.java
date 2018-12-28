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
package com.b3dgs.lionengine.game.feature.actionable;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.game.Action;
import com.b3dgs.lionengine.game.ActionConfig;
import com.b3dgs.lionengine.game.Cursor;
import com.b3dgs.lionengine.game.feature.ActionableModel;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.FeaturableModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.geom.Area;

/**
 * Utilities dedicated to actionnable test.
 */
final class UtilActionnable
{
    /**
     * Create a default action.
     * 
     * @param description The description.
     * @param area The button.
     * @return The temp media.
     */
    public static Media createAction(String description, Area area)
    {
        final Media media = Medias.create("action.xml");
        final String name = "name";
        final ActionConfig action = new ActionConfig(name,
                                                     description,
                                                     (int) area.getX(),
                                                     (int) area.getY(),
                                                     area.getWidth(),
                                                     area.getHeight());
        final Xml root = new Xml("test");
        root.add(ActionConfig.exports(action));
        root.save(media);

        return media;
    }

    /**
     * Create the services.
     * 
     * @param clicked The click flag.
     * @param clickNumber The click number recorded.
     * @return The services.
     */
    public static Services createServices(final AtomicBoolean clicked, final AtomicInteger clickNumber)
    {
        final Services services = new Services();
        final Cursor cursor = new Cursor()
        {
            @Override
            public boolean hasClickedOnce(int click)
            {
                clickNumber.set(click);
                return clicked.get();
            }
        };
        cursor.setArea(0, 0, 32, 32);
        cursor.setLocation(0, 1);
        services.add(cursor);

        return services;
    }

    /**
     * Create the actionable.
     * 
     * @param media The media.
     * @param services The services.
     * @return The prepared actionable.
     */
    public static ActionableModel createActionable(Media media, Services services)
    {
        final Setup setup = new Setup(media);
        final Featurable featurable = new FeaturableModel();
        final ActionableModel actionable = new ActionableModel(services, setup);
        actionable.prepare(featurable);

        return actionable;
    }

    /**
     * Create a default action.
     * 
     * @param executed The execution flag.
     * @return The created action.
     */
    public static Action createAction(final AtomicBoolean executed)
    {
        return () -> executed.set(true);
    }
}
