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
package com.b3dgs.lionengine.editor.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.b3dgs.lionengine.editor.Tools;

/**
 * Represents the abstract dialog.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public abstract class AbstractDialog
        extends Dialog
{
    /** Bottom button width. */
    public static final int BOTTOM_BUTTON_WIDTH = 96;
    /** Info icon. */
    protected static final Image ICON_INFO = Tools.getIcon("dialog", "info.png");
    /** Warning icon. */
    protected static final Image ICON_WARNING = Tools.getIcon("dialog", "warning.png");
    /** Error icon. */
    protected static final Image ICON_ERROR = Tools.getIcon("dialog", "error.png");
    /** Maximum characters input. */
    protected static final int MAX_CHAR = 64;

    /** Dialog shell. */
    protected final Shell dialog;
    /** Finish button. */
    protected Button finish;
    /** Tips label. */
    protected CLabel tipsLabel;
    /** Header title. */
    private final String headerTitle;
    /** Header description. */
    private final String headerDesc;
    /** Header icon. */
    private final Image headerIcon;

    /**
     * Constructor.
     * 
     * @param parent The parent reference.
     * @param title The dialog title.
     * @param headerTitle The header title.
     * @param headerDesc The header description.
     * @param headerIcon The header icon.
     */
    public AbstractDialog(Shell parent, String title, String headerTitle, String headerDesc, Image headerIcon)
    {
        super(parent);
        this.headerTitle = headerTitle;
        this.headerDesc = headerDesc;
        this.headerIcon = headerIcon;
        dialog = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
        dialog.setMinimumSize(500, 300);
        final GridLayout dialogLayout = new GridLayout(1, false);
        dialogLayout.marginHeight = 0;
        dialogLayout.marginWidth = 0;
        dialogLayout.verticalSpacing = 0;
        dialog.setLayout(dialogLayout);
        dialog.setText(title);
        dialog.setImage(Tools.getIcon("product.png")); //$NON-NLS-1$
    }

    /**
     * Create the content part of the dialog.
     * 
     * @param content The content composite.
     */
    protected abstract void createContent(Composite content);

    /**
     * Called when click on finish button.
     */
    protected abstract void onFinish();

    /**
     * Open the dialog.
     */
    public void open()
    {
        dialog.pack(true);
        Tools.center(dialog);
        dialog.open();

        final Display display = dialog.getDisplay();
        while (!dialog.isDisposed())
        {
            if (!display.readAndDispatch())
            {
                display.sleep();
            }
        }
    }

    /**
     * Create the header part of the dialog.
     * 
     * @param header The header composite.
     */
    protected void createHeader(Composite header)
    {
        final Composite titleArea = new Composite(header, SWT.NONE);
        titleArea.setLayout(new GridLayout(1, false));
        titleArea.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        titleArea.setBackground(titleArea.getDisplay().getSystemColor(SWT.COLOR_WHITE));

        final Label title = new Label(titleArea, SWT.NONE);
        final FontData data = title.getFont().getFontData()[0];
        data.setHeight(10);
        data.setStyle(SWT.BOLD);
        title.setFont(new Font(title.getDisplay(), data));
        title.setBackground(title.getDisplay().getSystemColor(SWT.COLOR_WHITE));
        title.setText(headerTitle);

        final Label text = new Label(titleArea, SWT.NONE);
        text.setBackground(text.getDisplay().getSystemColor(SWT.COLOR_WHITE));
        text.setText(headerDesc);

        final Label iconLabel = new Label(header, SWT.NONE);
        iconLabel.setImage(headerIcon);
        iconLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, true, false));
        iconLabel.setBackground(iconLabel.getDisplay().getSystemColor(SWT.COLOR_WHITE));
    }

    /**
     * Set the error message.
     * 
     * @param icon The message icon.
     * @param message The error message.
     */
    protected void setTipsMessage(Image icon, String message)
    {
        tipsLabel.setText(message);
        tipsLabel.setImage(icon);
        tipsLabel.pack(true);
        tipsLabel.setVisible(true);
    }

    /**
     * Create the dialog.
     */
    protected void createDialog()
    {
        final Composite header = new Composite(dialog, SWT.NONE);
        header.setLayout(new GridLayout(2, false));
        header.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        createHeader(header);
        header.setBackground(header.getDisplay().getSystemColor(SWT.COLOR_WHITE));

        final Label separatorHeader = new Label(dialog, SWT.SEPARATOR | SWT.HORIZONTAL);
        separatorHeader.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        final Composite content = new Composite(dialog, SWT.NONE);
        content.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true));
        content.setLayout(new GridLayout(1, false));
        createContent(content);

        final Label separatorContent = new Label(dialog, SWT.SEPARATOR | SWT.HORIZONTAL);
        separatorContent.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        final Composite bottom = new Composite(dialog, SWT.NONE);
        bottom.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        bottom.setLayout(new GridLayout(2, false));
        createBottom(bottom);
    }

    /**
     * Create the bottom part of the dialog.
     * 
     * @param bottom The bottom composite.
     */
    protected void createBottom(Composite bottom)
    {
        tipsLabel = new CLabel(bottom, SWT.LEFT_TO_RIGHT);
        tipsLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        tipsLabel.setVisible(false);

        final Composite buttonArea = new Composite(bottom, SWT.NONE);
        buttonArea.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
        buttonArea.setLayout(new GridLayout(2, false));

        finish = new Button(buttonArea, SWT.PUSH);
        final GridData finishData = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
        finishData.widthHint = AbstractDialog.BOTTOM_BUTTON_WIDTH;
        finish.setLayoutData(finishData);
        finish.setText(Messages.AbstractDialog_Finish);
        finish.setEnabled(false);
        finish.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent)
            {
                onFinish();
                dialog.dispose();
            }
        });

        final Button cancel = new Button(buttonArea, SWT.PUSH);
        final GridData cancelData = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
        cancelData.widthHint = AbstractDialog.BOTTOM_BUTTON_WIDTH;
        cancel.setLayoutData(cancelData);
        cancel.setText(Messages.AbstractDialog_Cancel);
        cancel.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent)
            {
                dialog.dispose();
            }
        });
    }
}
