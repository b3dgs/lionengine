package com.b3dgs.lionengine.example.c_platform.e_lionheart.editor;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import com.b3dgs.lionengine.utility.UtilityFile;

/**
 * Filter the map file format.
 */
public class MapFilter
        extends FileFilter
{
    /** Map file description. */
    private final String description;
    /** Map file extension. */
    private final String[] extensions;

    /**
     * Constructor.
     * 
     * @param description The map file description.
     * @param extensions The map file extensions.
     */
    public MapFilter(String description, String... extensions)
    {
        this.description = description;
        this.extensions = extensions;
    }

    /*
     * FileFilter
     */

    @Override
    public boolean accept(File f)
    {
        if (f.isDirectory())
        {
            return true;
        }

        final String ext = UtilityFile.getExtension(f);
        if (ext != null)
        {
            for (final String extension : extensions)
            {
                if (extension.equals(ext))
                {
                    return true;
                }
            }
            return false;
        }

        return false;
    }

    @Override
    public String getDescription()
    {
        StringBuilder buf = new StringBuilder(description);
        for (final String extension : extensions)
        {
            buf = buf.append(" (*.").append(extension).append(")");
        }
        return buf.toString();
    }
}
