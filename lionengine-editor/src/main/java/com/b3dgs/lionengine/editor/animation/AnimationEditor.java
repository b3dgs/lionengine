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

import java.util.HashMap;
import java.util.Map;

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

import com.b3dgs.lionengine.anim.Anim;
import com.b3dgs.lionengine.anim.Animation;
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
    /** Animations data. */
    Map<TreeItem, Animation> animationsData;
    /** Selected data. */
    Animation selectedAnimation;
    /** Selected data backup. */
    Animation selectedAnimationBackup;
    /** Configurable reference. */
    private final Configurable configurable;
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

        animationsData = new HashMap<>();

        final Composite content = new Composite(shell, SWT.NONE);
        content.setLayout(new GridLayout(3, false));
        content.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        createAnimator(content);
        createAnimationsList(content);
        createAnimationProperties(content);

        createBottom(shell);
    }

    /**
     * Set the selected animation, and update the properties fields.
     * 
     * @param animation The selected animation.
     */
    void setSelectedAnimation(Animation animation)
    {
        selectedAnimation = animation;
        selectedAnimationBackup = Anim.createAnimation(animation.getFirst(), animation.getLast(), animation.getSpeed(),
                animation.getReverse(), animation.getRepeat());
        firstFrame.setText(String.valueOf(animation.getFirst()));
        lastFrame.setText(String.valueOf(animation.getLast()));
        speed.setText(String.valueOf(animation.getSpeed()));
        reverseAnim.setSelection(Boolean.valueOf(animation.getReverse()).booleanValue());
        repeatAnim.setSelection(Boolean.valueOf(animation.getRepeat()).booleanValue());
    }

    /**
     * Set the next selected item from the current one.
     * 
     * @param side -1 for the previous, +1 for the next one.
     */
    void setNextSelection(int side)
    {
        TreeItem[] items = animationTree.getSelection();
        if (items.length == 0)
        {
            animationTree.forceFocus();
            animationTree.selectAll();
            animationTree.update();
        }
        items = animationTree.getSelection();
        if (items.length > 0)
        {
            final int index = getItemIndex(items[0]) + side;
            final int next = Math.max(0, Math.min(index, animationTree.getItemCount() - 1));
            final TreeItem previous = animationTree.getItem(next);
            animationTree.setSelection(previous);
            animationTree.forceFocus();
        }
    }

    /**
     * Get the selected item number from the tree.
     * 
     * @param item The item to search.
     * @return The item index.
     */
    int getItemIndex(TreeItem item)
    {
        int i = 0;
        for (final TreeItem current : animationTree.getItems())
        {
            if (current.equals(item))
            {
                break;
            }
            i++;
        }
        return i;
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

        animationRenderer = new AnimationRenderer(renderer, configurable);

        renderer.addPaintListener(animationRenderer);
        renderer.addMouseListener(animationRenderer);
        renderer.addMouseMoveListener(animationRenderer);
        renderer.addKeyListener(animationRenderer);
    }

    /**
     * Create the previous animation button.
     * 
     * @param parent The composite parent.
     */
    private void createButtonPreviousAnim(Composite parent)
    {
        final Button previousAnim = Tools.createButton(parent, null, AnimationEditor.ICON_ANIM_PREVIOUS, false);
        previousAnim.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent)
            {
                setNextSelection(-1);
            }
        });
    }

    /**
     * Create the play animation button.
     * 
     * @param parent The composite parent.
     */
    private void createButtonPlayAnim(Composite parent)
    {
        final Button playAnim = Tools.createButton(parent, null, AnimationEditor.ICON_ANIM_PLAY, false);
        playAnim.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent)
            {
                animationRenderer.setAnimation(selectedAnimation);
                animationRenderer.setPause(false);
            }
        });
    }

    /**
     * Create the pause animation button.
     * 
     * @param parent The composite parent.
     */
    private void createButtonPauseAnim(Composite parent)
    {
        final Button pauseAnim = Tools.createButton(parent, null, AnimationEditor.ICON_ANIM_PAUSE, false);
        pauseAnim.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent)
            {
                animationRenderer.setPause(true);
            }
        });
    }

    /**
     * Create the stop animation button.
     * 
     * @param parent The composite parent.
     */
    private void createButtonStopAnim(Composite parent)
    {
        final Button stopAnim = Tools.createButton(parent, null, AnimationEditor.ICON_ANIM_STOP, false);
        stopAnim.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent)
            {
                animationRenderer.stopAnimation();
            }
        });
    }

    /**
     * Create the next animation button.
     * 
     * @param parent The composite parent.
     */
    private void createButtonNextAnim(Composite parent)
    {
        final Button nextAnim = Tools.createButton(parent, null, AnimationEditor.ICON_ANIM_NEXT, false);
        nextAnim.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent)
            {
                setNextSelection(1);
            }
        });
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

        createButtonPreviousAnim(animatorPlayer);
        createButtonPlayAnim(animatorPlayer);
        createButtonPauseAnim(animatorPlayer);
        createButtonStopAnim(animatorPlayer);
        createButtonNextAnim(animatorPlayer);
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
                final TreeItem[] items = animationTree.getSelection();
                if (items.length > 0)
                {
                    final TreeItem selection = items[0];
                    final Object data = selection.getData();
                    if (data instanceof Animation)
                    {
                        setSelectedAnimation((Animation) data);
                    }
                    else
                    {
                        final Animation animation = Anim.createAnimation(Animation.MINIMUM_FRAME,
                                Animation.MINIMUM_FRAME + 1, 0.05, false, true);
                        animationsData.put(selection, animation);
                        setSelectedAnimation(animation);
                    }
                }
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

        final Composite animationButtons = new Composite(animationProperties, SWT.NONE);
        animationButtons.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        animationButtons.setLayout(new GridLayout(2, true));

        final Button cancel = Tools.createButton(animationButtons, "Reset", null, true);
        cancel.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent)
            {
                if (selectedAnimation != null)
                {
                    setSelectedAnimation(selectedAnimationBackup);
                }
            }
        });

        final Button accept = Tools.createButton(animationButtons, "Confirm", null, true);
        accept.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent)
            {
                if (selectedAnimation != null)
                {
                    // TODO modify animation
                }
            }
        });
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
