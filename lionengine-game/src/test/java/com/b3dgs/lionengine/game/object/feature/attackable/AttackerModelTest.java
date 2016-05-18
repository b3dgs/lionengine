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

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.game.handler.Services;
import com.b3dgs.lionengine.game.object.ObjectGame;
import com.b3dgs.lionengine.game.object.ObjectGameTest;
import com.b3dgs.lionengine.game.object.Setup;
import com.b3dgs.lionengine.game.object.feature.animatable.Animatable;
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

    private final Media media = ObjectGameTest.createMedia(ObjectGame.class);
    private final Services services = new Services();
    private final AtomicBoolean canAttack = new AtomicBoolean();
    private final ObjectAttacker object = new ObjectAttacker(new Setup(media), canAttack);
    private final Transformable target = new TransformableModel();
    private Attacker attacker;

    /**
     * Prepare test.
     */
    @Before
    public void prepare()
    {
        UtilAttackable.prepareObject(object);
        attacker = UtilAttackable.createAttacker(object, services);
    }

    /**
     * Clean test.
     */
    @After
    public void clean()
    {
        object.notifyDestroyed();
        Assert.assertTrue(media.getFile().delete());
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
     * Test the target.
     */
    @Test
    public void testTarget()
    {
        attacker.attack(target);

        Assert.assertEquals(target, attacker.getTarget());
    }

    /**
     * Test the cannot attack.
     */
    @Test
    public void testCantAttack()
    {
        target.teleport(0, 1);
        attacker.attack(target);

        attacker.update(1.0);
        attacker.update(1.0);

        Assert.assertNotNull(attacker.getTarget());
        Assert.assertFalse(attacker.isAttacking());
    }

    /**
     * Test the attack <code>null</code>.
     */
    @Test
    public void testAttackNull()
    {
        canAttack.set(true);
        attacker.attack(target);

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
    }

    /**
     * Test the attack different target.
     */
    @Test
    public void testAttackDifferent()
    {
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
    }

    /**
     * Test the stop attack.
     */
    @Test
    public void testStopAttack()
    {
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
    }

    /**
     * Test the self listener.
     */
    @Test
    public void testSelfListener()
    {
        final ObjectAttackerSelf object = new ObjectAttackerSelf(new Setup(media));
        UtilAttackable.prepareObject(object);
        final Attacker attacker = UtilAttackable.createAttacker(object, services);
        canAttack.set(true);

        target.teleport(10, 10);
        attacker.attack(target);
        attacker.update(1.0);

        Assert.assertTrue(object.flag.get());
    }

    /**
     * Test the attack.
     * 
     * @throws InterruptedException If error.
     */
    @Test
    public void testListener() throws InterruptedException
    {
        canAttack.set(true);

        final AtomicBoolean preparing = new AtomicBoolean();
        final AtomicReference<Transformable> reaching = new AtomicReference<Transformable>();
        final AtomicReference<Transformable> started = new AtomicReference<Transformable>();
        final AtomicBoolean anim = new AtomicBoolean();
        final AtomicReference<Transformable> ended = new AtomicReference<Transformable>();
        attacker.addListener(UtilAttackable.createListener(preparing, reaching, started, ended, anim));

        attacker.update(1.0);
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
    }

    /**
     * Test the auto add listener.
     */
    @Test
    public void testListenerAutoAdd()
    {
        final ObjectAttackerSelf object = new ObjectAttackerSelf(new Setup(media));
        UtilAttackable.prepareObject(object);
        final Attacker attacker = UtilAttackable.createAttacker(object, new Services());
        attacker.checkListener(object);

        attacker.attack(target);
        attacker.update(1.0);

        Assert.assertTrue(object.flag.get());
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
}
