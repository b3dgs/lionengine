package com.b3dgs.lionengine.example.tilecollision;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.game.CollisionData;
import com.b3dgs.lionengine.game.entity.SetupEntityGame;
import com.b3dgs.lionengine.game.platform.CameraPlatform;
import com.b3dgs.lionengine.game.platform.EntityPlatform;
import com.b3dgs.lionengine.input.Mouse;

/**
 * Entity implementation.
 */
final class Entity
        extends EntityPlatform
{
    /** Map reference. */
    private final Map map;
    /** Mouse click x. */
    private int mouseX;
    /** Mouse click y. */
    private int mouseY;
    /** Last tile collision. */
    private Tile tile;

    /**
     * Constructor.
     * 
     * @param map The map reference.
     */
    Entity(Map map)
    {
        super(new SetupEntityGame(Media.get("entities", "mario.xml")));
        this.map = map;
        mouseX = 64;
        mouseY = 180;
        setCollision(new CollisionData(0, 0, 16, 16, false));
    }

    /**
     * Update the mouse.
     * 
     * @param mouse The mouse.
     */
    public void updateMouse(Mouse mouse)
    {
        if (mouse.hasClicked(Mouse.LEFT))
        {
            mouseX = mouse.getOnWindowX();
            mouseY = mouse.getOnWindowY();
        }
    }

    /*
     * EntityPlatform
     */

    @Override
    public void render(Graphic g, CameraPlatform camera)
    {
        super.render(g, camera);
        renderCollision(g, camera);
        if (tile != null)
        {
            tile.renderCollision(g, camera);
        }
    }

    @Override
    protected void handleActions(double extrp)
    {
        setLocation(192, 112);
    }

    @Override
    protected void handleMovements(double extrp)
    {
        moveLocation(extrp, mouseX - 200, -mouseY + 128);
    }

    @Override
    protected void handleCollisions(double extrp)
    {
        updateCollision();
        setCollisionOffset(0, 0);
        tile = map.getFirstTileHit(this, TileCollision.COLLISION);
    }

    @Override
    protected void handleAnimations(double extrp)
    {
        // Nothing to do
    }
}
