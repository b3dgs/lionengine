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
package com.b3dgs.lionengine.game;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import com.b3dgs.lionengine.game.ActionRef;

/**
 * Test the action ref class.
 */
public class ActionRefTest
{
    private final boolean cancel = true;
    private final ActionRef actionRef1 = new ActionRef("path", !cancel, new ArrayList<ActionRef>());
    private final ActionRef actionRef2 = new ActionRef("path2", cancel, Arrays.asList(actionRef1));

    /**
     * Test the animation getter.
     */
    @Test
    public void testGetter()
    {
        Assert.assertEquals("path", actionRef1.getPath());
        Assert.assertEquals("path2", actionRef2.getPath());

        Assert.assertFalse(actionRef1.getCancel());
        Assert.assertTrue(actionRef2.getCancel());

        Assert.assertTrue(actionRef1.getRefs().isEmpty());
        Assert.assertEquals(actionRef1, actionRef2.getRefs().iterator().next());
    }

    /**
     * Test the animation hash code.
     */
    @Test
    public void testHashcode()
    {
        final int hash = actionRef1.hashCode();

        Assert.assertEquals(hash, new ActionRef("path", !cancel, new ArrayList<ActionRef>()).hashCode());

        Assert.assertEquals(actionRef2.hashCode(), actionRef2.hashCode());
        Assert.assertEquals(actionRef2.hashCode(),
                            new ActionRef("path2", cancel, Arrays.asList(actionRef1)).hashCode());

        Assert.assertNotEquals(hash, new Object().hashCode());
        Assert.assertNotEquals(hash, actionRef2.hashCode());
        Assert.assertNotEquals(actionRef2.hashCode(), hash);

        Assert.assertNotEquals(hash, new ActionRef("", !cancel, new ArrayList<ActionRef>()).hashCode());
        Assert.assertNotEquals(hash, new ActionRef("path", cancel, new ArrayList<ActionRef>()).hashCode());
        Assert.assertNotEquals(hash, new ActionRef("path", cancel, Arrays.asList(actionRef1)).hashCode());
        Assert.assertNotEquals(hash, new ActionRef("path", !cancel, Arrays.asList(actionRef1)).hashCode());
    }

    /**
     * Test the animation equality.
     */
    @Test
    public void testEquals()
    {
        Assert.assertEquals(actionRef1, actionRef1);
        Assert.assertEquals(actionRef1, new ActionRef("path", !cancel, new ArrayList<ActionRef>()));

        Assert.assertEquals(actionRef2, actionRef2);
        Assert.assertEquals(actionRef2, new ActionRef("path2", cancel, Arrays.asList(actionRef1)));

        Assert.assertNotEquals(actionRef1, null);
        Assert.assertNotEquals(actionRef1, new Object());
        Assert.assertNotEquals(actionRef1, actionRef2);
        Assert.assertNotEquals(actionRef2, actionRef1);

        Assert.assertNotEquals(actionRef1, new ActionRef("", !cancel, new ArrayList<ActionRef>()));
        Assert.assertNotEquals(actionRef1, new ActionRef("path", cancel, new ArrayList<ActionRef>()));
        Assert.assertNotEquals(actionRef1, new ActionRef("path", cancel, Arrays.asList(actionRef1)));
        Assert.assertNotEquals(actionRef1, new ActionRef("path", !cancel, Arrays.asList(actionRef1)));
    }

    /**
     * Test the to string.
     */
    @Test
    public void testToString()
    {
        Assert.assertEquals("ActionRef [path=path2, cancel=true, refs=[ActionRef [path=path, cancel=false, refs=[]]]]",
                            actionRef2.toString());
    }
}
