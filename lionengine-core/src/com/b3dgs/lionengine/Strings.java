package com.b3dgs.lionengine;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Used to keep a single reference of a string. This way, instead of using equals(), a simple "==" is enough as same
 * strings shares same reference.
 */
public final class Strings
{
    /** Strings list. */
    private static final Map<String, String> strings = new HashMap<>(8);

    /**
     * Private constructor.
     */
    private Strings()
    {
        throw new RuntimeException();
    }

    /**
     * Get string reference from its name.
     * 
     * @param string The input string.
     * @return The string reference.
     */
    public static String getStringRef(String string)
    {
        if (!Strings.strings.containsKey(string))
        {
            Strings.strings.put(string, string);
        }
        return Strings.strings.get(string);
    }

    /**
     * Remove string reference from its name.
     * 
     * @param string The string name.
     */
    public static void removeStringRef(String string)
    {
        Strings.strings.remove(string);
    }

    /**
     * Get collection of all strings reference.
     * 
     * @return The strings reference iterator.
     */
    public static Collection<String> getStringsRefs()
    {
        return Strings.strings.values();
    }

    /**
     * Clear all references.
     */
    public static void clearStringsRef()
    {
        Strings.strings.clear();
    }
}
