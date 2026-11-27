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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.InboxSpy;
import com.b3dgs.lionengine.resources.FakeResourceProvider;
import com.b3dgs.lionengine.resources.ResourceException;
import com.b3dgs.lionengine.resources.ResourceKey;
import com.b3dgs.lionengine.resources.Resources;
import com.b3dgs.lionengine.resources.StreamProvider;

/**
 * Tests of {@link Playlist} on {@link FakeAudioBackend} and its clock: advance at the end of a track, the next track
 * prepared ahead, stop and loop at the end, failing tracks skipped and reported on the runtime thread, abort, and stop
 * (ADR-0004, ADR-0018, ADR-0027, ADR-0033, ADR-0068, REQ-AUDIO-008).
 */
final class PlaylistTest
{
    /** Frames of the first track. */
    private static final int FIRST_FRAMES = 20_000;
    /** Frames of the second track. */
    private static final int SECOND_FRAMES = 30_000;
    /** Key of the first track. */
    private static final ResourceKey FIRST = new ResourceKey("music/first.wav");
    /** Key of the second track. */
    private static final ResourceKey SECOND = new ResourceKey("music/second.wav");
    /** Key of a missing track. */
    private static final ResourceKey MISSING = new ResourceKey("music/missing.wav");
    /** Key of a track truncated after its buffer. */
    private static final ResourceKey TRUNCATED = new ResourceKey("music/truncated.wav");

    /**
     * A failure reported to the listener.
     *
     * @param playlist The playlist.
     * @param track The index of the track.
     * @param exception The failure.
     * @param thread The thread of the call.
     */
    private record Failure(Playlist playlist, int track, RuntimeException exception, Thread thread)
    {
        // Value type of the tests.
    }

    /** The resources. */
    private FakeResourceProvider provider;
    /** The loader. */
    private LongSoundLoader loader;
    /** The inbox. */
    private InboxSpy inbox;
    /** The backend. */
    private FakeAudioBackend backend;
    /** The audio system. */
    private Audio audio;
    /** The first track. */
    private LongSound first;
    /** The second track. */
    private LongSound second;
    /** The failures reported. */
    private List<Failure> failures;

    /**
     * Creates the tracks and the audio system.
     */
    @BeforeEach
    void prepare()
    {
        provider = new FakeResourceProvider(false);
        provider.put(FIRST, LongSoundTest.wav(FIRST_FRAMES));
        provider.put(SECOND, LongSoundTest.wav(SECOND_FRAMES));
        final byte[] truncated = LongSoundTest.wav(FIRST_FRAMES);
        provider.put(TRUNCATED, Arrays.copyOf(truncated, truncated.length - 2 * 1_000));
        loader = new LongSoundLoader(new Resources(provider));
        inbox = new InboxSpy();
        backend = new FakeAudioBackend(inbox, 4);
        audio = new Audio(backend, 2, VoicePolicy.STEAL);
        first = loader.stream(FIRST);
        second = loader.stream(SECOND);
        failures = new ArrayList<>();
    }

    /**
     * Creates a playlist whose failures are recorded.
     *
     * @param tracks The tracks.
     * @return The playlist.
     */
    private Playlist playlist(LongSound... tracks)
    {
        final Playlist playlist = new Playlist(audio, tracks);
        playlist.setListener((list, track, exception) -> failures.add(new Failure(list,
                                                                                  track,
                                                                                  exception,
                                                                                  Thread.currentThread())));
        return playlist;
    }

    /**
     * Plays the frames of a track, and one more since a stream ends when a read returns fewer frames than asked, then
     * delivers the inbox.
     *
     * @param frames The frames of the track.
     */
    private void play(int frames)
    {
        backend.advance(frames + 1);
        inbox.deliverAll();
    }

    /**
     * The first track starts at once and the second is prepared ahead; the second starts at the delivery of the end of
     * the first; after the last track the playlist stops, by default.
     */
    @Test
    void testAdvanceAndStop()
    {
        final Playlist playlist = playlist(first, second);
        assertFalse(playlist.isPlaying());
        assertEquals(Playlist.NO_TRACK, playlist.getTrack());
        assertFalse(playlist.isLooping());
        assertFalse(playlist.isAbortOnFailure());

        playlist.play();

        assertTrue(playlist.isPlaying());
        assertEquals(0, playlist.getTrack());
        assertTrue(first.isPlaying());
        assertTrue(second.isPrepared());
        assertFalse(second.isPlaying());
        backend.advance(FIRST_FRAMES + 1);
        assertEquals(0, playlist.getTrack());
        inbox.deliverAll();
        assertEquals(1, playlist.getTrack());
        assertTrue(second.isPlaying());
        assertFalse(first.isPrepared());
        assertTrue(audio.isPlaying(playlist.getVoice()));

        play(SECOND_FRAMES);

        assertFalse(playlist.isPlaying());
        assertEquals(Playlist.NO_TRACK, playlist.getTrack());
        assertEquals(AudioBackend.NO_VOICE, playlist.getVoice());
        assertEquals(0, backend.getPlayingCount());
        assertFalse(first.isPrepared());
        assertEquals(0, provider.getUnclosed());
        assertTrue(failures.isEmpty());
    }

    /**
     * A looping playlist starts again from its first track, prepared ahead during the last one.
     */
    @Test
    void testLoop()
    {
        final Playlist playlist = playlist(first, second);
        playlist.setLooping(true);
        assertTrue(playlist.isLooping());
        playlist.play();

        play(FIRST_FRAMES);
        assertTrue(first.isPrepared());
        play(SECOND_FRAMES);

        assertEquals(0, playlist.getTrack());
        assertTrue(first.isPlaying());
        assertTrue(second.isPrepared());
        playlist.stop();
        assertEquals(0, provider.getUnclosed());
    }

    /**
     * A playlist of one track that loops plays it again at its end, prepared at its turn.
     */
    @Test
    void testSingleTrackLoop()
    {
        final Playlist playlist = playlist(first);
        playlist.setLooping(true);
        playlist.play();
        final int voice = playlist.getVoice();

        play(FIRST_FRAMES);

        assertEquals(0, playlist.getTrack());
        assertTrue(playlist.getVoice() > voice);
        assertTrue(first.isPlaying());
        play(FIRST_FRAMES);
        assertTrue(first.isPlaying());
        assertEquals(2, provider.getOpened() - 1);
    }

    /**
     * A track that fails to start is reported with the playlist, its index and the exception on the runtime thread,
     * then skipped; the playlist goes on with the next track.
     */
    @Test
    void testFailingTrackSkipped()
    {
        final Playlist playlist = playlist(first, loader.stream(MISSING), second);
        playlist.play();
        assertTrue(failures.isEmpty());

        play(FIRST_FRAMES);

        assertEquals(1, failures.size());
        final Failure failure = failures.get(0);
        assertSame(playlist, failure.playlist());
        assertEquals(1, failure.track());
        final ResourceException exception = assertInstanceOf(ResourceException.class, failure.exception());
        assertEquals(ResourceException.Reason.MISSING, exception.getReason());
        assertSame(Thread.currentThread(), failure.thread());
        assertEquals(2, playlist.getTrack());
        assertTrue(second.isPlaying());
    }

    /**
     * A failure to close the stream of a track at its end is reported, and the playlist goes on.
     */
    @Test
    void testCloseFailure()
    {
        final ResourceKey key = new ResourceKey("music/unclosable.wav");
        final byte[] bytes = LongSoundTest.wav(1_000);
        final StreamProvider streams = new StreamProvider();
        streams.register(key, () -> new ByteArrayInputStream(bytes)
        {
            @Override
            public void close() throws IOException
            {
                throw new IOException("Cannot close");
            }
        });
        streams.register(FIRST, () -> new ByteArrayInputStream(LongSoundTest.wav(FIRST_FRAMES)));
        final LongSoundLoader unclosable = new LongSoundLoader(new Resources(streams));
        final Playlist playlist = playlist(unclosable.stream(key), unclosable.stream(FIRST));
        playlist.play();

        play(1_000);

        assertEquals(1, failures.size());
        final ResourceException exception = assertInstanceOf(ResourceException.class, failures.get(0).exception());
        assertEquals(ResourceException.Reason.INACCESSIBLE, exception.getReason());
        assertEquals(1, playlist.getTrack());
    }

    /**
     * A track whose stream fails while it plays is reported at the delivery of its end, with its key, then skipped.
     */
    @Test
    void testFailingWhilePlaying()
    {
        final Playlist playlist = playlist(loader.stream(TRUNCATED), second);
        playlist.play();
        assertTrue(failures.isEmpty());

        backend.advance(FIRST_FRAMES + 1);
        assertTrue(failures.isEmpty());
        inbox.deliverAll();

        assertEquals(1, failures.size());
        assertEquals(0, failures.get(0).track());
        final AudioDecodingException exception = assertInstanceOf(AudioDecodingException.class,
                                                                  failures.get(0).exception());
        assertTrue(exception.getMessage().startsWith(TRUNCATED.toString()), exception::getMessage);
        assertEquals(1, playlist.getTrack());
    }

    /**
     * With abort on failure, a failing track is reported and the playlist stops, at its start or while it plays.
     */
    @Test
    void testAbort()
    {
        final Playlist starting = playlist(loader.stream(MISSING), first);
        starting.setAbortOnFailure(true);
        assertTrue(starting.isAbortOnFailure());

        starting.play();

        assertFalse(starting.isPlaying());
        assertFalse(first.isPlaying());
        assertEquals(1, failures.size());

        final Playlist playing = playlist(loader.stream(TRUNCATED), second);
        playing.setAbortOnFailure(true);
        playing.play();
        assertTrue(second.isPrepared());
        play(FIRST_FRAMES);

        assertFalse(playing.isPlaying());
        assertFalse(second.isPlaying());
        assertFalse(second.isPrepared());
        assertEquals(2, failures.size());
        assertEquals(0, provider.getUnclosed());
    }

    /**
     * The first track failing is reported during the play; when every track fails, the playlist stops, even when
     * looping, after reporting each one once.
     */
    @Test
    void testEveryTrackFails()
    {
        final Playlist playlist = playlist(loader.stream(MISSING), loader.stream(MISSING));
        playlist.setLooping(true);

        playlist.play();

        assertFalse(playlist.isPlaying());
        assertEquals(2, failures.size());
        assertEquals(0, failures.get(0).track());
        assertEquals(1, failures.get(1).track());
    }

    /**
     * A released track and a track without free voice are reported as failing; without listener, failures are
     * skipped silently.
     */
    @Test
    void testReleasedAndNoVoice()
    {
        final LongSound released = loader.stream(FIRST);
        released.release();
        final Playlist playlist = playlist(released, second);
        playlist.play();
        assertInstanceOf(IllegalStateException.class, failures.get(0).exception());
        assertTrue(second.isPlaying());
        playlist.stop();

        final FakeAudioBackend single = new FakeAudioBackend(inbox, 1);
        final Audio busy = new Audio(single, 1, VoicePolicy.STEAL);
        busy.play(loader.stream(FIRST));
        final Playlist full = new Playlist(busy, second);
        full.setListener((list, track, exception) -> failures.add(new Failure(list, track, exception, null)));
        full.play();

        assertFalse(full.isPlaying());
        assertInstanceOf(IllegalStateException.class, failures.get(1).exception());
        assertFalse(second.isPrepared());
        final Playlist silent = new Playlist(audio, loader.stream(MISSING), first);
        silent.play();
        assertEquals(1, silent.getTrack());
    }

    /**
     * Stopping the playlist stops its track and closes the track prepared ahead; stopping again has no effect;
     * stopping the voice of its track or releasing its track stops it as well; playing again restarts from the first
     * track.
     */
    @Test
    void testStop()
    {
        final Playlist playlist = playlist(first, second);
        playlist.play();

        playlist.stop();
        playlist.stop();

        assertFalse(playlist.isPlaying());
        assertFalse(first.isPlaying());
        assertFalse(second.isPrepared());
        assertEquals(0, provider.getUnclosed());

        playlist.play();
        audio.stop(playlist.getVoice());
        assertFalse(playlist.isPlaying());
        assertFalse(second.isPrepared());

        playlist.play();
        play(FIRST_FRAMES);
        playlist.play();
        assertEquals(0, playlist.getTrack());
        assertFalse(second.isPlaying());
        assertTrue(second.isPrepared());
        first.release();
        assertFalse(playlist.isPlaying());
        play(SECOND_FRAMES);
        assertFalse(playlist.isPlaying());
        assertTrue(failures.isEmpty());
    }

    /**
     * Invalid arguments are refused; the tracks are copied and given back.
     */
    @Test
    void testArguments()
    {
        final LongSound[] tracks =
        {
            first, second
        };
        final Playlist playlist = new Playlist(audio, tracks);
        tracks[0] = second;

        assertEquals(2, playlist.size());
        assertSame(first, playlist.getSound(0));
        assertThrows(IndexOutOfBoundsException.class, () -> playlist.getSound(2));
        assertThrows(IllegalArgumentException.class, () -> new Playlist(null, first));
        assertThrows(IllegalArgumentException.class, () -> new Playlist(audio, (LongSound[]) null));
        assertThrows(IllegalArgumentException.class, () -> new Playlist(audio));
        assertThrows(IllegalArgumentException.class, () -> new Playlist(audio, first, null));
    }

    /**
     * A listener that stops the playlist at a failure ends the advance: no later track starts.
     */
    @Test
    void testListenerStops()
    {
        final Playlist playlist = new Playlist(audio, loader.stream(MISSING), first);
        playlist.setListener((list, track, exception) -> list.stop());

        playlist.play();

        assertFalse(playlist.isPlaying());
        assertFalse(first.isPlaying());

        final Playlist restarted = new Playlist(audio, first, loader.stream(TRUNCATED), second);
        restarted.setListener((list, track, exception) -> list.stop());
        restarted.play();
        play(FIRST_FRAMES);
        assertEquals(1, restarted.getTrack());
        play(FIRST_FRAMES);
        assertFalse(restarted.isPlaying());
        assertFalse(second.isPlaying());
        assertFalse(second.isPrepared());
        assertEquals(0, provider.getUnclosed());
    }
}
