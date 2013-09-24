package com.b3dgs.lionengine.swing;

/**
 * Combo item class.
 */
public final class ComboItem
        implements CanEnable
{
    /**
     * Convert an array of objects to an array of combo item.
     * 
     * @param objects The objects array.
     * @return The items.
     */
    public static ComboItem[] get(Object[] objects)
    {
        final ComboItem[] items = new ComboItem[objects.length];
        for (int i = 0; i < items.length; i++)
        {
            items[i] = new ComboItem(objects[i]);
        }
        return items;
    }

    /** Object reference. */
    private final Object obj;
    /** Enabled flag. */
    private boolean isEnable;

    /**
     * Constructor.
     * 
     * @param obj The object reference.
     * @param isEnable The enabled flag.
     */
    public ComboItem(Object obj, boolean isEnable)
    {
        this.obj = obj;
        this.isEnable = isEnable;
    }

    /**
     * Constructor.
     * 
     * @param obj The object reference.
     */
    public ComboItem(Object obj)
    {
        this(obj, true);
    }

    /**
     * Get the object.
     * 
     * @return The object.
     */
    public Object getObject()
    {
        return obj;
    }

    /*
     * CanEnable
     */

    @Override
    public boolean isEnabled()
    {
        return isEnable;
    }

    @Override
    public void setEnabled(boolean isEnable)
    {
        this.isEnable = isEnable;
    }

    @Override
    public String toString()
    {
        return obj.toString();
    }
}
