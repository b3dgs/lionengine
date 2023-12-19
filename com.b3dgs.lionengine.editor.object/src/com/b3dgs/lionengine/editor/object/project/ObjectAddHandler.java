/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.editor.object.project;

import java.io.IOException;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b3dgs.lionengine.UtilFile;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.editor.project.ProjectModel;
import com.b3dgs.lionengine.editor.validator.InputValidator;
import com.b3dgs.lionengine.game.SizeConfig;
import com.b3dgs.lionengine.game.feature.FeaturableConfig;
import com.b3dgs.lionengine.game.feature.FeaturableModel;
import com.b3dgs.lionengine.game.feature.Setup;

/**
 * Add an object descriptor in the selected folder.
 */
public final class ObjectAddHandler
{
    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ObjectAddHandler.class);

    /**
     * Create handler.
     */
    public ObjectAddHandler()
    {
        super();
    }

    /**
     * Execute the handler.
     * 
     * @param parent The shell parent.
     */
    @Execute
    public void execute(Shell parent)
    {
        InputValidator.getFile(parent, Messages.Title, Messages.Text, FeaturableConfig.DEFAULT_FILENAME, file ->
        {
            final Xml root = new Xml(UtilFile.removeExtension(FeaturableConfig.NODE_FEATURABLE));
            root.add(FeaturableConfig.exportClass(FeaturableModel.class.getName()));
            root.add(FeaturableConfig.exportSetup(Setup.class.getName()));
            root.add(SizeConfig.exports(new SizeConfig(16, 16)));
            try
            {
                file.createNewFile();
                root.save(ProjectModel.INSTANCE.getProject().getResourceMedia(file));
            }
            catch (final IOException exception)
            {
                LOGGER.error("execute error", exception);
            }
        });
    }
}
