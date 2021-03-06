/*
 *  Copyright (c) 2002 Sun Microsystems, Inc.  All rights reserved.
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
 *  4. The names "Sun", "Sun Microsystems, Inc.", "JXTA" and "Project JXTA" must
 *  not be used to endorse or promote products derived from this
 *  software without prior written permission. For written
 *  permission, please contact Project JXTA at http://www.jxta.org.
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
 *  This license is based on the BSD license adopted by the Apache Foundation.
 *
 *  $Id: PipeResolverMessage.java,v 1.2 2005/06/01 16:53:12 hamada Exp $
 */
package net.jxta.protocol;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import net.jxta.document.AdvertisementFactory;
import net.jxta.document.Document;
import net.jxta.document.MimeMediaType;
import net.jxta.id.ID;
import net.jxta.peer.PeerID;
import net.jxta.pipe.PipeID;

/**
 *  This abstract class defines the PipeResolver Message. <p/>
 *
 *  This message is part of the Pipe Resolver Protocol.
 *
 *@see    net.jxta.pipe.PipeService
 *@see    net.jxta.protocol.PipeAdvertisement
 *@see    <a href="http://spec.jxta.org/nonav/v1.0/docbook/JXTAProtocols.html#proto-pbp"
 *      target='_blank'>JXTA Protocols Specification : Standard JXTA Protocols
 *      </a>
 */
public abstract class PipeResolverMessage {

    /**
     *  For responses, if true then this message indicates the pipe is present
     *  otherwise the pipe is not present.
     */
    private boolean found = true;

    /**
     *
     *  <ul>
     *    <li> For query : The peer advertisement of the querying peer.</li>
     *
     *    <li> For response : The peer advertisement of the responding peer.
     *    </li>
     *  </ul>
     *
     */
    private PeerAdvertisement inputPeerAdv = null;

    /**
     *  The type of message this object is communicating.
     */
    private MessageType msgType = null;

    /**
     *
     *  <ul>
     *    <li> For query : The peer ids which should respond to this query.</li>
     *
     *    <li> For response : The peer id on which is responding.</li>
     *  </ul>
     *
     */
    private Set peerids = new HashSet();

    private PipeAdvertisement pipeAdv = null;

    /**
     *  The type of the pipe which is the subject of this message.
     */
    private String pipeType = null;

    /**
     *  The pipe which is the subject of this message.
     */
    private ID pipeid = ID.nullID;

    /**
     *  Creates a new unintialized pipe resolver message
     */
    public PipeResolverMessage() {
        super();
    }

    /**
     *  Add a peer to the set of peers to which this query is directed.
     *
     *@param  id  the peer id to add.
     */
    public void addPeerID(ID id) {

        if (!(id instanceof PeerID)) {
            throw new IllegalArgumentException("can only add peer ids");
        }
        peerids.add(id);
    }

    /**
     *  Write message into a document. asMimeType is a mime media-type
     *  specification and provides the form of the document which is being
     *  requested. Two standard document forms are defined. "text/plain" encodes
     *  the document in "pretty-print" format for human viewing and "text/xml"
     *  which provides an XML format.
     *
     *@param  asMimeType  MimeMediaType format representation requested
     *@return             Document the document to be used in the construction
     */
    public abstract Document getDocument(MimeMediaType asMimeType);

    /**
     *  Gets the inputPeerAdv attribute of the PipeResolverMessage object
     *
     *@return    The inputPeerAdv value
     */
    public PeerAdvertisement getInputPeerAdv() {
        return inputPeerAdv;
    }

    /**
     *  returns the Message type. This will match the XML doctype declaration.
     *
     *@return    a string
     */
    public static String getMessageType() {
        return "jxta:PipeResolver";
    }

    /**
     *  Returns whether this message is a query or a response.
     *
     *@return    the type of this message.
     */
    public MessageType getMsgType() {
        return msgType;
    }

    /**
     *  Returns a {@link java.util.Set} (possibly empty) containing the peer ids
     *  which should respond to this query.
     *
     *@return    set containing the peer ids to which this peer is directed.
     */
    public Set getPeerIDs() {

        return Collections.unmodifiableSet(peerids);
    }

    /**
     *  Gets the pipeAdvertisement attribute of the PipeResolverMessage object
     *
     *@return    The pipeAdvertisement value
     */
    public PipeAdvertisement getPipeAdvertisement() {
        if (pipeAdv == null && (getPipeID().equals(ID.nullID) && getPipeType() != null)) {
            pipeAdv = (PipeAdvertisement)
                    AdvertisementFactory.newAdvertisement(
                    PipeAdvertisement.getAdvertisementType());
            pipeAdv.setPipeID(getPipeID());
            pipeAdv.setType(getPipeType());
        }
        return pipeAdv;
    }

    /**
     *  Return the id of the pipe which is the subject of this message.
     *
     *@return    the id of the pipe which is the subject of this message.
     */
    public ID getPipeID() {
        return pipeid;
    }

    /**
     *  Return the pipe type of the pipe which is the subject of this message.
     *
     *@return    the pipe type of the pipe which is the subject of this message.
     */
    public String getPipeType() {
        return pipeType;
    }

    /**
     *  If true then the pipe was found ont he
     *
     *@return    The found value
     */
    public boolean isFound() {
        return found;
    }

    /**
     *  Sets the found attribute of the PipeResolverMessage object
     *
     *@param  isFound  The new found value
     */
    public void setFound(boolean isFound) {
        found = isFound;
    }

    /**
     *  Sets the inputPeerAdv attribute of the PipeResolverMessage object
     *
     *@param  peerAdv  The new inputPeerAdv value
     */
    public void setInputPeerAdv(PeerAdvertisement peerAdv) {
        inputPeerAdv = peerAdv;
    }

    /**
     *  Sets the message type of this message.
     *
     *@param  type  the type this message is to be.
     */
    public void setMsgType(MessageType type) {
        msgType = type;
    }

    /**
     *  Set the id of pipe which is to be subject of this message.
     *
     *@param  id  the pipe id which is the subject of this message.
     */
    public void setPipeID(ID id) {

        if (!(id instanceof PipeID)) {
            throw new IllegalArgumentException("can only set to pipe ids.");
        }
        pipeid = id;
    }

    /**
     *  Set the pipe type of the pipe which is the subject of this message.
     *
     *@param  type  The pipe type of the pipe which is to be the subject of this
     *      message.
     */
    public void setPipeType(String type) {
        pipeType = type;
    }

    /**
     *  An enumeration class for message types.
     */
    public static class MessageType implements Cloneable {

        /**
         *  A response message
         */
        public final static MessageType ANSWER =
            new MessageType() {
                public String toString() {
                    return "Pipe Resolver Answer Message";
                }
            };
        /**
         *  A query message
         */
        public final static MessageType QUERY =
            new MessageType() {
                public String toString() {
                    return "Pipe Resolver Query Message";
                }
            };

        /**
         *  only the intrinsically declared types are supported.
         */
        private MessageType() { }

        /**
         *  {@inheritDoc} <p/>
         *
         *  immutable and thus clone just returns <tt>this</tt> .
         *
         *@return    Description of the Return Value
         */
        public Object clone() {
            return this;
        }

        /**
         *  {@inheritDoc} <p/>
         *
         *  only match ourselves.
         *
         *@param  target  Description of the Parameter
         *@return         Description of the Return Value
         */
        public boolean equals(Object target) {
            return (this == target);
        }
    }

}

