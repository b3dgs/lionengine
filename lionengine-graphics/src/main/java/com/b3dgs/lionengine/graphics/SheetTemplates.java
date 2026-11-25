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

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import com.b3dgs.lionengine.Configuration;
import com.b3dgs.lionengine.DefinitionException;
import com.b3dgs.lionengine.JsonObject;

/**
 * Check of the {@code templates} of a sprite sheet definition: named color mappings from key colors to target colors,
 * written {@code #RRGGBB} or {@code #AARRGGBB} (ADR-0026, ADR-0032, ADR-0033, REQ-SHEET-003, REQ-SPRITE-005).
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
    /** First character of a color. */
    private static final char HASH = '#';
    /** Error message of an invalid key color. */
    private static final String ERROR_KEY = "Invalid key color, expected #RRGGBB or #AARRGGBB: ";
    /** Error message of an invalid target color. */
    private static final String ERROR_TARGET = "Invalid target color, expected #RRGGBB or #AARRGGBB: ";
    /** Error message of a key color given twice, in another letter case. */
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
     * Reads and checks the templates of a definition, if any.
     *
     * @param sheet The configuration of the definition.
     * @param root The root object of the definition, for the names of the templates and their key colors.
     * @throws DefinitionException If a template is not valid.
     */
    static void read(Configuration sheet, JsonObject root)
    {
        final Configuration templates = sheet.getObject(TEMPLATES, null);
        if (templates != null)
        {
            final JsonObject names = root.get(TEMPLATES).asObject();
            final int count = names.size();
            for (int i = 0; i < count; i++)
            {
                final String name = names.getKey(i);
                if (!Configuration.COMMENT.equals(name))
                {
                    readTemplate(templates.getObject(name), names.getValue(i).asObject());
                }
            }
        }
    }

    /**
     * Reads and checks one template.
     *
     * @param template The configuration of the template.
     * @param mapping The object of the template, for its key colors.
     * @throws DefinitionException If a color is not valid.
     */
    private static void readTemplate(Configuration template, JsonObject mapping)
    {
        final int count = mapping.size();
        final Set<String> colors = new HashSet<>();
        for (int i = 0; i < count; i++)
        {
            final String key = mapping.getKey(i);
            if (!Configuration.COMMENT.equals(key))
            {
                if (!colors.add(key.toUpperCase(Locale.ROOT)))
                {
                    throw template.createError(key, ERROR_DUPLICATE + key);
                }
                final String target = template.getString(key);
                if (!isColor(key))
                {
                    throw template.createError(key, ERROR_KEY + key);
                }
                if (!isColor(target))
                {
                    throw template.createError(key, ERROR_TARGET + target);
                }
            }
        }
    }

    /**
     * Private constructor.
     */
    private SheetTemplates()
    {
        throw new UnsupportedOperationException();
    }
}
