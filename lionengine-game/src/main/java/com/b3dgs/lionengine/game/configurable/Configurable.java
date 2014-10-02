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
    /** Class node name. */
    public static final String CLASS = Configurable.PREFIX + "class";
    /** Surface node name. */
    public static final String SURFACE = Configurable.PREFIX + "surface";
    /** Surface image node. */
    public static final String SURFACE_IMAGE = "image";
    /** Surface icon node. */
    public static final String SURFACE_ICON = "icon";
    /** Frames node name. */
    public static final String FRAMES = Configurable.PREFIX + "frames";
    /** Frames horizontal node name. */
    public static final String FRAMES_HORIZONTAL = "horizontal";
    /** Frames vertical node name. */
    public static final String FRAMES_VERTICAL = "vertical";
    /** Size node name. */
    public static final String SIZE = Configurable.PREFIX + "size";
    /** Offset node name. */
    public static final String OFFSET = Configurable.PREFIX + "offset";
    /** Animation node name. */
    public static final String ANIMATION = Configurable.PREFIX + "animation";
    /** Collision node name. */
    public static final String COLLISION = Configurable.PREFIX + "collision";
    /** Animations map. */
    private final Map<String, Animation> animations;
    /** Collisions map. */
    private final Map<String, Collision> collisions;
    /** Media reference. */
    private Media media;
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
     * @throws LionEngineException If error when opening the media.
     */
    public void load(Media media) throws LionEngineException
    {
        Check.notNull(media);

        this.media = media;
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
     * @throws LionEngineException If unable to read node.
     */
    public String getText(String... path) throws LionEngineException
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
     * @throws LionEngineException If unable to read node.
     */
    public String getString(String attribute, String... path) throws LionEngineException
    {
        return getNodeString(attribute, path);
    }

    /**
     * Get a boolean in the xml tree.
     * 
     * @param attribute The attribute to get as boolean.
     * @param path The node path (child list)
     * @return The boolean value.
     * @throws LionEngineException If unable to read node.
     */
    public boolean getBoolean(String attribute, String... path) throws LionEngineException
    {
        return Boolean.parseBoolean(getNodeString(attribute, path));
    }

    /**
     * Get an integer in the xml tree.
     * 
     * @param attribute The attribute to get as integer.
     * @param path The node path (child list)
     * @return The integer value.
     * @throws LionEngineException If unable to read node or not a valid integer.
     */
    public int getInteger(String attribute, String... path) throws LionEngineException
    {
        try
        {
            return Integer.parseInt(getNodeString(attribute, path));
        }
        catch (final NumberFormatException exception)
        {
            throw new LionEngineException(exception);
        }
    }

    /**
     * Get a double in the xml tree.
     * 
     * @param attribute The attribute to get as double.
     * @param path The node path (child list)
     * @return The double value.
     * @throws LionEngineException If unable to read node.
     */
    public double getDouble(String attribute, String... path) throws LionEngineException
    {
        try
        {
            return Double.parseDouble(getNodeString(attribute, path));
        }
        catch (final NumberFormatException exception)
        {
            throw new LionEngineException(exception);
        }
    }

    /**
     * Get the class name node value.
     * 
     * @return The class name node value.
     * @throws LionEngineException If unable to read node.
     */
    public String getClassName() throws LionEngineException
    {
        return getText(Configurable.CLASS);
    }

    /**
     * Get the surface node value.
     * 
     * @return The surface node value.
     * @throws LionEngineException If unable to read node.
     */
    public SurfaceData getSurface() throws LionEngineException
    {
        return new SurfaceData(getString(Configurable.SURFACE_IMAGE, Configurable.SURFACE), getSurfaceIcon());
    }

    /**
     * Check if a surface is defined.
     * 
     * @return <code>true</code> if has surface, <code>false</code> else.
     */
    public boolean hasSurface()
    {
        try
        {
            return getString(Configurable.SURFACE_IMAGE, Configurable.SURFACE) != null;
        }
        catch (final LionEngineException exception)
        {
            return false;
        }
    }

    /**
     * Get the frames node value.
     * 
     * @return The frames node value.
     * @throws LionEngineException If unable to read node or not a valid integer.
     */
    public FramesData getFrames() throws LionEngineException
    {
        return new FramesData(getInteger(Configurable.FRAMES_HORIZONTAL, Configurable.FRAMES), getInteger(
                Configurable.FRAMES_VERTICAL, Configurable.FRAMES));
    }

    /**
     * Check if a surface is defined.
     * 
     * @return <code>true</code> if has surface, <code>false</code> else.
     */
    public boolean hasFrames()
    {
        try
        {
            return getInteger(Configurable.FRAMES_HORIZONTAL, Configurable.FRAMES) > 0
                    && getInteger(Configurable.FRAMES_VERTICAL, Configurable.FRAMES) > 0;
        }
        catch (final LionEngineException exception)
        {
            return false;
        }
    }

    /**
     * Get the size node value.
     * 
     * @return The size node value.
     * @throws LionEngineException If unable to read node or not a valid integer.
     */
    public SizeData getSize() throws LionEngineException
    {
        return new SizeData(getInteger("width", Configurable.SIZE), getInteger("height", Configurable.SIZE));
    }

    /**
     * Get the offset node value.
     * 
     * @return The offset node value.
     * @throws LionEngineException If unable to read node or not a valid integer.
     */
    public OffsetData getOffset() throws LionEngineException
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
        Check.notNull(animation);
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
        Check.notNull(collision);
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
     * 
     * @throws LionEngineException If unable to read animation.
     */
    private void loadAnimations() throws LionEngineException
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
     * 
     * @throws LionEngineException If unable to read collision.
     */
    private void loadCollisions() throws LionEngineException
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
     * @throws LionEngineException If node not found.
     */
    private XmlNode getNode(String... path) throws LionEngineException
    {
        XmlNode node = root;
        for (final String element : path)
        {
            try
            {
                node = node.getChild(element);
            }
            catch (final LionEngineException exception)
            {
                throw new LionEngineException(exception, media);
            }
        }
        return node;
    }

    /**
     * Get the string from a node.
     * 
     * @param attribute The attribute to get.
     * @param path The attribute node path.
     * @return The string found.
     * @throws LionEngineException If nod not found.
     */
    private String getNodeString(String attribute, String... path) throws LionEngineException
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
