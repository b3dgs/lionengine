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
package com.b3dgs.lionengine.core.awt;

import org.junit.Assert;
import org.junit.Test;

import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.Resolution;

/**
 * Test {@link AppletAwt}.
 */
public final class AppletAwtTest
{
    /**
     * Test applet.
     */
    @Test
    public void testApplet()
    {
        final AppletAwt applet = new AppletAwt();

        final Resolution resolution = new Resolution(320, 240, 60);
        final Config config = new Config(resolution, 32, true);
        config.setApplet(null);
        Assert.assertFalse(config.getApplet(AppletAwt.class).isPresent());

        config.setApplet(applet);
        Assert.assertTrue(config.getApplet(AppletAwt.class).isPresent());
    }
}
