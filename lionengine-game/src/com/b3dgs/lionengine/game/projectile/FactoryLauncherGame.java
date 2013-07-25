package com.b3dgs.lionengine.game.projectile;

/**
 * Handle projectile factory. Projectiles are instantiated from a list. This way it is easy to define different kind of
 * projectile.
 * 
 * @param <T> The enum containing all launchers type.
 * @param <L> The launcher type used.
 */
public abstract class FactoryLauncherGame<T extends Enum<T>, L extends LauncherProjectileGame<?, ?, ?, ?>>
{
    /**
     * Create a new projectile factory.
     */
    public FactoryLauncherGame()
    {
        // Nothing to do
    }

    /**
     * Create a projectile.
     * 
     * @param type The launcher enum.
     * @return The created launcher.
     */
    public abstract L createLauncher(T type);
}
