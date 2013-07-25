package com.b3dgs.lionengine;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Used to keep a single reference of a string. This way, instead of using equals(), a simple "==" is enough as same
 * strings shares same reference.
 * <p>
 * Example:
 * </p>
 * 
 * <pre>
 * final String str = &quot;test&quot;;
 * final String str1 = Strings.getStringRef(str);
 * final String str2 = Strings.getStringRef(str);
 * 
 * Assert.assertTrue(!Strings.getStringsRefs().isEmpty());
 * Assert.assertTrue(str1 == str2);
 * Assert.assertEquals(str1, str2);
 * 
 * for (final String string : Strings.getStringsRefs())
 * {
 *     Assert.assertTrue(string == str);
 * }
 * 
 * Strings.removeStringRef(str);
 * Strings.clearStringsRef();
 * Assert.assertTrue(Strings.getStringsRefs().isEmpty());
 * </pre>
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
