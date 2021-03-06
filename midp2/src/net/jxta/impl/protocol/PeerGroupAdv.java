/*
 *  Copyright (c) 2001-2008 Sun Microsystems, Inc.  All rights
 *  reserved.
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
 *  =========================================================
 *
 *  This software consists of voluntary contributions made by many
 *  individuals on behalf of Project JXTA.  For more
 *  information on Project JXTA, please see
 *  <http://www.jxta.org/>.
 *
 *  This license is based on the BSD license adopted by the Apache Foundation.
 *
 *  $Id: $
 */
package net.jxta.impl.protocol;

import net.jxta.document.*;
import net.jxta.id.ID;
import net.jxta.peergroup.PeerGroupID;
import net.jxta.platform.ModuleClassID;
import net.jxta.platform.ModuleSpecID;
import net.jxta.protocol.PeerGroupAdvertisement;
import net.jxta.util.java.net.URI;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

import java.util.Enumeration;
import java.util.Hashtable;

public class PeerGroupAdv extends PeerGroupAdvertisement {

    /**
     * Log4J Logger
     */
    private static final Logger LOG = Logger.getInstance(PeerGroupAdv.class.getName());

    public static class Instantiator implements AdvertisementFactory.Instantiator {

        /**
         * Returns the identifying type of this Advertisement.
         *
         * @return String the type of advertisement
         * @since JXTA 1.0
         */
        public String getAdvertisementType() {
            return PeerGroupAdv.getAdvertisementType();
        }

        /**
         * Constructs an instance of <CODE>Advertisement</CODE> matching the type
         * specified by the <CODE>advertisementType</CODE> parameter.
         *
         * @param advertisementType Specifies the mime media type to be associated with the
         *                          <CODE>StructuredDocument</CODE> to be created.
         * @return The instance of <CODE>Advertisement</CODE> or null if it
         *         could not be created.
         * @throws InvocationTargetException error invoking target constructor
         * @since JXTA 1.0
         */
        public Advertisement newInstance() {
            return new PeerGroupAdv();
        }

        /**
         * Constructs an instance of <CODE>Advertisement</CODE> matching the type
         * specified by the <CODE>advertisementType</CODE> parameter.
         *
         * @param root Specifies a portion of a StructuredDocument which will be
         *             converted into an Advertisement.
         * @return The instance of <CODE>Advertisement</CODE> or null if it
         *         could not be created.
         * @throws InvocationTargetException error invoking target constructor
         * @since JXTA 1.0
         */
//        public Advertisement newInstance(net.jxta.document.Element root) {
        public Advertisement newInstance(net.jxta.document.Element root) {
            return new PeerGroupAdv(root);
        }
    }

    public PeerGroupAdv() {
        // set defaults
        setDescription(null);
        setName(null);
        setModuleSpecID(null);
        setPeerGroupID(null);
    }

    public PeerGroupAdv(Element root) {
        if (!XMLElement.class.isInstance(root))
            throw new IllegalArgumentException(getClass().getName() + " only supports XLMElement");

        XMLElement doc = (XMLElement) root;

        String doctype = doc.getName();

        String typedoctype = "";
        Attribute itsType = doc.getAttribute("type");
        if (null != itsType)
            typedoctype = itsType.getValue();

        if (!doctype.equals(getAdvertisementType()) && !getAdvertisementType().equals(typedoctype)) {
            throw new IllegalArgumentException("Could not construct : "
                    + getClass().getName() + "from doc containing a " + doc.getName());
        }

        Enumeration elements = doc.getChildren();

        while (elements.hasMoreElements()) {
            XMLElement elem = (XMLElement) elements.nextElement();

            if (!handleElement(elem)) {
                if (LOG.isEnabledFor(Priority.DEBUG))
                    LOG.debug("Unhandled Element: " + elem.toString());
            }
        }

        // Sanity Check!!!

    }

    /**
     * {@inheritDoc}
     */
    protected boolean handleElement(Element raw) {

        if (super.handleElement(raw))
            return true;

        XMLElement elem = (XMLElement) raw;

        if (elem.getName().equals(nameTag)) {
            setName(elem.getTextValue());
            return true;
        }

        if (elem.getName().equals(descTag)) {
            setDescription(elem.getTextValue());
            return true;
        }

        if (elem.getName().equals(gidTag)) {
            setPeerGroupID((PeerGroupID) ID.create(URI.create(elem.getTextValue())));
            return true;
        }

        if (elem.getName().equals(msidTag)) {
            setModuleSpecID((ModuleSpecID) ID.create(URI.create(elem.getTextValue())));
            return true;
        }

        if (elem.getName().equals(svcTag)) {
            Enumeration elems = elem.getChildren();
            String classID = null;
            Element param = null;
            while (elems.hasMoreElements()) {
                TextElement e = (TextElement) elems.nextElement();
                if (e.getName().equals(mcidTag)) {
                    classID = e.getTextValue();
                    continue;
                }
                if (e.getName().equals(paramTag)) {
                    param = e;
                    continue;
                }
            }
            if (classID != null && param != null) {
                // Add this param to the table. putServiceParam()
                // clones param into a standalone document automatically.
                // (classID gets cloned too).
                putServiceParam(ID.create(URI.create(classID)), param);
            }
            return true;
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    public Document getDocument(MimeMediaType encodeAs) {
        StructuredDocument adv = (StructuredDocument) super.getDocument(encodeAs);

        Element e;
        e = adv.createElement(gidTag, getID().toString());
        adv.appendChild(e);

        e = adv.createElement(msidTag, getModuleSpecID().toString());
        adv.appendChild(e);

        e = adv.createElement(nameTag, getName());
        adv.appendChild(e);

        String description = getDescription();
        if (null != description) {
            e = adv.createElement(descTag, description);
            adv.appendChild(e);
        }

        // FIXME: this is inefficient - we force our base class to make
        // a deep clone of the table.
        Hashtable serviceParams = getServiceParams();
        Enumeration classIds = serviceParams.keys();
        while (classIds.hasMoreElements()) {
            ModuleClassID classId = (ModuleClassID) classIds.nextElement();

            Element s = adv.createElement(svcTag);
            adv.appendChild(s);

            e = adv.createElement(mcidTag, classId.toString());
            s.appendChild(e);

            e = (Element) serviceParams.get(classId);
            StructuredDocumentUtils.copyElements(adv, s, e, paramTag);

        }
        return adv;
    }

    /**
     * {@inheritDoc}
     */
    public final String [] getIndexFields() {
        return fields;
    }
}
