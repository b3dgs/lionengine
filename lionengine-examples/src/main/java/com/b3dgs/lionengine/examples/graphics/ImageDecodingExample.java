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

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

import com.b3dgs.lionengine.Log;
import com.b3dgs.lionengine.LogLevel;
import com.b3dgs.lionengine.graphics.ImageDecoder;
import com.b3dgs.lionengine.graphics.ImageDecodingException;
import com.b3dgs.lionengine.graphics.PixelBuffer;
import com.b3dgs.lionengine.graphics.PngDecoder;
import com.b3dgs.lionengine.render.TextureStore;
import com.b3dgs.lionengine.resources.ResourceException;
import com.b3dgs.lionengine.resources.ResourceKey;
import com.b3dgs.lionengine.resources.Resources;
import com.b3dgs.lionengine.resources.file.FileProvider;

/**
 * Decodes a PNG image read by key, changes a pixel of the decoded copy, and turns it into a texture (REQ-SPRITE-009,
 * REQ-SPR-001, REQ-SPRITE-010).
 *
 * <p>
 * The game opens the image through {@link Resources} and decodes it with {@link PngDecoder}, which gives the same
 * pixels on every platform. The decoded {@link PixelBuffer} belongs to the game, which may change it; the stream
 * belongs to the code that opens it, which closes it. A missing image raises a {@link ResourceException} with reason
 * {@code MISSING}, a read failure one with reason {@code INACCESSIBLE}, and invalid content an
 * {@link ImageDecodingException}. The texture is created from a copy of the pixels, which the texture store shares
 * read-only until the texture is released.
 * </p>
 *
 * <p>
 * Until the image cache of {@code graphics} exists, the game creates and releases its textures itself, as here; the
 * cache will then decode each image once, share it, and release its texture with it (ADR-0026).
 * </p>
 *
 * @since 11.0.0
 */
public final class ImageDecodingExample
{
    /** Key of the image, in the game data. */
    public static final ResourceKey IMAGE = new ResourceKey("sprites/hero.png");
    /** Color written on the top-left pixel of the decoded copy: opaque magenta. */
    public static final int MARK = 0xFFFF00FF;

    /**
     * Private constructor.
     */
    private ImageDecodingExample()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Reads and decodes an image, and closes its stream.
     *
     * @param resources The resources.
     * @param key The key of the image.
     * @param decoder The decoder of the image format.
     * @return The pixels, owned by the caller.
     * @throws ResourceException If the image is missing or unreadable.
     * @throws ImageDecodingException If the content is not a valid image.
     */
    public static PixelBuffer load(Resources resources, ResourceKey key, ImageDecoder decoder)
    {
        try (InputStream input = resources.open(key))
        {
            return decoder.decode(input);
        }
        catch (final IOException exception)
        {
            throw new ResourceException(key, ResourceException.Reason.INACCESSIBLE, exception);
        }
    }

    /**
     * Loads {@link #IMAGE}, marks its top-left pixel with {@link #MARK}, and creates a texture from it.
     *
     * @param resources The resources holding {@link #IMAGE}.
     * @param textures The texture store of the render backend.
     * @return The size of the image, its top-left pixel before the change, and the texture.
     */
    public static Result run(Resources resources, TextureStore textures)
    {
        final PixelBuffer pixels = load(resources, IMAGE, new PngDecoder());
        final int topLeft = pixels.getPixel(0, 0);
        pixels.setPixel(0, 0, MARK);
        final int texture = textures.create(pixels.getWidth(), pixels.getHeight(), pixels.copyPixels());
        return new Result(pixels.getWidth(), pixels.getHeight(), topLeft, texture);
    }

    /**
     * Decodes the image of a game data directory and logs its size.
     *
     * @param args The path of a directory holding {@code sprites/hero.png}.
     */
    // CHECKSTYLE IGNORE LINE: UncommentedMain
    public static void main(String[] args)
    {
        final Log log = new Log();
        log.setLevel(LogLevel.INFO);
        if (args.length == 1)
        {
            final Resources resources = new Resources(new FileProvider(Path.of(args[0]).toFile()));
            final PixelBuffer pixels = load(resources, IMAGE, new PngDecoder());
            log.log(LogLevel.INFO, "Image decoded, width and height in pixels:", pixels.getWidth(), pixels.getHeight());
        }
        else
        {
            log.log(LogLevel.WARNING, "Usage: ImageDecodingExample <data directory holding sprites/hero.png>");
        }
    }

    /**
     * Result of the example.
     *
     * @param width The width of the image in pixels.
     * @param height The height of the image in pixels.
     * @param topLeft The top-left pixel as decoded, packed {@code 0xAARRGGBB}.
     * @param texture The texture created from the marked copy, released by the caller.
     * @since 11.0.0
     */
    public record Result(int width, int height, int topLeft, int texture)
    {
        // Value type of the example.
    }
}
