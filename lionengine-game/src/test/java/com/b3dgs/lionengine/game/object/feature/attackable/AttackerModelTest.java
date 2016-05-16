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
package com.b3dgs.lionengine.game.object.feature.attackable;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.anim.Anim;
import com.b3dgs.lionengine.anim.Animator;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.game.handler.Services;
import com.b3dgs.lionengine.game.object.ObjectGame;
import com.b3dgs.lionengine.game.object.ObjectGameTest;
import com.b3dgs.lionengine.game.object.Setup;
import com.b3dgs.lionengine.game.object.feature.animatable.Animatable;
import com.b3dgs.lionengine.game.object.feature.animatable.AnimatableModel;
import com.b3dgs.lionengine.game.object.feature.transformable.Transformable;
import com.b3dgs.lionengine.game.object.feature.transformable.TransformableModel;
import com.b3dgs.lionengine.test.UtilEnum;
import com.b3dgs.lionengine.util.UtilReflection;

/**
 * Test the attackable trait.
 */
public class AttackerModelTest
{
    /** Hack enum. */
    private static final UtilEnum<AttackState> HACK = new UtilEnum<AttackState>(AttackState.class, AttackerModel.class);

    /**
     * Prepare test.
     */
    @BeforeClass
    public static void setUp()
    {
        Medias.setResourcesDirectory(System.getProperty("java.io.tmpdir"));
        HACK.addByValue(HACK.make("FAIL"));
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        Medias.setResourcesDirectory(Constant.EMPTY_STRING);
        HACK.restore();
    }

    /**
     * Create the object.
     * 
     * @param object The object.
     */
    private static void prepareObject(ObjectGame object)
    {
        final Animator animator = Anim.createAnimator();
        animator.play(Anim.createAnimation("test", 1, 1, 1.0, false, false));
        object.addFeature(new AnimatableModel(animator));
        object.addFeature(new TransformableModel());
    }

    /**
     * Test the config.
     */
    @Test
    public void testConfig()
    {
        final int damageMin = 1;
        final int damageMax = 2;
        final int distanceMin = 1;
        final int distanceMax = 2;
        final int frame = 1;
        final int time = 100;

        final AttackerModel attacker = new AttackerModel();
        attacker.setAttackDamages(damageMin, damageMax);
        attacker.setAttackDistance(distanceMin, distanceMax);
        attacker.setAttackFrame(frame);
        attacker.setAttackTimer(time);

        Assert.assertTrue(attacker.getAttackDamages() >= damageMin);
        Assert.assertTrue(attacker.getAttackDamages() <= damageMax);
    }

    /**
     * Create an attacker.
     * 
     * @param object The object.
     * @param services The services.
     * @return The attacker.
     */
    private static Attacker createAttacker(ObjectGame object, Services services)
    {
        final Attacker attacker = new AttackerModel();
        attacker.setAttackDamages(1, 2);
        attacker.setAttackDistance(1, 2);
        attacker.setAttackFrame(1);
        attacker.setAttackTimer(0);
        attacker.prepare(object, services);
        return attacker;
    }

    /**
     * Add the listener.
     * 
     * @param attacker The attacker.
     * @param preparing The preparing state.
     * @param reaching The reaching state.
     * @param started The attack started state.
     * @param ended The attack ended state.
     * @param anim The anim state.
     */
    private static void addListener(Attacker attacker,
                                    final AtomicBoolean preparing,
                                    final AtomicReference<Transformable> reaching,
                                    final AtomicReference<Transformable> started,
                                    final AtomicReference<Transformable> ended,
                                    final AtomicBoolean anim)
    {
        attacker.addListener(new AttackerListener()
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
        });
    }

    /**
     * Test the target.
     */
    @Test
    public void testTarget()
    {
        final Media media = ObjectGameTest.createMedia(ObjectGame.class);
        final Services services = new Services();
        final AtomicBoolean canAttack = new AtomicBoolean();
        final ObjectAttacker object = new ObjectAttacker(new Setup(media), canAttack);
        prepareObject(object);
        final Attacker attacker = createAttacker(object, services);
        final Transformable target = new TransformableModel();
        attacker.attack(target);

        Assert.assertEquals(target, attacker.getTarget());

        object.notifyDestroyed();
        Assert.assertTrue(media.getFile().delete());
    }

    /**
     * Test the cannot attack.
     */
    @Test
    public void testCantAttack()
    {
        final Media media = ObjectGameTest.createMedia(ObjectGame.class);
        final Services services = new Services();
        final AtomicBoolean canAttack = new AtomicBoolean();
        final ObjectAttacker object = new ObjectAttacker(new Setup(media), canAttack);
        prepareObject(object);
        final Attacker attacker = createAttacker(object, services);
        final Transformable target = new TransformableModel();
        target.teleport(0, 1);
        attacker.attack(target);

        attacker.update(1.0);
        attacker.update(1.0);

        Assert.assertNotNull(attacker.getTarget());
        Assert.assertFalse(attacker.isAttacking());

        object.notifyDestroyed();
        Assert.assertTrue(media.getFile().delete());
    }

    /**
     * Test the attack <code>null</code>.
     */
    @Test
    public void testAttackNull()
    {
        final Media media = ObjectGameTest.createMedia(ObjectGame.class);
        final Services services = new Services();
        final AtomicBoolean canAttack = new AtomicBoolean();
        final ObjectAttacker object = new ObjectAttacker(new Setup(media), canAttack);
        prepareObject(object);
        final Attacker attacker = createAttacker(object, services);
        canAttack.set(true);
        attacker.attack(new TransformableModel());

        Assert.assertNotNull(attacker.getTarget());
        Assert.assertFalse(attacker.isAttacking());

        attacker.update(1.0);

        Assert.assertNotNull(attacker.getTarget());
        Assert.assertFalse(attacker.isAttacking());

        attacker.attack(null);

        Assert.assertNotNull(attacker.getTarget());
        Assert.assertFalse(attacker.isAttacking());

        attacker.stopAttack();
        attacker.attack(null);
        attacker.update(1.0);

        Assert.assertNull(attacker.getTarget());
        Assert.assertFalse(attacker.isAttacking());

        object.notifyDestroyed();
        Assert.assertTrue(media.getFile().delete());
    }

    /**
     * Test the attack different target.
     */
    @Test
    public void testAttackDifferent()
    {
        final Media media = ObjectGameTest.createMedia(ObjectGame.class);
        final Services services = new Services();
        final AtomicBoolean canAttack = new AtomicBoolean();
        final ObjectAttacker object = new ObjectAttacker(new Setup(media), canAttack);
        prepareObject(object);
        final Attacker attacker = createAttacker(object, services);
        canAttack.set(true);

        final Transformable target1 = new TransformableModel();
        attacker.attack(target1);

        Assert.assertEquals(target1, attacker.getTarget());
        Assert.assertFalse(attacker.isAttacking());

        attacker.update(1.0);
        attacker.attack(target1);

        Assert.assertEquals(target1, attacker.getTarget());
        Assert.assertFalse(attacker.isAttacking());

        final Transformable target2 = new TransformableModel();
        attacker.stopAttack();
        attacker.attack(target2);

        Assert.assertEquals(target2, attacker.getTarget());
        Assert.assertFalse(attacker.isAttacking());

        attacker.update(1.0);

        Assert.assertEquals(target2, attacker.getTarget());
        Assert.assertFalse(attacker.isAttacking());

        object.notifyDestroyed();
        Assert.assertTrue(media.getFile().delete());
    }

    /**
     * Test the stop attack.
     */
    @Test
    public void testStopAttack()
    {
        final Media media = ObjectGameTest.createMedia(ObjectGame.class);
        final Services services = new Services();
        final AtomicBoolean canAttack = new AtomicBoolean();
        final ObjectAttacker object = new ObjectAttacker(new Setup(media), canAttack);
        prepareObject(object);
        final Attacker attacker = createAttacker(object, services);
        canAttack.set(true);

        final Transformable target = new TransformableModel();
        target.teleport(1, 1);
        attacker.attack(target);
        attacker.update(1.0);
        attacker.update(1.0);

        Assert.assertTrue(attacker.isAttacking());

        attacker.stopAttack();

        Assert.assertTrue(attacker.isAttacking());

        attacker.update(1.0);

        Assert.assertFalse(attacker.isAttacking());

        object.notifyDestroyed();
        Assert.assertTrue(media.getFile().delete());
    }

    /**
     * Test the self listener.
     */
    @Test
    public void testSelfListener()
    {
        final Media media = ObjectGameTest.createMedia(ObjectGame.class);
        final Services services = new Services();
        final AtomicBoolean canAttack = new AtomicBoolean();
        final ObjectAttackerSelf object = new ObjectAttackerSelf(new Setup(media));
        prepareObject(object);
        final Attacker attacker = createAttacker(object, services);
        canAttack.set(true);

        final Transformable target = new TransformableModel();
        target.teleport(10, 10);
        attacker.attack(target);
        attacker.update(1.0);

        Assert.assertTrue(object.flag.get());

        object.notifyDestroyed();
        Assert.assertTrue(media.getFile().delete());
    }

    /**
     * Test the attack.
     * 
     * @throws InterruptedException If error.
     */
    @Test
    public void testListener() throws InterruptedException
    {
        final Media media = ObjectGameTest.createMedia(ObjectGame.class);
        final Services services = new Services();
        final AtomicBoolean canAttack = new AtomicBoolean();
        final ObjectAttacker object = new ObjectAttacker(new Setup(media), canAttack);
        prepareObject(object);
        final Attacker attacker = createAttacker(object, services);
        canAttack.set(true);

        final AtomicBoolean preparing = new AtomicBoolean();
        final AtomicReference<Transformable> reaching = new AtomicReference<Transformable>();
        final AtomicReference<Transformable> started = new AtomicReference<Transformable>();
        final AtomicBoolean anim = new AtomicBoolean();
        final AtomicReference<Transformable> ended = new AtomicReference<Transformable>();
        addListener(attacker, preparing, reaching, started, ended, anim);

        attacker.update(1.0);

        final Transformable target = new TransformableModel();
        attacker.getOwner().getFeature(Transformable.class).teleport(0, 0);
        target.teleport(5, 5);
        attacker.attack(target);
        attacker.update(1.0);

        Assert.assertEquals(target, reaching.get());
        Assert.assertFalse(preparing.get());
        Assert.assertFalse(attacker.isAttacking());

        attacker.setAttackTimer(10);
        target.teleport(0, 1);
        attacker.update(1.0);

        Assert.assertTrue(attacker.isAttacking());
        Assert.assertNotEquals(target, started.get());
        Assert.assertNotEquals(target, ended.get());

        attacker.update(1.0);

        Assert.assertTrue(preparing.get());

        Thread.sleep(11);
        attacker.update(1.0);

        Assert.assertTrue(attacker.isAttacking());
        Assert.assertEquals(target, started.get());
        Assert.assertEquals(target, ended.get());

        object.getFeature(Animatable.class).update(1.0);
        attacker.update(1.0);

        Assert.assertTrue(anim.get());

        object.notifyDestroyed();
        Assert.assertTrue(media.getFile().delete());
    }

    /**
     * Test with enum fail.
     * 
     * @throws NoSuchFieldException If error.
     * @throws IllegalArgumentException If error.
     * @throws IllegalAccessException If error.
     */
    @Test
    public void testEnumFail() throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException
    {
        final AttackerModel attacker = new AttackerModel();
        final Field field = attacker.getClass().getDeclaredField("state");
        UtilReflection.setAccessible(field, true);
        field.set(attacker, AttackState.values()[3]);
        try
        {
            attacker.update(1.0);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            Assert.assertEquals("Unknown enum: FAIL", exception.getMessage());
        }
    }

    /**
     * Attacker test.
     */
    private static class ObjectAttacker extends ObjectGame implements AttackerChecker
    {
        /** Can attack flag. */
        private final AtomicBoolean canAttack;

        /**
         * Constructor.
         * 
         * @param setup The setup.
         * @param canAttack Attack flag.
         */
        public ObjectAttacker(Setup setup, AtomicBoolean canAttack)
        {
            super(setup);
            this.canAttack = canAttack;
        }

        @Override
        public boolean canAttack()
        {
            return canAttack.get();
        }
    }

    /**
     * Attacker test.
     */
    private static class ObjectAttackerSelf extends ObjectGame implements AttackerChecker, AttackerListener
    {
        /** Flag. */
        private final AtomicBoolean flag = new AtomicBoolean();

        /**
         * Constructor.
         * 
         * @param setup The setup.s
         */
        public ObjectAttackerSelf(Setup setup)
        {
            super(setup);
        }

        @Override
        public boolean canAttack()
        {
            return true;
        }

        @Override
        public void notifyReachingTarget(Transformable target)
        {
            flag.set(true);
        }

        @Override
        public void notifyAttackStarted(Transformable target)
        {
            // Mock
        }

        @Override
        public void notifyAttackEnded(int damages, Transformable target)
        {
            // Mock
        }

        @Override
        public void notifyAttackAnimEnded()
        {
            // Mock
        }

        @Override
        public void notifyPreparingAttack()
        {
            // Mock
        }
    }
}
