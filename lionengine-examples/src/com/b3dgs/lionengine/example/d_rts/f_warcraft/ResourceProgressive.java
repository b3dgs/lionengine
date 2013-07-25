package com.b3dgs.lionengine.example.d_rts.f_warcraft;

import com.b3dgs.lionengine.game.Alterable;
import com.b3dgs.lionengine.utility.UtilityMath;

/**
 * Progressive resource implementation.
 */
public final class ResourceProgressive
        extends Alterable
{
    /** Current real value. */
    private double value;

    /**
     * Constructor.
     * 
     * @param amount Initial amount of resource.
     */
    public ResourceProgressive(int amount)
    {
        super(Integer.MAX_VALUE);
        set(amount);
        value = amount;
    }

    /**
     * Update progressive value effect.
     * 
     * @param extrp extrapolation reference.
     * @param speed progressive speed.
     */
    public void update(double extrp, double speed)
    {
        value = UtilityMath.curveValue(value, super.getCurrent(), speed / extrp);
        if (value >= super.getCurrent() - 0.1 && value <= super.getCurrent() + 0.1)
        {
            value = super.getCurrent();
        }
    }

    /**
     * Get current progressive value.
     * 
     * @return The progressive value.
     */
    @Override
    public int getCurrent()
    {
        return (int) value;
    }
}
