package com.b3dgs.lionengine.example.c_platform.e_lionheart;

import java.awt.Font;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.Loader;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Sequence;
import com.b3dgs.lionengine.Text;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.menu.Menu;
import com.b3dgs.lionengine.input.Keyboard;

/**
 * Represents the scene where the player can control his hero over the map, fighting enemies.
 */
public final class Scene
        extends Sequence
{
    /** Text reference. */
    private final Text text;
    /** World reference. */
    private final World world;
    /** Last level index played. */
    private int lastLevelIndex;

    /**
     * Standard constructor.
     * 
     * @param loader The loader reference.
     */
    public Scene(Loader loader)
    {
        super(loader);
        text = new Text(Font.SANS_SERIF, 8, Text.NORMAL);
        world = new World(this);
        lastLevelIndex = -1;
    }

    /**
     * Load a level.
     * 
     * @param level The level to load.
     */
    private void loadLevel(TypeLevel level)
    {
        world.loadFromFile(Media.get(AppLionheart.LEVELS_DIR, level.getFilename()));
    }

    /**
     * Get the next level.
     * 
     * @return The next level.
     */
    private TypeLevel getNextLevel()
    {
        lastLevelIndex++;
        return TypeLevel.values()[lastLevelIndex % TypeLevel.LEVELS_NUMBER];
    }

    /*
     * Sequence
     */

    @Override
    protected void load()
    {
        loadLevel(getNextLevel());
    }

    @Override
    protected void update(double extrp)
    {
        world.update(extrp);
        if (keyboard.isPressedOnce(Keyboard.ESCAPE) || world.isGameOver())
        {
            end(new Menu(loader));
        }
    }

    @Override
    protected void render(Graphic g)
    {
        world.render(g);
        text.draw(g, 0, 0, String.valueOf(getFps()));
    }
}
