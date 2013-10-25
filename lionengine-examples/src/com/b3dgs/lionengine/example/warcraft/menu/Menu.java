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
package com.b3dgs.lionengine.example.warcraft.menu;

import java.util.Locale;

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.Text;
import com.b3dgs.lionengine.TextStyle;
import com.b3dgs.lionengine.core.Key;
import com.b3dgs.lionengine.core.Loader;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.Sequence;
import com.b3dgs.lionengine.core.UtilityImage;
import com.b3dgs.lionengine.core.UtilityMath;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.Sprite;
import com.b3dgs.lionengine.drawable.SpriteTiled;
import com.b3dgs.lionengine.example.warcraft.AppWarcraft;
import com.b3dgs.lionengine.example.warcraft.GameConfig;
import com.b3dgs.lionengine.example.warcraft.Music;
import com.b3dgs.lionengine.example.warcraft.RaceType;
import com.b3dgs.lionengine.example.warcraft.Scene;
import com.b3dgs.lionengine.example.warcraft.Sfx;
import com.b3dgs.lionengine.game.Cursor;

/**
 * Menu sequence implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class Menu
        extends Sequence
{
    /** Menu font. */
    static final Text FONT = UtilityImage.createText(Text.DIALOG, 10, TextStyle.NORMAL);
    /** Main menu element color. */
    static final ColorRgba COLOR_HEAD = new ColorRgba(255, 244, 69);
    /** Menu element color. */
    static final ColorRgba COLOR = new ColorRgba(255, 255, 235);
    /** Element on mouse over color. */
    static final ColorRgba COLOR_OVER = new ColorRgba(255, 245, 70);
    /** Box border color (About & GameSelect box). */
    static final ColorRgba COLOR_BOX_BORDER = new ColorRgba(190, 190, 190);
    /** Box inside color (About & GameSelect box). */
    static final ColorRgba COLOR_BOX_IN = new ColorRgba(20, 48, 77);
    /** Available races list. */
    static final RaceType[] RACES =
    {
            RaceType.HUMAN, RaceType.ORC
    };
    /** Available maps list. */
    static final String[] MAPS =
    {
            "forest.map", "swamp.map"
    };
    /** Available fog options. */
    static final String[] FOGS =
    {
            "Revealed", "Hidden"
    };
    /** Current menu. */
    static MenuType menu = MenuType.INTRO_UP;
    /** Clicked state. */
    static boolean clicked;

    /**
     * Get the name of the race enum.
     * 
     * @param name The race enum.
     * @return The race name.
     */
    private static String format(RaceType name)
    {
        return Menu.format(name.name(), false);
    }

    /**
     * Get the name of the race enum.
     * 
     * @param name The race enum.
     * @param hasExtension <code>true</code> if has extension (remove the extension part).
     * @return The race name.
     */
    private static String format(String name, boolean hasExtension)
    {
        String str = name.substring(0, 1).toUpperCase(Locale.ENGLISH)
                .concat(name.substring(1).toLowerCase(Locale.ENGLISH));
        if (hasExtension)
        {
            str = str.substring(0, str.length() - 4);
            str = str.replace('_', ' ');
        }
        return str;
    }

    /**
     * Get a button from its name.
     * 
     * @param filename The button filename.
     * @param width The button width.
     * @param height The button height.
     * @return The button instance.
     */
    private static SpriteTiled getButton(String filename, int width, int height)
    {
        return Drawable.loadSpriteTiled(Media.get(AppWarcraft.MENU_DIR, filename), width, height);
    }

    /** Introduction logo. */
    private final Sprite logo;
    /** Menu background. */
    private final Sprite background;
    /** Menu cursor. */
    private final Cursor cursor;
    /** Music. */
    private final Music music;
    /** Menu buttons. */
    private Button[] buttons;
    /** Menu choices buttons. */
    private Choice[] choices;
    /** Menu transition colors. */
    private ColorRgba[] alphas;
    /** Current alpha value. */
    private double alpha;
    /** Current player race. */
    private int playerRace;
    /** Current opponent race. */
    private int opponentRace;
    /** Current map selection. */
    private int map;
    /** Current fog mode. */
    private int fog;
    /** Introduction timer. */
    private long introTimer;
    /** Button pressed state. */
    private boolean pressed;
    /** Terminate flag. */
    private boolean end;

    /**
     * Constructor.
     * 
     * @param loader The loader reference.
     */
    public Menu(Loader loader)
    {
        super(loader, Scene.NATIVE);
        logo = Drawable.loadSprite(Media.get(AppWarcraft.MENU_DIR, "blizzard.png"));
        background = Drawable.loadSprite(Media.get(AppWarcraft.MENU_DIR, "menu.png"));
        cursor = new Cursor(mouse, source, Media.get("cursor.png"));
        music = Music.MENU;
    }

    /**
     * Set the alpha.
     * 
     * @param g The graphics output.
     * @param color The color.
     */
    private void applyAlpha(Graphic g, ColorRgba color)
    {
        g.setColor(color);
        g.drawRect(0, 0, width, height, true);
    }

    /**
     * Draw new menu.
     * 
     * @param g The graphics output.
     */
    private void drawNew(Graphic g)
    {
        background.render(g, 0, 0);
        g.setColor(Menu.COLOR_BOX_BORDER);
        g.drawRect(80, 84, 160, 82, false);
        g.setColor(Menu.COLOR_BOX_IN);
        g.drawRect(81, 85, 159, 81, true);
        Menu.FONT.setColor(Menu.COLOR_HEAD);
        Menu.FONT.draw(g, 160, 89, Align.CENTER, "Select game type");
        Menu.FONT.setColor(Menu.COLOR);
        Menu.FONT.draw(g, 136, 104, Align.RIGHT, "Race:");
        Menu.FONT.draw(g, 188, 104, Align.CENTER, Menu.format(Menu.RACES[playerRace]));

        Menu.FONT.draw(g, 136, 120, Align.RIGHT, "Opponent:");
        Menu.FONT.draw(g, 188, 120, Align.CENTER, Menu.format(Menu.RACES[opponentRace]));

        Menu.FONT.draw(g, 136, 136, Align.RIGHT, "Map:");
        Menu.FONT.draw(g, 188, 136, Align.CENTER, Menu.format(Menu.MAPS[map], true));

        Menu.FONT.draw(g, 136, 152, Align.RIGHT, "View:");
        Menu.FONT.draw(g, 188, 152, Align.CENTER, Menu.FOGS[fog]);

        for (int i = 4; i < 6; i++)
        {
            buttons[i].render(g);
        }
        for (int i = 0; i < 8; i++)
        {
            choices[i].render(g);
        }
    }

    /*
     * Sequence
     */

    @Override
    protected void load()
    {
        alphas = new ColorRgba[256];
        for (int i = 0; i < 256; i++)
        {
            alphas[i] = new ColorRgba(0, 0, 0, i);
        }

        logo.load(false);
        background.load(false);
        cursor.setLocation(width / 2, height / 2);
        buttons = new Button[7];

        final SpriteTiled bigButton = Menu.getButton("case1.png", 112, 16);
        bigButton.load(false);
        final SpriteTiled smallButton = Menu.getButton("case2.png", 54, 16);
        smallButton.load(false);
        final SpriteTiled arrowButton = Menu.getButton("case3.png", 15, 15);
        arrowButton.load(false);

        buttons[0] = new Button(bigButton, "Start a new game", 104, 93, MenuType.NEW);
        buttons[1] = new Button(bigButton, "Load existing game", 104, 111, MenuType.MAIN);
        buttons[2] = new Button(bigButton, "About", 104, 129, MenuType.ABOUT);
        buttons[3] = new Button(bigButton, "Quit game", 104, 163, MenuType.EXIT);

        buttons[4] = new Button(smallButton, "Ok", 84, 170, MenuType.NEW_OUT);
        buttons[5] = new Button(smallButton, "Cancel", 183, 170, MenuType.MAIN);

        buttons[6] = new Button(smallButton, "Back", 133, 170, MenuType.MAIN);

        choices = new Choice[8];
        choices[0] = new Choice(arrowButton, 142, 100, false);
        choices[1] = new Choice(arrowButton, 218, 100, true);
        choices[2] = new Choice(arrowButton, 142, 116, false);
        choices[3] = new Choice(arrowButton, 218, 116, true);
        choices[4] = new Choice(arrowButton, 142, 132, false);
        choices[5] = new Choice(arrowButton, 218, 132, true);
        choices[6] = new Choice(arrowButton, 142, 148, false);
        choices[7] = new Choice(arrowButton, 218, 148, true);

        playerRace = 0;
        opponentRace = 1;
        map = 0;
        alpha = 0;
        end = false;
        fog = 0;
        Menu.clicked = false;
        if (Menu.menu == MenuType.INTRO_UP)
        {
            Sfx.BLIZZARD.play();
        }
        if (keyboard.used())
        {
            pressed = true;
        }
        introTimer = UtilityMath.time();
        setMouseVisible(false);
    }

    @Override
    protected void update(double extrp)
    {
        cursor.update(1.0);
        if (cursor.getClick() == 0)
        {
            Menu.clicked = false;
        }
        if (!keyboard.used())
        {
            pressed = false;
        }
        switch (Menu.menu)
        {
            case INTRO_UP:
                alpha += 3 * extrp;
                alpha = UtilityMath.fixBetween(alpha, 0, 255);
                if (UtilityMath.time() - introTimer > 3000)
                {
                    Menu.menu = MenuType.INTRO_DOWN;
                }
                break;
            case INTRO_DOWN:
                alpha -= 10 * extrp;
                alpha = UtilityMath.fixBetween(alpha, 0, 255);
                if (alpha == 0)
                {
                    Menu.menu = MenuType.MAIN_UP;
                }
                break;
            case MAIN_UP:
                alpha += 10 * extrp;
                alpha = UtilityMath.fixBetween(alpha, 0, 255);
                if (alpha == 255)
                {
                    Music.play(music);
                    Menu.menu = MenuType.MAIN;
                }
                break;
            case MAIN:
                for (int i = 0; i < 4; i++)
                {
                    buttons[i].update(cursor);
                }
                break;
            case NEW:
                for (int i = 0; i < 8; i++)
                {
                    if (choices[i].update(cursor))
                    {
                        if (Math.floor(i / 2.0) == 0)
                        {
                            playerRace = UtilityMath.fixBetween(playerRace + choices[i].getSide(), 0,
                                    Menu.RACES.length - 1);
                            if (playerRace == 0)
                            {
                                opponentRace = 1;
                            }
                            else if (playerRace == 1)
                            {
                                opponentRace = 0;
                            }
                        }
                        else if (Math.floor(i / 2.0) == 1)
                        {
                            opponentRace = UtilityMath.fixBetween(opponentRace + choices[i].getSide(), 0,
                                    Menu.RACES.length - 1);
                            if (opponentRace == 0)
                            {
                                playerRace = 1;
                            }
                            else if (opponentRace == 1)
                            {
                                playerRace = 0;
                            }
                        }
                        else if (Math.floor(i / 2.0) == 2)
                        {
                            map = UtilityMath.fixBetween(map + choices[i].getSide(), 0, Menu.MAPS.length - 1);
                        }
                        else if (Math.floor(i / 2.0) == 3)
                        {
                            fog = UtilityMath.fixBetween(fog + choices[i].getSide(), 0, Menu.FOGS.length - 1);
                        }
                    }
                }
                for (int i = 4; i < 6; i++)
                {
                    buttons[i].update(cursor);
                }
                break;
            case ABOUT:
                buttons[6].update(cursor);
                break;
            case NEW_OUT:
                alpha -= 10 * extrp;
                alpha = UtilityMath.fixBetween(alpha, 0, 255);
                if (alpha == 0)
                {
                    Menu.menu = MenuType.PLAY;
                }
                break;
            case PLAY:
                boolean hide = false;
                boolean ffog = false;
                if (fog >= 1)
                {
                    hide = true;
                }
                if (fog == 2)
                {
                    ffog = true;
                }
                Menu.menu = MenuType.MAIN_UP;
                alpha = 0;
                final GameConfig config = new GameConfig(Menu.RACES[playerRace], Menu.RACES[opponentRace],
                        Menu.MAPS[map], hide, ffog);
                end(new Scene(loader, config));
                break;
            case EXIT:
                end();
                break;
            default:
                throw new RuntimeException();
        }
        if (!pressed && keyboard.isPressed(Key.ESCAPE))
        {
            end();
        }
    }

    @Override
    protected void render(Graphic g)
    {
        if (end)
        {
            return;
        }
        switch (Menu.menu)
        {
            case INTRO_UP:
            case INTRO_DOWN:
                logo.render(g, 0, 0);
                applyAlpha(g, alphas[255 - (int) alpha]);
                break;
            case MAIN_UP:
                background.render(g, 0, 0);
                applyAlpha(g, alphas[255 - (int) alpha]);
                break;
            case MAIN:
                background.render(g, 0, 0);
                for (int i = 0; i < 4; i++)
                {
                    buttons[i].render(g);
                }
                cursor.render(g);
                break;
            case NEW:
                drawNew(g);
                cursor.render(g);
                break;
            case ABOUT:
                background.render(g, 0, 0);
                g.setColor(Menu.COLOR_BOX_BORDER);
                g.drawRect(80, 84, 160, 71, false);
                g.setColor(Menu.COLOR_BOX_IN);
                g.drawRect(81, 85, 159, 70, true);
                Menu.FONT.setColor(Menu.COLOR_HEAD);
                Menu.FONT.draw(g, 160, 89, Align.CENTER, AppWarcraft.PROGRAM + " v" + AppWarcraft.VERSION);
                Menu.FONT.draw(g, 115, 110, Align.CENTER, "Author:");
                Menu.FONT.draw(g, 115, 120, Align.CENTER, "Graphics:");
                Menu.FONT.draw(g, 115, 130, Align.CENTER, "Musics:");
                Menu.FONT.draw(g, 115, 140, Align.CENTER, "Website:");
                Menu.FONT.setColor(Menu.COLOR);
                Menu.FONT.draw(g, 185, 110, Align.CENTER, "Pierre-Alexandre");
                Menu.FONT.draw(g, 185, 120, Align.CENTER, "Blizzard (c)");
                Menu.FONT.draw(g, 185, 130, Align.CENTER, "Blizzard (c)");
                Menu.FONT.draw(g, 185, 140, Align.CENTER, "www.b3dgs.com");
                buttons[6].render(g);
                cursor.render(g);
                break;
            case NEW_OUT:
                drawNew(g);
                applyAlpha(g, alphas[255 - (int) alpha]);
                break;
            case PLAY:
                applyAlpha(g, ColorRgba.BLACK);
                break;
            case EXIT:
                break;
            default:
                throw new RuntimeException();
        }
    }

    @Override
    protected void onTerminate(boolean hasNextSequence)
    {
        end = true;
        for (int i = 0; i < buttons.length; i++)
        {
            buttons[i] = null;
        }
        for (int i = 0; i < choices.length; i++)
        {
            choices[i] = null;
        }
        buttons = null;
        choices = null;
        Music.stop(music);
        Sfx.stopAll();
        if (!hasNextSequence)
        {
            Sfx.terminateAll();
        }
    }
}
