/*
 * Copyright (C) 2013-2026 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.b3dgs.lionengine.graphics;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.b3dgs.lionengine.Configuration;
import com.b3dgs.lionengine.DefinitionException;
import com.b3dgs.lionengine.JsonObject;
import com.b3dgs.lionengine.JsonValue;

/**
 * Reading of the {@code templates} of a sprite sheet definition: named color mappings from key colors to target
 * colors, written {@code #RRGGBB} (opaque) or {@code #AARRGGBB}, read as {@link ColorMapping} values (ADR-0026,
 * ADR-0032, ADR-0033, ADR-0057, REQ-SHEET-003, REQ-SPRITE-005).
 */
final class SheetTemplates
{
    /** Field of the templates. */
    static final String TEMPLATES = "templates";

    /** Length of a color without alpha, {@code #RRGGBB}. */
    private static final int RGB_LENGTH = 7;
    /** Length of a color with alpha, {@code #AARRGGBB}. */
    private static final int ARGB_LENGTH = 9;
    /** Radix of the color digits. */
    private static final int HEXADECIMAL = 16;
    /** Alpha of a color written without alpha: opaque. */
    private static final int OPAQUE = 0xFF000000;
    /** First character of a color. */
    private static final char HASH = '#';
    /** Error message of an invalid key color. */
    private static final String ERROR_KEY = "Invalid key color, expected #RRGGBB or #AARRGGBB: ";
    /** Error message of an invalid target color. */
    private static final String ERROR_TARGET = "Invalid target color, expected #RRGGBB or #AARRGGBB: ";
    /** Error message of a key color given twice, in another letter case or with and without its opaque alpha. */
    private static final String ERROR_DUPLICATE = "Key color given twice: ";

    /**
     * Tells whether a text is a color, {@code #RRGGBB} or {@code #AARRGGBB}.
     *
     * @param text The text.
     * @return {@code true} if it is a color.
     */
    static boolean isColor(String text)
    {
        final int length = text.length();
        boolean color = (length == RGB_LENGTH || length == ARGB_LENGTH) && text.charAt(0) == HASH;
        for (int i = 1; color && i < length; i++)
        {
            color = Character.digit(text.charAt(i), HEXADECIMAL) >= 0;
        }
        return color;
    }

    /**
     * Returns the color of a text already checked by {@link #isColor(String)}.
     *
     * @param text The text, {@code #RRGGBB} or {@code #AARRGGBB}.
     * @return The color, packed {@code 0xAARRGGBB}; opaque for {@code #RRGGBB}.
     */
    static int parseColor(String text)
    {
        final int value = (int) Long.parseLong(text.substring(1), HEXADECIMAL);
        final int color;
        if (text.length() == RGB_LENGTH)
        {
            color = OPAQUE | value;
        }
        else
        {
            color = value;
        }
        return color;
    }

    /**
     * Reads and checks the templates of a definition, if any.
     *
     * @param sheet The configuration of the definition.
     * @param root The root object of the definition, for the names of the templates and their key colors.
     * @return The templates by name, in the order of the file, unmodifiable; empty if none.
     * @throws DefinitionException If a template is not valid.
     */
    static Map<String, ColorMapping> read(Configuration sheet, JsonObject root)
    {
        final JsonValue member = root.get(TEMPLATES);
        final Map<String, ColorMapping> result = new LinkedHashMap<>();
        if (member != null)
        {
            final Configuration templates = sheet.getObject(TEMPLATES);
            final JsonObject names = member.asObject();
            final int count = names.size();
            for (int i = 0; i < count; i++)
            {
                final String name = names.getKey(i);
                if (!Configuration.COMMENT.equals(name))
                {
                    result.put(name, readTemplate(templates.getObject(name), names.getValue(i).asObject()));
                }
            }
        }
        return Collections.unmodifiableMap(result);
    }

    /**
     * Reads and checks one template.
     *
     * @param template The configuration of the template.
     * @param mapping The object of the template, for its key colors.
     * @return The color mapping.
     * @throws DefinitionException If a color is not valid, or a key color is given twice.
     */
    private static ColorMapping readTemplate(Configuration template, JsonObject mapping)
    {
        final int count = mapping.size();
        final Set<Integer> keys = new HashSet<>();
        ColorMapping result = ColorMapping.NONE;
        for (int i = 0; i < count; i++)
        {
            final String key = mapping.getKey(i);
            if (!Configuration.COMMENT.equals(key))
            {
                result = result.with(readKey(template, key, keys), readTarget(template, key));
            }
        }
        return result;
    }

    /**
     * Reads and checks the target color of a key color, after the key color.
     *
     * @param template The configuration of the template.
     * @param key The key color, as written.
     * @return The target color, packed {@code 0xAARRGGBB}.
     * @throws DefinitionException If the target color is not valid.
     */
    private static int readTarget(Configuration template, String key)
    {
        final String target = template.getString(key);
        if (!isColor(target))
        {
            throw template.createError(key, ERROR_TARGET + target);
        }
        return parseColor(target);
    }

    /**
     * Reads and checks one key color of a template.
     *
     * @param template The configuration of the template.
     * @param key The key color, as written.
     * @param keys The key colors already read, receiving this one.
     * @return The key color, packed {@code 0xAARRGGBB}.
     * @throws DefinitionException If the key color is not valid, or was already given in another form.
     */
    private static int readKey(Configuration template, String key, Set<Integer> keys)
    {
        if (!isColor(key))
        {
            throw template.createError(key, ERROR_KEY + key);
        }
        final int color = parseColor(key);
        if (!keys.add(Integer.valueOf(color)))
        {
            throw template.createError(key, ERROR_DUPLICATE + key);
        }
        return color;
    }

    /**
     * Private constructor.
     */
    private SheetTemplates()
    {
        throw new UnsupportedOperationException();
    }
}
