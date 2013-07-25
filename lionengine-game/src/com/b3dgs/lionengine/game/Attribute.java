package com.b3dgs.lionengine.game;

/**
 * Can describe an attribute (vitality, agility...). For fast and light development, it is recommended to make public
 * final instantiation, as it already purposes set & get functions.
 * <p>
 * Example:
 * </p>
 * 
 * <pre>
 * final Attribute vitality = new Attribute();
 * vitality.set(1);
 * vitality.increase(2);
 * System.out.println(vitality.get()); // print 3
 * </pre>
 */
public final class Attribute
{
    /** Current attribute value. */
    private int value;

    /**
     * Create a new blank attribute. Initial value is set to 0 by default.
     */
    public Attribute()
    {
        value = 0;
    }

    /**
     * Set value.
     * 
     * @param value The value.
     */
    public void set(int value)
    {
        this.value = value;
    }

    /**
     * Increase attribute with the specified step.
     * 
     * @param step The increase value.
     */
    public void increase(int step)
    {
        value += step;
    }

    /**
     * Get value.
     * 
     * @return The value.
     */
    public int get()
    {
        return value;
    }
}
