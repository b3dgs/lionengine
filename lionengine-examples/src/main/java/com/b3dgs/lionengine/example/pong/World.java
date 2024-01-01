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
package com.b3dgs.lionengine.example.pong;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.helper.WorldHelper;

/**
 * World representation.
 */
public final class World extends WorldHelper
{
    /** Racket media. */
    private static final Media RACKET = Medias.create("Racket.xml");
    /** Racket media. */
    private static final Media BALL = Medias.create("Ball.xml");

    /**
     * Create world.
     * 
     * @param services The services reference.
     */
    public World(Services services)
    {
        super(services);

        camera.setView(source, 0, 0, Origin.TOP_LEFT);

        final Featurable racket1 = factory.create(RACKET);
        racket1.getFeature(Racket.class).setSide(true);
        handler.add(racket1);

        final Featurable racket2 = factory.create(RACKET);
        racket2.getFeature(Racket.class).setSide(false);
        handler.add(racket2);

        final Featurable ball = factory.create(BALL);
        handler.add(ball);

        racket1.getFeature(Racket.class).setBall(ball.getFeature(Ball.class));
        racket2.getFeature(Racket.class).setBall(ball.getFeature(Ball.class));
    }

    @Override
    public void render(Graphic g)
    {
        g.clear(0, 0, source.getWidth(), source.getHeight());

        super.render(g);
    }
}
