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
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import com.b3dgs.lionengine.editor.Tools;
import com.b3dgs.lionengine.editor.dialogs.AbstractEditor;
import com.b3dgs.lionengine.game.configurable.Configurable;

/**
 * Animation editor dialog.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class AnimationEditor
        extends AbstractEditor
{
    /** Dialog title. */
    public static final String DIALOG_TITLE = "Animations Editor";
    /** Dialog icon. */
    public static final Image DIALOG_ICON = Tools.getIcon("animation-editor", "dialog.png");

    /** Configurable reference. */
    private final Configurable configurable;

    /**
     * Constructor.
     * 
     * @param parent The parent reference.
     * @param configurable The entity configurable reference.
     */
    public AnimationEditor(Composite parent, Configurable configurable)
    {
        super(AnimationEditor.DIALOG_TITLE, AnimationEditor.DIALOG_ICON, parent);
        this.configurable = configurable;
    }

    /**
     * Create the animation frame selector.
     * 
     * @param parent The composite parent.
     * @return The created frame selector.
     */
    private AnimationFrameSelector createAnimationFrameSelector(TabFolder parent)
    {
        final Composite sheet = new Composite(parent, SWT.NONE);
        sheet.setLayout(new GridLayout(1, false));
        sheet.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        final TabItem sheetTab = new TabItem(parent, SWT.NONE);
        sheetTab.setText("Sheet");
        sheetTab.setControl(sheet);

        final Composite renderer = new Composite(sheet, SWT.BORDER | SWT.DOUBLE_BUFFERED);
        renderer.setLayoutData(new GridData(256, 256));
        renderer.setSize(256, 256);
        renderer.layout(true, true);

        final AnimationFrameSelector animationFrameSelector = new AnimationFrameSelector(renderer, configurable);
        renderer.addPaintListener(animationFrameSelector);
        renderer.addMouseListener(animationFrameSelector);
        renderer.addMouseMoveListener(animationFrameSelector);

        return animationFrameSelector;
    }

    /**
     * Create the animator area, where the animation is played and controlled.
     * 
     * @param parent The composite parent.
     * @return The created composite.
     */
    private AnimationRenderer createAnimationRenderer(TabFolder parent)
    {
        final Composite content = new Composite(parent, SWT.NONE);
        content.setLayout(new GridLayout(1, false));
        content.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        final Composite renderer = new Composite(content, SWT.BORDER | SWT.DOUBLE_BUFFERED);
        renderer.setLayoutData(new GridData(256, 256));

        final AnimationRenderer animationRenderer = new AnimationRenderer(renderer, configurable);
        renderer.addPaintListener(animationRenderer);

        final TabItem animatorTab = new TabItem(parent, SWT.NONE);
        animatorTab.setText("Animator");
        animatorTab.setControl(content);

        return animationRenderer;
    }

    /*
     * AbstractEditor
     */

    @Override
    protected void createContent(Composite parent)
    {
        final Composite content = new Composite(parent, SWT.NONE);
        content.setLayout(new GridLayout(2, false));
        content.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        final Group animGroup = new Group(content, SWT.NONE);
        animGroup.setLayout(new GridLayout(1, false));
        animGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        animGroup.setText("Animation");

        final TabFolder animationTabs = new TabFolder(animGroup, SWT.TOP);
        final AnimationFrameSelector animationFrameSelector = createAnimationFrameSelector(animationTabs);
        final AnimationRenderer animationRenderer = createAnimationRenderer(animationTabs);

        final AnimationProperties animationProperties = new AnimationProperties(animationRenderer);
        final AnimationList animationList = new AnimationList(configurable, animationProperties);
        final AnimationPlayer animationPlayer = new AnimationPlayer(animationList, animationRenderer);
        animationPlayer.createAnimationPlayer(animationRenderer.getParent().getParent());

        animationFrameSelector.setAnimationList(animationList);
        animationFrameSelector.setAnimationProperties(animationProperties);
        animationProperties.setAnimationList(animationList);
        animationProperties.setAnimationFrameSelector(animationFrameSelector);
        animationRenderer.setAnimationPlayer(animationPlayer);

        final Composite properties = new Composite(content, SWT.NONE);
        properties.setLayout(new GridLayout(1, false));
        properties.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        animationList.createAnimationsList(properties);
        animationProperties.createAnimationProperties(properties);
        animationList.loadAnimations();
    }
}
