package com.b3dgs.lionengine.example.e_shmup.c_tyrian;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.example.e_shmup.c_tyrian.entity.Entity;
import com.b3dgs.lionengine.game.CameraGame;
import com.b3dgs.lionengine.game.entity.HandlerEntityGame;

/**
 * Handler entity.
 */
public final class HandlerEntity
        extends HandlerEntityGame<Entity>
{
    /** Camera reference. */
    private final CameraGame camera;
    
    /**
     * Constructor.
     * 
     * @param camera The camera reference.
     */
    public HandlerEntity(CameraGame camera)
    {
        super();
        this.camera = camera;
    }

    /*
     * HandlerEntityGame
     */
    
    @Override
    protected void render(Graphic g, Entity entity)
    {
        if (camera.isVisible(entity))
        {
            entity.render(g, camera);
        }
    }
}
