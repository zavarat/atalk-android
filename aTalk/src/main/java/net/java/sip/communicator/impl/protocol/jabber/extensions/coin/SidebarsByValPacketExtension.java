/*
 * Jitsi, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license. See terms of license at gnu.org.
 */
package net.java.sip.communicator.impl.protocol.jabber.extensions.coin;

import net.java.sip.communicator.impl.protocol.jabber.extensions.AbstractPacketExtension;

/**
 * Sidebars by val packet extension.
 *
 * @author Sebastien Vincent
 * @author Eng Chong Meng
 */
public class SidebarsByValPacketExtension extends AbstractPacketExtension
{
    /**
     * The name of the element that contains the sidebars by val.
     */
    public static final String ELEMENT_NAME = "sidebars-by-val";

    /**
     * The namespace that sidebars by val belongs to.
     */
    public static final String NAMESPACE = "";

    /**
     * Constructor.
     */
    public SidebarsByValPacketExtension()
    {
        super(ELEMENT_NAME, NAMESPACE);
    }
}
