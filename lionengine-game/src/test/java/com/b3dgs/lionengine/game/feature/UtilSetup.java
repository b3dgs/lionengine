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
package com.b3dgs.lionengine.game.feature;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Xml;

/**
 * Setup configuration utility.
 */
public final class UtilSetup
{
    /**
     * Create a test configuration.
     * 
     * @param caller The caller reference.
     * @return The test configuration media.
     */
    public static Media createConfig(Class<?> caller)
    {
        final Xml root = new Xml(Constant.XML_PREFIX + "featurable");
        final Media media = Medias.create("Object" + caller.getSimpleName() + Factory.FILE_DATA_DOT_EXTENSION);
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
        final Xml root = new Xml("test");
        root.add(FeaturableConfig.exportClass(clazz.getName()));

        final Media media = Medias.create(clazz.getSimpleName() + ".xml");
        root.save(media);

        return media;
    }
}
