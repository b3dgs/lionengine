package com.b3dgs.lionengine.example.c_platform.e_lionheart;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.audio.AudioWav;
import com.b3dgs.lionengine.audio.Wav;
import com.b3dgs.lionengine.utility.UtilityRandom;

/**
 * Handle the SFX.
 */
public enum Sfx
{
    /** Blank. */
    BLANK("blank"),
    /** Menu select. */
    SELECT("select"),
    /** Explode sound. */
    EXPLODE(160, 5, "explode1", "explode2", "explode3"),
    /** Sword sounds. */
    SWORD("valdyn_attack"),
    /** Item taken sound. */
    ITEM_TAKEN(0, 4, "item_taken"),
    /** Item potion big. */
    ITEM_POTION_BIG("item_potionfull"),
    /** Item potion. */
    ITEM_POTION_LITTLE("item_potion"),
    /** Bipbipbip sound. */
    BIPBIPBIP(0, 3, "bipbipbip"),
    /** Monster hurt. */
    MONSTER_HURT("monster_hurt"),
    /** Valdyn hurt. */
    VALDYN_HURT("valdyn_hurt"),
    /** Valdyn die. */
    VALDYN_DIE("valdyn_die");

    /** Audio file extension. */
    private static final String FILE_EXTENSION = ".wav";

    /**
     * Static init.
     */
    static
    {
        Sfx.BLANK.play();
    }

    /**
     * Stop all sounds.
     */
    public static void terminate()
    {
        for (Sfx sfx : Sfx.values())
        {
            sfx.stop();
        }
    }

    /** Sound delay. */
    private final int delay;
    /** Sound count. */
    private final int count;
    /** Sounds list composing the effect. */
    private final Wav[] sounds;

    /**
     * Constructor.
     * 
     * @param sounds Sounds list.
     */
    private Sfx(String... sounds)
    {
        this(0, 1, sounds);
    }

    /**
     * Constructor.
     * 
     * @param delay The sounds delay.
     * @param count The total number of random.
     * @param sounds The sounds list.
     */
    private Sfx(int delay, int count, String... sounds)
    {
        this.delay = delay;
        this.count = count;
        this.sounds = new Wav[sounds.length];
        for (int i = 0; i < sounds.length; i++)
        {
            this.sounds[i] = AudioWav.loadWav(Media.get(AppLionheart.SFX_DIR, sounds[i] + Sfx.FILE_EXTENSION), count);
        }
    }

    /**
     * Play the sound effect.
     */
    public void play()
    {
        if (sounds.length == 1)
        {
            sounds[0].stop();
            sounds[0].play();
        }
        else
        {
            for (int i = 0; i < count; i++)
            {
                final int rand = UtilityRandom.getRandomInteger(sounds.length - 1);
                sounds[rand].play(delay * i);
            }
        }
    }

    /**
     * Stop all channels.
     */
    private void stop()
    {
        for (Wav wav : sounds)
        {
            wav.terminate();
        }
    }
}
