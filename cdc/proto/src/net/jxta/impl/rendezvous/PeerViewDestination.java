/*
 *  Copyright (c) 2004 Sun Microsystems, Inc.  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  1. Redistributions of source code must retain the above copyright
 *  notice, this list of conditions and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in
 *  the documentation and/or other materials provided with the
 *  distribution.
 *
 *  3. The end-user documentation included with the redistribution,
 *  if any, must include the following acknowledgment:
 *  "This product includes software developed by the
 *  Sun Microsystems, Inc. for Project JXTA."
 *  Alternately, this acknowledgment may appear in the software itself,
 *  if and wherever such third-party acknowledgments normally appear.
 *
 *  4. The names "Sun", "Sun Microsystems, Inc.", "JXTA" and
 *  "Project JXTA" must not be used to endorse or promote products
 *  derived from this software without prior written permission.
 *  For written permission, please contact Project JXTA at
 *  http://www.jxta.org.
 *
 *  5. Products derived from this software may not be called "JXTA",
 *  nor may "JXTA" appear in their name, without prior written
 *  permission of Sun.
 *
 *  THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 *  WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED.  IN NO EVENT SHALL SUN MICROSYSTEMS OR
 *  ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 *  SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 *  LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 *  USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 *  OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 *  OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 *  SUCH DAMAGE.
 *  ====================================================================
 *
 *  This software consists of voluntary contributions made by many
 *  individuals on behalf of Project JXTA.  For more
 *  information on Project JXTA, please see
 *  <http://www.jxta.org/>.
 *
 *  This license is based on the BSD license adopted by the Apache
 *  Foundation.
 *
 *  $Id: PeerViewDestination.java,v 1.1 2005/05/15 19:18:35 hamada Exp $
 */
package net.jxta.impl.rendezvous;

import net.jxta.endpoint.EndpointAddress;
import net.jxta.id.ID;

/**
 *  This class contains only the comparable portion of PeerViewElement, so that
 *  it is possible to search for elements in the sorted set that the local
 *  PeerView is, without having enough information to create a valid
 *  PeerViewElement.
 */
public class PeerViewDestination implements Comparable {

    /**
     *  An explicit endpoint address. This is normally a peerID based address
     *  but we happen to need it more often in the address form (it has also
     *  occured in the past that it could usefully be a real transport address).
     */
    private transient EndpointAddress destAddress = null;


    /**
     *  Constructs a PeerViewDestination from the given endpoint address.
     *
     *@param  addr  Description of the Parameter
     */
    public PeerViewDestination(EndpointAddress addr) {
        destAddress = addr;
    }


    /**
     *  Constructs a PeerViewDestination from a (peer)ID.
     *
     *@param  peerId  Description of the Parameter
     */
    public PeerViewDestination(ID peerId) {
        destAddress = new EndpointAddress("jxta", peerId.getUniqueValue().toString(), null, null);
    }


    /**
     *  {@inheritDoc} Note that only the protocol address and at a lower order
     *  the protocol name are considered for comparision.
     *
     *@param  other  Description of the Parameter
     *@return        Description of the Return Value
     */
    public int compareTo(Object other) {
        PeerViewDestination pve = (PeerViewDestination) other;

        int result = destAddress.getProtocolAddress().compareTo(pve.destAddress.getProtocolAddress());

        if (result != 0) {
            return result;
        }

        return destAddress.getProtocolName().compareTo(pve.destAddress.getProtocolName());
    }


    /**
     *  {@inheritDoc}
     *
     *@param  other  Description of the Parameter
     *@return        Description of the Return Value
     */
    public boolean equals(Object other) {

        if (null == other) {
            return false;
        }

        if (this == other) {
            return true;
        }

        return 0 == compareTo(other);
    }


    /**
     *  returns the destination address.
     *
     *@return    The destAddress value
     */
    public EndpointAddress getDestAddress() {
        return destAddress;
    }


    /**
     *  {@inheritDoc}
     *
     *@return    Description of the Return Value
     */
    public int hashCode() {
        return destAddress.hashCode();
    }
}

