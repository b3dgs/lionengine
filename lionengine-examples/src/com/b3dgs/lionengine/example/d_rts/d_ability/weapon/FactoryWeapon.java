package com.b3dgs.lionengine.example.d_rts.d_ability.weapon;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.example.d_rts.d_ability.Context;
import com.b3dgs.lionengine.example.d_rts.d_ability.TypeWeapon;
import com.b3dgs.lionengine.example.d_rts.d_ability.entity.Entity;
import com.b3dgs.lionengine.game.SetupGame;
import com.b3dgs.lionengine.game.rts.ability.attacker.AttackerUsedServices;
import com.b3dgs.lionengine.game.rts.ability.attacker.FactoryWeaponRts;

/**
 * Weapons factory.
 */
public final class FactoryWeapon
        extends FactoryWeaponRts<TypeWeapon, Entity, Weapon, AttackerUsedServices<Entity>>
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
        super(TypeWeapon.class);
        loadAll(TypeWeapon.values());
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
    public Weapon createWeapon(TypeWeapon id, AttackerUsedServices<Entity> user)
    {
        switch (id)
        {
            case axe:
                return new Axe(user, context);
            case spear:
                return new Spear(user, context);
            default:
                throw new LionEngineException("Weapon not found: " + id.name());
        }
    }

    @Override
    protected SetupGame createSetup(TypeWeapon id)
    {
        return new SetupGame(Media.get(FactoryWeapon.WEAPONS_DIR, id + ".xml"));
    }
}
