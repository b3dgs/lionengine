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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.b3dgs.lionengine.DefinitionException;
import com.b3dgs.lionengine.JsonReader;
import com.b3dgs.lionengine.resources.FakeResourceProvider;
import com.b3dgs.lionengine.resources.ResourceException;
import com.b3dgs.lionengine.resources.ResourceKey;
import com.b3dgs.lionengine.resources.Resources;
import com.b3dgs.lionengine.resources.StreamProvider;
import com.b3dgs.lionengine.testsupport.RecordingBackend;

/**
 * Test {@link SheetLoader}: a valid definition, defaults, each missing or invalid field, unknown fields, checks
 * against the image, and resource errors (REQ-SHEET-003, REQ-DEF-001, REQ-DEF-002, ADR-0032, ADR-0033).
 */
final class SheetLoaderTest
{
    /** Key of the definition. */
    private static final ResourceKey KEY = new ResourceKey("sheets/hero.json");
    /** Key of the image, 64 by 32 pixels: 8 cells of 16 pixels. */
    private static final ResourceKey IMAGE = new ResourceKey("sprites/hero.png");
    /** Names of the required members, in order. */
    private static final String[] REQUIRED =
    {
        "format", "version", "image", "cellWidth", "cellHeight"
    };
    /** Required members of a valid definition, in the order of {@link #REQUIRED}. */
    private static final String[] VALID =
    {
        "\"format\": \"lionengine.sheet\"", "\"version\": 1", "\"image\": \"sprites/hero.png\"", "\"cellWidth\": 16",
        "\"cellHeight\": 16"
    };
    /** A valid animation, without its braces. */
    private static final String IDLE = "\"frames\": [0, 1], \"frameDuration\": 0.1, \"loop\": \"loop\"";

    /**
     * Builds a definition from the valid required members, one of them replaced or one member added.
     *
     * @param name The name of the member to replace, or of the member to add.
     * @param member The member, {@code null} to remove the required member.
     * @return The definition text.
     */
    private static String definition(String name, String member)
    {
        final List<String> members = new ArrayList<>();
        boolean replaced = false;
        for (int i = 0; i < REQUIRED.length; i++)
        {
            if (REQUIRED[i].equals(name))
            {
                replaced = true;
                if (member != null)
                {
                    members.add(member);
                }
            }
            else
            {
                members.add(VALID[i]);
            }
        }
        if (!replaced)
        {
            members.add(member);
        }
        return "{" + String.join(", ", members) + "}";
    }

    /**
     * Builds an animation member named {@code idle}.
     *
     * @param content The content of the animation, without its braces.
     * @return The member.
     */
    private static String idle(String content)
    {
        return "\"animations\": {\"idle\": {" + content + "}}";
    }

    /**
     * Returns the invalid members and the path of the error.
     *
     * @return The cases: name of the member, member, expected path.
     */
    static Stream<Arguments> invalid()
    {
        return Stream.of(Arguments.of("format", "\"format\": \"lionengine.map\"", "$.format"),
                         Arguments.of("version", "\"version\": 2", "$.version"),
                         Arguments.of("version", "\"version\": 0", "$.version"),
                         Arguments.of("image", "\"image\": \"../hero.png\"", "$.image"),
                         Arguments.of("image", "\"image\": 3", "$.image"),
                         Arguments.of("cellWidth", "\"cellWidth\": 0", "$.cellWidth"),
                         Arguments.of("cellWidth", "\"cellWidth\": 16.5", "$.cellWidth"),
                         Arguments.of("cellHeight", "\"cellHeight\": -16", "$.cellHeight"),
                         Arguments.of("columns", "\"columns\": 0", "$.columns"),
                         Arguments.of("rows", "\"rows\": -1", "$.rows"),
                         Arguments.of("margin", "\"margin\": -1", "$.margin"),
                         Arguments.of("spacing", "\"spacing\": \"2\"", "$.spacing"),
                         Arguments.of("pivotX", "\"pivotX\": \"center\"", "$.pivotX"),
                         Arguments.of("pivotY", "\"pivotY\": null", "$.pivotY"),
                         Arguments.of("scale", "\"scale\": 2", "$.scale"),
                         Arguments.of("animations", "\"animations\": []", "$.animations"),
                         Arguments.of("animations", "\"animations\": {\"idle\": 1}", "$.animations.idle"),
                         Arguments.of("animations",
                                      idle("\"frames\": [], \"frameDuration\": 0.1, \"loop\": \"loop\""),
                                      "$.animations.idle.frames"),
                         Arguments.of("animations",
                                      idle("\"frames\": [0, -1], \"frameDuration\": 0.1, \"loop\": \"loop\""),
                                      "$.animations.idle.frames[1]"),
                         Arguments.of("animations",
                                      idle("\"frames\": [0.5], \"frameDuration\": 0.1, \"loop\": \"loop\""),
                                      "$.animations.idle.frames[0]"),
                         Arguments.of("animations",
                                      idle("\"frames\": [7, 8], \"frameDuration\": 0.1, \"loop\": \"loop\""),
                                      "$.animations.idle.frames[1]"),
                         Arguments.of("animations",
                                      idle("\"frames\": [0], \"frameDuration\": 0, \"loop\": \"loop\""),
                                      "$.animations.idle.frameDuration"),
                         Arguments.of("animations", idle("\"frames\": [0], \"loop\": \"loop\""), "$.animations.idle"),
                         Arguments.of("animations",
                                      idle(IDLE + ", \"frameDurations\": [0.1]"),
                                      "$.animations.idle.frameDurations"),
                         Arguments.of("animations",
                                      idle(IDLE + ", \"frameDurations\": [0.1, -0.2]"),
                                      "$.animations.idle.frameDurations[1]"),
                         Arguments.of("animations",
                                      idle("\"frames\": [0], \"frameDuration\": 0.1, \"loop\": \"bounce\""),
                                      "$.animations.idle.loop"),
                         Arguments.of("animations",
                                      idle("\"frames\": [0], \"frameDuration\": 0.1"),
                                      "$.animations.idle"),
                         Arguments.of("animations", idle(IDLE + ", \"speed\": 2"), "$.animations.idle.speed"),
                         Arguments.of("templates", "\"templates\": 1", "$.templates"),
                         Arguments.of("templates", "\"templates\": {\"red\": \"#FF0000\"}", "$.templates.red"),
                         Arguments.of("templates",
                                      "\"templates\": {\"red\": {\"#FF00F\": \"#FF0000\"}}",
                                      "$.templates.red['#FF00F']"),
                         Arguments.of("templates",
                                      "\"templates\": {\"red\": {\"#FF00FF\": \"#GG0000\"}}",
                                      "$.templates.red['#FF00FF']"),
                         Arguments.of("templates",
                                      "\"templates\": {\"red\": {\"#FF00FF\": 16711680}}",
                                      "$.templates.red['#FF00FF']"),
                         Arguments.of("templates",
                                      "\"templates\": {\"red\": {\"#ff00ff\": \"#FF0000\", \"#FF00FF\": \"#0000FF\"}}",
                                      "$.templates.red['#FF00FF']"),
                         Arguments.of("templates",
                                      "\"templates\": {\"red\": {\"#FF00FF\": \"#FF0000\", "
                                                   + "\"#FFFF00FF\": \"#0000FF\"}}",
                                      "$.templates.red['#FFFF00FF']"),
                         Arguments.of("columns", "\"columns\": 5", "$.columns"),
                         Arguments.of("rows", "\"rows\": 3", "$.rows"),
                         Arguments.of("cellWidth", "\"cellWidth\": 100", "$.cellWidth"),
                         Arguments.of("margin", "\"margin\": 20", "$.cellHeight"));
    }

    /** Provider of the definitions and the image. */
    private FakeResourceProvider provider;
    /** Image cache. */
    private ImageCache cache;
    /** Group receiving the sheets. */
    private SheetGroup group;
    /** Loader under test. */
    private SheetLoader loader;

    /**
     * Creates the provider holding the image, the cache, the group and the loader of each test.
     */
    @BeforeEach
    void prepare()
    {
        provider = new FakeResourceProvider(false);
        provider.put(IMAGE, PngWriter.rgba(64, 32, new int[64 * 32]).write());
        final Resources resources = new Resources(provider);
        cache = new ImageCache(resources, new RecordingBackend());
        group = new SheetGroup(cache);
        loader = new SheetLoader(resources);
    }

    /**
     * Stores the definition.
     *
     * @param text The definition text.
     */
    private void define(String text)
    {
        provider.put(KEY, text.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Loads the definition, expects a definition error, and checks that nothing was added or cached.
     *
     * @return The error.
     */
    private DefinitionException assertDefinitionError()
    {
        final DefinitionException error = assertThrows(DefinitionException.class, () -> loader.load(group, KEY));
        assertEquals(0, group.size());
        assertEquals(0, cache.size());
        assertEquals(0, provider.getUnclosed());
        return error;
    }

    /**
     * Every field is read: the grid, the pivot, the animations and the templates; comments are allowed at every level;
     * the sheet joins the group under the key of its definition.
     */
    @Test
    void testFullDefinition()
    {
        define("""
            {
              "format": "lionengine.sheet",
              "version": 1,
              "comment": "The hero",
              "image": "sprites/hero.png",
              "cellWidth": 16,
              "cellHeight": 14,
              "columns": 3,
              "rows": 2,
              "margin": 1,
              "spacing": 2,
              "pivotX": 8,
              "pivotY": 13.5,
              "animations": {
                "comment": "Frames by row",
                "idle": {"frames": [0, 1], "frameDuration": 0.1, "loop": "loop", "comment": "Breathing"},
                "jump": {"frames": [3, 4, 5, 4], "frameDuration": 0.2, "frameDurations": [0.1, 0.2, 0.3, 0.2],
                         "loop": "once"},
                "swing": {"frames": [5], "frameDuration": 1, "loop": "ping-pong"}
              },
              "templates": {
                "comment": "Team colors",
                "red": {"#FF00FF": "#FF0000", "#80FFFFFF": "#80ff0000", "comment": "Cape"},
                "blue": {}
              }
            }
            """);

        final SpriteSheet sheet = loader.load(group, KEY);

        assertSame(sheet, group.get(KEY));
        assertEquals(KEY, sheet.getId());
        assertEquals(IMAGE, sheet.getImage().getKey());
        assertEquals(16, sheet.getCellWidth());
        assertEquals(14, sheet.getCellHeight());
        assertEquals(3, sheet.getColumns());
        assertEquals(2, sheet.getRows());
        assertEquals(1, sheet.getMargin());
        assertEquals(2, sheet.getSpacing());
        assertEquals(8.0, sheet.getPivotX());
        assertEquals(13.5, sheet.getPivotY());
        assertEquals(37, sheet.getCellX(5));
        assertEquals(17, sheet.getCellY(5));
        assertEquals(List.of("red", "blue"), sheet.getTemplateNames());
        assertEquals(ColorMapping.NONE.with(0xFFFF00FF, 0xFFFF0000).with(0x80FFFFFF, 0x80FF0000),
                     sheet.getTemplate("red"));
        assertTrue(sheet.getTemplate("blue").isIdentity());
        assertFalse(sheet.hasTemplate("comment"));
        assertEquals(0, provider.getUnclosed());
    }

    /**
     * Optional fields take their defaults: counts derived from the image, no margin or spacing, pivot at the cell
     * center.
     */
    @Test
    void testDefaults()
    {
        define(definition("cellHeight", "\"cellHeight\": 15"));

        final SpriteSheet sheet = loader.load(group, KEY);

        assertEquals(4, sheet.getColumns());
        assertEquals(2, sheet.getRows());
        assertEquals(0, sheet.getMargin());
        assertEquals(0, sheet.getSpacing());
        assertEquals(8.0, sheet.getPivotX());
        assertEquals(7.5, sheet.getPivotY());
        assertTrue(sheet.getTemplateNames().isEmpty());
    }

    /**
     * A missing required field is an error naming the field, located at the object of the definition.
     *
     * @param name The missing field.
     */
    @ParameterizedTest
    @MethodSource("requiredNames")
    void testMissing(String name)
    {
        define(definition(name, null));

        final DefinitionException error = assertDefinitionError();
        assertEquals(KEY.path(), error.getSource());
        assertEquals("$", error.getPath());
        assertEquals(name, error.getParameter());
    }

    /**
     * Returns the names of the required fields.
     *
     * @return The names.
     */
    static Stream<String> requiredNames()
    {
        return Stream.of(REQUIRED);
    }

    /**
     * An invalid or unknown field is an error located at the faulty value, with the key, the line and the column; the
     * frames and the grid are checked against the image.
     *
     * @param name The name of the member replaced or added.
     * @param member The member.
     * @param path The expected JSON path of the error.
     */
    @ParameterizedTest
    @MethodSource("invalid")
    void testInvalid(String name, String member, String path)
    {
        define(definition(name, member));

        final DefinitionException error = assertDefinitionError();
        assertEquals(path, error.getPath(), error.getMessage());
        assertEquals(KEY.path(), error.getSource());
        assertEquals(1, error.getLine());
        assertTrue(error.getColumn() > 1, error.getMessage());
    }

    /**
     * A document that is not a JSON object, or not JSON at all, is an error with its position.
     */
    @Test
    void testNotAnObject()
    {
        define("[1, 2]");
        assertEquals("$", assertDefinitionError().getPath());

        define("{\n  \"format\": \"lionengine.sheet\",\n  \"version\": 1,\n}");
        final DefinitionException error = assertDefinitionError();
        assertEquals(4, error.getLine());
        assertEquals(KEY.path(), error.getSource());
    }

    /**
     * A document above the limit of the reader is refused without being read further.
     */
    @Test
    void testDocumentLimit()
    {
        final SheetLoader small = new SheetLoader(new Resources(provider), new JsonReader(32, 8, 32));
        define(definition("scale", "\"comment\": \"" + "x".repeat(4096) + "\""));

        assertThrows(DefinitionException.class, () -> small.load(group, KEY));
        assertEquals(0, provider.getUnclosed());
        assertEquals(0, group.size());
    }

    /**
     * A missing or unreadable definition, and a missing image, raise the resource exception of their key.
     */
    @Test
    void testResourceErrors()
    {
        final ResourceException missing = assertThrows(ResourceException.class, () -> loader.load(group, KEY));
        assertEquals(ResourceException.Reason.MISSING, missing.getReason());
        assertEquals(KEY, missing.getKey());

        define(definition("image", "\"image\": \"sprites/absent.png\""));
        final ResourceException image = assertThrows(ResourceException.class, () -> loader.load(group, KEY));
        assertEquals(new ResourceKey("sprites/absent.png"), image.getKey());

        provider.failOn(KEY);
        final ResourceException unreadable = assertThrows(ResourceException.class, () -> loader.load(group, KEY));
        assertEquals(ResourceException.Reason.INACCESSIBLE, unreadable.getReason());
        assertEquals(0, provider.getUnclosed());
        assertEquals(0, group.size());
        assertEquals(0, cache.size());
    }

    /**
     * A stream failing while it is read is an inaccessible definition, with the read failure as cause.
     */
    @Test
    void testReadFailure()
    {
        final StreamProvider streams = new StreamProvider();
        final IOException failure = new IOException("Read failure");
        streams.register(KEY, () -> new InputStream()
        {
            @Override
            public int read() throws IOException
            {
                throw failure;
            }
        });
        final SheetLoader failing = new SheetLoader(new Resources(streams));

        final ResourceException exception = assertThrows(ResourceException.class, () -> failing.load(group, KEY));
        assertEquals(ResourceException.Reason.INACCESSIBLE, exception.getReason());
        assertSame(failure, exception.getCause());
    }

    /**
     * A definition already in the group is refused; a released group and another thread are refused before anything
     * is read.
     *
     * @throws InterruptedException If interrupted.
     */
    @Test
    void testGroupRules() throws InterruptedException
    {
        define(definition("scale", "\"comment\": \"Hero\""));
        loader.load(group, KEY);
        assertThrows(IllegalStateException.class, () -> loader.load(group, KEY));
        final int opened = provider.getOpened();

        final IllegalStateException[] failure = new IllegalStateException[1];
        final Thread thread = new Thread(() ->
        {
            try
            {
                loader.load(group, KEY);
            }
            catch (final IllegalStateException exception)
            {
                failure[0] = exception;
            }
        }, "worker");
        thread.start();
        thread.join();
        group.release();

        assertTrue(failure[0].getMessage().startsWith("ImageCache used on the thread worker"));
        assertThrows(IllegalStateException.class, () -> loader.load(group, KEY));
        assertEquals(opened, provider.getOpened());
    }

    /**
     * {@code null} arguments are refused.
     */
    @Test
    void testNullArguments()
    {
        final Resources resources = new Resources(provider);

        assertThrows(IllegalArgumentException.class, () -> new SheetLoader(null));
        assertThrows(IllegalArgumentException.class, () -> new SheetLoader(resources, null));
        assertThrows(IllegalArgumentException.class, () -> loader.load(null, KEY));
        assertThrows(IllegalArgumentException.class, () -> loader.load(group, null));
    }

    /**
     * Colors are {@code #RRGGBB} or {@code #AARRGGBB}, with hexadecimal digits in any case.
     */
    @Test
    void testColors()
    {
        assertTrue(SheetTemplates.isColor("#a0B1c2"));
        assertTrue(SheetTemplates.isColor("#80FFFFFF"));
        assertFalse(SheetTemplates.isColor("FF00FF"));
        assertFalse(SheetTemplates.isColor("#FF00F"));
        assertFalse(SheetTemplates.isColor("#FF00FFF"));
        assertFalse(SheetTemplates.isColor("#FF00FG"));
        assertFalse(SheetTemplates.isColor(""));
    }
}
