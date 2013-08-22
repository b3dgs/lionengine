package com.b3dgs.lionengine.example.d_rts.f_warcraft.projectile;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.ResourcesLoader;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.type.TypeProjectile;
import com.b3dgs.lionengine.game.SetupEntityGame;
import com.b3dgs.lionengine.game.projectile.FactoryProjectileGame;
import com.b3dgs.lionengine.game.purview.model.ConfigurableModel;

/**
 * Factory projectile implementation.
 */
public final class FactoryProjectile
        extends FactoryProjectileGame<TypeProjectile, Projectile, SetupEntityGame>
{
    /**
     * Constructor.
     */
    public FactoryProjectile()
    {
        super(TypeProjectile.class);
        loadAll(TypeProjectile.values());
    }

    /*
     * FactoryProjectileGame
     */

    @Override
    public Projectile createProjectile(TypeProjectile type, int id, int frame)
    {
        switch (type)
        {
            case spear:
                return new Spear(getSetup(TypeProjectile.spear), id, frame);
            case arrow:
                return new Arrow(getSetup(TypeProjectile.arrow), id, frame);
            default:
                throw new LionEngineException("Projectile not found: " + type.name());
        }
    }

    @Override
    protected SetupEntityGame createSetup(TypeProjectile id)
    {
        return new SetupEntityGame(new ConfigurableModel(), Media.get(ResourcesLoader.PROJECTILES_DIR, id + ".xml"),
                false);
    }
}
