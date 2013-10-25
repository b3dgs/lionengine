/*
 * Copyright (C) 2013 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.example.lionheart;

import java.io.IOException;

import com.b3dgs.lionengine.file.FileReading;
import com.b3dgs.lionengine.file.FileWriting;
import com.b3dgs.lionengine.game.ObjectTypeUtility;

/**
 * List of world types.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public enum WorldType
{
    /** Swamp world. */
    SWAMP(Music.SWAMP);

    /**
     * Load type from its saved format.
     * 
     * @param file The file reading.
     * @return The loaded type.
     * @throws IOException If error.
     */
    public static WorldType load(FileReading file) throws IOException
    {
        return WorldType.valueOf(file.readString());
    }

    /** World music. */
    private Music music;

    /**
     * Constructor.
     * 
     * @param music The music type.
     */
    private WorldType(Music music)
    {
        this.music = music;
    }

    /**
     * Save the world type.
     * 
     * @param file The file writing.
     * @throws IOException If error.
     */
    public void save(FileWriting file) throws IOException
    {
        file.writeString(name());
    }

    /**
     * Get the music type.
     * 
     * @return The music type.
     */
    public Music getMusic()
    {
        return music;
    }

    /**
     * Get the name as a path (lower case).
     * 
     * @return The name.
     */
    public String getPathName()
    {
        return ObjectTypeUtility.getPathName(this);
    }

    /**
     * Get the title name (first letter as upper).
     * 
     * @return The title name.
     */
    @Override
    public String toString()
    {
        return ObjectTypeUtility.toString(this);
    }
}
