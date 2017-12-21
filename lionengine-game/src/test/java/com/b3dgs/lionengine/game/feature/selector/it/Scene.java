/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.selector.it;

import org.junit.Assert;

import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.Timing;
import com.b3dgs.lionengine.core.Engine;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.core.sequence.Sequence;
import com.b3dgs.lionengine.game.Cursor;
import com.b3dgs.lionengine.game.feature.Camera;
import com.b3dgs.lionengine.game.feature.ComponentDisplayable;
import com.b3dgs.lionengine.game.feature.ComponentRefreshable;
import com.b3dgs.lionengine.game.feature.Factory;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.Handler;
import com.b3dgs.lionengine.game.feature.LayerableModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.collidable.ComponentCollision;
import com.b3dgs.lionengine.game.feature.collidable.selector.Hud;
import com.b3dgs.lionengine.game.feature.collidable.selector.Selectable;
import com.b3dgs.lionengine.game.feature.collidable.selector.Selector;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGame;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGroup;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGroupModel;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.MapTilePath;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.MapTilePathModel;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.Pathfindable;
import com.b3dgs.lionengine.game.feature.tile.map.viewer.MapTileViewerModel;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.io.awt.Mouse;

/**
 * Game loop designed to handle our little world.
 */
class Scene extends Sequence
{
    private static final Resolution NATIVE = new Resolution(320, 200, 60);
    private static final int VIEW_X = 72;
    private static final int VIEW_Y = 12;

    private final Services services = new Services();
    private final Handler handler = services.create(Handler.class);
    private final Camera camera = services.create(Camera.class);
    private final MapTile map = services.create(MapTileGame.class);
    private final Cursor cursor = services.create(Cursor.class);
    private final Mouse mouse = getInputDevice(Mouse.class);
    private final Timing timing = new Timing();
    private Transformable transformable;

    private int click;

    /**
     * Constructor.
     * 
     * @param context The context reference.
     */
    public Scene(Context context)
    {
        super(context, NATIVE);

        handler.addComponent(new ComponentRefreshable());
        handler.addComponent(new ComponentDisplayable());
        handler.addComponent(new ComponentCollision());

        services.add(Integer.valueOf(NATIVE.getRate()));

        setSystemCursorVisible(false);
    }

    @Override
    public void load()
    {
        map.create(Medias.create("forest.png"));

        final MapTileGroup mapGroup = map.addFeatureAndGet(new MapTileGroupModel());
        final MapTilePath mapPath = map.addFeatureAndGet(new MapTilePathModel(services));

        camera.setView(VIEW_X, VIEW_Y, getWidth() - VIEW_X, getHeight() - VIEW_Y, getHeight());
        camera.setLimits(map);
        camera.setLocation(160, 96);

        map.addFeature(new MapTileViewerModel(services));
        handler.add(map);
        mapGroup.loadGroups(Medias.create("groups.xml"));
        mapPath.loadPathfinding(Medias.create("pathfinding.xml"));

        cursor.addImage(0, Medias.create("cursor.png"));
        cursor.addImage(1, Medias.create("cursor_order.png"));
        cursor.load();
        cursor.setGrid(map.getTileWidth(), map.getTileHeight());
        cursor.setInputDevice(mouse);
        cursor.setViewer(camera);

        final Factory factory = services.create(Factory.class);

        final Hud hud = factory.create(Medias.create("Hud.xml"));
        handler.add(hud);

        final Selector selector = services.get(Selector.class);
        selector.addFeature(new LayerableModel(4, 4));
        selector.setClickableArea(camera);
        selector.setSelectionColor(ColorRgba.GREEN);
        selector.setClickSelection(Mouse.LEFT);
        selector.getFeature(Collidable.class).addAccept(2);

        final Featurable peon = factory.create(Medias.create("Peon.xml"));
        peon.getFeature(Pathfindable.class).setLocation(20, 10);
        handler.add(peon);
        transformable = peon.getFeature(Transformable.class);

        timing.start();
    }

    @Override
    public void update(double extrp)
    {
        mouse.update(extrp);
        cursor.update(extrp);
        handler.update(extrp);

        if (timing.elapsed(1000L))
        {
            end();
        }

        switch (click)
        {
            case 0: // Perform selection
                Assert.assertFalse(handler.get(Selectable.class).iterator().next().isSelected());
                mouse.doSetMouse((int) camera.getViewpointX(transformable.getX() - 16)
                                 * 2,
                                 (int) camera.getViewpointY(transformable.getY() - 16) * 2);
                mouse.doClick(Mouse.LEFT);
                click = 1;
                break;
            case 1: // Selection done
                mouse.doMoveMouse((int) camera.getViewpointX(transformable.getX() + 32)
                                  * 2,
                                  (int) camera.getViewpointY(transformable.getY() + 32) * 2);
                click = 2;
                break;
            case 2:
                click = 3;
                Assert.assertEquals(4, handler.size());
                break;
            case 3:
                click = 4;
                Assert.assertTrue(handler.get(Selectable.class).iterator().next().isSelected());
                break;
            case 4: // Click build button
                mouse.doClickAt(Mouse.LEFT, 16 * 2, 170 * 2);
                click = 5;
                break;
            case 5:
                click = 6;
                Assert.assertEquals(6, handler.size());
                break;
            case 6: // Click cancel button
                mouse.doClickAt(Mouse.LEFT, 45 * 2, 150 * 2);
                click = 7;
                break;
            case 7:
                click = 8;
                Assert.assertEquals(6, handler.size());
                break;
            case 8: // Click move button
                mouse.doClickAt(Mouse.LEFT, 16 * 2, 125 * 2);
                click = 9;
                break;
            case 9: // Assign destination
                mouse.doClickAt(Mouse.LEFT,
                                (int) camera.getViewpointX(transformable.getX() + 64) * 2,
                                (int) camera.getViewpointY(transformable.getY() + 64) * 2);
                click = 10;
                break;
            case 10: // Unselect
                mouse.doClickAt(Mouse.LEFT, 100 * 2, 100 * 2);
                click = 11;
                break;
            case 11:
                click = 12;
                Assert.assertEquals(6, handler.size());
                break;
            case 12:
                mouse.doClick(Mouse.LEFT);
                click = 13;
                break;
            case 13:
                click = 14;
                break;
            case 14:
                Assert.assertFalse(handler.get(Selectable.class).iterator().next().isSelected());
                click = 15;
                break;
            default:
                break;
        }
    }

    @Override
    public void render(Graphic g)
    {
        handler.render(g);
        cursor.render(g);
    }

    @Override
    public void onTerminated(boolean hasNextSequence)
    {
        Engine.terminate();
    }
}
