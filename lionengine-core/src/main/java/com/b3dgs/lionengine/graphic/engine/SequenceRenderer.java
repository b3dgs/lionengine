/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import com.b3dgs.lionengine.graphic.RenderableVoid;
import com.b3dgs.lionengine.graphic.Scanline;
import com.b3dgs.lionengine.graphic.Screen;
import com.b3dgs.lionengine.graphic.Transform;

/**
 * Sequence rendering.
 */
public final class SequenceRenderer implements Rasterbar
{
    /** Scaling precision. */
    private static final double SCALE_PRECISION = 0.01;
    /** No alpha clamp. */
    private static final int NO_ALPHA = 0x00FFFFFF;

    /** Filter graphic. */
    private final Graphic graphic;
    /** Config reference. */
    private final Config config;
    /** Renderer target. */
    private final Renderable target;
    /** Source resolution. */
    private Resolution source;
    /** Filter reference. */
    private Filter filter = FilterNone.INSTANCE;
    /** Scanline reference. */
    private Scanline scanline = ScanlineNone.INSTANCE;
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
    /** Pending cursor visibility. */
    private Boolean cursorVisibility = Boolean.TRUE;

    private final IntMap<int[]> raster = new IntMap<>();
    private Renderable rasterRenderer = RenderableVoid.getInstance();
    private int[] bu;
    private int id;
    private int x;
    private int y;
    private int w;
    private int h;
    private int y1;
    private int marginY;
    private int offsetY;
    private int factorY;
    private final int scaleDivX;
    private final int scaleDivY;

    private Renderable renderer = this::renderBuffer;

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

        source = resolution;
        scaleDivX = dx;
        scaleDivY = dy;

        config = context.getConfig();
        graphic = Graphics.createGraphic();
        this.target = target;
    }

    /**
     * Initialize resolution.
     * 
     * @param source The resolution source (must not be <code>null</code>).
     * @throws LionEngineException If invalid argument.
     */
    void initResolution(Resolution source)
    {
        Check.notNull(source);

        setSystemCursorVisible(cursorVisibility.booleanValue());

        if (scaleDivX > 0 && scaleDivY > 0)
        {
            this.source = new Resolution(source.getWidth() * scaleDivY,
                                         source.getHeight() * scaleDivX,
                                         source.getRate());
        }
        else
        {
            this.source = new Resolution(source.getWidth(), source.getHeight(), source.getRate());
        }

        final double fw = config.getOutput().getWidth() / (double) this.source.getWidth();
        final double fh = config.getOutput().getHeight() / (double) this.source.getHeight();

        screen.onSourceChanged(this.source);
        w = this.source.getWidth();
        h = this.source.getHeight();

        buf = Graphics.createImageBuffer(w, h);
        bu = new int[w * h];
        transform = getTransform();

        if (id == 1)
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
        if (id == 2)
        {
            y = (int) (h * fw / 2);
        }
        // CHECKSTYLE IGNORE LINE: MagicNumber
        if (id == 3)
        {
            x = (int) (w * fh / 2);
            y = (int) (h * fw / 2);
        }

        final Graphic gbuf = buf.createGraphic();
        graphic.setGraphic(gbuf.getGraphic());

        scanline.prepare(config);

        setDirect(false);
        initFilter();
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
        final Resolution output = config.getOutput();
        final int scale = filter.getScale();

        double scaleY = output.getHeight() / ((double) source.getHeight() * scale);

        if (scaleDivX == 0
            && scaleDivY == 0
            && UtilMath.equals(output.getWidth() / (double) output.getHeight(),
                               source.getWidth() * 2 / ((double) source.getHeight() * scale),
                               SCALE_PRECISION))
        {
            return filter.getTransform(scaleY, scaleY);
        }

        final double scaleX;
        if (scaleDivX == 0 && scaleDivY == 0)
        {
            scaleX = output.getWidth() / ((double) source.getWidth() * scale);
        }
        else if (scaleDivX == 1 && scaleDivY == 1)
        {
            scaleX = output.getWidth() / ((double) source.getWidth() * scale) / 2;
            scaleY = output.getHeight() / ((double) source.getHeight() * scale) / 2;
        }
        else
        {
            scaleX = output.getWidth() / ((double) source.getWidth() * scale) / scaleDivX;
            scaleY = output.getHeight() / ((double) source.getHeight() * scale) / scaleDivY;
        }

        return filter.getTransform(scaleX, scaleY);
    }

    /**
     * Render.
     * 
     * @param g The graphic output.
     */
    private void renderRasterbar(Graphic g)
    {
        buf.getRgb(0, 0, w, h, bu, 0, w);

        final int n = bu.length;
        for (int i = 0; i < n; i++)
        {
            final int lineY = h - i / w;
            final int[] k = raster.get(bu[i] & NO_ALPHA);
            if (k != null)
            {
                final int r = UtilMath.clamp((y1 + lineY + offsetY) / factorY, 1, k.length - 1);
                if (lineY < marginY)
                {
                    bu[i] = k[0];
                }
                else if (k.length > 1 && k[r] != Integer.MIN_VALUE)
                {
                    bu[i] = k[r];
                }
            }
        }

        buf.setRgb(0, 0, w, h, bu, 0, w);
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
        rasterRenderer = RenderableVoid.getInstance();
        raster.clear();
    }

    @Override
    public void addRasterbarColor(ImageBuffer buffer)
    {
        rasterRenderer = this::renderRasterbar;
        final int bw = buffer.getWidth();
        final int bh = buffer.getHeight();

        for (int bx = 0; bx < bw; bx++)
        {
            final int p = buffer.getRgb(bx, 0) & NO_ALPHA;
            int[] v = raster.get(p);
            if (v == null)
            {
                v = new int[bh - 1];
                raster.put(p, v);
            }

            for (int by = 0; by < bh - 1; by++)
            {
                v[by] = buffer.getRgb(bx, by + 1);
            }
        }
    }

    @Override
    public void setRasterbarOffset(int offsetY, int factorY)
    {
        this.offsetY = offsetY;
        this.factorY = factorY;
    }

    @Override
    public void setRasterbarY(int y1, int y2)
    {
        this.y1 = y1;
        marginY = y2 - y1;
    }

    @Override
    public void renderRasterbar()
    {
        rasterRenderer.render(graphic);
    }
}
