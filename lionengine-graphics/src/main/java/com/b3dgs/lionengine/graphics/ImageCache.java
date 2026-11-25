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

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.b3dgs.lionengine.ThreadConfinement;
import com.b3dgs.lionengine.render.TextureStore;
import com.b3dgs.lionengine.resources.ResourceException;
import com.b3dgs.lionengine.resources.ResourceKey;
import com.b3dgs.lionengine.resources.Resources;

/**
 * Cache of decoded images, owned by the game and shared by its scenes (ADR-0005, ADR-0007, ADR-0018, ADR-0019,
 * ADR-0026, ADR-0047, ADR-0055, REQ-SPRITE-002, REQ-RES-004, REQ-RES-005, REQ-RT-003, REQ-RT-005, REQ-SPR-001).
 *
 * <p>
 * An image is cached under its resource key and a discriminator: {@link #DECODED} for the image decoded from the
 * resource, any other string for an image derived from it, such as a modified copy or, later, a color variant.
 * {@link #load(ResourceKey)} decodes the image at its first acquisition, through {@link Resources} and the decoder
 * registered for the extension of the key, then shares it: every acquisition returns a new {@link Image} handle on the
 * same immutable pixels and texture. Each handle is released by the code that acquired it; an image leaves the cache,
 * and its texture is released, with its last handle. Nothing is evicted automatically.
 * </p>
 *
 * <p>
 * Memory: {@link #getUsedBytes()} counts 4 bytes per cached pixel. A limit in bytes is opt-in, none by default
 * ({@link #NO_LIMIT}): with a limit, an image that would exceed it is refused with an {@link IllegalStateException}
 * and nothing is cached, since every cached image is in use and nothing can be evicted (ADR-0055).
 * </p>
 *
 * <p>
 * Decoders are chosen by the extension of the last segment of the key, after its last dot, compared exactly (asset
 * names are lower case, ADR-0023). The built-in {@link PngDecoder}, with its default limits (ADR-0054), is registered
 * for {@code png}; {@link #setDecoder(String, ImageDecoder)} replaces it (for example with a lower pixel limit) or adds
 * other formats, such as the optional decoder modules; nothing is discovered (ADR-0020).
 * </p>
 *
 * <p>
 * Textures: the cache creates the texture of an image when the image enters the cache (no GPU work happens then), and
 * releases it when the image leaves; the pixels are shared read-only with the texture store (ADR-0047), which keeps
 * them to upload the texture again after a lost rendering context, so a context loss needs nothing from the cache. The
 * upload happens at the first frame that draws the image, or at {@link Image#prepare()}. During development,
 * {@link #reload(ResourceKey)} decodes an image again after its file changed (ADR-0019).
 * </p>
 *
 * <p>
 * Threads: runtime thread only (ADR-0018). The cache is not thread-safe and loading workers never touch it; the thread
 * of its first operation becomes its owner, and every operation of the cache, and the release and preparation of its
 * handles, on another thread throws an {@link IllegalStateException} (ADR-0055). Loading is synchronous; asynchronous
 * loading comes with the asynchronous handles of the game loop (ADR-0019).
 * </p>
 *
 * @since 11.0.0
 */
public final class ImageCache
{
    /** Discriminator of the image decoded from its resource: empty. */
    public static final String DECODED = "";
    /** Limit of a cache without limit, in bytes: the default. */
    public static final long NO_LIMIT = Long.MAX_VALUE;

    /** Bytes held per cached pixel: one packed {@code int}. */
    private static final long BYTES_PER_PIXEL = 4L;

    /** Extension of the PNG images. */
    private static final String PNG = "png";
    /** Extension of a key without extension. */
    private static final String NO_EXTENSION = "";
    /** Separator of the segments of a key. */
    private static final char SEGMENT = '/';
    /** Separator of an extension. */
    private static final char DOT = '.';
    /** Name of the key argument, for the messages. */
    private static final String NAME_KEY = "Key";
    /** Name of the discriminator argument, for the messages. */
    private static final String NAME_DISCRIMINATOR = "Discriminator";
    /** Error message of a {@code null} argument, after its name, as in the other modules. */
    private static final String ERROR_NULL = " must not be null";
    /** Error message of an invalid extension. */
    private static final String ERROR_EXTENSION = "Extension empty or with a dot: ";
    /** Error message of a key without decoder. */
    private static final String ERROR_NO_DECODER = "No image decoder for the extension of: ";
    /** Error message of an empty discriminator given to add. */
    private static final String ERROR_DECODED = "Discriminator empty: the decoded image of a key comes from load: ";
    /** Error message of an image already cached. */
    private static final String ERROR_CACHED = "Image already cached: ";
    /** Error message of an image not cached. */
    private static final String ERROR_NOT_CACHED = "Image not cached: ";
    /** Separator of a key and its discriminator in messages. */
    private static final String SEPARATOR = " / ";
    /** Separator of a key and a detail in messages. */
    private static final String COLON = ": ";
    /** Error message of a limit that is not positive. */
    private static final String ERROR_MAX_BYTES = "Limit not positive: ";
    /** Error message of an exceeded limit: key. */
    private static final String ERROR_LIMIT = "Image cache limit exceeded by ";
    /** Error message of an exceeded limit: bytes needed. */
    private static final String ERROR_LIMIT_NEEDED = " bytes needed, ";
    /** Error message of an exceeded limit: bytes used. */
    private static final String ERROR_LIMIT_USED = " of ";
    /** Error message of an exceeded limit: unit. */
    private static final String ERROR_LIMIT_UNIT = " bytes used";

    /**
     * Checks that an argument is not {@code null}, for the public types of the package.
     *
     * @param value The argument.
     * @param name The name of the argument, for the message.
     * @throws IllegalArgumentException If the argument is {@code null}.
     */
    static void checkNotNull(Object value, String name)
    {
        if (value == null)
        {
            throw new IllegalArgumentException(name + ERROR_NULL);
        }
    }

    /**
     * Returns the bytes held by pixels.
     *
     * @param width The width in pixels.
     * @param height The height in pixels.
     * @return The bytes, 4 per pixel.
     */
    static long bytes(int width, int height)
    {
        return BYTES_PER_PIXEL * width * height;
    }

    /**
     * Returns the extension of a key: the end of its last segment after the last dot.
     *
     * @param key The key.
     * @return The extension, empty if none.
     */
    private static String extension(ResourceKey key)
    {
        final String path = key.path();
        final int dot = path.lastIndexOf(DOT);
        final String extension;
        if (dot > path.lastIndexOf(SEGMENT))
        {
            extension = path.substring(dot + 1);
        }
        else
        {
            extension = NO_EXTENSION;
        }
        return extension;
    }

    /** The resources of the images. */
    private final Resources resources;
    /** The texture store of the render backend. */
    private final TextureStore textures;
    /** Decoders by extension. */
    private final Map<String, ImageDecoder> decoders = new HashMap<>();
    /** Cached images. */
    private final Map<ImageKey, ImageEntry> entries = new HashMap<>();
    /** The runtime thread check. */
    private final ThreadConfinement thread = new ThreadConfinement("ImageCache");
    /** The limit of the cached pixels in bytes, {@link #NO_LIMIT} if none. */
    private final long maxBytes;
    /** The bytes held by the cached pixels. */
    private long usedBytes;

    /**
     * Creates an empty cache without limit, with {@link PngDecoder} registered for {@code png}.
     *
     * @param resources The resources the images are read from (never {@code null}).
     * @param textures The texture store of the render backend, which receives the texture of every cached image
     *            (never {@code null}).
     * @throws IllegalArgumentException If an argument is {@code null}.
     */
    public ImageCache(Resources resources, TextureStore textures)
    {
        this(resources, textures, NO_LIMIT);
    }

    /**
     * Creates an empty cache with a limit, with {@link PngDecoder} registered for {@code png}.
     *
     * @param resources The resources the images are read from (never {@code null}).
     * @param textures The texture store of the render backend, which receives the texture of every cached image
     *            (never {@code null}).
     * @param maxBytes The limit of the cached pixels in bytes, 4 per pixel (positive; {@link #NO_LIMIT} for none).
     * @throws IllegalArgumentException If an argument is {@code null} or the limit is not positive.
     */
    public ImageCache(Resources resources, TextureStore textures, long maxBytes)
    {
        super();

        checkNotNull(resources, "Resources");
        checkNotNull(textures, "Textures");
        if (maxBytes <= 0L)
        {
            throw new IllegalArgumentException(ERROR_MAX_BYTES + maxBytes);
        }
        this.resources = resources;
        this.textures = textures;
        this.maxBytes = maxBytes;
        decoders.put(PNG, new PngDecoder());
    }

    /**
     * Registers the decoder of an extension, replacing the previous one.
     *
     * @param extension The extension, without dot, compared exactly with the end of the keys (not empty).
     * @param decoder The decoder (never {@code null}).
     * @throws IllegalArgumentException If the extension is {@code null}, empty or has a dot, or the decoder is
     *             {@code null}.
     * @throws IllegalStateException If called on another thread than the owner of the cache.
     */
    public void setDecoder(String extension, ImageDecoder decoder)
    {
        thread.check();
        checkNotNull(extension, "Extension");
        checkNotNull(decoder, "Decoder");
        if (extension.isEmpty() || extension.indexOf(DOT) >= 0)
        {
            throw new IllegalArgumentException(ERROR_EXTENSION + extension);
        }
        decoders.put(extension, decoder);
    }

    /**
     * Acquires the image decoded from a resource: decoded at its first acquisition, shared afterwards.
     *
     * @param key The resource key (never {@code null}).
     * @return A new handle on the image, to release once it is no longer needed.
     * @throws IllegalArgumentException If the key is {@code null}.
     * @throws ResourceException If the resource is missing ({@code MISSING}) or cannot be read
     *             ({@code INACCESSIBLE}).
     * @throws ImageDecodingException If no decoder is registered for the extension of the key, or the content is not
     *             a valid image; the message starts with the key.
     * @throws IllegalStateException If the image would exceed the limit of the cache, or if called on another thread
     *             than the owner of the cache.
     */
    public Image load(ResourceKey key)
    {
        thread.check();
        checkNotNull(key, NAME_KEY);
        final ImageKey imageKey = new ImageKey(key, DECODED);
        ImageEntry entry = entries.get(imageKey);
        if (entry == null)
        {
            final PixelBuffer pixels = decode(key);
            final long bytes = bytes(pixels.getWidth(), pixels.getHeight());
            reserve(key, bytes, 0L);
            entry = new ImageEntry(this, imageKey, pixels.getWidth(), pixels.getHeight(), pixels.array());
            entries.put(imageKey, entry);
            usedBytes += bytes;
        }
        return entry.acquire();
    }

    /**
     * Adds a copy of pixels to the cache as an image derived from a resource, for example a modified copy of its
     * decoded image (ADR-0026, REQ-SPR-001).
     *
     * @param key The resource key the image derives from (never {@code null}).
     * @param discriminator The discriminator of the derived image (not empty: {@link #DECODED} is reserved for the
     *            decoded image).
     * @param pixels The pixels, copied: the caller keeps its buffer (never {@code null}).
     * @return A new handle on the image, to release once it is no longer needed.
     * @throws IllegalArgumentException If an argument is {@code null} or the discriminator is empty.
     * @throws IllegalStateException If an image is already cached under this key and discriminator, if the image
     *             would exceed the limit of the cache, or if called on another thread than the owner of the cache.
     */
    public Image add(ResourceKey key, String discriminator, PixelBuffer pixels)
    {
        thread.check();
        checkNotNull(key, NAME_KEY);
        checkNotNull(discriminator, NAME_DISCRIMINATOR);
        checkNotNull(pixels, "Pixels");
        if (discriminator.isEmpty())
        {
            throw new IllegalArgumentException(ERROR_DECODED + key);
        }
        final ImageKey imageKey = new ImageKey(key, discriminator);
        if (entries.containsKey(imageKey))
        {
            throw new IllegalStateException(ERROR_CACHED + key + SEPARATOR + discriminator);
        }
        final long bytes = bytes(pixels.getWidth(), pixels.getHeight());
        reserve(key, bytes, 0L);
        final ImageEntry entry = new ImageEntry(this,
                                                imageKey,
                                                pixels.getWidth(),
                                                pixels.getHeight(),
                                                pixels.copyPixels());
        entries.put(imageKey, entry);
        usedBytes += bytes;
        return entry.acquire();
    }

    /**
     * Acquires an image already cached, decoded or derived, without reading anything.
     *
     * @param key The resource key (never {@code null}).
     * @param discriminator The discriminator, {@link #DECODED} for the decoded image (never {@code null}).
     * @return A new handle on the image, to release once it is no longer needed.
     * @throws IllegalArgumentException If an argument is {@code null}.
     * @throws IllegalStateException If no image is cached under this key and discriminator (see
     *             {@link #has(ResourceKey, String)}), or if called on another thread than the owner of the cache.
     */
    public Image acquire(ResourceKey key, String discriminator)
    {
        thread.check();
        checkNotNull(key, NAME_KEY);
        checkNotNull(discriminator, NAME_DISCRIMINATOR);
        final ImageEntry entry = entries.get(new ImageKey(key, discriminator));
        if (entry == null)
        {
            throw new IllegalStateException(ERROR_NOT_CACHED + key + SEPARATOR + discriminator);
        }
        return entry.acquire();
    }

    /**
     * Tells whether an image is cached.
     *
     * @param key The resource key (never {@code null}).
     * @param discriminator The discriminator, {@link #DECODED} for the decoded image (never {@code null}).
     * @return {@code true} while at least one handle on the image is not released.
     * @throws IllegalArgumentException If an argument is {@code null}.
     * @throws IllegalStateException If called on another thread than the owner of the cache.
     */
    public boolean has(ResourceKey key, String discriminator)
    {
        thread.check();
        checkNotNull(key, NAME_KEY);
        checkNotNull(discriminator, NAME_DISCRIMINATOR);
        return entries.containsKey(new ImageKey(key, discriminator));
    }

    /**
     * Returns the number of cached images, for example to check that a scene released everything it acquired.
     *
     * @return The number of images with at least one handle not released.
     * @throws IllegalStateException If called on another thread than the owner of the cache.
     */
    public int size()
    {
        thread.check();
        return entries.size();
    }

    /**
     * Decodes again the image of a resource, during development, after its file changed (ADR-0019, ADR-0055).
     *
     * <p>
     * Only the decoded image ({@link #DECODED}) is read again; images derived from it keep their pixels. On success,
     * every handle on the image gives the new size, pixels and texture: the cache creates the new texture and releases
     * the previous one. Sprite sheets over the image keep the grid computed when they were added: after a reload that
     * changes the size, release and add them again. Call it between two frames, from the update phase or from an
     * inbox event (for example a development key), never while the render phase is writing commands that name the
     * previous texture. On failure the image is unchanged.
     * </p>
     *
     * @param key The resource key of a cached image (never {@code null}).
     * @throws IllegalArgumentException If the key is {@code null}.
     * @throws IllegalStateException If the decoded image of the key is not cached, if the new image would exceed the
     *             limit of the cache, or if called on another thread than the owner of the cache.
     * @throws ResourceException If the resource is missing ({@code MISSING}) or cannot be read
     *             ({@code INACCESSIBLE}).
     * @throws ImageDecodingException If the content is not a valid image; the message starts with the key.
     */
    public void reload(ResourceKey key)
    {
        thread.check();
        checkNotNull(key, NAME_KEY);
        final ImageEntry entry = entries.get(new ImageKey(key, DECODED));
        if (entry == null)
        {
            throw new IllegalStateException(ERROR_NOT_CACHED + key + SEPARATOR + DECODED);
        }
        final PixelBuffer pixels = decode(key);
        final long bytes = bytes(pixels.getWidth(), pixels.getHeight());
        final long previous = entry.getBytes();
        reserve(key, bytes, previous);
        entry.replace(pixels.getWidth(), pixels.getHeight(), pixels.array());
        usedBytes += bytes - previous;
    }

    /**
     * Returns the memory held by the cached pixels.
     *
     * @return The bytes of the cached images, 4 per pixel, derived images included.
     * @throws IllegalStateException If called on another thread than the owner of the cache.
     */
    public long getUsedBytes()
    {
        thread.check();
        return usedBytes;
    }

    /**
     * Returns the limit of the cached pixels.
     *
     * @return The limit in bytes, {@link #NO_LIMIT} if the cache has none.
     */
    public long getMaxBytes()
    {
        return maxBytes;
    }

    /**
     * Checks that the calling thread owns the cache, for the handles.
     *
     * @throws IllegalStateException If called on another thread than the owner of the cache.
     */
    void checkThread()
    {
        thread.check();
    }

    /**
     * Returns the texture store, for the entries.
     *
     * @return The texture store.
     */
    TextureStore getTextures()
    {
        return textures;
    }

    /**
     * Removes an entry whose last handle was released.
     *
     * @param key The key of the entry.
     * @param bytes The bytes held by its pixels.
     */
    void remove(ImageKey key, long bytes)
    {
        entries.remove(key);
        usedBytes -= bytes;
    }

    /**
     * Checks that pixels fit the limit of the cache.
     *
     * @param key The resource key of the image, for the message.
     * @param bytes The bytes of the pixels to cache.
     * @param freed The bytes freed at the same time, by an image replaced.
     * @throws IllegalStateException If the pixels would exceed the limit.
     */
    private void reserve(ResourceKey key, long bytes, long freed)
    {
        if (bytes > maxBytes - usedBytes + freed)
        {
            throw new IllegalStateException(ERROR_LIMIT
                                            + key
                                            + COLON
                                            + bytes
                                            + ERROR_LIMIT_NEEDED
                                            + usedBytes
                                            + ERROR_LIMIT_USED
                                            + maxBytes
                                            + ERROR_LIMIT_UNIT);
        }
    }

    /**
     * Reads and decodes an image, and closes its stream.
     *
     * @param key The resource key.
     * @return The pixels, owned by the cache.
     * @throws ResourceException If the resource is missing or cannot be read.
     * @throws ImageDecodingException If no decoder is registered, or the content is invalid.
     */
    private PixelBuffer decode(ResourceKey key)
    {
        final ImageDecoder decoder = decoders.get(extension(key));
        if (decoder == null)
        {
            throw new ImageDecodingException(ERROR_NO_DECODER + key);
        }
        try (InputStream input = resources.open(key))
        {
            return decoder.decode(input);
        }
        catch (final IOException exception)
        {
            throw new ResourceException(key, ResourceException.Reason.INACCESSIBLE, exception);
        }
        catch (final ImageDecodingException exception)
        {
            throw new ImageDecodingException(key + COLON + exception.getMessage(), exception);
        }
    }
}
