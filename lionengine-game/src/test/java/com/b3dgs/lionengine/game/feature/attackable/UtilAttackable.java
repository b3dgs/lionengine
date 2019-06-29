/*
 * Copyright (C) 2013-2019 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.attackable;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.Animator;
import com.b3dgs.lionengine.AnimatorModel;
import com.b3dgs.lionengine.Range;
import com.b3dgs.lionengine.game.feature.AnimatableModel;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.TransformableModel;

/**
 * Utilities dedicated to attackable test.
 */
final class UtilAttackable
{
    /**
     * Create the featurable.
     * 
     * @param featurable The featurable to prepare.
     */
    public static void prepare(Featurable featurable)
    {
        final Animator animator = new AnimatorModel();
        animator.play(new Animation("test", 1, 1, 1.0, false, false));
        featurable.addFeature(new AnimatableModel(animator));
        featurable.addFeature(new TransformableModel());
    }

    /**
     * Create an attacker.
     * 
     * @param object The object.
     * @param services The services.
     * @return The attacker.
     */
    public static AttackerModel createAttacker(Featurable object, Services services)
    {
        final AttackerModel attacker = new AttackerModel();
        attacker.recycle();
        attacker.setAttackDamages(new Range(1, 2));
        attacker.setAttackDistance(new Range(1, 2));
        attacker.setAttackFrame(1);
        attacker.setAttackTimer(2);
        attacker.prepare(object);

        return attacker;
    }

    /**
     * Create the listener.
     * 
     * @param preparing The preparing state.
     * @param reaching The reaching state.
     * @param started The attack started state.
     * @param ended The attack ended state.
     * @param anim The anim state.
     * @return The created listener
     */
    public static AttackerListener createListener(final AtomicBoolean preparing,
                                                  final AtomicReference<Transformable> reaching,
                                                  final AtomicReference<Transformable> started,
                                                  final AtomicReference<Transformable> ended,
                                                  final AtomicBoolean anim)
    {
        return new AttackerListener()
        {
            @Override
            public void notifyReachingTarget(Transformable target)
            {
                reaching.set(target);
            }

            @Override
            public void notifyPreparingAttack()
            {
                preparing.set(true);
            }

            @Override
            public void notifyAttackStarted(Transformable target)
            {
                started.set(target);
            }

            @Override
            public void notifyAttackEnded(int damages, Transformable target)
            {
                ended.set(target);
            }

            @Override
            public void notifyAttackAnimEnded()
            {
                anim.set(true);
            }
        };
    }
}
