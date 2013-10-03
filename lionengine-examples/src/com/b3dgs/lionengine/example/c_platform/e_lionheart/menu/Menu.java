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
package com.b3dgs.lionengine.example.c_platform.e_lionheart.menu;

import java.awt.event.KeyEvent;

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.Timing;
import com.b3dgs.lionengine.core.ColorRgba;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.Loader;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.Sequence;
import com.b3dgs.lionengine.core.Text;
import com.b3dgs.lionengine.core.TextStyle;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.Sprite;
import com.b3dgs.lionengine.drawable.SpriteFont;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.Scene;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.Sfx;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.SonicArranger;
import com.b3dgs.lionengine.input.Keyboard;
import com.b3dgs.lionengine.utility.UtilityImage;
import com.b3dgs.lionengine.utility.UtilityMath;

/**
 * Menu implementation.
 */
public class Menu
        extends Sequence
{
    /** Default key set. */
    public static final Integer[] KEYS =
    {
            Keyboard.UP, Keyboard.DOWN, Keyboard.CONTROL, Keyboard.LEFT, Keyboard.RIGHT, Keyboard.ALT
    };
    /** List of difficulties. */
    private static final String[] OPTIONS_DIFFICULTY =
    {
        "Normal"
    };
    /** List of available controls. */
    private static final String[] OPTIONS_CONTROL =
    {
        "Press action"
    };
    /** Swamp text. */
    private static final String[] SWAMP_TEXT =
    {
            "Nothing could be done. Valdyn would have", "to make the entire journey on foot.",
            "He sighed and entered the swamps."
    };
    /** Original menu display. */
    public static final Resolution MENU_DISPLAY = new Resolution(640, 480, 60);
    /** Push button message. */
    private static final String PUSH_BUTTON = "Push button";
    /** Error message. */
    private static final String ERROR_MESSAGE = "Unknown type: ";
    /** Font filename. */
    private static final Media FONT_SPRITE = Media.get("sprites", "font_big.png");
    /** Font data. */
    private static final Media FONT_DATA = Media.get("sprites", "fontdata_big.xml");
    /** Text color in menu option. */
    private static final ColorRgba COLOR_OPTION = new ColorRgba(170, 170, 238);
    /** Alpha step speed. */
    private static final double ALPHA_STEP = 8.0;
    /** Cached alpha values. */
    private static final ColorRgba[] ALPHAS;

    /**
     * Static init.
     */
    static
    {
        ALPHAS = new ColorRgba[256];
        for (int i = 0; i < 256; i++)
        {
            Menu.ALPHAS[i] = new ColorRgba(0, 0, 0, i);
        }
    }

    /** Text for menu content. */
    private final Text text;
    /** Background menus. */
    private final Sprite[] menus;
    /** Levels picture. */
    private final Sprite[] pics;
    /** Level loading text font. */
    private final SpriteFont font;
    /** List of menu data with their content. */
    private final Data[] menusData;
    /** Press start display timer. */
    private final Timing timerPressStart;
    /** Horizontal factor. */
    private final double factorH;
    /** Vertical factor. */
    private final double factorV;
    /** Current picture to render. */
    private final int pic;
    /** Screen mask alpha current value. */
    private double alpha;
    /** Text alpha current value. */
    private double txtAlpha;
    /** Alpha changed. */
    private boolean alphaChanged;
    /** Line choice on menu. */
    private int choice;
    /** Current difficulty index. */
    private int difficulty;
    /** Current volume value. */
    private int volume;
    /** Current control type. */
    private int control;
    /** Press start flag. */
    private boolean pressStart;
    /** First loaded flag. */
    private boolean firstLoaded;
    /** Wait for key flag. */
    private boolean waitKey;
    /** Choice pressed flag. */
    private boolean choicePressed;
    /** Loaded flag. */
    private boolean loaded;
    /** Can start flag. */
    private boolean canStart;
    /** Current menu. */
    private MenuType menu;
    /** Next menu. */
    private MenuType menuNext;
    /** Current menu transition. */
    private TransitionType transition;

    /**
     * Constructor.
     * 
     * @param loader The loader reference.
     */
    public Menu(Loader loader)
    {
        super(loader, Menu.MENU_DISPLAY);
        text = UtilityImage.createText(Text.SERIF, 24, TextStyle.NORMAL);
        timerPressStart = new Timing();
        factorH = 2.0 * (width / (double) Menu.MENU_DISPLAY.getWidth());
        factorV = 2.0 * (height / (double) Menu.MENU_DISPLAY.getHeight());

        font = Drawable.loadSpriteFont(Menu.FONT_SPRITE, Menu.FONT_DATA, 24, 24);

        pics = new Sprite[1];
        for (int i = 0; i < 1; i++)
        {
            pics[i] = Drawable.loadSprite(Media.get("menu", "pic" + (i + 1) + ".png"));
        }

        menus = new Sprite[3];
        for (int i = 0; i < 3; i++)
        {
            menus[i] = Drawable.loadSprite(Media.get("menu", "menu" + (i + 1) + ".png"));
        }
        menusData = new Data[3];

        // Main menu
        Choice[] choices = new Choice[]
        {
                new Choice(text, factorH, factorV, "Start Game", 160, 74, Align.CENTER, MenuType.NEW),
                new Choice(text, factorH, factorV, "Options", 160, 92, Align.CENTER, MenuType.OPTIONS),
                new Choice(text, factorH, factorV, "Introduction", 160, 112, Align.CENTER, MenuType.MAIN),
                new Choice(text, factorH, factorV, "Exit", 160, 140, Align.CENTER, MenuType.EXIT)
        };
        menusData[0] = new Data(text, factorH, factorV, "Main", false, choices);

        // Options menu
        choices = new Choice[]
        {
                new Choice(text, factorH, factorV, "Difficulty", (int) (104 / factorH), 86, Align.LEFT),
                new Choice(text, factorH, factorV, "Sound", (int) (104 / factorH), 102, Align.LEFT),
                new Choice(text, factorH, factorV, "Controls", (int) (104 / factorH), 118, Align.LEFT),
                new Choice(text, factorH, factorV, "Back", 160, 140, Align.CENTER, MenuType.MAIN)
        };
        menusData[1] = new Data(text, factorH, factorV, "Options", true, choices);

        // Keys menu
        choices = new Choice[]
        {
                new Choice(text, factorH, factorV, "Up", (int) (44 / factorH), 90, Align.LEFT),
                new Choice(text, factorH, factorV, "Down", (int) (44 / factorH), 106, Align.LEFT),
                new Choice(text, factorH, factorV, "Action", (int) (44 / factorH), 125, Align.LEFT),
                new Choice(text, factorH, factorV, "Left", 172, 90, Align.LEFT),
                new Choice(text, factorH, factorV, "Right", 172, 106, Align.LEFT),
                new Choice(text, factorH, factorV, "Look", 172, 125, Align.LEFT),
                new Choice(text, factorH, factorV, "Back", 160, 160, Align.CENTER, MenuType.OPTIONS)
        };
        menusData[2] = new Data(text, factorH, factorV, "Keys", true, choices);

        menu = MenuType.MAIN;
        transition = TransitionType.IN;
        pic = 0;
        alpha = 255.0;
        choice = 0;
        difficulty = 0;
        control = 0;
        volume = 100;
        menuNext = null;
    }

    /**
     * Change an option.
     * 
     * @param option The option.
     * @param min The minimum value.
     * @param max The maximum value.
     * @param once Can be pressed once.
     * @return The new value.
     */
    private int changeOption(int option, int min, int max, boolean once)
    {
        int value = option;
        final boolean left = once ? keyboard.isPressedOnce(Menu.KEYS[3]) : keyboard.isPressed(Menu.KEYS[3]);
        final boolean right = once ? keyboard.isPressedOnce(Menu.KEYS[4]) : keyboard.isPressed(Menu.KEYS[4]);
        if (left)
        {
            value--;
        }
        if (right)
        {
            value++;
        }
        value = UtilityMath.fixBetween(value, min, max);
        if (value != option)
        {
            Sfx.SELECT.play();
        }
        return value;
    }

    /*
     * Sequence
     */

    /**
     * Update the navigation against the menu.
     * 
     * @param menuId The menu id.
     */
    private void updateMenuNavigation(int menuId)
    {
        final int choiceOld = choice;
        if (menu == MenuType.KEYS)
        {
            if (!waitKey && !choicePressed)
            {
                if (choice != 0 && choice != 3 && isPressed(Menu.KEYS[0]))
                {
                    choice--;
                }
                if (isPressed(Menu.KEYS[1]))
                {
                    choice++;
                    if (choice == 3)
                    {
                        choice += 3;
                    }
                }
                if ((choice == 3 || choice == 4 || choice == 5) && isPressed(Menu.KEYS[3]))
                {
                    choice -= 3;
                }
                if (choice < 3 && isPressed(Menu.KEYS[4]))
                {
                    choice += 3;
                }
            }
        }
        else
        {
            if (isPressed(Menu.KEYS[0]))
            {
                choice--;
            }
            if (isPressed(Menu.KEYS[1]))
            {
                choice++;
            }
        }
        final Data data = menusData[menuId];
        choice = UtilityMath.fixBetween(choice, 0, data.choiceMax);
        if (choiceOld != choice)
        {
            Sfx.SELECT.play();
        }
        MenuType next = data.choices[choice].next;
        // Go to menu key
        if (menu == MenuType.OPTIONS)
        {
            if (choice == 2)
            {
                next = MenuType.KEYS;
            }
        }
        // Save keys
        if (menu == MenuType.KEYS)
        {
            if (choice == 6)
            {
                // save keys
            }
        }
        // Accept choice
        if (next != null && isPressed(Menu.KEYS[2], Keyboard.SPACE, Keyboard.ENTER, Keyboard.CONTROL))
        {
            menuNext = next;
            transition = TransitionType.OUT;
            txtAlpha = 0.0;
        }
    }

    /**
     * Handle the menu new sub menu.
     * 
     * @param extrp The extrapolation value.
     */
    private void handleMenuNew(double extrp)
    {
        if (alpha == 0.0)
        {
            final double oldAlpha = txtAlpha;
            txtAlpha += Menu.ALPHA_STEP * extrp;
            if (txtAlpha > 255.0)
            {
                txtAlpha = 255.0;
            }
            alphaChanged = oldAlpha != txtAlpha;
            // Wait for loading
            if (!firstLoaded && txtAlpha == 255.0)
            {
                firstLoaded = true;
                timerPressStart.start();
                start(new Scene(loader), true);
                loaded = true;
            }
            if (txtAlpha == 255.0 && timerPressStart.elapsed(500))
            {
                timerPressStart.stop();
                timerPressStart.start();
                pressStart = !pressStart;
            }
            // Enter the game
            if (loaded && !keyboard.used())
            {
                canStart = true;
            }
            if (canStart && keyboard.used())
            {
                transition = TransitionType.OUT;
                menuNext = MenuType.GAME;
                firstLoaded = false;
            }
        }
    }

    /**
     * Handle the menu options sub menu.
     */
    private void handleMenuOptions()
    {
        if (choice == 0)
        {
            difficulty = changeOption(difficulty, 0, Menu.OPTIONS_DIFFICULTY.length - 1, true);
        }
        else if (choice == 1)
        {
            volume = changeOption(volume, 0, 100, false);
        }
        else if (choice == 2)
        {
            control = changeOption(control, 0, Menu.OPTIONS_CONTROL.length - 1, true);
        }
    }

    /**
     * Handle the menu keys chooser sub menu.
     */
    private void handleMenuKeys()
    {
        if (choice < 5)
        {
            if (!keyboard.used())
            {
                choicePressed = false;
            }
            final boolean changeKey = !choicePressed && !waitKey
                    && (isPressed(Menu.KEYS[2]) || isPressed(Keyboard.SPACE) || keyboard.isPressedOnce(Keyboard.ENTER));
            final boolean acceptKey = !choicePressed && waitKey && keyboard.used();
            if (changeKey)
            {
                waitKey = true;
                choicePressed = true;
            }
            if (acceptKey)
            {
                waitKey = false;
                choicePressed = true;
                Menu.KEYS[choice] = keyboard.getKeyCode();
            }
        }
    }

    /**
     * Handle the menu states.
     * 
     * @param extrp The extrapolation value.
     */
    private void handleMenu(double extrp)
    {
        switch (menu)
        {
            case MAIN:
                break;
            case NEW:
                handleMenuNew(extrp);
                break;
            case GAME:
                end();
                break;
            case OPTIONS:
                handleMenuOptions();
                break;
            case KEYS:
                handleMenuKeys();
                break;
            case INTRO:
                break;
            case EXIT:
                end();
                break;
            default:
                throw new LionEngineException(Menu.ERROR_MESSAGE + menu);
        }
    }

    /**
     * Handle the menu transitions.
     * 
     * @param extrp The extrapolation value.
     */
    private void handleMenuTransition(double extrp)
    {
        switch (transition)
        {
        // Fading in to new menu
            case IN:
                alpha -= Menu.ALPHA_STEP * extrp;
                if (alpha < 0.0 - Menu.ALPHA_STEP)
                {
                    alpha = 0.0;
                    transition = TransitionType.NONE;
                }
                break;
            // Ready to navigate inside the current menu
            case NONE:
                final int menuId = getMenuID();
                if (menuId > -1)
                {
                    updateMenuNavigation(menuId);
                }
                break;
            // Fading out from current menu
            case OUT:
                alpha += Menu.ALPHA_STEP * extrp;
                if (alpha >= 255.0 + Menu.ALPHA_STEP)
                {
                    alpha = 255.0;
                    menu = menuNext;
                    transition = TransitionType.IN;
                    choice = 0;
                }
                break;
            default:
                throw new LionEngineException(Menu.ERROR_MESSAGE + transition);
        }
    }

    /**
     * Render the menus.
     * 
     * @param g The graphic output.
     * @param id The menu id.
     */
    private void renderMenus(Graphic g, int id)
    {
        switch (menu)
        {
            case MAIN:
                break;
            case NEW:
                menus[1].render(g, (int) (320 * factorH / 2) - 320, (int) (110 * factorV / 2) - 110);
                pics[pic].render(g, (int) (160 * factorH) - 152, (int) (56 * factorV));
                if (alphaChanged)
                {
                    font.setAlpha((int) txtAlpha);
                }
                font.draw(g, (int) (160 * factorH), (int) (186 * factorV), Align.CENTER, Menu.SWAMP_TEXT);
                if (pressStart)
                {
                    font.draw(g, (int) (160 * factorH), (int) (225 * factorV), Align.CENTER, Menu.PUSH_BUTTON);
                }
                break;
            case GAME:
                break;
            case OPTIONS:
                text.setColor(Menu.COLOR_OPTION);
                text.draw(g, (int) (172 * factorH), menusData[id].choices[0].y, Align.LEFT,
                        Menu.OPTIONS_DIFFICULTY[difficulty]);
                text.draw(g, (int) (172 * factorH), menusData[id].choices[1].y, Align.LEFT, String.valueOf(volume));
                text.draw(g, (int) (172 * factorH), menusData[id].choices[2].y, Align.LEFT,
                        Menu.OPTIONS_CONTROL[control]);
                break;
            case KEYS:
                text.setColor(Menu.COLOR_OPTION);
                for (int i = 0; i < 6; i++)
                {
                    text.draw(g, menusData[id].choices[i].x + (int) (40 * factorH), menusData[id].choices[i].y,
                            Align.LEFT, KeyEvent.getKeyText(Menu.KEYS[i].intValue()));
                }
                break;
            case INTRO:
                break;
            case EXIT:
                end();
                break;
            default:
                throw new LionEngineException(Menu.ERROR_MESSAGE + menu);
        }
    }

    /**
     * Get the menu id.
     * 
     * @return The menu id.
     */
    private int getMenuID()
    {
        if (menu == MenuType.MAIN)
        {
            return 0;
        }
        else if (menu == MenuType.OPTIONS)
        {
            return 1;
        }
        else if (menu == MenuType.KEYS)
        {
            return 2;
        }
        else
        {
            return -1;
        }
    }

    /**
     * Check if the key is pressed once.
     * 
     * @param keys The keys to test.
     * @return <code>true</code> if pressed once, <code>false</code> else.
     */
    private boolean isPressed(Integer... keys)
    {
        for (final Integer key : keys)
        {
            if (keyboard.isPressedOnce(key))
            {
                return true;
            }
        }
        return false;
    }

    /*
     * Sequence
     */

    @Override
    protected void load()
    {
        font.load(true);
        font.setAlpha(0);
        for (int i = 0; i < 1; i++)
        {
            pics[i].load(false);
        }
        for (int i = 0; i < 3; i++)
        {
            menus[i].load(false);
        }
    }

    @Override
    protected void update(double extrp)
    {
        handleMenuTransition(extrp);
        handleMenu(extrp);
        if (isPressed(Keyboard.ESCAPE))
        {
            end();
        }
    }

    @Override
    protected void render(Graphic g)
    {
        g.clear(source);
        final int id = getMenuID();
        if (id > -1)
        {
            menus[id].render(g, (int) (320 * factorH / 2) - 320, (int) (110 * factorV / 2) - 110);
            menusData[id].render(g, choice);
        }
        renderMenus(g, id);
        if (transition != TransitionType.NONE)
        {
            final int a = UtilityMath.fixBetween((int) alpha, 0, 255);
            g.setColor(Menu.ALPHAS[a]);
            g.drawRect(0, 0, width, height, true);
        }
    }

    @Override
    protected void onTerminate(boolean hasNextSequence)
    {
        Sfx.terminate();
        SonicArranger.stop();
        if (!hasNextSequence)
        {
            SonicArranger.terminate();
        }
        System.gc();
    }
}
