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
package com.b3dgs.lionengine.editor.properties.animation.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.TreeItem;

import com.b3dgs.lionengine.anim.Animation;
import com.b3dgs.lionengine.editor.UtilEclipse;
import com.b3dgs.lionengine.editor.dialog.AbstractEditor;
import com.b3dgs.lionengine.game.configurer.ConfigAnimations;
import com.b3dgs.lionengine.game.configurer.Configurer;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Animation editor dialog.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class AnimationEditor
        extends AbstractEditor
{
    /** Dialog icon. */
    public static final Image ICON = UtilEclipse.getIcon("animation-editor", "dialog.png");

    /** Configurer reference. */
    private final Configurer configurer;
    /** Animations list. */
    private AnimationList animationList;

    /**
     * Create an animation editor and associate its configurer.
     * 
     * @param parent The parent reference.
     * @param configurer The entity configurer reference.
     */
    public AnimationEditor(Composite parent, Configurer configurer)
    {
        super(parent, Messages.AnimationEditor_Title, ICON);
        this.configurer = configurer;
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
        sheetTab.setText(Messages.AnimationEditor_Sheet);
        sheetTab.setControl(sheet);

        final Composite renderer = new Composite(sheet, SWT.BORDER | SWT.DOUBLE_BUFFERED);
        final AnimationFrameSelector animationFrameSelector = new AnimationFrameSelector(renderer, configurer);
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
        final AnimationRenderer animationRenderer = new AnimationRenderer(renderer, configurer);
        renderer.addPaintListener(animationRenderer);

        final TabItem animatorTab = new TabItem(parent, SWT.NONE);
        animatorTab.setText(Messages.AnimationEditor_Animator);
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
        animGroup.setText(Messages.AnimationEditor_Animation);

        final TabFolder animationTabs = new TabFolder(animGroup, SWT.TOP);
        final AnimationFrameSelector animationFrameSelector = createAnimationFrameSelector(animationTabs);
        final AnimationRenderer animationRenderer = createAnimationRenderer(animationTabs);

        final AnimationProperties animationProperties = new AnimationProperties(animationRenderer);
        animationList = new AnimationList(configurer, animationProperties);
        animationList.addListener(animationProperties);
        final AnimationPlayer animationPlayer = new AnimationPlayer(animationList, animationRenderer);
        animationPlayer.createAnimationPlayer(animationRenderer.getParent().getParent());

        animationFrameSelector.setAnimationList(animationList);
        animationFrameSelector.setAnimationProperties(animationProperties);
        animationProperties.setAnimationFrameSelector(animationFrameSelector);
        animationRenderer.setAnimationPlayer(animationPlayer);

        final Composite properties = new Composite(content, SWT.NONE);
        properties.setLayout(new GridLayout(1, false));
        properties.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        animationList.create(properties);
        animationProperties.create(properties);
        animationList.loadAnimations();
    }

    @Override
    protected void onExit()
    {
        animationList.save();

        final XmlNode root = configurer.getRoot();
        root.removeChildren(ConfigAnimations.ANIMATION);

        for (final TreeItem item : animationList.getTree().getItems())
        {
            final Animation animation = (Animation) item.getData();
            ConfigAnimations.export(root, animation);
        }
        configurer.save();
    }
}
