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
package com.b3dgs.lionengine.game.map.fog;

import com.b3dgs.lionengine.Localizable;
import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.drawable.SpriteTiled;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Filter;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.ImageBuffer;
import com.b3dgs.lionengine.graphic.Viewer;

/**
 * Sprite tiled mock.
 */
public class SpriteTiledMock implements SpriteTiled
{
    @Override
    public void stretch(double percentWidth, double percentHeight)
    {
        // Mock
    }

    @Override
    public void rotate(int angle)
    {
        // Mock
    }

    @Override
    public void filter(Filter filter)
    {
        // Mock
    }

    @Override
    public void setTransparency(ColorRgba mask)
    {
        // Mock
    }

    @Override
    public void setAlpha(int alpha)
    {
        // Mock
    }

    @Override
    public void setFade(int alpha, int fade)
    {
        // Mock
    }

    @Override
    public void setMirror(Mirror mirror)
    {
        // Mock
    }

    @Override
    public Mirror getMirror()
    {
        return null;
    }

    @Override
    public void load()
    {
        // Mock
    }

    @Override
    public void prepare()
    {
        // Mock
    }

    @Override
    public void setOrigin(Origin origin)
    {
        // Mock
    }

    @Override
    public void setLocation(double x, double y)
    {
        // Mock
    }

    @Override
    public void setLocation(Viewer viewer, Localizable localizable)
    {
        // Mock
    }

    @Override
    public ImageBuffer getSurface()
    {
        return null;
    }

    @Override
    public boolean isLoaded()
    {
        return false;
    }

    @Override
    public int getWidth()
    {
        return 0;
    }

    @Override
    public int getHeight()
    {
        return 0;
    }

    @Override
    public double getX()
    {
        return 0;
    }

    @Override
    public double getY()
    {
        return 0;
    }

    @Override
    public void render(Graphic g)
    {
        // Mock
    }

    @Override
    public void setTile(int tile)
    {
        // Mock
    }

    @Override
    public int getTilesHorizontal()
    {
        return 0;
    }

    @Override
    public int getTilesVertical()
    {
        return 0;
    }

    @Override
    public int getTileWidth()
    {
        return 0;
    }

    @Override
    public int getTileHeight()
    {
        return 0;
    }
}
