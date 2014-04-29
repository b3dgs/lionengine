/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.editor.project;

import java.io.File;

/**
 * Represents a project and its data.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class Project
{
    /** Project name. */
    private String name;
    /** Source folder (represents the main source folder, such as <code>project/src/</code>). */
    private File sources;
    /** Resources folder (represents the main resources folder, such as <code>project/resources/</code>. */
    private File resources;
    /** Opened state. */
    private boolean opened;

    /**
     * Constructor.
     */
    public Project()
    {
        opened = true;
    }

    /**
     * Open the project.
     */
    public void open()
    {
        opened = true;
    }

    /**
     * Close the project.
     */
    public void close()
    {
        opened = false;
    }

    /**
     * Set the project name.
     * 
     * @param name The project name.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Set the sources folder.
     * 
     * @param folder The source folder.
     */
    public void setSources(File folder)
    {
        sources = folder;
    }

    /**
     * Set the resources folder.
     * 
     * @param folder The resource folder.
     */
    public void setResources(File folder)
    {
        resources = folder;
    }

    /**
     * Get the project name.
     * 
     * @return The project name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Get the sources folder.
     * 
     * @return The sources folder.
     */
    public File getSources()
    {
        return sources;
    }

    /**
     * Get the resources folder.
     * 
     * @return The resources folder.
     */
    public File getResources()
    {
        return resources;
    }

    /**
     * Check if the project is opened.
     * 
     * @return <code>true</code> if opened, <code>false</code> else.
     */
    public boolean isOpened()
    {
        return opened;
    }
}
