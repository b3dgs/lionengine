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
package com.b3dgs.lionengine.examples.core;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.b3dgs.lionengine.Configuration;
import com.b3dgs.lionengine.DefinitionException;
import com.b3dgs.lionengine.JsonArray;
import com.b3dgs.lionengine.JsonReader;
import com.b3dgs.lionengine.Log;
import com.b3dgs.lionengine.LogLevel;
import com.b3dgs.lionengine.Registry;

/**
 * Reads a definition file and creates data-driven objects from a registry (REQ-DEF-001 to REQ-DEF-003, REQ-RT-007).
 *
 * <p>
 * Steps: register a factory per registry id at startup, read the JSON file with {@link JsonReader}, check its
 * {@code format} and {@code version}, then create each object with {@link Registry#create(String, Configuration)}.
 * Every member must be read: a typo such as {@code "damgae"} fails with a {@link DefinitionException} giving the file,
 * line, column, JSON path, registry id and parameter. Headless; {@link #main(String[])} logs the result.
 * </p>
 *
 * @since 11.0.0
 */
public final class DefinitionExample
{
    /** Kind of the definition file. */
    public static final String FORMAT = "example.arsenal";
    /** Newest version of the definition file this example reads. */
    public static final int VERSION = 1;
    /** Registry id of a sword. */
    public static final String SWORD = "example:sword";
    /** Registry id of a bow. */
    public static final String BOW = "example:bow";
    /** Definition file of the example, as it would be stored under {@code resources}. */
    public static final String ARSENAL = """
        {
          "format": "example.arsenal",
          "version": 1,
          "comment": "Each weapon names a registry id and the parameters read by its factory.",
          "weapons": [
            {"type": "example:sword", "parameters": {"damage": 12, "reachMeters": 1.5}},
            {"type": "example:bow", "parameters": {"damage": 7, "rangeMeters": 30}}
          ]
        }""";

    /** Default reach of a sword, in meters. */
    private static final double DEFAULT_SWORD_REACH_METERS = 1.0;
    /** Centimeters per meter, for the log. */
    private static final double CENTIMETERS_PER_METER = 100.0;
    /** Parameter of both weapons: the damage per hit, a positive integer. */
    private static final String DAMAGE = "damage";

    /**
     * Private constructor.
     */
    private DefinitionExample()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Creates the registry of weapons, as a game does at startup.
     *
     * @return The registry with the sword and bow factories.
     */
    public static Registry<Weapon> createRegistry()
    {
        final Registry<Weapon> registry = new Registry<>();
        registry.register(SWORD, DefinitionExample::createSword);
        registry.register(BOW, DefinitionExample::createBow);
        return registry;
    }

    /**
     * Reads a definition file and creates its weapons.
     *
     * @param registry The registry of weapon factories.
     * @param utf8 The file content, in UTF-8.
     * @param source The resource key of the file, reported in errors.
     * @return The weapons, in file order.
     * @throws DefinitionException If the file is not a valid arsenal definition.
     */
    public static List<Weapon> load(Registry<Weapon> registry, byte[] utf8, String source)
    {
        final Configuration arsenal = new Configuration(new JsonReader().read(utf8, source).asObject());
        arsenal.checkFormat(FORMAT, VERSION);
        final JsonArray items = arsenal.getArray("weapons");
        final int count = items.size();
        final List<Weapon> weapons = new ArrayList<>(count);
        for (int i = 0; i < count; i++)
        {
            final Configuration item = new Configuration(items.get(i).asObject());
            weapons.add(registry.create(item.getString("type"), item.getObject("parameters")));
            item.checkAllRead();
        }
        arsenal.checkAllRead();
        return weapons;
    }

    /**
     * Runs the example: loads the arsenal and logs each weapon.
     *
     * @param args Unused.
     */
    // CHECKSTYLE IGNORE LINE: UncommentedMain
    public static void main(String[] args)
    {
        final Log log = new Log();
        log.setLevel(LogLevel.INFO);

        final List<Weapon> weapons = load(createRegistry(), ARSENAL.getBytes(StandardCharsets.UTF_8), "arsenal.json");
        log.log(LogLevel.INFO, "Weapons created:", weapons.size());
        for (int i = 0; i < weapons.size(); i++)
        {
            final Weapon weapon = weapons.get(i);
            log.log(LogLevel.INFO,
                    "Weapon damage and reach in centimeters:",
                    weapon.damage(),
                    Math.round(weapon.reachMeters() * CENTIMETERS_PER_METER));
        }
    }

    /**
     * Factory of {@link #SWORD}: {@code damage} required, {@code reachMeters} optional.
     *
     * @param configuration The parameters.
     * @return The sword.
     * @throws DefinitionException If a parameter is invalid.
     */
    private static Weapon createSword(Configuration configuration)
    {
        return new Weapon("sword",
                          readDamage(configuration),
                          configuration.getDouble("reachMeters", DEFAULT_SWORD_REACH_METERS));
    }

    /**
     * Factory of {@link #BOW}: {@code damage} and {@code rangeMeters} required.
     *
     * @param configuration The parameters.
     * @return The bow.
     * @throws DefinitionException If a parameter is invalid.
     */
    private static Weapon createBow(Configuration configuration)
    {
        return new Weapon("bow", readDamage(configuration), configuration.getDouble("rangeMeters"));
    }

    /**
     * Reads the damage, which must be positive: a check of the factory itself.
     *
     * @param configuration The parameters.
     * @return The damage.
     * @throws DefinitionException If the damage is missing, not an integer or not positive.
     */
    private static int readDamage(Configuration configuration)
    {
        final int damage = configuration.getInt(DAMAGE);
        if (damage < 1)
        {
            throw configuration.createError(DAMAGE, "Damage must be positive");
        }
        return damage;
    }

    /**
     * Weapon created from a definition: the common type of the registry.
     *
     * @param name The weapon name.
     * @param damage The damage per hit, positive.
     * @param reachMeters The reach in meters.
     * @since 11.0.0
     */
    public record Weapon(String name, int damage, double reachMeters)
    {
        // Value type of the example.
    }
}
