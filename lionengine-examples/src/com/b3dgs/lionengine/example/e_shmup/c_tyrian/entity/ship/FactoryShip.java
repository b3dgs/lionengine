package com.b3dgs.lionengine.example.e_shmup.c_tyrian.entity.ship;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.example.e_shmup.c_tyrian.weapon.FactoryWeapon;
import com.b3dgs.lionengine.game.SetupSurfaceGame;
import com.b3dgs.lionengine.game.entity.FactoryEntityGame;

/**
 * Factory ship.
 */
public class FactoryShip
        extends FactoryEntityGame<ShipType, SetupSurfaceGame, Ship>
{
    /** Factory weapon. */
    private final FactoryWeapon factoryWeapon;

    /**
     * Constructor
     * 
     * @param factoryWeapon The factory weapon reference.
     */
    public FactoryShip(FactoryWeapon factoryWeapon)
    {
        super(ShipType.class);
        this.factoryWeapon = factoryWeapon;
        loadAll(ShipType.values());
    }

    @Override
    public Ship createEntity(ShipType id)
    {
        switch (id)
        {
            case GENCORE_PHOENIX:
                return new GencorePhoenix(getSetup(id), factoryWeapon);
            default:
                throw new LionEngineException("Unknown entity: " + id);
        }
    }

    @Override
    protected SetupSurfaceGame createSetup(ShipType id)
    {
        return new SetupSurfaceGame(Media.get("ships", id.toString() + ".xml"));
    }
}
