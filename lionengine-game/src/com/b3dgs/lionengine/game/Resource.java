package com.b3dgs.lionengine.game;

/**
 * Represent a resource, such as gold, wood... It is possible to increase the resource amount, or spend it. It is also
 * possible to check if there are enough resource before spending it.
 * <p>
 * Example:
 * </p>
 * 
 * <pre>
 * final Resource gold = new Resource();
 * gold.add(100);
 * gold.get(); // returns 100
 * gold.canSpend(25); // returns true
 * gold.spend(25); // returns 75
 * gold.canSpend(100); // returns false
 * </pre>
 */
public class Resource
{
    /** Resource value. */
    private Alterable ressource;

    /**
     * Create a new blank resource.
     */
    public Resource()
    {
        ressource = new Alterable(Integer.MAX_VALUE);
    }

    /**
     * Create a new resource.
     * 
     * @param amount The starting amount.
     */
    public Resource(int amount)
    {
        ressource.setMax(amount);
    }

    /**
     * Increase resource stock with a specified amount.
     * 
     * @param amount The amount of new resource.
     * @return The amount added.
     */
    public int add(int amount)
    {
        return ressource.increase(amount);
    }

    /**
     * Decrease resource stock with a specified amount. Caution, it is possible to be negative. Ensure to call first
     * {@link Resource#canSpend(int)} if you expect only positive values !
     * 
     * @param amount The amount of resource to spend.
     */
    public void spend(int amount)
    {
        ressource.decrease(amount);
    }

    /**
     * Check if the specified amount of resource can be spent.
     * 
     * @param amount The amount to check.
     * @return <code>true</code> if current stock - amount > 0, <code>false</code> else.
     */
    public boolean canSpend(int amount)
    {
        return ressource.isEnough(amount);
    }

    /**
     * Get current amount of resource.
     * 
     * @return The current stock.
     */
    public int get()
    {
        return ressource.getCurrent();
    }
}
