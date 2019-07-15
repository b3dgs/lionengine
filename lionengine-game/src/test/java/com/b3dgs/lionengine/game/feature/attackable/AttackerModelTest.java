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

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertFalse;
import static com.b3dgs.lionengine.UtilAssert.assertNotEquals;
import static com.b3dgs.lionengine.UtilAssert.assertNotNull;
import static com.b3dgs.lionengine.UtilAssert.assertNull;
import static com.b3dgs.lionengine.UtilAssert.assertThrows;
import static com.b3dgs.lionengine.UtilAssert.assertTimeout;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.UtilEnum;
import com.b3dgs.lionengine.UtilReflection;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.game.feature.Animatable;
import com.b3dgs.lionengine.game.feature.FeaturableModel;
import com.b3dgs.lionengine.game.feature.Identifiable;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.TransformableModel;
import com.b3dgs.lionengine.game.feature.UtilSetup;

/**
 * Test {@link AttackerModel}.
 */
public final class AttackerModelTest
{
    /** Hack enum. */
    private static final UtilEnum<AttackState> HACK = new UtilEnum<>(AttackState.class, AttackerModel.class);

    /**
     * Prepare test.
     */
    @BeforeAll
    public static void beforeTests()
    {
        HACK.addByValue(HACK.make("FAIL"));
        Medias.setResourcesDirectory(System.getProperty("java.io.tmpdir"));
    }

    /**
     * Clean up test.
     */
    @AfterAll
    public static void afterTests()
    {
        HACK.restore();
        Medias.setResourcesDirectory(null);
    }

    private final Services services = new Services();
    private final AtomicBoolean canAttack = new AtomicBoolean();
    private final FeaturableModel object = new FeaturableModel();
    private final Transformable target = new TransformableModel();
    private AttackerModel attacker;

    /**
     * Prepare test.
     */
    @BeforeEach
    public void prepare()
    {
        UtilAttackable.prepare(object);
        attacker = UtilAttackable.createAttacker(object, services);
    }

    /**
     * Clean test.
     */
    @AfterEach
    public void clean()
    {
        object.getFeature(Identifiable.class).notifyDestroyed();
    }

    /**
     * Test constructor with null configurer.
     */
    @Test
    public void testConstructorNullConfigurer()
    {
        assertThrows(() -> new AttackerModel(null), "Unexpected null argument !");
    }

    /**
     * Test the config.
     */
    @Test
    public void testConfig()
    {
        final int damagesMin = 1;
        final int damagesMax = 2;
        final int distanceMin = 1;
        final int distanceMax = 2;
        final int frame = 1;
        final int time = 100;

        final Media media = UtilSetup.createConfig();
        final Xml xml = new Xml(media);
        xml.add(AttackerConfig.exports(new AttackerConfig(time, distanceMin, distanceMax, damagesMin, damagesMax)));
        xml.save(media);

        final AttackerModel attacker = new AttackerModel(new Setup(media));
        attacker.setAttackFrame(frame);

        assertTrue(attacker.getAttackDamages() >= damagesMin);
        assertTrue(attacker.getAttackDamages() <= damagesMax);
        assertTrue(media.getFile().delete());
    }

    /**
     * Test the target.
     */
    @Test
    public void testTarget()
    {
        attacker.attack(target);

        assertEquals(target, attacker.getTarget());
    }

    /**
     * Test the reach target with not elapsed time.
     */
    @Test
    public void testTargetReachTimeNotElapsed()
    {
        target.teleport(0, 10);
        attacker.attack(target);
        final ObjectAttackerSelf listener = new ObjectAttackerSelf();
        attacker.addListener(listener);
        attacker.update(1.0);

        assertTrue(listener.flag.get());
    }

    /**
     * Test the cannot attack.
     */
    @Test
    public void testCantAttack()
    {
        canAttack.set(false);
        attacker.setAttackChecker(canAttack::get);
        target.teleport(0, 1);
        attacker.attack(target);

        attacker.update(1.0);
        attacker.update(1.0);

        assertNotNull(attacker.getTarget());
        assertFalse(attacker.isAttacking());
    }

    /**
     * Test the attack <code>null</code>.
     */
    @Test
    public void testAttackNull()
    {
        canAttack.set(true);
        attacker.setAttackChecker(canAttack::get);
        attacker.attack(target);

        assertNotNull(attacker.getTarget());
        assertFalse(attacker.isAttacking());

        attacker.update(1.0);

        assertNotNull(attacker.getTarget());
        assertFalse(attacker.isAttacking());

        attacker.attack(null);

        assertNotNull(attacker.getTarget());
        assertFalse(attacker.isAttacking());

        attacker.stopAttack();
        attacker.attack(null);
        attacker.update(1.0);

        assertNull(attacker.getTarget());
        assertFalse(attacker.isAttacking());
    }

    /**
     * Test the attack different target.
     */
    @Test
    public void testAttackDifferent()
    {
        canAttack.set(true);
        attacker.setAttackChecker(canAttack::get);

        final Transformable target1 = new TransformableModel();
        attacker.attack(target1);

        assertEquals(target1, attacker.getTarget());
        assertFalse(attacker.isAttacking());

        attacker.update(1.0);
        attacker.attack(target1);

        assertEquals(target1, attacker.getTarget());
        assertFalse(attacker.isAttacking());

        final Transformable target2 = new TransformableModel();
        attacker.stopAttack();
        attacker.attack(target2);

        assertEquals(target2, attacker.getTarget());
        assertFalse(attacker.isAttacking());

        attacker.update(1.0);

        assertEquals(target2, attacker.getTarget());
        assertFalse(attacker.isAttacking());
    }

    /**
     * Test the stop attack.
     */
    @Test
    public void testStopAttack()
    {
        canAttack.set(true);
        attacker.setAttackChecker(canAttack::get);

        final Transformable target = new TransformableModel();
        target.teleport(1, 1);
        attacker.attack(target);
        attacker.update(1.0);
        attacker.update(1.0);

        assertTrue(attacker.isAttacking());

        attacker.stopAttack();

        assertTrue(attacker.isAttacking());

        attacker.update(1.0);

        assertFalse(attacker.isAttacking());
    }

    /**
     * Test the self listener.
     */
    @Test
    public void testSelfListener()
    {
        UtilAttackable.createAttacker(object, services); // No listener check

        final ObjectAttackerSelf object2 = new ObjectAttackerSelf();
        UtilAttackable.prepare(object2);
        final AttackerModel attacker = UtilAttackable.createAttacker(object2, services);
        attacker.recycle();
        canAttack.set(true);
        attacker.setAttackChecker(canAttack::get);

        target.teleport(10, 10);
        attacker.update(1.0);

        assertFalse(object2.flag.get());

        attacker.attack(target);
        attacker.update(1.0);

        assertTrue(object2.flag.get());

        object2.getFeature(Identifiable.class).notifyDestroyed();
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
        attacker.setAttackChecker(canAttack::get);

        final AtomicBoolean preparing = new AtomicBoolean();
        final AtomicReference<Transformable> reaching = new AtomicReference<>();
        final AtomicReference<Transformable> started = new AtomicReference<>();
        final AtomicBoolean anim = new AtomicBoolean();
        final AtomicReference<Transformable> ended = new AtomicReference<>();
        final AttackerListener listener = UtilAttackable.createListener(preparing, reaching, started, ended, anim);
        attacker.addListener(listener);
        attacker.recycle();
        attacker.update(1.0);
        attacker.getFeature(Transformable.class).teleport(0, 0);
        attacker.setAttackDelay(5);
        attacker.setAttackFrame(1);
        target.teleport(5, 5);
        attacker.attack(target);
        attacker.update(1.0);
        attacker.update(1.0); // 2 ticks for attack interval
        attacker.getFeature(Animatable.class).play(new Animation("test", 1, 1, 1.0, false, false));

        assertEquals(target, reaching.get());
        assertFalse(preparing.get());
        assertFalse(attacker.isAttacking());

        target.teleport(0, 1);
        attacker.update(1.0);

        assertTrue(attacker.isAttacking());
        assertNotEquals(target, started.get());
        assertNotEquals(target, ended.get());

        attacker.update(1.0);
        attacker.getFeature(Animatable.class).update(1.0);
        attacker.update(1.0);

        assertFalse(preparing.get());

        attacker.update(1.0);
        attacker.update(1.0);
        attacker.update(1.0);
        attacker.update(1.0);
        attacker.update(1.0);

        assertTrue(preparing.get());

        assertTimeout(1000L, () ->
        {
            while (!target.equals(started.get()))
            {
                attacker.update(1.0);
            }
        });
        assertTrue(attacker.isAttacking());
        assertEquals(target, started.get());
        assertEquals(target, ended.get());

        object.getFeature(Animatable.class).update(1.0);
        attacker.update(1.0);

        assertTrue(anim.get());

        attacker.removeListener(listener);
        preparing.set(false);
        reaching.set(null);
        started.set(null);
        ended.set(null);
        anim.set(false);

        attacker.attack(target);
        attacker.update(1.0);
        attacker.update(1.0);

        assertFalse(preparing.get());
        assertNull(reaching.get());
        assertNull(started.get());
        assertNull(ended.get());
        assertFalse(anim.get());
    }

    /**
     * Test the auto add listener.
     */
    @Test
    public void testListenerAutoAdd()
    {
        final ObjectAttackerSelf object = new ObjectAttackerSelf();
        UtilAttackable.prepare(object);
        final Attacker attacker = UtilAttackable.createAttacker(object, new Services());
        attacker.checkListener(object);

        attacker.attack(target);
        attacker.update(1.0);
        attacker.update(1.0); // 2 ticks for attack interval

        assertTrue(object.flag.get());
    }

    /**
     * Test with enum fail.
     * 
     * @throws ReflectiveOperationException If error.
     */
    @Test
    public void testEnumFail() throws ReflectiveOperationException
    {
        final AttackerModel attacker = new AttackerModel();
        final Field field = attacker.getClass().getDeclaredField("state");
        UtilReflection.setAccessible(field, true);
        field.set(attacker, AttackState.values()[3]);

        assertThrows(() -> attacker.update(1.0), "Unknown enum: FAIL");
    }
}
