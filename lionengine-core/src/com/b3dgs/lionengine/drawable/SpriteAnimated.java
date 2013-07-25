package com.b3dgs.lionengine.drawable;

import java.awt.image.BufferedImage;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.anim.AnimState;
import com.b3dgs.lionengine.anim.Animation;
import com.b3dgs.lionengine.anim.Animator;

/**
 * <p>
 * SpriteAnimated is an extended sprite, as it is now able to play it, using animations data. It works like a sprite
 * expected it renders only a part of it (animation job).
 * </p>
 * <p>
 * Animation data contains the first/last frame and the animation speed. Considering the main first frame is on the
 * top-left sprite surface, and the last frame is on the down-right sprite surface, reading it from left to right.
 * </p>
 * <p>
 * The first frame number is 1.
 * </p>
 * <p>
 * Example:
 * </p>
 * 
 * <pre>
 * // Load
 * final SpriteAnimated animation = Drawable.loadSpriteAnimated(Media.get(&quot;animation.png&quot;), 7, 1);
 * animation.load(false);
 * final Animation anim = Anim.createAnimation(4, 6, 0.125, false, true);
 * animation.play(anim);
 * 
 * // Update
 * animation.updateAnimation(extrp);
 * 
 * // Render
 * animation.setMirror(false);
 * animation.render(g, 160, 300);
 * animation.setMirror(true);
 * animation.render(g, 200, 300);
 * </pre>
 * 
 * @see Animation
 * @see AnimState
 */
public interface SpriteAnimated
        extends Sprite, Animator
{
    /**
     * Render a specific frame on graphic output at specified coordinates.
     * 
     * @param g The graphic output.
     * @param frame The frame to render (>= 1).
     * @param x The abscissa.
     * @param y The ordinate.
     */
    void render(Graphic g, int frame, int x, int y);

    /**
     * Set the mirror state.
     * 
     * @param mirror Set <code>true</code> if it is a mirror rendering (rendering is flipped), <code>false</code> if
     *            normal.
     */
    void setMirror(boolean mirror);

    /**
     * Get the number of horizontal frames.
     * 
     * @return The number of horizontal frames.
     */
    int getFramesHorizontal();

    /**
     * Get the number of vertical frames.
     * 
     * @return The number of vertical frames.
     */
    int getFramesVertical();

    /**
     * Get the number of frames.
     * 
     * @return The number of frames.
     */
    int getFramesNumber();

    /**
     * Get current frame width.
     * 
     * @return The tile width.
     */
    int getFrameWidth();

    /**
     * Get current frame height.
     * 
     * @return The tile height.
     */
    int getFrameHeight();

    /**
     * Get original frame width.
     * 
     * @return The tile width.
     */
    int getFrameWidthOriginal();

    /**
     * Get original frame height.
     * 
     * @return The tile height.
     */
    int getFrameHeightOriginal();

    /**
     * Get the representative surface of a frame.
     * 
     * @param frame The frame to get (>= 1).
     * @return The frame's surface.
     */
    BufferedImage getFrame(int frame);

    /*
     * Image
     */

    /**
     * Get instanced version of current animated sprite (shares the same surface).
     * 
     * @return The cloned animated sprite.
     */
    @Override
    SpriteAnimated instanciate();
}
