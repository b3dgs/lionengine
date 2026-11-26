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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.render.CommandBuffer;
import com.b3dgs.lionengine.resources.FakeResourceProvider;
import com.b3dgs.lionengine.resources.ResourceKey;
import com.b3dgs.lionengine.resources.Resources;
import com.b3dgs.lionengine.testsupport.RecordedCommand;
import com.b3dgs.lionengine.testsupport.RecordingBackend;

/**
 * Integration test of the template color variants: a sheet definition read through the resources, its templates
 * baked into variants cached by (source image, mapping), and two sprites drawing two color variants of one sheet
 * (REQ-SPRITE-005, REQ-SPRITE-002, REQ-SHEET-003, ADR-0026, ADR-0056, ADR-0057).
 */
final class TemplateVariantTest
{
    /** Key of the soldier definition. */
    private static final ResourceKey SOLDIER = new ResourceKey("sheets/soldier.json");
    /** Key of the soldier image: two cells of 2 by 1 pixels. */
    private static final ResourceKey IMAGE = new ResourceKey("sprites/soldier.png");
    /** Pixels of the soldier image: a magenta and a half-transparent magenta cape, a skin color, a dark outline. */
    private static final int[] PIXELS =
    {
        0xFFFF00FF, 0x80FF00FF, 0xFFF0C090, 0xFF202020
    };
    /** Definition of the soldier, with three templates: two of them map the same colors. */
    private static final String DEFINITION = """
        {
          "format": "lionengine.sheet",
          "version": 1,
          "image": "sprites/soldier.png",
          "cellWidth": 2,
          "cellHeight": 1,
          "templates": {
            "red": {"#FF00FF": "#FF0000", "#80FF00FF": "#80FF0000"},
            "blue": {"#ff00ff": "#0000ff", "#80ff00ff": "#800000ff", "comment": "Second player"},
            "crimson": {"#80FF00FF": "#80FF0000", "#FFFF00FF": "#FFFF0000"}
          }
        }
        """;
    /** Logical frame size in pixels. */
    private static final int SIZE = 64;

    /** The render backend. */
    private RecordingBackend backend;
    /** The image cache. */
    private ImageCache cache;
    /** The group of the sheet. */
    private SheetGroup group;
    /** The soldier sheet. */
    private SpriteSheet sheet;

    /**
     * Loads the soldier sheet of each test from its definition.
     */
    @BeforeEach
    void prepare()
    {
        final FakeResourceProvider provider = new FakeResourceProvider(false);
        provider.put(IMAGE, PngWriter.rgba(4, 1, PIXELS.clone()).write());
        provider.put(SOLDIER, DEFINITION.getBytes(StandardCharsets.UTF_8));
        final Resources resources = new Resources(provider);
        backend = new RecordingBackend();
        cache = new ImageCache(resources, backend);
        group = new SheetGroup(cache);
        sheet = new SheetLoader(resources).load(group, SOLDIER);
    }

    /**
     * Bakes a template of the sheet.
     *
     * @param name The name of the template.
     * @return The variant of the sheet image.
     */
    private Image variant(String name)
    {
        return cache.bake(sheet.getImage(), ColorTransform.NONE.withMapping(sheet.getTemplate(name)));
    }

    /**
     * Two sprites over two variants of one sheet draw two textures whose pixels are the source pixels with the key
     * colors replaced, alpha included; the other colors and the source never change.
     */
    @Test
    void testTwoVariants()
    {
        final Image red = variant("red");
        final Image blue = variant("blue");
        final CommandBuffer commands = new CommandBuffer(SIZE, SIZE, 2);
        final Sprite redSoldier = new Sprite(sheet, red);
        final Sprite blueSoldier = new Sprite(sheet, blue);
        redSoldier.setPosition(16.0, 16.0);
        blueSoldier.setPosition(32.0, 16.0);
        blueSoldier.setFrame(1);

        redSoldier.render(commands);
        blueSoldier.render(commands);
        backend.present(commands);
        final List<RecordedCommand> frame = backend.getLastFrame();

        assertEquals(2, frame.size());
        assertEquals(red.getTexture(), frame.get(0).getTexture());
        assertEquals(blue.getTexture(), frame.get(1).getTexture());
        assertEquals(2, frame.get(1).getSourceX());
        assertArrayEquals(new int[]
        {
            0xFFFF0000, 0x80FF0000, 0xFFF0C090, 0xFF202020
        }, backend.getPixels(red.getTexture()));
        assertArrayEquals(new int[]
        {
            0xFF0000FF, 0x800000FF, 0xFFF0C090, 0xFF202020
        }, backend.getPixels(blue.getTexture()));
        assertArrayEquals(PIXELS, backend.getPixels(sheet.getImage().getTexture()));
        assertEquals(3, cache.size());
        red.release();
        blue.release();
        group.release();
        assertEquals(0, cache.size());
    }

    /**
     * Variants are cached by (source image, mapping), not by template name: two templates mapping the same colors,
     * written in another order or letter case, share one variant and its texture.
     */
    @Test
    void testSharedByMapping()
    {
        final Image red = variant("red");
        final Image crimson = variant("crimson");

        assertEquals(sheet.getTemplate("red"), sheet.getTemplate("crimson"));
        assertEquals(red.getTexture(), crimson.getTexture());
        assertEquals("+mapping(#80FF00FF=#80FF0000,#FFFF00FF=#FFFF0000)", red.getDiscriminator());
        assertEquals(2, cache.size());
        red.release();
        assertTrue(cache.has(IMAGE, crimson.getDiscriminator()));
        crimson.release();
        assertFalse(cache.has(IMAGE, red.getDiscriminator()));
        group.release();
    }

    /**
     * A template combines with the other parts of a color transform: the hit flash of a red soldier is one more
     * variant, its own image, and the mapping applies first.
     */
    @Test
    void testWithOtherParts()
    {
        final ColorTransform red = ColorTransform.NONE.withMapping(sheet.getTemplate("red"));
        final Image redGray = cache.bake(sheet.getImage(), red.withGrading(ColorMatrix.saturation(0.0)));
        final Image redFlash = cache.bake(sheet.getImage(), red.withMask(0xFFFFFF, 0.5));

        assertEquals(0xFF363636, redGray.getPixel(0, 0));
        assertEquals(0xFFFF8080, redFlash.getPixel(0, 0));
        assertNotEquals(redGray.getTexture(), redFlash.getTexture());
        assertEquals("+mapping(#80FF00FF=#80FF0000,#FFFF00FF=#FFFF0000),mask(#FFFFFF,0.5)",
                     redFlash.getDiscriminator());
        assertEquals(3, cache.size());
        redGray.release();
        redFlash.release();
        group.release();
        assertEquals(0, cache.size());
    }

    /**
     * An empty template bakes nothing: it gives a new handle on the sheet image.
     */
    @Test
    void testEmptyTemplate()
    {
        final Image none = cache.bake(sheet.getImage(), ColorTransform.NONE.withMapping(ColorMapping.NONE));

        assertEquals(sheet.getImage().getTexture(), none.getTexture());
        assertEquals(1, cache.size());
        group.release();
        assertEquals(1, cache.size());
        none.release();
        assertEquals(0, cache.size());
    }
}
