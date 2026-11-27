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

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.b3dgs.lionengine.AsyncHandle;
import com.b3dgs.lionengine.AsyncLoading;
import com.b3dgs.lionengine.Inbox;
import com.b3dgs.lionengine.ThreadConfinement;
import com.b3dgs.lionengine.WorkerPool;
import com.b3dgs.lionengine.resources.ResourceException;
import com.b3dgs.lionengine.resources.ResourceKey;
import com.b3dgs.lionengine.resources.Resources;

/**
 * Cache of decoded short sounds, owned by the game and shared by its scenes (ADR-0004, ADR-0005, ADR-0007, ADR-0018,
 * ADR-0019, ADR-0027, ADR-0033, ADR-0055, ADR-0066, REQ-AUDIO-003, REQ-AUDIO-013, REQ-RES-004, REQ-RES-005,
 * REQ-RT-003, REQ-RT-004, REQ-RT-005).
 *
 * <p>
 * A short sound is cached under its resource key. {@link #load(ResourceKey)} preloads it: it decodes the sound at its
 * first load, through {@link Resources} and the decoder registered for the extension of the key, then shares it:
 * every load returns a new {@link ShortSound} handle on the same immutable samples. Sounds are preloaded explicitly,
 * normally by the scene that needs them, so that playing never decodes; {@link #loadLazily(ResourceKey)} is the
 * option that decodes at the first play instead. Each handle is released by the code that loaded it; a sound leaves
 * the cache with its last handle. Nothing is evicted automatically.
 * </p>
 *
 * <p>
 * Length: a short sound lasts at most {@link #MAX_SECONDS} seconds; a longer one is refused with an
 * {@link AudioDecodingException} and belongs to the long sounds (ADR-0033). Memory: {@link #getUsedBytes()} counts 2
 * bytes per cached sample. A limit in bytes is opt-in, none by default ({@link #NO_LIMIT}): with a limit, a sound
 * that would exceed it is refused with an {@link IllegalStateException} and nothing is cached, since every cached
 * sound is in use and nothing can be evicted (ADR-0055).
 * </p>
 *
 * <p>
 * Decoders are chosen by the extension of the last segment of the key, after its last dot, compared exactly (asset
 * names are lower case, ADR-0023). The built-in {@link WavDecoder}, with its default limit, is registered for
 * {@code wav}; {@link #setDecoder(String, AudioDecoder)} replaces it or adds other formats, such as
 * {@code audio-codec-ogg}; nothing is discovered (ADR-0020). During development, {@link #reload(ResourceKey)} decodes
 * a sound again after its file changed (ADR-0019).
 * </p>
 *
 * <p>
 * Threads: runtime thread only (ADR-0018). The cache is not thread-safe and loading workers never touch it; the thread
 * of its first operation becomes its owner, and every operation of the cache, and the release and lazy decoding of
 * its handles, on another thread throws an {@link IllegalStateException} (ADR-0055). Loading is synchronous with
 * {@link #load(ResourceKey)}, or asynchronous with {@link #loadAsync(ResourceKey, WorkerPool, Inbox)}: decoded on a
 * loading worker, cached on the runtime thread at inbox delivery (ADR-0019).
 * </p>
 *
 * @since 11.0.0
 */
public final class ShortSoundCache
{
    /** Longest short sound, in seconds (ADR-0033). */
    public static final double MAX_SECONDS = 10.0;
    /** Limit of a cache without limit, in bytes: the default. */
    public static final long NO_LIMIT = Long.MAX_VALUE;

    /** Bytes held per cached sample: one {@code short}. */
    private static final long BYTES_PER_SAMPLE = Short.BYTES;
    /** Extension of the WAV sounds. */
    private static final String WAV = "wav";
    /** Extension of a key without extension. */
    private static final String NO_EXTENSION = "";
    /** Separator of the segments of a key. */
    private static final char SEGMENT = '/';
    /** Separator of an extension. */
    private static final char DOT = '.';
    /** Name of the key argument, for the messages. */
    private static final String NAME_KEY = "Key";
    /** Error message of a {@code null} argument, after its name. */
    private static final String ERROR_NULL = " must not be null";
    /** Error message of an invalid extension. */
    private static final String ERROR_EXTENSION = "Extension empty or with a dot: ";
    /** Error message of a key without decoder. */
    private static final String ERROR_NO_DECODER = "No audio decoder for the extension of: ";
    /** Error message of a sound not cached. */
    private static final String ERROR_NOT_CACHED = "Short sound not cached: ";
    /** Error message of a sound too long: its duration. */
    private static final String ERROR_TOO_LONG = ": short sound of ";
    /** Error message of a sound too long: the limit. */
    private static final String ERROR_TOO_LONG_LIMIT = " s, longer than ";
    /** Error message of a sound too long: the advice. */
    private static final String ERROR_TOO_LONG_ADVICE = " s: play it as a long sound";
    /** Separator of a key and a detail in messages. */
    private static final String COLON = ": ";
    /** Error message of a limit that is not positive. */
    private static final String ERROR_MAX_BYTES = "Limit not positive: ";
    /** Error message of an exceeded limit: key. */
    private static final String ERROR_LIMIT = "Short sound cache limit exceeded by ";
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
     * Returns the bytes held by samples.
     *
     * @param buffer The samples.
     * @return The bytes, 2 per sample.
     */
    static long bytes(PcmBuffer buffer)
    {
        return BYTES_PER_SAMPLE * buffer.getFrames() * buffer.getFormat().channels();
    }

    /**
     * Returns the extension of a key: the end of its last segment after the last dot.
     *
     * @param key The key.
     * @return The extension, empty if none.
     */
    static String extension(ResourceKey key)
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

    /** The resources of the sounds. */
    private final Resources resources;
    /** Decoders by extension. */
    private final Map<String, AudioDecoder> decoders = new HashMap<>();
    /** Cached sounds. */
    private final Map<ResourceKey, ShortSoundEntry> entries = new HashMap<>();
    /** The runtime thread check. */
    private final ThreadConfinement thread = new ThreadConfinement("ShortSoundCache");
    /** The limit of the cached samples in bytes, {@link #NO_LIMIT} if none. */
    private final long maxBytes;
    /** The bytes held by the cached samples. */
    private long usedBytes;

    /**
     * Creates an empty cache without limit, with {@link WavDecoder} registered for {@code wav}.
     *
     * @param resources The resources the sounds are read from (never {@code null}).
     * @throws IllegalArgumentException If {@code resources} is {@code null}.
     */
    public ShortSoundCache(Resources resources)
    {
        this(resources, NO_LIMIT);
    }

    /**
     * Creates an empty cache with a limit, with {@link WavDecoder} registered for {@code wav}.
     *
     * @param resources The resources the sounds are read from (never {@code null}).
     * @param maxBytes The limit of the cached samples in bytes, 2 per sample (positive; {@link #NO_LIMIT} for none).
     * @throws IllegalArgumentException If {@code resources} is {@code null} or the limit is not positive.
     */
    public ShortSoundCache(Resources resources, long maxBytes)
    {
        super();

        checkNotNull(resources, "Resources");
        if (maxBytes <= 0L)
        {
            throw new IllegalArgumentException(ERROR_MAX_BYTES + maxBytes);
        }
        this.resources = resources;
        this.maxBytes = maxBytes;
        decoders.put(WAV, new WavDecoder());
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
    public void setDecoder(String extension, AudioDecoder decoder)
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
     * Preloads a short sound: decoded at its first load, shared afterwards.
     *
     * @param key The resource key (never {@code null}).
     * @return A new handle on the sound, loaded, to release once it is no longer needed.
     * @throws IllegalArgumentException If the key is {@code null}.
     * @throws ResourceException If the resource is missing ({@code MISSING}) or cannot be read
     *             ({@code INACCESSIBLE}).
     * @throws AudioDecodingException If no decoder is registered for the extension of the key, if the content is not
     *             a valid sound, or if the sound lasts more than {@link #MAX_SECONDS}; the message starts with the
     *             key.
     * @throws IllegalStateException If the sound would exceed the limit of the cache, or if called on another thread
     *             than the owner of the cache.
     */
    public ShortSound load(ResourceKey key)
    {
        thread.check();
        checkNotNull(key, NAME_KEY);
        return new ShortSound(this, key, resolve(key));
    }

    /**
     * Gives a handle on a short sound decoded at its first play instead of now, the lazy option (ADR-0027): the first
     * {@link Audio#play(ShortSound)} pays the reading and decoding, and raises their errors. A sound already cached is
     * shared at once.
     *
     * @param key The resource key (never {@code null}).
     * @return A new handle on the sound, to release once it is no longer needed, played or not.
     * @throws IllegalArgumentException If the key is {@code null}.
     * @throws AudioDecodingException If no decoder is registered for the extension of the key.
     * @throws IllegalStateException If called on another thread than the owner of the cache.
     */
    public ShortSound loadLazily(ResourceKey key)
    {
        thread.check();
        checkNotNull(key, NAME_KEY);
        decoder(key);
        return new ShortSound(this, key, entries.get(key));
    }

    /**
     * Requests a short sound without waiting: read and decoded on a loading worker, cached on the runtime thread when
     * the inbox delivers it (ADR-0018, ADR-0019, ADR-0055, REQ-RT-004).
     *
     * <p>
     * The worker only reads the resource, decodes it and checks its length, and never touches the cache; at the
     * delivery, on the runtime thread, the samples enter the cache under the thread check and the limit, as with
     * {@link #load(ResourceKey)}, or a sound cached meanwhile under the same key is shared and the samples are
     * discarded. A sound already cached gives a handle ready at once, a key without decoder a handle failed at once. A
     * request cancelled before the delivery caches nothing. The sound of a ready handle is a new load, released by the
     * code that made the request, cancelled or not.
     * </p>
     *
     * @param key The resource key (never {@code null}).
     * @param workers The loading worker pool of the platform (never {@code null}).
     * @param inbox The inbox of the game loop, delivered on the runtime thread (never {@code null}).
     * @return The handle of the sound: failed with the {@link ResourceException} of a missing or unreadable resource,
     *         the {@link AudioDecodingException} of invalid or too long content or of a key without decoder, or the
     *         {@link IllegalStateException} of the limit.
     * @throws IllegalArgumentException If an argument is {@code null}.
     * @throws IllegalStateException If called on another thread than the owner of the cache, or if the pool is closed.
     */
    public AsyncHandle<ShortSound> loadAsync(ResourceKey key, WorkerPool workers, Inbox inbox)
    {
        thread.check();
        checkNotNull(key, NAME_KEY);
        checkNotNull(workers, "Workers");
        checkNotNull(inbox, "Inbox");
        final ShortSoundEntry entry = entries.get(key);
        final AudioDecoder decoder = decoders.get(extension(key));
        final AsyncHandle<ShortSound> handle;
        if (entry != null)
        {
            handle = AsyncLoading.ready(new ShortSound(this, key, entry));
        }
        else if (decoder == null)
        {
            handle = AsyncLoading.failed(new AudioDecodingException(ERROR_NO_DECODER + key));
        }
        else
        {
            handle = AsyncLoading.submit(workers,
                                         inbox,
                                         () -> read(key, decoder),
                                         buffer -> new ShortSound(this, key, insert(key, buffer)));
        }
        return handle;
    }

    /**
     * Tells whether a short sound is cached.
     *
     * @param key The resource key (never {@code null}).
     * @return {@code true} while at least one handle decoded on the sound is not released.
     * @throws IllegalArgumentException If the key is {@code null}.
     * @throws IllegalStateException If called on another thread than the owner of the cache.
     */
    public boolean has(ResourceKey key)
    {
        thread.check();
        checkNotNull(key, NAME_KEY);
        return entries.containsKey(key);
    }

    /**
     * Returns the number of cached sounds, for example to check that a scene released everything it loaded.
     *
     * @return The number of decoded sounds with at least one handle not released.
     * @throws IllegalStateException If called on another thread than the owner of the cache.
     */
    public int size()
    {
        thread.check();
        return entries.size();
    }

    /**
     * Decodes again a short sound, during development, after its file changed (ADR-0019, ADR-0055).
     *
     * <p>
     * On success, every handle on the sound plays the new samples from its next play; voices already playing keep the
     * previous samples to their end. Call it between two frames, from the update phase or from an inbox event. On
     * failure the sound is unchanged.
     * </p>
     *
     * @param key The resource key of a cached sound (never {@code null}).
     * @throws IllegalArgumentException If the key is {@code null}.
     * @throws IllegalStateException If the sound is not cached, if the new samples would exceed the limit of the
     *             cache, or if called on another thread than the owner of the cache.
     * @throws ResourceException If the resource is missing ({@code MISSING}) or cannot be read
     *             ({@code INACCESSIBLE}).
     * @throws AudioDecodingException If the content is not a valid sound or lasts more than {@link #MAX_SECONDS}; the
     *             message starts with the key.
     */
    public void reload(ResourceKey key)
    {
        thread.check();
        checkNotNull(key, NAME_KEY);
        final ShortSoundEntry entry = entries.get(key);
        if (entry == null)
        {
            throw new IllegalStateException(ERROR_NOT_CACHED + key);
        }
        final PcmBuffer buffer = read(key, decoder(key));
        final long bytes = bytes(buffer);
        final long previous = entry.getBytes();
        reserve(key, bytes, previous);
        entry.replace(buffer);
        usedBytes += bytes - previous;
    }

    /**
     * Returns the memory held by the cached samples.
     *
     * @return The bytes of the cached sounds, 2 per sample.
     * @throws IllegalStateException If called on another thread than the owner of the cache.
     */
    public long getUsedBytes()
    {
        thread.check();
        return usedBytes;
    }

    /**
     * Returns the limit of the cached samples.
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
     * Returns the entry of a key, reading and decoding it if it is not cached; the caller adds its reference.
     *
     * @param key The resource key.
     * @return The entry.
     * @throws ResourceException If the resource is missing or cannot be read.
     * @throws AudioDecodingException If there is no decoder, or the content is invalid or too long.
     * @throws IllegalStateException If the sound would exceed the limit, or if called on another thread than the owner
     *             of the cache.
     */
    ShortSoundEntry resolve(ResourceKey key)
    {
        thread.check();
        ShortSoundEntry entry = entries.get(key);
        if (entry == null)
        {
            entry = insert(key, read(key, decoder(key)));
        }
        return entry;
    }

    /**
     * Removes an entry whose last handle was released.
     *
     * @param entry The entry.
     */
    void remove(ShortSoundEntry entry)
    {
        entries.remove(entry.getKey());
        usedBytes -= entry.getBytes();
    }

    /**
     * Caches decoded samples under a key, or returns the entry cached meanwhile under that key.
     *
     * @param key The resource key.
     * @param buffer The decoded samples, taken over.
     * @return The entry; the caller adds its reference.
     * @throws IllegalStateException If the sound would exceed the limit, or if called on another thread than the owner
     *             of the cache.
     */
    private ShortSoundEntry insert(ResourceKey key, PcmBuffer buffer)
    {
        thread.check();
        ShortSoundEntry entry = entries.get(key);
        if (entry == null)
        {
            final long bytes = bytes(buffer);
            reserve(key, bytes, 0L);
            entry = new ShortSoundEntry(this, key, buffer);
            entries.put(key, entry);
            usedBytes += bytes;
        }
        return entry;
    }

    /**
     * Checks that samples fit the limit of the cache.
     *
     * @param key The resource key of the sound, for the message.
     * @param bytes The bytes of the samples to cache.
     * @param freed The bytes freed at the same time, by samples replaced.
     * @throws IllegalStateException If the samples would exceed the limit.
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
     * Returns the decoder of a key.
     *
     * @param key The resource key.
     * @return The decoder registered for its extension.
     * @throws AudioDecodingException If no decoder is registered.
     */
    private AudioDecoder decoder(ResourceKey key)
    {
        final AudioDecoder decoder = decoders.get(extension(key));
        if (decoder == null)
        {
            throw new AudioDecodingException(ERROR_NO_DECODER + key);
        }
        return decoder;
    }

    /**
     * Reads and decodes a sound, checks its length, and closes its stream. Touches no state of the cache, so that a
     * loading worker may run it: the resources and the decoders are thread-safe for reading and decoding.
     *
     * @param key The resource key.
     * @param decoder The decoder of the key.
     * @return The samples, owned by the caller.
     * @throws ResourceException If the resource is missing or cannot be read.
     * @throws AudioDecodingException If the content is invalid or lasts more than {@link #MAX_SECONDS}.
     */
    private PcmBuffer read(ResourceKey key, AudioDecoder decoder)
    {
        final PcmBuffer buffer;
        try (InputStream input = resources.open(key))
        {
            buffer = decoder.decode(input);
        }
        catch (final IOException exception)
        {
            throw new ResourceException(key, ResourceException.Reason.INACCESSIBLE, exception);
        }
        catch (final AudioDecodingException exception)
        {
            throw new AudioDecodingException(key + COLON + exception.getMessage(), exception);
        }
        if (buffer.getDuration() > MAX_SECONDS)
        {
            throw new AudioDecodingException(key
                                             + ERROR_TOO_LONG
                                             + buffer.getDuration()
                                             + ERROR_TOO_LONG_LIMIT
                                             + MAX_SECONDS
                                             + ERROR_TOO_LONG_ADVICE);
        }
        return buffer;
    }
}
