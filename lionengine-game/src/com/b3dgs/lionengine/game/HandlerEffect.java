package com.b3dgs.lionengine.game;

import java.util.HashMap;
import java.util.Map;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.drawable.SpriteAnimated;

/**
 * Handle effects.
 */
public class HandlerEffect
        extends HandlerGame<SpriteAnimated, SpriteAnimated>
{
    /** List of entities handled. */
    protected final Map<SpriteAnimated, Coord> locations;

    /**
     * Create a new handler.
     */
    public HandlerEffect()
    {
        locations = new HashMap<>(8);
    }

    /**
     * Main routine, which has to be called in main game loop.
     * 
     * @param extrp The extrapolation value.
     */
    public void update(double extrp)
    {
        // Add entities
        updateAdd();

        // Check animations
        for (final SpriteAnimated animation : list())
        {
            animation.updateAnimation(extrp);
        }

        // Delete
        updateRemove();
    }

    /**
     * Render the effects.
     * 
     * @param g The graphics output.
     * @param camera The camera reference.
     */
    public void render(Graphic g, CameraGame camera)
    {
        for (final SpriteAnimated animation : list())
        {
            final Coord coord = locations.get(animation);
            final int x = camera.getViewpointX((int) coord.getX());
            final int y = camera.getViewpointY((int) coord.getY() + animation.getFrameHeight());
            animation.render(g, x, y);
        }
    }

    /**
     * Add a renderable to the handler list. Don't forget to call {@link #updateAdd()} at the begin of the update to add
     * them properly.
     * 
     * @param animation The renderable to add.
     * @param x The horizontal location.
     * @param y The horizontal location.
     */
    public void add(int x, int y, SpriteAnimated animation)
    {
        super.add(animation);
        locations.put(animation, new Coord(x, y));
    }

    /**
     * Add a renderable to the remove list. Don't forget to call {@link #updateRemove()} at the end of the update to
     * clear them properly.
     * 
     * @param animation The renderable to remove.
     */
    @Override
    public void remove(SpriteAnimated animation)
    {
        super.remove(animation);
        locations.remove(animation);
    }

    @Override
    protected SpriteAnimated getKey(SpriteAnimated object)
    {
        return object;
    }
}
