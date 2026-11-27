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
package com.b3dgs.lionengine.examples.graphics;

import java.util.List;

import com.b3dgs.lionengine.graphics.Animation;
import com.b3dgs.lionengine.graphics.AnimationLoop;
import com.b3dgs.lionengine.graphics.ImageCache;
import com.b3dgs.lionengine.graphics.SheetGroup;
import com.b3dgs.lionengine.graphics.SheetLoader;
import com.b3dgs.lionengine.graphics.SpriteSheet;
import com.b3dgs.lionengine.render.TextureStore;
import com.b3dgs.lionengine.resources.ResourceKey;
import com.b3dgs.lionengine.resources.Resources;

/**
 * Reads the animations of a sprite sheet from its definition file, and builds another one in code
 * (REQ-ANIM-001).
 *
 * <p>
 * The definition {@code sheets/hero.json} names the animations of the hero, each with its frames (cells of the
 * sheet), a frame duration in seconds, optional durations per frame overriding it, and a loop mode:
 * </p>
 *
 * <pre>
 * {
 *   "format": "lionengine.sheet",
 *   "version": 1,
 *   "image": "sprites/hero.png",
 *   "cellWidth": 16,
 *   "cellHeight": 16,
 *   "animations": {
 *     "walk": {"frames": [0, 1, 2, 3], "frameDuration": 0.1, "loop": "loop"},
 *     "attack": {"frames": [4, 5, 6], "frameDuration": 0.1, "frameDurations": [0.05, 0.05, 0.3], "loop": "once"}
 *   }
 * }
 * </pre>
 *
 * <p>
 * The {@link SheetLoader} checks every animation, its frames against the cells of the sheet included, and the sheet
 * keeps them as {@link Animation} values: {@link SpriteSheet#getAnimationNames()} in the order of the file,
 * {@link SpriteSheet#getAnimation(String)} by name. A game can also build an animation in code, here an idle
 * animation playing forward then backward. An animation only describes; it is played by the animator, which advances
 * it with the update step.
 * </p>
 *
 * @since 11.0.0
 */
public final class AnimationDefinitionExample
{
    /** Key of the hero sheet definition. */
    public static final ResourceKey HERO = new ResourceKey("sheets/hero.json");
    /** The idle animation, built in code: the first cell then the second, 0.5 seconds each, forward then backward. */
    public static final Animation IDLE = new Animation(new int[]
    {
        0, 7
    }, 0.5, AnimationLoop.PING_PONG);

    /**
     * Private constructor.
     */
    private AnimationDefinitionExample()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Loads {@link #HERO}, reads its animations, and releases the group.
     *
     * @param resources The resources holding the definition and the image.
     * @param textures The texture store of the render backend.
     * @return What was read from the animations.
     */
    public static Result run(Resources resources, TextureStore textures)
    {
        final SheetGroup group = new SheetGroup(new ImageCache(resources, textures));
        final SpriteSheet hero = new SheetLoader(resources).load(group, HERO);

        final Animation walk = hero.getAnimation("walk");
        final Animation attack = hero.getAnimation("attack");
        final Result result = new Result(hero.getAnimationNames(),
                                         walk.getFrameCount(),
                                         walk.getLoop(),
                                         attack.getFrameDuration(attack.getFrameCount() - 1),
                                         IDLE.getFrame(1) < hero.getCellCount());
        group.release();
        return result;
    }

    /**
     * Result of the example.
     *
     * @param names The names of the animations of the definition, in the order of the file.
     * @param walkFrames The number of frames of the walk.
     * @param walkLoop The loop mode of the walk.
     * @param attackLast The duration of the last frame of the attack in seconds, from its frame durations.
     * @param idleFits {@code true} if the cells of the idle animation built in code are cells of the sheet.
     * @since 11.0.0
     */
    public record Result(List<String> names,
                         int walkFrames,
                         AnimationLoop walkLoop,
                         double attackLast,
                         boolean idleFits)
    {
        // Value type of the example.
    }
}
