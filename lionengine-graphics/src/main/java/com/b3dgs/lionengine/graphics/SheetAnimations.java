/*
 * Copyright (C) 2013-2026 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.graphics;

import java.util.List;

import com.b3dgs.lionengine.Configuration;
import com.b3dgs.lionengine.DefinitionException;
import com.b3dgs.lionengine.JsonArray;
import com.b3dgs.lionengine.JsonObject;
import com.b3dgs.lionengine.JsonValue;

/**
 * Check of the {@code animations} of a sprite sheet definition: named animations, each with its frames, frame
 * duration, optional frame durations and loop mode (ADR-0026, ADR-0033, REQ-SHEET-003, REQ-ANIM-001).
 */
final class SheetAnimations
{
    /** Field of the animations. */
    static final String ANIMATIONS = "animations";
    /** Field of the frames of an animation. */
    static final String FRAMES = "frames";
    /** Field of the frame duration. */
    static final String FRAME_DURATION = "frameDuration";
    /** Field of the frame durations. */
    static final String FRAME_DURATIONS = "frameDurations";
    /** Field of the loop mode. */
    static final String LOOP = "loop";
    /** Loop mode: stop on the last frame. */
    static final String MODE_ONCE = "once";
    /** Loop mode: restart at the first frame, the same word as its field. */
    static final String MODE_LOOP = LOOP;
    /** Loop mode: forward then backward. */
    static final String MODE_PING_PONG = "ping-pong";

    /** Error message of an empty frame list. */
    private static final String ERROR_EMPTY = "No frame";
    /** Error message of a negative frame. */
    private static final String ERROR_FRAME = "Frame negative: ";
    /** Error message of a duration that is not positive. */
    private static final String ERROR_DURATION = "Duration not positive, in seconds: ";
    /** Error message of frame durations of another length than the frames. */
    private static final String ERROR_DURATIONS = "One duration per frame expected, frames: ";
    /** Error message of an unknown loop mode. */
    private static final String ERROR_LOOP = "Unknown loop mode, expected once, loop or ping-pong: ";

    /**
     * Reads and checks the animations of a definition, if any.
     *
     * @param sheet The configuration of the definition.
     * @param root The root object of the definition, for the names of the animations.
     * @param frames The list receiving the frames of every animation, for the check against the cells.
     * @throws DefinitionException If an animation is not valid.
     */
    static void read(Configuration sheet, JsonObject root, List<JsonArray> frames)
    {
        final JsonValue member = root.get(ANIMATIONS);
        if (member != null)
        {
            final Configuration animations = sheet.getObject(ANIMATIONS);
            final JsonObject names = member.asObject();
            final int count = names.size();
            for (int i = 0; i < count; i++)
            {
                final String name = names.getKey(i);
                if (!Configuration.COMMENT.equals(name))
                {
                    frames.add(readAnimation(animations.getObject(name)));
                }
            }
        }
    }

    /**
     * Reads and checks one animation.
     *
     * @param animation The configuration of the animation.
     * @return The frames.
     * @throws DefinitionException If the animation is not valid.
     */
    private static JsonArray readAnimation(Configuration animation)
    {
        final JsonArray frames = animation.getArray(FRAMES);
        final int count = frames.size();
        if (count == 0)
        {
            throw animation.createError(FRAMES, ERROR_EMPTY);
        }
        for (int i = 0; i < count; i++)
        {
            final JsonValue frame = frames.get(i);
            if (frame.asInt() < 0)
            {
                throw new DefinitionException(ERROR_FRAME + frame.asInt(), frame, null, FRAMES);
            }
        }
        final double duration = animation.getDouble(FRAME_DURATION);
        if (duration <= 0.0)
        {
            throw animation.createError(FRAME_DURATION, ERROR_DURATION + duration);
        }
        final JsonArray durations = animation.getArray(FRAME_DURATIONS, null);
        if (durations != null)
        {
            checkDurations(animation, durations, count);
        }
        final String loop = animation.getString(LOOP);
        if (!MODE_ONCE.equals(loop) && !MODE_LOOP.equals(loop) && !MODE_PING_PONG.equals(loop))
        {
            throw animation.createError(LOOP, ERROR_LOOP + loop);
        }
        return frames;
    }

    /**
     * Checks the frame durations: one positive number per frame.
     *
     * @param animation The configuration of the animation.
     * @param durations The durations.
     * @param frames The number of frames.
     * @throws DefinitionException If the durations are not valid.
     */
    private static void checkDurations(Configuration animation, JsonArray durations, int frames)
    {
        final int count = durations.size();
        if (count != frames)
        {
            throw animation.createError(FRAME_DURATIONS, ERROR_DURATIONS + frames + ", durations: " + count);
        }
        for (int i = 0; i < count; i++)
        {
            final JsonValue duration = durations.get(i);
            if (duration.asDouble() <= 0.0)
            {
                throw new DefinitionException(ERROR_DURATION + duration.asDouble(), duration, null, FRAME_DURATIONS);
            }
        }
    }

    /**
     * Private constructor.
     */
    private SheetAnimations()
    {
        throw new UnsupportedOperationException();
    }
}
