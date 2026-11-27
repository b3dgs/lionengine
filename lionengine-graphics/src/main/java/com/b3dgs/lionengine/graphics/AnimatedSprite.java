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

import com.b3dgs.lionengine.render.CommandBuffer;

/**
 * Sprite whose frame is played by an animation, driven by the simulation update step (ADR-0003, ADR-0026, ADR-0058,
 * REQ-ANIM-003, REQ-SPR-002).
 *
 * <p>
 * An animated sprite joins a {@link Sprite}, which keeps its transform, colors and layer, and an {@link Animator},
 * which keeps the playback state. {@link #play(Animation)}, {@link #pause()}, {@link #resume()} and {@link #stop()}
 * follow the animator (ADR-0058); {@link #play(Animation)} also checks that every frame of the animation is a frame of
 * the sprite, so an animation built in code cannot point outside its sheet.
 * </p>
 *
 * <p>
 * Time: the game calls {@link #update(double)} once per update step with the constant step in seconds; it advances
 * the animation and gives its frame to the sprite. The render phase calls {@link #render(CommandBuffer)} or
 * {@link #render(CommandBuffer, double, double)}, which only read: an interpolated or extrapolated position at render
 * time never changes the frame or the time of the animation (REQ-LOOP-006).
 * </p>
 *
 * <p>
 * The animated sprite holds playback state only: the image stays owned by its acquirer, as for the sprite. Nothing
 * allocates after construction. Runtime thread only, not thread-safe (ADR-0018).
 * </p>
 *
 * @since 11.0.0
 */
public final class AnimatedSprite
{
    /** The sprite. */
    private final Sprite sprite;
    /** The animator. */
    private final Animator animator = new Animator();

    /**
     * Creates an animated sprite, stopped, without animation; the sprite keeps its frame until the first play.
     *
     * @param sprite The sprite, usually over a sprite sheet whose cells are the frames (never {@code null}).
     * @throws IllegalArgumentException If the sprite is {@code null}.
     */
    public AnimatedSprite(Sprite sprite)
    {
        super();

        ImageCache.checkNotNull(sprite, "Sprite");
        this.sprite = sprite;
    }

    /**
     * Returns the sprite, to move, scale, rotate, color or layer it in the update phase.
     *
     * @return The sprite.
     */
    public Sprite getSprite()
    {
        return sprite;
    }

    /**
     * Plays an animation from its first frame, in every state, and shows that frame.
     *
     * @param animation The animation, every frame a frame of the sprite (never {@code null}).
     * @throws IllegalArgumentException If the animation is {@code null}, or a frame is not a frame of the sprite;
     *             nothing changes then.
     */
    public void play(Animation animation)
    {
        ImageCache.checkNotNull(animation, "Animation");
        final int count = sprite.getFrameCount();
        final int frames = animation.getFrameCount();
        for (int i = 0; i < frames; i++)
        {
            if (animation.getFrame(i) >= count)
            {
                throw new IllegalArgumentException("Animation frame outside the sprite of "
                                                   + count
                                                   + " frames: "
                                                   + animation.getFrame(i));
            }
        }
        animator.play(animation);
        sprite.setFrame(animator.getFrame());
    }

    /**
     * Pauses a playing animation, keeping the frame and the time spent in it.
     *
     * @throws IllegalStateException If the animation is not playing.
     */
    public void pause()
    {
        animator.pause();
    }

    /**
     * Resumes a paused animation, from the frame and the time kept by the pause.
     *
     * @throws IllegalStateException If the animation is not paused.
     */
    public void resume()
    {
        animator.resume();
    }

    /**
     * Stops the animation and shows its first frame, in every state; without animation, changes nothing.
     */
    public void stop()
    {
        animator.stop();
        if (animator.getAnimation() != null)
        {
            sprite.setFrame(animator.getFrame());
        }
    }

    /**
     * Advances a playing animation by one update step and shows its frame; changes nothing in another state.
     * Allocates nothing.
     *
     * @param stepSeconds The update step in seconds, zero or positive and finite.
     * @throws IllegalArgumentException If the step is negative, infinite or not a number.
     */
    public void update(double stepSeconds)
    {
        animator.update(stepSeconds);
        if (animator.getAnimation() != null)
        {
            sprite.setFrame(animator.getFrame());
        }
    }

    /**
     * Returns the state of the animation.
     *
     * @return The state, {@link AnimatorState#STOPPED} at creation.
     */
    public AnimatorState getState()
    {
        return animator.getState();
    }

    /**
     * Returns the animation.
     *
     * @return The animation of the last play, {@code null} before the first play.
     */
    public Animation getAnimation()
    {
        return animator.getAnimation();
    }

    /**
     * Returns the index of the current frame in the animation.
     *
     * @return The index, from {@code 0}; {@code 0} before the first play.
     */
    public int getFrameIndex()
    {
        return animator.getFrameIndex();
    }

    /**
     * Returns the time spent in the current frame.
     *
     * @return The time in seconds.
     */
    public double getFrameTime()
    {
        return animator.getFrameTime();
    }

    /**
     * Adds the render command of the current frame at the position of the sprite; reads only.
     *
     * @param commands The command buffer of the frame (never {@code null}).
     * @throws IllegalArgumentException If the buffer is {@code null}.
     * @throws IllegalStateException If the image of the sprite is released.
     */
    public void render(CommandBuffer commands)
    {
        sprite.render(commands);
    }

    /**
     * Adds the render command of the current frame at a position computed for the frame, such as one interpolated
     * between two steps, without changing the sprite or the animation.
     *
     * @param commands The command buffer of the frame (never {@code null}).
     * @param x The horizontal position of the pivot in pixels of the logical frame (finite).
     * @param y The vertical position of the pivot in pixels of the logical frame (finite).
     * @throws IllegalArgumentException If the buffer is {@code null} or a position is not finite.
     * @throws IllegalStateException If the image of the sprite is released.
     */
    public void render(CommandBuffer commands, double x, double y)
    {
        sprite.render(commands, x, y);
    }
}
