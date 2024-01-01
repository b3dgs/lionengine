/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 *
 * This program is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
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
package com.b3dgs.lionengine.android;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.UtilFile;
import com.b3dgs.lionengine.UtilFolder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Media implementation.
 */
final class MediaAndroid implements Media
{
    /** No parent. */
    private static final String NO_PARENT = Constant.EMPTY_STRING;
    /** Error get stream. */
    private static final String ERROR_GET_STREAM = "Error on getting stream of: ";
    /** Error get stream. */
    private static final String ERROR_CREATE_FOLDER = "Unable to create folder: {}";
    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(MediaAndroid.class);

    /** Asset manager. */
    private final AssetManager assetManager;
    /** Context reference. */
    private final Context context;
    /** Separator. */
    private final String separator;
    /** Resources directory. */
    private final String resourcesDir;
    /** Media path. */
    private final String path;
    /** Media parent path. */
    private final String parent;
    /** Media name. */
    private final String name;

    /**
     * Internal constructor.
     * 
     * @param assetManager The asset manager.
     * @param context The context reference.
     * @param separator The path separator.
     * @param resourcesDir The resources directory.
     * @param path The media path.
     * @throws LionEngineException If path in <code>null</code>.
     */
    MediaAndroid(AssetManager assetManager, Context context, String separator, String resourcesDir, String path)
    {
        Check.notNull(assetManager);
        Check.notNull(context);
        Check.notNull(separator);
        Check.notNull(resourcesDir);
        Check.notNull(path);

        this.assetManager = assetManager;
        this.context = context;
        this.separator = separator;
        this.resourcesDir = resourcesDir;
        this.path = path;
        final int index = path.lastIndexOf(separator);
        if (index > -1)
        {
            parent = path.substring(0, index);
        }
        else
        {
            parent = NO_PARENT;
        }
        name = path.substring(path.lastIndexOf(separator) + 1);
    }

    /**
     * Get file descriptor.
     * 
     * @return The file descriptor.
     */
    AssetFileDescriptor getDescriptor()
    {
        try
        {
            return assetManager.openFd(getPathAbsolute());
        }
        catch (final IOException exception)
        {
            try
            {
                return assetManager.openFd(getPathTemp());
            }
            catch (@SuppressWarnings("unused") final IOException exception2)
            {
                throw new LionEngineException(exception, this);
            }
        }
    }

    /**
     * Get the absolute media path.
     * 
     * @return The absolute media path.
     */
    private String getPathAbsolute()
    {
        return UtilFolder.getPathSeparator(separator, resourcesDir, path);
    }

    /**
     * Get the temporary path equivalent.
     * 
     * @return The temporary path.
     */
    private String getPathTemp()
    {
        return UtilFolder.getPathSeparator(File.separator,
                                           context.getCacheDir().getAbsolutePath(),
                                           path.replace(separator, File.separator));
    }

    /*
     * Media
     */

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public String getPath()
    {
        return path;
    }

    @Override
    public String getParentPath()
    {
        return parent;
    }

    @Override
    public File getFile()
    {
        if (UtilFile.exists(getPathAbsolute()))
        {
            return new File(getPathAbsolute());
        }
        return new File(getPathTemp());
    }

    @Override
    public URL getUrl()
    {
        try
        {
            return getFile().toURI().toURL();
        }
        catch (MalformedURLException exception)
        {
           LOGGER.error("getUrl error", exception);
           return null;
        }
    }

    @Override
    public Collection<Media> getMedias()
    {
        try
        {
            final String fullPath = getPathAbsolute();
            final String prefix = fullPath.substring(resourcesDir.length());
            final String[] files = assetManager.list(fullPath);
            final Collection<Media> medias = new ArrayList<>(files.length);
            for (final String file : files)
            {
                medias.add(Medias.create(prefix, file));
            }
            return medias;
        }
        catch (final IOException exception)
        {
            throw new LionEngineException(exception, this);
        }
    }

    @Override
    public InputStream getInputStream()
    {
        Check.notNull(path);

        try
        {
            return assetManager.open(getPathAbsolute());
        }
        catch (final IOException exception)
        {
            try
            {
                return new FileInputStream(new File(getPathTemp()));
            }
            catch (@SuppressWarnings("unused") final IOException exception2)
            {
                throw new LionEngineException(exception,
                                              this,
                                              ERROR_GET_STREAM + Constant.QUOTE + path + Constant.QUOTE);
            }
        }
    }

    @Override
    public OutputStream getOutputStream()
    {
        Check.notNull(path);

        try
        {
            final File file = new File(getPathTemp());
            final File parent = file.getParentFile();
            if (!parent.exists() && !parent.mkdirs())
            {
                LOGGER.warn(ERROR_CREATE_FOLDER, parent.getAbsolutePath());
            }
            return new FileOutputStream(file);
        }
        catch (final IOException exception)
        {
            throw new LionEngineException(exception, this, ERROR_GET_STREAM + Constant.QUOTE + path + Constant.QUOTE);
        }
    }

    @Override
    public boolean exists()
    {
        try
        {
            assetManager.open(getPathAbsolute()).close();
        }
        catch (@SuppressWarnings("unused") final IOException exception)
        {
            return new File(getPathTemp()).exists();
        }
        return true;
    }

    @Override
    public boolean isJar()
    {
        return true;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + path.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object object)
    {
        if (this == object)
        {
            return true;
        }
        if (object == null || object.getClass() != getClass())
        {
            return false;
        }
        final Media media = (Media) object;
        return media.getPath().equals(path);
    }

    @Override
    public String toString()
    {
        return path;
    }
}
