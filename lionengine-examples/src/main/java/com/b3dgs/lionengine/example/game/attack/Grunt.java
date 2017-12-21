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
package com.b3dgs.lionengine.example.game.attack;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.core.drawable.Drawable;
import com.b3dgs.lionengine.game.feature.AnimatableModel;
import com.b3dgs.lionengine.game.feature.DisplayableModel;
import com.b3dgs.lionengine.game.feature.FeaturableModel;
import com.b3dgs.lionengine.game.feature.LayerableModel;
import com.b3dgs.lionengine.game.feature.RefreshableModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.TransformableModel;
import com.b3dgs.lionengine.game.feature.attackable.Attacker;
import com.b3dgs.lionengine.game.feature.attackable.AttackerChecker;
import com.b3dgs.lionengine.game.feature.attackable.AttackerListener;
import com.b3dgs.lionengine.game.feature.attackable.AttackerModel;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.Pathfindable;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.PathfindableModel;
import com.b3dgs.lionengine.graphic.SpriteAnimated;

/**
 * Grunt entity implementation.
 */
class Grunt extends FeaturableModel implements AttackerChecker, AttackerListener
{
    /** Media reference. */
    public static final Media MEDIA = Medias.create("Grunt.xml");

    private final Pathfindable pathfindable;
    private final Attacker attacker;

    /**
     * Create a peon.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     */
    public Grunt(Services services, Setup setup)
    {
        super();

        addFeature(new LayerableModel(1));

        final Transformable transformable = addFeatureAndGet(new TransformableModel(setup));
        pathfindable = addFeatureAndGet(new PathfindableModel(services, setup));

        final SpriteAnimated surface = Drawable.loadSpriteAnimated(setup.getSurface(), 8, 7);
        surface.setOrigin(Origin.MIDDLE);
        surface.setFrameOffsets(-8, -8);

        addFeature(new AnimatableModel(surface));

        attacker = addFeatureAndGet(new AttackerModel());
        attacker.setAttackDistance(16, 16);
        attacker.setAttackDamages(1, 5);
        attacker.setAttackFrame(1);
        attacker.setAttackTimer(1000);

        final Viewer viewer = services.get(Viewer.class);

        addFeature(new RefreshableModel(extrp ->
        {
            pathfindable.update(extrp);
            attacker.update(extrp);
            surface.setLocation(viewer, transformable);
        }));

        addFeature(new DisplayableModel(surface::render));
    }

    /***
     * Attack a target.
     * 
     * @param target The target to attack.
     */
    public void attack(Transformable target)
    {
        pathfindable.setDestination(target);
        attacker.attack(target);
    }

    /**
     * Set location in tile.
     * 
     * @param tx The horizontal tile.
     * @param ty The vertical tile.
     */
    public void teleport(int tx, int ty)
    {
        pathfindable.setLocation(tx, ty);
    }

    @Override
    public boolean canAttack()
    {
        return true;
    }

    @Override
    public void notifyReachingTarget(Transformable target)
    {
        // Nothing to do
    }

    @Override
    public void notifyAttackStarted(Transformable target)
    {
        System.out.println("Attack !");
    }

    @Override
    public void notifyAttackEnded(int damages, Transformable target)
    {
        // Nothing to do
    }

    @Override
    public void notifyAttackAnimEnded()
    {
        // Nothing to do
    }

    @Override
    public void notifyPreparingAttack()
    {
        // Nothing to do
    }
}
