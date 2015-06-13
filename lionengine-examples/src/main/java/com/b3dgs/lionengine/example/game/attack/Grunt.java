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
package com.b3dgs.lionengine.example.game.attack;

import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.core.Renderable;
import com.b3dgs.lionengine.core.Updatable;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.SpriteAnimated;
import com.b3dgs.lionengine.game.object.ObjectGame;
import com.b3dgs.lionengine.game.object.Services;
import com.b3dgs.lionengine.game.object.SetupSurface;
import com.b3dgs.lionengine.game.trait.attackable.Attacker;
import com.b3dgs.lionengine.game.trait.attackable.AttackerChecker;
import com.b3dgs.lionengine.game.trait.attackable.AttackerListener;
import com.b3dgs.lionengine.game.trait.attackable.AttackerModel;
import com.b3dgs.lionengine.game.trait.pathfindable.Pathfindable;
import com.b3dgs.lionengine.game.trait.pathfindable.PathfindableModel;
import com.b3dgs.lionengine.game.trait.transformable.Transformable;
import com.b3dgs.lionengine.game.trait.transformable.TransformableModel;

/**
 * Grunt entity implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
class Grunt
        extends ObjectGame
        implements Updatable, Renderable, AttackerChecker, AttackerListener
{
    /** Media reference. */
    public static final Media MEDIA = Medias.create("Grunt.xml");

    /** Transformable model. */
    private final Transformable transformable = addTrait(new TransformableModel());
    /** Pathfindable model. */
    private final Pathfindable pathfindable = addTrait(new PathfindableModel());
    /** Attacker model. */
    private final Attacker attacker = addTrait(new AttackerModel());
    /** Surface reference. */
    private final SpriteAnimated surface;
    /** Viewer reference. */
    private final Viewer viewer;

    /**
     * Create a peon.
     * 
     * @param setup The setup reference.
     * @param services The services reference.
     */
    public Grunt(SetupSurface setup, Services services)
    {
        super(setup, services);
        viewer = services.get(Viewer.class);

        attacker.setAttackDistance(16, 16);
        attacker.setAttackDamages(1, 5);
        attacker.setAttackFrame(1);
        attacker.setAttackTimer(1000);

        surface = Drawable.loadSpriteAnimated(setup.surface, 8, 7);
        surface.setOrigin(Origin.MIDDLE);
        surface.setFrameOffsets(-8, -8);
        addType(surface);
    }

    /***
     * Attack a target.
     * 
     * @param target The target to attack.
     */
    public void attack(Transformable target)
    {
        pathfindable.setDestination(4, 10);
        attacker.attack(target);
    }

    @Override
    protected void onPrepared()
    {
        pathfindable.setLocation(2, 6);
    }

    @Override
    public void update(double extrp)
    {
        pathfindable.update(extrp);
        attacker.update(extrp);
        surface.setLocation(viewer, transformable);
    }

    @Override
    public void render(Graphic g)
    {
        surface.render(g);
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

    @Override
    public void notifyTargetLost(Transformable target)
    {
        // Nothing to do
    }
}
