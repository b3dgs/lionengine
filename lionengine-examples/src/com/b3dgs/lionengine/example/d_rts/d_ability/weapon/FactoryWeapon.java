package com.b3dgs.lionengine.example.d_rts.d_ability.weapon;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.example.d_rts.d_ability.Context;
import com.b3dgs.lionengine.example.d_rts.d_ability.WeaponType;
import com.b3dgs.lionengine.example.d_rts.d_ability.entity.Entity;
import com.b3dgs.lionengine.game.SetupGame;
import com.b3dgs.lionengine.game.rts.ability.attacker.AttackerUsedServices;
import com.b3dgs.lionengine.game.rts.ability.attacker.FactoryWeaponRts;

/**
 * Weapons factory.
 */
public final class FactoryWeapon
        extends FactoryWeaponRts<WeaponType, Entity, Weapon, AttackerUsedServices<Entity>>
{
    /** Weapons path. */
    private static final String WEAPONS_DIR = "weapons";
    /** Context reference. */
    private Context context;

    /**
     * Constructor.
     */
    public FactoryWeapon()
    {
        super(WeaponType.class);
        loadAll(WeaponType.values());
    }

    /**
     * Set the context.
     * 
     * @param context The context
     */
    public void setContext(Context context)
    {
        this.context = context;
    }

    /*
     * FactoryWeaponRts
     */

    @Override
    public Weapon createWeapon(WeaponType id, AttackerUsedServices<Entity> user)
    {
        switch (id)
        {
            case AXE:
                return new Axe(user, context);
            case SPEAR:
                return new Spear(user, context);
            default:
                throw new LionEngineException("Weapon not found: " + id);
        }
    }

    @Override
    protected SetupGame createSetup(WeaponType id)
    {
        return new SetupGame(Media.get(FactoryWeapon.WEAPONS_DIR, id + ".xml"));
    }
}
