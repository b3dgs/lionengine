package com.b3dgs.lionengine.example.tilecollision;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.Loader;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Sequence;
import com.b3dgs.lionengine.game.platform.CameraPlatform;
import com.b3dgs.lionengine.input.Keyboard;

/**
 * This is where the game loop is running.
 */
class Scene
        extends Sequence
{
    /** Map. */
    private final Map map;
    /** Entity. */
    private final Entity entityRef;
    /** Entity. */
    private final Entity entity;
    /** Camera. */
    private final CameraPlatform camera;

    /**
     * Create the scene and its vars.
     * 
     * @param loader The loader reference.
     */
    Scene(Loader loader)
    {
        super(loader);
        map = new Map();
        entityRef = new Entity(map);
        entity = new Entity(map);
        camera = new CameraPlatform(width, height);
    }

    @Override
    protected void load()
    {
        map.create(20, 15);
        map.loadPatterns(Media.get("tiles", "mario"));
        map.createBlock(5, 7);
        map.createBlock(5, 8);
        map.createBlock(5, 9);
        map.createBlock(6, 7);
        map.createBlock(7, 7);
        map.createBlock(6, 8);

        entityRef.setLocation(192, 112);
        camera.setLimits(map);
        camera.setView(0, 0, width, height);
    }

    @Override
    protected void update(double extrp)
    {
        entity.updateMouse(mouse);
        entity.update(extrp);
        // Terminate
        if (keyboard.isPressed(Keyboard.ESCAPE))
        {
            end();
        }
    }

    @Override
    protected void render(Graphic g)
    {
        clearScreen(g);
        map.render(g, camera);
        entity.render(g, camera);
        entityRef.render(g, camera);
    }
}
