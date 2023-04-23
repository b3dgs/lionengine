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
package com.b3dgs.lionengine.graphic.drawable;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertFalse;
import static com.b3dgs.lionengine.UtilAssert.assertPrivateConstructor;
import static com.b3dgs.lionengine.UtilAssert.assertThrows;
import static com.b3dgs.lionengine.UtilAssert.assertThrowsIo;
import static com.b3dgs.lionengine.UtilAssert.assertThrowsPrefix;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Locale;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.FactoryMediaDefault;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.graphic.ImageFormat;

/**
 * Test {@link ImageInfo}.
 */
final class ImageInfoTest
{
    /**
     * Prepare tests.
     */
    @BeforeAll
    public static void beforeTests()
    {
        Medias.setFactoryMedia(new FactoryMediaDefault());
        Medias.setLoadFromJar(ImageInfoTest.class);
    }

    /**
     * Clean up tests.
     */
    @AfterAll
    public static void afterTests()
    {
        Medias.setLoadFromJar(null);
    }

    /**
     * Test image info from its type.
     * 
     * @param type The expected image type.
     * @param number The number of different files.
     */
    private static void testImageInfo(ImageFormat type, int number)
    {
        for (int i = 0; i < number; i++)
        {
            final String name;
            if (i == 0)
            {
                name = "image";
            }
            else
            {
                name = "image" + i;
            }
            final Media media = Medias.create(name + "." + type.name().toLowerCase(Locale.ENGLISH));
            final ImageHeader info = ImageInfo.get(media);

            assertEquals(64, info.getWidth());
            assertEquals(32, info.getHeight());
            assertEquals(type, info.getFormat());
            assertFalse(ImageInfo.isImage(null));
            assertTrue(ImageInfo.isImage(media));
        }
    }

    /**
     * Test constructor.
     */
    @Test
    void testConstructorPrivate()
    {
        assertPrivateConstructor(ImageInfo.class);
    }

    /**
     * Test failure cases.
     */
    @Test
    void testImageFailure()
    {
        assertThrows(() -> ImageInfo.get(null), "Unexpected null argument !");
        assertThrowsPrefix(() -> ImageInfo.get(Medias.create(Constant.EMPTY_STRING)), "[] ");
        assertThrows(() -> ImageInfo.get(Medias.create("image_error")), "[image_error] " + ImageInfo.ERROR_READ);
        assertThrows(() -> ImageInfo.get(Medias.create("image.tga")), "[image.tga] " + ImageInfo.ERROR_READ);
        assertThrows(() -> ImageInfo.get(Medias.create("image_error1.gif")),
                     "[image_error1.gif] " + ImageInfo.ERROR_READ);
        assertThrows(() -> ImageInfo.get(Medias.create("image_error2.gif")),
                     "[image_error2.gif] " + ImageInfo.ERROR_READ);
        assertThrows(() -> ImageInfo.get(Medias.create("image_error1.jpg")),
                     "[image_error1.jpg] " + ImageInfo.ERROR_READ);
        assertThrows(() -> ImageInfo.get(Medias.create("image_error2.jpg")),
                     "[image_error2.jpg] " + ImageInfo.ERROR_READ);
        assertThrows(() -> ImageInfo.get(Medias.create("image_error3.jpg")),
                     "[image_error3.jpg] " + ImageInfo.ERROR_READ);
        assertThrows(() -> ImageInfo.get(Medias.create("image_error1.png")),
                     "[image_error1.png] " + ImageInfo.ERROR_READ);
        assertThrows(() -> ImageInfo.get(Medias.create("image_error2.png")),
                     "[image_error2.png] " + ImageInfo.ERROR_READ);
        assertThrows(() -> ImageInfo.get(Medias.create("image_error1.bmp")),
                     "[image_error1.bmp] " + ImageInfo.ERROR_READ);
        assertThrows(() -> ImageInfo.get(Medias.create("image_error1.tiff")),
                     "[image_error1.tiff] " + ImageInfo.ERROR_READ);
        assertThrows(() -> ImageInfo.get(Medias.create("image_error2.tiff")),
                     "[image_error2.tiff] " + ImageInfo.ERROR_READ);
        assertThrows(() -> ImageInfo.get(Medias.create("image_error3.tiff")),
                     "[image_error3.tiff] " + ImageInfo.ERROR_READ);
        assertThrows(() -> ImageInfo.get(Medias.create("image_error4.tiff")),
                     "[image_error4.tiff] " + ImageInfo.ERROR_READ);
        assertThrows(() -> ImageInfo.get(Medias.create("image_error5.tiff")),
                     "[image_error5.tiff] " + ImageInfo.ERROR_READ);
        assertThrows(() -> ImageInfo.get(Medias.create("image_error6.tiff")),
                     "[image_error6.tiff] " + ImageInfo.ERROR_READ);
        assertThrows(() -> ImageInfo.get(Medias.create("image_error7.tiff")),
                     "[image_error7.tiff] " + ImageInfo.ERROR_READ);

        Verbose.info("*********************************** EXPECTED VERBOSE ***********************************");

        assertFalse(ImageInfo.isImage(Medias.create("image_error7.tiff")));

        Verbose.info("****************************************************************************************");
    }

    /**
     * Test info functions.
     */
    @Test
    void testImageInfo()
    {
        testImageInfo(ImageFormat.PNG, 1);
        testImageInfo(ImageFormat.GIF, 1);
        testImageInfo(ImageFormat.BMP, 1);
        testImageInfo(ImageFormat.JPG, 3);
        testImageInfo(ImageFormat.TIFF, 2);

        final ImageHeader info = ImageInfo.get(Medias.create("image.tif"));

        assertEquals(64, info.getWidth());
        assertEquals(32, info.getHeight());
        assertEquals(ImageFormat.TIFF, info.getFormat());

        assertThrows(() -> ImageInfo.get(Medias.create("image2.tiff")), "[image2.tiff] " + ImageInfo.ERROR_READ);
        assertFalse(ImageInfo.isImage(Medias.create("raster.xml")));
    }

    /**
     * Test skipped error tool.
     * 
     * @throws ReflectiveOperationException If error.
     */
    @Test
    void testSkippedError() throws ReflectiveOperationException
    {
        final Method method = ImageHeaderReaderAbstract.class.getDeclaredMethod("checkSkippedError",
                                                                                Long.TYPE,
                                                                                Integer.TYPE);

        assertThrowsIo(() ->
        {
            try
            {
                method.invoke(ImageInfo.class, Long.valueOf(1), Integer.valueOf(0));
            }
            catch (final Exception exception)
            {
                if (exception.getCause() instanceof IOException)
                {
                    throw exception.getCause();
                }
            }
        }, "Skipped 1 bytes instead of 0");
    }
}
