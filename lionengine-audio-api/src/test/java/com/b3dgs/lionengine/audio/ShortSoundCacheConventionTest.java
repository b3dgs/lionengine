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
package com.b3dgs.lionengine.audio;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import com.b3dgs.lionengine.resources.CacheConventionContract;
import com.b3dgs.lionengine.resources.ResourceKey;
import com.b3dgs.lionengine.resources.Resources;

/**
 * Binds the cache convention suite of the {@code resources} test JAR on {@link ShortSoundCache} (ADR-0045, ADR-0055,
 * ADR-0066, REQ-RES-004, REQ-RES-005, REQ-RT-003, REQ-RT-005, REQ-AUDIO-003).
 */
final class ShortSoundCacheConventionTest extends CacheConventionContract<ShortSound>
{
    /** Format of the sounds. */
    private static final PcmFormat FORMAT = new PcmFormat(8_000, PcmFormat.MONO);

    /** The cache of the current test. */
    private ShortSoundCache cache;

    /**
     * Returns the samples of a variant.
     *
     * @param variant The variant.
     * @return The samples: two frames, the first one naming the variant.
     */
    private static short[] samples(int variant)
    {
        return new short[]
        {
            (short) variant, 1_000
        };
    }

    @Override
    protected void createCache(Resources resources)
    {
        cache = new ShortSoundCache(resources);
    }

    @Override
    protected ResourceKey key(String name)
    {
        return new ResourceKey("sounds/" + name + ".wav");
    }

    @Override
    protected byte[] getContent(int variant)
    {
        return WavWriter.write(new PcmBuffer(FORMAT, samples(variant)), 16);
    }

    @Override
    protected byte[] getInvalidContent()
    {
        return "Not a WAV sound".getBytes(StandardCharsets.UTF_8);
    }

    @Override
    protected Class<? extends RuntimeException> getDecodingException()
    {
        return AudioDecodingException.class;
    }

    @Override
    protected ShortSound acquire(ResourceKey key)
    {
        return cache.load(key);
    }

    @Override
    protected void release(ShortSound handle)
    {
        handle.release();
    }

    @Override
    protected void reload(ResourceKey key)
    {
        cache.reload(key);
    }

    @Override
    protected boolean hasContent(ShortSound handle, int variant)
    {
        final short[] samples = new short[2];
        handle.getBuffer().readFrames(0, samples, 0, 2);
        return Arrays.equals(samples(variant), samples);
    }

    @Override
    protected boolean isShared(ShortSound first, ShortSound second)
    {
        return first.getBuffer() == second.getBuffer();
    }

    @Override
    protected int getCachedCount()
    {
        return cache.size();
    }
}
