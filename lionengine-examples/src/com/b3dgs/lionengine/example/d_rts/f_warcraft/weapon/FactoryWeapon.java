package com.b3dgs.lionengine.example.d_rts.f_warcraft.weapon;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.Context;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.ResourcesLoader;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.entity.Attacker;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.entity.Entity;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.type.TypeWeapon;
import com.b3dgs.lionengine.game.SetupGame;
import com.b3dgs.lionengine.game.rts.ability.attacker.FactoryWeaponRts;

/**
 * Weapons factory.
 */
public final class FactoryWeapon
        extends FactoryWeaponRts<TypeWeapon, Entity, Weapon, Attacker>
{
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
    public Weapon createWeapon(TypeWeapon id, Attacker user)
    {
        switch (id)
        {
            case axe:
                return new Axe(user, context);
            case sword:
                return new Sword(user, context);
            case spear:
                return new Spear(user, context);
            case bow:
                return new Bow(user, context);
            default:
                throw new LionEngineException("Weapon not found: " + id.name());
        }
    }

    @Override
    protected SetupGame createSetup(TypeWeapon id)
    {
        return new SetupGame(Media.get(ResourcesLoader.WEAPONS_DIR, id + ".xml"));
    }
}
