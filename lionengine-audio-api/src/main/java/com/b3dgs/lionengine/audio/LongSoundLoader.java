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

import com.b3dgs.lionengine.ThreadConfinement;
import com.b3dgs.lionengine.resources.ResourceException;
import com.b3dgs.lionengine.resources.ResourceKey;
import com.b3dgs.lionengine.resources.Resources;

/**
 * Creates the long sounds of the game, music and ambient sounds, streamed by default or fully decoded on request
 * (ADR-0004, ADR-0005, ADR-0007, ADR-0018, ADR-0027, ADR-0033, ADR-0055, ADR-0068, REQ-AUDIO-006, REQ-RES-004,
 * REQ-RES-005).
 *
 * <p>
 * The game creates one loader with its {@link Resources}. {@link #stream(ResourceKey)} gives a long sound streamed
 * through a bounded buffer of {@link #DEFAULT_CHUNKS} chunks of {@link #DEFAULT_CHUNK_FRAMES} frames, so its memory
 * stays bounded and it has no length limit; {@link #stream(ResourceKey, int, int)} chooses the buffer.
 * {@link #decode(ResourceKey)} gives a long sound decoded entirely, for a short music loop, within the frame limit of
 * its decoder. Long sounds are not cached nor shared: each handle has its own stream, and reads nothing until it is
 * prepared or played. Long sounds are music by default (ADR-0033).
 * </p>
 *
 * <p>
 * Decoders are chosen by the extension of the last segment of the key, as for the short sounds. The built-in
 * {@link WavDecoder}, with its default limit, is registered for {@code wav};
 * {@link #setDecoder(String, AudioStreamDecoder)} replaces it, for example to raise the frame limit of fully decoded
 * sounds, or adds other formats, such as {@code audio-codec-ogg}; nothing is discovered (ADR-0020).
 * </p>
 *
 * <p>
 * Threads: runtime thread only (ADR-0018). The thread of the first operation becomes the owner of the loader, and
 * every operation of the loader and of its sounds on another thread throws an {@link IllegalStateException}
 * (ADR-0055). The backend's thread only pulls the streams, which touch nothing else.
 * </p>
 *
 * @since 11.0.0
 */
public final class LongSoundLoader
{
    /** Default number of chunks of the stream buffer (ADR-0033). */
    public static final int DEFAULT_CHUNKS = 4;
    /** Default frames of a chunk of the stream buffer (ADR-0033): about 0.09 s at 44.1 kHz. */
    public static final int DEFAULT_CHUNK_FRAMES = 4_096;

    /** Extension of the WAV sounds. */
    private static final String WAV = "wav";
    /** Separator of an extension. */
    private static final char DOT = '.';
    /** Separator of a key and a detail in messages. */
    private static final String COLON = ": ";
    /** Name of the key argument, for the messages. */
    private static final String NAME_KEY = "Key";

    /** The resources of the sounds. */
    private final Resources resources;
    /** Decoders by extension. */
    private final Map<String, AudioStreamDecoder> decoders = new HashMap<>();
    /** The runtime thread check. */
    private final ThreadConfinement thread = new ThreadConfinement("LongSoundLoader");

    /**
     * Creates a loader with {@link WavDecoder} registered for {@code wav}.
     *
     * @param resources The resources the sounds are read from (never {@code null}).
     * @throws IllegalArgumentException If {@code resources} is {@code null}.
     */
    public LongSoundLoader(Resources resources)
    {
        super();

        ShortSoundCache.checkNotNull(resources, "Resources");
        this.resources = resources;
        decoders.put(WAV, new WavDecoder());
    }

    /**
     * Registers the decoder of an extension, replacing the previous one; sounds already created keep theirs.
     *
     * @param extension The extension, without dot, compared exactly with the end of the keys (not empty).
     * @param decoder The decoder (never {@code null}).
     * @throws IllegalArgumentException If the extension is {@code null}, empty or has a dot, or the decoder is
     *             {@code null}.
     * @throws IllegalStateException If called on another thread than the owner of the loader.
     */
    public void setDecoder(String extension, AudioStreamDecoder decoder)
    {
        thread.check();
        ShortSoundCache.checkNotNull(extension, "Extension");
        ShortSoundCache.checkNotNull(decoder, "Decoder");
        if (extension.isEmpty() || extension.indexOf(DOT) >= 0)
        {
            throw new IllegalArgumentException("Extension empty or with a dot: " + extension);
        }
        decoders.put(extension, decoder);
    }

    /**
     * Gives a long sound streamed through the default buffer: {@link #DEFAULT_CHUNKS} chunks of
     * {@link #DEFAULT_CHUNK_FRAMES} frames.
     *
     * @param key The resource key (never {@code null}).
     * @return A new handle, not prepared, to release once it is no longer needed.
     * @throws IllegalArgumentException If the key is {@code null}.
     * @throws AudioDecodingException If no decoder is registered for the extension of the key.
     * @throws IllegalStateException If called on another thread than the owner of the loader.
     * @see #stream(ResourceKey, int, int)
     */
    public LongSound stream(ResourceKey key)
    {
        return stream(key, DEFAULT_CHUNKS, DEFAULT_CHUNK_FRAMES);
    }

    /**
     * Gives a long sound streamed through a buffer of {@code chunks × chunkFrames} frames: the only samples it holds in
     * memory while it plays.
     *
     * @param key The resource key (never {@code null}).
     * @param chunks The number of chunks of the buffer (positive).
     * @param chunkFrames The frames of a chunk (positive).
     * @return A new handle, not prepared, to release once it is no longer needed.
     * @throws IllegalArgumentException If the key is {@code null}, or a size is not positive or too large for an
     *             array.
     * @throws AudioDecodingException If no decoder is registered for the extension of the key.
     * @throws IllegalStateException If called on another thread than the owner of the loader.
     */
    public LongSound stream(ResourceKey key, int chunks, int chunkFrames)
    {
        thread.check();
        ShortSoundCache.checkNotNull(key, NAME_KEY);
        if (chunks <= 0 || chunkFrames <= 0 || (long) chunks * chunkFrames * PcmFormat.STEREO > Integer.MAX_VALUE)
        {
            throw new IllegalArgumentException("Invalid stream buffer: " + chunks + " chunks of " + chunkFrames
                                               + " frames");
        }
        return new LongSound(this, key, decoder(key), chunks, chunkFrames);
    }

    /**
     * Gives a long sound decoded entirely when it is prepared, on request, for example a short music loop that must
     * not read its file while it plays. The frame limit of its decoder applies: for WAV,
     * {@link WavDecoder#DEFAULT_MAX_FRAMES} unless a {@link WavDecoder} with another limit is registered.
     *
     * @param key The resource key (never {@code null}).
     * @return A new handle, not prepared, to release once it is no longer needed.
     * @throws IllegalArgumentException If the key is {@code null}.
     * @throws AudioDecodingException If no decoder is registered for the extension of the key.
     * @throws IllegalStateException If called on another thread than the owner of the loader.
     */
    public LongSound decode(ResourceKey key)
    {
        thread.check();
        ShortSoundCache.checkNotNull(key, NAME_KEY);
        return new LongSound(this, key, decoder(key), LongSound.NOT_STREAMED, LongSound.NOT_STREAMED);
    }

    /**
     * Checks that the calling thread owns the loader, for the sounds.
     *
     * @throws IllegalStateException If called on another thread than the owner of the loader.
     */
    void checkThread()
    {
        thread.check();
    }

    /**
     * Opens the stream of a sound and its source; on failure the stream is closed.
     *
     * @param key The resource key.
     * @param decoder The decoder of the key.
     * @return The stream, to close once the source is no longer read, and its source.
     * @throws ResourceException If the resource is missing or cannot be read.
     * @throws AudioDecodingException If the header is invalid; the message starts with the key.
     */
    Opened open(ResourceKey key, AudioStreamDecoder decoder)
    {
        final InputStream input = resources.open(key);
        try
        {
            return new Opened(input, decoder.open(input));
        }
        catch (final IOException exception)
        {
            close(input);
            throw new ResourceException(key, ResourceException.Reason.INACCESSIBLE, exception);
        }
        catch (final AudioDecodingException exception)
        {
            close(input);
            throw wrap(key, exception);
        }
    }

    /**
     * Reads and decodes a sound entirely, and closes its stream.
     *
     * @param key The resource key.
     * @param decoder The decoder of the key.
     * @return The samples, owned by the caller.
     * @throws ResourceException If the resource is missing or cannot be read.
     * @throws AudioDecodingException If the content is invalid or above the frame limit of the decoder; the message
     *             starts with the key.
     */
    PcmBuffer read(ResourceKey key, AudioStreamDecoder decoder)
    {
        try (InputStream input = resources.open(key))
        {
            return decoder.decode(input);
        }
        catch (final IOException exception)
        {
            throw new ResourceException(key, ResourceException.Reason.INACCESSIBLE, exception);
        }
        catch (final AudioDecodingException exception)
        {
            throw wrap(key, exception);
        }
    }

    /**
     * Turns the failure of a stream into the exception reported for its key.
     *
     * @param key The resource key.
     * @param failure The failure: an {@link IOException} or a runtime exception.
     * @return A {@link ResourceException} for a read failure, an {@link AudioDecodingException} starting with the key
     *         for a decoding failure, the failure itself otherwise.
     */
    static RuntimeException report(ResourceKey key, Exception failure)
    {
        final RuntimeException exception;
        if (failure instanceof final AudioDecodingException decoding)
        {
            exception = wrap(key, decoding);
        }
        else if (failure instanceof final RuntimeException runtime)
        {
            exception = runtime;
        }
        else
        {
            exception = new ResourceException(key, ResourceException.Reason.INACCESSIBLE, failure);
        }
        return exception;
    }

    /**
     * Closes a stream no longer read; a failure to close loses nothing, so it is ignored.
     *
     * @param input The stream.
     */
    static void close(InputStream input)
    {
        try
        {
            input.close();
        }
        catch (final IOException exception)
        {
            // Nothing is lost: the stream is no longer read.
        }
    }

    /**
     * Prefixes a decoding failure with its key.
     *
     * @param key The resource key.
     * @param exception The failure.
     * @return The failure whose message starts with the key.
     */
    private static AudioDecodingException wrap(ResourceKey key, AudioDecodingException exception)
    {
        return new AudioDecodingException(key + COLON + exception.getMessage(), exception);
    }

    /**
     * Returns the decoder of a key.
     *
     * @param key The resource key.
     * @return The decoder registered for its extension.
     * @throws AudioDecodingException If no decoder is registered.
     */
    private AudioStreamDecoder decoder(ResourceKey key)
    {
        final AudioStreamDecoder decoder = decoders.get(ShortSoundCache.extension(key));
        if (decoder == null)
        {
            throw new AudioDecodingException("No audio decoder for the extension of: " + key);
        }
        return decoder;
    }

    /**
     * An opened stream and its source.
     *
     * @param input The stream, to close once the source is no longer read.
     * @param source The source reading it.
     */
    record Opened(InputStream input, PcmSource source)
    {
        // Value type.
    }
}
