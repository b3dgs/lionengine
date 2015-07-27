/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.editor;

import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MTrimmedWindow;
import org.eclipse.e4.ui.workbench.lifecycle.ProcessAdditions;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Monitor;

import com.b3dgs.lionengine.editor.utility.UtilPart;

/**
 * Manage the product life cycle.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
@SuppressWarnings("restriction")
public class Manager
{
    /**
     * Create manager.
     */
    public Manager()
    {
        // Nothing to do
    }

    /**
     * Center the main trimmed window.
     * 
     * @param app The application.
     * @param modelService The model service.
     * @param display The display reference.
     */
    @ProcessAdditions
    @SuppressWarnings("static-method")
    public void processAdditions(MApplication app, EModelService modelService, Display display)
    {
        UtilPart.setApplication(app);

        final Monitor monitor = display.getPrimaryMonitor();
        final Rectangle monitorRect = monitor.getBounds();
        final MTrimmedWindow window = (MTrimmedWindow) modelService.find(Activator.PLUGIN_ID + ".window", app);
        final int windowx = window.getWidth();
        final int windowy = window.getHeight();
        final int centerX = monitorRect.x + (monitorRect.width - windowx) / 2;
        final int centerY = monitorRect.y + (monitorRect.height - windowy) / 2;
        window.setX(centerX);
        window.setY(centerY);
    }
}
