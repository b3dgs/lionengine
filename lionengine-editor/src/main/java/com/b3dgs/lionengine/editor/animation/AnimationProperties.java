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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TreeItem;

import com.b3dgs.lionengine.anim.Anim;
import com.b3dgs.lionengine.anim.Animation;
import com.b3dgs.lionengine.editor.Tools;
import com.b3dgs.lionengine.editor.dialogs.AbstractDialog;

/**
 * Represents the animation properties edition view.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class AnimationProperties
{
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

    /**
     * Set the text value by checking if it is not disposed.
     * 
     * @param text The text reference.
     * @param value The text value.
     */
    private static void setTextValue(Text text, String value)
    {
        if (!text.isDisposed())
        {
            text.setText(value);
        }
    }

    /**
     * Set the button selection by checking if it is not disposed.
     * 
     * @param button The button reference.
     * @param selected The selected state.
     */
    private static void setButtonSelection(Button button, boolean selected)
    {
        if (!button.isDisposed())
        {
            button.setSelection(selected);
        }
    }

    /** Maximum frame */
    final int maxFrame;
    /** Animation list. */
    AnimationList animationList;
    /** Animation frame selector. */
    AnimationFrameSelector animationFrameSelector;
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

    /**
     * Constructor.
     * 
     * @param animationRenderer The animation renderer reference.
     */
    public AnimationProperties(AnimationRenderer animationRenderer)
    {
        maxFrame = animationRenderer.surface.getFramesNumber();
    }

    /**
     * Set the animation list.
     * 
     * @param animationList The animation list reference.
     */
    public void setAnimationList(AnimationList animationList)
    {
        this.animationList = animationList;
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
     * Set the selected animation, and update the properties fields.
     * 
     * @param animation The selected animation.
     */
    public void setSelectedAnimation(Animation animation)
    {
        AnimationProperties.setTextValue(firstFrame, String.valueOf(animation.getFirst()));
        AnimationProperties.setTextValue(lastFrame, String.valueOf(animation.getLast()));
        AnimationProperties.setTextValue(speed, String.valueOf(animation.getSpeed()));
        AnimationProperties.setButtonSelection(reverseAnim, animation.getReverse());
        AnimationProperties.setButtonSelection(repeatAnim, animation.getRepeat());

        animationFrameSelector.setSelectedFrames(animation.getFirst(), animation.getLast());
    }

    /**
     * Set the animation range.
     * 
     * @param first The first frame.
     * @param last The last frame.
     */
    public void setAnimationRange(int first, int last)
    {
        AnimationProperties.setTextValue(firstFrame, String.valueOf(first));
        AnimationProperties.setTextValue(lastFrame, String.valueOf(last));
    }

    /**
     * Create the animation data area.
     * 
     * @param parent The composite parent.
     */
    public void createAnimationProperties(Composite parent)
    {
        final Group animationProperties = new Group(parent, SWT.NONE);
        animationProperties.setLayout(new GridLayout(1, false));
        animationProperties.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true));
        animationProperties.setText("Properties");

        final Composite animationData = new Composite(animationProperties, SWT.NONE);
        animationData.setLayoutData(new GridData(SWT.CENTER, SWT.BOTTOM, false, true));
        animationData.setLayout(new GridLayout(1, false));

        createTextFields(animationData);

        final Composite animationButtons = new Composite(animationProperties, SWT.NONE);
        animationButtons.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        animationButtons.setLayout(new GridLayout(2, true));

        createResetButton(animationButtons);
        createConfirmButton(animationButtons);
    }

    /**
     * Create the text fields.
     * 
     * @param parent The composite parent.
     */
    private void createTextFields(Composite parent)
    {
        final Composite frames = new Composite(parent, SWT.NONE);
        frames.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
        frames.setLayout(new GridLayout(1, false));

        firstFrame = AnimationProperties.createTextField(frames, "First Frame:");
        lastFrame = AnimationProperties.createTextField(frames, "Last Frame:");
        speed = AnimationProperties.createTextField(frames, "Animation Speed:");

        final Composite flags = new Composite(parent, SWT.NONE);
        flags.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
        flags.setLayout(new GridLayout(2, true));

        reverseAnim = new Button(flags, SWT.CHECK | SWT.RIGHT_TO_LEFT);
        reverseAnim.setText("Reverse");
        repeatAnim = new Button(flags, SWT.CHECK | SWT.RIGHT_TO_LEFT);
        repeatAnim.setText("Repeat");
    }

    /**
     * Create the confirm button.
     * 
     * @param parent The composite parent.
     */
    private void createConfirmButton(Composite parent)
    {
        final Button confirm = Tools.createButton(parent, "Confirm", null);
        confirm.setImage(AbstractDialog.ICON_OK);
        confirm.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent)
            {
                if (animationList.getSelectedAnimation() != null)
                {
                    final TreeItem[] items = animationList.getTree().getSelection();
                    if (items.length > 0)
                    {
                        final TreeItem selection = items[0];
                        final int first = Math.max(Animation.MINIMUM_FRAME,
                                Math.min(Integer.parseInt(firstFrame.getText()), maxFrame));
                        final int last = Math.max(Animation.MINIMUM_FRAME,
                                Math.min(Integer.parseInt(lastFrame.getText()), maxFrame));
                        final Animation animation = Anim.createAnimation(Math.min(first, last), Math.max(first, last),
                                Double.parseDouble(speed.getText()), reverseAnim.getSelection(),
                                repeatAnim.getSelection());
                        animationList.updateAnimation(selection, animation);
                    }
                }
            }
        });
    }

    /**
     * Create the reset button.
     * 
     * @param parent The composite parent.
     */
    private void createResetButton(Composite parent)
    {
        final Button reset = Tools.createButton(parent, "Reset", null);
        reset.setImage(AbstractDialog.ICON_CANCEL);
        reset.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent)
            {
                if (animationList.getSelectedAnimation() != null)
                {
                    animationList.restoreSelectedAnimation();
                }
            }
        });
    }
}
