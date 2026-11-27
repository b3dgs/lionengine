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
 * Lightweight mutable instance of a shared immutable source: where and how one frame of an image is drawn, by adding
 * one render command (ADR-0003, ADR-0014, ADR-0016, ADR-0026, ADR-0056, REQ-SPRITE-001, REQ-SPRITE-003,
 * REQ-SPRITE-004, REQ-SPRITE-006, REQ-SPRITE-007).
 *
 * <p>
 * The source is an {@link Image} of an {@link ImageCache}, whose whole area is the only frame, or a
 * {@link SpriteSheet}, whose cells are the frames, numbered as in the sheet; the first frame is shown until
 * {@link #setFrame(int)}. The frames of a sheet can also be drawn from a variant of its image of the same size, such as
 * one baked with {@link ImageCache#bake(Image, ColorTransform)}. Loading a sprite is loading its image:
 * {@link ImageCache#load(com.b3dgs.lionengine.resources.ResourceKey)} raises the
 * {@link com.b3dgs.lionengine.resources.ResourceException} of a missing resource and the
 * {@link ImageDecodingException} of invalid data, and {@link SheetGroup} and {@link SheetLoader} do the same for a
 * sheet. Any number of sprites share one source, with its pixels and its texture. A sprite holds no image and releases
 * nothing: the image stays owned by the code that acquired it, the handle of the cache or the sheet group, and once it
 * is released the sprite can no longer be drawn. The size of the frames is read from the source when the sprite is
 * created and never changes.
 * </p>
 *
 * <p>
 * Transform (ADR-0026): the frame is scaled, then rotated, then translated, all about its pivot. The pivot is in
 * pixels from the top-left corner of the frame, by default the center of the image, or for a sheet its pivot (the
 * cell center unless its definition says otherwise); it may lie outside the frame. The position is where the pivot is
 * drawn, in pixels of the logical frame relative to the viewport of the command buffer, Y down (ADR-0014); it is
 * given by the caller, usually from the view transform of the camera. A negative scale mirrors the frame, a scale of
 * zero hides it. The rotation is in degrees, counter-clockwise as seen on screen: the sprite hands it to the command
 * unchanged, and the render backends turn it on the screen, whose Y axis points down (ADR-0014). It is not normalized
 * (ADR-0016): a game that accumulates it normalizes it itself.
 * </p>
 *
 * <p>
 * Color (ADR-0056): the tint and the alpha are the per-frame part of a {@link ColorTransform}, written into every
 * command and applied by a backend that supports them ({@link com.b3dgs.lionengine.render.ColorEffect}); no tint and
 * full opacity by default. A sprite never bakes: the static part of a transform (grading, mask), and the tint or alpha
 * a backend cannot apply per frame, are baked into a variant of the image by the cache, which the sprite then draws.
 * </p>
 *
 * <p>
 * Rendering adds one entry to the command buffer and draws nothing directly: the texture of the image, the source
 * rectangle of the frame, the position, scale, rotation and pivot, the tint and alpha, and the layer. Entries are
 * presented by ascending layer, then submission order.
 * </p>
 *
 * <p>
 * Threads and phases: runtime thread only (ADR-0018). The setters belong to the update phase; rendering reads the
 * sprite and never changes it, so a position computed for the frame, interpolated between two steps or converted by
 * the camera, is given to {@link #render(CommandBuffer, double, double)} instead of being set. Neither the setters nor
 * rendering allocate (ADR-0019).
 * </p>
 *
 * @since 11.0.0
 */
public final class Sprite
{
    /** Error message of a value that is not finite. */
    private static final String ERROR_NOT_FINITE = " not finite: ";
    /** Error message of a value that is not finite: separator. */
    private static final String ERROR_SEPARATOR = ", ";
    /** Name of the position, for the messages. */
    private static final String NAME_POSITION = "Position";
    /** Separator of a name and a detail in the messages. */
    private static final String COLON = ": ";
    /** Name of the sheet, for the messages. */
    private static final String NAME_SHEET = "Sheet";
    /** Name of the image, for the messages. */
    private static final String NAME_IMAGE = "Image";

    /**
     * Checks that two values are finite.
     *
     * @param name The name of the values, for the message.
     * @param first The first value.
     * @param second The second value.
     * @throws IllegalArgumentException If a value is not finite.
     */
    private static void checkFinite(String name, double first, double second)
    {
        if (!Double.isFinite(first) || !Double.isFinite(second))
        {
            throw new IllegalArgumentException(name + ERROR_NOT_FINITE + first + ERROR_SEPARATOR + second);
        }
    }

    /**
     * Returns the image of a sheet, for the constructor drawing the sheet from its own image.
     *
     * @param sheet The sheet.
     * @return Its image.
     * @throws IllegalArgumentException If the sheet is {@code null}.
     */
    private static Image imageOf(SpriteSheet sheet)
    {
        ImageCache.checkNotNull(sheet, NAME_SHEET);
        return sheet.getImage();
    }

    /** The image of the source, owned by its acquirer. */
    private final Image image;
    /** The sheet of the frames, {@code null} when the whole image is the only frame. */
    private final SpriteSheet sheet;
    /** Width of a frame in pixels. */
    private final int width;
    /** Height of a frame in pixels. */
    private final int height;
    /** Number of frames. */
    private final int frameCount;
    /** Current frame. */
    private int frame;
    /** Left edge of the current frame in the image. */
    private int sourceX;
    /** Top edge of the current frame in the image. */
    private int sourceY;
    /** Horizontal position of the pivot in logical-frame pixels. */
    private double x;
    /** Vertical position of the pivot in logical-frame pixels. */
    private double y;
    /** Horizontal scale. */
    private double scaleX = 1.0;
    /** Vertical scale. */
    private double scaleY = 1.0;
    /** Rotation in degrees, counter-clockwise on screen. */
    private double rotation;
    /** Horizontal pivot in pixels from the left edge of the frame. */
    private double pivotX;
    /** Vertical pivot in pixels from the top edge of the frame. */
    private double pivotY;
    /** Tint of the render command, {@code 0xAARRGGBB}. */
    private int tint = CommandBuffer.NO_TINT;
    /** Alpha of the render command, in {@code [0, 1]}. */
    private double alpha = 1.0;
    /** Layer of the render command. */
    private int layer;

    /**
     * Creates a sprite showing a whole image, with its pivot at the center of the image.
     *
     * @param image The image, which stays owned by its acquirer (never {@code null}).
     * @throws IllegalArgumentException If the image is {@code null}.
     * @throws IllegalStateException If the image is released.
     */
    public Sprite(Image image)
    {
        super();

        ImageCache.checkNotNull(image, NAME_IMAGE);
        this.image = image;
        sheet = null;
        width = image.getWidth();
        height = image.getHeight();
        frameCount = 1;
        pivotX = width / 2.0;
        pivotY = height / 2.0;
    }

    /**
     * Creates a sprite showing the cells of a sheet, its first cell first, with the pivot of the sheet.
     *
     * @param sheet The sheet, whose image stays owned by its group (never {@code null}).
     * @throws IllegalArgumentException If the sheet is {@code null}.
     * @throws IllegalStateException If the image of the sheet is released, with its group.
     */
    public Sprite(SpriteSheet sheet)
    {
        this(sheet, imageOf(sheet));
    }

    /**
     * Creates a sprite showing the cells of a sheet drawn from another image of the same size, such as a variant of
     * the image of the sheet baked by the cache, its first cell first, with the pivot of the sheet.
     *
     * @param sheet The sheet, giving the frames and the pivot (never {@code null}).
     * @param image The image the frames are drawn from, of the size of the image of the sheet, which stays owned by
     *            its acquirer (never {@code null}).
     * @throws IllegalArgumentException If an argument is {@code null}, or the image is not of the size of the image
     *             of the sheet.
     * @throws IllegalStateException If the image of the sheet, with its group, or the image is released.
     */
    public Sprite(SpriteSheet sheet, Image image)
    {
        super();

        ImageCache.checkNotNull(sheet, NAME_SHEET);
        ImageCache.checkNotNull(image, NAME_IMAGE);
        final Image own = sheet.getImage();
        if (own.isReleased())
        {
            throw new IllegalStateException("Image of the sheet released: " + sheet.getId());
        }
        if (image.getWidth() != own.getWidth() || image.getHeight() != own.getHeight())
        {
            throw new IllegalArgumentException("Image not of the size of the sheet "
                                               + sheet.getId()
                                               + COLON
                                               + image.getWidth()
                                               + "x"
                                               + image.getHeight());
        }
        this.image = image;
        this.sheet = sheet;
        width = sheet.getCellWidth();
        height = sheet.getCellHeight();
        frameCount = sheet.getCellCount();
        sourceX = sheet.getCellX(0);
        sourceY = sheet.getCellY(0);
        pivotX = sheet.getPivotX();
        pivotY = sheet.getPivotY();
    }

    /**
     * Returns the image of the source.
     *
     * @return The image handle, owned by its acquirer: the sprite never releases it.
     */
    public Image getImage()
    {
        return image;
    }

    /**
     * Returns the width of a frame.
     *
     * @return The width in pixels: the width of the image, or of a cell of the sheet.
     */
    public int getWidth()
    {
        return width;
    }

    /**
     * Returns the height of a frame.
     *
     * @return The height in pixels: the height of the image, or of a cell of the sheet.
     */
    public int getHeight()
    {
        return height;
    }

    /**
     * Returns the number of frames.
     *
     * @return {@code 1} for an image, the number of cells for a sheet.
     */
    public int getFrameCount()
    {
        return frameCount;
    }

    /**
     * Returns the frame shown.
     *
     * @return The frame, from {@code 0}: the cell index of the sheet.
     */
    public int getFrame()
    {
        return frame;
    }

    /**
     * Shows a frame.
     *
     * @param frame The frame, from {@code 0} at the top-left cell of the sheet, row by row; {@code 0} for an image.
     * @throws IndexOutOfBoundsException If the frame is outside {@code [0, getFrameCount())}.
     */
    public void setFrame(int frame)
    {
        if (frame < 0 || frame >= frameCount)
        {
            throw new IndexOutOfBoundsException("Frame outside the sprite "
                                                + image.getKey()
                                                + COLON
                                                + frame
                                                + " of "
                                                + frameCount);
        }
        if (sheet != null)
        {
            sourceX = sheet.getCellX(frame);
            sourceY = sheet.getCellY(frame);
        }
        this.frame = frame;
    }

    /**
     * Returns the horizontal position, used by {@link #render(CommandBuffer)}.
     *
     * @return The position of the pivot in logical-frame pixels, {@code 0} by default.
     */
    public double getX()
    {
        return x;
    }

    /**
     * Returns the vertical position, used by {@link #render(CommandBuffer)}.
     *
     * @return The position of the pivot in logical-frame pixels, {@code 0} by default.
     */
    public double getY()
    {
        return y;
    }

    /**
     * Sets where the pivot is drawn by {@link #render(CommandBuffer)}.
     *
     * @param x The horizontal position in logical-frame pixels, relative to the viewport (finite).
     * @param y The vertical position in logical-frame pixels, relative to the viewport, Y down (finite).
     * @throws IllegalArgumentException If a value is not finite.
     */
    public void setPosition(double x, double y)
    {
        checkFinite(NAME_POSITION, x, y);
        this.x = x;
        this.y = y;
    }

    /**
     * Returns the horizontal scale.
     *
     * @return The scale, {@code 1} by default.
     */
    public double getScaleX()
    {
        return scaleX;
    }

    /**
     * Returns the vertical scale.
     *
     * @return The scale, {@code 1} by default.
     */
    public double getScaleY()
    {
        return scaleY;
    }

    /**
     * Sets the scale about the pivot, applied before the rotation.
     *
     * @param x The horizontal scale: {@code 1} for none, negative to mirror (finite).
     * @param y The vertical scale: {@code 1} for none, negative to flip (finite).
     * @throws IllegalArgumentException If a value is not finite.
     */
    public void setScale(double x, double y)
    {
        checkFinite("Scale", x, y);
        scaleX = x;
        scaleY = y;
    }

    /**
     * Returns the rotation.
     *
     * @return The rotation in degrees, counter-clockwise on screen, {@code 0} by default, as set.
     */
    public double getRotation()
    {
        return rotation;
    }

    /**
     * Sets the rotation about the pivot, applied after the scale; the value is kept as given, not normalized
     * (ADR-0016).
     *
     * @param degrees The rotation in degrees, counter-clockwise as seen on screen (finite).
     * @throws IllegalArgumentException If the value is not finite.
     */
    public void setRotation(double degrees)
    {
        if (!Double.isFinite(degrees))
        {
            throw new IllegalArgumentException("Rotation" + ERROR_NOT_FINITE + degrees);
        }
        rotation = degrees;
    }

    /**
     * Returns the horizontal pivot.
     *
     * @return The pivot in pixels from the left edge of the frame.
     */
    public double getPivotX()
    {
        return pivotX;
    }

    /**
     * Returns the vertical pivot.
     *
     * @return The pivot in pixels from the top edge of the frame.
     */
    public double getPivotY()
    {
        return pivotY;
    }

    /**
     * Sets the pivot: the point of the frame drawn at the position, scaled and rotated about.
     *
     * @param x The horizontal pivot in pixels from the left edge of the frame (finite; may be outside the frame).
     * @param y The vertical pivot in pixels from the top edge of the frame (finite; may be outside the frame).
     * @throws IllegalArgumentException If a value is not finite.
     */
    public void setPivot(double x, double y)
    {
        checkFinite("Pivot", x, y);
        pivotX = x;
        pivotY = y;
    }

    /**
     * Returns the tint.
     *
     * @return The tint, packed {@code 0xAARRGGBB}; {@link CommandBuffer#NO_TINT} by default.
     */
    public int getTint()
    {
        return tint;
    }

    /**
     * Sets the tint of the render command, multiplied with the colors and the opacity of the frame by a backend that
     * supports {@link com.b3dgs.lionengine.render.ColorEffect#TINT}; bake it with the cache for another backend.
     *
     * @param argb The tint, packed {@code 0xAARRGGBB}; {@link CommandBuffer#NO_TINT} for none.
     */
    public void setTint(int argb)
    {
        tint = argb;
    }

    /**
     * Returns the alpha.
     *
     * @return The alpha in {@code [0, 1]}, {@code 1} by default.
     */
    public double getAlpha()
    {
        return alpha;
    }

    /**
     * Sets the alpha of the render command, multiplied with the opacity of the frame by a backend that supports
     * {@link com.b3dgs.lionengine.render.ColorEffect#ALPHA}, for a fade; bake it with the cache for another backend.
     *
     * @param value The alpha in {@code [0, 1]}: {@code 1} opaque, {@code 0} invisible.
     * @throws IllegalArgumentException If the value is outside {@code [0, 1]} or not a number.
     */
    public void setAlpha(double value)
    {
        if (Double.isNaN(value) || value < 0.0 || value > 1.0)
        {
            throw new IllegalArgumentException("Alpha outside [0, 1]: " + value);
        }
        alpha = value;
    }

    /**
     * Returns the layer.
     *
     * @return The layer of the render command, {@code 0} by default.
     */
    public int getLayer()
    {
        return layer;
    }

    /**
     * Sets the layer of the render command: commands are presented by ascending layer, then submission order.
     *
     * @param layer The layer.
     */
    public void setLayer(int layer)
    {
        this.layer = layer;
    }

    /**
     * Adds the render command of the sprite at its position ({@link #setPosition(double, double)}).
     *
     * @param commands The command buffer of the frame (never {@code null}).
     * @throws IllegalArgumentException If the buffer is {@code null}.
     * @throws IllegalStateException If the image is released; nothing is added then.
     */
    public void render(CommandBuffer commands)
    {
        render(commands, x, y);
    }

    /**
     * Adds the render command of the sprite at a position computed for the frame, for example interpolated between
     * two steps or converted from the world by the camera, without changing the sprite.
     *
     * @param commands The command buffer of the frame (never {@code null}).
     * @param x The horizontal position of the pivot in logical-frame pixels, relative to the viewport (finite).
     * @param y The vertical position of the pivot in logical-frame pixels, relative to the viewport, Y down (finite).
     * @throws IllegalArgumentException If the buffer is {@code null} or a position is not finite.
     * @throws IllegalStateException If the image is released; nothing is added then.
     */
    public void render(CommandBuffer commands, double x, double y)
    {
        ImageCache.checkNotNull(commands, "Commands");
        checkFinite(NAME_POSITION, x, y);
        commands.add(image.getTexture(), sourceX, sourceY, width, height)
                .position((float) x, (float) y)
                .scale((float) scaleX, (float) scaleY)
                .rotation((float) rotation)
                .pivot((float) pivotX, (float) pivotY)
                .tint(tint)
                .alpha((float) alpha)
                .layer(layer);
    }
}
