package com.b3dgs.lionengine.example.d_rts.d_ability;

import java.awt.Color;
import java.util.Set;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.drawable.Bar;
import com.b3dgs.lionengine.example.d_rts.d_ability.entity.Entity;
import com.b3dgs.lionengine.game.TextGame;
import com.b3dgs.lionengine.game.rts.CameraRts;
import com.b3dgs.lionengine.game.rts.CursorRts;
import com.b3dgs.lionengine.game.rts.HandlerEntityRts;

/**
 * Handler implementation, containing all entities.
 */
public final class HandlerEntity
        extends HandlerEntityRts<ResourceType, Tile, Entity, ControlPanel>
{
    /** Entity progress bar. */
    private final Bar barProgress;

    /**
     * Constructor.
     * 
     * @param camera The camera viewpoint.
     * @param cursor The cursor reference (used for selection).
     * @param controlPanel The panel reference.
     * @param map The map reference.
     * @param text The text reference.
     */
    HandlerEntity(CameraRts camera, CursorRts cursor, ControlPanel controlPanel, Map map, TextGame text)
    {
        super(camera, cursor, controlPanel, map);
        barProgress = new Bar(0, 0);
        barProgress.setBorderSize(1, 1);
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
        }
        if (entity.isSelected())
        {
            final int x = camera.getViewpointX(entity.getLocationIntX());
            final int y = camera.getViewpointY(entity.getLocationIntY() + 4 + entity.getHeight());

            barProgress.setLocation(x, y);
            barProgress.setMaximumSize(entity.getWidth(), 4);
            barProgress.setWidthPercent(entity.life.getPercent());
            barProgress.setColorBackground(Color.GRAY);
            barProgress.setColorForeground(Color.RED);
            barProgress.render(g);
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
