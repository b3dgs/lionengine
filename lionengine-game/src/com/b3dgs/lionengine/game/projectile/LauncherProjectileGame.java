package com.b3dgs.lionengine.game.projectile;

import com.b3dgs.lionengine.Timing;
import com.b3dgs.lionengine.game.Alterable;
import com.b3dgs.lionengine.game.SetupEntityGame;
import com.b3dgs.lionengine.game.Surface;
import com.b3dgs.lionengine.game.entity.EntityGame;
import com.b3dgs.lionengine.utility.UtilityMath;

/**
 * Represents a projectile launcher. It allows to handle projectile shots from an entity.
 * 
 * @param <T> The enum containing all projectiles type.
 * @param <E> The entity type used.
 * @param <E2> The entity attacker type used.
 * @param <P> The projectile type used.
 */
public abstract class LauncherProjectileGame<T extends Enum<T>, E extends EntityGame, E2 extends Surface, P extends ProjectileGame<E, E2>>
{
    /** Launcher level. */
    public final Alterable level;
    /** The projectile factory reference. */
    private final FactoryProjectileGame<T, P, ? extends SetupEntityGame> factory;
    /** The projectile handler reference. */
    private final HandlerProjectileGame<E, P> handler;
    /** The shoot timer. */
    private final Timing timer;
    /** Entity owner. */
    private E2 owner;
    /** Hit only target. */
    private boolean hitTargetOnly;
    /** Horizontal offset. */
    private int offsetX;
    /** Vertical offset. */
    private int offsetY;
    /** The shoot rate. */
    private int rate;

    /**
     * Create a new launcher.
     * 
     * @param factory The projectiles factory.
     * @param handler The projectiles handler.
     */
    public LauncherProjectileGame(FactoryProjectileGame<T, P, ? extends SetupEntityGame> factory,
            HandlerProjectileGame<E, P> handler)
    {
        this.factory = factory;
        this.handler = handler;
        owner = null;
        timer = new Timing();
        level = new Alterable(99);
        level.set(0);
        timer.start();
    }

    /**
     * Set the projectile owner.
     * 
     * @param owner The entity owner.
     */
    public void setOwner(E2 owner)
    {
        this.owner = owner;
    }

    /**
     * Set the hit target properties.
     * 
     * @param hitTargetOnly <code>true</code> to make the projectile hit only the target, <code>false</code> to allows
     *            other hits.
     */
    public void setCanHitTargetOnly(boolean hitTargetOnly)
    {
        this.hitTargetOnly = hitTargetOnly;
    }

    /**
     * Start shoot.
     * 
     * @return <code>true</code> if shot, <code>false</code> else.
     */
    public boolean launch()
    {
        if (timer.elapsed(rate))
        {
            launchProjectile(owner);
            timer.start();
            return true;
        }
        return false;
    }

    /**
     * Start shoot.
     * 
     * @param target The target.
     * @return <code>true</code> if shot, <code>false</code> else.
     */
    public boolean launch(E target)
    {
        if (timer.elapsed(rate))
        {
            launchProjectile(owner, target);
            timer.start();
            return true;
        }
        return false;
    }

    /**
     * Get rate shoot percent.
     * 
     * @return rate percent value.
     */
    public int getRatePercent()
    {
        final int percent = (int) (timer.elapsed() * 100.0 / rate);
        return UtilityMath.fixBetween(percent, 0, 100);
    }

    /**
     * Set shoot rate (in millisecond).
     * 
     * @param rate shoot rate.
     */
    public void setRate(int rate)
    {
        this.rate = rate;
    }

    /**
     * Set global starting projectile horizontal offset.
     * 
     * @param x horizontal starting shoot offset.
     */
    public void setOffsetX(int x)
    {
        this.offsetX = x;
    }

    /**
     * Set global starting projectile vertical offset.
     * 
     * @param y vertical starting shoot offset.
     */
    public void setOffsetY(int y)
    {
        this.offsetY = y;
    }

    /**
     * Get the hit target only state.
     * 
     * @return <code>true</code> if can hit only the target, <code>false</code> else.
     */
    public boolean canHitOnlyTarget()
    {
        return hitTargetOnly;
    }

    /**
     * Perform an attack from the owner (called when the launcher fired its projectile(s).
     * <p>
     * Use theses functions to add projectiles:
     * </p>
     * <ul>
     * <li>{@link #addProjectile(Enum, int, int, double, double, double, double)}</li>
     * <li>{@link #addProjectile(Enum, int, int, int, double, double, double, double)}</li>
     * <li>{@link #addProjectile(Enum, long, int, int, double, double, double, double)}</li>
     * </ul>
     * 
     * @param owner The owner reference.
     */
    protected abstract void launchProjectile(E2 owner);

    /**
     * Perform an attack from the owner to target (called when the launcher fired its projectile(s).
     * 
     * @param owner The owner reference.
     * @param target The target reference.
     */
    protected abstract void launchProjectile(E2 owner, E target);

    /**
     * Add a projectile for the shoot.
     * 
     * @param type The projectile type.
     * @param dmg The projectile damage.
     * @param frame The projectile frame.
     * @param target The target to reach.
     * @param speed The projectile speed.
     * @param offX The horizontal projectile location offset.
     * @param offY The vertical projectile location offset.
     */
    protected void addProjectile(T type, int dmg, int frame, E target, double speed, double offX, double offY)
    {
        if (target != null)
        {
            final int sx = owner.getLocationIntX() + owner.getWidth() / 2;
            final int sy = owner.getLocationIntY() + owner.getHeight() / 2;
            final int dx = target.getLocationIntX() + target.getWidth() / 2;
            final int dy = target.getLocationIntY() + target.getHeight() / 2;
            final double dist = Math.max(Math.abs(sx - dx), Math.abs(sy - dy));
            final double vecX = (dx - sx) / dist * speed;
            final double vecY = (dy - sy) / dist * speed;

            addProjectile(type, -1, 0, dmg, frame, vecX, vecY, offX, offY, target);
        }
    }

    /**
     * Add a projectile for the shoot.
     * 
     * @param type The projectile type.
     * @param dmg The projectile damage.
     * @param frame The projectile frame.
     * @param vecX The horizontal projectile move.
     * @param vecY The vertical projectile move.
     * @param offX The horizontal projectile location offset.
     * @param offY The vertical projectile location offset.
     */
    protected void addProjectile(T type, int dmg, int frame, double vecX, double vecY, double offX, double offY)
    {
        addProjectile(type, -1, 0, dmg, frame, vecX, vecY, offX, offY, null);
    }

    /**
     * Add a linked projectile for the shoot.
     * 
     * @param type The projectile type.
     * @param id projectile id (when a projectile is destroyed, all projectiles with this id are also destroyed).
     * @param dmg The projectile damage.
     * @param frame The projectile frame.
     * @param vecX The horizontal projectile move.
     * @param vecY The vertical projectile move.
     * @param offX The horizontal projectile location offset.
     * @param offY The vertical projectile location offset.
     */
    protected void addProjectile(T type, int id, int dmg, int frame, double vecX, double vecY, double offX, double offY)
    {
        addProjectile(type, id, 0, dmg, frame, vecX, vecY, offX, offY, null);
    }

    /**
     * Add a delayed projectile for the shoot.
     * 
     * @param type The projectile type.
     * @param delay delay before be thrown.
     * @param dmg The projectile damage.
     * @param frame The projectile frame.
     * @param vecX The horizontal projectile move.
     * @param vecY The vertical projectile move.
     * @param offX The horizontal projectile location offset.
     * @param offY The vertical projectile location offset.
     */
    protected void addProjectile(T type, long delay, int dmg, int frame, double vecX, double vecY, double offX,
            double offY)
    {
        addProjectile(type, -1, delay, dmg, frame, vecX, vecY, offX, offY, null);
    }

    /**
     * Add a linked projectile for the shoot.
     * 
     * @param type The projectile type.
     * @param id projectile id (when a projectile is destroyed, all projectiles with this id are also destroyed).
     * @param delay delay before be thrown.
     * @param dmg The projectile damage.
     * @param frame The projectile frame.
     * @param vecX The horizontal projectile move.
     * @param vecY The vertical projectile move.
     * @param offX The horizontal projectile location offset.
     * @param offY The vertical projectile location offset.
     * @param target The target reference.
     */
    private void addProjectile(T type, int id, long delay, int dmg, int frame, double vecX, double vecY, double offX,
            double offY, E target)
    {
        final P projectile = factory.createProjectile(type, id, frame);

        projectile.setOwner(owner);
        projectile.setTarget(target);
        projectile.setCanHitTargetOnly(hitTargetOnly);
        projectile.damages.setMin(dmg);
        projectile.damages.setMax(dmg);
        projectile.start(owner.getLocationIntX(), owner.getLocationIntY(), vecX, vecY);
        projectile.moveLocation(1, offX + offsetX, offsetY - offY);
        projectile.setDelay(delay);

        this.handler.add(projectile);
    }
}
