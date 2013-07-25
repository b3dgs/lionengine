package com.b3dgs.lionengine.tools;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import com.b3dgs.lionengine.utility.UtilityFile;

/**
 * Level rip filter.
 */
class LevelRipFilter
        extends FileFilter
{
    /** File extension. */
    private final String extension;
    /** File description. */
    private final String description;

    /**
     * Constructor.
     * 
     * @param extension Level file extension.
     * @param description Level file description.
     */
    LevelRipFilter(String extension, String description)
    {
        this.extension = extension;
        this.description = description;
    }

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
