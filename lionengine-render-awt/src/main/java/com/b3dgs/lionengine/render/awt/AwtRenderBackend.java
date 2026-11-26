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
package com.b3dgs.lionengine.render.awt;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import com.b3dgs.lionengine.platform.Platform;
import com.b3dgs.lionengine.platform.PresentationGeometry;
import com.b3dgs.lionengine.platform.awt.AwtSurface;
import com.b3dgs.lionengine.render.ColorEffect;
import com.b3dgs.lionengine.render.CommandBuffer;
import com.b3dgs.lionengine.render.OutputFilter;
import com.b3dgs.lionengine.render.RenderBackend;
import com.b3dgs.lionengine.render.TextureStore;

/**
 * Render backend of the AWT platform (ADR-0006, ADR-0022, ADR-0026, ADR-0033, ADR-0043, ADR-0046, ADR-0047,
 * REQ-SPRITE-010, REQ-FE-003, REQ-PLATFORM-001).
 *
 * <p>
 * Created for a platform that has the {@link AwtSurface} capability ({@code platform-awt}), then attached to it before
 * its start. Each {@link #present(CommandBuffer)} draws the entries on an image of the logical frame, cleared to opaque
 * black, with nearest-neighbor sampling; then it scales that image to the canvas with the presentation geometry of the
 * platform, in device pixels (ADR-0043), with the scaling filter of the effective chain, and fills the rest of the
 * canvas with black. A tinted entry
 * copies its source rectangle on the CPU; an entry whose texture is not alive draws nothing. Before the window is
 * shown, only the logical frame is drawn.
 * </p>
 *
 * <p>
 * Textures are images created from the pixel arrays at their first use or at {@code prepare}. Output filters
 * (ADR-0063): {@link OutputFilter#NONE} and {@link OutputFilter#BILINEAR} drawn by Java2D, {@link OutputFilter#HQ2X}
 * drawn by Scale2x at a presentation scale of 2 or more (as {@code NONE} below), no post filter. Thread: the runtime
 * thread of the platform.
 * </p>
 *
 * @since 11.0.0
 */
public final class AwtRenderBackend implements RenderBackend
{
    /** Color of the surface outside the presented frame. */
    private static final Color BARS = new Color(AwtFrame.CLEAR, true);

    /** Platform, for its presentation geometry and its surface size. */
    private final Platform platform;
    /** Canvas of the surface. */
    private final Canvas canvas;
    /** Textures. */
    private final AwtTextures textures = new AwtTextures();
    /** Logical frame. */
    private final AwtFrame frame;
    /** Presentation of the logical frame with the scaling filter. */
    private final AwtPresenter presenter;
    /** Identity transform, to draw in device pixels. */
    private final AffineTransform identity = new AffineTransform();

    /**
     * Creates the backend of a platform.
     *
     * @param platform The platform it presents on, with the {@link AwtSurface} capability (never {@code null}).
     * @throws IllegalArgumentException If {@code platform} is {@code null} or has no {@link AwtSurface}.
     */
    public AwtRenderBackend(Platform platform)
    {
        if (platform == null)
        {
            throw new IllegalArgumentException("Platform must not be null");
        }
        final AwtSurface surface = platform.getCapability(AwtSurface.class);
        if (surface == null)
        {
            throw new IllegalArgumentException("Platform without AWT surface: " + platform);
        }
        this.platform = platform;
        canvas = surface.getCanvas();
        frame = new AwtFrame(platform.getLogicalWidth(), platform.getLogicalHeight());
        presenter = new AwtPresenter(platform.getLogicalWidth(), platform.getLogicalHeight());
    }

    /**
     * Returns the image of the logical frame, for the tests.
     *
     * @return The image of the last frame drawn.
     */
    BufferedImage getLogicalFrame()
    {
        return frame.getImage();
    }

    /**
     * Draws the logical frame on the surface, in device pixels, until the buffer contents are kept.
     *
     * @param strategy The buffer strategy of the canvas.
     */
    private void show(BufferStrategy strategy)
    {
        final PresentationGeometry geometry = platform.getPresentationGeometry();
        final OutputFilter filter = AwtPresenter.getScalingFilter(platform.getEffectiveResult().effective().filters());
        final BufferedImage image = frame.getImage();
        do
        {
            do
            {
                final Graphics2D graphics = (Graphics2D) strategy.getDrawGraphics();
                graphics.setTransform(identity);
                graphics.setColor(BARS);
                graphics.fillRect(0, 0, platform.getSurfaceWidth(), platform.getSurfaceHeight());
                presenter.draw(graphics, image, geometry, filter);
                graphics.dispose();
            }
            while (strategy.contentsRestored());
            strategy.show();
        }
        while (strategy.contentsLost());
        Toolkit.getDefaultToolkit().sync();
    }

    @Override
    public TextureStore getTextureStore()
    {
        return textures;
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The scaling filters {@link OutputFilter#NONE}, {@link OutputFilter#BILINEAR} and {@link OutputFilter#HQ2X}, the
     * last one drawn by Scale2x until the license record of {@code HQ2X} (ADR-0063); no post filter, so CRT and the
     * custom filters are skipped.
     * </p>
     */
    @Override
    public boolean isSupported(OutputFilter filter)
    {
        if (filter == null)
        {
            throw new IllegalArgumentException("Filter must not be null");
        }
        return OutputFilter.NONE.equals(filter) || OutputFilter.BILINEAR.equals(filter)
               || OutputFilter.HQ2X.equals(filter);
    }

    /**
     * Both effects are applied per frame: the tint on a CPU copy of the source rectangle, the alpha with the opacity
     * of the composite (ADR-0056).
     */
    @Override
    public boolean isSupported(ColorEffect effect)
    {
        if (effect == null)
        {
            throw new IllegalArgumentException("Effect must not be null");
        }
        return true;
    }

    @Override
    public void present(CommandBuffer commands)
    {
        if (commands == null)
        {
            throw new IllegalArgumentException("Commands must not be null");
        }
        frame.draw(commands, textures);
        final BufferStrategy strategy = canvas.getBufferStrategy();
        if (strategy != null)
        {
            show(strategy);
        }
    }
}
