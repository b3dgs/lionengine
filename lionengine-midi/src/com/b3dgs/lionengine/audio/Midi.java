package com.b3dgs.lionengine.audio;


/**
 * Handle midi routine. A midi is a light sound, designed to be played as a background music. Midi are played in a
 * separated thread. It supports the following main controls:
 * <ul>
 * <li>Start index</li>
 * <li>Loop (range setting)</li>
 * <li>Volume</li>
 * <li>Pause & resume</li>
 * </ul>
 * <p>
 * The <code>tick</code> represents the position in the sound data.
 * </p>
 * <p>
 * Example:
 * </p>
 * 
 * <pre>
 * final Midi midi = AudioMidi.loadMidi(Media.get(&quot;music.mid&quot;));
 * midi.play(false);
 * 
 * Thread.sleep(1000);
 * midi.pause();
 * Thread.sleep(1000);
 * midi.resume();
 * midi.pause();
 * midi.stop();
 * </pre>
 */
public interface Midi
{
    /** Minimum volume value. */
    int VOLUME_MIN = 0;
    /** Maximum volume value. */
    int VOLUME_MAX = 100;

    /**
     * Get the total number of ticks.
     * 
     * @return The total number of ticks.
     */
    long getTicks();

    /**
     * Set starting tick (starting music position).
     * 
     * @param tick The starting tick <code>[0 - {@link #getTicks()}]</code>.
     */
    void setStart(long tick);

    /**
     * Set loop area in tick.
     * 
     * @param first The first tick <code>[0 - last}]</code>.
     * @param last The last tick <code>[first - {@link #getTicks()}}]</code>.
     */
    void setLoop(long first, long last);

    /**
     * Set the midi volume.
     * 
     * @param volume The volume in percent <code>[{@link #VOLUME_MIN} - {@link #VOLUME_MAX}]</code>.
     */
    void setVolume(int volume);

    /**
     * Play the music.
     * <p>
     * The music will be played from the beginning (can be set by {@link #setStart(long)}) until the end.
     * </p>
     * <p>
     * In case of a loop, music will be played in loop between the set ticks using {@link #setLoop(long, long)}.
     * </p>
     * 
     * @param repeat The loop flag.
     */
    void play(boolean repeat);

    /**
     * Stop the music.
     */
    void stop();

    /**
     * Pause the music (can be resumed).
     */
    void pause();

    /**
     * Resume the music (if paused).
     */
    void resume();
}
