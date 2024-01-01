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
package com.b3dgs.lionengine.game.it.feature.tile.map.collision;

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.game.TextGame;
import com.b3dgs.lionengine.game.feature.Displayable;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.TextStyle;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.SpriteAnimated;

/**
 * Rendering of our controllable entity.
 */
@FeatureInterface
public final class MarioRenderer extends FeatureModel implements Displayable
{
    private final Viewer viewer = services.get(Viewer.class);

    private final Collidable collidable;
    private final Transformable transformable;

    private final TextGame text = new TextGame(Constant.FONT_SERIF, 12, TextStyle.NORMAL);
    private final SpriteAnimated surface;

    /**
     * Constructor.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     * @param collidable The collidable feature.
     * @param transformable The transformable feature.
     */
    public MarioRenderer(Services services, Setup setup, Collidable collidable, Transformable transformable)
    {
        super(services, setup);

        this.collidable = collidable;
        this.transformable = transformable;

        surface = Drawable.loadSpriteAnimated(setup.getSurface(), 7, 1);
        surface.setOrigin(Origin.CENTER_BOTTOM);

        text.setAlign(Align.CENTER);
        text.setColor(ColorRgba.BLACK);
        text.setText(Mario.class.getSimpleName());

        collidable.setCollisionVisibility(true);
        surface.setFrameOffsets(0, -1);
    }

    @Override
    public void render(Graphic g)
    {
        surface.setLocation(viewer, transformable);
        surface.render(g);

        text.update(viewer);
        text.setLocation((int) transformable.getX(), (int) transformable.getY());
        g.setColor(ColorRgba.BLACK);
        text.draw(g, transformable, 0, 28, Align.CENTER, Mario.class.getSimpleName());
        text.render(g);

        g.setColor(ColorRgba.GREEN);
        collidable.render(g);
    }
}
