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

import java.nio.charset.StandardCharsets;

import com.b3dgs.lionengine.resources.CacheConventionContract;
import com.b3dgs.lionengine.resources.ResourceKey;
import com.b3dgs.lionengine.resources.Resources;
import com.b3dgs.lionengine.testsupport.RecordingBackend;

/**
 * Binds the cache convention suite of the {@code resources} test JAR on {@link ImageCache} (ADR-0045, REQ-RES-004,
 * REQ-RES-005, REQ-SPRITE-002).
 */
final class ImageCacheConventionTest extends CacheConventionContract<Image>
{
    /** The cache of the current test. */
    private ImageCache cache;

    @Override
    protected void createCache(Resources resources)
    {
        cache = new ImageCache(resources, new RecordingBackend());
    }

    @Override
    protected ResourceKey key(String name)
    {
        return new ResourceKey("images/" + name + ".png");
    }

    @Override
    protected byte[] getContent(int variant)
    {
        return PngWriter.rgba(2, 1, 0xFF000000 | variant, 0x80FFFFFF).write();
    }

    @Override
    protected byte[] getInvalidContent()
    {
        return "Not a PNG image".getBytes(StandardCharsets.UTF_8);
    }

    @Override
    protected Class<? extends RuntimeException> getDecodingException()
    {
        return ImageDecodingException.class;
    }

    @Override
    protected Image acquire(ResourceKey key)
    {
        return cache.load(key);
    }

    @Override
    protected void release(Image handle)
    {
        handle.release();
    }

    @Override
    protected boolean isShared(Image first, Image second)
    {
        return first.getTexture() == second.getTexture();
    }

    @Override
    protected int getCachedCount()
    {
        return cache.size();
    }
}
