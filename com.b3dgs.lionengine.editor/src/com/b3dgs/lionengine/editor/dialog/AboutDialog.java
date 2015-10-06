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
package com.b3dgs.lionengine.editor.dialog;

import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.editor.Activator;
import com.b3dgs.lionengine.editor.utility.UtilButton;
import com.b3dgs.lionengine.editor.utility.UtilIcon;
import com.b3dgs.lionengine.editor.utility.UtilSwt;

/**
 * Represents the about dialog of the application.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class AboutDialog
{
    /** About icon. */
    private static final Image ICON_ABOUT = UtilIcon.get("about.png");

    /**
     * Create the dialog content.
     * 
     * @param dialog The dialog parent.
     */
    private static void create(Shell dialog)
    {
        final Composite top = new Composite(dialog, SWT.NONE);
        top.setLayout(new GridLayout(2, false));
        top.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        top.setBackground(top.getDisplay().getSystemColor(SWT.COLOR_WHITE));
        createTop(top);

        final Label separatorHeader = new Label(dialog, SWT.SEPARATOR | SWT.HORIZONTAL);
        separatorHeader.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        final Composite bottom = new Composite(dialog, SWT.NONE);
        bottom.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));

        final GridLayout bottomLayout = new GridLayout(1, false);
        bottomLayout.marginHeight = 4;
        bottomLayout.marginWidth = 4;
        bottom.setLayout(bottomLayout);
        createBottom(dialog, bottom);
    }

    /**
     * Create the top part.
     * 
     * @param top The top composite.
     */
    private static void createTop(Composite top)
    {
        final Label aboutIcon = new Label(top, SWT.NONE);
        aboutIcon.setImage(AboutDialog.ICON_ABOUT);

        final Label aboutText = new Label(top, SWT.NONE);
        aboutText.setBackground(aboutText.getDisplay().getSystemColor(SWT.COLOR_WHITE));

        final String vendor = Platform.getBundle(Activator.PLUGIN_ID).getHeaders().get("Bundle-Vendor");
        final StringBuilder text = new StringBuilder(Activator.PLUGIN_NAME).append(Constant.SPACE);
        text.append(Activator.PLUGIN_VERSION + Constant.NEW_LINE);
        text.append(vendor).append(Constant.NEW_LINE);
        text.append(Activator.PLUGIN_WEBSITE);

        aboutText.setText(text.toString());
    }

    /**
     * Create the bottom part.
     * 
     * @param dialog The dialog parent.
     * @param bottom The bottom composite.
     */
    private static void createBottom(final Shell dialog, Composite bottom)
    {
        final Button okButton = UtilButton.create(bottom, "OK", null);
        final GridData data = new GridData();
        data.widthHint = 72;
        okButton.setLayoutData(data);
        okButton.setImage(AbstractDialog.ICON_EXIT);
        UtilButton.setAction(okButton, () -> dialog.dispose());
    }

    /** The dialog. */
    private final Shell dialog;

    /**
     * Create the about dialog.
     * 
     * @param shell The shell parent.
     */
    public AboutDialog(Shell shell)
    {
        dialog = new Shell(shell, SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM);
        dialog.setLayout(UtilSwt.borderless());
        dialog.setText(Messages.AboutDialog_Title);

        create(dialog);
    }

    /**
     * Open the dialog.
     */
    public void open()
    {
        UtilSwt.open(dialog);
    }
}
