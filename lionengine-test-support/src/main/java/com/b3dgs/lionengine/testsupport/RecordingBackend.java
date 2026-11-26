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
package com.b3dgs.lionengine.testsupport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.b3dgs.lionengine.render.ColorEffect;
import com.b3dgs.lionengine.render.CommandBuffer;
import com.b3dgs.lionengine.render.OutputFilter;
import com.b3dgs.lionengine.render.RenderBackend;
import com.b3dgs.lionengine.render.TextureStore;

/**
 * Render backend that records the frames it presents, for headless tests of rendering code (ADR-0045, ADR-0056,
 * REQ-TEST-001, REQ-SPRITE-004, REQ-SPRITE-010).
 *
 * <p>
 * Each {@link #present(CommandBuffer)} records one frame: the list of the commands in presentation order (ascending
 * layer, then submission order), copied as {@link RecordedCommand}s. Textures are kept with their pixels, so a test
 * can check what was created, prepared and released. The test chooses the output filters it implements;
 * {@link OutputFilter#NONE} always is. It applies every color effect per frame until the test switches one off with
 * {@link #setSupported(ColorEffect, boolean)}, to check the CPU fallback of the rendering code (ADR-0056); the commands
 * are recorded as written either way. Not thread-safe: the test drives it from one thread.
 * </p>
 *
 * @since 11.0.0
 */
public final class RecordingBackend implements RenderBackend, TextureStore
{
    /**
     * Checks a filter.
     *
     * @param filter The filter.
     * @throws IllegalArgumentException If {@code filter} is {@code null}.
     */
    private static void checkFilter(OutputFilter filter)
    {
        if (filter == null)
        {
            throw new IllegalArgumentException("Filter must not be null");
        }
    }

    /**
     * Checks a color effect.
     *
     * @param effect The effect.
     * @throws IllegalArgumentException If {@code effect} is {@code null}.
     */
    private static void checkEffect(ColorEffect effect)
    {
        if (effect == null)
        {
            throw new IllegalArgumentException("Effect must not be null");
        }
    }

    /** Filters implemented. */
    private final Set<OutputFilter> filters = new HashSet<>();
    /** Color effects applied per frame, by ordinal. */
    private final boolean[] effects = new boolean[ColorEffect.values().length];
    /** Recorded frames, oldest first. */
    private final List<List<RecordedCommand>> frames = new ArrayList<>();
    /** Pixels of the live textures by handle. */
    private final Map<Integer, int[]> textures = new HashMap<>();
    /** Sizes of the live textures by handle: width, then height. */
    private final Map<Integer, int[]> sizes = new HashMap<>();
    /** Prepared textures. */
    private final Set<Integer> prepared = new HashSet<>();
    /** Last texture handle. */
    private int lastHandle;

    /**
     * Creates a backend implementing {@link OutputFilter#NONE} and the given filters.
     *
     * @param supported The other filters it implements.
     * @throws IllegalArgumentException If {@code supported} or one of the filters is {@code null}.
     */
    public RecordingBackend(OutputFilter... supported)
    {
        if (supported == null)
        {
            throw new IllegalArgumentException("Filters must not be null");
        }
        Arrays.fill(effects, true);
        filters.add(OutputFilter.NONE);
        for (final OutputFilter filter : supported)
        {
            checkFilter(filter);
            filters.add(filter);
        }
    }

    /**
     * Returns the number of frames presented.
     *
     * @return The number of recorded frames.
     */
    public int getFrameCount()
    {
        return frames.size();
    }

    /**
     * Returns a recorded frame.
     *
     * @param index The frame index, from {@code 0} for the first presented frame.
     * @return The commands of the frame in presentation order, unmodifiable.
     * @throws IndexOutOfBoundsException If no frame has this index.
     */
    public List<RecordedCommand> getFrame(int index)
    {
        return frames.get(index);
    }

    /**
     * Returns the last recorded frame.
     *
     * @return The commands of the last frame in presentation order, unmodifiable.
     * @throws IllegalStateException If no frame was presented.
     */
    public List<RecordedCommand> getLastFrame()
    {
        if (frames.isEmpty())
        {
            throw new IllegalStateException("No frame presented");
        }
        return frames.get(frames.size() - 1);
    }

    /**
     * Tells whether a texture is created and not released.
     *
     * @param texture The texture handle.
     * @return {@code true} if the texture is alive.
     */
    public boolean isAlive(int texture)
    {
        return textures.containsKey(Integer.valueOf(texture));
    }

    /**
     * Tells whether a live texture was prepared.
     *
     * @param texture The texture handle.
     * @return {@code true} if the texture is alive and was prepared.
     */
    public boolean isPrepared(int texture)
    {
        return prepared.contains(Integer.valueOf(texture));
    }

    /**
     * Returns the pixels of a live texture, as given at its creation.
     *
     * @param texture The texture handle.
     * @return The shared pixels, {@code null} if the texture is not alive.
     */
    public int[] getPixels(int texture)
    {
        return textures.get(Integer.valueOf(texture));
    }

    /**
     * Returns the width of a live texture.
     *
     * @param texture The texture handle.
     * @return The width in pixels.
     * @throws IllegalArgumentException If the texture is not alive.
     */
    public int getTextureWidth(int texture)
    {
        return size(texture)[0];
    }

    /**
     * Returns the height of a live texture.
     *
     * @param texture The texture handle.
     * @return The height in pixels.
     * @throws IllegalArgumentException If the texture is not alive.
     */
    public int getTextureHeight(int texture)
    {
        return size(texture)[1];
    }

    /**
     * Returns the size of a live texture.
     *
     * @param texture The texture handle.
     * @return The width and the height.
     * @throws IllegalArgumentException If the texture is not alive.
     */
    private int[] size(int texture)
    {
        final int[] size = sizes.get(Integer.valueOf(texture));
        if (size == null)
        {
            throw new IllegalArgumentException("Texture not alive: " + texture);
        }
        return size;
    }

    /**
     * Tells whether the backend reports a color effect as applied per frame, from now on.
     *
     * @param effect The effect (never {@code null}).
     * @param supported {@code true} to report it as applied per frame (the default), {@code false} as not applied.
     * @throws IllegalArgumentException If {@code effect} is {@code null}.
     */
    public void setSupported(ColorEffect effect, boolean supported)
    {
        checkEffect(effect);
        effects[effect.ordinal()] = supported;
    }

    @Override
    public TextureStore getTextureStore()
    {
        return this;
    }

    @Override
    public boolean isSupported(ColorEffect effect)
    {
        checkEffect(effect);
        return effects[effect.ordinal()];
    }

    @Override
    public boolean isSupported(OutputFilter filter)
    {
        checkFilter(filter);
        return filters.contains(filter);
    }

    @Override
    public void present(CommandBuffer commands)
    {
        if (commands == null)
        {
            throw new IllegalArgumentException("Commands must not be null");
        }
        final List<RecordedCommand> frame = new ArrayList<>(commands.size());
        for (int rank = 0; rank < commands.size(); rank++)
        {
            frame.add(new RecordedCommand(commands, commands.getEntry(rank)));
        }
        frames.add(Collections.unmodifiableList(frame));
    }

    @Override
    public int create(int width, int height, int[] argb)
    {
        if (width <= 0 || height <= 0 || argb == null || argb.length < (long) width * height)
        {
            throw new IllegalArgumentException("Invalid texture: " + width + 'x' + height);
        }
        lastHandle++;
        final Integer handle = Integer.valueOf(lastHandle);
        textures.put(handle, argb);
        sizes.put(handle, new int[]
        {
            width, height
        });
        return lastHandle;
    }

    @Override
    public void prepare(int texture)
    {
        size(texture);
        prepared.add(Integer.valueOf(texture));
    }

    @Override
    public void release(int texture)
    {
        final Integer handle = Integer.valueOf(texture);
        textures.remove(handle);
        sizes.remove(handle);
        prepared.remove(handle);
    }
}
