/*
 * Copyright 2016 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.rnkrsoft.io.buffer.util.internal;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketPermission;
import java.net.UnknownHostException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Enumeration;

/**
 * Provides socket operations with privileges enabled. This is necessary for applications that use the
 * {@link SecurityManager} to restrict {@link SocketPermission} to their application. By asserting that these
 * operations are privileged, the operations can proceed even if some code in the calling chain lacks the appropriate
 * {@link SocketPermission}.
 */
public final class SocketUtils {

    private SocketUtils() {
    }

    public static void connect(final Socket socket, final SocketAddress remoteAddress, final int timeout)
            throws IOException {
        try {
            AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws IOException {
                    socket.connect(remoteAddress, timeout);
                    return null;
                }
            });
        } catch (PrivilegedActionException e) {
            throw (IOException) e.getCause();
        }
    }

    public static void bind(final Socket socket, final SocketAddress bindpoint) throws IOException {
        try {
            AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws IOException {
                    socket.bind(bindpoint);
                    return null;
                }
            });
        } catch (PrivilegedActionException e) {
            throw (IOException) e.getCause();
        }
    }

    public static boolean connect(final SocketChannel socketChannel, final SocketAddress remoteAddress)
            throws IOException {
        try {
            return AccessController.doPrivileged(new PrivilegedExceptionAction<Boolean>() {
                @Override
                public Boolean run() throws IOException {
                    return socketChannel.connect(remoteAddress);
                }
            });
        } catch (PrivilegedActionException e) {
            throw (IOException) e.getCause();
        }
    }

    public static void bind(final SocketChannel socketChannel, final SocketAddress address) throws IOException {
        try {
            AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws IOException {
                    socketChannel.bind(address);
                    return null;
                }
            });
        } catch (PrivilegedActionException e) {
            throw (IOException) e.getCause();
        }
    }

    public static SocketChannel accept(final ServerSocketChannel serverSocketChannel) throws IOException {
        try {
            return AccessController.doPrivileged(new PrivilegedExceptionAction<SocketChannel>() {
                @Override
                public SocketChannel run() throws IOException {
                    return serverSocketChannel.accept();
                }
            });
        } catch (PrivilegedActionException e) {
            throw (IOException) e.getCause();
        }
    }

    public static void bind(final DatagramChannel networkChannel, final SocketAddress address) throws IOException {
        try {
            AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws IOException {
                    networkChannel.bind(address);
                    return null;
                }
            });
        } catch (PrivilegedActionException e) {
            throw (IOException) e.getCause();
        }
    }

    public static SocketAddress localSocketAddress(final ServerSocket socket) {
        return AccessController.doPrivileged(new PrivilegedAction<SocketAddress>() {
            @Override
            public SocketAddress run() {
                return socket.getLocalSocketAddress();
            }
        });
    }

    public static InetAddress addressByName(final String hostname) throws UnknownHostException {
        try {
            return AccessController.doPrivileged(new PrivilegedExceptionAction<InetAddress>() {
                @Override
                public InetAddress run() throws UnknownHostException {
                    return InetAddress.getByName(hostname);
                }
            });
        } catch (PrivilegedActionException e) {
            throw (UnknownHostException) e.getCause();
        }
    }

    public static InetAddress[] allAddressesByName(final String hostname) throws UnknownHostException {
        try {
            return AccessController.doPrivileged(new PrivilegedExceptionAction<InetAddress[]>() {
                @Override
                public InetAddress[] run() throws UnknownHostException {
                    return InetAddress.getAllByName(hostname);
                }
            });
        } catch (PrivilegedActionException e) {
            throw (UnknownHostException) e.getCause();
        }
    }

    public static InetSocketAddress socketAddress(final String hostname, final int port) {
        return AccessController.doPrivileged(new PrivilegedAction<InetSocketAddress>() {
            @Override
            public InetSocketAddress run() {
                return new InetSocketAddress(hostname, port);
            }
        });
    }

    public static Enumeration<InetAddress> addressesFromNetworkInterface(final NetworkInterface intf) {
        return AccessController.doPrivileged(new PrivilegedAction<Enumeration<InetAddress>>() {
            @Override
            public Enumeration<InetAddress> run() {
                return intf.getInetAddresses();
            }
        });
    }

    public static byte[] hardwareAddressFromNetworkInterface(final NetworkInterface intf) throws SocketException {
        try {
            return AccessController.doPrivileged(new PrivilegedExceptionAction<byte[]>() {
                @Override
                public byte[] run() throws SocketException {
                    return intf.getHardwareAddress();
                }
            });
        } catch (PrivilegedActionException e) {
            throw (SocketException) e.getCause();
        }
    }
}