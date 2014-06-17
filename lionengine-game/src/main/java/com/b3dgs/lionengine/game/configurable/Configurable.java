/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.configurable;

import java.util.HashMap;
import java.util.Map;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.anim.Anim;
import com.b3dgs.lionengine.anim.Animation;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.game.Collision;
import com.b3dgs.lionengine.stream.Stream;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Represents an object which can be externally configured. When data are loaded, the object can used theses data.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class Configurable
{
    /** Prefix XML node. */
    public static final String PREFIX = "lionengine:";
    /** Surface node name. */
    public static final String SURFACE = Configurable.PREFIX + "surface";
    /** Frames node name. */
    public static final String FRAMES = Configurable.PREFIX + "frames";
    /** Size node name. */
    public static final String SIZE = Configurable.PREFIX + "size";
    /** Size in tile node name. */
    public static final String TILE_SIZE = Configurable.PREFIX + "tileSize";
    /** Offset node name. */
    public static final String OFFSET = Configurable.PREFIX + "offset";
    /** Animation node name. */
    public static final String ANIMATION = Configurable.PREFIX + "animation";
    /** Collision node name. */
    public static final String COLLISION = Configurable.PREFIX + "collision";
    /** Animation not found. */
    private static final String ERROR_ANIMATION = "Animation does not exist: ";
    /** Collision not found. */
    private static final String ERROR_COLLISION = "Collision does not exist: ";
    /** Animations map. */
    private final Map<String, Animation> animations;
    /** Collisions map. */
    private final Map<String, Collision> collisions;
    /** Root path. */
    private String path;
    /** Root node. */
    private XmlNode root;

    /**
     * Constructor.
     */
    public Configurable()
    {
        animations = new HashMap<>(0);
        collisions = new HashMap<>(0);
    }

    /**
     * Load data from configuration media.
     * 
     * @param media The xml media.
     */
    public void load(Media media)
    {
        path = media.getFile().getParent();
        root = Stream.loadXml(media);
        loadAnimations();
        loadCollisions();
    }

    /**
     * Clear the configurable data.
     */
    public void clear()
    {
        root = null;
        animations.clear();
        collisions.clear();
    }

    /**
     * Get the data root container for raw access.
     * 
     * @return The root node.
     */
    public XmlNode getRoot()
    {
        return root;
    }

    /**
     * Get the configuration directory path.
     * 
     * @return The configuration directory path.
     */
    public String getPath()
    {
        return path;
    }

    /**
     * Get the node text value.
     * 
     * @param path The node path.
     * @return The node text value.
     */
    public String getText(String... path)
    {
        final XmlNode node = getNode(path);
        return node.getText();
    }

    /**
     * Get a string in the xml tree.
     * 
     * @param attribute The attribute to get as string.
     * @param path The node path (child list)
     * @return The string value.
     */
    public String getString(String attribute, String... path)
    {
        return getNodeString(attribute, path);
    }

    /**
     * Get an integer in the xml tree.
     * 
     * @param attribute The attribute to get as integer.
     * @param path The node path (child list)
     * @return The integer value.
     */
    public int getInteger(String attribute, String... path)
    {
        return Integer.parseInt(getNodeString(attribute, path));
    }

    /**
     * Get a boolean in the xml tree.
     * 
     * @param attribute The attribute to get as boolean.
     * @param path The node path (child list)
     * @return The boolean value.
     */
    public boolean getBoolean(String attribute, String... path)
    {
        return Boolean.parseBoolean(getNodeString(attribute, path));
    }

    /**
     * Get a double in the xml tree.
     * 
     * @param attribute The attribute to get as double.
     * @param path The node path (child list)
     * @return The double value.
     */
    public double getDouble(String attribute, String... path)
    {
        return Double.parseDouble(getNodeString(attribute, path));
    }

    /**
     * Get the surface node value.
     * 
     * @return The surface node value.
     */
    public SurfaceData getSurface()
    {
        return new SurfaceData(getString("image", Configurable.SURFACE), getSurfaceIcon());
    }

    /**
     * Get the frames node value.
     * 
     * @return The frames node value.
     */
    public FramesData getFrames()
    {
        return new FramesData(getInteger("horizontal", Configurable.FRAMES),
                getInteger("vertical", Configurable.FRAMES));
    }

    /**
     * Get the size node value.
     * 
     * @return The size node value.
     */
    public SizeData getSize()
    {
        return new SizeData(getInteger("width", Configurable.SURFACE), getInteger("height", Configurable.SURFACE));
    }

    /**
     * Get the size in tile node value.
     * 
     * @return The size in tile node value.
     */
    public TileSizeData getTileSize()
    {
        return new TileSizeData(getInteger("widthInTile", Configurable.TILE_SIZE), getInteger("heightInTile",
                Configurable.TILE_SIZE));
    }

    /**
     * Get the offset node value.
     * 
     * @return The offset node value.
     */
    public OffsetData getOffset()
    {
        return new OffsetData(getInteger("x", Configurable.OFFSET), getInteger("y", Configurable.OFFSET));
    }

    /**
     * Get an animation data from its name.
     * 
     * @param name The animation name.
     * @return The animation reference.
     * @throws LionEngineException If the animation with the specified name is not found.
     */
    public Animation getAnimation(String name) throws LionEngineException
    {
        final Animation animation = animations.get(name);
        Check.notNull(animation, Configurable.ERROR_ANIMATION, name);
        return animation;
    }

    /**
     * Get all animations.
     * 
     * @return The animations list.
     */
    public Map<String, Animation> getAnimations()
    {
        return animations;
    }

    /**
     * Get a collision data from its name.
     * 
     * @param name The collision name.
     * @return The collision reference.
     * @throws LionEngineException If the collision with the specified name is not found.
     */
    public Collision getCollision(String name) throws LionEngineException
    {
        final Collision collision = collisions.get(name);
        Check.notNull(collision, Configurable.ERROR_COLLISION, name);
        return collision;
    }

    /**
     * Get all collisions.
     * 
     * @return The collisions list.
     */
    public Map<String, Collision> getCollisions()
    {
        return collisions;
    }

    /**
     * Load all animations.
     */
    private void loadAnimations()
    {
        for (final XmlNode node : root.getChildren(Configurable.ANIMATION))
        {
            final String anim = node.readString("name");
            final Animation animation = Anim.createAnimation(node.readInteger("start"), node.readInteger("end"),
                    node.readDouble("speed"), node.readBoolean("reversed"), node.readBoolean("repeat"));
            animations.put(anim, animation);
        }
    }

    /**
     * Load all collisions.
     */
    private void loadCollisions()
    {
        for (final XmlNode node : root.getChildren(Configurable.COLLISION))
        {
            final String coll = node.readString("name");
            final Collision collision = new Collision(node.readInteger("offsetX"), node.readInteger("offsetY"),
                    node.readInteger("width"), node.readInteger("height"), node.readBoolean("mirror"));
            collisions.put(coll, collision);
        }
    }

    /**
     * Get the node at the following path.
     * 
     * @param path The node path.
     * @return The node found.
     */
    private XmlNode getNode(String... path)
    {
        XmlNode node = root;
        for (final String element : path)
        {
            node = node.getChild(element);
        }
        return node;
    }

    /**
     * Get the string from a node.
     * 
     * @param attribute The attribute to get.
     * @param path The attribute node path.
     * @return The string found.
     */
    private String getNodeString(String attribute, String... path)
    {
        final XmlNode node = getNode(path);
        return node.readString(attribute);
    }

    /**
     * Get the surface icon if existing.
     * 
     * @return The surface icon, <code>null</code> if none.
     */
    private String getSurfaceIcon()
    {
        try
        {
            return getString("icon", Configurable.SURFACE);
        }
        catch (final LionEngineException exception)
        {
            return null;
        }
    }
}
