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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import com.b3dgs.lionengine.anim.Anim;
import com.b3dgs.lionengine.anim.Animation;
import com.b3dgs.lionengine.editor.ObjectListListener;
import com.b3dgs.lionengine.editor.ObjectProperties;

/**
 * Represents the animation properties edition view.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class AnimationProperties extends ObjectProperties<Animation> implements ObjectListListener<Animation>
{
    /** Maximum frame. */
    private final int maxFrame;
    /** Animation frame selector. */
    private AnimationFrameSelector animationFrameSelector;
    /** First frame. */
    private Text firstFrame;
    /** Last frame. */
    private Text lastFrame;
    /** Animation speed. */
    private Text speed;
    /** Animation reverse. */
    private Button reverseAnim;
    /** Animation repeat. */
    private Button repeatAnim;

    /**
     * Create an animation properties and associate its renderer to retrieve the maximum frames number.
     * 
     * @param animationRenderer The animation renderer reference.
     */
    public AnimationProperties(AnimationRenderer animationRenderer)
    {
        super();
        maxFrame = animationRenderer.surface.getFramesHorizontal() * animationRenderer.surface.getFramesVertical();
    }

    /**
     * Set the animation frame selector.
     * 
     * @param animationFrameSelector The animation frame selector reference.
     */
    public void setAnimationFrameSelector(AnimationFrameSelector animationFrameSelector)
    {
        this.animationFrameSelector = animationFrameSelector;
    }

    /**
     * Set the animation range.
     * 
     * @param first The first frame.
     * @param last The last frame.
     */
    public void setAnimationRange(int first, int last)
    {
        setValue(firstFrame, String.valueOf(first));
        setValue(lastFrame, String.valueOf(last));
    }

    /*
     * ObjectProperties
     */

    @Override
    protected void createTextFields(Composite parent)
    {
        final Composite frames = new Composite(parent, SWT.NONE);
        frames.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        frames.setLayout(new GridLayout(1, false));

        firstFrame = createTextField(frames, Messages.AnimationProperties_FirstFrame);
        lastFrame = createTextField(frames, Messages.AnimationProperties_LastFrame);
        speed = createTextField(frames, Messages.AnimationProperties_AnimSpeed);

        final Composite flags = new Composite(parent, SWT.NONE);
        flags.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
        flags.setLayout(new GridLayout(2, true));

        reverseAnim = new Button(flags, SWT.CHECK | SWT.RIGHT_TO_LEFT);
        reverseAnim.setText(Messages.AnimationProperties_Reverse);
        repeatAnim = new Button(flags, SWT.CHECK | SWT.RIGHT_TO_LEFT);
        repeatAnim.setText(Messages.AnimationProperties_Repeat);
    }

    @Override
    protected Animation createObject(String name)
    {
        final int first = Math.max(Animation.MINIMUM_FRAME, Math.min(Integer.parseInt(firstFrame.getText()), maxFrame));
        final int last = Math.max(Animation.MINIMUM_FRAME, Math.min(Integer.parseInt(lastFrame.getText()), maxFrame));
        final Animation animation = Anim.createAnimation(name,
                                                         Math.min(first, last),
                                                         Math.max(first, last),
                                                         Double.parseDouble(speed.getText()),
                                                         reverseAnim.getSelection(),
                                                         repeatAnim.getSelection());
        return animation;
    }

    /*
     * ObjectListListener
     */

    @Override
    public void notifyObjectSelected(Animation animation)
    {
        setValueDefault(firstFrame, String.valueOf(animation.getFirst()));
        setValueDefault(lastFrame, String.valueOf(animation.getLast()));
        setValueDefault(speed, String.valueOf(animation.getSpeed()));
        setButtonSelection(reverseAnim, animation.getReverse());
        setButtonSelection(repeatAnim, animation.getRepeat());

        animationFrameSelector.setSelectedFrames(animation.getFirst(), animation.getLast());
    }

    @Override
    public void notifyObjectDeleted(Animation animation)
    {
        // Nothing to do
    }
}
