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
package com.b3dgs.lionengine.game.handler;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.core.Config;
import com.b3dgs.lionengine.core.Context;
import com.b3dgs.lionengine.core.Engine;
import com.b3dgs.lionengine.core.Resolution;
import com.b3dgs.lionengine.core.Sequencable;
import com.b3dgs.lionengine.core.Sequence;
import com.b3dgs.lionengine.core.Sequencer;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.graphic.Graphic;

/**
 * Sequence base dedicated to game module, supporting base tools by default.
 * <p>
 * The following tools are included:
 * </p>
 * <ul>
 * <li>{@link Services}: providing {@link Context} and {@link Sequencer} to control sequence (available after
 * {@link #load()}).</li>
 * <li>{@link WorldGame}: added to {@link Services}, {@link #update(double)} and {@link #render(Graphic)} are already
 * called.</li>
 * <li>{@link #setSystemCursorVisible(boolean)}: set to <code>false</code>.</li>
 * </ul>
 */
public abstract class SequenceGame extends Sequence
{
    /** Services instance. */
    protected final Services services = new Services();
    /** Handled world. */
    protected final WorldGame world;

    /**
     * Create sequence. Resolution will be based on {@link Config#getOutput()}.
     * 
     * @param context The context reference.
     * @throws LionEngineException If invalid arguments.
     */
    public SequenceGame(Context context)
    {
        this(context, context.getConfig().getOutput());
    }

    /**
     * Create sequence.
     * 
     * @param context The context reference.
     * @param resolution The resolution source reference.
     * @throws LionEngineException If invalid arguments.
     */
    public SequenceGame(Context context, Resolution resolution)
    {
        super(context, resolution);

        services.add(context);
        services.add(new Sequencer()
        {
            @Override
            public void end()
            {
                SequenceGame.this.end();
            }

            @Override
            public void end(Class<? extends Sequencable> nextSequenceClass, Object... arguments)
            {
                SequenceGame.this.end(nextSequenceClass, arguments);
            }
        });

        world = services.add(createWorld(context, services));

        setSystemCursorVisible(false);
    }

    /**
     * Create the world.
     * 
     * @param context The context reference.
     * @param services The services reference.
     * @return The created world.
     */
    protected abstract WorldGame createWorld(Context context, Services services);

    /*
     * Sequence
     */

    @Override
    public void update(double extrp)
    {
        world.update(extrp);
    }

    @Override
    public void render(Graphic g)
    {
        world.render(g);
    }

    /**
     * Called when sequence is closing.
     * {@link Engine#terminate()} called if does not have next sequence.
     * 
     * @param hasNextSequence <code>true</code> if there is a next sequence, <code>false</code> else (then application
     *            will end definitely).
     */
    @Override
    public void onTerminated(boolean hasNextSequence)
    {
        if (!hasNextSequence)
        {
            Engine.terminate();
        }
    }
}
