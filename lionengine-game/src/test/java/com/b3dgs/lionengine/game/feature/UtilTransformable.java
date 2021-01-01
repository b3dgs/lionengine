/*
 * Copyright (C) 2013-2021 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.game.SizeConfig;

/**
 * Utilities dedicated to transformable test.
 */
public final class UtilTransformable
{
    /**
     * Create the object media.
     * 
     * @param caller The caller class.
     * @return The object media.
     */
    public static Media createMedia(Class<?> caller)
    {
        final Xml root = new Xml("test");
        root.add(FeaturableConfig.exportClass(Featurable.class.getName()));
        root.add(SizeConfig.exports(new SizeConfig(16, 32)));

        final Media media = Medias.create("transformable_" + caller.getSimpleName() + Factory.FILE_DATA_DOT_EXTENSION);
        root.save(media);

        return media;
    }
}
