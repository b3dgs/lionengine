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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.graphics.AnimationLoop;
import com.b3dgs.lionengine.graphics.PngWriter;
import com.b3dgs.lionengine.resources.ResourceKey;
import com.b3dgs.lionengine.resources.Resources;
import com.b3dgs.lionengine.resources.memory.MemoryProvider;
import com.b3dgs.lionengine.testsupport.RecordingBackend;

/**
 * Runs {@link AnimationDefinitionExample} on a definition and an image written in memory (REQ-DOC-001,
 * REQ-ANIM-001).
 */
final class AnimationDefinitionExampleTest
{
    /** The hero definition, as in the example. */
    private static final String HERO = """
        {
          "format": "lionengine.sheet",
          "version": 1,
          "image": "sprites/hero.png",
          "cellWidth": 16,
          "cellHeight": 16,
          "animations": {
            "walk": {"frames": [0, 1, 2, 3], "frameDuration": 0.1, "loop": "loop"},
            "attack": {"frames": [4, 5, 6], "frameDuration": 0.1, "frameDurations": [0.05, 0.05, 0.3], "loop": "once"}
          }
        }
        """;
    /** Key of the hero image: 4 by 2 cells. */
    private static final ResourceKey IMAGE = new ResourceKey("sprites/hero.png");

    /**
     * Writes bytes into memory.
     *
     * @param memory The memory provider.
     * @param key The key.
     * @param bytes The bytes.
     * @throws IOException If the bytes cannot be written.
     */
    private static void write(MemoryProvider memory, ResourceKey key, byte[] bytes) throws IOException
    {
        try (OutputStream output = memory.openWrite(key))
        {
            output.write(bytes);
        }
    }

    /**
     * The animations of the definition are read in the order of the file, with their frames, loop mode and frame
     * durations; the animation built in code fits the sheet.
     *
     * @throws IOException If a resource cannot be written.
     */
    @Test
    void testAnimations() throws IOException
    {
        final MemoryProvider memory = new MemoryProvider();
        write(memory, IMAGE, PngWriter.rgba(64, 32, new int[64 * 32]).write());
        write(memory, AnimationDefinitionExample.HERO, HERO.getBytes(StandardCharsets.UTF_8));

        final AnimationDefinitionExample.Result result = AnimationDefinitionExample.run(new Resources(memory),
                                                                                        new RecordingBackend());

        assertEquals(List.of("walk", "attack"), result.names());
        assertEquals(4, result.walkFrames());
        assertEquals(AnimationLoop.LOOP, result.walkLoop());
        assertEquals(0.3, result.attackLast());
        assertTrue(result.idleFits());
    }
}
