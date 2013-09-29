package com.b3dgs.lionengine.example.e_shmup.b_shipweapon;

import java.io.IOException;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.Sequence;
import com.b3dgs.lionengine.example.e_shmup.b_shipweapon.effect.FactoryEffect;
import com.b3dgs.lionengine.example.e_shmup.b_shipweapon.effect.HandlerEffect;
import com.b3dgs.lionengine.example.e_shmup.b_shipweapon.projectile.FactoryProjectile;
import com.b3dgs.lionengine.example.e_shmup.b_shipweapon.projectile.HandlerProjectile;
import com.b3dgs.lionengine.example.e_shmup.b_shipweapon.weapon.FactoryWeapon;
import com.b3dgs.lionengine.file.FileReading;
import com.b3dgs.lionengine.file.FileWriting;
import com.b3dgs.lionengine.game.CameraGame;
import com.b3dgs.lionengine.game.WorldGame;

/**
 * World implementation.
 */
final class World
        extends WorldGame
{
    /** Camera reference. */
    private final CameraGame camera;
    /** Factory effect. */
    private final FactoryEffect factoryEffect;
    /** Handler effect. */
    private final HandlerEffect handlerEffect;
    /** Handler entity. */
    private final HandlerEntity handlerEntity;
    /** Factory projectile. */
    private final FactoryProjectile factoryProjectile;
    /** Handler projectile. */
    private final HandlerProjectile handlerProjectile;
    /** Weapon factory. */
    private final FactoryWeapon factoryWeapon;
    /** Ship reference. */
    private final Ship ship;

    /**
     * @see WorldGame#WorldGame(Sequence)
     */
    World(Sequence sequence)
    {
        super(sequence);
        camera = new CameraGame();
        factoryEffect = new FactoryEffect();
        handlerEffect = new HandlerEffect();
        handlerEntity = new HandlerEntity();
        factoryProjectile = new FactoryProjectile(factoryEffect, handlerEffect);
        handlerProjectile = new HandlerProjectile(handlerEntity);
        factoryWeapon = new FactoryWeapon(factoryProjectile, handlerProjectile);
        ship = new Ship(height, factoryWeapon);
        camera.setView(0, 0, width, height);
    }

    /*
     * WorldGame
     */

    @Override
    public void update(double extrp)
    {
        ship.update(extrp, mouse);
        handlerProjectile.update(extrp);
        handlerEffect.update(extrp);
    }

    @Override
    public void render(Graphic g)
    {
        g.clear(source);
        ship.render(g, camera);
        handlerProjectile.render(g, camera);
        handlerEffect.render(g, camera);
    }

    @Override
    protected void saving(FileWriting file) throws IOException
    {
        // Nothing to do
    }

    @Override
    protected void loading(FileReading file) throws IOException
    {
        // Nothing to do
    }
}
