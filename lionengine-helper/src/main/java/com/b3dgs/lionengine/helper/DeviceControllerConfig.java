/*
 * Copyright (C) 2013-2021 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.InputDevice;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.XmlReader;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.io.DeviceActionModel;
import com.b3dgs.lionengine.io.DeviceAxis;
import com.b3dgs.lionengine.io.DeviceController;
import com.b3dgs.lionengine.io.DeviceControllerModel;
import com.b3dgs.lionengine.io.DeviceMapper;
import com.b3dgs.lionengine.io.DevicePointer;
import com.b3dgs.lionengine.io.DevicePush;

/**
 * Represents the input controller data.
 */
public final class DeviceControllerConfig
{
    /** Input node name. */
    public static final String NODE_INPUT = Constant.XML_PREFIX + "input";
    /** Mapping attribute. */
    public static final String ATT_MAPPING = "mapping";
    /** Device attribute. */
    public static final String NODE_DEVICE = Constant.XML_PREFIX + "device";
    /** Disabled attribute. */
    public static final String ATT_DISABLED = "disabled";
    /** Class attribute. */
    public static final String ATT_CLASS = "class";
    /** Code node name. */
    public static final String NODE_HORIZONTAL = Constant.XML_PREFIX + "horizontal";
    /** Code node name. */
    public static final String NODE_VERTICAL = Constant.XML_PREFIX + "vertical";
    /** Code node name. */
    public static final String NODE_FIRE = Constant.XML_PREFIX + "fire";
    /** Positive attribute. */
    public static final String ATT_POSITIVE = "positive";
    /** Negative attribute. */
    public static final String ATT_NEGATIVE = "negative";
    /** Index attribute. */
    public static final String ATT_INDEX = "index";

    /**
     * Create device controller from configuration.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param media The media configuration (must not be <code>null</code>).
     * @return The created controller.
     * @throws LionEngineException If unable to read node.
     */
    public static DeviceController create(Services services, Media media)
    {
        final Context context = services.get(Context.class);
        final DeviceController controller = new DeviceControllerModel();
        final Collection<DeviceControllerConfig> configs = DeviceControllerConfig.imports(services, media);

        for (final DeviceControllerConfig config : configs)
        {
            final InputDevice device = context.getInputDevice(config.getDevice());
            if (device instanceof DevicePush)
            {
                final DevicePush push = (DevicePush) device;
                config.getHorizontal().ifPresent(h -> controller.addHorizontal(push, new DeviceActionModel(h, push)));
                config.getVertical().ifPresent(v -> controller.addVertical(push, new DeviceActionModel(v, push)));

                config.getFire()
                      .entrySet()
                      .forEach(e -> e.getValue()
                                     .forEach(c -> controller.addFire(device,
                                                                      e.getKey(),
                                                                      new DeviceActionModel(c, push))));
            }
            if (device instanceof DevicePointer)
            {
                final DevicePointer pointer = (DevicePointer) device;
                controller.addHorizontal(pointer, () -> pointer.getMoveX());
                controller.addVertical(pointer, () -> -pointer.getMoveY());
            }
            if (config.isDisabled())
            {
                controller.setDisabled(device.getName(), true, true);
            }
        }
        return controller;
    }

    /**
     * Import the data from configurer.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param media The media configuration (must not be <code>null</code>).
     * @return The loaded input.
     * @throws LionEngineException If unable to read node.
     */
    @SuppressWarnings("unchecked")
    public static Collection<DeviceControllerConfig> imports(Services services, Media media)
    {
        Check.notNull(services);
        Check.notNull(media);

        final Collection<DeviceControllerConfig> configs = new ArrayList<>();
        final ClassLoader loader = services.getOptional(ClassLoader.class)
                                           .orElse(DeviceControllerConfig.class.getClassLoader());

        final Configurer configurer = new Configurer(media);
        try
        {
            final Class<Enum<? extends DeviceMapper>> mapping;
            mapping = (Class<Enum<? extends DeviceMapper>>) loader.loadClass(configurer.getString(ATT_MAPPING));

            for (final XmlReader deviceNode : configurer.getChildren(NODE_DEVICE))
            {
                final Class<DevicePush> device = (Class<DevicePush>) loader.loadClass(deviceNode.getString(ATT_CLASS));
                final boolean disabled = deviceNode.getBoolean(false, ATT_DISABLED);
                final Optional<DeviceAxis> horizontal = readAxis(deviceNode, NODE_HORIZONTAL);
                final Optional<DeviceAxis> vertical = readAxis(deviceNode, NODE_VERTICAL);
                final Map<Integer, Set<Integer>> fire = readFire(mapping, deviceNode);

                configs.add(new DeviceControllerConfig(device, disabled, horizontal, vertical, fire));
            }
        }
        catch (final ReflectiveOperationException exception)
        {
            throw new LionEngineException(exception);
        }
        return configs;
    }

    /**
     * Read fire data from node.
     * 
     * @param mapping The mapping type.
     * @param node The node parent.
     * @return The fire data.
     */
    private static Map<Integer, Set<Integer>> readFire(Class<Enum<? extends DeviceMapper>> mapping, XmlReader node)
    {
        final Map<Integer, Set<Integer>> fire = new HashMap<>();
        for (final XmlReader nodeFire : node.getChildren(NODE_FIRE))
        {
            final DeviceMapper mapper = findEnum(mapping, nodeFire.getString(ATT_INDEX));
            final Integer index = mapper.getIndex();
            final Integer positive = Integer.valueOf(nodeFire.getInteger(ATT_POSITIVE));
            Set<Integer> codes = fire.get(index);
            if (codes == null)
            {
                codes = new HashSet<>();
                fire.put(index, codes);
            }
            codes.add(positive);
        }
        return fire;
    }

    /**
     * Find corresponding enum for mapping.
     * 
     * @param <T> The enum type.
     * @param clazz The class type.
     * @param name The enum name.
     * @return The mapper instance.
     */
    private static <T extends Enum<? extends DeviceMapper>> DeviceMapper findEnum(Class<T> clazz, String name)
    {
        for (final T t : clazz.getEnumConstants())
        {
            if (t.name().equals(name))
            {
                return (DeviceMapper) t;
            }
        }
        throw new LionEngineException("Unable to find enum for " + clazz + " with " + name);
    }

    /**
     * Read axis data.
     * 
     * @param node The parent node.
     * @param nodeAxis The axis node name.
     * @return The axis data.
     */
    private static Optional<DeviceAxis> readAxis(XmlReader node, String nodeAxis)
    {
        if (node.hasNode(nodeAxis))
        {
            final XmlReader horizontal = node.getChild(nodeAxis);
            final Integer positive = Integer.valueOf(horizontal.getInteger(ATT_POSITIVE));
            final Integer negative = Integer.valueOf(horizontal.getInteger(ATT_NEGATIVE));
            return Optional.of(new DeviceAxis(positive, negative));
        }
        return Optional.empty();
    }

    /** Device class. */
    private final Class<? extends DevicePush> device;
    /** Disabled. */
    private final boolean disabled;
    /** Horizontal axis. */
    private final Optional<DeviceAxis> horizontal;
    /** Vertical axis. */
    private final Optional<DeviceAxis> vertical;
    /** Fire index mapping. */
    private final Map<Integer, Set<Integer>> fire;

    /**
     * Create a size configuration.
     * 
     * @param device The device interface.
     * @param disabled The disable flag.
     * @param horizontal The horizontal axis.
     * @param vertical The vertical axis.
     * @param fire The fire.
     */
    private DeviceControllerConfig(Class<? extends DevicePush> device,
                                   boolean disabled,
                                   Optional<DeviceAxis> horizontal,
                                   Optional<DeviceAxis> vertical,
                                   Map<Integer, Set<Integer>> fire)
    {
        super();

        this.device = device;
        this.disabled = disabled;
        this.horizontal = horizontal;
        this.vertical = vertical;
        this.fire = fire;
    }

    /**
     * Get the device interface.
     * 
     * @return The control value.
     */
    public Class<? extends InputDevice> getDevice()
    {
        return device;
    }

    /**
     * Get the disable flag.
     * 
     * @return <code>true</code> if disabled, <code>false</code> else.
     */
    public boolean isDisabled()
    {
        return disabled;
    }

    /**
     * Get the horizontal positive.
     * 
     * @return The horizontal positive.
     */
    public Optional<DeviceAxis> getHorizontal()
    {
        return horizontal;
    }

    /**
     * Get the vertical positive.
     * 
     * @return The vertical positive.
     */
    public Optional<DeviceAxis> getVertical()
    {
        return vertical;
    }

    /**
     * Get the fire.
     * 
     * @return The fire.
     */
    public Map<Integer, Set<Integer>> getFire()
    {
        return fire;
    }
}
