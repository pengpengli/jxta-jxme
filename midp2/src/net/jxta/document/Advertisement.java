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
package net.jxta.document;

import com.sun.java.util.collections.Map;
import com.sun.java.util.collections.UnsupportedOperationException;
import net.jxta.id.ID;


public abstract class Advertisement {


    /**
     * Returns the identifying type of this Advertisement. Unlike {@link
     * #getAdvertisementType()} this method will return the correct runtime
     * type of an Advertisement object. <p/>
     * <p/>
     * This implementation is provided so as to NOT break the code of existing
     * advertisements. In most cases you should provide your own implementation
     * for efficeiny reasons.
     *
     * @return the identifying type of this Advertisement
     */
    abstract public String getAdvType();

    /**
     * Returns the identifying type of this Advertisement. <p/>
     * <p/>
     * <b>Note:</b> This is a static method. It cannot be used to determine the
     * runtime type of an advertisment. ie. </p> <code><pre>
     *      Advertisement adv = module.getSomeAdv();
     *      String advType = adv.getAdvertisementType();
     *  </pre></code> <p/>
     * <p/>
     * <b>This is wrong and does not work the way you might expect.</b> This
     * call is not polymorphic and calls Advertiement.getAdvertisementType() no
     * matter what the real type of the advertisment.
     *
     * @return String the type of advertisement
     */
    public static String getAdvertisementType() {
        throw new UnsupportedOperationException(
                "Advertisement : sub-class failed to override getAdvertisementType. getAdvertisementType() is static and is *not* polymorphic.");
    }

    /**
     * Write advertisement into a document. <code>asMimeType</code> is a mime
     * media-type specification and provides the form of the document which is
     * being requested. Two standard document forms are defined. <code>"text/plain"</code>
     * encodes the document in a "pretty-print" format for human viewing and
     * <code>"text/xml"<code> which provides an
     * XML format.
     *
     * @param asMimeType MimeMediaType format representation requested
     * @return Document the document to be used in the construction
     */
    public abstract Document getDocument(MimeMediaType asMimeType);

    /**
     * Returns a unique ID suitable for indexing of this Advertisement. <p/>
     * <p/>
     * The ID is supposed to be unique and is not guaranteed to be of any
     * particular subclass of ID. Each class of advertisement is responsible
     * for the choice of ID to return. The value for the ID returned can either
     * be: <p>
     * <p/>
     * <p/>
     * <ul>
     * <li> An ID which is already part of the advertisement definition and
     * is relatively unique between advertisements instances. For example,
     * the Peer Advertisement returns the Peer ID.</li>
     * <li> A static CodatID which is generated via some canonical process
     * which will produce the same value each time and different values for
     * different advertisements of the same type.</li>
     * <li> ID.nullID for advertisement types which are not readily indexed.
     * </li>
     * </ul>
     * <p/>
     * <p/>
     * Since this ID is normally used for indexing, the IDs returned must be as
     * unique as possible to avoid collisions. <p/>
     * <p/>
     * For Advertisement types which normally return non-ID.nullID values no ID
     * should be returned when asked to generate an ID while the Advertisement
     * is an inconsistent state (example: unitialized index fields). Instead
     * <code>java.lang.IllegalStateException</code> should be thrown.
     *
     * @return ID An ID that uniquely identifies the advertisement or
     *         ID.nullID if this advertisement is of a type that is not normally
     *         indexed.
     */
    public abstract ID getID();

    /**
     * returns an array of String fields to index the advertisement on.
     *
     * @return String [] attributes to index this advertisement on.
     */
    public abstract String[] getIndexFields();

    /**
     * returns a Map of the Advertisement indexes and the values
     *
     * @return Map of the Advertisement indexes and the values
     */
    public abstract Map getIndexMap();

    /**
     * Return a string representaion of this advertisement. The string will
     * contain the advertisement formated as a UTF-8 encoded XML Document.
     *
     * @return String a String containing the advertisement.
     */
    public String toString() {

        try {
            StructuredTextDocument doc =
                    (StructuredTextDocument) getDocument(MimeMediaType.XMLUTF8);

            return doc.toString();
        } catch (Throwable e) {
            if (e instanceof Error) {
                throw (Error) e;
            } else if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else {
                throw new RuntimeException(e.getMessage());
            }
        }
    }
}
