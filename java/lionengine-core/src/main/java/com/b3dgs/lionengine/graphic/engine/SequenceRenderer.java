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
package com.b3dgs.lionengine.graphic.engine;

import java.util.Optional;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.graphic.Filter;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.ImageBuffer;
import com.b3dgs.lionengine.graphic.Renderable;
import com.b3dgs.lionengine.graphic.Scanline;
import com.b3dgs.lionengine.graphic.Screen;
import com.b3dgs.lionengine.graphic.Transform;

/**
 * Sequence rendering.
 */
public final class SequenceRenderer implements Rasterbar
{
    /** Renderers. */
    static final int RENDERERS_SPLIT = 2;
    /** Renderer split left. */
    static final int RENDERER_SPLIT_LEFT = 0;
    /** Renderer split right. */
    static final int RENDERER_SPLIT_RIGHT = 1;
    /** Renderers. */
    static final int RENDERERS_QUAD = 4;
    /** Renderer top left. */
    static final int RENDERER_QUAD_TOP_LEFT = 0;
    /** Renderer top right. */
    static final int RENDERER_QUAD_TOP_RIGHT = 1;
    /** Renderer bottom left. */
    static final int RENDERER_QUAD_BOTTOM_LEFT = 2;
    /** Renderer quad bottom right. */
    static final int RENDERER_QUAD_BOTTOM_RIGHT = 3;
    /** Scaling precision. */
    private static final double SCALE_PRECISION = 0.01;

    /** Filter graphic. */
    private final Graphic graphic = Graphics.createGraphic();
    /** Rasterbar reference. */
    private final RasterbarImpl rasterbar = new RasterbarImpl(graphic);
    /** Config reference. */
    private final Config config;
    /** Renderer target. */
    private final Renderable target;
    /** Horizontal scale. */
    private final int scaleDivX;
    /** Vertical scale. */
    private final int scaleDivY;

    /** Source resolution. */
    private Resolution source;
    /** Filter reference. */
    private Filter filter = FilterNone.INSTANCE;
    /** Scanline reference. */
    private Scanline scanline = ScanlineNone.INSTANCE;
    /** Pending cursor visibility. */
    private Boolean cursorVisibility = Boolean.TRUE;
    /** Current renderer. */
    private Renderable renderer = this::renderBuffer;
    /** Image buffer (can be <code>null</code> for direct rendering). */
    private ImageBuffer buf;
    /** Image buffer (can be <code>null</code> for direct rendering). */
    private ImageBuffer buf2;
    /** Buffer 2. */
    private Graphic buf2g;
    /** Filter used (can be <code>null</code> for direct rendering). */
    private Transform transformbuf;
    /** Filter used (can be <code>null</code> for direct rendering). */
    private Transform transform;
    /** Current screen used (<code>null</code> if not started). */
    private Screen screen;
    /** Renderer id. */
    private int id;
    /** Renderer horizontal location. */
    private int x;
    /** Renderer vertical location. */
    private int y;
    /** Renderer width size. */
    private int w;
    /** Renderer height size. */
    private int h;

    /**
     * Constructor base.
     * 
     * @param context The context reference (must not be <code>null</code>).
     * @param resolution The resolution source reference (must not be <code>null</code>).
     * @param dx The horizontal scale.
     * @param dy The vertical scale.
     * @param target The renderer target.
     * @throws LionEngineException If invalid arguments.
     */
    public SequenceRenderer(Context context, Resolution resolution, int dx, int dy, Renderable target)
    {
        super();

        Check.notNull(context);
        Check.notNull(resolution);

        this.target = target;

        config = context.getConfig();
        source = computeSource(resolution);
        scaleDivX = dx;
        scaleDivY = dy;
    }

    /**
     * Initialize resolution.
     * 
     * @param resolution The resolution source (must not be <code>null</code>).
     * @throws LionEngineException If invalid argument.
     */
    void initResolution(Resolution resolution)
    {
        Check.notNull(resolution);

        setSystemCursorVisible(cursorVisibility.booleanValue());

        source = computeSource(resolution);
        screen.onSourceChanged(source);

        w = source.width();
        h = source.height();

        buf = Graphics.createImageBuffer(w, h);
        transform = getTransform();

        computePosition();

        final Graphic gbuf = buf.createGraphic();
        graphic.setGraphic(gbuf.getGraphic());

        scanline.prepare(config);

        setDirect(false);
        initFilter();

        rasterbar.init(w, h, buf);
    }

    /**
     * Local render routine.
     */
    void render()
    {
        if (screen.isReady())
        {
            final Graphic g = screen.getGraphic();
            renderer.render(g);
            scanline.render(g);
        }
    }

    /**
     * Close resources.
     */
    void close()
    {
        if (filter != null)
        {
            filter.close();
        }
    }

    /**
     * Set rendering location.
     * 
     * @param id The rendering id.
     */
    void setLocation(int id)
    {
        this.id = id;
    }

    /**
     * Set the current screen to use.
     * 
     * @param screen The screen to use.
     */
    void setScreen(Screen screen)
    {
        this.screen = screen;
    }

    /**
     * Set the filter to use.
     * 
     * @param filter The filter to use (if <code>null</code> then {@link FilterNone#INSTANCE} is used).
     */
    void setFilter(Filter filter)
    {
        if (this.filter != null)
        {
            this.filter.close();
        }
        this.filter = Optional.ofNullable(filter).orElse(FilterNone.INSTANCE);
        transform = getTransform();

        if (w > 0 && h > 0)
        {
            initFilter();
        }
    }

    /**
     * Set the scanline to use.
     * 
     * @param scanline The scanline to use (if <code>null</code> then {@link ScanlineNone#INSTANCE} is used).
     */
    void setScanline(Scanline scanline)
    {
        this.scanline = Optional.ofNullable(scanline).orElse(ScanlineNone.INSTANCE);
    }

    /**
     * Set the direct rendering.
     * 
     * @param direct <code>true</code> for direct rendering, <code>false</code> with buffer.
     */
    void setDirect(boolean direct)
    {
        if (direct)
        {
            renderer = this::renderDirect;
        }
        else
        {
            renderer = this::renderBuffer;
        }
    }

    /**
     * Set the system cursor visibility.
     * 
     * @param visible <code>true</code> if visible, <code>false</code> else.
     */
    void setSystemCursorVisible(boolean visible)
    {
        if (screen == null)
        {
            cursorVisibility = Boolean.valueOf(visible);
        }
        else if (visible)
        {
            screen.showCursor();
        }
        else
        {
            screen.hideCursor();
        }
    }

    /**
     * Get width.
     * 
     * @return The width.
     */
    int getWidth()
    {
        return w;
    }

    /**
     * Get height.
     * 
     * @return The height.
     */
    int getHeight()
    {
        return h;
    }

    /**
     * Compute source resolution based on current scales.
     * 
     * @param resolution The base resolution.
     * @return The computed source resolution.
     */
    private Resolution computeSource(Resolution resolution)
    {
        if (scaleDivX > 0 && scaleDivY > 0)
        {
            return new Resolution(resolution.width() * scaleDivY, resolution.height() * scaleDivX, resolution.rate());
        }
        return new Resolution(resolution.width(), resolution.height(), resolution.rate());
    }

    /**
     * Compute rendering position on screen.
     */
    private void computePosition()
    {
        final double fw = config.output().width() / (double) source.width();
        final double fh = config.output().height() / (double) source.height();

        if (id == RENDERER_SPLIT_RIGHT || id == RENDERER_QUAD_TOP_RIGHT)
        {
            if (scaleDivX > 1)
            {
                x = (int) (w * fh);
            }
            else if (scaleDivY > 1)
            {
                y = (int) (h * fw);
            }
            else if (scaleDivX == 1 && scaleDivY == 1)
            {
                x = (int) (w * fh / 2);
            }
        }
        else if (id == RENDERER_QUAD_BOTTOM_LEFT)
        {
            y = (int) (h * fw / 2);
        }
        else if (id == RENDERER_QUAD_BOTTOM_RIGHT)
        {
            x = (int) (w * fh / 2);
            y = (int) (h * fw / 2);
        }
    }

    /**
     * Init filter.
     */
    private void initFilter()
    {
        final int scale = filter.getScale();
        if (scale > 1)
        {
            buf2 = Graphics.createImageBuffer(w * scale, h * scale);
            buf2g = buf2.createGraphic();
            transformbuf = new TransformNone(scale, scale);
            renderer = this::renderBufferScaled;
        }
        else
        {
            transform = getTransform();
            renderer = this::renderBuffer;
        }
    }

    /**
     * Get the transform associated to the filter keeping screen scale independent.
     * 
     * @return The associated transform instance.
     */
    private Transform getTransform()
    {
        final Resolution output = config.output();
        final int scale = filter.getScale();

        double scaleY = output.height() / ((double) source.height() * scale);

        if (scaleDivX == 0
            && scaleDivY == 0
            && UtilMath.equals(output.width() / (double) output.height(),
                               source.width() * 2 / ((double) source.height() * scale),
                               SCALE_PRECISION))
        {
            return filter.getTransform(scaleY, scaleY);
        }

        final double scaleX;
        if (scaleDivX == 0 && scaleDivY == 0)
        {
            scaleX = output.width() / ((double) source.width() * scale);
        }
        else if (scaleDivX == 1 && scaleDivY == 1)
        {
            scaleX = output.width() / ((double) source.width() * scale) / 2;
            scaleY = output.height() / ((double) source.height() * scale) / 2;
        }
        else
        {
            scaleX = output.width() / ((double) source.width() * scale) / scaleDivX;
            scaleY = output.height() / ((double) source.height() * scale) / scaleDivY;
        }

        return filter.getTransform(scaleX, scaleY);
    }

    /**
     * Direct rendering.
     * 
     * @param g The graphic output.
     */
    private void renderDirect(Graphic g)
    {
        target.render(g);
    }

    /**
     * Buffered rendering.
     * 
     * @param g The graphic output.
     */
    private void renderBuffer(Graphic g)
    {
        target.render(graphic);
        g.drawImage(filter.filter(buf), transform, x, y);
    }

    /**
     * Buffered rendering with scaled.
     * 
     * @param g The graphic output.
     */
    private void renderBufferScaled(Graphic g)
    {
        target.render(graphic);
        buf2g.drawImage(buf, transformbuf, x, y);
        g.drawImage(filter.filter(buf2), transform, x, y);
    }

    @Override
    public void clearRasterbarColor()
    {
        rasterbar.clearRasterbarColor();
    }

    @Override
    public void addRasterbarColor(ImageBuffer buffer)
    {
        rasterbar.addRasterbarColor(buffer);
    }

    @Override
    public void prepare()
    {
        rasterbar.prepare();
    }

    @Override
    public void setRasterbarOffset(int offsetY, int factorY)
    {
        rasterbar.setRasterbarOffset(offsetY, factorY);
    }

    @Override
    public void setRasterbarY(int y1, int y2)
    {
        rasterbar.setRasterbarY(y1, y2);
    }

    @Override
    public void renderRasterbar()
    {
        rasterbar.renderRasterbar();
    }
}
