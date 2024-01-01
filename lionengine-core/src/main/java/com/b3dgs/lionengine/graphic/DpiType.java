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
package com.b3dgs.lionengine.graphic;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Resolution;

/**
 * Represents the type of DPI.
 * <p>
 * This class is Thread-Safe.
 * </p>
 */
public enum DpiType
{
    /** Low pixel density (scale factor 0.75). Generally for 240p. */
    LDPI,
    /** Baseline pixel density (scale factor 1.0). Generally for 480p. */
    MDPI,
    /** High pixel density (scale factor 1.5). Generally for 720p. */
    HDPI,
    /** Extra high pixel density (scale factor 2.0). Generally for 1080p. */
    XHDPI,
    /** Extra extra high pixel density (scale factor 3.0). Generally for 1440p. */
    XXHDPI;

    /** Baseline factor. */
    private static final double FACTOR_BASELINE = 1.0;
    /** High factor. */
    private static final double FACTOR_HIGH = 1.5;
    /** Extra high factor. */
    private static final double FACTOR_EXTRA_HIGH = 2.0;
    /** Extra extra high factor. */
    private static final double FACTOR_EXTRA_EXTRA_HIGH = 3.0;

    /**
     * Get the DPI type from resolution difference.
     * 
     * @param baseline The baseline resolution (must not be <code>null</code>).
     * @param target The target resolution (must not be <code>null</code>).
     * @return The computed DPI type.
     */
    public static DpiType from(Resolution baseline, Resolution target)
    {
        Check.notNull(baseline);
        Check.notNull(target);

        final double x = target.getWidth() / (double) baseline.getWidth();
        final double y = target.getHeight() / (double) baseline.getHeight();
        final double factor = Math.min(x, y);

        final DpiType type;
        if (factor < FACTOR_BASELINE)
        {
            type = LDPI;
        }
        else if (factor < FACTOR_HIGH)
        {
            type = MDPI;
        }
        else if (factor < FACTOR_EXTRA_HIGH)
        {
            type = HDPI;
        }
        else if (factor < FACTOR_EXTRA_EXTRA_HIGH)
        {
            type = XHDPI;
        }
        else
        {
            type = XXHDPI;
        }
        return type;
    }
}
