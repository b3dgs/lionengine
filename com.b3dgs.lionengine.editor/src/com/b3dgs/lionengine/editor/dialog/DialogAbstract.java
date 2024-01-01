/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.editor.dialog;

import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
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

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.editor.utility.UtilIcon;
import com.b3dgs.lionengine.editor.utility.control.UtilButton;
import com.b3dgs.lionengine.editor.utility.control.UtilSwt;
import com.b3dgs.lionengine.editor.world.WorldModel;
import com.b3dgs.lionengine.editor.world.view.WorldPart;

/**
 * Represents the abstract dialog.
 */
// CHECKSTYLE IGNORE LINE: DataAbstractionCoupling
public abstract class DialogAbstract extends Dialog implements MDirtyable
{
    /** Dialog folder. */
    public static final String DIALOG_FOLDER = "dialog";
    /** OK icon. */
    public static final Image ICON_OK = UtilIcon.get(DIALOG_FOLDER, "ok.png");
    /** Cancel icon. */
    public static final Image ICON_CANCEL = UtilIcon.get(DIALOG_FOLDER, "cancel.png");
    /** Exit icon. */
    public static final Image ICON_EXIT = UtilIcon.get(DIALOG_FOLDER, "exit.png");
    /** Info icon. */
    protected static final Image ICON_INFO = UtilIcon.get(DIALOG_FOLDER, "info.png");
    /** Warning icon. */
    protected static final Image ICON_WARNING = UtilIcon.get(DIALOG_FOLDER, "warning.png");
    /** Error icon. */
    protected static final Image ICON_ERROR = UtilIcon.get(DIALOG_FOLDER, "error.png");
    /** Maximum characters input. */
    protected static final int MAX_CHAR = 64;
    /** Dialog width. */
    private static final int DIALOG_WIDTH = 512;
    /** Dialog height. */
    private static final int DIALOG_HEIGHT = 640;
    /** Dialog title height. */
    private static final int DIALOG_TITLE_HEIGHT = 10;

    /** Dialog shell. */
    protected final Shell dialog;
    /** Dialog title. */
    private final String dialogTitle;
    /** Header title. */
    private final String headerTitle;
    /** Header description. */
    private final String headerDesc;
    /** Header icon. */
    private final Image headerIcon;
    /** Finish button. */
    protected Button finish;
    /** Cancel button. */
    protected Button cancel;
    /** Tips label. */
    protected CLabel tipsLabel;
    /** Canceled flag. */
    protected boolean canceled;
    /** Dirty flag. */
    private boolean dirty;
    /** Last dirty flag. */
    private boolean dirtyOld;

    /**
     * Dialog constructor base.
     * 
     * @param parent The parent reference.
     * @param title The dialog title.
     * @param headerTitle The header title.
     * @param headerDesc The header description.
     * @param headerIcon The header icon.
     */
    protected DialogAbstract(Shell parent, String title, String headerTitle, String headerDesc, Image headerIcon)
    {
        this(parent, title, headerTitle, headerDesc, headerIcon, SWT.DIALOG_TRIM);
    }

    /**
     * Dialog constructor base.
     * 
     * @param parent The parent reference.
     * @param title The dialog title.
     * @param headerTitle The header title.
     * @param headerDesc The header description.
     * @param headerIcon The header icon.
     * @param type The dialogue type.
     */
    protected DialogAbstract(Shell parent,
                             String title,
                             String headerTitle,
                             String headerDesc,
                             Image headerIcon,
                             int type)
    {
        super(parent);
        dialogTitle = title;
        this.headerTitle = headerTitle;
        this.headerDesc = headerDesc;
        this.headerIcon = headerIcon;
        dialog = new Shell(parent, type | SWT.APPLICATION_MODAL);
        dialog.setMinimumSize(DIALOG_WIDTH, DIALOG_HEIGHT);
        dialog.setLayout(UtilSwt.borderless());
        dialog.setText(title);
        dialog.setImage(headerIcon);
        dialog.addShellListener(new ShellAdapter()
        {
            @Override
            public void shellClosed(ShellEvent event)
            {
                canceled = true;
            }
        });
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
     * Called when canceled. Does nothing by default.
     */
    protected void onCanceled()
    {
        // Nothing by default
    }

    /**
     * Open the dialog.
     */
    public void open()
    {
        dialog.setData(this);
        UtilSwt.open(dialog);

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
     * Set the finish button enabled state.
     * 
     * @param enabled <code>true</code> to enabled it, <code>false</code> else.
     */
    public void setFinishEnabled(boolean enabled)
    {
        finish.setEnabled(enabled);
    }

    /**
     * Check if dialog has been canceled.
     * 
     * @return <code>true</code> if canceled, <code>false</code> else.
     */
    public boolean isCanceled()
    {
        return canceled;
    }

    /**
     * Create the header part of the dialog.
     * 
     * @param header The header composite.
     */
    protected final void createHeader(Composite header)
    {
        final Composite titleArea = new Composite(header, SWT.NONE);
        titleArea.setLayout(new GridLayout(1, false));
        titleArea.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        titleArea.setBackground(titleArea.getDisplay().getSystemColor(SWT.COLOR_WHITE));

        final Label title = new Label(titleArea, SWT.NONE);
        final FontData data = title.getFont().getFontData()[0];
        data.setHeight(DIALOG_TITLE_HEIGHT);
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
    protected final void setTipsMessage(Image icon, String message)
    {
        tipsLabel.setText(message);
        tipsLabel.setImage(icon);
        tipsLabel.pack(true);
        tipsLabel.setVisible(true);
    }

    /**
     * Create the dialog.
     */
    protected final void createDialog()
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

        createBottom(dialog);
    }

    /**
     * Create the bottom part of the dialog.
     * 
     * @param dialog The dialog composite.
     */
    protected final void createBottom(final Composite dialog)
    {
        final Composite bottom = new Composite(dialog, SWT.NONE);
        bottom.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        bottom.setLayout(new GridLayout(2, false));

        tipsLabel = new CLabel(bottom, SWT.LEFT_TO_RIGHT);
        tipsLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
        tipsLabel.setVisible(false);

        final Composite buttonArea = new Composite(bottom, SWT.NONE);
        buttonArea.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        buttonArea.setLayout(new GridLayout(2, true));

        finish = UtilButton.create(buttonArea, Messages.Finish, DialogAbstract.ICON_OK);
        finish.setEnabled(false);
        UtilButton.setAction(finish, this::close);

        cancel = UtilButton.create(buttonArea, Messages.Cancel, DialogAbstract.ICON_CANCEL);
        UtilButton.setAction(cancel, () ->
        {
            canceled = true;
            onCanceled();
            dialog.dispose();

            final WorldPart part = WorldModel.INSTANCE.getServices().get(WorldPart.class);
            part.update();
        });
    }

    /**
     * Close dialog.
     */
    protected final void close()
    {
        onFinish();
        dialog.dispose();

        final WorldPart part = WorldModel.INSTANCE.getServices().get(WorldPart.class);
        part.update();
    }

    @Override
    public void setDirty(boolean value)
    {
        dirty = value;
        if (dirtyOld != dirty)
        {
            dirtyOld = dirty;
            if (dirty)
            {
                dialog.setText(Constant.STAR + dialogTitle);
                dialog.update();
            }
            else
            {
                dialog.update();
                dialog.setText(dialogTitle);
            }
        }
    }

    @Override
    public boolean isDirty()
    {
        return dirty;
    }
}
