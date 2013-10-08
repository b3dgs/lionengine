/*
 * Copyright (C) 2013 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.b3dgs.lionengine.example.warcraft;

import com.b3dgs.lionengine.audio.AudioWav;
import com.b3dgs.lionengine.audio.Wav;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.Sprite;
import com.b3dgs.lionengine.drawable.SpriteAnimated;
import com.b3dgs.lionengine.drawable.SpriteTiled;
import com.b3dgs.lionengine.example.warcraft.type.TypeRace;

/**
 * Static access to loaded resources for misc uses.
 */
public final class ResourcesLoader
{
    /** Effects directory. */
    public static final String EFFECT_DIR = "effects";
    /** Entities directory. */
    public static final String ENTITIES_DIR = "entities";
    /** Projectiles directory. */
    public static final String PROJECTILES_DIR = "projectiles";
    /** Skills directory. */
    public static final String SKILLS_DIR = "skills";
    /** Maps directory. */
    public static final String MAPS_DIR = "maps";
    /** Tiles directory. */
    public static final String TILES_DIR = "tiles";
    /** Weapons directory. */
    public static final String WEAPONS_DIR = "weapons";
    /** Sounds FX directory. */
    public static final String SFXS_DIR = "sfxs";
    /** Musics directory. */
    public static final String MUSICS_DIR = "musics";
    /** Menu directory. */
    public static final String MENU_DIR = "menu";
    /** Gold icon. */
    public static final Sprite GOLD;
    /** Wood icon. */
    public static final Sprite WOOD;
    /** Hud sprite. */
    public static final Sprite HUD;
    /** Entity stats. */
    public static final Sprite ENTITY_STATS;
    /** Hud sprite. */
    public static final Sprite PROGRESS;
    /** Skill background. */
    public static final SpriteTiled SKILL_BACKGROUND;
    /** Constructing building. */
    public static final SpriteAnimated CONSTRUCTION;
    /** Burning building. */
    public static final SpriteAnimated BURNING;
    /** Burning building. */
    public static final SpriteAnimated EXPLODE;
    /** Click sound. */
    public static final Wav SOUND_CLICK;
    /** Corps human. */
    private static final SpriteAnimated CORPSE_HUMAN;
    /** Corps orc. */
    private static final SpriteAnimated CORPSE_ORC;
    /** Loaded flag. */
    private static boolean loaded = false;

    /**
     * Initialize.
     */
    static
    {
        GOLD = Drawable.loadSprite(Media.get("gold.png"));
        WOOD = Drawable.loadSprite(Media.get("wood.png"));
        HUD = Drawable.loadSprite(Media.get("hud.png"));
        ENTITY_STATS = Drawable.loadSprite(Media.get("entity_stats.png"));
        PROGRESS = Drawable.loadSprite(Media.get("progress.png"));
        SKILL_BACKGROUND = Drawable.loadSpriteTiled(Media.get("skill_background.png"), 31, 23);
        CORPSE_HUMAN = Drawable.loadSpriteAnimated(Media.get(ResourcesLoader.EFFECT_DIR, "corpse_human.png"), 4, 2);
        CORPSE_ORC = Drawable.loadSpriteAnimated(Media.get(ResourcesLoader.EFFECT_DIR, "corpse_orc.png"), 4, 2);
        CONSTRUCTION = Drawable.loadSpriteAnimated(Media.get(ResourcesLoader.EFFECT_DIR, "construction.png"), 3, 1);
        BURNING = Drawable.loadSpriteAnimated(Media.get(ResourcesLoader.EFFECT_DIR, "burning.png"), 4, 2);
        EXPLODE = Drawable.loadSpriteAnimated(Media.get(ResourcesLoader.EFFECT_DIR, "explode.png"), 6, 3);
        SOUND_CLICK = AudioWav.loadWav(Media.get(ResourcesLoader.SFXS_DIR, "click1.wav"));
    }

    /**
     * Get the corps depending of the race.
     * 
     * @param race The race.
     * @return The corps instance.
     */
    public static SpriteAnimated getCorpse(TypeRace race)
    {
        switch (race)
        {
            case HUMAN:
                return Drawable.loadSpriteAnimated(ResourcesLoader.CORPSE_HUMAN.getSurface(), 4, 2);
            case ORC:
                return Drawable.loadSpriteAnimated(ResourcesLoader.CORPSE_ORC.getSurface(), 4, 2);
            default:
                throw new RuntimeException();
        }
    }

    /**
     * Load all surfaces.
     */
    static void load()
    {
        if (!ResourcesLoader.loaded)
        {
            ResourcesLoader.HUD.load(false);
            ResourcesLoader.ENTITY_STATS.load(false);
            ResourcesLoader.SKILL_BACKGROUND.load(false);
            ResourcesLoader.GOLD.load(false);
            ResourcesLoader.WOOD.load(false);
            ResourcesLoader.PROGRESS.load(false);
            ResourcesLoader.CORPSE_HUMAN.load(false);
            ResourcesLoader.CORPSE_ORC.load(false);
            ResourcesLoader.CONSTRUCTION.load(false);
            ResourcesLoader.BURNING.load(false);
            ResourcesLoader.EXPLODE.load(false);
            ResourcesLoader.loaded = true;
        }
    }

    /**
     * Constructor.
     */
    private ResourcesLoader()
    {
        throw new RuntimeException();
    }
}
