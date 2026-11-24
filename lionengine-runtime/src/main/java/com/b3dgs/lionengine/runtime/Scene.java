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
package com.b3dgs.lionengine.runtime;

/**
 * Lifecycle contract of a scene: one screen of the game, such as a menu or a level (ADR-0021, ADR-0031, ADR-0036,
 * REQ-SCENE-001).
 *
 * <p>
 * The scene manager calls, in this order: {@link #enter()} once before the first update; {@link #update(double)} once
 * per fixed step while the scene is on top of the stack; {@link #render(double, double)} once per presentation while
 * the scene is in the stack, bottom to top; {@link #exit()} once when it leaves the stack; {@link #dispose()} once
 * after {@link #exit()}, after which the instance is never used again.
 * </p>
 *
 * <p>
 * A scene knows no world, camera, graphics or audio type through this contract; it composes them itself, or uses the
 * optional {@code composition} module. Thread: runtime thread only.
 * </p>
 *
 * @since 11.0.0
 */
public interface Scene
{
    /**
     * Called once before the first update: acquire resources and start asynchronous loads.
     */
    void enter();

    /**
     * Runs one fixed simulation step while the scene is on top of the stack.
     *
     * @param stepSeconds The constant step duration in seconds.
     */
    void update(double stepSeconds);

    /**
     * Renders the scene; must not change simulation state.
     *
     * @param alpha The presentation factor in {@code [0, 1)}.
     * @param unscaledDeltaSeconds The real time elapsed since the previous render, in seconds, for presentation-only
     *            state.
     */
    void render(double alpha, double unscaledDeltaSeconds);

    /**
     * Called once when the scene leaves the stack: stop sounds, release acquired resources.
     */
    void exit();

    /**
     * Called once after {@link #exit()}; the instance is never used again.
     */
    void dispose();
}
