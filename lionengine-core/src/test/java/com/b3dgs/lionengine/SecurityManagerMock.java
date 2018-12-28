/*
 * Copyright (C) 2013-2018 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine;

import java.io.FileDescriptor;
import java.net.InetAddress;
import java.security.Permission;

/**
 * Security manager mock with no access.
 */
final class SecurityManagerMock extends SecurityManager
{
    /** Allow state. */
    private final boolean allow;

    /**
     * Constructor.
     * 
     * @param allow The allow state.
     */
    SecurityManagerMock(boolean allow)
    {
        this.allow = allow;
    }

    /*
     * SecurityManager
     */

    @Override
    public void checkPropertyAccess(String key)
    {
        if (!allow
            && !"line.separator".equals(key)
            && !"sun.util.logging.disableCallerCheck".equals(key)
            && !"jdk.logging.allowStackWalkSearch".equals(key))
        {
            throw new SecurityException();
        }
    }

    @Override
    public void checkPermission(Permission perm)
    {
        if ("control".equals(perm.getName()))
        {
            throw new SecurityException();
        }
    }

    @Override
    public void checkPermission(Permission perm, Object context)
    {
        // Nothing to do
    }

    @Override
    public void checkCreateClassLoader()
    {
        // Nothing to do
    }

    @Override
    public void checkAccess(Thread t)
    {
        // Nothing to do
    }

    @Override
    public void checkAccess(ThreadGroup g)
    {
        // Nothing to do
    }

    @Override
    public void checkExit(int status)
    {
        // Nothing to do
    }

    @Override
    public void checkExec(String cmd)
    {
        // Nothing to do
    }

    @Override
    public void checkLink(String lib)
    {
        // Nothing to do
    }

    @Override
    public void checkRead(FileDescriptor fd)
    {
        // Nothing to do
    }

    @Override
    public void checkRead(String file)
    {
        // Nothing to do
    }

    @Override
    public void checkRead(String file, Object context)
    {
        // Nothing to do
    }

    @Override
    public void checkWrite(FileDescriptor fd)
    {
        // Nothing to do
    }

    @Override
    public void checkWrite(String file)
    {
        // Nothing to do
    }

    @Override
    public void checkDelete(String file)
    {
        // Nothing to do
    }

    @Override
    public void checkConnect(String host, int port)
    {
        // Nothing to do
    }

    @Override
    public void checkConnect(String host, int port, Object context)
    {
        // Nothing to do
    }

    @Override
    public void checkListen(int port)
    {
        // Nothing to do
    }

    @Override
    public void checkAccept(String host, int port)
    {
        // Nothing to do
    }

    @Override
    public void checkMulticast(InetAddress maddr)
    {
        // Nothing to do
    }

    @Override
    public void checkPropertiesAccess()
    {
        // Nothing to do
    }

    @Override
    public void checkPrintJobAccess()
    {
        // Nothing to do
    }

    @Override
    public void checkPackageAccess(String pkg)
    {
        // Nothing to do
    }

    @Override
    public void checkPackageDefinition(String pkg)
    {
        // Nothing to do
    }

    @Override
    public void checkSetFactory()
    {
        // Nothing to do
    }

    @Override
    public void checkSecurityAccess(String target)
    {
        // Nothing to do
    }
}
