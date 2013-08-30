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
    /** Map file extension. */
    private final String extension;
    /** Map file description. */
    private final String description;

    /**
     * Constructor.
     * 
     * @param extension The map file extension.
     * @param description The map file description.
     */
    public MapFilter(String extension, String description)
    {
        this.extension = extension;
        this.description = description;
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
            if (extension.equals(ext))
            {
                return true;
            }
            return false;
        }

        return false;
    }

    @Override
    public String getDescription()
    {
        StringBuilder buf = new StringBuilder(description);
        buf = buf.append(" (*.").append(extension).append(")");
        return buf.toString();
    }
}
