/*
 * Copyright (C) 2013-2020 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.helper;

import java.util.HashMap;
import java.util.Map;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.io.InputDeviceControl;

/**
 * Represents the input controller data.
 */
public final class InputControllerConfig
{
    /** Input node name. */
    public static final String NODE_INPUT = Constant.XML_PREFIX + "input";
    /** Input width attribute. */
    public static final String ATT_CLASS = "class";
    /** Code node name. */
    public static final String NODE_CODE = Constant.XML_PREFIX + "code";
    /** Code index attribute. */
    public static final String ATT_INDEX = "index";
    /** Code value attribute. */
    public static final String ATT_VALUE = "value";

    /**
     * Import the data from configurer.
     * 
     * @param configurer The configurer reference (must not be <code>null</code>).
     * @return The size data.
     * @throws LionEngineException If unable to read node.
     */
    public static InputControllerConfig imports(Configurer configurer)
    {
        Check.notNull(configurer);

        return imports(configurer.getRoot());
    }

    /**
     * Import the data from configurer.
     * 
     * @param root The root reference (must not be <code>null</code>).
     * @return The data.
     * @throws LionEngineException If unable to read node.
     */
    public static InputControllerConfig imports(Xml root)
    {
        Check.notNull(root);

        final Xml node = root.getChild(NODE_INPUT);
        final String clazz = node.readString(ATT_CLASS);

        final Map<Integer, Integer> codes = new HashMap<>();
        for (final Xml code : node.getChildren(NODE_CODE))
        {
            codes.put(Integer.valueOf(code.readInteger(ATT_INDEX)), Integer.valueOf(code.readInteger(ATT_VALUE)));
        }

        try
        {
            @SuppressWarnings("unchecked")
            final Class<InputDeviceControl> control = (Class<InputDeviceControl>) Class.forName(clazz);

            return new InputControllerConfig(control, codes);
        }
        catch (final ReflectiveOperationException exception)
        {
            throw new LionEngineException(exception);
        }
    }

    /** The device value. */
    private final Class<? extends InputDeviceControl> control;
    /** The height value. */
    private final Map<Integer, Integer> codes;

    /**
     * Create a size configuration.
     * 
     * @param control The control value.
     * @param codes The codes value.
     */
    public InputControllerConfig(Class<? extends InputDeviceControl> control, Map<Integer, Integer> codes)
    {
        super();

        this.control = control;
        this.codes = codes;
    }

    /**
     * Get the control value.
     * 
     * @return The control value.
     */
    public Class<? extends InputDeviceControl> getControl()
    {
        return control;
    }

    /**
     * Get the codes value.
     * 
     * @return The codes value.
     */
    public Map<Integer, Integer> getCodes()
    {
        return codes;
    }
}
