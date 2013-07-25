package com.b3dgs.lionengine.swing;

/**
 * Can enable interface for action combo.
 */
public interface CanEnable
{
    /**
     * Set the enabled flag.
     * 
     * @param isEnable <code>true</code> if enabled, <code>false</code> else.
     */
    void setEnabled(boolean isEnable);

    /**
     * Get the enabled flag.
     * 
     * @return <code>true</code> if enabled, <code>false</code> else.
     */
    boolean isEnabled();
}
