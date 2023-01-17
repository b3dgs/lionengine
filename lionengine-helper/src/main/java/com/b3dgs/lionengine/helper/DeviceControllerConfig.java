/*
 * Copyright (C) 2013-2022 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.IntFunction;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.InputDevice;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Xml;
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
    /** Id attribute. */
    public static final String ATT_ID = "id";
    /** Name attribute. */
    public static final String ATT_NAME = "name";
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
     * @param indexes The allowed devices indexes (not defined for all).
     * @return The created controller.
     * @throws LionEngineException If unable to read node.
     */
    public static DeviceController create(Services services, Media media, int... indexes)
    {
        final DeviceController controller = new DeviceControllerModel();
        final Collection<DeviceControllerConfig> configs = DeviceControllerConfig.imports(services, media);

        for (final DeviceControllerConfig config : configs)
        {
            if (isAllowed(indexes, config.getIndex()))
            {
                final InputDevice device = getDevice(services, config.getDevice());
                if (device != null)
                {
                    create(config, device, controller);
                }
            }
        }
        return controller;
    }

    private static void create(DeviceControllerConfig config, InputDevice device, DeviceController controller)
    {
        final InputDevice current = device.getCurrent(config.getId());
        if (current instanceof DevicePush)
        {
            final DevicePush push = (DevicePush) current;
            config.getHorizontal().forEach(h -> controller.addHorizontal(push, new DeviceActionModel(h, push)));
            config.getVertical().forEach(v -> controller.addVertical(push, new DeviceActionModel(v, push)));

            config.getFire()
                  .entrySet()
                  .forEach(e -> e.getValue()
                                 .forEach(c -> controller.addFire(current,
                                                                  e.getKey(),
                                                                  new DeviceActionModel(c, push))));
        }
        if (current instanceof DevicePointer)
        {
            final DevicePointer pointer = (DevicePointer) current;
            controller.addHorizontal(pointer, () -> pointer.getMoveX());
            controller.addVertical(pointer, () -> -pointer.getMoveY());
        }
        if (config.isDisabled())
        {
            controller.setDisabled(current.getName(), true, true);
        }
    }

    // CHECKSTYLE IGNORE LINE: ReturnCount
    private static boolean isAllowed(int[] indexes, int index)
    {
        if (indexes.length == 0)
        {
            return true;
        }
        for (int i = 0; i < indexes.length; i++)
        {
            if (index == indexes[i])
            {
                return true;
            }
        }
        return false;
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
                final int index = deviceNode.getInteger(ATT_INDEX);
                final int id = deviceNode.getInteger(0, ATT_ID);
                final Class<DevicePush> device = (Class<DevicePush>) loader.loadClass(deviceNode.getString(ATT_CLASS));
                final String name = deviceNode.getString(ATT_NAME);
                final boolean disabled = deviceNode.getBoolean(false, ATT_DISABLED);
                final List<DeviceAxis> horizontal = readAxis(deviceNode, NODE_HORIZONTAL);
                final List<DeviceAxis> vertical = readAxis(deviceNode, NODE_VERTICAL);
                final Map<Integer, Set<Integer>> fire = readFire(mapping, deviceNode);

                configs.add(new DeviceControllerConfig(name, index, id, device, disabled, horizontal, vertical, fire));
            }
        }
        catch (final ReflectiveOperationException exception)
        {
            throw new LionEngineException(exception);
        }
        return configs;
    }

    /**
     * Export the data to file.
     * 
     * @param configs The config reference (must not be <code>null</code>).
     * @param media The media configuration (must not be <code>null</code>).
     * @param mapper The mapper reference.
     * @param toEnum The mapper converter reference.
     * @throws LionEngineException If unable to read node.
     */
    public static void exports(Collection<DeviceControllerConfig> configs,
                               Media media,
                               Class<? extends Enum<? extends DeviceMapper>> mapper,
                               IntFunction<String> toEnum)
    {
        Check.notNull(configs);
        Check.notNull(media);

        final Xml xml = new Xml(NODE_INPUT);
        xml.writeString(ATT_MAPPING, mapper.getName());

        for (final DeviceControllerConfig config : configs)
        {
            final Xml nodeDevice = xml.createChild(NODE_DEVICE);
            nodeDevice.writeInteger(ATT_INDEX, config.getIndex());
            nodeDevice.writeInteger(ATT_ID, config.getId());
            nodeDevice.writeString(ATT_CLASS, config.getDevice().getName());
            nodeDevice.writeString(ATT_NAME, config.getName());
            if (config.isDisabled())
            {
                nodeDevice.writeBoolean(ATT_DISABLED, config.isDisabled());
            }
            final Xml horizontal = nodeDevice.createChild(NODE_HORIZONTAL);
            for (final DeviceAxis axis : config.getHorizontal())
            {
                horizontal.writeInteger(ATT_POSITIVE, axis.getPositive().intValue());
                horizontal.writeInteger(ATT_NEGATIVE, axis.getNegative().intValue());
            }
            final Xml vertical = nodeDevice.createChild(NODE_VERTICAL);
            for (final DeviceAxis axis : config.getVertical())
            {
                vertical.writeInteger(ATT_POSITIVE, axis.getPositive().intValue());
                vertical.writeInteger(ATT_NEGATIVE, axis.getNegative().intValue());
            }
            for (final Entry<Integer, Set<Integer>> axis : config.getFire().entrySet())
            {
                // CHECKSTYLE IGNORE LINE: NestedForDepth
                for (final Integer positive : axis.getValue())
                {
                    final Xml fire = nodeDevice.createChild(NODE_FIRE);
                    fire.writeString(ATT_INDEX, toEnum.apply(axis.getKey().intValue()));
                    fire.writeInteger(ATT_POSITIVE, positive.intValue());
                }
            }
        }
        xml.save(media);
    }

    /**
     * Get device from available source.
     * 
     * @param services The services reference.
     * @param clazz The device type.
     * @return The device reference.
     */
    private static InputDevice getDevice(Services services, Class<? extends InputDevice> clazz)
    {
        final Context context = services.getOptional(Context.class).orElse(null);
        if (context != null)
        {
            final InputDevice device = context.getInputDevice(clazz);
            return device;
        }
        return services.get(clazz);
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
    private static List<DeviceAxis> readAxis(XmlReader node, String nodeAxis)
    {
        final List<DeviceAxis> axis = new ArrayList<>();
        final Collection<XmlReader> children = node.getChildren(nodeAxis);
        for (final XmlReader child : children)
        {
            final Integer positive = Integer.valueOf(child.getInteger(ATT_POSITIVE));
            final Integer negative = Integer.valueOf(child.getInteger(ATT_NEGATIVE));
            axis.add(new DeviceAxis(positive, negative));
        }
        return axis;
    }

    /** Device name. */
    private final String name;
    /** Device index. */
    private final int index;
    /** Device id. */
    private final int id;
    /** Device class. */
    private final Class<? extends InputDevice> device;
    /** Disabled. */
    private final boolean disabled;
    /** Horizontal axis. */
    private final List<DeviceAxis> horizontal;
    /** Vertical axis. */
    private final List<DeviceAxis> vertical;
    /** Fire index mapping. */
    private final Map<Integer, Set<Integer>> fire;

    /**
     * Create a size configuration.
     * 
     * @param name The device name.
     * @param index The device index.
     * @param id The device id.
     * @param device The device interface.
     * @param disabled The disable flag.
     * @param horizontal The horizontal axis.
     * @param vertical The vertical axis.
     * @param fire The fire.
     */
    public DeviceControllerConfig(String name,
                                  int index,
                                  int id,
                                  Class<? extends InputDevice> device,
                                  boolean disabled,
                                  List<DeviceAxis> horizontal,
                                  List<DeviceAxis> vertical,
                                  Map<Integer, Set<Integer>> fire)
    {
        super();

        this.name = name;
        this.index = index;
        this.id = id;
        this.device = device;
        this.disabled = disabled;
        this.horizontal = horizontal;
        this.vertical = vertical;
        this.fire = fire;
    }

    /**
     * Get the device name.
     * 
     * @return The device name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Get the device index.
     * 
     * @return The device index.
     */
    public int getIndex()
    {
        return index;
    }

    /**
     * Get the device id.
     * 
     * @return The device id.
     */
    public int getId()
    {
        return id;
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
    public List<DeviceAxis> getHorizontal()
    {
        return horizontal;
    }

    /**
     * Get the vertical positive.
     * 
     * @return The vertical positive.
     */
    public List<DeviceAxis> getVertical()
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
