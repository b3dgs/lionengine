package com.b3dgs.lionengine.example.c_platform.e_lionheart.menu;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Loader;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Sequence;
import com.b3dgs.lionengine.Text;
import com.b3dgs.lionengine.Timing;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.Sprite;
import com.b3dgs.lionengine.drawable.SpriteFont;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.Scene;
import com.b3dgs.lionengine.input.Keyboard;
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
    /** Push button message. */
    private static final String PUSH_BUTTON = "Push button";
    /** Error message. */
    private static final String ERROR_MESSAGE = "Unknown type: ";
    /** Font filename. */
    private static final Media FONT_SPRITE = Media.get("sprites", "font.png");
    /** Font data. */
    private static final Media FONT_DATA = Media.get("sprites", "fontdata.xml");
    /** Text color in menu option. */
    private static final Color COLOR_OPTION = new Color(170, 170, 238);
    /** Alpha step speed. */
    private static final double ALPHA_STEP = 8.0;
    /** Cached alpha values. */
    private static final Color[] ALPHAS;

    /**
     * Static init.
     */
    static
    {
        ALPHAS = new Color[256];
        for (int i = 0; i < 256; i++)
        {
            Menu.ALPHAS[i] = new Color(0, 0, 0, i);
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
    /** Wide factor. */
    private final int wideFactor;
    /** Current picture to render. */
    private final int pic;
    /** Screen mask alpha current value. */
    private double alpha;
    /** Text alpha current value. */
    private double txtAlpha;
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
    /** Current menu. */
    private TypeMenu menu;
    /** Next menu. */
    private TypeMenu menuNext;
    /** Current menu transition. */
    private TypeTransition transition;

    /**
     * Constructor.
     * 
     * @param loader The loader reference.
     */
    public Menu(Loader loader)
    {
        super(loader);
        text = new Text(Font.SERIF, 12, Text.NORMAL);
        timerPressStart = new Timing();
        wideFactor = wide ? 0 : 1;

        font = Drawable.createSpriteFont(Menu.FONT_SPRITE, Menu.FONT_DATA, 12, 12);

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
                new Choice(text, wideFactor, "Start Game", 160, 74, Align.CENTER, TypeMenu.NEW),
                new Choice(text, wideFactor, "Options", 160, 92, Align.CENTER, TypeMenu.OPTIONS),
                new Choice(text, wideFactor, "Introduction", 160, 112, Align.CENTER, TypeMenu.MAIN),
                new Choice(text, wideFactor, "Exit", 160, 140, Align.CENTER, TypeMenu.EXIT)
        };
        menusData[0] = new Data(text, wideFactor, "Main", false, choices);

        // Options menu
        choices = new Choice[]
        {
                new Choice(text, wideFactor, "Difficulty", 104, 86, Align.LEFT),
                new Choice(text, wideFactor, "Sound", 104, 102, Align.LEFT),
                new Choice(text, wideFactor, "Controls", 104, 118, Align.LEFT),
                new Choice(text, wideFactor, "Back", 160, 140, Align.CENTER, TypeMenu.MAIN)
        };
        menusData[1] = new Data(text, wideFactor, "Options", true, choices);

        // Keys menu
        choices = new Choice[]
        {
                new Choice(text, wideFactor, "Up", 44, 90, Align.LEFT),
                new Choice(text, wideFactor, "Down", 44, 106, Align.LEFT),
                new Choice(text, wideFactor, "Action", 44, 125, Align.LEFT),
                new Choice(text, wideFactor, "Left", 172, 90, Align.LEFT),
                new Choice(text, wideFactor, "Right", 172, 106, Align.LEFT),
                new Choice(text, wideFactor, "Look", 172, 125, Align.LEFT),
                new Choice(text, wideFactor, "Back", 160, 160, Align.CENTER, TypeMenu.OPTIONS)
        };
        menusData[2] = new Data(text, wideFactor, "Keys", true, choices);

        menu = TypeMenu.MAIN;
        transition = TypeTransition.IN;
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
            // Play sound select
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
        if (menu == TypeMenu.KEYS)
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
            // Play sound select
        }
        TypeMenu next = data.choices[choice].next;
        // Go to menu key
        if (menu == TypeMenu.OPTIONS)
        {
            if (choice == 2)
            {
                next = TypeMenu.KEYS;
            }
        }
        // Save keys
        if (menu == TypeMenu.KEYS)
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
            transition = TypeTransition.OUT;
            txtAlpha = 0.0;
        }
    }

    /**
     * Handle the menu new sub menu.
     */
    private void handleMenuNew()
    {
        if (alpha == 0.0)
        {
            txtAlpha += Menu.ALPHA_STEP;
            if (txtAlpha > 255.0)
            {
                txtAlpha = 255.0;
            }
            // Wait for loading
            if (!firstLoaded)
            {
                firstLoaded = true;
                timerPressStart.start();
            }
            if (txtAlpha == 255.0 && timerPressStart.elapsed(500))
            {
                timerPressStart.start();
                pressStart = !pressStart;
            }
        }

        // Entering game
        if (keyboard.used())
        {
            transition = TypeTransition.OUT;
            menuNext = TypeMenu.GAME;
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
     */
    private void handleMenu()
    {
        switch (menu)
        {
            case MAIN:
                break;
            case NEW:
                handleMenuNew();
                break;
            case GAME:
                end(new Scene(loader));
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
     */
    private void handleMenuTransition()
    {
        switch (transition)
        {
        // Fading in to new menu
            case IN:
                alpha -= Menu.ALPHA_STEP;
                if (alpha < 0.0 - Menu.ALPHA_STEP)
                {
                    alpha = 0.0;
                    transition = TypeTransition.NONE;
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
                alpha += Menu.ALPHA_STEP;
                if (alpha >= 255.0 + Menu.ALPHA_STEP)
                {
                    alpha = 255.0;
                    menu = menuNext;
                    transition = TypeTransition.IN;
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
                menus[1].render(g, 160 * wideFactor - 160, 0);
                pics[pic].render(g, 160 * wideFactor - 76, 56);
                font.setAlpha((int) txtAlpha);
                font.draw(g, 160 * wideFactor, 186, Align.CENTER, Menu.SWAMP_TEXT);
                if (pressStart)
                {
                    font.draw(g, 160 * wideFactor, 225, Align.CENTER, Menu.PUSH_BUTTON);
                }
                break;
            case GAME:
                break;
            case OPTIONS:
                text.setColor(Menu.COLOR_OPTION);
                text.draw(g, 172 * wideFactor, menusData[id].choices[0].y, Align.LEFT,
                        Menu.OPTIONS_DIFFICULTY[difficulty]);
                text.draw(g, 172 * wideFactor, menusData[id].choices[1].y, Align.LEFT, String.valueOf(volume));
                text.draw(g, 172 * wideFactor, menusData[id].choices[2].y, Align.LEFT, Menu.OPTIONS_CONTROL[control]);
                break;
            case KEYS:
                text.setColor(Menu.COLOR_OPTION);
                for (int i = 0; i < 6; i++)
                {
                    text.draw(g, menusData[id].choices[i].x + 40 * wideFactor, menusData[id].choices[i].y, Align.LEFT,
                            KeyEvent.getKeyText(Menu.KEYS[i].intValue()));
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
        if (menu == TypeMenu.MAIN)
        {
            return 0;
        }
        else if (menu == TypeMenu.OPTIONS)
        {
            return 1;
        }
        else if (menu == TypeMenu.KEYS)
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
        handleMenuTransition();
        handleMenu();
        if (isPressed(Keyboard.ESCAPE))
        {
            end();
        }
    }

    @Override
    protected void render(Graphic g)
    {
        g.clear(config.internal);
        final int id = getMenuID();
        if (id > -1)
        {
            menus[id].render(g, 160 * wideFactor - 160, 0);
            menusData[id].render(g, choice);
        }
        renderMenus(g, id);
        if (transition != TypeTransition.NONE)
        {
            final int a = UtilityMath.fixBetween((int) alpha, 0, 255);
            g.setColor(Menu.ALPHAS[a]);
            g.drawRect(0, 0, width, height, true);
        }
    }
}
