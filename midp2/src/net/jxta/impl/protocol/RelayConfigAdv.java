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

import com.sun.java.util.collections.*;
import net.jxta.document.*;
import net.jxta.endpoint.EndpointAddress;
import net.jxta.id.ID;
import net.jxta.impl.util.TimeUtils;
import net.jxta.util.java.net.URI;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

import java.util.Enumeration;

/**
 * Contains parameters for configuration of the Reference Implemenation Relay
 * Service. <p/>
 * <p/>
 * <pre><code>
 * <p/>
 * </code></pre>
 */
public final class RelayConfigAdv extends ExtendableAdvertisement {

    /**
     * Log4J Logger
     */
    private final static Logger LOG = Logger.getInstance(RelayConfigAdv.class.getName());

    private final static String RELAY_CLIENT_ATTR = "client";

    private final static String RELAY_CLIENT_ELEMENT = "client";
    private final static String RELAY_CLIENT_LEASE_ATTR = "maxLease";
    private final static String RELAY_CLIENT_POLL_ATTR = "messengerPollInterval";

    private final static String RELAY_CLIENT_SEEDS_ELEMENT = "seeds";
    private final static String RELAY_CLIENT_SERVERS_ATTR = "maxRelays";
    private final static String RELAY_SERVER_ANNOUNCE_ATTR = "announceInterval";
    private final static String RELAY_SERVER_ATTR = "server";
    private final static String RELAY_SERVER_CLIENTS_ATTR = "maxClients";

    private final static String RELAY_SERVER_ELEMENT = "server";
    private final static String RELAY_SERVER_LEASE_ATTR = "leaseDuration";
    private final static String RELAY_SERVER_QUEUE_ATTR = "clientQueue";
    private final static String RELAY_SERVER_STALL_ATTR = "stallTimeout";

    private final static String SEED_RELAY_ADDR_ELEMENT = "addr";
    private final static String SEED_RELAY_ADDR_SEEDING_ATTR = "seeding";
    private final static String USE_ONLY_SEEDS_ATTR = "useOnlySeeds";

    /**
     * Our DOCTYPE
     */
    private final static String advType = "jxta:RelayConfig";

    /**
     * Announce interval in relative milliseconds.
     */
    private long announceInterval = -1;

    /**
     * Are we configured as a relay client?
     */
    private boolean clientEnabled = false;

    private final static String[] fields = {};

    /**
     * Max clients lease in relative milliseconds.
     */
    private long maxClientLeaseDuration = -1;

    /**
     */
    private int maxClientMessageQueue = -1;

    /**
     * Max Clients
     */
    private int maxClients = -1;

    /**
     * Max Relays
     */
    private int maxRelays = -1;

    /**
     * Max Lease offered by server in relative milliseconds.
     */
    private long maxServerLeaseDuration = -1;

    /**
     * Messenger poll interval in relative milliseconds.
     */
    private long messengerPollInterval = -1;

    /**
     * Seed Relays
     * <ul>
     * <li> Elements are {@link net.jxta.edpoint.EndpointAddress}</li>
     * </ul>
     */
    private Set seedRelays = new HashSet();

    /**
     * The list of seeding resources. <p/>
     * <p/>
     * <p/>
     * <ul>
     * <li> The values are {@link net.jxta.util.java.net.URI}.</li>
     * </ul>
     */
    private Set seedingURIs = new HashSet();

    /**
     * Are we configured as a relay server?
     */
    private boolean serverEnabled = false;

    /**
     * Stall timeout in relative milliseconds.
     */
    private long stallTimeout = -1;

    /**
     * Use only seeded relays.
     */
    private boolean useOnlySeeds = false;

    /**
     * Use the Instantiator through the factory
     */
    RelayConfigAdv() {
    }

    /**
     * Use the Instantiator through the factory
     *
     * @param root Description of the Parameter
     */
    RelayConfigAdv(Element root) {
        if (!XMLElement.class.isInstance(root)) {
            throw new IllegalArgumentException(getClass().getName() + " only supports XLMElement");
        }

        XMLElement doc = (XMLElement) root;

        String doctype = doc.getName();

        String typedoctype = "";
        Attribute itsType = doc.getAttribute("type");

        if (null != itsType) {
            typedoctype = itsType.getValue();
        }

        if (!doctype.equals(getAdvertisementType()) && !getAdvertisementType().equals(typedoctype)) {
            throw new IllegalArgumentException("Could not construct : " + getClass().getName() + "from doc containing a " + doc.getName());
        }

        Enumeration eachAttr = doc.getAttributes();

        while (eachAttr.hasMoreElements()) {
            Attribute aRelayAttr = (Attribute) eachAttr.nextElement();

            if (RELAY_CLIENT_ATTR.equals(aRelayAttr.getName())) {
                clientEnabled = "true".equals(aRelayAttr.getValue().trim());
            } else if (RELAY_SERVER_ATTR.equals(aRelayAttr.getName())) {
                serverEnabled = "true".equals(aRelayAttr.getValue().trim());
            } else {
                if (LOG.isEnabledFor(Priority.WARN)) {
                    LOG.warn("Unhandled Attribute: " + aRelayAttr.getName());
                }
            }
        }

        Enumeration elements = doc.getChildren();

        while (elements.hasMoreElements()) {
            XMLElement elem = (XMLElement) elements.nextElement();

            if (!handleElement(elem)) {
                if (LOG.isEnabledFor(Priority.WARN)) {
                    LOG.warn("Unhandled Element: " + elem.toString());
                }
            }
        }

        // Sanity Check!!!
        if ((-1 != maxRelays) && (maxRelays <= 0)) {
            throw new IllegalArgumentException("Max relays must not be negative or zero.");
        }

        if ((-1 != maxClientLeaseDuration) && (maxClientLeaseDuration <= 0)) {
            throw new IllegalArgumentException("Max lease duration must not be negative or zero.");
        }

        if ((-1 != messengerPollInterval) && (messengerPollInterval <= 0)) {
            throw new IllegalArgumentException("Messenger poll interval must not be negative or zero.");
        }

        if (useOnlySeeds && clientEnabled && seedRelays.isEmpty() && seedingURIs.isEmpty()) {
            throw new IllegalArgumentException("Cannot specify 'useOnlySeeds' and no seed relays");
        }

        if ((-1 != maxClients) && (maxClients <= 0)) {
            throw new IllegalArgumentException("Max clients must not be negative or zero.");
        }

        if ((-1 != maxClientMessageQueue) && (maxClientMessageQueue <= 0)) {
            throw new IllegalArgumentException("Max client queue must not be negative or zero.");
        }

        if ((-1 != maxServerLeaseDuration) && (maxServerLeaseDuration <= 0)) {
            throw new IllegalArgumentException("Max lease duration must not be negative or zero.");
        }

        if ((-1 != stallTimeout) && (stallTimeout <= 0)) {
            throw new IllegalArgumentException("Client stall timeout duration must not be negative or zero.");
        }

        if ((-1 != announceInterval) && (announceInterval <= 0)) {
            throw new IllegalArgumentException("Announce interval must not be negative or zero.");
        }
    }

    /**
     * Adds a feature to the SeedRelay attribute of the RelayConfigAdv object
     *
     * @param addr The feature to be added to the SeedRelay attribute
     */
    public void addSeedRelay(EndpointAddress addr) {
        if (null == addr) {
            throw new IllegalArgumentException("addr may not be null");
        }

        seedRelays.add(addr);
    }

    /**
     * Adds a feature to the SeedRelay attribute of the RelayConfigAdv object
     *
     * @param addr The feature to be added to the SeedRelay attribute
     */
    public void addSeedRelay(String addr) {
        if (null == addr) {
            throw new IllegalArgumentException("addr may not be null");
        }

        seedRelays.add(new EndpointAddress(addr));
    }

    /**
     * Adds a feature to the SeedingURI attribute of the RelayConfigAdv object
     *
     * @param addr The feature to be added to the SeedingURI attribute
     */
    public void addSeedingURI(URI addr) {
        if (null == addr) {
            throw new IllegalArgumentException("addr may not be null");
        }

        seedingURIs.add(addr);
    }

    /**
     * Adds a feature to the SeedingURI attribute of the RelayConfigAdv object
     *
     * @param addr The feature to be added to the SeedingURI attribute
     */
    public void addSeedingURI(String addr) {
        if (null == addr) {
            throw new IllegalArgumentException("addr may not be null");
        }

        seedingURIs.add(URI.create(addr));
    }

    /**
     * Description of the Method
     */
    public void clearSeedRelays() {
        seedRelays.clear();
    }

    /**
     * Description of the Method
     */
    public void clearSeedingURIs() {
        seedingURIs.clear();
    }

    /**
     * {@inheritDoc}
     *
     * @return The advType value
     */
    public String getAdvType() {
        return getAdvertisementType();
    }

    /**
     * {@inheritDoc}
     *
     * @return The advertisementType value
     */
    public static String getAdvertisementType() {
        return advType;
    }

    /**
     * The interval in relative milliseconds at which relay server will
     * announce its presence.
     *
     * @return The interval in relative milliseconds at which relay server
     *         will broadcast its presence or <code>-1</code> for default value.
     */
    public long getAnnounceInterval() {
        return announceInterval;
    }

    /**
     * {@inheritDoc}
     *
     * @return The baseAdvType value
     */
    public final String getBaseAdvType() {
        return getAdvertisementType();
    }

    /**
     * The interval in relative milliseconds of leases accepted by clients.
     *
     * @return The interval in relative milliseconds of leases accepted by
     *         clients or <code>-1</code> for default value.
     */
    public long getClientLeaseDuration() {
        return maxClientLeaseDuration;
    }

    /**
     * Return the client message queue length size.
     *
     * @return The client message queue length size or <code>-1</code> for
     *         default value.
     */
    public int getClientMessageQueueSize() {
        return maxClientMessageQueue;
    }

    /**
     * {@inheritDoc}
     *
     * @param encodeAs Description of the Parameter
     * @return The document value
     */
    public Document getDocument(MimeMediaType encodeAs) {
        StructuredDocument adv = (StructuredDocument) super.getDocument(encodeAs);

        if (!(adv instanceof Attributable)) {
            throw new IllegalStateException("Only Attributable documents are supported.");
        }

        if ((-1 != maxRelays) && (maxRelays <= 0)) {
            throw new IllegalStateException("Max relays must not be negative or zero.");
        }

        if ((-1 != maxClientLeaseDuration) && (maxClientLeaseDuration <= 0)) {
            throw new IllegalStateException("Max lease duration must not be negative or zero.");
        }

        if ((-1 != messengerPollInterval) && (messengerPollInterval <= 0)) {
            throw new IllegalStateException("Messenger poll interval must not be negative or zero.");
        }

        if (useOnlySeeds && clientEnabled && seedRelays.isEmpty() && seedingURIs.isEmpty()) {
            throw new IllegalStateException("Cannot specify 'useOnlySeeds' and no seed relays");
        }

        if ((-1 != maxClients) && (maxClients <= 0)) {
            throw new IllegalStateException("Max clients must not be negative or zero.");
        }

        if ((-1 != maxClientMessageQueue) && (maxClientMessageQueue <= 0)) {
            throw new IllegalStateException("Max client queue must not be negative or zero.");
        }

        if ((-1 != maxServerLeaseDuration) && (maxServerLeaseDuration <= 0)) {
            throw new IllegalStateException("Max lease duration must not be negative or zero.");
        }

        if ((-1 != stallTimeout) && (stallTimeout <= 0)) {
            throw new IllegalStateException("Client stall timeout duration must not be negative or zero.");
        }

        if ((-1 != announceInterval) && (announceInterval <= 0)) {
            throw new IllegalStateException("Announce interval must not be negative or zero.");
        }

        Attributable attrDoc = (Attributable) adv;

        if (clientEnabled) {
            attrDoc.addAttribute(RELAY_CLIENT_ATTR, Boolean.TRUE.toString());
        }

        if (serverEnabled) {
            attrDoc.addAttribute(RELAY_SERVER_ATTR, Boolean.TRUE.toString());
        }

        Element clientElem = adv.createElement(RELAY_CLIENT_ELEMENT);

        adv.appendChild(clientElem);

        Attributable attrElem = (Attributable) clientElem;

        if (-1 != maxRelays) {
            attrElem.addAttribute(RELAY_CLIENT_SERVERS_ATTR, Integer.toString(maxRelays));
        }

        if (-1 != maxClientLeaseDuration) {
            attrElem.addAttribute(RELAY_CLIENT_LEASE_ATTR, Long.toString(maxClientLeaseDuration));
        }

        if (-1 != messengerPollInterval) {
            attrElem.addAttribute(RELAY_CLIENT_POLL_ATTR, Long.toString(messengerPollInterval));
        }

        if (!seedRelays.isEmpty() || !seedingURIs.isEmpty()) {
            Element seedsElem = adv.createElement(RELAY_CLIENT_SEEDS_ELEMENT);

            clientElem.appendChild(seedsElem);

            attrElem = (Attributable) seedsElem;

            if (useOnlySeeds) {
                attrElem.addAttribute(USE_ONLY_SEEDS_ATTR, Boolean.TRUE.toString());
            }

            Iterator eachSeed = seedRelays.iterator();

            while (eachSeed.hasNext()) {
                Element addrElement = adv.createElement(SEED_RELAY_ADDR_ELEMENT, eachSeed.next().toString());

                seedsElem.appendChild(addrElement);
            }

            eachSeed = seedingURIs.iterator();

            while (eachSeed.hasNext()) {
                Element addrElement = adv.createElement(SEED_RELAY_ADDR_ELEMENT, eachSeed.next().toString());

                seedsElem.appendChild(addrElement);

                ((Attributable) addrElement).addAttribute(SEED_RELAY_ADDR_SEEDING_ATTR, Boolean.TRUE.toString());
            }
        }

        Element serverElem = adv.createElement(RELAY_SERVER_ELEMENT);

        adv.appendChild(serverElem);

        attrElem = (Attributable) serverElem;

        if (-1 != maxClients) {
            attrElem.addAttribute(RELAY_SERVER_CLIENTS_ATTR, Integer.toString(maxClients));
        }

        if (-1 != maxClientMessageQueue) {
            attrElem.addAttribute(RELAY_SERVER_QUEUE_ATTR, Integer.toString(maxClientMessageQueue));
        }

        if (-1 != maxServerLeaseDuration) {
            attrElem.addAttribute(RELAY_SERVER_LEASE_ATTR, Long.toString(maxServerLeaseDuration));
        }

        if (-1 != stallTimeout) {
            attrElem.addAttribute(RELAY_SERVER_STALL_ATTR, Long.toString(stallTimeout));
        }

        if (-1 != announceInterval) {
            attrElem.addAttribute(RELAY_SERVER_ANNOUNCE_ATTR, Long.toString(announceInterval));
        }

        return adv;
    }

    /**
     * {@inheritDoc}
     *
     * @return The iD value
     */
    public ID getID() {
        return ID.nullID;
    }

    /**
     * {@inheritDoc}
     *
     * @return The indexFields value
     */
    public String[] getIndexFields() {
        return fields;
    }

    /**
     * Return the maximum number of relay clients.
     *
     * @return The maximum number of relay clients or <code>-1</code> for
     *         default value.
     */
    public int getMaxClients() {
        return maxClients;
    }

    /**
     * Return the maximum number of relay clients.
     *
     * @return The maximum number of relay clients or <code>-1</code> for
     *         default value.
     */
    public int getMaxRelays() {
        return maxRelays;
    }

    /**
     * The interval in relative milliseconds of at which clients will poll for
     * messengers
     *
     * @return The interval in relative milliseconds of at which clients will
     *         poll for messengers or <code>-1</code> for default value.
     */
    public long getMessengerPollInterval() {
        return messengerPollInterval;
    }

    /**
     * Gets the seedRelays attribute of the RelayConfigAdv object
     *
     * @return The seedRelays value
     */
    public EndpointAddress[] getSeedRelays() {
        return (EndpointAddress[]) seedRelays.toArray(new EndpointAddress[seedRelays.size()]);
    }

    /**
     * Gets the seedingURIs attribute of the RelayConfigAdv object
     *
     * @return The seedingURIs value
     */
    public URI[] getSeedingURIs() {
        return (URI[]) seedingURIs.toArray(new URI[seedingURIs.size()]);
    }

    /**
     * The interval in relative milliseconds of leases offered by servers.
     *
     * @return The interval in relative milliseconds of leases offered by
     *         servers.
     */
    public long getServerLeaseDuration() {
        return maxServerLeaseDuration;
    }

    /**
     * The interval in relative milliseconds after which a client is assumed to
     * no longer be connected if it fails to request messages.
     *
     * @return The interval in relative milliseconds after which a client is
     *         assumed to no longer be connected if it fails to request messages or
     *         <code>-1</code> for default value.
     */
    public long getStallTimeout() {
        return stallTimeout;
    }

    /**
     * If true then this peer will use only seed rendezvous when configured as
     * an edge peer.
     *
     * @return If true then this peer will use only seed rendezvous when
     *         configured as an edge peer.
     */
    public boolean getUseOnlySeeds() {
        return useOnlySeeds;
    }

    /**
     * {@inheritDoc}
     *
     * @param raw Description of the Parameter
     * @return Description of the Return Value
     */
    protected boolean handleElement(Element raw) {

        if (super.handleElement(raw)) {
            return true;
        }

        XMLElement elem = (XMLElement) raw;

        if (RELAY_CLIENT_ELEMENT.equals(elem.getName())) {
            Enumeration eachAttr = elem.getAttributes();

            while (eachAttr.hasMoreElements()) {
                Attribute aRelayAttr = (Attribute) eachAttr.nextElement();

                if (RELAY_CLIENT_SERVERS_ATTR.equals(aRelayAttr.getName())) {
                    maxRelays = Integer.parseInt(aRelayAttr.getValue().trim());
                } else if (RELAY_CLIENT_LEASE_ATTR.equals(aRelayAttr.getName())) {
                    maxClientLeaseDuration = Long.parseLong(aRelayAttr.getValue().trim());
                } else if (RELAY_CLIENT_POLL_ATTR.equals(aRelayAttr.getName())) {
                    messengerPollInterval = Long.parseLong(aRelayAttr.getValue().trim());
                } else {
                    if (LOG.isEnabledFor(Priority.WARN)) {
                        LOG.warn("Unhandled Attribute: " + aRelayAttr.getName());
                    }
                }
            }

            Enumeration elements = elem.getChildren();

            while (elements.hasMoreElements()) {
                XMLElement seedsElem = (XMLElement) elements.nextElement();

                if (RELAY_CLIENT_SEEDS_ELEMENT.equals(seedsElem.getName())) {
                    Enumeration eachSeedsAttr = seedsElem.getAttributes();

                    while (eachSeedsAttr.hasMoreElements()) {
                        Attribute aRelayAttr = (Attribute) eachSeedsAttr.nextElement();

                        if (USE_ONLY_SEEDS_ATTR.equals(aRelayAttr.getName())) {
                            useOnlySeeds = "true".equals(aRelayAttr.getValue().trim());
                        } else {
                            if (LOG.isEnabledFor(Priority.WARN)) {
                                LOG.warn("Unhandled Attribute: " + aRelayAttr.getName());
                            }
                        }
                    }

                    Enumeration addrElements = seedsElem.getChildren();

                    while (addrElements.hasMoreElements()) {
                        XMLElement addrElem = (XMLElement) addrElements.nextElement();

                        if (SEED_RELAY_ADDR_ELEMENT.equals(addrElem.getName())) {
                            String endpAddrString = addrElem.getTextValue();

                            if (null != endpAddrString) {
                                URI endpURI = URI.create(endpAddrString.trim());

                                Attribute seedingAttr = addrElem.getAttribute(SEED_RELAY_ADDR_SEEDING_ATTR);
                                if ((null != seedingAttr) && "true".equals(seedingAttr.getValue().trim())) {
                                    seedingURIs.add(endpURI);
                                } else {
                                    seedRelays.add(new EndpointAddress(endpURI));
                                }
                            }
                        } else {
                            if (LOG.isEnabledFor(Priority.WARN)) {
                                LOG.warn("Unhandled Element: " + elem.toString());
                            }
                        }
                    }
                } else {
                    if (LOG.isEnabledFor(Priority.WARN)) {
                        LOG.warn("Unhandled Element: " + elem.toString());
                    }
                }
            }

            return true;
        } else if (RELAY_SERVER_ELEMENT.equals(elem.getName())) {
            Enumeration eachAttr = elem.getAttributes();

            while (eachAttr.hasMoreElements()) {
                Attribute aRelayAttr = (Attribute) eachAttr.nextElement();

                if (RELAY_SERVER_CLIENTS_ATTR.equals(aRelayAttr.getName())) {
                    maxClients = Integer.parseInt(aRelayAttr.getValue().trim());
                } else if (RELAY_SERVER_QUEUE_ATTR.equals(aRelayAttr.getName())) {
                    maxClientMessageQueue = Integer.parseInt(aRelayAttr.getValue().trim());
                } else if (RELAY_SERVER_LEASE_ATTR.equals(aRelayAttr.getName())) {
                    maxServerLeaseDuration = Long.parseLong(aRelayAttr.getValue().trim());
                } else if (RELAY_SERVER_STALL_ATTR.equals(aRelayAttr.getName())) {
                    stallTimeout = Long.parseLong(aRelayAttr.getValue().trim());
                } else if (RELAY_SERVER_ANNOUNCE_ATTR.equals(aRelayAttr.getName())) {
                    announceInterval = Long.parseLong(aRelayAttr.getValue().trim());
                } else {
                    if (LOG.isEnabledFor(Priority.WARN)) {
                        LOG.warn("Unhandled Attribute: " + aRelayAttr.getName());
                    }
                }
            }

            return true;
        }

        // ////////////// DEPRECATED PARSING ////////////////

        String value = elem.getTextValue();

        if ((null == value) || (0 == value.trim().length())) {
            return false;
        }

        value = value.trim();

        if ("isServer".equals(elem.getName())) {
            serverEnabled = "true".equals(value);

            return true;
        } else if ("isClient".equals(elem.getName())) {
            clientEnabled = "true".equals(value);

            return true;
        } else if ("ServerMaximumClients".equals(elem.getName())) {
            maxClients = Integer.parseInt(value);

            return true;
        } else if ("ClientMaximumServers".equals(elem.getName())) {
            maxRelays = Integer.parseInt(value);

            return true;
        } else if ("ServerLeaseInSeconds".equals(elem.getName())) {
            maxServerLeaseDuration = Long.parseLong(value) * TimeUtils.ASECOND;

            return true;
        } else if ("ClientLeaseInSeconds".equals(elem.getName())) {
            maxClientLeaseDuration = Long.parseLong(value) * TimeUtils.ASECOND;

            return true;
        } else if ("StallTimeoutInSeconds".equals(elem.getName())) {
            stallTimeout = Long.parseLong(value) * TimeUtils.ASECOND;

            return true;
        } else if ("ClientQueueSize".equals(elem.getName())) {
            maxClientMessageQueue = Integer.parseInt(value);

            return true;
        } else if ("BroadcastIntervalInSeconds".equals(elem.getName())) {
            announceInterval = Long.parseLong(value) * TimeUtils.ASECOND;

            return true;
        } else if ("PollIntervalInSeconds".equals(elem.getName())) {
            messengerPollInterval = Long.parseLong(value) * TimeUtils.ASECOND;

            return true;
        } else if ("tcpaddr".equals(elem.getName())) {
            seedRelays.add(new EndpointAddress("tcp", value, null, null));

            return true;
        } else if ("httpaddr".equals(elem.getName())) {
            seedRelays.add(new EndpointAddress("http", value, null, null));

            return true;
        }

        return false;
    }

    /**
     * If true then this peer will act as a relay client.
     *
     * @return If true then this peer will act as a relay client.
     */
    public boolean isClientEnabled() {
        return clientEnabled;
    }

    /**
     * If true then this peer will act as a relay server.
     *
     * @return If true then this peer will act as a relay server.
     */
    public boolean isServerEnabled() {
        return serverEnabled;
    }

    /**
     * Description of the Method
     *
     * @param addr Description of the Parameter
     * @return Description of the Return Value
     */
    public boolean removeSeedRelay(EndpointAddress addr) {
        if (null == addr) {
            throw new IllegalArgumentException("addr may not be null");
        }

        return seedRelays.remove(addr);
    }

    /**
     * Description of the Method
     *
     * @param addr Description of the Parameter
     * @return Description of the Return Value
     */
    public boolean removeSeedingURI(URI addr) {
        if (null == addr) {
            throw new IllegalArgumentException("addr may not be null");
        }

        return seedingURIs.remove(addr);
    }

    /**
     * Sets interval in relative milliseconds at which relay server will
     * announce its presence or <code>-1</code> for default value.
     *
     * @param newvalue The interval in relative milliseconds at which relay
     *                 server will announce its presence.
     */
    public void setAnnounceInterval(long newvalue) {
        if ((-1 != newvalue) && (newvalue <= 0)) {
            throw new IllegalArgumentException("Announce Interval must be > 0");
        }

        announceInterval = newvalue;
    }

    /**
     * If true then this peer will act as a relay client.
     *
     * @param enabled If true then this peer will act as a relay client.
     */
    public void setClientEnabled(boolean enabled) {
        clientEnabled = enabled;
    }

    /**
     * Sets interval in relative milliseconds of leases accepted by clients.
     *
     * @param newvalue The interval in relative milliseconds of leases accepted
     *                 by clients or <code>-1</code> for default value.
     */
    public void setClientLeaseDuration(long newvalue) {
        if ((-1 != newvalue) && (newvalue <= 0)) {
            throw new IllegalArgumentException("Lease Duration must be > 0");
        }

        maxClientLeaseDuration = newvalue;
    }

    /**
     * Sets the client message queue length size.
     *
     * @param newvalue The client message queue length size or <code>-1</code>
     *                 for default value.
     */
    public void setClientMessageQueueSize(int newvalue) {
        if ((-1 != newvalue) && (newvalue <= 0)) {
            throw new IllegalArgumentException("Client Message Queue Size must be > 0");
        }

        maxClientMessageQueue = newvalue;
    }

    /**
     * Sets he maximum number of relay clients.
     *
     * @param newvalue The maximum number of relay clients or <code>-1</code>
     *                 for default value.
     */
    public void setMaxClients(int newvalue) {
        if ((-1 != newvalue) && (newvalue <= 0)) {
            throw new IllegalArgumentException("Max Clients must be > 0");
        }

        maxClients = newvalue;
    }

    /**
     * Sets the maximum number of relay clients.
     *
     * @param newvalue The maximum number of relay clients or <code>-1</code>
     *                 for default value or <code>-1</code> for default value.
     */
    public void setMaxRelays(int newvalue) {
        if ((-1 != newvalue) && (newvalue <= 0)) {
            throw new IllegalArgumentException("Max Relays must be > 0");
        }

        maxRelays = newvalue;
    }

    /**
     * Sets interval in relative milliseconds of at which clients will poll for
     * messengers.
     *
     * @param newvalue The interval in relative milliseconds of at which
     *                 clients will poll for messengers or <code>-1</code> for default
     *                 value.
     */
    public void setMessengerPollInterval(long newvalue) {
        if ((-1 != newvalue) && (newvalue <= 0)) {
            throw new IllegalArgumentException("Poll interval must be > 0");
        }

        messengerPollInterval = newvalue;
    }

    /**
     * If true then this peer will act as a relay server.
     *
     * @param enabled If true then this peer will act as a relay server.
     */
    public void setServerEnabled(boolean enabled) {
        serverEnabled = enabled;
    }

    /**
     * Sets interval in relative milliseconds of leases offered by servers.
     *
     * @param newvalue The interval in relative milliseconds of leases offered
     *                 by servers or <code>-1</code> for default value.
     */
    public void setServerLeaseDuration(long newvalue) {
        if ((-1 != newvalue) && (newvalue <= 0)) {
            throw new IllegalArgumentException("Lease Duration must be >= 0");
        }

        maxServerLeaseDuration = newvalue;
    }

    /**
     * Sets interval in relative milliseconds after which a client is assumed
     * to no longer be connected if it fails to request messages.
     *
     * @param newvalue The interval in relative milliseconds after which a
     *                 client is assumed to no longer be connected if it fails to request
     *                 messages or <code>-1</code> for default value.
     */
    public void setStallTimeout(long newvalue) {
        if ((-1 != newvalue) && (newvalue <= 0)) {
            throw new IllegalArgumentException("Stall timeout must be > 0");
        }

        stallTimeout = newvalue;
    }

    /**
     * Set whether this peer will use only seed rendezvous when configured as
     * an edge peer.
     *
     * @param onlySeeds The new useOnlySeeds value
     */
    public void setUseOnlySeeds(boolean onlySeeds) {
        useOnlySeeds = onlySeeds;
    }

    /**
     * Instantiator for RelayConfigAdv
     */
    public static class Instantiator implements AdvertisementFactory.Instantiator {

        /**
         * {@inheritDoc}
         *
         * @return The advertisementType value
         */
        public String getAdvertisementType() {
            return advType;
        }

        /**
         * {@inheritDoc}
         *
         * @return Description of the Return Value
         */
        public Advertisement newInstance() {
            return new RelayConfigAdv();
        }

        /**
         * {@inheritDoc}
         *
         * @param root Description of the Parameter
         * @return Description of the Return Value
         */
        public Advertisement newInstance(Element root) {
            return new RelayConfigAdv(root);
        }
    }

    /**
     * {@inheritDoc}
     */
    public final Map getIndexMap() {
        return Collections.unmodifiableMap(new HashMap());
    }
}
