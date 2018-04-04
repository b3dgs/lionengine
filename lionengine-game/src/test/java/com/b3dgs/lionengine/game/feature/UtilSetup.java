/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.game.SizeConfig;

/**
 * Setup configuration utility.
 */
public class UtilSetup
{
    /**
     * Create a test configuration.
     * 
     * @return The test configuration media.
     */
    public static Media createConfig()
    {
        final Media media = Medias.create("object.xml");

        final Xml root = new Xml("test");
        root.add(FeaturableConfig.exportClass("class"));
        root.add(FeaturableConfig.exportSetup("setup"));
        root.save(media);

        return media;
    }

    /**
     * Create the object media.
     * 
     * @param clazz The class type.
     * @return The object media.
     */
    public static Media createMedia(Class<?> clazz)
    {
        final Media media = Medias.create(clazz.getSimpleName() + ".xml");
        final Xml root = new Xml("test");
        root.add(FeaturableConfig.exportClass(clazz.getName()));
        root.add(FeaturableConfig.exportSetup(Setup.class.getName()));
        root.add(SizeConfig.exports(new SizeConfig(16, 32)));
        root.save(media);
        return media;
    }
}
