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

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.b3dgs.lionengine.editor.Tools;
import com.b3dgs.lionengine.editor.dialogs.AbstractDialog;

/**
 * Animation editor dialog.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class AnimationEditor
{
    /** Dialog icon. */
    private static final Image ICON_DIALOG = Tools.getIcon("animation-editor", "dialog.png");
    /** Play icon. */
    private static final Image ICON_ANIM_PLAY = Tools.getIcon("animation-editor", "anim-play.png");
    /** Pause icon. */
    private static final Image ICON_ANIM_PAUSE = Tools.getIcon("animation-editor", "anim-pause.png");
    /** Stop icon. */
    private static final Image ICON_ANIM_STOP = Tools.getIcon("animation-editor", "anim-stop.png");
    /** Previous animation icon. */
    private static final Image ICON_ANIM_PREVIOUS = Tools.getIcon("animation-editor", "anim-prev.png");
    /** Next animation icon. */
    private static final Image ICON_ANIM_NEXT = Tools.getIcon("animation-editor", "anim-next.png");
    /** Stop icon. */
    private static final Image ICON_ANIM_ADD = Tools.getIcon("animation-editor", "anim-add.png");
    /** Stop icon. */
    private static final Image ICON_ANIM_REMOVE = Tools.getIcon("animation-editor", "anim-remove.png");

    /**
     * Create a text and its label.
     * 
     * @param parent The parent reference.
     * @param title The text title.
     * @return The text instance.
     */
    private static Text createTextField(Composite parent, String title)
    {
        final Composite field = new Composite(parent, SWT.NONE);
        field.setLayout(new GridLayout(2, false));

        final Label label = new Label(field, SWT.NONE);
        label.setText(title);
        final Text text = new Text(field, SWT.SINGLE);

        return text;
    }

    /** Shell reference. */
    final Shell shell;
    /** Animation list. */
    Tree animationTree;
    /** First frame. */
    Text firstFrame;
    /** Last frame. */
    Text lastFrame;
    /** Animation speed. */
    Text speed;
    /** Animation reverse. */
    Button reverseAnim;
    /** Animation repeat. */
    Button repeatAnim;
    /** Animation renderer. */
    private AnimationRenderer animationRenderer;

    /**
     * Constructor.
     * 
     * @param parent The parent reference.
     */
    public AnimationEditor(Composite parent)
    {
        shell = new Shell(parent.getDisplay(), SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM);
        shell.setLayout(new GridLayout(1, false));
        shell.setImage(AnimationEditor.ICON_DIALOG);
        shell.setText("Animations Editor");

        final Composite content = new Composite(shell, SWT.NONE);
        content.setLayout(new GridLayout(3, false));
        content.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        createAnimator(content);
        createAnimationsList(content);
        createAnimationProperties(content);

        createBottom(shell);
    }

    /**
     * Create the animator area, where the animation is played and controlled.
     * 
     * @param parent The composite parent.
     */
    private void createAnimator(Composite parent)
    {
        final Group animatorArea = new Group(parent, SWT.NONE);
        animatorArea.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, true));
        animatorArea.setLayout(new GridLayout(1, false));
        animatorArea.setText("Animation");

        createAnimationRenderer(animatorArea);
        createAnimationPlayer(animatorArea);
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
        animationRenderer = new AnimationRenderer(renderer);
        parent.addPaintListener(animationRenderer);
        parent.addMouseListener(animationRenderer);
        parent.addMouseMoveListener(animationRenderer);
        parent.addKeyListener(animationRenderer);
    }

    /**
     * Create a button with a name and an icon.
     * 
     * @param parent The button parent.
     * @param name The button name.
     * @param icon The button icon.
     * @return The button instance.
     */
    private static Button createButton(Composite parent, String name, Image icon)
    {
        final Button button = new Button(parent, SWT.PUSH);
        button.setImage(icon);
        return button;
    }

    /**
     * Create the player area of the animation.
     * 
     * @param parent The composite parent.
     */
    private void createAnimationPlayer(Composite parent)
    {
        final Composite animatorPlayer = new Composite(parent, SWT.NONE);
        animatorPlayer.setLayout(new GridLayout(5, true));
        animatorPlayer.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));

        final Button previousAnim = AnimationEditor.createButton(animatorPlayer, "Previous",
                AnimationEditor.ICON_ANIM_PREVIOUS);
        previousAnim.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent)
            {
                // TODO previous animation
            }
        });
        final Button playAnim = AnimationEditor.createButton(animatorPlayer, "Play", AnimationEditor.ICON_ANIM_PLAY);
        playAnim.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent)
            {
                // TODO play animation
            }
        });
        final Button pauseAnim = AnimationEditor.createButton(animatorPlayer, "Pause", AnimationEditor.ICON_ANIM_PAUSE);
        pauseAnim.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent)
            {
                // TODO pause animation
            }
        });
        final Button stopAnim = AnimationEditor.createButton(animatorPlayer, "Stop", AnimationEditor.ICON_ANIM_STOP);
        stopAnim.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent)
            {
                // TODO stop animation
            }
        });
        final Button nextAnim = AnimationEditor.createButton(animatorPlayer, "Next", AnimationEditor.ICON_ANIM_NEXT);
        nextAnim.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent)
            {
                // TODO next animation
            }
        });
    }

    /**
     * Create the animations list area.
     * 
     * @param parent The composite parent.
     */
    private void createAnimationsList(Composite parent)
    {
        final Group animations = new Group(parent, SWT.NONE);
        animations.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        animations.setLayout(new GridLayout(1, false));
        animations.setText("List");

        final ToolBar toolbar = new ToolBar(animations, SWT.HORIZONTAL);
        toolbar.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, true, false));
        final ToolItem addAnimation = new ToolItem(toolbar, SWT.PUSH);
        addAnimation.setImage(AnimationEditor.ICON_ANIM_ADD);
        addAnimation.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent)
            {
                final InputDialog inputDialog = new InputDialog(shell, "Animation name", "Enter the animation name",
                        "newAnimation", null);
                if (inputDialog.open() == Window.OK)
                {
                    final String name = inputDialog.getValue();
                    final TreeItem item = new TreeItem(animationTree, SWT.NONE);
                    item.setText(name);
                }
            }
        });
        final ToolItem removeAnimation = new ToolItem(toolbar, SWT.PUSH);
        removeAnimation.setImage(AnimationEditor.ICON_ANIM_REMOVE);
        removeAnimation.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent)
            {
                for (final TreeItem item : animationTree.getSelection())
                {
                    item.dispose();
                }
                animationTree.layout(true, true);
            }
        });

        animationTree = new Tree(animations, SWT.SINGLE);
        animationTree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        animationTree.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent)
            {
                // TODO update current animation data
            }
        });
    }

    /**
     * Create the animation data area.
     * 
     * @param parent The composite parent.
     */
    private void createAnimationProperties(Composite parent)
    {
        final Group animationProperties = new Group(parent, SWT.NONE);
        animationProperties.setLayout(new GridLayout(1, false));
        animationProperties.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, false, true));
        animationProperties.setText("Properties");

        final Composite animationData = new Composite(animationProperties, SWT.NONE);
        animationData.setLayoutData(new GridData(SWT.CENTER, SWT.BOTTOM, false, true));
        animationData.setLayout(new GridLayout(1, false));

        firstFrame = AnimationEditor.createTextField(animationData, "First Frame:");
        lastFrame = AnimationEditor.createTextField(animationData, "Last Frame:");
        speed = AnimationEditor.createTextField(animationData, "Animation Speed:");
        reverseAnim = new Button(animationData, SWT.CHECK | SWT.RIGHT_TO_LEFT);
        reverseAnim.setText("Reverse");
        repeatAnim = new Button(animationData, SWT.CHECK | SWT.RIGHT_TO_LEFT);
        repeatAnim.setText("Repeat");
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

        final Button accept = new Button(bottom, SWT.PUSH);
        final GridData acceptData = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
        acceptData.widthHint = AbstractDialog.BOTTOM_BUTTON_WIDTH;
        accept.setLayoutData(acceptData);
        accept.setText("Accept");
        accept.addSelectionListener(new SelectionAdapter()
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
