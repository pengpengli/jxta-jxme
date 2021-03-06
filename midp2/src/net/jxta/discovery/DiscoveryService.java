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
package net.jxta.discovery;

import net.jxta.document.Advertisement;
import net.jxta.id.ID;
import net.jxta.service.Service;

import java.io.IOException;
import java.util.Enumeration;

/**
 * Provides an asynchronous mechanism for discovering Advertisement (Peers,
 * Groups, Pipes, Modules, etc.). The scope of discovery queries can be
 * controlled by specifying a name and attribute pair, and/or a threshold.
 * The threshold is an upper limit the requesting peer specifies for
 * responding peers not to exceed. Each JXTA Peer Group has an instance of
 * a DiscoveryService. The scope of discovery is limited to the group. For
 * example :
 * <p/>
 * <p/>A peer in the soccer group invokes the soccer group's DiscoveryService
 * to discover pipe advertisements for the Score tracker service in the group,
 * and is interested in a maximum of 10 Advertisements from each peer:
 * <pre>
 *  discovery.getRemoteAdvertisements(null, discovery.ADV,
 *                                    "Name", "Score tracker*", 10, null);
 * <p/>
 * </pre>
 * <p/>
 * In the above example, peers that are part of the soccer group would
 * respond. After a getRemoteAdvertisements call is made and the peers respond,
 * a call to getLocalAdvertisements can be made to retrieve results that have
 * been found and added to the local group cache. Alternately, a call to
 * addDiscoveryListener() will provide asynchronous notification of discovered
 * advertisements.
 * <p/>
 * When an Advertisement is published, it is stored, and indexed in the
 * peer's local cache. The Advertisement indexes are also shared with
 * Rendezvous peers. Advertisement indexes may not be shared with other
 * peers immediately, but may be updated as part of a periodic processs. The
 * Discovery Service currently updates remote indexes every 30 seconds.
 * <p/>
 * <p/>It is important to note that what is shared with the rendezvous peer is
 * the index and expiration of the advertisement, not the advertisement. The
 * indexes are republished whenever the peer establishes a new connection with
 * a different rendezvous peer.
 * <p/>
 * <p/>Distributed index garbage collection. A rendezvous peer will GC indexes for
 * a specific peer when it receive a disconnect message, or it has determined
 * that a peer is no longer reachable, the latter action is a lazy GC and is
 * triggered by messenger creation failures which results in a mark and sweep at
 * a future point in time.
 * <p/>
 * <p/>DiscoveryService also provides a mechanism for publishing advertisements,
 * so that they may be discovered. The rules to follow when publishing are:
 * <p/>
 * <ul>
 * <li>
 * use the current discovery service to publish advertisements private to the
 * group.
 * <pre>discovery.publish(adv);
 * </pre></li>
 * <p/>
 * <li>Use the parent's discovery to publish advertisements that public outside
 * of the group. Example : a peer would like publish the "soccer" group in the
 * NetPeerGroup
 * <pre>
 *        parent=soccerGroup.getParent();
 *        discovery= parent.getDiscoveryService()
 *        discovery.publish(adv);
 * </pre></li>
 * </ul>
 * <p/>
 * The threshold can be utilized in peer discovery in situations where a peer
 * is only interested in other peers, and not about additional peers they may
 * know about. To achieve this effect for peer discovery set the Threshold to
 * <code>0</code>
 * <p/>
 * Advertisements are often stored in a persistent local cache. This cache
 * can improve performance and responsiveness by retaining advertisements
 * between restarts.
 *
 * @see net.jxta.service.Service
 * @see net.jxta.resolver.ResolverService
 * @see net.jxta.protocol.DiscoveryQueryMsg
 * @see net.jxta.protocol.DiscoveryResponseMsg
 * @see net.jxta.protocol.ResolverQueryMsg
 * @see net.jxta.protocol.ResolverResponseMsg
 */
public interface DiscoveryService extends Service {

    /**
     * Discovery type Peer
     */
    public final static int PEER = 0;

    /**
     * Discovery type Group
     */
    public final static int GROUP = 1;

    /**
     * Discovery type Advertisement
     */
    public final static int ADV = 2;

    /**
     * Default lifetime time for advertisements. This is the maximum
     * amount of time which the advertisement will remain valid. If the
     * advertisement remains valid after this time, then the creator will
     * need to republish the advertisement.
     */
    public final static long DEFAULT_LIFETIME = 1000L * 60L * 60L * 24L * 365L;

    /**
     * Default expiration time for advertisements. This is the amount of
     * time which advertisements will live in caches. After this time, the
     * advertisement should be refreshed from the source.
     */
    public final static long DEFAULT_EXPIRATION = 1000L * 60L * 60L * 2L;

    /**
     * Infinite lifetime for advertisements. The advertisement is valid
     * forever. (well maybe it will expire when the sun burns out, but not
     * before then).
     */
    public final static long INFINITE_LIFETIME = Long.MAX_VALUE;

    /**
     * Specifies that the advertisement will have no expiration and will be
     * kept indefinitely.
     */
    public final static long NO_EXPIRATION = Long.MAX_VALUE;

    /**
     * Discover advertisements from remote peers.
     * <p/>
     * <p/>The scope of advertisements returned can be narrowed by specifying
     * an <tt>attribute</tt> and <tt>value</tt> pair. You may also limit the
     * number of responses any single peer may return.
     * <p/>
     * <p/>Discovery can be performed in two ways : <ul>
     * <li>With a <tt>null</tt> peerid - The discovery query is
     * propagated on via the Rendezvous Service and via local sub-net
     * utilizing IP multicast.</li>
     * <li>With a provided peerid - The discovery query is forwarded to the
     * specified peer.</li>
     * </ul>
     *
     * @param peerid    The ID of a peer which will receive the query or
     *                  <tt>null</tt> in order to propagate the query.
     * @param type      Discovery type <tt>PEER</tt>, <tt>GROUP</tt>, <tt>ADV</tt>.
     * @param attribute indexed element name (see advertisement(s) for a
     *                  list of indexed fields. A null attribute indicates any advertisement
     *                  of specified type
     * @param value     value of attribute to narrow discovery to valid values for
     *                  this parameter are null (don't care), Exact value, or use of wild
     *                  card(s) (e.g. if a Advertisement defines <Name>FooBar</name> , a value
     *                  of "*bar", "foo*", or "*ooB*", will return the Advertisement
     * @param threshold The upper limit of responses from each peer responding.
     *                  <tt>threshold</tt> of 0, and type of PEER has a special behaviour.
     * @return query ID for this discovery query.
     */
    public int getRemoteAdvertisements(String peerid,
                                       int type,
                                       String attribute,
                                       String value,
                                       int threshold);

    /**
     * Discover advertisements from remote peers.
     * <p/>
     * <p/>The scope of advertisements returned can be narrowed by specifying
     * an <tt>attribute</tt> and <tt>value</tt> pair. You may also limit the
     * number of responses any single peer may return.
     * <p/>
     * <p/>Discovery can be performed in two ways : <ul>
     * <li>With a <tt>null</tt> peerid, the discovery query is
     * propagated on via the Rendezvous Service and via local sub-net
     * utilizing IP multicast.</li>
     * <li>With a provided peerid, the discovery query is forwarded to the
     * specified peer.</li>
     * </ul>
     *
     * @param peerid    The ID of a peer which will receive the query or
     *                  <tt>null</tt> in order to propagate the query.
     * @param type      Discovery type <tt>PEER</tt>, <tt>GROUP</tt>, <tt>ADV</tt>.
     * @param attribute indexed element name (see advertisement(s) for a
     *                  list of indexed fields. A null attribute indicates any advertisement
     *                  of specified type
     * @param value     value of attribute to narrow discovery to valid values for
     *                  this parameter are null (don't care), Exact value, or use of wild
     *                  card(s) (e.g. if a Advertisement defines <Name>FooBar</name> , a value
     *                  of "*bar", "foo*", or "*ooB*", will return the Advertisement
     * @param threshold The upper limit of responses from each peer responding.
     *                  <tt>threshold</tt> of 0, and type of PEER has a special behaviour.
     * @param listener  The listener which will be called when advertisement
     *                  which match this query are discovered or <tt>null</tt> if no
     *                  callback is desired.
     * @return query ID for this discovery query.
     */
    public int getRemoteAdvertisements(String peerid,
                                       int type,
                                       String attribute,
                                       String value,
                                       int threshold,
                                       DiscoveryListener listener);

    /**
     * Retrieve locally stored Advertisements.
     *
     * @param type      Discovery type <tt>PEER</tt>, <tt>GROUP</tt>, <tt>ADV</tt>.
     * @param attribute indexed element name (see advertisement(s) for a
     *                  list of indexed fields. A null attribute indicates any advertisement
     *                  of specified type
     * @param value     value of attribute to narrow discovery to valid
     *                  values for this parameter are null (don't care), Exact value, or use of
     *                  wild card(s) (e.g. if a Advertisement defines <Name>FooBar</name> , a
     *                  value of "*bar", "foo*", or "*ooB*", will return the Advertisement
     * @return Enumeration of stored advertisements.
     * @throws java.io.IOException Thrown if an error occurs during retrieval.
     */
    public Enumeration getLocalAdvertisements(int type, String attribute, String value) throws IOException;

    /**
     * Publish an Advertisement. The Advertisement will expire automatically
     * on the local peer after <code>DEFAULT_LIFETIME</code> and will expire on
     * other peers after <code>DEFAULT_EXPIRATION</code>.
     * <p/>
     * <p/>When an Advertisement is published, it is stored, and indexed in the
     * peer's local cache. The Advertisement indexes are also shared with
     * Rendezvous peers. Advertisement indexes may not be shared with other
     * peers immediately, but may be updated as part of a periodic processs. The
     * Discovery Service currently publishes index updates every 30 seconds.
     *
     * @param adv The Advertisement to publish.
     * @throws IOException When an error occurs during Advertisement publication.
     */
    public void publish(Advertisement adv) throws IOException;

    /**
     * Publish an Advertisement. The Advertisement will expire automatically
     * after the specified time. A peer that discovers this advertisement will
     * hold it for about <tt>expiration</tt> or <tt>lifetime</tt> milliseconds,
     * whichever is smaller.
     * <p/>
     * <p/>When an Advertisement is published, it is stored, and indexed in the
     * peer's local cache. The Advertisement indexes are also shared with
     * Rendezvous peers. Advertisement indexes may not be shared with other
     * peers immediately, but may be updated as part of a periodic processs. The
     * Discovery Service currently publishes index updates every 30 seconds.
     *
     * @param adv        The Advertisement to publish.
     * @param lifetime   Duration in relative milliseconds that this advertisement will exist.
     * @param expiration Duration in relative milliseconds that this advertisement will be cached by other peers.
     * @throws IOException When an error occurs during Advertisement publication.
     */
    public void publish(Advertisement adv, long lifetime, long expiration) throws IOException;

    /**
     * Publish an Advertisement via propagation to other peers on the network.
     * This does not result in the advertisement being stored locally. The
     * Advertisement will be published with an expiration time of
     * <tt>DEFAULT_EXPIRATION</tt>.
     *
     * @param adv Advertisement to publish.
     */
    public void remotePublish(Advertisement adv);

    /**
     * Publish an Advertisement via propagation to other peers on the network.
     * This does not result in the advertisement being stored locally.
     *
     * @param adv        The Advertisement to publish.
     * @param expiration Duration in relative milliseconds that this
     *                   Advertisement will be cached by other peers.
     */
    public void remotePublish(Advertisement adv, long expiration);


    /**
     * Publish an Advertisement to another peer on the network.
     * This does not result in the advertisement being stored locally. The
     * Advertisement will be published with an expiration time of
     * <tt>DEFAULT_EXPIRATION</tt>.
     *
     * @param peerid The ID of a peer, specifying <tt>null</tt> results in
     *               propagation within the group.
     * @param adv    The Advertisement to publish.
     */
    public void remotePublish(String peerid, Advertisement adv);


    /**
     * Publish an Advertisement to another peer on the network. This does not
     * result in the advertisement being stored locally.
     *
     * @param peerid     id of a peer, specifying null results in a propagate
     *                   within the group
     * @param adv        The Advertisement to publish.
     * @param expiration Duration in relative milliseconds that this
     *                   Advertisement will be cached by other peers.
     */
    public void remotePublish(String peerid, Advertisement adv, long expiration);

    /**
     * Removes the specified Advertisement from the cache of locally stored
     * Advertisements.
     *
     * @param adv Advertisement to remove.
     * @throws IOException If there is a problem removing the advertisement.
     */
    public void flushAdvertisement(Advertisement adv) throws IOException;

    /**
     * Removes the specified Advertisement from the cache of locally stored
     * Advertisements.
     *
     * @param id   The {@link Advertisement#getID()} value of the
     *             Advertisement to be removed.
     * @param type Discovery type PEER, GROUP, ADV.
     * @throws IOException If there is a problem removing the advertisement.
     */
    public void flushAdvertisements(String id, int type) throws IOException;

    /**
     * Returns the maximum duration in milliseconds for which this document
     * will be cached by peers other than the publisher. This value is either
     * the stored lifetime or the remaining lifetime of the document, whichever
     * is less.
     *
     * @param id   Document ID, Peer ID, or PeerGroup ID
     * @param type Discovery type PEER, GROUP, ADV
     * @return The number of milliseconds that other peers will be told to
     *         retain this Advertisement in their local caches. -1 is returned if
     *         the Advertisement is not known or already expired.
     */
    public long getAdvExpirationTime(ID id, int type);

    /**
     * Returns the maximum duration in milliseconds for which this document
     * should be kept in local cache.
     *
     * @param id   Document ID, Peer ID, or PeerGroup ID
     * @param type Discovery type PEER, GROUP, ADV
     * @return The number of milliseconds this Advertisement will remain in the
     *         local cache unless refreshed before that time. -1 is returned if the
     *         Advertisement is not known or already expired.
     */
    public long getAdvLifeTime(ID id, int type);

    /**
     * Returns the maximum duration in milliseconds for which this document
     * will be cached by peers other than the publisher. This value is either
     * the stored lifetime or the remaining lifetime of the document, whichever
     * is less.
     *
     * @param adv Advertisement
     * @return The number of milliseconds that other peers will be told to
     *         retain this Advertisement in their local caches. -1 is returned if
     *         the Advertisement is not known or already expired.
     */
    public long getAdvExpirationTime(Advertisement adv);

    /**
     * Returns the maximum duration in milliseconds for which this document
     * should be kept in local cache.
     *
     * @param adv Advertisement
     * @return The number of milliseconds this Advertisement will remain in the
     *         local cache unless refreshed before that time. -1 is returned if the
     *         Advertisement is not known or already expired.
     */
    public long getAdvLifeTime(Advertisement adv);

    /**
     * Register a Discovery listener. The Discovery listener will be called
     * whenever Advertisement responses are received from remote peers by the
     * Discovery Service.
     *
     * @param listener the DiscoveryListener
     */
    public void addDiscoveryListener(DiscoveryListener listener);

    /**
     * Remove a Discovery listener which was previously registered with
     * {@link #getRemoteAdvertisements(String,int,String,String,int,DiscoveryListener) getRemoteAdvertisements()}
     * or {@link #addDiscoveryListener(DiscoveryListener) addDiscoveryListener()}.
     *
     * @param listener The listener to be removed.
     * @return true if the listener was successfully removed, false otherwise
     */
    public boolean removeDiscoveryListener(DiscoveryListener listener);
}
