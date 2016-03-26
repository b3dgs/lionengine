/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.editor.animation.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import com.b3dgs.lionengine.anim.Animation;
import com.b3dgs.lionengine.editor.utility.UtilIcon;
import com.b3dgs.lionengine.editor.utility.control.UtilButton;

/**
 * Represents the animation player view, where the animation can be controlled.
 */
public class AnimationPlayer
{
    /** Animation editor folder. */
    private static final String FOLDER = "animation-editor";
    /** Play icon. */
    private static final Image ICON_ANIM_PLAY = UtilIcon.get(FOLDER, "anim-play.png");
    /** Pause icon. */
    private static final Image ICON_ANIM_PAUSE = UtilIcon.get(FOLDER, "anim-pause.png");
    /** Stop icon. */
    private static final Image ICON_ANIM_STOP = UtilIcon.get(FOLDER, "anim-stop.png");
    /** Previous animation icon. */
    private static final Image ICON_ANIM_PREVIOUS = UtilIcon.get(FOLDER, "anim-prev.png");
    /** Next animation icon. */
    private static final Image ICON_ANIM_NEXT = UtilIcon.get(FOLDER, "anim-next.png");

    /** Animation list. */
    private final AnimationList animationList;
    /** Animation renderer. */
    private final AnimationRenderer animationRenderer;
    /** Previous anim. */
    private Button previousAnim;
    /** Play anim. */
    private Button playAnim;
    /** Pause anim. */
    private Button pauseAnim;
    /** Stop anim. */
    private Button stopAnim;
    /** Next anim. */
    private Button nextAnim;

    /**
     * Create an animation player and associate its animation list and renderer.
     * 
     * @param animationList The animation list reference.
     * @param animationRenderer The animation renderer reference.
     */
    public AnimationPlayer(AnimationList animationList, AnimationRenderer animationRenderer)
    {
        this.animationList = animationList;
        this.animationRenderer = animationRenderer;
    }

    /**
     * Create the player area of the animation.
     * 
     * @param parent The composite parent.
     */
    public void createAnimationPlayer(Composite parent)
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
     * Called when animation is finished.
     */
    public void notifyAnimationFinished()
    {
        previousAnim.setEnabled(true);
        playAnim.setEnabled(true);
        pauseAnim.setEnabled(false);
        stopAnim.setEnabled(false);
        nextAnim.setEnabled(true);
    }

    /**
     * Create the previous animation button.
     * 
     * @param parent The composite parent.
     */
    private void createButtonPreviousAnim(Composite parent)
    {
        previousAnim = UtilButton.create(parent, null, AnimationPlayer.ICON_ANIM_PREVIOUS);
        UtilButton.setAction(previousAnim, () -> animationList.setNextSelection(-1));
    }

    /**
     * Create the play animation button.
     * 
     * @param parent The composite parent.
     */
    private void createButtonPlayAnim(Composite parent)
    {
        playAnim = UtilButton.create(parent, null, AnimationPlayer.ICON_ANIM_PLAY);
        UtilButton.setAction(playAnim, () ->
        {
            final Animation animation = animationList.getSelectedObject();
            if (animation != null)
            {
                animationRenderer.setAnimation(animation);
                animationRenderer.setPause(false);
                previousAnim.setEnabled(false);
                playAnim.setEnabled(false);
                pauseAnim.setEnabled(true);
                stopAnim.setEnabled(true);
                nextAnim.setEnabled(false);
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
        pauseAnim = UtilButton.create(parent, null, AnimationPlayer.ICON_ANIM_PAUSE);
        pauseAnim.setEnabled(false);
        UtilButton.setAction(pauseAnim, () ->
        {
            animationRenderer.setPause(true);
            previousAnim.setEnabled(false);
            playAnim.setEnabled(true);
            pauseAnim.setEnabled(false);
            nextAnim.setEnabled(false);
        });
    }

    /**
     * Create the stop animation button.
     * 
     * @param parent The composite parent.
     */
    private void createButtonStopAnim(Composite parent)
    {
        stopAnim = UtilButton.create(parent, null, AnimationPlayer.ICON_ANIM_STOP);
        stopAnim.setEnabled(false);
        UtilButton.setAction(stopAnim, () ->
        {
            animationRenderer.stopAnimation();
            previousAnim.setEnabled(true);
            playAnim.setEnabled(true);
            pauseAnim.setEnabled(false);
            stopAnim.setEnabled(false);
            nextAnim.setEnabled(true);
        });
    }

    /**
     * Create the next animation button.
     * 
     * @param parent The composite parent.
     */
    private void createButtonNextAnim(Composite parent)
    {
        nextAnim = UtilButton.create(parent, null, AnimationPlayer.ICON_ANIM_NEXT);
        UtilButton.setAction(nextAnim, () -> animationList.setNextSelection(1));
    }
}
