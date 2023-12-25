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
package com.b3dgs.lionengine.editor.formula;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.editor.formula.editor.FormulasEditHandler;
import com.b3dgs.lionengine.editor.formula.project.FormulaTester;
import com.b3dgs.lionengine.editor.project.ProjectTreeCreator;
import com.b3dgs.lionengine.editor.project.ResourceChecker;
import com.b3dgs.lionengine.editor.utility.UtilIcon;

/**
 * Formulas resource checker.
 */
public class FormulaResource implements ResourceChecker
{
    /** Formulas file icon. */
    private static final Image ICON = UtilIcon.get(ProjectTreeCreator.RESOURCES_FOLDER, "formulas.png");

    /**
     * Create the checker.
     */
    public FormulaResource()
    {
        super();
    }

    @Override
    public boolean check(Shell shell, Media media)
    {
        if (FormulaTester.isFormulasFile(media))
        {
            FormulasEditHandler.executeHandler(shell);
            return true;
        }
        return false;
    }

    @Override
    public Image getIcon(Media media)
    {
        if (FormulaTester.isFormulasFile(media))
        {
            return ICON;
        }
        return null;
    }
}
