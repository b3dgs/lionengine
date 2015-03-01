/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.strategy.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.game.ControlPanelListener;
import com.b3dgs.lionengine.game.CoordTile;
import com.b3dgs.lionengine.game.Tiled;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.object.Handler;
import com.b3dgs.lionengine.game.strategy.CameraStrategy;
import com.b3dgs.lionengine.game.strategy.CursorStrategy;
import com.b3dgs.lionengine.game.strategy.ability.attacker.AttackerServices;
import com.b3dgs.lionengine.game.strategy.ability.extractor.Extractible;
import com.b3dgs.lionengine.game.strategy.ability.extractor.ExtractorServices;
import com.b3dgs.lionengine.game.strategy.ability.mover.MoverServices;
import com.b3dgs.lionengine.game.strategy.ability.skilled.SkilledServices;
import com.b3dgs.lionengine.game.strategy.map.MapTileStrategy;
import com.b3dgs.lionengine.game.strategy.map.TileStrategy;
import com.b3dgs.lionengine.game.strategy.skill.SkillStrategy;
import com.b3dgs.lionengine.geom.Geom;
import com.b3dgs.lionengine.geom.Rectangle;

/**
 * This class will handle a list of entities, by updating and rendering them. All actions are defined here, such as
 * selection and orders. Don't forget to call {@link #createLayers(MapTile)} after map creation (when its size is
 * established).
 * 
 * @param <R> Resource enumeration type.
 * @param <T> Tile type used.
 * @param <E> Entity type used.
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public abstract class HandlerEntityStrategy<R extends Enum<R>, T extends TileStrategy<R>, E extends EntityStrategy>
        extends Handler<E>
        implements ControlPanelListener
{
    /** Maximum number of layers. */
    static final int LAYERS = 4;

    /** Camera reference. */
    private final CameraStrategy camera;
    /** Cursor reference. */
    private final CursorStrategy cursor;
    /** Entity listener. */
    private final Collection<EntityStrategyListener<E>> listeners;
    /** Current entity selection set. */
    private final Collection<E> selectedEntity;
    /** Map reference. */
    private final MapTileStrategy<R, T> map;
    /** Entity area buffer for selection check. */
    private final Rectangle entityArea;
    /** List of entities id that shared the same path. */
    private final Collection<Integer> sharedPathIds;
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
     * @param map The map reference.
     */
    public HandlerEntityStrategy(CameraStrategy camera, CursorStrategy cursor, MapTileStrategy<R, T> map)
    {
        super();
        this.camera = camera;
        this.cursor = cursor;
        this.map = map;
        listeners = new ArrayList<>(1);
        selectedEntity = new HashSet<>(8);
        entityArea = Geom.createRectangle();
        sharedPathIds = new HashSet<>(0);
        layers = null;
    }

    /**
     * Update the entity.
     * 
     * @param entity The entity reference.
     * @param cursor The cursor reference.
     * @param camera The camera reference.
     */
    protected abstract void updatingEntity(E entity, CursorStrategy cursor, CameraStrategy camera);

    /**
     * Get the corresponding color when the mouse is over this entity.
     * 
     * @param entity The current entity.
     * @return color The representing mouse over an entity.
     */
    protected abstract ColorRgba getEntityColorOver(E entity);

    /**
     * Get the corresponding color when the entity is selected.
     * 
     * @param entity The current entity.
     * @return color The representing selected entity.
     */
    protected abstract ColorRgba getEntityColorSelection(E entity);

    /**
     * Add an entity listener.
     * 
     * @param listener Entity listener.
     */
    public void addListener(EntityStrategyListener<E> listener)
    {
        listeners.add(listener);
    }

    /**
     * Prepare rendering layers, depending of the map height. When an entity moved and its vertical location changed,
     * its layer is automatically adjusted.
     * 
     * @param map The map reference.
     */
    public void createLayers(MapTile<T> map)
    {
        layers = new ArrayList<>(HandlerEntityStrategy.LAYERS);
        for (int i = 0; i < HandlerEntityStrategy.LAYERS; i++)
        {
            final List<List<E>> layer = new ArrayList<>(map.getInTileWidth());
            layers.add(layer);
            for (int j = 0; j <= map.getInTileHeight(); j++)
            {
                layer.add(new LinkedList<E>());
            }
        }
    }

    /**
     * Set the mouse click assignment.
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
        return getEntityAt(location.getInTileX(), location.getInTileY());
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
        final Integer id = map.getObjectsId(tx, ty);
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
        for (final E entity : getObjects())
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
    public Collection<E> getSelection()
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
    protected void updatingEntityClickAssignment(E entity, CursorStrategy cursor, CameraStrategy camera, int i)
    {
        // Make the unit move on click if already selected
        // Allow only single assination on click
        if (entity.getPlayerId() == playerId && camera.isInside(cursor) && entity.isSelected())
        {
            final int tx = cursor.getInTileX();
            final int ty = cursor.getInTileY();
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
    protected void updateSelection(Rectangle selection)
    {
        // Reset selection
        selectedEntity.clear();

        // Allows both selection and single click without moving the mouse
        selection.set(selection.getX(), selection.getY() - 1, Math.max(1, selection.getWidth()),
                Math.max(1, selection.getHeight()));

        // Update the selection list
        for (final E entity : getObjects())
        {
            if (!entity.isActive() || !entity.isSelectable())
            {
                continue;
            }
            entityArea.set(entity.getX(), entity.getY(), entity.getWidth(), entity.getHeight());

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
        for (final EntityStrategyListener<E> listener : listeners)
        {
            listener.notifyUpdatedSelection(selectedEntity);
        }
    }

    /**
     * Rendering function for an entity. Here are handled the entity rendering, such as sprite, border selection...
     * 
     * @param g The graphic output.
     * @param entity The current entity.
     * @param camera The camera reference.
     * @param cursor The cursor reference.
     */
    protected void renderingEntity(Graphic g, E entity, CameraStrategy camera, CursorStrategy cursor)
    {
        if (entity.isActive())
        {
            renderEntityOver(g, entity, camera, getEntityColorOver(entity));
            renderEntitySelection(g, entity, camera, getEntityColorSelection(entity));
        }

        if (entity instanceof SkilledServices)
        {
            final SkilledServices<?> skilled = (SkilledServices<?>) entity;
            final Collection<?> skills = skilled.getSkills();
            for (final Object skill : skills)
            {
                if (skill instanceof SkillStrategy)
                {
                    final SkillStrategy skillStrategy = (SkillStrategy) skill;
                    if (skillStrategy.isIgnored() || !skillStrategy.isActive())
                    {
                        continue;
                    }
                    skillStrategy.renderOnMap(g, cursor, camera);
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
    protected void renderEntityOver(Graphic g, E entity, CameraStrategy camera, ColorRgba color)
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
    protected void renderEntitySelection(Graphic g, E entity, CameraStrategy camera, ColorRgba color)
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
    private void updateEntity(double extrp, CameraStrategy camera, CursorStrategy cursor, E entity)
    {
        // Check selection
        if (!entity.isAlive())
        {
            entity.setSelection(false);
        }

        // Check cursor over
        checkCursorOverEntity(entity, camera, cursor);

        // Update entity
        final int oldX = entity.getInTileX();
        final int oldY = entity.getInTileY();

        // Update after is active
        if (entity.isActive())
        {
            updatingEntity(entity, cursor, camera);
        }

        entity.update(extrp);

        // Notify movement
        if (oldX != entity.getInTileX() || oldY != entity.getInTileY())
        {
            for (final EntityStrategyListener<E> listener : listeners)
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
    private void updateClickAssignment(CameraStrategy camera, CursorStrategy cursor)
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
        final AttackerServices<E, ?, ?> attacker = (AttackerServices<E, ?, ?>) entity;
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
            final ExtractorServices extractor = (ExtractorServices) entity;
            final E resource = getEntityAt(tx, ty);
            // Resource location is a building
            if (resource instanceof Extractible)
            {
                final Extractible extractible = (Extractible) resource;
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
            if (map.getObjectsId(tx, ty).intValue() == 0 && tile.hasResources())
            {
                final R type = tile.getResourceType();
                final ExtractorServices extractor = (ExtractorServices) entity;
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
    private void checkCursorOverEntity(E entity, CameraStrategy camera, CursorStrategy cursor)
    {
        if (entity.isActive())
        {
            entityArea.set(entity.getX(), entity.getY(), entity.getWidth(), entity.getHeight());
            if (entity.isSelectable() && camera.canSee(entity)
                    && entityArea.contains(cursor.getLocationX(), cursor.getLocationY() - 1))
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
        if (oldY != entity.getInTileY())
        {
            entity.setMapLayer(entity.getInTileY());
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
    private void checkEntitySkills(double extrp, E entity, CameraStrategy camera, CursorStrategy cursor)
    {
        if (entity instanceof SkilledServices)
        {
            final SkilledServices<?> skilled = (SkilledServices<?>) entity;
            final Collection<?> skills = skilled.getSkills();
            for (final Object skill : skills)
            {
                if (skill instanceof SkillStrategy)
                {
                    final SkillStrategy skillStrategy = (SkillStrategy) skill;
                    if (skillStrategy.isIgnored())
                    {
                        continue;
                    }
                    skillStrategy.updateOnMap(extrp, camera, cursor);
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
    private void drawRect(Graphic g, E entity, CameraStrategy camera, ColorRgba color)
    {
        g.setColor(color);
        final int x = (int) camera.getViewpointX(entity.getX());
        final int y = (int) camera.getViewpointY(entity.getY() + entity.getHeight());
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
                    render(g, null, entity);
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
    protected void render(Graphic g, Viewer viewer, E entity)
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
    public void notifySelectionStarted(Rectangle selection)
    {
        updateSelection(selection);
    }

    @Override
    public void notifySelectionDone(Rectangle selection)
    {
        updateSelection(selection);
    }
}
