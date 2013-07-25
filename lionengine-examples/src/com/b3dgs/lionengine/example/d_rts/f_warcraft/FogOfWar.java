package com.b3dgs.lionengine.example.d_rts.f_warcraft;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.SpriteTiled;
import com.b3dgs.lionengine.game.rts.map.FogOfWarRts;

/**
 * Fog of war implementation base.
 */
public final class FogOfWar
        extends FogOfWarRts<Tile>
{
    /**
     * Constructor.
     * 
     * @param config The game configuration.
     */
    FogOfWar(GameConfig config)
    {
        final SpriteTiled hide = Drawable.loadSpriteTiled(Media.get(ResourcesLoader.TILES_DIR, "hide.png"), 16, 16);
        final SpriteTiled fog = Drawable.loadSpriteTiled(Media.get(ResourcesLoader.TILES_DIR, "fog.png"), 16, 16);
        hide.load(false);
        fog.load(false);
        setFogTiles(hide, fog);
        setFogOfWar(config.hide, config.fog);
    }
}
