/*
 * Copyright (C) 2013-2021 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game;

import java.io.IOException;

import com.b3dgs.lionengine.io.FileReading;
import com.b3dgs.lionengine.io.FileWriting;

/**
 * Represents something which can be saved and loaded.
 */
public interface Persistable
{
    /**
     * Save to local storage.
     * 
     * @param writing The output writer.
     * @throws IOException If error on writing.
     */
    void save(FileWriting writing) throws IOException;

    /**
     * Load from local storage.
     * 
     * @param reading The input reader.
     * @throws IOException If error on reading.
     */
    void load(FileReading reading) throws IOException;
}
