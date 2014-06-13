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
package com.b3dgs.lionengine.editor.animation;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import com.b3dgs.lionengine.editor.Tools;
import com.b3dgs.lionengine.editor.dialogs.AbstractDialog;
import com.b3dgs.lionengine.game.configurable.Configurable;

/**
 * Animation editor dialog.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class AnimationEditor
{
    /** Dialog icon. */
    public static final Image ICON_DIALOG = Tools.getIcon("animation-editor", "dialog.png");

    /** Shell reference. */
    final Shell shell;
    /** Animation list. */
    final AnimationList animationList;
    /** Configurable reference. */
    private final Configurable configurable;
    /** Animation player. */
    private final AnimationPlayer animationPlayer;
    /** Animation properties. */
    private final AnimationProperties animationProperties;
    /** Animation renderer. */
    AnimationRenderer animationRenderer;

    /**
     * Constructor.
     * 
     * @param parent The parent reference.
     * @param configurable The entity configurable reference.
     */
    public AnimationEditor(Composite parent, Configurable configurable)
    {
        this.configurable = configurable;

        shell = new Shell(parent.getDisplay(), SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM);
        shell.setLayout(new GridLayout(1, false));
        shell.setImage(AnimationEditor.ICON_DIALOG);
        shell.setText("Animations Editor");

        final Composite content = new Composite(shell, SWT.NONE);
        content.setLayout(new GridLayout(2, false));
        content.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        final TabFolder animationTabs = new TabFolder(content, SWT.TOP);
        final TabItem sheetTab = new TabItem(animationTabs, SWT.NONE);
        sheetTab.setText("Animation sheet");

        final Composite sheet = new Composite(animationTabs, SWT.NONE);
        sheet.setLayout(new GridLayout(1, false));
        sheet.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        sheetTab.setControl(sheet);

        final Composite renderer = new Composite(sheet, SWT.BORDER | SWT.DOUBLE_BUFFERED);
        renderer.setLayoutData(new GridData(256, 256));
        renderer.setSize(256, 256);
        renderer.layout(true, true);

        final AnimationFrameSelector animationFrameSelector = new AnimationFrameSelector(renderer, configurable);
        renderer.addPaintListener(animationFrameSelector);
        renderer.addMouseListener(animationFrameSelector);
        renderer.addMouseMoveListener(animationFrameSelector);

        final Composite animatorArea = createAnimator(animationTabs);

        final TabItem animatorTab = new TabItem(animationTabs, SWT.NONE);
        animatorTab.setText("Animator");
        animatorTab.setControl(animatorArea);

        animationProperties = new AnimationProperties(animationRenderer);
        animationList = new AnimationList(configurable, animationProperties);
        animationPlayer = new AnimationPlayer(animationList, animationRenderer);

        animationFrameSelector.setAnimationList(animationList);
        animationFrameSelector.setAnimationProperties(animationProperties);
        animationProperties.setAnimationList(animationList);
        animationProperties.setAnimationFrameSelector(animationFrameSelector);

        animationPlayer.createAnimationPlayer(animatorArea);

        final Composite properties = new Composite(content, SWT.NONE);
        properties.setLayout(new GridLayout(1, false));
        properties.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        animationList.createAnimationsList(properties);
        animationProperties.createAnimationProperties(properties);

        createBottom(shell);
    }

    /**
     * Open the dialog.
     */
    public void open()
    {
        shell.pack(true);
        shell.layout(true, true);
        Tools.center(shell);
        shell.setVisible(true);
    }

    /**
     * Create the animator area, where the animation is played and controlled.
     * 
     * @param parent The composite parent.
     * @return The created composite.
     */
    private Composite createAnimator(Composite parent)
    {
        final Composite content = new Composite(parent, SWT.NONE);
        content.setLayout(new GridLayout(1, false));
        content.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        final Composite renderer = new Composite(content, SWT.BORDER | SWT.DOUBLE_BUFFERED);
        renderer.setLayoutData(new GridData(256, 256));

        animationRenderer = new AnimationRenderer(renderer, configurable);
        renderer.addPaintListener(animationRenderer);

        return content;
    }

    /**
     * Create the bottom part.
     * 
     * @param parent The parent reference.
     */
    private void createBottom(Composite parent)
    {
        final Composite bottom = new Composite(parent, SWT.NONE);
        bottom.setLayout(new GridLayout(1, false));
        bottom.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));

        final Button exit = Tools.createButton(bottom, "Exit", null);
        exit.setImage(AbstractDialog.ICON_EXIT);
        exit.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent)
            {
                shell.dispose();
            }
        });
    }
}
