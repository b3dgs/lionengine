package com.b3dgs.lionengine.audio;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.utility.UtilityMath;

/**
 * Sound routine implementation. One sound represents one thread.
 */
final class WavRoutine
        extends Thread
{
    /** Sound buffer size. */
    static final int BUFFER = 128000;
    /** Way player reference. */
    private final WavPlayer player;
    /** Sound file reference. */
    private Media media;
    /** Sound alignment. */
    private Align alignment;
    /** Sound volume. */
    private int volume;
    /** Routine flags. */
    private boolean isRunning;
    /** Playing flag. */
    private boolean isPlaying;
    /** Restart flag. */
    private boolean restart;
    /** Audio output. */
    private SourceDataLine sourceDataLine;
    /** Audio stream. */
    private AudioInputStream audioInputStream;

    /**
     * Create a sound routine.
     * 
     * @param player The wav player reference.
     */
    WavRoutine(WavPlayer player)
    {
        super("SFX Player");
        this.player = player;
        isRunning = true;
        media = null;
        isPlaying = false;
        restart = false;
    }

    /**
     * Set sound alignment.
     * 
     * @param alignment sound alignment.
     */
    void setAlignement(Align alignment)
    {
        this.alignment = alignment;
    }

    /**
     * Set sound file name.
     * 
     * @param media The audio sound media to play.
     */
    void setMedia(Media media)
    {
        this.media = media;
    }

    /**
     * Terminate play and pause thread.
     */
    void terminate()
    {
        isRunning = false;
        media = null;
    }

    /**
     * Set sound volume.
     * 
     * @param vol sound volume.
     */
    void setVolume(int vol)
    {
        volume = vol;
    }

    /**
     * Stop sound, close stream and flush output.
     */
    void stopSound()
    {
        if (sourceDataLine != null)
        {
            try
            {
                sourceDataLine.flush();
                sourceDataLine.stop();
                audioInputStream.close();
                restart = false;
            }
            catch (final IOException exception)
            {
                Verbose.exception(WavRoutine.class, "stopSound", exception);
            }
        }
    }

    /**
     * Restart sound.
     */
    void restart()
    {
        restart = true;
    }

    /**
     * Check if sound is playing.
     * 
     * @return true if playing, false else.
     */
    boolean isPlaying()
    {
        return isPlaying;
    }

    /*
     * Thread
     */

    @Override
    public void run()
    {
        while (isRunning)
        {
            player.addBusy(this);
            if (media != null)
            {
                final String filename = media.getPath();
                try
                {
                    isPlaying = true;
                    restart = false;

                    // Open stream
                    final File file = Media.getTempFile(media, true, false);
                    audioInputStream = AudioSystem.getAudioInputStream(file);
                    final AudioFormat audioFormat = audioInputStream.getFormat();
                    final DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, audioFormat);

                    // Prepare output
                    sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
                    sourceDataLine.open(audioFormat);

                    // Set alignment
                    if (sourceDataLine.isControlSupported(FloatControl.Type.PAN))
                    {
                        final FloatControl pan = (FloatControl) sourceDataLine.getControl(FloatControl.Type.PAN);
                        switch (alignment)
                        {
                            case CENTER:
                                pan.setValue(0.0f);
                                break;
                            case RIGHT:
                                pan.setValue(1.0f);
                                break;
                            case LEFT:
                                pan.setValue(-1.0f);
                                break;
                            default:
                                throw new LionEngineException("Unsupported alignment !");
                        }
                    }

                    // Set volume
                    if (sourceDataLine.isControlSupported(FloatControl.Type.MASTER_GAIN))
                    {
                        final FloatControl gainControl = (FloatControl) sourceDataLine
                                .getControl(FloatControl.Type.MASTER_GAIN);
                        final double gain = UtilityMath.fixBetween(volume / 100.0, 0.0, 100.0);
                        final double dB = Math.log(gain) / Math.log(10.0) * 20.0;
                        gainControl.setValue((float) dB);
                    }

                    // Start playing by filling buffer till the end
                    sourceDataLine.start();
                    int cnt;
                    final byte[] tempBuffer = new byte[WavRoutine.BUFFER];

                    while ((cnt = audioInputStream.read(tempBuffer, 0, tempBuffer.length)) != -1)
                    {
                        if (cnt > 0)
                        {
                            sourceDataLine.write(tempBuffer, 0, cnt);
                        }
                    }

                    // Flush and close stream
                    sourceDataLine.drain();
                    sourceDataLine.flush();
                    sourceDataLine.stop();
                    sourceDataLine.close();
                    audioInputStream.close();
                }
                catch (final UnsupportedAudioFileException
                             | IllegalArgumentException exception)
                {
                    Verbose.critical(WavRoutine.class, "run", "Unsupported audio format: \"", filename, "\"");
                }
                catch (final LineUnavailableException exception)
                {
                    Verbose.critical(WavRoutine.class, "run", "Unavailable audio line: \"", filename, "\"");
                }
                catch (final IOException exception)
                {
                    Verbose.critical(WavRoutine.class, "run", "Error on playing sound \"", filename, "\"");
                }
            }

            isPlaying = false;
            if (!restart)
            {
                media = null;
            }
            synchronized (player.monitor)
            {
                try
                {
                    player.addFree(this);
                    player.monitor.wait();
                }
                catch (final InterruptedException exception)
                {
                    interrupt();
                    media = null;
                    isRunning = false;
                    synchronized (player.count)
                    {
                        player.decreaseCount();
                    }
                }
            }
        }
    }
}
