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

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.b3dgs.lionengine.Configuration;
import com.b3dgs.lionengine.DefinitionException;
import com.b3dgs.lionengine.JsonArray;
import com.b3dgs.lionengine.JsonObject;
import com.b3dgs.lionengine.JsonValue;

/**
 * Reading of the {@code animations} of a sprite sheet definition: named animations, each with its frames, frame
 * duration, optional frame durations and loop mode, read as {@link Animation} values (ADR-0026, ADR-0033,
 * REQ-SHEET-003, REQ-ANIM-001).
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
     * @return The animations by name, in the order of the file, unmodifiable; empty if none.
     * @throws DefinitionException If an animation is not valid.
     */
    static Map<String, Animation> read(Configuration sheet, JsonObject root, List<JsonArray> frames)
    {
        final JsonValue member = root.get(ANIMATIONS);
        final Map<String, Animation> result = new LinkedHashMap<>();
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
                    final Configuration animation = animations.getObject(name);
                    result.put(name, readAnimation(animation));
                    frames.add(animation.getArray(FRAMES));
                }
            }
        }
        return Collections.unmodifiableMap(result);
    }

    /**
     * Reads and checks one animation.
     *
     * @param animation The configuration of the animation.
     * @return The animation, its frames not yet checked against the cells of the sheet.
     * @throws DefinitionException If the animation is not valid.
     */
    private static Animation readAnimation(Configuration animation)
    {
        return readAnimation(animation, readFrames(animation));
    }

    /**
     * Reads and checks the rest of an animation, once its frames are read.
     *
     * @param animation The configuration of the animation.
     * @param frames The frames of the animation, checked.
     * @return The animation, its frames not yet checked against the cells of the sheet.
     * @throws DefinitionException If the animation is not valid.
     */
    private static Animation readAnimation(Configuration animation, int[] frames)
    {
        final double duration = animation.getDouble(FRAME_DURATION);
        if (!Animation.isDuration(duration))
        {
            throw animation.createError(FRAME_DURATION, ERROR_DURATION + duration);
        }
        final JsonArray list = animation.getArray(FRAME_DURATIONS, null);
        double[] durations = null;
        if (list != null)
        {
            durations = readDurations(animation, list, frames.length);
        }
        final String name = animation.getString(LOOP);
        final AnimationLoop loop = AnimationLoop.fromName(name);
        if (loop == null)
        {
            throw animation.createError(LOOP, ERROR_LOOP + name);
        }
        final Animation result;
        if (durations == null)
        {
            result = new Animation(frames, duration, loop);
        }
        else
        {
            result = new Animation(frames, durations, loop);
        }
        return result;
    }

    /**
     * Reads and checks the frames of an animation: at least one, each zero or positive.
     *
     * @param animation The configuration of the animation.
     * @return The frames.
     * @throws DefinitionException If the frames are not valid.
     */
    private static int[] readFrames(Configuration animation)
    {
        final JsonArray list = animation.getArray(FRAMES);
        final int count = list.size();
        if (count == 0)
        {
            throw animation.createError(FRAMES, ERROR_EMPTY);
        }
        final int[] frames = new int[count];
        for (int i = 0; i < count; i++)
        {
            final JsonValue frame = list.get(i);
            frames[i] = frame.asInt();
            if (frames[i] < 0)
            {
                throw new DefinitionException(ERROR_FRAME + frames[i], frame, null, FRAMES);
            }
        }
        return frames;
    }

    /**
     * Reads and checks the frame durations: one positive number per frame.
     *
     * @param animation The configuration of the animation.
     * @param durations The durations.
     * @param frames The number of frames.
     * @return The durations in seconds.
     * @throws DefinitionException If the durations are not valid.
     */
    private static double[] readDurations(Configuration animation, JsonArray durations, int frames)
    {
        final int count = durations.size();
        if (count != frames)
        {
            throw animation.createError(FRAME_DURATIONS, ERROR_DURATIONS + frames + ", durations: " + count);
        }
        final double[] result = new double[count];
        for (int i = 0; i < count; i++)
        {
            final JsonValue duration = durations.get(i);
            result[i] = duration.asDouble();
            if (!Animation.isDuration(result[i]))
            {
                throw new DefinitionException(ERROR_DURATION + result[i], duration, null, FRAME_DURATIONS);
            }
        }
        return result;
    }

    /**
     * Private constructor.
     */
    private SheetAnimations()
    {
        throw new UnsupportedOperationException();
    }
}
