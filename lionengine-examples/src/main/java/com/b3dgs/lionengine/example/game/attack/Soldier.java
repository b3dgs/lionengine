/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.b3dgs.lionengine.example.game.attack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.game.AnimationConfig;
import com.b3dgs.lionengine.game.Orientation;
import com.b3dgs.lionengine.game.feature.Animatable;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Layerable;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.attackable.Attacker;
import com.b3dgs.lionengine.game.feature.attackable.AttackerListener;
import com.b3dgs.lionengine.game.feature.rasterable.Rasterable;
import com.b3dgs.lionengine.game.feature.tile.map.Orientable;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.Pathfindable;

/**
 * Soldier entity implementation.
 */
@FeatureInterface
public final class Soldier extends FeatureModel implements AttackerListener
{
    /** Media reference. */
    static final Media HUMAN_SOLDIER = Medias.create("HumanSoldier.xml");
    /** Media reference. */
    static final Media ORC_SOLDIER = Medias.create("OrcSoldier.xml");
    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(Soldier.class);

    private final Layerable layerable;
    private final Animatable animatable;
    private final Pathfindable pathfindable;
    private final Orientable orientable;
    private final Rasterable rasterable;
    private final Attacker attacker;

    private final Animation idle;
    private final Animation walk;
    private final Animation attack;

    private int frameOffsetOld;

    /**
     * Create soldier.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     * @param layerable The layerable feature.
     * @param animatable The animatable feature.
     * @param pathfindable The pathfindable feature.
     * @param orientable The orientable feature.
     * @param rasterable The rasterable feature.
     * @param attacker The attacker feature.
     */
    public Soldier(Services services,
                   Setup setup,
                   Layerable layerable,
                   Animatable animatable,
                   Pathfindable pathfindable,
                   Orientable orientable,
                   Rasterable rasterable,
                   Attacker attacker)
    {
        super(services, setup);

        this.layerable = layerable;
        this.animatable = animatable;
        this.pathfindable = pathfindable;
        this.orientable = orientable;
        this.rasterable = rasterable;
        this.attacker = attacker;

        pathfindable.setSpeed(0.8, 0.8);

        final AnimationConfig config = AnimationConfig.imports(setup);
        idle = config.getAnimation("idle");
        walk = config.getAnimation("walk");
        attack = config.getAnimation("attack");
        attacker.setAttackFrame(attack.getLast());
        animatable.play(idle);
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
        animatable.play(walk);
    }

    /**
     * Set location in tile.
     * 
     * @param tx The horizontal tile.
     * @param ty The vertical tile.
     * @param mirror The mirror flag.
     */
    public void teleport(int tx, int ty, boolean mirror)
    {
        pathfindable.setLocation(tx, ty);
        if (mirror)
        {
            orientable.setOrientation(Orientation.SOUTH);
            layerable.setLayer(Integer.valueOf(1), Integer.valueOf(1));
        }
    }

    /**
     * Update frame offset to match animation with orientation.
     */
    private void updateFrameOffset()
    {
        final int frameOffset = pathfindable.getOrientation().ordinal();
        if (frameOffset != frameOffsetOld)
        {
            frameOffsetOld = frameOffset;
            rasterable.setAnimOffset(frameOffset * 24);
        }
    }

    @Override
    public void notifyReachingTarget(Transformable target)
    {
        updateFrameOffset();
    }

    @Override
    public void notifyAttackStarted(Transformable target)
    {
        animatable.play(attack);
    }

    @Override
    public void notifyAttackEnded(Transformable target, int damages)
    {
        LOGGER.info("Attack: ", Integer.valueOf(damages));
    }

    @Override
    public void notifyAttackAnimEnded()
    {
        animatable.play(idle);
    }

    @Override
    public void notifyPreparingAttack(Transformable target)
    {
        // Nothing to do
    }

    @Override
    public void notifyAttackStopped()
    {
        // Nothing to do
    }
}
