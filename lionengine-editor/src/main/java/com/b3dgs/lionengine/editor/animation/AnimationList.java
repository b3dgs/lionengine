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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.b3dgs.lionengine.anim.Anim;
import com.b3dgs.lionengine.anim.Animation;
import com.b3dgs.lionengine.editor.Tools;

/**
 * Represents the animation list, allowing to add and remove animations.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class AnimationList
{
    /** Stop icon. */
    private static final Image ICON_ANIM_ADD = Tools.getIcon("animation-editor", "anim-add.png");
    /** Stop icon. */
    private static final Image ICON_ANIM_REMOVE = Tools.getIcon("animation-editor", "anim-remove.png");

    /** Animation properties. */
    private final AnimationProperties animationProperties;
    /** Animation list. */
    Tree animationTree;
    /** Selected data. */
    Animation selectedAnimation;
    /** Selected data backup. */
    Animation selectedAnimationBackup;

    /**
     * Constructor.
     * 
     * @param animationProperties The animation properties reference.
     */
    public AnimationList(AnimationProperties animationProperties)
    {
        this.animationProperties = animationProperties;
    }

    /**
     * Update the selected animation with its new values.
     * 
     * @param selection The selected item.
     * @param animation The new animation.
     */
    public void updateAnimation(TreeItem selection, Animation animation)
    {
        selection.setData(animation);
        setSelectedAnimation(animation);
    }

    /**
     * Restore the selected animation with the previous one.
     */
    public void restoreSelectedAnimation()
    {
        setSelectedAnimation(selectedAnimationBackup);
    }

    /**
     * Set the next selected item from the current one.
     * 
     * @param side -1 for the previous, +1 for the next one.
     */
    public void setNextSelection(int side)
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
     * Get the selected animation.
     * 
     * @return The selected animation.
     */
    public Animation getSelectedAnimation()
    {
        return selectedAnimation;
    }

    /**
     * Get the animation tree reference.
     * 
     * @return The animation tree reference.
     */
    public Tree getTree()
    {
        return animationTree;
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
        animationProperties.setSelectedAnimation(animation);
    }

    /**
     * Create the animations list area.
     * 
     * @param parent The composite parent.
     */
    public void createAnimationsList(final Composite parent)
    {
        final Group animations = new Group(parent, SWT.NONE);
        animations.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        animations.setLayout(new GridLayout(1, false));
        animations.setText("List");

        createToolBar(animations);
        createTree(animations);
    }

    /**
     * Create the tool bar.
     * 
     * @param parent The composite parent.
     */
    private void createToolBar(final Composite parent)
    {
        final ToolBar toolbar = new ToolBar(parent, SWT.HORIZONTAL);
        toolbar.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, true, false));

        createAddAnimationToolItem(toolbar);
        createRemoveAnimationToolItem(toolbar);
    }

    /**
     * Create the add animation tool item.
     * 
     * @param toolbar The tool bar reference.
     */
    private void createAddAnimationToolItem(final ToolBar toolbar)
    {
        final ToolItem addAnimation = new ToolItem(toolbar, SWT.PUSH);
        addAnimation.setImage(AnimationList.ICON_ANIM_ADD);
        addAnimation.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent)
            {
                final InputDialog inputDialog = new InputDialog(toolbar.getShell(), "Animation name",
                        "Enter the animation name", "newAnimation", null);
                if (inputDialog.open() == Window.OK)
                {
                    final String name = inputDialog.getValue();
                    final TreeItem item = new TreeItem(animationTree, SWT.NONE);
                    item.setText(name);
                }
            }
        });
    }

    /**
     * Create the remove animation tool item.
     * 
     * @param toolbar The tool bar reference.
     */
    private void createRemoveAnimationToolItem(ToolBar toolbar)
    {
        final ToolItem removeAnimation = new ToolItem(toolbar, SWT.PUSH);
        removeAnimation.setImage(AnimationList.ICON_ANIM_REMOVE);
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
    }

    /**
     * Create the animation tree.
     * 
     * @param parent The composite parent.
     */
    private void createTree(final Composite parent)
    {
        animationTree = new Tree(parent, SWT.SINGLE);
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
                        selection.setData(animation);
                        setSelectedAnimation(animation);
                    }
                }
            }
        });
    }

    /**
     * Get the selected item number from the tree.
     * 
     * @param item The item to search.
     * @return The item index.
     */
    private int getItemIndex(TreeItem item)
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
}
