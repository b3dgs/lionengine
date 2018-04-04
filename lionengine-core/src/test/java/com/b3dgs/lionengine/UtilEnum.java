/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.b3dgs.lionengine;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Allows to create enum dynamically.
 * 
 * @param <E> The enum type.
 */
@SuppressWarnings("restriction")
public final class UtilEnum<E extends Enum<E>>
{
    /** Factory. */
    private static final sun.reflect.ReflectionFactory FACTORY = sun.reflect.ReflectionFactory.getReflectionFactory();
    /** Empty class. */
    private static final Class<?>[] EMPTY_CLASS_ARRAY = new Class[0];
    /** Empty object. */
    private static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];

    /** Values field. */
    private static final String VALUES_FIELD = "$VALUES";
    /** Ordinal field. */
    private static final String ORDINAL_FIELD = "ordinal";
    /** Modifiers field. */
    private static final String MODIFIERS_FIELD = "modifiers";

    /**
     * Make a new enum instance, without adding it to the values array and using the default ordinal of 0.
     * 
     * @param clazz The enum type.
     * @param value The enum name.
     * @return The enum instance.
     */
    public static <E extends Enum<E>> E make(Class<E> clazz, String value)
    {
        return new UtilEnum<>(clazz).make(value);
    }

    /** Enum type. */
    private final Class<E> clazz;
    /** Switch fields. */
    private final Collection<Field> switchFields;
    /** Operation performed. */
    private final Deque<Memento> undoStack = new LinkedList<>();

    /**
     * Construct an EnumBuster for the given enum class and keep the switch statements of the classes specified in
     * switchUsers in sync with the enum values.
     * 
     * @param clazz The enum type.
     * @param switchUsers The classes using the hack.
     */
    public UtilEnum(Class<E> clazz, Class<?>... switchUsers)
    {
        this.clazz = clazz;
        switchFields = findRelatedSwitchFields(switchUsers);
    }

    /**
     * Make a new enum instance, without adding it to the values array and using the default ordinal of 0.
     * 
     * @param value The enum name.
     * @return The enum instance.
     */
    public E make(String value)
    {
        return make(value, 0, EMPTY_CLASS_ARRAY, EMPTY_OBJECT_ARRAY);
    }

    /**
     * Make a new enum instance with the given ordinal.
     * 
     * @param value The enum name.
     * @param ordinal The enum ordinal.
     * @return The enum instance.
     */
    public E make(String value, int ordinal)
    {
        return make(value, ordinal, EMPTY_CLASS_ARRAY, EMPTY_OBJECT_ARRAY);
    }

    /**
     * Make a new enum instance with the given value, ordinal and additional parameters. The additionalTypes is used to
     * match the constructor accurately.
     * 
     * @param value The enum name.
     * @param ordinal The enum ordinal.
     * @param additionalTypes The additional types.
     * @param additional The additional parameters.
     * @return The enum instance.
     */
    public E make(String value, int ordinal, Class<?>[] additionalTypes, Object[] additional)
    {
        try
        {
            undoStack.push(new Memento());
            final sun.reflect.ConstructorAccessor ca = findConstructorAccessor(additionalTypes, clazz);
            return constructEnum(clazz, ca, value, ordinal, additional);
        }
        catch (final NoSuchMethodException exception)
        {
            throw new IllegalArgumentException("Could not create enum", exception);
        }
        catch (final IllegalAccessException exception)
        {
            throw new IllegalArgumentException("Could not create enum", exception);
        }
        catch (final InstantiationException exception)
        {
            throw new IllegalArgumentException("Could not create enum", exception);
        }
        catch (final InvocationTargetException exception)
        {
            throw new IllegalArgumentException("Could not create enum", exception);
        }
    }

    /**
     * This method adds the given enum into the array inside the enum class. If the enum already contains that
     * particular value, then the value is overwritten with our enum. Otherwise it is added at the end of the array.
     * <p>
     * In addition, if there is a constant field in the enum class pointing to an enum with our value, then we replace
     * that with our enum instance.
     * </p>
     * <p>
     * The ordinal is either set to the existing position or to the last value.
     * </p>
     * <p>
     * Warning: This should probably never be called, since it can cause permanent changes to the enum values. Use only
     * in extreme conditions.
     * </p>
     *
     * @param enumConstant the enum to add
     */
    public void addByValue(E enumConstant)
    {
        try
        {
            undoStack.push(new Memento());
            final Field valuesField = findValuesField();
            final E[] values = values();
            for (int i = 0; i < values.length; i++)
            {
                final E value = values[i];
                if (value.name().equals(enumConstant.name()))
                {
                    setOrdinal(enumConstant, value.ordinal());
                    values[i] = enumConstant;
                    replaceConstant(enumConstant);
                    return;
                }
            }
            final E[] newValues = Arrays.copyOf(values, values.length + 1);
            newValues[newValues.length - 1] = enumConstant;
            setStaticFinal(valuesField, newValues);

            final int ordinal = newValues.length - 1;
            setOrdinal(enumConstant, ordinal);
            addSwitchCase();
        }
        catch (final NoSuchFieldException exception)
        {
            throw new IllegalArgumentException("Could not set the enum", exception);
        }
        catch (final IllegalAccessException exception)
        {
            throw new IllegalArgumentException("Could not set the enum", exception);
        }
    }

    /**
     * Delete the enum from the values array and set the constant pointer to <code>null</code>.
     *
     * @param enumConstant the enum to delete from the type.
     * @return <code>true</code> if the enum was found and deleted, <code>false</code> else.
     */
    public boolean deleteByValue(E enumConstant)
    {
        try
        {
            undoStack.push(new Memento());
            final E[] values = values();
            for (int i = 0; i < values.length; i++)
            {
                final E value = values[i];
                if (value.name().equals(enumConstant.name()))
                {
                    final E[] newValues = Arrays.copyOf(values, values.length - 1);
                    System.arraycopy(values, i + 1, newValues, i, values.length - i - 1);
                    for (int j = i; j < newValues.length; j++)
                    {
                        setOrdinal(newValues[j], j);
                    }
                    final Field valuesField = findValuesField();
                    setStaticFinal(valuesField, newValues);
                    removeSwitchCase(i);
                    blankOutConstant(enumConstant);
                    return true;
                }
            }
        }
        catch (final NoSuchFieldException exception)
        {
            throw new IllegalArgumentException("Could not set the enum", exception);
        }
        catch (final IllegalAccessException exception)
        {
            throw new IllegalArgumentException("Could not set the enum", exception);
        }
        return false;
    }

    /**
     * Undo the state right back to the beginning when the UtilEnum was created.
     */
    public void restore()
    {
        while (undo())
        {
            continue;
        }
    }

    /**
     * Undo the previous operation.
     * 
     * @return <code>true</code> if undo, <code>false</code> else.
     */
    public boolean undo()
    {
        final Memento memento = undoStack.poll();
        if (memento == null)
        {
            return false;
        }
        try
        {
            memento.undo();
        }
        catch (final NoSuchFieldException exception)
        {
            throw new IllegalStateException("Unable to undo", exception);
        }
        catch (final IllegalAccessException exception)
        {
            throw new IllegalStateException("Unable to undo", exception);
        }
        return true;
    }

    /**
     * Get the constructor accessor.
     * 
     * @param additionalParameterTypes The additional parameter types.
     * @param clazz The enum type.
     * @return The constructor accessor.
     * @throws NoSuchMethodException If error.
     */
    private sun.reflect.ConstructorAccessor findConstructorAccessor(Class<?>[] additionalParameterTypes, Class<E> clazz)
            throws NoSuchMethodException
    {
        final Class<?>[] parameterTypes = new Class[additionalParameterTypes.length + 2];
        parameterTypes[0] = String.class;
        parameterTypes[1] = int.class;
        System.arraycopy(additionalParameterTypes, 0, parameterTypes, 2, additionalParameterTypes.length);
        final Constructor<E> cstr = clazz.getDeclaredConstructor(parameterTypes);
        return FACTORY.newConstructorAccessor(cstr);
    }

    /**
     * Create the enum.
     * 
     * @param clazz The enum type.
     * @param ca The constructor accessor.
     * @param value The enum name.
     * @param ord The enum ordinal.
     * @param more The additional parameters.
     * @return The enum instance.
     * @throws InvocationTargetException If error.
     * @throws InstantiationException If error.
     * @throws IllegalArgumentException If error.
     */
    private E constructEnum(Class<E> clazz, sun.reflect.ConstructorAccessor ca, String value, int ord, Object[] more)
            throws IllegalArgumentException,
            InstantiationException,
            InvocationTargetException
    {
        final Object[] parms = new Object[more.length + 2];
        parms[0] = value;
        parms[1] = Integer.valueOf(ord);
        System.arraycopy(more, 0, parms, 2, more.length);
        return clazz.cast(ca.newInstance(parms));
    }

    /**
     * The only time we ever add a new enum is at the end. Thus all we need to do is expand the switch map arrays by one
     * empty slot.
     */
    private void addSwitchCase()
    {
        try
        {
            for (final Field switchField : switchFields)
            {
                int[] switches = (int[]) switchField.get(null);
                switches = Arrays.copyOf(switches, switches.length + 1);
                setStaticFinal(switchField, switches);
            }
        }
        catch (final Exception exception)
        {
            throw new IllegalArgumentException("Could not fix switch", exception);
        }
    }

    /**
     * Replace the enum constant.
     * 
     * @param enumConstant The enum constant.
     * @throws IllegalAccessException If error.
     * @throws NoSuchFieldException If error.
     */
    private void replaceConstant(E enumConstant) throws IllegalAccessException, NoSuchFieldException
    {
        final Field[] fields = clazz.getDeclaredFields();
        for (final Field field : fields)
        {
            if (field.getName().equals(enumConstant.name()))
            {
                setStaticFinal(field, enumConstant);
            }
        }
    }

    /**
     * Blank out constant.
     * 
     * @param enumConstant The enum constant.
     * @throws IllegalAccessException If error.
     * @throws NoSuchFieldException If error.
     */
    private void blankOutConstant(E enumConstant) throws IllegalAccessException, NoSuchFieldException
    {
        final Field[] fields = clazz.getDeclaredFields();
        for (final Field field : fields)
        {
            if (field.getName().equals(enumConstant.name()))
            {
                setStaticFinal(field, null);
            }
        }
    }

    /**
     * Set ordinal value.
     * 
     * @param enumConstant The enum constant.
     * @param ordinal The ordinal value.
     * @throws NoSuchFieldException If error.
     * @throws IllegalAccessException If error.
     */
    private void setOrdinal(E enumConstant, int ordinal) throws NoSuchFieldException, IllegalAccessException
    {
        final Field ordinalField = Enum.class.getDeclaredField(ORDINAL_FIELD);
        ordinalField.setAccessible(true);
        ordinalField.set(enumConstant, Integer.valueOf(ordinal));
    }

    /**
     * Method to find the values field, set it to be accessible, and return it.
     *
     * @return the values array field for the enum.
     * @throws NoSuchFieldException If the field could not be found
     */
    private Field findValuesField() throws NoSuchFieldException
    {
        Field valuesField = null;
        for (final Field field : clazz.getDeclaredFields())
        {
            if (field.getName().contains(VALUES_FIELD))
            {
                valuesField = field;
                break;
            }
        }
        if (valuesField == null)
        {
            throw new NoSuchFieldException("Field not found: " + VALUES_FIELD);
        }
        valuesField.setAccessible(true);
        return valuesField;
    }

    /**
     * Find related switch.
     * 
     * @param switchUsers The switch users.
     * @return The associated fields.
     */
    private Collection<Field> findRelatedSwitchFields(Class<?>[] switchUsers)
    {
        final Collection<Field> result = new ArrayList<>();
        for (final Class<?> switchUser : switchUsers)
        {
            final Class<?>[] clazzes = switchUser.getDeclaredClasses();
            for (final Class<?> suspect : clazzes)
            {
                final Field[] fields = suspect.getDeclaredFields();
                for (final Field field : fields)
                {
                    if (field.getName().startsWith("$SwitchMap$" + clazz.getSimpleName()))
                    {
                        field.setAccessible(true);
                        result.add(field);
                    }
                }
            }
        }
        return result;
    }

    /**
     * Remove the switch case.
     * 
     * @param ordinal The ordinal.
     */
    private void removeSwitchCase(int ordinal)
    {
        try
        {
            for (final Field switchField : switchFields)
            {
                final int[] switches = (int[]) switchField.get(null);
                final int[] newSwitches = Arrays.copyOf(switches, switches.length - 1);
                System.arraycopy(switches, ordinal + 1, newSwitches, ordinal, switches.length - ordinal - 1);
                setStaticFinal(switchField, newSwitches);
            }
        }
        catch (final Exception e)
        {
            throw new IllegalArgumentException("Could not fix switch", e);
        }
    }

    /**
     * Get enum values.
     * 
     * @return The enum values.
     * @throws NoSuchFieldException If error.
     * @throws IllegalAccessException If error.
     */
    @SuppressWarnings("unchecked")
    private E[] values() throws NoSuchFieldException, IllegalAccessException
    {
        final Field valuesField = findValuesField();
        return (E[]) valuesField.get(null);
    }

    /**
     * Remember enum class.
     */
    private class Memento
    {
        /** Enum values. */
        private final E[] values;
        /** Switch fields saved. */
        private final Map<Field, int[]> savedSwitchFieldValues = new HashMap<>();

        /**
         * Constructor.
         * 
         * @throws IllegalAccessException If error.
         */
        private Memento() throws IllegalAccessException
        {
            try
            {
                values = values().clone();
                for (final Field switchField : switchFields)
                {
                    final int[] switchArray = (int[]) switchField.get(null);
                    savedSwitchFieldValues.put(switchField, switchArray.clone());
                }
            }
            catch (final Exception exception)
            {
                throw new IllegalArgumentException("Could not create the class", exception);
            }
        }

        /**
         * Undo.
         * 
         * @throws NoSuchFieldException If error.
         * @throws IllegalAccessException If error.
         */
        private void undo() throws NoSuchFieldException, IllegalAccessException
        {
            final Field valuesField = findValuesField();
            setStaticFinal(valuesField, values);

            for (int i = 0; i < values.length; i++)
            {
                setOrdinal(values[i], i);
            }

            final Map<String, E> valuesMap = new HashMap<>();
            for (final E enumValue : values)
            {
                valuesMap.put(enumValue.name(), enumValue);
            }
            final Field[] constantEnumFields = clazz.getDeclaredFields();
            for (final Field constantEnumField : constantEnumFields)
            {
                final E enumValue = valuesMap.get(constantEnumField.getName());
                if (enumValue != null)
                {
                    setStaticFinal(constantEnumField, enumValue);
                }
            }

            for (final Map.Entry<Field, int[]> entry : savedSwitchFieldValues.entrySet())
            {
                final Field field = entry.getKey();
                final int[] mappings = entry.getValue();
                setStaticFinal(field, mappings);
            }
        }
    }

    /**
     * Set the static final field.
     * 
     * @param field The field.
     * @param value The new value.
     * @throws NoSuchFieldException If error.
     * @throws IllegalAccessException If error.
     */
    public static void setStaticFinal(Field field, Object value) throws NoSuchFieldException, IllegalAccessException
    {
        field.setAccessible(true);

        final Field modifiersField = Field.class.getDeclaredField(MODIFIERS_FIELD);
        modifiersField.setAccessible(true);

        int modifiers = modifiersField.getInt(field);
        modifiers &= ~Modifier.FINAL;
        modifiersField.setInt(field, modifiers);

        final sun.reflect.FieldAccessor accessor = FACTORY.newFieldAccessor(field, false);
        accessor.set(null, value);
    }
}
