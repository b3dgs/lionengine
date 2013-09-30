package com.b3dgs.lionengine.example.d_rts.f_warcraft;

import com.b3dgs.lionengine.example.d_rts.f_warcraft.effect.FactoryEffect;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.entity.FactoryEntity;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.projectile.FactoryProjectile;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.skill.FactorySkill;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.weapon.FactoryWeapon;
import com.b3dgs.lionengine.game.TimedMessage;
import com.b3dgs.lionengine.game.rts.CameraRts;

/**
 * Context container.
 */
public final class Context
{
    /** The map reference. */
    public final Map map;
    /** The factory reference. */
    public final FactoryEntity factoryEntity;
    /** The factory reference. */
    public final FactoryProjectile factoryProjectile;
    /** The factory skill. */
    public final FactorySkill factorySkill;
    /** The factory production. */
    public final FactoryProduction factoryProduction;
    /** The factory weapon. */
    public final FactoryWeapon factoryWeapon;
    /** The factory effect. */
    public final FactoryEffect factoryEffect;
    /** The handler entity reference. */
    public final HandlerEntity handlerEntity;
    /** The handler projectile reference. */
    public final HandlerProjectile handlerProjectile;
    /** The handler effect reference. */
    public final HandlerEffect handlerEffect;
    /** The timed message reference. */
    public final TimedMessage timedMessage;
    /** Cursor. */
    public final Cursor cursor;
    /** The desired fps. */
    public final int desiredFps;

    /**
     * Constructor.
     * 
     * @param camera The camera reference.
     * @param map The map reference.
     * @param handlerEntity The handler entity reference.
     * @param handlerProjectile The handler arrow reference.
     * @param cursor The cursor reference.
     * @param message The timed message reference.
     * @param desiredFps The the desired fps.
     */
    Context(CameraRts camera, Map map, HandlerEntity handlerEntity, HandlerProjectile handlerProjectile, Cursor cursor,
            TimedMessage message, int desiredFps)
    {
        this.map = map;
        this.handlerEntity = handlerEntity;
        this.handlerProjectile = handlerProjectile;
        this.cursor = cursor;
        timedMessage = message;
        this.desiredFps = desiredFps;
        factoryProduction = new FactoryProduction();
        factoryEntity = new FactoryEntity();
        factoryProjectile = new FactoryProjectile();
        factoryWeapon = new FactoryWeapon();
        factorySkill = new FactorySkill(handlerEntity, factoryProduction, cursor, map, message);
        factoryEffect = new FactoryEffect();
        handlerEffect = new HandlerEffect(camera);
    }

    /**
     * Assign context to factories.
     */
    public void assignContext()
    {
        factoryEntity.setContext(this);
        factoryWeapon.setContext(this);
    }
}
