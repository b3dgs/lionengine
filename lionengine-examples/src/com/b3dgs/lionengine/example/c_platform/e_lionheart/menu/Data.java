package com.b3dgs.lionengine.example.c_platform.e_lionheart.menu;

import java.awt.Color;
import java.awt.Font;

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.Text;

/**
 * Handle a group of choice, which represents the actions in a menu.
 */
final class Data
{
    /** Title text color. */
    private static final Color COLOR_TITLE = new Color(208, 208, 224);
    /** Text color. */
    private static final Color COLOR_TEXT = new Color(153, 153, 153);
    /** Text color on selection. */
    private static final Color COLOR_OVER = new Color(255, 255, 255);
    /** Text instance for the title. */
    private static final Text TEXT_TITLE = new Text(Font.SERIF, 15, Text.NORMAL);
    /** Maximum number of choices. */
    final int choiceMax;
    /** Choices list. */
    final Choice[] choices;
    /** Text reference. */
    private final Text text;
    /** Wide factor value. */
    private final int wideFactor;
    /** Title name. */
    private final String title;
    /** Show title flag. */
    private final boolean showTitle;

    /**
     * Constructor.
     * 
     * @param text The text reference.
     * @param wideFactor The wide factor value.
     * @param title The menu title.
     * @param showTitle <code>true</code> to show the title, <code>false</code> else.
     * @param choices The choices list.
     */
    Data(Text text, int wideFactor, String title, boolean showTitle, Choice... choices)
    {
        this.text = text;
        this.wideFactor = wideFactor;
        this.title = title;
        choiceMax = choices.length - 1;
        this.showTitle = showTitle;
        this.choices = choices;
    }

    /**
     * Render the menu.
     * 
     * @param g The graphic output.
     * @param choice The current choice.
     */
    void render(Graphic g, int choice)
    {
        if (showTitle)
        {
            Data.TEXT_TITLE.setColor(Data.COLOR_TITLE);
            Data.TEXT_TITLE.draw(g, 160 * wideFactor, 66, Align.CENTER, title);
        }
        for (int i = 0; i <= choiceMax; i++)
        {
            if (choice == i)
            {
                text.setColor(Data.COLOR_OVER);
            }
            else
            {
                text.setColor(Data.COLOR_TEXT);
            }
            choices[i].render(g);
        }
    }
}
