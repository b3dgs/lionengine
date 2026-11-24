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
package com.b3dgs.lionengine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Test {@link Registry} and {@link RegistryFactory} (REQ-RT-007, REQ-DEF-002, REQ-DEF-003).
 */
final class RegistryTest
{
    /** Source name of the test documents. */
    private static final String SOURCE = "objects.json";
    /** Registry id of the test type. */
    private static final String MOVABLE = "lionengine:movable";

    /**
     * Type created by the tests.
     *
     * @param maxSpeed The maximum speed.
     * @param jumps The number of jumps.
     */
    private record Movable(double maxSpeed, int jumps)
    {
        /**
         * Creates the type from its parameters.
         *
         * @param configuration The parameters.
         */
        Movable(Configuration configuration)
        {
            this(configuration.getDouble("maxSpeed"), configuration.getInt("jumps", 1));
        }
    }

    /**
     * Ids that have the {@code namespace:name} form.
     *
     * @return The ids.
     */
    static Stream<String> validIds()
    {
        return Stream.of(MOVABLE, "a:b", "game:enemy-2", "my_game:boss_level-3", "g0:n9");
    }

    /**
     * Ids that do not have the {@code namespace:name} form.
     *
     * @return The ids.
     */
    static Stream<String> invalidIds()
    {
        return Stream.of("",
                         "lionengine",
                         ":movable",
                         "lionengine:",
                         "Lionengine:movable",
                         "lionengine:Movable",
                         "1game:enemy",
                         "game:2enemy",
                         "_game:enemy",
                         "game:-enemy",
                         "game:enemy:boss",
                         "game :enemy",
                         "game:en emy",
                         "game:énemy",
                         "game.enemy");
    }

    /**
     * Creates parameters.
     *
     * @param json The parameters, an object.
     * @return The configuration.
     */
    private static Configuration parameters(String json)
    {
        return new Configuration(new JsonReader().read(json, SOURCE).asObject());
    }

    /**
     * Creates a registry with the movable type.
     *
     * @return The registry.
     */
    private static Registry<Object> registry()
    {
        final Registry<Object> registry = new Registry<>();
        registry.register(MOVABLE, Movable::new);
        return registry;
    }

    /**
     * A registered id creates its instance from the parameters.
     */
    @Test
    void testCreate()
    {
        final Object created = registry().create(MOVABLE, parameters("{\"maxSpeed\": 6, \"comment\": \"fast\"}"));

        assertEquals(new Movable(6.0, 1), created);
    }

    /**
     * Valid ids can be registered and created.
     *
     * @param id The id.
     */
    @ParameterizedTest
    @MethodSource("validIds")
    void testValidId(String id)
    {
        final Registry<String> registry = new Registry<>();
        registry.register(id, configuration -> id);

        assertEquals(id, registry.create(id, parameters("{}")));
    }

    /**
     * Invalid ids are rejected at registration, naming the id.
     *
     * @param id The id.
     */
    @ParameterizedTest
    @MethodSource("invalidIds")
    void testInvalidIdRegistered(String id)
    {
        final Registry<String> registry = new Registry<>();
        final DefinitionException error = assertThrows(DefinitionException.class,
                                                       () -> registry.register(id, configuration -> id));

        assertEquals(Registry.ERROR_INVALID_ID + " (" + id + ")", error.getMessage());
        assertEquals(id, error.getId());
    }

    /**
     * Invalid ids are rejected at creation, located at the parameters.
     *
     * @param id The id.
     */
    @ParameterizedTest
    @MethodSource("invalidIds")
    void testInvalidIdCreated(String id)
    {
        final Registry<Object> registry = registry();
        final Configuration parameters = parameters("{}");
        final DefinitionException error = assertThrows(DefinitionException.class,
                                                       () -> registry.create(id, parameters));

        assertEquals(Registry.ERROR_INVALID_ID + " (objects.json:1:1, $, " + id + ")", error.getMessage());
    }

    /**
     * A {@code null} id is invalid.
     */
    @Test
    void testNullId()
    {
        final Registry<Object> registry = registry();

        assertThrows(DefinitionException.class, () -> registry.register(null, Movable::new));
    }

    /**
     * An unknown id fails, naming the id, located at the parameters.
     */
    @Test
    void testUnknownId()
    {
        final Registry<Object> registry = registry();
        final Configuration parameters = parameters("{}");
        final DefinitionException error = assertThrows(DefinitionException.class,
                                                       () -> registry.create("lionengine:jumpable", parameters));

        assertEquals("Unknown registry id (objects.json:1:1, $, lionengine:jumpable)", error.getMessage());
    }

    /**
     * An id registered twice fails, naming the id.
     */
    @Test
    void testDuplicateId()
    {
        final Registry<Object> registry = registry();
        final DefinitionException error = assertThrows(DefinitionException.class,
                                                       () -> registry.register(MOVABLE, Movable::new));

        assertEquals("Duplicate registry id (lionengine:movable)", error.getMessage());
    }

    /**
     * A parameter not read by the factory fails after it returns, naming the id and the parameter.
     */
    @Test
    void testUnreadParameter()
    {
        final Registry<Object> registry = registry();
        final Configuration parameters = parameters("{\"maxSpeed\": 6, \"maxSpped\": 7}");
        final DefinitionException error = assertThrows(DefinitionException.class,
                                                       () -> registry.create(MOVABLE, parameters));

        assertEquals("Unknown field (objects.json:1:29, $.maxSpped, lionengine:movable, maxSpped)",
                     error.getMessage());
    }

    /**
     * A missing parameter fails, naming the id and the parameter.
     */
    @Test
    void testMissingParameter()
    {
        final Registry<Object> registry = registry();
        final Configuration parameters = parameters("{}");
        final DefinitionException error = assertThrows(DefinitionException.class,
                                                       () -> registry.create(MOVABLE, parameters));

        assertEquals("Missing value (objects.json:1:1, $, lionengine:movable, maxSpeed)", error.getMessage());
        assertEquals(MOVABLE, error.getId());
        assertEquals("maxSpeed", error.getParameter());
    }

    /**
     * A parameter of the wrong type fails, naming the id and the parameter.
     */
    @Test
    void testWrongParameterType()
    {
        final Registry<Object> registry = registry();
        final Configuration parameters = parameters("{\"maxSpeed\": \"fast\"}");
        final DefinitionException error = assertThrows(DefinitionException.class,
                                                       () -> registry.create(MOVABLE, parameters));

        assertEquals("Expected a number, found a string (objects.json:1:14, $.maxSpeed, lionengine:movable, maxSpeed)",
                     error.getMessage());
    }

    /**
     * A factory check reports an invalid value with the id and the parameter.
     */
    @Test
    void testInvalidParameter()
    {
        final Registry<Object> registry = new Registry<>();
        registry.register(MOVABLE, configuration ->
        {
            final double speed = configuration.getDouble("maxSpeed");
            if (speed < 0.0)
            {
                throw configuration.createError("maxSpeed", "Negative speed");
            }
            return new Movable(speed, 1);
        });
        final Configuration parameters = parameters("{\"maxSpeed\": -1}");
        final DefinitionException error = assertThrows(DefinitionException.class,
                                                       () -> registry.create(MOVABLE, parameters));

        assertEquals("Negative speed (objects.json:1:14, $.maxSpeed, lionengine:movable, maxSpeed)",
                     error.getMessage());
    }

    /**
     * Nested parameters are checked too and name the id.
     */
    @Test
    void testNestedParameters()
    {
        final Registry<Object> registry = new Registry<>();
        registry.register("game:box", configuration -> Integer.valueOf(configuration.getObject("size").getInt("w")));
        final Configuration parameters = parameters("{\"size\": {\"w\": 1, \"h\": 2}}");
        final DefinitionException error = assertThrows(DefinitionException.class,
                                                       () -> registry.create("game:box", parameters));

        assertEquals("Unknown field (objects.json:1:24, $.size.h, game:box, h)", error.getMessage());
    }

    /**
     * Parameters obtained from a parent configuration take the id of the creation they serve.
     */
    @Test
    void testParametersFromParent()
    {
        final Configuration item = parameters("{\"type\": \"lionengine:movable\", \"parameters\": {}}");
        final String type = item.getString("type");
        final Configuration parameters = item.getObject("parameters");
        final Registry<Object> registry = registry();

        final DefinitionException error = assertThrows(DefinitionException.class,
                                                       () -> registry.create(type, parameters));

        assertEquals("Missing value (objects.json:1:46, $.parameters, lionengine:movable, maxSpeed)",
                     error.getMessage());
    }

    /**
     * Registries are independent instances.
     */
    @Test
    void testIndependentRegistries()
    {
        final Registry<Object> first = registry();
        final Registry<Object> second = new Registry<>();
        second.register(MOVABLE, configuration -> "second");
        final Registry<Object> empty = new Registry<>();
        final Configuration parameters = parameters("{}");

        assertInstanceOf(Movable.class, first.create(MOVABLE, parameters("{\"maxSpeed\": 1}")));
        assertEquals("second", second.create(MOVABLE, parameters("{}")));
        assertThrows(DefinitionException.class, () -> empty.create(MOVABLE, parameters));
    }

    /**
     * A configuration serves one creation.
     */
    @Test
    void testConfigurationReused()
    {
        final Registry<Object> registry = registry();
        final Configuration parameters = parameters("{\"maxSpeed\": 1}");
        registry.create(MOVABLE, parameters);

        assertThrows(IllegalStateException.class, () -> registry.create(MOVABLE, parameters));
    }

    /**
     * A factory must not return {@code null}.
     */
    @Test
    void testFactoryReturnsNull()
    {
        final Registry<Object> registry = new Registry<>();
        registry.register(MOVABLE, configuration -> null);
        final Configuration parameters = parameters("{}");

        assertThrows(IllegalStateException.class, () -> registry.create(MOVABLE, parameters));
    }

    /**
     * {@code null} arguments are rejected.
     */
    @Test
    void testNullArguments()
    {
        final Registry<Object> registry = registry();

        assertThrows(IllegalArgumentException.class, () -> registry.register("game:a", null));
        assertThrows(IllegalArgumentException.class, () -> registry.create(MOVABLE, null));
    }

    /**
     * The engine namespace.
     */
    @Test
    void testEngineNamespace()
    {
        assertEquals("lionengine", Registry.ENGINE_NAMESPACE);
    }
}
