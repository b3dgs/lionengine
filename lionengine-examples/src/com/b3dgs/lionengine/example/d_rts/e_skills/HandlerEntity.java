package com.b3dgs.lionengine.example.d_rts.e_skills;

import java.awt.Color;
import java.util.Set;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.example.d_rts.e_skills.entity.Entity;
import com.b3dgs.lionengine.game.TextGame;
import com.b3dgs.lionengine.game.rts.CameraRts;
import com.b3dgs.lionengine.game.rts.CursorRts;
import com.b3dgs.lionengine.game.rts.HandlerEntityRts;

/**
 * Handler implementation, containing all entities.
 */
public final class HandlerEntity
        extends HandlerEntityRts<TypeResource, Tile, Entity, ControlPanel>
{
    /** Cursor reference. */
    private final Cursor cursor;

    /**
     * Constructor.
     * 
     * @param cursor The cursor reference.
     * @param controlPanel The panel reference.
     * @param map The map reference.
     * @param text The text reference.
     */
    HandlerEntity(Cursor cursor, ControlPanel controlPanel, Map map, TextGame text)
    {
        super(controlPanel, map);
        this.cursor = cursor;
    }

    /*
     * HandlerEntityRts
     */

    @Override
    public void update(double extrp, CameraRts camera, CursorRts cursor)
    {
        // Keep standard update
        if (this.cursor.getType() == TypeCursor.WEN)
        {
            this.cursor.setType(TypeCursor.POINTER);
        }
        super.update(extrp, camera, cursor);
    }

    @Override
    protected void updatingEntity(Entity entity, CursorRts cursor, CameraRts camera)
    {
        // Adapt cursor
        if (cursor.getClick() == 0 && this.cursor.getType() == TypeCursor.POINTER && entity.isOver()
                && cursor.isOver(entity, camera))
        {
            this.cursor.setType(TypeCursor.WEN);
        }
    }

    @Override
    protected void renderingEntity(Graphic g, Entity entity, CameraRts camera, CursorRts cursor)
    {
        if (cursor.getClick() == 0 && entity.isOver() && !panel.canClick(cursor) || entity.isSelected())
        {
            super.renderingEntity(g, entity, camera, cursor);
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
