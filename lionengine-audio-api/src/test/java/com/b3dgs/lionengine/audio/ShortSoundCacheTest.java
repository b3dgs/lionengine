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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.AsyncHandle;
import com.b3dgs.lionengine.AsyncState;
import com.b3dgs.lionengine.FakeWorkerPool;
import com.b3dgs.lionengine.InboxSpy;
import com.b3dgs.lionengine.resources.FakeResourceProvider;
import com.b3dgs.lionengine.resources.ResourceException;
import com.b3dgs.lionengine.resources.ResourceKey;
import com.b3dgs.lionengine.resources.Resources;

/**
 * Test {@link ShortSoundCache} and {@link ShortSound}: cache hit and miss, length limit, memory limit, lazy and
 * asynchronous loading, decoders (ADR-0027, ADR-0033, ADR-0055, ADR-0066, REQ-AUDIO-003, REQ-AUDIO-013, REQ-RT-004).
 */
final class ShortSoundCacheTest
{
    /** Sample rate of the sounds: 100 Hz, for round durations. */
    private static final int RATE = 100;
    /** Format of the sounds. */
    private static final PcmFormat FORMAT = new PcmFormat(RATE, PcmFormat.MONO);
    /** Key of the jump sound. */
    private static final ResourceKey JUMP = new ResourceKey("sounds/jump.wav");
    /** Key of the coin sound. */
    private static final ResourceKey COIN = new ResourceKey("sounds/coin.wav");
    /** Frames of the jump sound: 0.5 s. */
    private static final int JUMP_FRAMES = 50;
    /** Bytes of the jump sound: 2 per sample. */
    private static final long JUMP_BYTES = 100L;

    /**
     * Encodes a sound of silence.
     *
     * @param frames The frames.
     * @return The WAV bytes.
     */
    private static byte[] wav(int frames)
    {
        return WavWriter.write(new PcmBuffer(FORMAT, new short[frames]), 16);
    }

    /** The provider of the resources. */
    private FakeResourceProvider provider;
    /** The cache of the test. */
    private ShortSoundCache cache;

    /**
     * Creates the resources and the cache.
     */
    @BeforeEach
    void prepare()
    {
        provider = new FakeResourceProvider(false);
        provider.put(JUMP, wav(JUMP_FRAMES));
        provider.put(COIN, wav(10));
        cache = new ShortSoundCache(new Resources(provider));
    }

    /**
     * The first load decodes, the next ones share; the handle gives the format and the duration; the memory is
     * counted until the last release.
     */
    @Test
    void testHitAndMiss()
    {
        final ShortSound first = cache.load(JUMP);
        final ShortSound second = cache.load(JUMP);

        assertEquals(1, provider.getOpened());
        assertSame(first.getBuffer(), second.getBuffer());
        assertTrue(first.isLoaded());
        assertEquals(JUMP, first.getKey());
        assertEquals(FORMAT, first.getFormat());
        assertEquals(0.5, first.getDuration(), 1e-12);
        assertTrue(cache.has(JUMP));
        assertEquals(JUMP_BYTES, cache.getUsedBytes());
        assertEquals(ShortSoundCache.NO_LIMIT, cache.getMaxBytes());

        first.release();
        second.release();
        assertFalse(cache.has(JUMP));
        assertEquals(0L, cache.getUsedBytes());
        assertFalse(first.isLoaded());
        assertTrue(first.isReleased());
        assertEquals(JUMP, first.getKey());
        assertThrows(IllegalStateException.class, first::getDuration);
        assertThrows(IllegalStateException.class, first::getBuffer);
    }

    /**
     * A sound of exactly 10 s is accepted; a longer one is refused with a decoding error naming the key, and caches
     * nothing.
     */
    @Test
    void testLengthLimit()
    {
        final ResourceKey exact = new ResourceKey("sounds/exact.wav");
        final ResourceKey longer = new ResourceKey("sounds/longer.wav");
        provider.put(exact, wav(10 * RATE));
        provider.put(longer, wav(10 * RATE + 1));

        assertEquals(ShortSoundCache.MAX_SECONDS, cache.load(exact).getDuration(), 1e-12);
        final AudioDecodingException exception = assertThrows(AudioDecodingException.class, () -> cache.load(longer));

        assertTrue(exception.getMessage().startsWith(longer.toString()), exception.getMessage());
        assertTrue(exception.getMessage().contains("long sound"));
        assertFalse(cache.has(longer));
        assertEquals(0, provider.getUnclosed());
    }

    /**
     * With a limit, a sound that would exceed it is refused and nothing is cached; a released sound frees its bytes.
     */
    @Test
    void testMemoryLimit()
    {
        final ShortSoundCache limited = new ShortSoundCache(new Resources(provider), JUMP_BYTES + 19L);
        final ShortSound jump = limited.load(JUMP);

        final IllegalStateException exception = assertThrows(IllegalStateException.class, () -> limited.load(COIN));
        assertTrue(exception.getMessage().contains(COIN.toString()));
        assertEquals(1, limited.size());
        assertEquals(JUMP_BYTES, limited.getUsedBytes());
        assertEquals(JUMP_BYTES + 19L, limited.getMaxBytes());

        jump.release();
        limited.load(COIN);
        assertEquals(20L, limited.getUsedBytes());
        assertThrows(IllegalArgumentException.class, () -> new ShortSoundCache(new Resources(provider), 0L));
        assertThrows(IllegalArgumentException.class, () -> new ShortSoundCache(null));
    }

    /**
     * A reload that would exceed the limit is refused and leaves the sound unchanged.
     */
    @Test
    void testReloadLimit()
    {
        final ShortSoundCache limited = new ShortSoundCache(new Resources(provider), JUMP_BYTES);
        final ShortSound jump = limited.load(JUMP);
        provider.put(JUMP, wav(JUMP_FRAMES + 1));

        assertThrows(IllegalStateException.class, () -> limited.reload(JUMP));
        assertEquals(JUMP_FRAMES, jump.getBuffer().getFrames());

        provider.put(JUMP, wav(JUMP_FRAMES - 1));
        limited.reload(JUMP);
        assertEquals(JUMP_FRAMES - 1, jump.getBuffer().getFrames());
        assertEquals(JUMP_BYTES - 2L, limited.getUsedBytes());
    }

    /**
     * A lazy handle reads nothing until its first play, then joins the cache; a key already cached is shared at once;
     * a released lazy handle that never played releases nothing.
     */
    @Test
    void testLazy()
    {
        final ShortSound lazy = cache.loadLazily(JUMP);
        final ShortSound unused = cache.loadLazily(COIN);

        assertFalse(lazy.isLoaded());
        assertEquals(0, provider.getOpened());
        assertThrows(IllegalStateException.class, lazy::getFormat);
        assertEquals(JUMP_FRAMES, lazy.getBuffer().getFrames());
        assertTrue(lazy.isLoaded());
        assertEquals(1, provider.getOpened());
        final ShortSound shared = cache.loadLazily(JUMP);
        assertTrue(shared.isLoaded());
        assertSame(lazy.getBuffer(), shared.getBuffer());

        unused.release();
        lazy.release();
        assertTrue(cache.has(JUMP));
        shared.release();
        assertEquals(0, cache.size());
        assertThrows(AudioDecodingException.class, () -> cache.loadLazily(new ResourceKey("sounds/jump.mp3")));
    }

    /**
     * The errors of a lazy handle come at its first play, and leave it unloaded so that a later play can retry.
     */
    @Test
    void testLazyFailure()
    {
        final ResourceKey absent = new ResourceKey("sounds/absent.wav");
        final ShortSound lazy = cache.loadLazily(absent);

        assertThrows(ResourceException.class, lazy::getBuffer);
        assertFalse(lazy.isLoaded());
        provider.put(absent, wav(1));
        assertEquals(1, lazy.getBuffer().getFrames());
    }

    /**
     * Decoders are chosen by extension; a key without decoder is refused; extensions and decoders are checked.
     */
    @Test
    void testDecoders()
    {
        final ResourceKey raw = new ResourceKey("sounds/beep.raw");
        final ResourceKey none = new ResourceKey("sounds.dir/beep");
        provider.put(raw, RawAudioDecoder.encode(new PcmBuffer(FORMAT, new short[3])));
        provider.put(none, wav(1));
        final RawAudioDecoder decoder = new RawAudioDecoder(1_000);

        assertThrows(AudioDecodingException.class, () -> cache.load(raw));
        cache.setDecoder("raw", decoder);

        assertEquals(3, cache.load(raw).getBuffer().getFrames());
        assertThrows(AudioDecodingException.class, () -> cache.load(none));
        assertThrows(IllegalArgumentException.class, () -> cache.setDecoder("", decoder));
        assertThrows(IllegalArgumentException.class, () -> cache.setDecoder("a.b", decoder));
        assertThrows(IllegalArgumentException.class, () -> cache.setDecoder(null, decoder));
        assertThrows(IllegalArgumentException.class, () -> cache.setDecoder("raw", null));
        assertThrows(IllegalArgumentException.class, () -> cache.load(null));
        assertThrows(IllegalArgumentException.class, () -> cache.has(null));
    }

    /**
     * An asynchronous load decodes on the worker and caches at the delivery; a sound cached meanwhile is shared.
     */
    @Test
    void testLoadAsync()
    {
        final FakeWorkerPool workers = new FakeWorkerPool(1);
        final InboxSpy inbox = new InboxSpy();

        final AsyncHandle<ShortSound> handle = cache.loadAsync(JUMP, workers, inbox);
        final AsyncHandle<ShortSound> twice = cache.loadAsync(JUMP, workers, inbox);
        workers.runAll();
        assertEquals(0, cache.size());
        assertEquals(AsyncState.PENDING, handle.getState());
        inbox.deliverAll();

        assertEquals(JUMP_FRAMES, handle.get().getBuffer().getFrames());
        assertSame(handle.get().getBuffer(), twice.get().getBuffer());
        assertEquals(1, cache.size());
        assertEquals(JUMP_BYTES, cache.getUsedBytes());
        handle.get().release();
        twice.get().release();
        assertEquals(0, cache.size());
    }

    /**
     * A cached sound gives a ready handle, a key without decoder a failed one, an invalid or too long sound a failed
     * handle at the delivery; nothing is cached for the failures.
     */
    @Test
    void testLoadAsyncFailures()
    {
        final FakeWorkerPool workers = new FakeWorkerPool(1);
        final InboxSpy inbox = new InboxSpy();
        final ResourceKey longer = new ResourceKey("sounds/longer.wav");
        provider.put(longer, wav(10 * RATE + 1));
        final ShortSound jump = cache.load(JUMP);

        final AsyncHandle<ShortSound> cached = cache.loadAsync(JUMP, workers, inbox);
        final AsyncHandle<ShortSound> unknown = cache.loadAsync(new ResourceKey("sounds/a.ogg"), workers, inbox);
        final AsyncHandle<ShortSound> tooLong = cache.loadAsync(longer, workers, inbox);
        workers.runAll();
        inbox.deliverAll();

        assertSame(jump.getBuffer(), cached.get().getBuffer());
        assertInstanceOf(AudioDecodingException.class, unknown.getFailure());
        assertInstanceOf(AudioDecodingException.class, tooLong.getFailure());
        assertEquals(1, cache.size());
        assertThrows(IllegalArgumentException.class, () -> cache.loadAsync(null, workers, inbox));
        assertThrows(IllegalArgumentException.class, () -> cache.loadAsync(JUMP, null, inbox));
        assertThrows(IllegalArgumentException.class, () -> cache.loadAsync(JUMP, workers, null));
    }
}
