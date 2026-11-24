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
package com.b3dgs.lionengine.examples.resources;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import com.b3dgs.lionengine.Log;
import com.b3dgs.lionengine.LogLevel;
import com.b3dgs.lionengine.resources.ResourceException;
import com.b3dgs.lionengine.resources.ResourceKey;
import com.b3dgs.lionengine.resources.Resources;
import com.b3dgs.lionengine.resources.file.FileProvider;
import com.b3dgs.lionengine.resources.memory.MemoryProvider;

/**
 * Reads game data by key through two providers, a memory provider overriding a file provider (REQ-RES-001 to
 * REQ-RES-003, REQ-RES-006).
 *
 * <p>
 * The game builds {@link Resources} once with its providers in lookup order: here the memory provider first, then the
 * files of the game data directory. A key is read from the first provider that has it, so content written to memory
 * overrides the file with the same key. The stream belongs to the code that opens it, which closes it. A key that no
 * provider has raises a {@link ResourceException} with reason {@code MISSING}. A desktop example: Android games read
 * their data with the Android asset provider instead of {@link FileProvider}.
 * </p>
 *
 * @since 11.0.0
 */
public final class ResourcesExample
{
    /** Key of the greeting text, present in the game data directory. */
    public static final ResourceKey GREETING = new ResourceKey("text/greeting.txt");
    /** Key that no provider has. */
    public static final ResourceKey ABSENT = new ResourceKey("text/absent.txt");
    /** Greeting written to memory, which overrides the file. */
    public static final String OVERRIDE = "Hello from memory";

    /**
     * Private constructor.
     */
    private ResourcesExample()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Reads the greeting from the files, overrides it in memory, reads it again, then tries a missing key.
     *
     * @param dataDirectory The game data directory, holding {@code text/greeting.txt}.
     * @return The greeting before and after the override, and the reason of the missing key error.
     */
    public static Result run(File dataDirectory)
    {
        final MemoryProvider memory = new MemoryProvider();
        final Resources resources = new Resources(memory, new FileProvider(dataDirectory));

        final String fromFile = readText(resources, GREETING);

        try (OutputStream output = resources.openWrite(GREETING))
        {
            output.write(OVERRIDE.getBytes(StandardCharsets.UTF_8));
        }
        catch (final IOException exception)
        {
            throw new ResourceException(GREETING, ResourceException.Reason.INACCESSIBLE, exception);
        }
        final String fromMemory = readText(resources, GREETING);

        ResourceException.Reason absent = null;
        try
        {
            readText(resources, ABSENT);
        }
        catch (final ResourceException exception)
        {
            absent = exception.getReason();
        }
        return new Result(fromFile, fromMemory, absent);
    }

    /**
     * Reads a resource as UTF-8 text and closes its stream.
     *
     * @param resources The resources.
     * @param key The key.
     * @return The text.
     * @throws ResourceException If the key is missing or unreadable.
     */
    private static String readText(Resources resources, ResourceKey key)
    {
        try (InputStream input = resources.open(key))
        {
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        }
        catch (final IOException exception)
        {
            throw new ResourceException(key, ResourceException.Reason.INACCESSIBLE, exception);
        }
    }

    /**
     * Runs the example on a temporary data directory and logs the result.
     *
     * @param args Unused.
     * @throws IOException If the temporary data directory cannot be written.
     */
    // CHECKSTYLE IGNORE LINE: UncommentedMain
    public static void main(String[] args) throws IOException
    {
        final Path directory = Files.createTempDirectory("lionengine-data");
        final Path text = Files.createDirectory(directory.resolve("text"));
        final Path greeting = Files.writeString(text.resolve("greeting.txt"), "Hello from the files");
        directory.toFile().deleteOnExit();
        text.toFile().deleteOnExit();
        greeting.toFile().deleteOnExit();

        final Result result = run(directory.toFile());
        final Log log = new Log();
        log.setLevel(LogLevel.INFO);
        log.log(LogLevel.INFO, "Greeting overridden in memory, characters before and after:",
                result.fromFile().length(),
                result.fromMemory().length());
    }

    /**
     * Result of the example.
     *
     * @param fromFile The greeting read from the file.
     * @param fromMemory The greeting read after the override in memory.
     * @param absent The reason of the error raised for the missing key.
     * @since 11.0.0
     */
    public record Result(String fromFile, String fromMemory, ResourceException.Reason absent)
    {
        // Value type of the example.
    }
}
