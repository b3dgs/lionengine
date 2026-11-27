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

import java.util.Arrays;

/**
 * Audio system of the game: plays short sounds and variant sets on a bounded number of voices of an
 * {@link AudioBackend}, with priorities and an overflow policy, and long sounds, with their gain, looping, pause,
 * mixer groups and fades (ADR-0004, ADR-0018, ADR-0027, ADR-0031, ADR-0033, ADR-0065, ADR-0066, ADR-0067, ADR-0068,
 * ADR-0069, ADR-0071, REQ-AUDIO-001, REQ-AUDIO-003, REQ-AUDIO-004, REQ-AUDIO-005, REQ-AUDIO-007, REQ-AUDIO-010,
 * REQ-AUDIO-011, REQ-AUDIO-012).
 *
 * <p>
 * The game creates one {@code Audio} over its backend and keeps it for its whole life: it is the only user of the
 * backend and its voice listener. {@link #play(ShortSound, int)} starts a short sound at once on a new voice, an
 * {@code int} returned to the caller, which can {@link #stop(int)} it; stopping a voice that already ended has no
 * effect. Voices are never reused, so a stale voice never stops another sound. A {@link VariantSet} plays one
 * variant chosen by the set with {@link #play(VariantSet, int)}, or every layer of a composed set at once with
 * {@link #play(VariantSet, int, int[])}, which writes the voices to an array of the caller.
 * </p>
 *
 * <p>
 * Long sounds: {@link #play(LongSound)} plays a {@link LongSound} on a voice of the backend outside the short-sound
 * limit and its policy, and {@link #stop(int)} stops it as any voice; a {@link Playlist} plays its tracks through it.
 * A long sound plays on one voice at a time.
 * </p>
 *
 * <p>
 * Voices: at most {@link #getMaxVoices()} short sounds play at once, {@link #DEFAULT_MAX_VOICES} by default, within
 * the voices of the backend. A sound has a priority from {@link #MIN_PRIORITY} to {@link #MAX_PRIORITY},
 * {@link #DEFAULT_PRIORITY} by default. When every voice is busy, the {@link VoicePolicy} decides: by default the
 * oldest voice of the lowest priority is stolen, if that priority is not above the new sound's; drop-new and refuse
 * are selectable. A voice is free again as soon as it is stopped, and when the end its backend posted to the inbox is
 * delivered.
 * </p>
 *
 * <p>
 * Controls (ADR-0069):
 * </p>
 * <ul>
 * <li>Gain: a {@code double} in {@code [0, 1]}. The master gain ({@link #setMasterGain(double)}) and the gain of each
 * sound ({@link ShortSound#setGain(double)}, {@link LongSound#setGain(double)}) and the gain of its {@link MixerGroup}
 * multiply into the effective gain of a voice, master × group × sound. A change reaches every voice it concerns at
 * once: the backend applies it from its next audio buffer.</li>
 * <li>Mixer groups (ADR-0071): {@link MixerGroup#MUSIC}, {@link MixerGroup#EFFECTS} and {@link MixerGroup#UI} exist
 * from the creation, and {@link #addGroup(String)} adds a group of the game. A sound names its group on its handle,
 * {@link MixerGroup#EFFECTS} for a short sound and {@link MixerGroup#MUSIC} for a long sound by default, read at the
 * play: a voice keeps the group it started with.</li>
 * <li>Fades (ADR-0071): {@link ShortSound#fade(double, double)}, {@link LongSound#fade(double, double)} and
 * {@link MixerGroup#fade(double, double)} set the gain of the sound or of the group to a target at once and ramp each
 * voice concerned to its new effective gain, linearly over a duration in seconds, in the backend, per audio buffer.
 * The end of the ramp of each voice is posted to the inbox and reaches {@link #setFadeListener(FadeListener) the fade
 * listener} on the runtime thread, if the voice still plays. A new fade of a voice replaces its ramp; a gain change
 * during a ramp changes its target and keeps its remaining time; a pause holds it; a stop, a steal or the end of the
 * voice drops it without completion. A fade to {@code 0} stops nothing.</li>
 * <li>Looping: a sound whose handle loops ({@link ShortSound#setLooping(boolean)},
 * {@link LongSound#setLooping(boolean)}) is played looping; its voice never ends by itself and posts no end, so it
 * plays until it is stopped, stolen or its long sound is released.</li>
 * <li>Pause: {@link #pause(int)} and {@link #resume(int)} act on one voice, of a short sound or of a long sound;
 * pausing or resuming a voice that ended, was stopped or does not exist has no effect. A paused voice keeps its voice:
 * it counts in the limit, is still playing for {@link #isPlaying(int)}, may be stolen as any voice, and is stopped by
 * {@link #stop(int)} without posting an end. The master pause ({@link #setMasterPaused(boolean)}) pauses the output of
 * the backend, for example when the application goes to the background (ADR-0031); it is independent of the pause of
 * each voice, and sounds played meanwhile start when it ends.</li>
 * </ul>
 *
 * <p>
 * Threads: runtime thread only (ADR-0018), not thread-safe; the backend calls the listener at the inbox delivery, on
 * the runtime thread. Playing a sound or a variant set, stopping, the controls and the end of a voice allocate nothing,
 * except the first play of a lazy short sound, which decodes it (ADR-0019), and the play of a long sound that is not
 * prepared.
 * </p>
 *
 * @since 11.0.0
 */
public final class Audio
{
    /** Default number of simultaneous short voices (ADR-0027, ADR-0033). */
    public static final int DEFAULT_MAX_VOICES = 16;
    /** Lowest priority. */
    public static final int MIN_PRIORITY = 0;
    /** Highest priority. */
    public static final int MAX_PRIORITY = 100;
    /** Default priority. */
    public static final int DEFAULT_PRIORITY = 50;
    /** Lowest gain: silence. */
    public static final double MIN_GAIN = 0.0;
    /** Highest gain, and the default gain of the master and of each sound. */
    public static final double MAX_GAIN = 1.0;

    /** No slot found. */
    private static final int NONE = -1;
    /** End of the messages of busy voices. */
    private static final String ERROR_PLAYING = " voices playing";
    /** Name of the variant set argument. */
    private static final String NAME_SET = "Set";
    /** Start of the message of an unknown mixer group. */
    private static final String ERROR_GROUP = "Mixer group not defined: ";
    /** Initial capacity of the long voices, grown when more play at once. */
    private static final int INITIAL_LONG_VOICES = 2;
    /** Initial capacity of the mixer groups, the default ones and one of the game. */
    private static final int INITIAL_GROUPS = 4;

    /** The backend. */
    private final AudioBackend backend;
    /** The overflow policy. */
    private final VoicePolicy policy;
    /** The voice limit. */
    private final int maxVoices;
    /** Voices playing, in start order, the oldest first. */
    private final int[] voices;
    /** Priority of each voice playing. */
    private final int[] priorities;
    /** Sound of each voice playing, {@code null} in the free slots. */
    private final ShortSound[] sounds;
    /** {@code true} for each voice playing that loops. */
    private final boolean[] loops;
    /** {@code true} for each voice playing that is paused on its own. */
    private final boolean[] paused;
    /** Mixer group of each voice playing, {@code null} in the free slots. */
    private final MixerGroup[] groups;
    /** Number of voices playing. */
    private int count;
    /** Voices playing long sounds, in start order. */
    private int[] longVoices = new int[INITIAL_LONG_VOICES];
    /** The long sound of each long voice. */
    private LongSound[] longSounds = new LongSound[INITIAL_LONG_VOICES];
    /** {@code true} for each long voice that is paused on its own. */
    private boolean[] longPaused = new boolean[INITIAL_LONG_VOICES];
    /** The mixer group of each long voice. */
    private MixerGroup[] longGroups = new MixerGroup[INITIAL_LONG_VOICES];
    /** The mixer groups, the default ones first. */
    private MixerGroup[] mixerGroups = new MixerGroup[INITIAL_GROUPS];
    /** Number of mixer groups. */
    private int groupCount;
    /** Listener of the fades, {@code null} for none. */
    private FadeListener fadeListener;
    /** Number of long voices playing. */
    private int longCount;
    /** The master gain. */
    private double masterGain = MAX_GAIN;
    /** {@code true} while the output is paused. */
    private boolean masterPaused;

    /**
     * Creates the audio system with {@link #DEFAULT_MAX_VOICES} voices, or fewer if the backend has fewer, and the
     * {@link VoicePolicy#STEAL} policy.
     *
     * @param backend The audio backend, of which this system becomes the voice listener (never {@code null}).
     * @throws IllegalArgumentException If {@code backend} is {@code null}.
     */
    public Audio(AudioBackend backend)
    {
        this(backend, Math.min(DEFAULT_MAX_VOICES, maxVoices(backend)), VoicePolicy.STEAL);
    }

    /**
     * Creates the audio system.
     *
     * @param backend The audio backend, of which this system becomes the voice listener (never {@code null}).
     * @param maxVoices The number of short sounds playing at once (from {@code 1} to the voices of the backend).
     * @param policy The overflow policy (never {@code null}).
     * @throws IllegalArgumentException If an argument is {@code null}, or the limit is outside its range.
     */
    public Audio(AudioBackend backend, int maxVoices, VoicePolicy policy)
    {
        super();

        final int backendVoices = maxVoices(backend);
        ShortSoundCache.checkNotNull(policy, "Policy");
        if (maxVoices < 1 || maxVoices > backendVoices)
        {
            throw new IllegalArgumentException("Voice limit outside 1 to " + backendVoices + ": " + maxVoices);
        }
        this.backend = backend;
        this.maxVoices = maxVoices;
        this.policy = policy;
        voices = new int[maxVoices];
        priorities = new int[maxVoices];
        sounds = new ShortSound[maxVoices];
        loops = new boolean[maxVoices];
        paused = new boolean[maxVoices];
        groups = new MixerGroup[maxVoices];
        addGroup(MixerGroup.MUSIC);
        addGroup(MixerGroup.EFFECTS);
        addGroup(MixerGroup.UI);
        backend.setVoiceListener(this::ended);
        backend.setFadeListener(this::faded);
    }

    /**
     * Returns the voices of a backend.
     *
     * @param backend The backend.
     * @return Its voice limit.
     * @throws IllegalArgumentException If {@code backend} is {@code null}.
     */
    private static int maxVoices(AudioBackend backend)
    {
        ShortSoundCache.checkNotNull(backend, "Backend");
        return backend.getMaxVoices();
    }

    /**
     * Plays a short sound at {@link #DEFAULT_PRIORITY}.
     *
     * @param sound The sound, not released (never {@code null}).
     * @return The voice playing it, positive; {@link AudioBackend#NO_VOICE} if it was not played (every voice busy
     *         under the policy, or no voice free in the backend).
     * @throws IllegalArgumentException If {@code sound} is {@code null}.
     * @throws IllegalStateException If the sound is released, if every voice is busy under {@link VoicePolicy#REFUSE},
     *             or for the errors of a lazy sound decoded now.
     * @see #play(ShortSound, int)
     */
    public int play(ShortSound sound)
    {
        return play(sound, DEFAULT_PRIORITY);
    }

    /**
     * Plays a short sound at once, from its start, on a new voice, at its effective gain and looping if its handle
     * loops; a lazy sound is decoded first (ADR-0027).
     *
     * @param sound The sound, not released (never {@code null}).
     * @param priority The priority of the voice, from {@link #MIN_PRIORITY} to {@link #MAX_PRIORITY}.
     * @return The voice playing it, positive; {@link AudioBackend#NO_VOICE} if it was not played (every voice busy
     *         under the policy, or no voice free in the backend).
     * @throws IllegalArgumentException If {@code sound} is {@code null}, or the priority is outside its range.
     * @throws IllegalStateException If the sound is released, if every voice is busy under {@link VoicePolicy#REFUSE},
     *             if a lazy sound would exceed the limit of its cache, or if called on another thread than the owner
     *             of the cache of a lazy sound.
     * @throws com.b3dgs.lionengine.resources.ResourceException If the resource of a lazy sound is missing or
     *             unreadable.
     * @throws AudioDecodingException If the content of a lazy sound is invalid or too long.
     */
    public int play(ShortSound sound, int priority)
    {
        ShortSoundCache.checkNotNull(sound, "Sound");
        checkPriority(priority);
        return start(sound, priority, count);
    }

    /**
     * Plays a random variant set at {@link #DEFAULT_PRIORITY}.
     *
     * @param set The random set, of which no variant is released (never {@code null}).
     * @return The voice playing the chosen variant, positive; {@link AudioBackend#NO_VOICE} if it was not played.
     * @throws IllegalArgumentException If {@code set} is {@code null} or composed.
     * @throws IllegalStateException As {@link #play(VariantSet, int)}.
     * @see #play(VariantSet, int)
     */
    public int play(VariantSet set)
    {
        return play(set, DEFAULT_PRIORITY);
    }

    /**
     * Plays a random variant set: the set chooses a variant (ADR-0027, ADR-0067), which is played as
     * {@link #play(ShortSound, int)} plays a sound. The choice counts as the last play of the set even when the
     * variant is not played.
     *
     * @param set The random set, of which no variant is released (never {@code null}).
     * @param priority The priority of the voice, from {@link #MIN_PRIORITY} to {@link #MAX_PRIORITY}.
     * @return The voice playing the chosen variant, positive; {@link AudioBackend#NO_VOICE} if it was not played
     *         (every voice busy under the policy, or no voice free in the backend).
     * @throws IllegalArgumentException If {@code set} is {@code null} or composed, or the priority is outside its
     *             range.
     * @throws IllegalStateException If the chosen variant is released, for the errors of a lazy variant decoded now,
     *             or if every voice is busy under {@link VoicePolicy#REFUSE}.
     * @throws com.b3dgs.lionengine.resources.ResourceException If the resource of a lazy variant is missing or
     *             unreadable.
     * @throws AudioDecodingException If the content of a lazy variant is invalid or too long.
     * @see #play(VariantSet, int, int[])
     */
    public int play(VariantSet set, int priority)
    {
        ShortSoundCache.checkNotNull(set, NAME_SET);
        if (set.isComposed())
        {
            throw new IllegalArgumentException("Composed variant set: play it with a voices array");
        }
        checkPriority(priority);
        return start(set.getSound(set.select()), priority, count);
    }

    /**
     * Plays a variant set of either kind and writes its voices to an array, allocating nothing.
     *
     * <ul>
     * <li>A random set plays one chosen variant as {@link #play(VariantSet, int)} does; its voice is written to
     * {@code voices[0]}.</li>
     * <li>A composed set starts every layer in this call, in its order, so that they start together; the voice of
     * layer {@code i} is written to {@code voices[i]}. Every layer is decoded and checked before any starts. Under
     * {@link VoicePolicy#REFUSE}, the set is refused before any layer starts unless a voice is free for each layer;
     * under the other policies, each layer takes a voice as a single play would, but never a voice of another layer of
     * the same play, and a layer that finds no voice gets {@link AudioBackend#NO_VOICE}.</li>
     * </ul>
     *
     * <p>
     * Each started voice is stopped with {@link #stop(int)} and ends by itself as any voice.
     * </p>
     *
     * @param set The set, of which no sound is released (never {@code null}).
     * @param priority The priority of the voices, from {@link #MIN_PRIORITY} to {@link #MAX_PRIORITY}.
     * @param voices The array receiving the voices, at least {@link VariantSet#getVoicesPerPlay()} long; its first
     *            {@link VariantSet#getVoicesPerPlay()} entries are written, positive or {@link AudioBackend#NO_VOICE},
     *            the others are left as they are (never {@code null}).
     * @return The number of voices started, from {@code 0} to {@link VariantSet#getVoicesPerPlay()}.
     * @throws IllegalArgumentException If an argument is {@code null}, the priority is outside its range, or the array
     *             is too short; nothing starts then.
     * @throws IllegalStateException If a sound to play is released, if a lazy sound would exceed the limit of its
     *             cache, if called on another thread than the owner of the cache of a lazy sound, or if the voices are
     *             too few under {@link VoicePolicy#REFUSE}; no layer of a composed set starts then.
     * @throws com.b3dgs.lionengine.resources.ResourceException If the resource of a lazy sound is missing or
     *             unreadable.
     * @throws AudioDecodingException If the content of a lazy sound is invalid or too long.
     */
    public int play(VariantSet set, int priority, int[] voices)
    {
        ShortSoundCache.checkNotNull(set, NAME_SET);
        ShortSoundCache.checkNotNull(voices, "Voices");
        checkPriority(priority);
        final int needed = set.getVoicesPerPlay();
        if (voices.length < needed)
        {
            throw new IllegalArgumentException("Voices array of " + voices.length + " entries, " + needed + " needed");
        }
        final int started;
        if (set.isComposed())
        {
            started = playLayers(set, priority, voices);
        }
        else
        {
            voices[0] = play(set, priority);
            started = voices[0] == AudioBackend.NO_VOICE ? 0 : 1;
        }
        return started;
    }

    /**
     * Plays a long sound at once from its start, on a voice of the backend outside the short-sound limit; a sound that
     * is not prepared is prepared first, which reads its resource now (ADR-0068).
     *
     * <p>
     * The voice ends by itself at the end of the sound, delivered through the inbox, or is stopped with
     * {@link #stop(int)}; a looping sound never ends by itself. A streamed sound is no longer prepared once its voice
     * ends or is stopped.
     * </p>
     *
     * @param sound The sound, not released and not playing (never {@code null}).
     * @return The voice playing it, positive; {@link AudioBackend#NO_VOICE} if no voice of the backend is free, and the
     *         sound stays prepared.
     * @throws IllegalArgumentException If {@code sound} is {@code null}.
     * @throws IllegalStateException If the sound is released or already playing, or if called on another thread than
     *             the owner of its loader.
     * @throws com.b3dgs.lionengine.resources.ResourceException If the resource of a sound not prepared is missing or
     *             unreadable.
     * @throws AudioDecodingException If the content of a sound not prepared is invalid, or a fully decoded one is above
     *             the frame limit of its decoder.
     */
    public int play(LongSound sound)
    {
        ShortSoundCache.checkNotNull(sound, "Long sound");
        return play(sound, null, false);
    }

    /**
     * Stops a voice at once, of a short sound or of a long sound; no effect on a voice that ended, was stopped or does
     * not exist.
     *
     * @param voice The voice returned by a play.
     */
    public void stop(int voice)
    {
        final int slot = slot(voice);
        if (slot != NONE)
        {
            backend.stop(voice);
            remove(slot);
        }
        else
        {
            final int longSlot = longSlot(voice);
            if (longSlot != NONE)
            {
                backend.stop(voice);
                removeLong(longSlot).stopped();
            }
        }
    }

    /**
     * Stops every short voice, paused or not, for example when a scene exits; long sounds and playlists are stopped by
     * their own voice or {@link Playlist#stop()}.
     */
    public void stopAll()
    {
        while (count > 0)
        {
            backend.stop(voices[count - 1]);
            count--;
            sounds[count] = null;
            groups[count] = null;
        }
    }

    /**
     * Tells whether a voice is playing, of a short sound or of a long sound.
     *
     * @param voice The voice.
     * @return {@code true} from its play until it is stopped or its end is delivered, also while it is paused.
     */
    public boolean isPlaying(int voice)
    {
        return slot(voice) != NONE || longSlot(voice) != NONE;
    }

    /**
     * Pauses a voice, of a short sound or of a long sound, where it is; no effect on a paused voice, or on a voice that
     * ended, was stopped or does not exist. The voice keeps its voice and posts no end until it is resumed; the stream
     * of a long sound is not read meanwhile, and a {@link Playlist} playing it is paused with it.
     *
     * @param voice The voice returned by a play.
     */
    public void pause(int voice)
    {
        setPaused(voice, true);
    }

    /**
     * Resumes a paused voice from where it was paused; no effect on a voice that is not paused, ended, was stopped or
     * does not exist. During the master pause, the voice continues when the master pause ends.
     *
     * @param voice The voice returned by a play.
     */
    public void resume(int voice)
    {
        setPaused(voice, false);
    }

    /**
     * Tells whether a voice is paused on its own, by {@link #pause(int)}; the master pause is told by
     * {@link #isMasterPaused()}.
     *
     * @param voice The voice.
     * @return {@code true} from its pause until it is resumed, stopped or stolen; {@code false} for a voice that is not
     *         playing.
     */
    public boolean isPaused(int voice)
    {
        final int slot = slot(voice);
        final boolean result;
        if (slot != NONE)
        {
            result = paused[slot];
        }
        else
        {
            final int longSlot = longSlot(voice);
            result = longSlot != NONE && longPaused[longSlot];
        }
        return result;
    }

    /**
     * Sets the master pause, which pauses the output of the backend: every voice, short or long, holds where it is,
     * for example while the application is in the background (ADR-0031). It is independent of the pause of each voice:
     * a voice paused on its own stays paused when the master pause ends, and a sound played during the master pause
     * starts when it ends. No effect if the master pause is already in that state.
     *
     * @param pausedOutput {@code true} to pause the output, {@code false} to resume it.
     */
    public void setMasterPaused(boolean pausedOutput)
    {
        if (pausedOutput != masterPaused)
        {
            masterPaused = pausedOutput;
            if (pausedOutput)
            {
                backend.pauseOutput();
            }
            else
            {
                backend.resumeOutput();
            }
        }
    }

    /**
     * Tells whether the master pause is on.
     *
     * @return {@code true} while the output is paused.
     */
    public boolean isMasterPaused()
    {
        return masterPaused;
    }

    /**
     * Sets the master gain, which multiplies the gain of every sound; every voice playing gets its new effective gain
     * at once.
     *
     * @param gain The master gain, from {@link #MIN_GAIN} to {@link #MAX_GAIN} ({@link #MAX_GAIN} by default).
     * @throws IllegalArgumentException If the gain is NaN or outside its range; nothing changes then.
     */
    public void setMasterGain(double gain)
    {
        checkGain(gain);
        masterGain = gain;
        for (int i = 0; i < count; i++)
        {
            backend.setGain(voices[i], effective(groups[i], sounds[i].getGain()));
        }
        for (int i = 0; i < longCount; i++)
        {
            backend.setGain(longVoices[i], effective(longGroups[i], longSounds[i].getGain()));
        }
    }

    /**
     * Returns the master gain.
     *
     * @return The gain multiplying the gain of every sound.
     */
    public double getMasterGain()
    {
        return masterGain;
    }

    /**
     * Adds a mixer group of the game, of full gain.
     *
     * @param id The id of the group, not used by another group of this system (never {@code null} nor empty).
     * @return The new group.
     * @throws IllegalArgumentException If the id is {@code null}, empty, or already a group of this system.
     */
    public MixerGroup addGroup(String id)
    {
        MixerGroup.checkId(id);
        if (findGroup(id) != null)
        {
            throw new IllegalArgumentException("Mixer group already defined: " + id);
        }
        if (groupCount == mixerGroups.length)
        {
            mixerGroups = Arrays.copyOf(mixerGroups, groupCount * 2);
        }
        final MixerGroup group = new MixerGroup(this, id);
        mixerGroups[groupCount] = group;
        groupCount++;
        return group;
    }

    /**
     * Returns a mixer group.
     *
     * @param id The id of the group: {@link MixerGroup#MUSIC}, {@link MixerGroup#EFFECTS}, {@link MixerGroup#UI} or a
     *            group added by {@link #addGroup(String)}.
     * @return The group.
     * @throws IllegalArgumentException If the id is {@code null}, empty, or not a group of this system.
     */
    public MixerGroup getGroup(String id)
    {
        MixerGroup.checkId(id);
        final MixerGroup group = findGroup(id);
        if (group == null)
        {
            throw new IllegalArgumentException(ERROR_GROUP + id);
        }
        return group;
    }

    /**
     * Tells whether a mixer group exists.
     *
     * @param id The id of the group.
     * @return {@code true} if it is a group of this system, the default ones included; {@code false} for
     *         {@code null}.
     */
    public boolean hasGroup(String id)
    {
        return id != null && findGroup(id) != null;
    }

    /**
     * Sets the listener of the fades: it is called on the runtime thread, at the inbox delivery of the end of the ramp
     * of each voice, if the voice still plays; it replaces the previous one.
     *
     * @param listener The listener, {@code null} for none.
     */
    public void setFadeListener(FadeListener listener)
    {
        fadeListener = listener;
    }

    /**
     * Returns the number of short voices playing.
     *
     * @return The short voices neither stopped nor ended, at most {@link #getMaxVoices()}.
     */
    public int getPlayingCount()
    {
        return count;
    }

    /**
     * Returns the number of long voices playing.
     *
     * @return The long sounds playing, outside the short-sound limit.
     */
    public int getLongPlayingCount()
    {
        return longCount;
    }

    /**
     * Returns the voice limit.
     *
     * @return The number of short sounds playing at once.
     */
    public int getMaxVoices()
    {
        return maxVoices;
    }

    /**
     * Returns the overflow policy.
     *
     * @return The policy applied when every voice is busy.
     */
    public VoicePolicy getPolicy()
    {
        return policy;
    }

    /**
     * Plays a long sound for a playlist, which listens to its end.
     *
     * @param sound The sound, not {@code null}.
     * @param listener The listener of its end, {@code null} for none.
     * @param loop {@code true} to loop the voice even if the sound does not loop, for a playlist of one track that
     *            loops.
     * @return The voice, or {@link AudioBackend#NO_VOICE}.
     * @throws IllegalStateException As {@link #play(LongSound)}.
     * @throws com.b3dgs.lionengine.resources.ResourceException As {@link #play(LongSound)}.
     * @throws AudioDecodingException As {@link #play(LongSound)}.
     */
    int play(LongSound sound, TrackListener listener, boolean loop)
    {
        if (sound.isPlaying())
        {
            throw new IllegalStateException("Long sound already playing: " + sound.getKey());
        }
        final MixerGroup group = groupOf(sound.getGroup());
        final int voice = sound.start(backend, effective(group, sound.getGain()), loop || sound.isLooping());
        if (voice != AudioBackend.NO_VOICE)
        {
            if (longCount == longVoices.length)
            {
                longVoices = Arrays.copyOf(longVoices, longCount * 2);
                longSounds = Arrays.copyOf(longSounds, longCount * 2);
                longPaused = Arrays.copyOf(longPaused, longCount * 2);
                longGroups = Arrays.copyOf(longGroups, longCount * 2);
            }
            longVoices[longCount] = voice;
            longSounds[longCount] = sound;
            longPaused[longCount] = false;
            longGroups[longCount] = group;
            longCount++;
            sound.started(this, voice, listener);
        }
        return voice;
    }

    /**
     * Gives the voices playing a short sound its new gain, at a change of its gain.
     *
     * @param sound The sound.
     */
    void updateGain(ShortSound sound)
    {
        for (int i = 0; i < count; i++)
        {
            if (sounds[i] == sound)
            {
                backend.setGain(voices[i], effective(groups[i], sound.getGain()));
            }
        }
    }

    /**
     * Gives the voice of a long sound its new gain, at a change of its gain.
     *
     * @param sound The sound, playing.
     * @param voice The voice playing it.
     */
    void updateGain(LongSound sound, int voice)
    {
        final int slot = longSlot(voice);
        if (slot != NONE)
        {
            backend.setGain(voice, effective(longGroups[slot], sound.getGain()));
        }
    }

    /**
     * Gives the voices of a mixer group their new gain, at a change of its gain.
     *
     * @param group The group.
     */
    void updateGain(MixerGroup group)
    {
        for (int i = 0; i < count; i++)
        {
            if (groups[i] == group)
            {
                backend.setGain(voices[i], effective(group, sounds[i].getGain()));
            }
        }
        for (int i = 0; i < longCount; i++)
        {
            if (longGroups[i] == group)
            {
                backend.setGain(longVoices[i], effective(group, longSounds[i].getGain()));
            }
        }
    }

    /**
     * Ramps the voices playing a short sound to their new gain, at a fade of the sound.
     *
     * @param sound The sound, its gain set to the target.
     * @param seconds The duration, checked.
     */
    void fade(ShortSound sound, double seconds)
    {
        for (int i = 0; i < count; i++)
        {
            if (sounds[i] == sound)
            {
                backend.fade(voices[i], effective(groups[i], sound.getGain()), seconds);
            }
        }
    }

    /**
     * Ramps the voice of a long sound to its new gain, at a fade of the sound.
     *
     * @param sound The sound, its gain set to the target, playing.
     * @param voice The voice playing it.
     * @param seconds The duration, checked.
     */
    void fade(LongSound sound, int voice, double seconds)
    {
        final int slot = longSlot(voice);
        if (slot != NONE)
        {
            backend.fade(voice, effective(longGroups[slot], sound.getGain()), seconds);
        }
    }

    /**
     * Ramps the voices of a mixer group to their new gain, at a fade of the group.
     *
     * @param group The group, its gain set to the target.
     * @param seconds The duration, checked.
     */
    void fade(MixerGroup group, double seconds)
    {
        for (int i = 0; i < count; i++)
        {
            if (groups[i] == group)
            {
                backend.fade(voices[i], effective(group, sounds[i].getGain()), seconds);
            }
        }
        for (int i = 0; i < longCount; i++)
        {
            if (longGroups[i] == group)
            {
                backend.fade(longVoices[i], effective(group, longSounds[i].getGain()), seconds);
            }
        }
    }

    /**
     * Stops the looping voices of a short sound, at its release: they would never end.
     *
     * @param sound The sound.
     */
    void stopLooping(ShortSound sound)
    {
        int i = 0;
        while (i < count)
        {
            if (sounds[i] == sound && loops[i])
            {
                backend.stop(voices[i]);
                remove(i);
            }
            else
            {
                i++;
            }
        }
    }

    /**
     * Checks a gain.
     *
     * @param gain The gain.
     * @throws IllegalArgumentException If it is NaN or outside {@link #MIN_GAIN} to {@link #MAX_GAIN}.
     */
    static void checkGain(double gain)
    {
        if (!(gain >= MIN_GAIN && gain <= MAX_GAIN))
        {
            throw new IllegalArgumentException("Gain outside 0 to 1: " + gain);
        }
    }

    /**
     * Checks a fade duration.
     *
     * @param seconds The duration.
     * @throws IllegalArgumentException If it is NaN, infinite or negative.
     */
    static void checkSeconds(double seconds)
    {
        if (!(seconds >= 0.0 && seconds < Double.POSITIVE_INFINITY))
        {
            throw new IllegalArgumentException("Fade duration not finite and not negative: " + seconds);
        }
    }

    /**
     * Checks a priority.
     *
     * @param priority The priority.
     * @throws IllegalArgumentException If it is outside {@link #MIN_PRIORITY} to {@link #MAX_PRIORITY}.
     */
    private static void checkPriority(int priority)
    {
        if (priority < MIN_PRIORITY || priority > MAX_PRIORITY)
        {
            throw new IllegalArgumentException("Priority outside 0 to 100: " + priority);
        }
    }

    /**
     * Starts every layer of a composed set, after decoding and checking them all.
     *
     * @param set The composed set.
     * @param priority The priority of the voices, checked.
     * @param voices The array receiving the voices, long enough.
     * @return The number of voices started.
     * @throws IllegalStateException If a layer is released or cannot be decoded now, or if the voices are too few
     *             under {@link VoicePolicy#REFUSE}.
     */
    private int playLayers(VariantSet set, int priority, int[] voices)
    {
        final int layers = set.size();
        for (int i = 0; i < layers; i++)
        {
            final ShortSound layer = set.getSound(i);
            layer.getBuffer();
            groupOf(layer.getGroup());
        }
        if (policy == VoicePolicy.REFUSE && maxVoices - count < layers)
        {
            throw new IllegalStateException("Short voices too few for a composed set of "
                                            + layers
                                            + " layers: "
                                            + count
                                            + " of "
                                            + maxVoices
                                            + ERROR_PLAYING);
        }
        int started = 0;
        for (int i = 0; i < layers; i++)
        {
            // The voices of this play are the last slots: only the slots before them may be stolen.
            voices[i] = start(set.getSound(i), priority, count - started);
            if (voices[i] != AudioBackend.NO_VOICE)
            {
                started++;
            }
        }
        return started;
    }

    /**
     * Returns the effective gain of a voice: master × group × sound (ADR-0033, REQ-AUDIO-012).
     *
     * @param group The mixer group of the voice.
     * @param soundGain The gain of the sound.
     * @return The master gain times the gain of the group times the gain of the sound.
     */
    private double effective(MixerGroup group, double soundGain)
    {
        return masterGain * group.getGain() * soundGain;
    }

    /**
     * Finds a mixer group.
     *
     * @param id The id, not {@code null}.
     * @return The group, {@code null} if none has this id.
     */
    private MixerGroup findGroup(String id)
    {
        MixerGroup found = null;
        for (int i = 0; i < groupCount && found == null; i++)
        {
            if (mixerGroups[i].getId().equals(id))
            {
                found = mixerGroups[i];
            }
        }
        return found;
    }

    /**
     * Returns the mixer group of a sound to play.
     *
     * @param id The id of the group of the sound.
     * @return The group.
     * @throws IllegalStateException If no group of this system has this id; nothing is played then.
     */
    private MixerGroup groupOf(String id)
    {
        final MixerGroup group = findGroup(id);
        if (group == null)
        {
            throw new IllegalStateException(ERROR_GROUP + id);
        }
        return group;
    }

    /**
     * Pauses or resumes a voice.
     *
     * @param voice The voice.
     * @param pause {@code true} to pause it, {@code false} to resume it.
     */
    private void setPaused(int voice, boolean pause)
    {
        final int slot = slot(voice);
        if (slot != NONE)
        {
            if (paused[slot] != pause)
            {
                paused[slot] = pause;
                pauseBackend(voice, pause);
            }
        }
        else
        {
            final int longSlot = longSlot(voice);
            if (longSlot != NONE && longPaused[longSlot] != pause)
            {
                longPaused[longSlot] = pause;
                pauseBackend(voice, pause);
            }
        }
    }

    /**
     * Pauses or resumes a voice of the backend.
     *
     * @param voice The voice.
     * @param pause {@code true} to pause it, {@code false} to resume it.
     */
    private void pauseBackend(int voice, boolean pause)
    {
        if (pause)
        {
            backend.pause(voice);
        }
        else
        {
            backend.resume(voice);
        }
    }

    /**
     * Starts a short sound on a new voice, freeing one first if every voice is busy.
     *
     * @param sound The sound.
     * @param priority The priority of the voice, checked.
     * @param candidates The number of the oldest slots that may be stolen.
     * @return The voice, or {@link AudioBackend#NO_VOICE}.
     * @throws IllegalStateException If every voice is busy under {@link VoicePolicy#REFUSE}, or for the errors of the
     *             sound.
     */
    private int start(ShortSound sound, int priority, int candidates)
    {
        final PcmBuffer buffer = sound.getBuffer();
        final MixerGroup group = groupOf(sound.getGroup());
        int voice = AudioBackend.NO_VOICE;
        if (count < maxVoices || freeVoice(priority, candidates))
        {
            final boolean looping = sound.isLooping();
            voice = backend.play(buffer, effective(group, sound.getGain()), looping);
            if (voice != AudioBackend.NO_VOICE)
            {
                voices[count] = voice;
                priorities[count] = priority;
                sounds[count] = sound;
                loops[count] = looping;
                paused[count] = false;
                groups[count] = group;
                count++;
                sound.played(this);
            }
        }
        return voice;
    }

    /**
     * Frees a voice for a new sound when every voice is busy, according to the policy.
     *
     * @param priority The priority of the new sound.
     * @param candidates The number of the oldest slots that may be stolen.
     * @return {@code true} if a voice was stolen, {@code false} if the new sound is dropped.
     * @throws IllegalStateException Under {@link VoicePolicy#REFUSE}.
     */
    private boolean freeVoice(int priority, int candidates)
    {
        if (policy == VoicePolicy.REFUSE)
        {
            throw new IllegalStateException("Every short voice is busy: " + maxVoices + ERROR_PLAYING);
        }
        return policy == VoicePolicy.STEAL && candidates > 0 && steal(priority, candidates);
    }

    /**
     * Stops the oldest voice of the lowest priority among the oldest slots, if that priority is not above a new
     * sound's.
     *
     * @param priority The priority of the new sound.
     * @param candidates The number of the oldest slots that may be stolen, positive.
     * @return {@code true} if a voice was stopped.
     */
    private boolean steal(int priority, int candidates)
    {
        int victim = 0;
        for (int i = 1; i < candidates; i++)
        {
            if (priorities[i] < priorities[victim])
            {
                victim = i;
            }
        }
        final boolean stolen = priorities[victim] <= priority;
        if (stolen)
        {
            backend.stop(voices[victim]);
            remove(victim);
        }
        return stolen;
    }

    /**
     * Calls the fade listener at the inbox delivery of the end of a ramp, if the voice still plays.
     *
     * @param voice The voice.
     */
    private void faded(int voice)
    {
        final FadeListener current = fadeListener;
        if (current != null && isPlaying(voice))
        {
            current.onFadeEnded(voice);
        }
    }

    /**
     * Frees the slot of a voice that ended by itself, at the inbox delivery of its end.
     *
     * @param voice The voice.
     */
    private void ended(int voice)
    {
        final int slot = slot(voice);
        if (slot != NONE)
        {
            remove(slot);
        }
        else
        {
            final int longSlot = longSlot(voice);
            if (longSlot != NONE)
            {
                removeLong(longSlot).ended();
            }
        }
    }

    /**
     * Finds the slot of a long voice.
     *
     * @param voice The voice.
     * @return The index of its slot, {@link #NONE} if it is not playing.
     */
    private int longSlot(int voice)
    {
        int slot = NONE;
        for (int i = 0; i < longCount && slot == NONE; i++)
        {
            if (longVoices[i] == voice)
            {
                slot = i;
            }
        }
        return slot;
    }

    /**
     * Removes a long slot, keeping the start order of the others.
     *
     * @param slot The index of the slot.
     * @return The long sound of the slot.
     */
    private LongSound removeLong(int slot)
    {
        final LongSound sound = longSounds[slot];
        final int moved = longCount - slot - 1;
        System.arraycopy(longVoices, slot + 1, longVoices, slot, moved);
        System.arraycopy(longSounds, slot + 1, longSounds, slot, moved);
        System.arraycopy(longPaused, slot + 1, longPaused, slot, moved);
        System.arraycopy(longGroups, slot + 1, longGroups, slot, moved);
        longCount--;
        longSounds[longCount] = null;
        longGroups[longCount] = null;
        return sound;
    }

    /**
     * Finds the slot of a voice.
     *
     * @param voice The voice.
     * @return The index of its slot, {@link #NONE} if it is not playing.
     */
    private int slot(int voice)
    {
        int slot = NONE;
        for (int i = 0; i < count && slot == NONE; i++)
        {
            if (voices[i] == voice)
            {
                slot = i;
            }
        }
        return slot;
    }

    /**
     * Removes a slot, keeping the start order of the others.
     *
     * @param slot The index of the slot.
     */
    private void remove(int slot)
    {
        final int moved = count - slot - 1;
        System.arraycopy(voices, slot + 1, voices, slot, moved);
        System.arraycopy(priorities, slot + 1, priorities, slot, moved);
        System.arraycopy(sounds, slot + 1, sounds, slot, moved);
        System.arraycopy(loops, slot + 1, loops, slot, moved);
        System.arraycopy(paused, slot + 1, paused, slot, moved);
        System.arraycopy(groups, slot + 1, groups, slot, moved);
        count--;
        sounds[count] = null;
        groups[count] = null;
    }
}
