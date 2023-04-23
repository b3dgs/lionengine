/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import com.b3dgs.lionengine.Range;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.game.feature.Animatable;
import com.b3dgs.lionengine.game.feature.FeaturableModel;
import com.b3dgs.lionengine.game.feature.Identifiable;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.TransformableModel;
import com.b3dgs.lionengine.game.feature.UtilSetup;
import com.b3dgs.lionengine.game.feature.UtilTransformable;

/**
 * Test {@link AttackerModel}.
 */
final class AttackerModelTest
{
    /** Object config test. */
    private static Media config;

    /**
     * Prepare test.
     */
    @BeforeAll
    public static void beforeTests()
    {
        Medias.setResourcesDirectory(System.getProperty("java.io.tmpdir"));
        config = UtilSetup.createMedia(AttackerModelTest.class);
    }

    /**
     * Clean up test.
     */
    @AfterAll
    public static void afterTests()
    {
        assertTrue(config.getFile().delete());
        Medias.setResourcesDirectory(null);
    }

    private final Services services = new Services();
    private final Setup setup = new Setup(config);
    private final AtomicBoolean canAttack = new AtomicBoolean();
    private final FeaturableModel object = new FeaturableModel(services, setup);
    private final Transformable target = new TransformableModel(services, setup);
    private AttackerModel attacker;

    /**
     * Prepare test.
     */
    @BeforeEach
    public void prepare()
    {
        UtilAttackable.prepare(object, services, setup);
        attacker = UtilAttackable.createAttacker(object, services, setup);
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
     * Test without config.
     */
    @Test
    void testNoConfig()
    {
        final Media media = UtilTransformable.createMedia(AttackerModelTest.class);
        final Xml xml = new Xml(media);
        xml.save(media);

        final AttackerModel attacker = new AttackerModel(services, new Setup(media));

        assertTrue(attacker.getAttackDamages() == 0);
        assertTrue(media.getFile().delete());
    }

    /**
     * Test the config.
     */
    @Test
    void testConfig()
    {
        final int damagesMin = 1;
        final int damagesMax = 2;
        final int distanceMin = 1;
        final int distanceMax = 2;
        final int frame = 1;
        final int time = 100;

        final Media media = UtilTransformable.createMedia(AttackerModelTest.class);
        final Xml xml = new Xml(media);
        xml.add(AttackerConfig.exports(new AttackerConfig(time, distanceMin, distanceMax, damagesMin, damagesMax)));
        xml.save(media);

        final AttackerModel attacker = new AttackerModel(services, new Setup(media));
        attacker.setAttackFrame(frame);

        assertTrue(attacker.getAttackDamages() >= damagesMin);
        assertTrue(attacker.getAttackDamages() <= damagesMax);
        assertTrue(media.getFile().delete());
    }

    /**
     * Test the target.
     */
    @Test
    void testTarget()
    {
        attacker.attack(target);

        assertEquals(target, attacker.getTarget());
    }

    /**
     * Test the reach target with not elapsed time.
     */
    @Test
    void testTargetReachTimeNotElapsed()
    {
        target.teleport(0, 10);
        attacker.attack(target);
        final ObjectAttackerSelf listener = new ObjectAttackerSelf(services, setup);
        attacker.addListener(listener);
        attacker.update(1.0);

        assertTrue(listener.flag.get());
    }

    /**
     * Test the custom distance computer.
     */
    @Test
    void testCustomDistandceComputer()
    {
        assertThrows(() -> attacker.setAttackDistanceComputer(null), "Unexpected null argument !");

        canAttack.set(true);
        attacker.setAttackChecker(t -> canAttack.get());

        final Transformable target = new TransformableModel(services, setup);
        target.teleport(1, 1);
        attacker.setAttackDistanceComputer((s, t) -> 5);
        attacker.attack(target);
        attacker.update(1.0);

        assertFalse(attacker.isAttacking());

        attacker.setAttackDistanceComputer((s, t) -> 1);
        attacker.update(1.0);
        attacker.update(1.0);

        assertTrue(attacker.isAttacking());
    }

    /**
     * Test the cannot attack.
     */
    @Test
    void testCantAttack()
    {
        canAttack.set(false);
        attacker.setAttackChecker(t -> canAttack.get());
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
    void testAttackNull()
    {
        canAttack.set(true);
        attacker.setAttackChecker(t -> canAttack.get());
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
    void testAttackDifferent()
    {
        canAttack.set(true);
        attacker.setAttackChecker(t -> canAttack.get());

        final Transformable target1 = new TransformableModel(services, setup);
        attacker.attack(target1);

        assertEquals(target1, attacker.getTarget());
        assertFalse(attacker.isAttacking());

        attacker.update(1.0);
        attacker.attack(target1);

        assertEquals(target1, attacker.getTarget());
        assertFalse(attacker.isAttacking());

        final Transformable target2 = new TransformableModel(services, setup);
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
    void testStopAttack()
    {
        canAttack.set(true);
        attacker.setAttackChecker(t -> canAttack.get());
        attacker.setAttackDistance(new Range(0, 2));

        final Transformable target = new TransformableModel(services, setup);
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
    void testSelfListener()
    {
        UtilAttackable.createAttacker(object, services, setup); // No listener check

        final ObjectAttackerSelf object2 = new ObjectAttackerSelf(services, setup);
        UtilAttackable.prepare(object2, services, setup);
        final AttackerModel attacker = UtilAttackable.createAttacker(object2, services, setup);
        attacker.recycle();
        canAttack.set(true);
        attacker.setAttackChecker(t -> canAttack.get());

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
    void testListener() throws InterruptedException
    {
        canAttack.set(true);
        attacker.setAttackChecker(t -> canAttack.get());

        final AtomicBoolean preparing = new AtomicBoolean();
        final AtomicReference<Transformable> reaching = new AtomicReference<>();
        final AtomicReference<Transformable> started = new AtomicReference<>();
        final AtomicBoolean anim = new AtomicBoolean();
        final AtomicReference<Transformable> ended = new AtomicReference<>();
        final AtomicBoolean stopped = new AtomicBoolean();
        final AttackerListener listener;
        listener = UtilAttackable.createListener(preparing, reaching, started, ended, anim, stopped);

        attacker.addListener(listener);
        attacker.recycle();
        attacker.update(1.0);
        attacker.getFeature(Transformable.class).teleport(0, 0);
        attacker.setAttackDelay(5);
        attacker.setAttackFrame(1);
        attacker.setAttackDistance(new Range(0, 2));
        target.setSize(1, 1);
        target.teleport(5, 5);
        attacker.attack(target);
        attacker.update(1.0);
        attacker.update(1.0);
        attacker.update(1.0); // 2 ticks for attack interval
        attacker.getFeature(Animatable.class).play(new Animation("test", 1, 1, 1.0, false, false));

        assertEquals(target, reaching.get());
        assertFalse(preparing.get());
        assertFalse(attacker.isAttacking());

        target.teleport(0, 1);
        attacker.update(1.0);

        assertNotEquals(target, started.get());
        assertNotEquals(target, ended.get());

        attacker.update(1.0);

        assertTrue(attacker.isAttacking());

        attacker.update(1.0);
        attacker.getFeature(Animatable.class).update(1.0);
        attacker.update(1.0);

        assertFalse(preparing.get());

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
        assertEquals(target, started.get());
        assertEquals(target, ended.get());

        attacker.update(1.0);

        assertTrue(attacker.isAttacking());

        object.getFeature(Animatable.class).update(1.0);
        attacker.update(1.0);

        assertTrue(anim.get());
        assertFalse(stopped.get());

        attacker.stopAttack();

        assertFalse(stopped.get());

        attacker.update(1.0);

        assertTrue(stopped.get());

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
    void testListenerAutoAdd()
    {
        final ObjectAttackerSelf object = new ObjectAttackerSelf(services, setup);
        UtilAttackable.prepare(object, services, setup);
        final Attacker attacker = UtilAttackable.createAttacker(object, services, setup);
        attacker.checkListener(new Object());
        attacker.checkListener(object);

        attacker.attack(target);
        attacker.update(1.0);
        attacker.update(1.0); // 2 ticks for attack interval

        assertTrue(object.flag.get());
    }
}
