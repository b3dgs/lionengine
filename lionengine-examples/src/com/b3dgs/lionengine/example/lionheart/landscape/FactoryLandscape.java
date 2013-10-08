/*
 * Copyright (C) 2013 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.example.lionheart.landscape;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.game.platform.background.BackgroundPlatform;

/**
 * Landscape factory.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class FactoryLandscape
{
    /** Unknown entity error message. */
    private static final String UNKNOWN_LANDSCAPE_ERROR = "Unknown landscape: ";
    /** The resolution source reference. */
    private final Resolution source;
    /** The horizontal factor. */
    private final double scaleH;
    /** The vertical factor. */
    private final double scaleV;
    /** Background flickering flag. */
    private final boolean flicker;

    /**
     * Constructor.
     * 
     * @param source The resolution source reference.
     * @param scaleH The horizontal factor.
     * @param scaleV The horizontal factor.
     * @param flicker The flicker flag.
     */
    public FactoryLandscape(Resolution source, double scaleH, double scaleV, boolean flicker)
    {
        this.source = source;
        this.scaleH = scaleH;
        this.scaleV = scaleV;
        this.flicker = flicker;
    }

    /**
     * Create a landscape from its type.
     * 
     * @param landscape The landscape type.
     * @return The landscape instance.
     */
    public Landscape createLandscape(LandscapeType landscape)
    {
        switch (landscape.getWorld())
        {
            case SWAMP:
            {
                final BackgroundPlatform background = new Swamp(source, scaleH, scaleV, landscape.getTheme(), flicker);
                final Foreground foreground = new Foreground(source, scaleH, scaleV, landscape.getForeground()
                        .getTheme());
                return new Landscape(landscape, background, foreground);
            }
            default:
                throw new LionEngineException(FactoryLandscape.UNKNOWN_LANDSCAPE_ERROR + landscape);
        }
    }
}
