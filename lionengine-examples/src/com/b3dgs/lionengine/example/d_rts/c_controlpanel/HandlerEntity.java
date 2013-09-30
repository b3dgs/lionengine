package com.b3dgs.lionengine.example.d_rts.c_controlpanel;

import java.awt.Color;
import java.util.Set;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.game.TextGame;
import com.b3dgs.lionengine.game.rts.CameraRts;
import com.b3dgs.lionengine.game.rts.CursorRts;
import com.b3dgs.lionengine.game.rts.HandlerEntityRts;

/**
 * Handler implementation, containing all entities.
 */
final class HandlerEntity
        extends HandlerEntityRts<ResourceType, Tile, Entity, ControlPanel>
{
    /** Text reference. */
    private final TextGame text;

    /**
     * Constructor.
     * 
     * @param camera The camera viewpoint.
     * @param cursor The cursor reference (used for selection).
     * @param controlPanel The panel reference.
     * @param map The map reference.
     * @param text The game text reference.
     */
    HandlerEntity(CameraRts camera, CursorRts cursor, ControlPanel controlPanel, Map map, TextGame text)
    {
        super(camera, cursor, controlPanel, map);
        this.text = text;
    }

    /*
     * HandlerEntityRts
     */

    @Override
    protected void updatingEntity(Entity entity, CursorRts cursor, CameraRts camera)
    {
        // Nothing to do
    }

    @Override
    protected void renderingEntity(Graphic g, Entity entity, CameraRts camera, CursorRts cursor)
    {
        if (cursor.getClick() == 0 && entity.isOver() || entity.isSelected())
        {
            super.renderingEntity(g, entity, camera, cursor);
            text.draw(g, entity.getLocationIntX() + 18, entity.getLocationIntY() + 16, entity.getClass()
                    .getSimpleName());
        }
        if (entity.isSelected())
        {
            text.draw(g, entity.getLocationIntX() + 18, entity.getLocationIntY() + 8, "Life: " + entity.getLife());
        }
    }

    @Override
    protected void notifyUpdatedSelection(Set<Entity> selection)
    {
        // Nothing to do
    }

    @Override
    protected Color getEntityColorOver(Entity entity)
    {
        return Color.GRAY;
    }

    @Override
    protected Color getEntityColorSelection(Entity entity)
    {
        return Color.GREEN;
    }
}
