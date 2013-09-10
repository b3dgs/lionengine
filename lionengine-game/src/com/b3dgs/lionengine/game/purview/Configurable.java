package com.b3dgs.lionengine.game.purview;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.anim.Animation;
import com.b3dgs.lionengine.file.XmlNode;
import com.b3dgs.lionengine.game.CollisionData;

/**
 * Purview representing an object which can be externally configured. When data are loaded, the object can used
 * internally theses data.
 */
public interface Configurable
{
    /**
     * Load data from configuration media.
     * 
     * @param media The xml media.
     */
    void loadData(Media media);

    /**
     * Get the data root container for raw access.
     * 
     * @return The root node.
     */
    XmlNode getDataRoot();

    /**
     * Get a string in the xml tree.
     * 
     * @param attribute The attribute to get as string.
     * @param path The node path (child list)
     * @return The string value.
     */
    String getDataString(String attribute, String... path);

    /**
     * Get an integer in the xml tree.
     * 
     * @param attribute The attribute to get as integer.
     * @param path The node path (child list)
     * @return The integer value.
     */
    int getDataInteger(String attribute, String... path);

    /**
     * Get a boolean in the xml tree.
     * 
     * @param attribute The attribute to get as boolean.
     * @param path The node path (child list)
     * @return The boolean value.
     */
    boolean getDataBoolean(String attribute, String... path);

    /**
     * Get a double in the xml tree.
     * 
     * @param attribute The attribute to get as double.
     * @param path The node path (child list)
     * @return The double value.
     */
    double getDataDouble(String attribute, String... path);

    /**
     * Get an animation data from its name.
     * 
     * @param name The animation name.
     * @return The animation reference.
     */
    Animation getDataAnimation(String name);

    /**
     * Get a collision data from its name.
     * 
     * @param name The collision name.
     * @return The collision reference.
     */
    CollisionData getDataCollision(String name);
}
