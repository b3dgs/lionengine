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

import org.eclipse.core.expressions.PropertyTester;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.editor.project.Project;
import com.b3dgs.lionengine.editor.project.ProjectModel;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionFormulaConfig;
import com.b3dgs.lionengine.io.Xml;

/**
 * Test if the folder contains formulas file.
 */
public final class FormulaTester extends PropertyTester
{
    /** Can add formulas property. */
    private static final String PROPERTY_ADD_FORMULAS = "addFormulas";
    /** Can edit formulas property. */
    private static final String PROPERTY_EDIT_FORMULAS = "editFormulas";

    /**
     * Check if the media is a formulas descriptor.
     * 
     * @param media The media to test.
     * @return <code>true</code> if valid, <code>false</code> else.
     */
    public static boolean isFormulasFile(Media media)
    {
        try
        {
            final Xml root = new Xml(media);
            return CollisionFormulaConfig.FORMULAS.equals(root.getNodeName());
        }
        catch (final LionEngineException exception)
        {
            Verbose.exception(exception);
            return false;
        }
    }

    /**
     * Check result depending of selection.
     * 
     * @param selection The selection reference.
     * @param property The property to check.
     * @return <code>true</code> if valid, <code>false</code> else.
     */
    private static boolean check(Media selection, String property)
    {
        final boolean result;
        if (PROPERTY_EDIT_FORMULAS.equals(property))
        {
            result = isFormulasFile(selection);
        }
        else if (PROPERTY_ADD_FORMULAS.equals(property))
        {
            result = selection.getFile().isDirectory();
        }
        else
        {
            result = false;
        }
        return result;
    }

    /**
     * Create tester.
     */
    public FormulaTester()
    {
        super();
    }

    /*
     * PropertyTester
     */

    @Override
    public boolean test(Object receiver, String property, Object[] args, Object expectedValue)
    {
        final Project project = ProjectModel.INSTANCE.getProject();
        if (project != null)
        {
            final Media selection = ProjectModel.INSTANCE.getSelection();
            if (selection != null)
            {
                return check(selection, property);
            }
        }
        return false;
    }
}
