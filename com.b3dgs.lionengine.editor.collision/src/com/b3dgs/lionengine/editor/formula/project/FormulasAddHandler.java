/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.editor.formula.project;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.widgets.Shell;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.editor.collision.project.Messages;
import com.b3dgs.lionengine.editor.utility.UtilTemplate;
import com.b3dgs.lionengine.editor.validator.InputValidator;
import com.b3dgs.lionengine.game.collision.tile.CollisionFormulaConfig;

/**
 * Add a formulas descriptor in the selected folder.
 */
public final class FormulasAddHandler
{
    /** Template formulas. */
    private static final String TEMPLATE_FORMULAS = "formulas." + UtilTemplate.TEMPLATE_EXTENSION;

    /**
     * Create the formulas.
     * 
     * @param file The formulas file destination.
     * @throws IOException If error when creating the formulas.
     */
    private static void createFormulas(File file) throws IOException
    {
        final File template = UtilTemplate.getTemplate(TEMPLATE_FORMULAS);
        final Collection<String> lines = Files.readAllLines(template.toPath(), StandardCharsets.UTF_8);
        final Collection<String> dest = new ArrayList<>();
        for (final String line : lines)
        {
            dest.add(line);
        }
        Files.write(file.toPath(), dest, StandardCharsets.UTF_8);
        lines.clear();
        dest.clear();
    }

    /**
     * Create handler.
     */
    public FormulasAddHandler()
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
        InputValidator.getFile(parent, Messages.Title, Messages.Text, CollisionFormulaConfig.FILENAME, file ->
        {
            try
            {
                createFormulas(file);
            }
            catch (final IOException exception)
            {
                throw new LionEngineException(exception);
            }

        });
    }
}
