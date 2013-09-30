/*
 * Copyright (C) 2013 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
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
    private static final Map<String, String> STRINGS = new HashMap<>(8);

    /**
     * Get string reference from its name.
     * 
     * @param string The input string.
     * @return The string reference.
     */
    public static String getStringRef(String string)
    {
        if (!Strings.STRINGS.containsKey(string))
        {
            Strings.STRINGS.put(string, string);
        }
        return Strings.STRINGS.get(string);
    }

    /**
     * Remove string reference from its name.
     * 
     * @param string The string name.
     */
    public static void removeStringRef(String string)
    {
        Strings.STRINGS.remove(string);
    }

    /**
     * Get collection of all strings reference.
     * 
     * @return The strings reference iterator.
     */
    public static Collection<String> getStringsRefs()
    {
        return Strings.STRINGS.values();
    }

    /**
     * Clear all references.
     */
    public static void clearStringsRef()
    {
        Strings.STRINGS.clear();
    }

    /**
     * Private constructor.
     */
    private Strings()
    {
        throw new RuntimeException();
    }
}
