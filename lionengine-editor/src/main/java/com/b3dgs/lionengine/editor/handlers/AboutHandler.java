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
package com.b3dgs.lionengine.editor.handlers;

import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.b3dgs.lionengine.editor.Activator;

/**
 * About handler implementation.
 * 
 * @author Pierre-Alexandre
 */
public class AboutHandler
{
    /** About icon. */
    private static final Image ICON_ABOUT = Activator.getIcon("about.png");

    /**
     * Execute the handler.
     * 
     * @param shell The shell reference.
     */
    @Execute
    public void execute(Shell shell)
    {
        final Shell popup = new Shell(shell, SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM);
        popup.setLayout(new FillLayout());
        popup.setText("About");

        final Composite composite = new Composite(popup, SWT.NONE);
        composite.setLayout(new GridLayout(2, false));

        final Label aboutIcon = new Label(composite, SWT.NONE);
        aboutIcon.setImage(AboutHandler.ICON_ABOUT);

        final Label aboutText = new Label(composite, SWT.NONE);
        final String version = Platform.getBundle(Activator.PLUGIN_ID).getHeaders().get("Bundle-Version");
        aboutText.setText("LionEngine Editor " + version + "\nByron 3D Games Studio\nwww.b3dgs.com");

        popup.pack();
        Activator.center(popup);
        popup.open();
    }
}
