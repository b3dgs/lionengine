/*
 * Copyright (C) 2013 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.b3dgs.lionengine.game.rts;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.game.CoordTile;
import com.b3dgs.lionengine.game.Tiled;
import com.b3dgs.lionengine.game.entity.EntityNotFoundException;
import com.b3dgs.lionengine.game.entity.HandlerEntityGame;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.rts.ability.attacker.AttackerServices;
import com.b3dgs.lionengine.game.rts.ability.extractor.Extractible;
import com.b3dgs.lionengine.game.rts.ability.extractor.ExtractorServices;
import com.b3dgs.lionengine.game.rts.ability.mover.MoverServices;
import com.b3dgs.lionengine.game.rts.ability.skilled.SkilledServices;
import com.b3dgs.lionengine.game.rts.map.MapTileRts;
import com.b3dgs.lionengine.game.rts.map.TileRts;
import com.b3dgs.lionengine.game.rts.skill.SkillRts;
import com.b3dgs.lionengine.input.Mouse;

/**
 * This class will handle a list of entities, by updating and rendering them. All actions are defined here, such as
 * selection and orders. Don't forget to call {@link #createLayers(MapTile)} after map creation (when its size is
 * established).
 * 
 * @param <R> Resource enumeration type.
 * @param <T> Tile type used.
 * @param <E> Entity type used.
 * @param <C> Control panel type.
 */
public abstract class HandlerEntityRts<R extends Enum<R>, T extends TileRts<?, R>, E extends EntityRts, C extends ControlPanelModel<E>>
        extends HandlerEntityGame<E>
        implements ControlPanelListener
{
    /** Maximum number of layers. */
    static final int LAYERS = 4;

    /** Control panel reference. */
    protected final C panel;
    /** Camera reference. */
    private final CameraRts camera;
    /** Cursor reference. */
    private final CursorRts cursor;
    /** Entity listener. */
    private final List<EntityRtsListener<E>> listeners;
    /** Current entity selection set. */
    private final Set<E> selectedEntity;
    /** Map reference. */
    private final MapTileRts<?, T> map;
    /** Entity area buffer for selection check. */
    private final Rectangle2D entityArea;
    /** List of entities id that shared the same path. */
    private final Set<Integer> sharedPathIds;
    /** Player (main owner) reference. */
    protected int playerId;
    /** List of rendering layers. */
    private List<List<List<E>>> layers;
    /** Mouse click assignment. */
    private int mouseClickAssign;
    /** Clicked flag. */
    private boolean clicked;

    /**
     * Create a new entity handler. Don't forget to call {@link #createLayers(MapTile)} after map creation (when its
     * size is established).
     * 
     * @param camera The camera viewpoint.
     * @param cursor The cursor reference (used for selection).
     * @param panel The control panel reference.
     * @param map The map reference.
     */
    public HandlerEntityRts(CameraRts camera, CursorRts cursor, C panel, MapTileRts<?, T> map)
    {
        super();
        this.camera = camera;
        this.cursor = cursor;
        this.panel = panel;
        this.map = map;
        listeners = new ArrayList<>(1);
        selectedEntity = new HashSet<>(8);
        entityArea = new Rectangle2D.Double();
        mouseClickAssign = Mouse.RIGHT;
        sharedPathIds = new HashSet<>(0);
        layers = null;
        panel.addListener(this);
    }

    /**
     * Update the entity.
     * 
     * @param entity The entity reference.
     * @param cursor The cursor reference.
     * @param camera The camera reference.
     */
    protected abstract void updatingEntity(E entity, CursorRts cursor, CameraRts camera);

    /**
     * Get the corresponding color when the mouse is over this entity.
     * 
     * @param entity The current entity.
     * @return color The representing mouse over an entity.
     */
    protected abstract Color getEntityColorOver(E entity);

    /**
     * Get the corresponding color when the entity is selected.
     * 
     * @param entity The current entity.
     * @return color The representing selected entity.
     */
    protected abstract Color getEntityColorSelection(E entity);

    /**
     * Check the last selected entities. Selection filter can be done here. To apply the filter correctly, it is
     * recommended to unselect the entity with {@link EntityRts#setSelection(boolean)} and remove the entity from the
     * selection which is given as a parameter. Example: Ignore a certain type of entity.
     * 
     * @param selection The selected entities.
     */
    protected abstract void notifyUpdatedSelection(Set<E> selection);

    /**
     * Add an entity listener.
     * 
     * @param listener Entity listener.
     */
    public void addListener(EntityRtsListener<E> listener)
    {
        listeners.add(listener);
    }

    /**
     * Prepare rendering layers, depending of the map height. When an entity moved and its vertical location changed,
     * its layer is automatically adjusted.
     * 
     * @param map The map reference.
     */
    public void createLayers(MapTile<?, T> map)
    {
        layers = new ArrayList<>(HandlerEntityRts.LAYERS);
        for (int i = 0; i < HandlerEntityRts.LAYERS; i++)
        {
            final List<List<E>> layer = new ArrayList<>(map.getWidthInTile());
            layers.add(layer);
            for (int j = 0; j <= map.getHeightInTile(); j++)
            {
                layer.add(new LinkedList<E>());
            }
        }
    }

    /**
     * Set the mouse click assignment ({@link Mouse#LEFT}, {@link Mouse#RIGHT}, {@link Mouse#MIDDLE}).
     * 
     * @param click The mouse click value.
     */
    public void setClickAssignment(int click)
    {
        mouseClickAssign = click;
    }

    /**
     * Set player id (player id owning this panel).
     * 
     * @param playerId The player id.
     */
    public void setPlayerId(int playerId)
    {
        this.playerId = playerId;
    }

    /**
     * Get the entity at the specified location in tile.
     * 
     * @param location The location.
     * @return The entity found at this location.
     * @throws EntityNotFoundException Thrown if no entity is found at this location.
     */
    public E getEntityAt(Tiled location) throws EntityNotFoundException
    {
        return getEntityAt(location.getLocationInTileX(), location.getLocationInTileY());
    }

    /**
     * Get the entity at the specified location in tile.
     * 
     * @param tx The horizontal tile.
     * @param ty The vertical tile.
     * @return The entity found at this location.
     * @throws EntityNotFoundException Thrown if no entity is found at this location.
     */
    public E getEntityAt(int tx, int ty) throws EntityNotFoundException
    {
        final Integer id = map.getRef(tx, ty);
        if (id.intValue() > 0)
        {
            return get(id);
        }
        final StringBuilder message = new StringBuilder("Entity not found at ");
        message.append("(").append(tx).append("|").append(ty).append(")");
        throw new EntityNotFoundException(message.toString());
    }

    /**
     * Get the closest entity from its type.
     * 
     * @param from The entity source (from which entity to search).
     * @param type The entity class type to find (used as filter).
     * @param samePlayer <code>true</code> if returned reference has to be owned by the same player as the entity from,
     *            <code>false</code> else.
     * @return The closest entity found.
     * @throws EntityNotFoundException If no entity was found.
     */
    public E getClosestEntity(E from, Class<?> type, boolean samePlayer) throws EntityNotFoundException
    {
        int dist = Integer.MAX_VALUE;
        E closest = null;
        for (final E entity : list())
        {
            if ((samePlayer && entity.getPlayerId() == from.getPlayerId() || !samePlayer) && type.isInstance(entity))
            {
                final int cur = from.getDistanceInTile(entity, false);
                if (cur < dist)
                {
                    dist = cur;
                    closest = entity;
                }
            }
        }
        if (closest == null)
        {
            throw new EntityNotFoundException();
        }
        return closest;
    }

    /**
     * Get list of selected entries during cursor selection.
     * 
     * @return The list of selected entity.
     */
    public Set<E> getSelection()
    {
        return selectedEntity;
    }

    /**
     * Update the entity click assignment.
     * 
     * @param entity The entity reference.
     * @param cursor The cursor reference.
     * @param camera The camera reference.
     * @param i The entity number in the local group.
     */
    protected void updatingEntityClickAssignment(E entity, CursorRts cursor, CameraRts camera, int i)
    {
        // Make the unit move on click if already selected
        // Allow only single assination on click
        if (entity.getPlayerId() == playerId && camera.isInside(cursor) && entity.isSelected())
        {
            final int tx = cursor.getLocationInTileX();
            final int ty = cursor.getLocationInTileY();
            final CoordTile coord = getPositioning(tx, ty, i);
            final int dtx = coord.getX();
            final int dty = coord.getY();

            // Attacker case
            if (entity instanceof AttackerServices)
            {
                updateClickAttacker(entity, dtx, dty);
            }
            // Extractor case
            else if (entity instanceof ExtractorServices)
            {
                updateClickExtractor(entity, dtx, dty);
            }
            // Mover case
            else
            {
                updateClickMover(entity, dtx, dty);
            }

            // Prepare for shared path
            sharedPathIds.add(entity.getId());
        }
    }

    /**
     * Update the selection list.
     * 
     * @param selection The selection area.
     */
    protected void updateSelection(Rectangle2D selection)
    {
        // Reset selection
        selectedEntity.clear();

        // Allows both selection and single click without moving the mouse
        selection.setRect(selection.getX(), selection.getY(), Math.max(1, selection.getWidth()),
                Math.max(1, selection.getHeight()));

        // Update the selection list
        for (final E entity : list())
        {
            if (!entity.isActive() || !entity.isSelectable())
            {
                continue;
            }
            entityArea.setRect(entity.getLocationIntX(), entity.getLocationIntY(), entity.getWidth(),
                    entity.getHeight());

            if (selection.contains(entityArea) || selection.intersects(entityArea))
            {
                entity.setSelection(true);
                selectedEntity.add(entity);
            }
            else
            {
                entity.setSelection(false);
            }
        }

        // Notify the selection update
        notifyUpdatedSelection(selectedEntity);
        panel.notifyUpdatedSelection(selectedEntity);
    }

    /**
     * Rendering function for an entity. Here are handled the entity rendering, such as sprite, border selection...
     * 
     * @param g The graphic output.
     * @param entity The current entity.
     * @param camera The camera reference.
     * @param cursor The cursor reference.
     */
    protected void renderingEntity(Graphic g, E entity, CameraRts camera, CursorRts cursor)
    {
        if (entity.isActive())
        {
            renderEntityOver(g, entity, camera, getEntityColorOver(entity));
            renderEntitySelection(g, entity, camera, getEntityColorSelection(entity));
        }

        if (entity instanceof SkilledServices)
        {
            final SkilledServices<?, ?> skilled = (SkilledServices<?, ?>) entity;
            final Collection<?> skills = skilled.getSkills();
            for (final Object skill : skills)
            {
                if (skill instanceof SkillRts)
                {
                    final SkillRts<?> skillRts = (SkillRts<?>) skill;
                    if (skillRts.isIgnored() || !skillRts.isActive())
                    {
                        continue;
                    }
                    skillRts.renderOnMap(g, cursor, camera);
                }
            }
        }
    }

    /**
     * Rendering function when cursor is over an entity. If color is null, nothing will be displayed.
     * 
     * @param g The graphic output.
     * @param entity The current entity.
     * @param camera The camera reference.
     * @param color The box color.
     */
    protected void renderEntityOver(Graphic g, E entity, CameraRts camera, Color color)
    {
        if (entity.isOver() && color != null)
        {
            drawRect(g, entity, camera, color);
        }
    }

    /**
     * Rendering function when entity is selected. If color is null, nothing will be displayed.
     * 
     * @param g The graphic output.
     * @param entity The current entity.
     * @param camera The camera reference.
     * @param color The box color.
     */
    protected void renderEntitySelection(Graphic g, E entity, CameraRts camera, Color color)
    {
        if (entity.isSelected() && color != null)
        {
            drawRect(g, entity, camera, color);
        }
    }

    /**
     * Get the positioning offset in case of multiple entity destination assignment.
     * 
     * @param dtx The original horizontal tile destination.
     * @param dty The original vertical tile destination.
     * @param i The entity number in local group.
     * @return The modified destination.
     */
    protected CoordTile getPositioning(int dtx, int dty, int i)
    {
        final int factor = i / 4;
        final int x;
        final int y;
        if (i % 4 == 0)
        {
            x = factor;
            y = factor;
        }
        else if (i % 4 == 1)
        {
            x = factor + 1;
            y = factor;
        }
        else if (i % 4 == 2)
        {
            x = factor;
            y = factor - 1;
        }
        else
        {
            x = factor + 1;
            y = factor - 1;
        }
        return new CoordTile(dtx + x, dty + y);
    }

    /**
     * Main routine, which has to be called in main game loop.
     * 
     * @param extrp The extrapolation value.
     * @param camera The camera viewpoint.
     * @param cursor The cursor reference (used for selection).
     * @param entity The entity reference.
     */
    private void updateEntity(double extrp, CameraRts camera, CursorRts cursor, E entity)
    {
        // Check selection
        if (!entity.isAlive())
        {
            entity.setSelection(false);
        }

        // Check cursor over
        checkCursorOverEntity(entity, camera, cursor);

        // Update entity
        final int oldX = entity.getLocationInTileX();
        final int oldY = entity.getLocationInTileY();

        // Update after is active
        if (entity.isActive())
        {
            updatingEntity(entity, cursor, camera);
        }

        entity.update(extrp);

        // Notify movement
        if (oldX != entity.getLocationInTileX() || oldY != entity.getLocationInTileY())
        {
            for (final EntityRtsListener<E> listener : listeners)
            {
                listener.entityMoved(entity);
            }
        }

        checkEntityLayer(entity, oldY);

        // Remove entity
        if (entity.isDestroyed())
        {
            remove(entity);
        }

        checkEntitySkills(extrp, entity, camera, cursor);
    }

    /**
     * Update the click assignment for the current selected entities.
     * 
     * @param camera The camera reference.
     * @param cursor The cursor reference.
     */
    private void updateClickAssignment(CameraRts camera, CursorRts cursor)
    {
        sharedPathIds.clear();
        int i = 0;
        for (final E entity : selectedEntity)
        {
            updatingEntityClickAssignment(entity, cursor, camera, i);
            i++;
        }
        // Check shared path list
        for (final Integer id : sharedPathIds)
        {
            final E entity = get(id);
            if (entity instanceof MoverServices)
            {
                final MoverServices mover = (MoverServices) entity;
                mover.setSharedPathIds(sharedPathIds);
            }
        }
    }

    /**
     * Update the click assignment for the attacker case.
     * 
     * @param entity The entity reference.
     * @param tx The horizontal tile assignment.
     * @param ty The vertical tile assignment.
     */
    private void updateClickAttacker(E entity, int tx, int ty)
    {
        @SuppressWarnings("unchecked")
        final AttackerServices<E, ?> attacker = (AttackerServices<E, ?>) entity;
        attacker.stopAttack();
        try
        {
            final E target = getEntityAt(tx, ty);
            attacker.attack(target);
        }
        catch (final EntityNotFoundException exception)
        {
            updateClickMover(entity, tx, ty);
        }
    }

    /**
     * Update the click assignment for the extractor case.
     * 
     * @param entity The entity reference.
     * @param tx The horizontal tile assignment.
     * @param ty The vertical tile assignment.
     */
    private void updateClickExtractor(E entity, int tx, int ty)
    {
        try
        {
            @SuppressWarnings("unchecked")
            final ExtractorServices<R> extractor = (ExtractorServices<R>) entity;
            final E resource = getEntityAt(tx, ty);
            // Resource location is a building
            if (resource instanceof Extractible)
            {
                @SuppressWarnings("unchecked")
                final Extractible<R> extractible = (Extractible<R>) resource;
                extractor.setResource(extractible);
                extractor.startExtraction();
            }
            else
            {
                updateClickMover(entity, tx, ty);
            }
        }
        catch (final EntityNotFoundException exception)
        {
            // Resource location is a tile
            final T tile = map.getTile(tx, ty);
            if (map.getRef(tx, ty).intValue() == 0 && tile.hasResources())
            {
                final R type = tile.getResourceType();
                @SuppressWarnings("unchecked")
                final ExtractorServices<R> extractor = (ExtractorServices<R>) entity;
                extractor.setResource(type, tx, ty, 1, 1);
                extractor.startExtraction();
            }
            else
            {
                updateClickMover(entity, tx, ty);
            }
        }
    }

    /**
     * Update the click assignment for the mover case.
     * 
     * @param entity The entity reference.
     * @param tx The horizontal tile assignment.
     * @param ty The vertical tile assignment.
     */
    private void updateClickMover(E entity, int tx, int ty)
    {
        if (entity instanceof MoverServices)
        {
            final MoverServices mover = (MoverServices) entity;
            mover.setDestination(tx, ty);
        }
    }

    /**
     * Check and update if the cursor is over the entity.
     * 
     * @param entity The entity to check.
     * @param camera The camera reference.
     * @param cursor The cursor reference.
     */
    private void checkCursorOverEntity(E entity, CameraRts camera, CursorRts cursor)
    {
        if (entity.isActive())
        {
            entityArea.setRect(entity.getLocationIntX(), entity.getLocationIntY(), entity.getWidth(),
                    entity.getHeight());
            if (entity.isSelectable() && camera.canSee(entity)
                    && entityArea.contains(cursor.getLocationX(), cursor.getLocationY()))
            {
                entity.setOver(true);
            }
            else
            {
                entity.setOver(false);
            }
        }
    }

    /**
     * Update the entity layer depending of its vertical position.
     * 
     * @param entity The entity reference.
     * @param oldY The old vertical location.
     */
    private void checkEntityLayer(E entity, int oldY)
    {
        if (oldY != entity.getLocationInTileY())
        {
            entity.setMapLayer(entity.getLocationInTileY());
            entity.setMapLayerChanged(true);
        }
        if (entity.isLayerChanged() || entity.isMapLayerChanged())
        {
            entity.setMapLayerChanged(false);
            final List<List<E>> layer = layers.get(entity.getLayer());
            layer.get(entity.getMapLayerOld()).remove(entity);
            layer.get(entity.getMapLayer()).add(entity);
        }
    }

    /**
     * Update the entity skills.
     * 
     * @param extrp The extrapolation value.
     * @param entity The entity reference.
     * @param camera The camera reference.
     * @param cursor The cursor reference.
     */
    private void checkEntitySkills(double extrp, E entity, CameraRts camera, CursorRts cursor)
    {
        if (entity instanceof SkilledServices)
        {
            final SkilledServices<?, ?> skilled = (SkilledServices<?, ?>) entity;
            final Collection<?> skills = skilled.getSkills();
            for (final Object skill : skills)
            {
                if (skill instanceof SkillRts)
                {
                    final SkillRts<?> skillRts = (SkillRts<?>) skill;
                    if (skillRts.isIgnored())
                    {
                        continue;
                    }
                    skillRts.updateOnMap(extrp, camera, cursor);
                }
            }
        }
    }

    /**
     * Draw a rectangle around the entity.
     * 
     * @param g The graphic output.
     * @param entity The current entity.
     * @param camera The camera reference.
     * @param color The box color.
     */
    private void drawRect(Graphic g, E entity, CameraRts camera, Color color)
    {
        g.setColor(color);
        final int x = camera.getViewpointX(entity.getLocationIntX());
        final int y = camera.getViewpointY(entity.getLocationIntY() + entity.getHeight());
        g.drawRect(x, y, entity.getWidth() - 1, entity.getHeight() - 1, false);
    }

    /*
     * HandlerEntityGame
     */

    @Override
    public void update(double extrp)
    {
        // Restore clicked flag
        if (cursor.getClick() == 0)
        {
            clicked = false;
        }
        super.update(extrp);

        // Update the click assignment
        if (cursor.getClick() == mouseClickAssign && !clicked)
        {
            updateClickAssignment(camera, cursor);
        }

        // Update clicked flag
        if (cursor.getClick() > 0)
        {
            clicked = true;
        }
    }

    @Override
    public void render(Graphic g)
    {
        for (final List<List<E>> layer : layers)
        {
            for (final List<E> l : layer)
            {
                for (final E entity : l)
                {
                    render(g, entity);
                }
            }
        }
    }

    @Override
    protected void update(double extrp, E entity)
    {
        updateEntity(extrp, camera, cursor, entity);
    }

    @Override
    protected void render(Graphic g, E entity)
    {
        if (camera.canSee(entity))
        {
            entity.render(g, camera);
            renderingEntity(g, entity, camera, cursor);
        }
    }

    /*
     * ControlPanelListener
     */

    @Override
    public void notifySelectionStarted(Rectangle2D selection)
    {
        updateSelection(selection);
    }

    @Override
    public void notifySelectionDone(Rectangle2D selection)
    {
        updateSelection(selection);
    }
}
