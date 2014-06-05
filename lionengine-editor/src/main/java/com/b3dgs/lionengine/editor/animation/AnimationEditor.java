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
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

import com.b3dgs.lionengine.editor.Tools;
import com.b3dgs.lionengine.game.configurable.Configurable;

/**
 * Animation editor dialog.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class AnimationEditor
{
    /** Dialog icon. */
    private static final Image ICON_DIALOG = Tools.getIcon("animation-editor", "dialog.png");

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
        content.setLayout(new GridLayout(3, false));
        content.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        final Composite animatorArea = createAnimator(content);

        animationProperties = new AnimationProperties(animationRenderer);
        animationList = new AnimationList(animationProperties);
        animationPlayer = new AnimationPlayer(animationList, animationRenderer);

        animationProperties.setAnimationList(animationList);

        animationPlayer.createAnimationPlayer(animatorArea);
        animationList.createAnimationsList(content);
        animationProperties.createAnimationProperties(content);

        createBottom(shell);
    }

    /**
     * Create the animator area, where the animation is played and controlled.
     * 
     * @param parent The composite parent.
     * @return The created composite.
     */
    private Composite createAnimator(Composite parent)
    {
        final Group animatorArea = new Group(parent, SWT.NONE);
        animatorArea.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, true));
        animatorArea.setLayout(new GridLayout(1, false));
        animatorArea.setText("Animation");

        createAnimationRenderer(animatorArea);

        return animatorArea;
    }

    /**
     * Create the animation renderer, where the animation is displayed.
     * 
     * @param parent The composite parent.
     */
    private void createAnimationRenderer(Composite parent)
    {
        final Composite renderer = new Composite(parent, SWT.BORDER);
        renderer.setLayoutData(new GridData(256, 256));

        animationRenderer = new AnimationRenderer(renderer, configurable);

        renderer.addPaintListener(animationRenderer);
        renderer.addMouseListener(animationRenderer);
        renderer.addMouseMoveListener(animationRenderer);
        renderer.addKeyListener(animationRenderer);
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

        final Button exit = Tools.createButton(bottom, "Exit", null, true);
        exit.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent)
            {
                shell.dispose();
            }
        });
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
}
