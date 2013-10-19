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
package com.b3dgs.lionengine.example.tyrian.menu;

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.audio.AudioMidi;
import com.b3dgs.lionengine.audio.Midi;
import com.b3dgs.lionengine.core.Click;
import com.b3dgs.lionengine.core.Key;
import com.b3dgs.lionengine.core.Loader;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.Sequence;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.Sprite;
import com.b3dgs.lionengine.drawable.SpriteFont;
import com.b3dgs.lionengine.drawable.SpriteTiled;
import com.b3dgs.lionengine.example.tyrian.AppTyrian;
import com.b3dgs.lionengine.example.tyrian.Scene;
import com.b3dgs.lionengine.example.tyrian.Sfx;

/**
 * The menu implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class Menu
        extends Sequence
{
    /** Colors alpha. */
    private final ColorRgba colors[];
    /** Color green alpha. */
    private final ColorRgba colorGreen[];
    /** Buttons surface. */
    private final SpriteTiled buttonsSurfaces;
    /** Pictures. */
    private final Sprite surfaces[];
    /** Cursor. */
    private final Sprite cursor;
    /** Armory. */
    private final Sprite armory;
    /** Ship. */
    private final SpriteTiled ship;
    /** Font 1. */
    private final SpriteFont font;
    /** Font 2. */
    private final SpriteFont font2;
    /** Music. */
    private final Midi midi;
    /** Buttons. */
    private final Button[] buttons;
    /** Current alpha value. */
    int alpha;
    /** Current menu index. */
    int menu;
    /** Alpha value for remake title. */
    private int alphaRemake;
    /** Title vertical location. */
    private int titleY;
    /** Mouse x. */
    private int mx;
    /** Mouse y. */
    private int my;
    /** Mouse used flag. */
    private boolean used;

    /**
     * Constructor.
     * 
     * @param loader The loader reference.
     */
    public Menu(Loader loader)
    {
        super(loader, new Resolution(320, 200, 60));
        colors = new ColorRgba[256];
        colorGreen = new ColorRgba[256];
        for (int i = 0; i < 256; i++)
        {
            colors[i] = new ColorRgba(255, 255, 255, i);
            colorGreen[i] = new ColorRgba(0, 255, 0, i);
        }

        buttonsSurfaces = Drawable.loadSpriteTiled(Media.get("fonts", "buttons.png"), 96, 16);
        font = Drawable.loadSpriteFont(Media.get("fonts", "font.png"), Media.get("fonts", "font.xml"), 8, 9);
        font2 = Drawable.loadSpriteFont(Media.get("fonts", "font2.png"), Media.get("fonts", "font2.xml"), 6, 6);

        surfaces = new Sprite[5];
        surfaces[0] = Drawable.loadSprite(Media.get("pics", "background.png"));
        surfaces[1] = Drawable.loadSprite(Media.get("fonts", "tyrian.png"));
        surfaces[2] = Drawable.loadSprite(Media.get("fonts", "remake.png"));
        surfaces[3] = Drawable.loadSprite(Media.get("pics", "start.png"));
        surfaces[4] = Drawable.loadSprite(Media.get("pics", "howto.png"));

        cursor = Drawable.loadSprite(Media.get("sprites", "cursor.png"));
        armory = Drawable.loadSprite(Media.get("sprites", "armory.png"));
        ship = Drawable.loadSpriteTiled(Media.get("ships", "usp_fang.png"), 24, 28);

        midi = AudioMidi.loadMidi(Media.get("musics", "menu.mid"));
        buttons = new Button[3];
    }

    @Override
    protected void load()
    {
        buttonsSurfaces.load(true);
        buttonsSurfaces.setAlpha(0);

        font.load(true);
        font2.load(true);
        cursor.load(false);
        armory.load(true);
        ship.load(true);

        font.setAlpha(0);
        font2.setAlpha(0);

        for (final Sprite surface : surfaces)
        {
            surface.load(true);
        }

        buttons[0] = new Button(buttonsSurfaces, 0, 112, 130)
        {
            @Override
            public void onClick()
            {
                super.onClick();
                alpha = 255;
                menu = 4;
            }
        };
        buttons[1] = new Button(buttonsSurfaces, 2, 112, 145)
        {
            @Override
            public void onClick()
            {
                super.onClick();
                alpha = 255;
                menu = 5;
            }
        };
        buttons[2] = new Button(buttonsSurfaces, 4, 112, 160)
        {
            @Override
            public void onClick()
            {
                super.onClick();
                alpha = 255;
                menu = 3;
            }
        };

        alpha = 0;
        menu = 0;
        titleY = 72;
        setMouseVisible(false);
        midi.play(true);
        used = true;
    }

    @Override
    protected void update(double extrp)
    {
        mx = mouse.getOnWindowX();
        my = mouse.getOnWindowY();
        if (keyboard.isPressed(Key.ESCAPE))
        {
            end();
        }
        switch (menu)
        {
            case 0: // Incoming
                alpha += 5;
                if (alpha > 255)
                {
                    alpha = 255;
                }
                titleY -= 1;
                if (titleY < 0)
                {
                    titleY = 0;
                }
                alphaRemake += 3;
                if (alphaRemake > 200)
                {
                    alphaRemake = 200;
                }
                if (alphaRemake == 200 && titleY == 0)
                {
                    menu = 1;
                }
                surfaces[0].setAlpha(alpha);
                surfaces[1].setAlpha(alpha);
                surfaces[2].setAlpha(alphaRemake);
                break;
            case 1: // Buttons incoming
                alpha -= 6;
                if (alpha < 0)
                {
                    alpha = 0;
                    menu = 2;
                }
                buttonsSurfaces.setAlpha(255 - alpha);
                font2.setAlpha(255 - alpha);
                break;
            case 2: // Main buttons handling
                for (final Button button : buttons)
                {
                    button.update(extrp, mouse, Click.LEFT);
                }
                break;
            case 3: // Exit
                alpha -= 8;
                if (alpha < 0)
                {
                    alpha = 0;
                    this.end();
                }
                surfaces[0].setAlpha(alpha);
                surfaces[1].setAlpha(alpha);
                surfaces[2].setAlpha(alpha);
                buttonsSurfaces.setAlpha(alpha);
                font2.setAlpha(alpha);
                break;
            case 4: // Start
                alpha -= 10;
                if (alpha < 0)
                {
                    alpha = 0;
                    this.end(new Scene(loader));
                }
                break;
            case 5: // Fade out menu -> how to play
                alpha -= 15;
                if (alpha < 0)
                {
                    alpha = 0;
                    menu = 6;
                    surfaces[4].setAlpha(0);
                }
                surfaces[0].setAlpha(alpha);
                surfaces[1].setAlpha(alpha);
                surfaces[2].setAlpha(alpha);
                buttonsSurfaces.setAlpha(alpha);
                font2.setAlpha(alpha);
                armory.setAlpha(0);
                ship.setAlpha(0);
                break;
            case 6: // Fade in How to play
                alpha += 15;
                if (alpha > 255)
                {
                    alpha = 255;
                    used = true;
                    menu = 7;
                }
                surfaces[4].setAlpha(alpha);
                font.setAlpha(alpha);
                armory.setAlpha(alpha);
                ship.setAlpha(alpha);
                break;
            case 7: // How to play
                if (!used && (keyboard.used() || mouse.getMouseClick() > 0))
                {
                    menu = 8;
                    used = true;
                }
                break;
            case 8: // How to play fade out -> menu
                alpha -= 15;
                if (alpha < 0)
                {
                    alpha = 0;
                    menu = 0;
                    titleY = 72;
                    alphaRemake = 0;
                }
                surfaces[4].setAlpha(alpha);
                font.setAlpha(alpha);
                armory.setAlpha(alpha);
                ship.setAlpha(alpha);
                break;
            default:
                break;
        }
        if (mouse.getMouseClick() == 0 && !keyboard.used())
        {
            used = false;
        }
    }

    @Override
    protected void render(Graphic g)
    {
        g.clear(source);
        if (menu < 7)
        {
            surfaces[0].render(g, 0, 0);
            surfaces[1].render(g, 0, titleY);
            surfaces[2].render(g, 150, 90 - titleY / 4);
            font2.draw(g, 2, 180, Align.LEFT, "TYRIAN REMAKE", "BYRON 3D GAMES STUDIO", "V" + AppTyrian.VERSION);
        }
        if (menu >= 6)
        {
            surfaces[4].render(g, 0, 0);
            font.draw(g, 160, 2, Align.CENTER, "How to Play ?");
            font.draw(g, 4, 24, Align.LEFT, "Goal: You have to protect the earth", "by destroying all meteors before",
                    "they reach the bottom of the screen.");

            font.draw(g, 4, 60, Align.LEFT, "Meteor are comming during a wave.",
                    "Each wave duration is about one minute.", "At the end, you can buy/upgrade weapons",
                    "by putting your ship under the big one.");

            font.draw(g, 125, 120, Align.LEFT, "-Mouse/arrowkeys to move", "-Click/space to shoot",
                    "-Escape to exit game");
            font.draw(g, 265, 170, Align.CENTER, "Press any key", "or", "Do any click");

            armory.render(g, 24, 98);
            ship.render(g, 2, 140, 170);
            g.setColor(colorGreen[alpha]);
            g.drawLine(70, 162, 140, 180);
        }

        if (menu > 0 && menu < 7)
        {
            for (final Button button : buttons)
            {
                button.render(g);
            }
        }
        if (menu > 1)
        {
            cursor.render(g, mx, my);
        }
        if (menu == 4)
        {
            g.setColor(colors[255 - alpha]);
            g.drawRect(0, 0, width, height, true);
        }
    }

    @Override
    protected void onTerminate(boolean hasNextSequence)
    {
        midi.stop();
        Sfx.stopAll();
        if (!hasNextSequence)
        {
            Sfx.terminateAll();
        }
    }
}
