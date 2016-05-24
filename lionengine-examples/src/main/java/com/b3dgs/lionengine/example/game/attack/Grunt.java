/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.SpriteAnimated;
import com.b3dgs.lionengine.game.feature.Service;
import com.b3dgs.lionengine.game.feature.animatable.AnimatableModel;
import com.b3dgs.lionengine.game.feature.displayable.DisplayableModel;
import com.b3dgs.lionengine.game.feature.layerable.Layerable;
import com.b3dgs.lionengine.game.feature.layerable.LayerableModel;
import com.b3dgs.lionengine.game.feature.refreshable.RefreshableModel;
import com.b3dgs.lionengine.game.object.ObjectGame;
import com.b3dgs.lionengine.game.object.SetupSurface;
import com.b3dgs.lionengine.game.object.feature.attackable.Attacker;
import com.b3dgs.lionengine.game.object.feature.attackable.AttackerChecker;
import com.b3dgs.lionengine.game.object.feature.attackable.AttackerListener;
import com.b3dgs.lionengine.game.object.feature.attackable.AttackerModel;
import com.b3dgs.lionengine.game.object.feature.transformable.Transformable;
import com.b3dgs.lionengine.game.object.feature.transformable.TransformableModel;
import com.b3dgs.lionengine.game.pathfinding.Pathfindable;
import com.b3dgs.lionengine.game.pathfinding.PathfindableModel;
import com.b3dgs.lionengine.graphic.Viewer;

/**
 * Grunt entity implementation.
 */
class Grunt extends ObjectGame implements AttackerChecker, AttackerListener
{
    /** Media reference. */
    public static final Media MEDIA = Medias.create("Grunt.xml");

    private final Pathfindable pathfindable;
    private final Attacker attacker;

    @Service private Viewer viewer;

    /**
     * Create a peon.
     * 
     * @param setup The setup reference.
     */
    public Grunt(SetupSurface setup)
    {
        super(setup);

        final Layerable layerable = addFeatureAndGet(new LayerableModel());
        layerable.setLayer(1);

        final Transformable transformable = addFeatureAndGet(new TransformableModel());
        pathfindable = addFeatureAndGet(new PathfindableModel(setup));

        attacker = addFeatureAndGet(new AttackerModel());
        attacker.setAttackDistance(16, 16);
        attacker.setAttackDamages(1, 5);
        attacker.setAttackFrame(1);
        attacker.setAttackTimer(1000);

        final SpriteAnimated surface = Drawable.loadSpriteAnimated(setup.getSurface(), 8, 7);
        surface.setOrigin(Origin.MIDDLE);
        surface.setFrameOffsets(-8, -8);

        addFeature(new RefreshableModel(extrp ->
        {
            pathfindable.update(extrp);
            attacker.update(extrp);
            surface.setLocation(viewer, transformable);
        }));
        addFeature(new AnimatableModel(surface));
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
