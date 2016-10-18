/**
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Transient;
import javax.validation.Valid;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Inheritance(strategy = InheritanceType.JOINED)
@Cacheable(true)
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
public class Editor extends AbstractUser {

    private static final Logger LOG = LoggerFactory.getLogger(Editor.class);


    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "principalid")
    /*@org.hibernate.annotations.Cascade({
        org.hibernate.annotations.CascadeType.ALL
    })
    */
    @OrderBy("organization.id asc")
    //@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
////    @IsEmployee Works on domain model, but not on Hibernate persisted collections
    @Valid
    @XmlTransient
    SortedSet<BroadcasterEditor> broadcasters = new TreeSet<>();

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "principalid")
    @OrderBy("organization.id asc")
    //@Cacheable(true)
    //@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @Valid
    @XmlTransient
    SortedSet<PortalEditor> portals = new TreeSet<>();

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "principalid")
/*
    @org.hibernate.annotations.Cascade({
        org.hibernate.annotations.CascadeType.ALL
    })
    @SortNatural
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
*/
    @Valid
    @XmlTransient
    @OrderBy("organization.id asc")
    SortedSet<ThirdPartyEditor> thirdParties = new TreeSet<>();

    @Transient
    private SortedSet<Broadcaster> allowedBroadcasterCache;

    @Transient
    private SortedSet<Broadcaster> activeBroadcasterCache;

    @Transient
    private SortedSet<Portal> allowedPortalCache;

    @Transient
    private SortedSet<Portal> activePortalCache;

    @Transient
    private SortedSet<ThirdParty> allowedThirdPartyCache;

    @Transient
    private SortedSet<ThirdParty> activeThirdPartyCache;

    @Transient
    private Set<String> roles;

    protected Editor() {
    }

    public Editor(String principalId, String displayName, String email, Broadcaster broadcaster, Set<String> roles) {
        super(principalId, displayName, email);
        if(broadcaster != null) {
            broadcasters.add(new BroadcasterEditor(this, broadcaster, true));
        }
        if (roles == null) {
            LOG.warn("No roles for {}", principalId);
        }
        this.roles = roles == null ? Collections.emptySet() : Collections.unmodifiableSet(roles);
    }

    Editor(String principalId, String displayName, String email, Broadcaster broadcaster, String givenName, String familyName, Instant lastLogin) {
        this(principalId, displayName, email, broadcaster, Collections.emptySet());
        this.givenName = givenName;
        this.familyName = familyName;
        this.lastLogin = lastLogin;
    }

    public boolean hasEqualRights(Editor editor) {
        return editor != null
            &&
            Objects.equals(this.getPrincipalId(), editor.getPrincipalId()) &&
            Objects.equals(this.roles, editor.getRoles()) &&
            Objects.equals(this.getAllowedBroadcasters(), editor.getAllowedBroadcasters()) &&
            Objects.equals(this.getAllowedPortals(), editor.getAllowedPortals()) &&
            Objects.equals(this.getAllowedThirdParties(), editor.getAllowedThirdParties());
    }


    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public Set<String> getRoles() {
        return this.roles;
    }

    public Broadcaster getEmployer() {
        for(BroadcasterEditor rel : broadcasters) {
            if(rel.isEmployee()) {
                return rel.getOrganization();
            }
        }
        return null;
    }


    void setEmployer(Broadcaster broadcaster) {
        BroadcasterEditor toAdd = new BroadcasterEditor(this, broadcaster, true);

        for(Iterator<BroadcasterEditor> iterator = broadcasters.iterator(); iterator.hasNext(); ) {
            BroadcasterEditor existing = iterator.next();

            if(broadcaster != null && toAdd.equals(existing)) {
                if(existing.isEmployee()) {
                    return;
                } else {
                    iterator.remove();
                }
            } else if(existing.isEmployee()) {
                iterator.remove();
            }
        }
        broadcasters.add(toAdd);
        allowedBroadcasterCache = null;
    }

    SortedSet<Broadcaster> getAllowedBroadcasters() {
        if(allowedBroadcasterCache == null) {
            allowedBroadcasterCache = new TreeSet<>();

            Broadcaster broadcaster = getEmployer();
            if(broadcaster != null) {
                allowedBroadcasterCache.add(broadcaster);
            }

            for(BroadcasterEditor rel : broadcasters) {
                final Broadcaster organization = rel.getOrganization();
                if(organization != null) {
                    allowedBroadcasterCache.add(organization);
                }
            }
            allowedBroadcasterCache = Collections.unmodifiableSortedSet(allowedBroadcasterCache);
        }
        return allowedBroadcasterCache;
    }

    SortedSet<Broadcaster> getActiveBroadcasters() {
        if(activeBroadcasterCache == null) {
            activeBroadcasterCache = new TreeSet<>();

            for(BroadcasterEditor rel : broadcasters) {
                if(rel.isActive()) {
                    final Broadcaster organization = rel.getOrganization();
                    if(organization != null) {
                        activeBroadcasterCache.add(organization);
                    }
                }
            }
            activeBroadcasterCache = Collections.unmodifiableSortedSet(activeBroadcasterCache);
        }
        return activeBroadcasterCache;
    }

    boolean isActive(Broadcaster broadcaster) {
        for (BroadcasterEditor be : broadcasters) {
            if (broadcaster.equals(be.getOrganization())) {
                return be.isActive();
            }
        }
        return false;
    }

    void setActiveBroadcaster(String broadcasterId, boolean value) {
        setActive(new Broadcaster(broadcasterId, broadcasterId), value);
    }

    void setActive(Broadcaster broadcaster, boolean value) {
        for (BroadcasterEditor be : broadcasters) {
            if (broadcaster.equals(be.getOrganization())) {
                be.setActive(value);
                activeBroadcasterCache = null;
                return;
            }
        }
        throw new IllegalArgumentException();
    }

    void addBroadcaster(Broadcaster broadcaster) {
        BroadcasterEditor toAdd = new BroadcasterEditor(this, broadcaster);
        if (broadcasters.add(toAdd)) {
            allowedBroadcasterCache = null;
            activeBroadcasterCache = null;
        }
    }

    void removeBroadcaster(Broadcaster broadcaster) {
        BroadcasterEditor toRemove = new BroadcasterEditor(this, broadcaster);
        if (broadcasters.remove(toRemove)) {
            activeBroadcasterCache = null;
            allowedBroadcasterCache = null;
        }
    }

    SortedSet<Portal> getAllowedPortals() {
        if(allowedPortalCache== null) {
            allowedPortalCache = new TreeSet<>();

            for(PortalEditor rel : portals) {
                if (rel.getOrganization() != null) {
                    allowedPortalCache.add(rel.getOrganization());
                }
            }
            allowedPortalCache = Collections.unmodifiableSortedSet(allowedPortalCache);
        }
        return allowedPortalCache;
    }

    SortedSet<Portal> getActivePortals() {
        if(activePortalCache == null) {
            activePortalCache = new TreeSet<>();

            for(PortalEditor rel : portals) {
                if(rel.isActive()) {
                    activePortalCache.add(rel.getOrganization());
                }
            }
            activePortalCache = Collections.unmodifiableSortedSet(activePortalCache);
        }
        return activePortalCache;
    }

    boolean isActive(Portal portal) {
        for (PortalEditor be : portals) {
            if (portal.equals(be.getOrganization())) {
                return be.isActive();
            }
        }
        return false;
    }

    void setActivePortal(String portalId, boolean value) {
        setActive(new Portal(portalId, null), value);
    }

    void setActive(Portal portal, boolean value) {
        for (PortalEditor be : portals) {
            if (portal.equals(be.getOrganization())) {
                be.setActive(value);
                activePortalCache= null;
                return;
            }
        }
        throw new IllegalArgumentException();
    }

    void addPortal(Portal portal) {
        if (portal == null) {
            LOG.warn("Cannot add null to {}", this);
            return;
        }
        PortalEditor toAdd = new PortalEditor(this, portal);
        if(portals.add(toAdd)) {
            allowedPortalCache = null;
            activePortalCache = null;
        }
    }

    void removePortal(Portal portal) {
        PortalEditor toRemove = new PortalEditor(this, portal);
        if (portals.remove(toRemove)) {
            allowedPortalCache = null;
            activePortalCache = null;
        }
    }

    SortedSet<ThirdParty> getAllowedThirdParties() {
        if(allowedThirdPartyCache == null) {
            allowedThirdPartyCache = new TreeSet<>();

            for(ThirdPartyEditor rel : thirdParties) {
                allowedThirdPartyCache.add(rel.getOrganization());
            }
            allowedThirdPartyCache = Collections.unmodifiableSortedSet(allowedThirdPartyCache);
        }
        return allowedThirdPartyCache;
    }

    void addThirdParty(ThirdParty thirdParty) {
        if (thirdParty == null) {
            LOG.warn("Cannot add null to {}", this);
            return;
        }
        ThirdPartyEditor toAdd = new ThirdPartyEditor(this, thirdParty);
        toAdd.setActive(true);
        if(thirdParties.add(toAdd)) {
            allowedThirdPartyCache = null;
            activeThirdPartyCache = null;
        }
    }

    void removeThirdParty(ThirdParty thirdParty) {
        ThirdPartyEditor toRemove = new ThirdPartyEditor(this, thirdParty);
        if (thirdParties.remove(toRemove)) {
            allowedThirdPartyCache = null;
            activeThirdPartyCache = null;
        }
    }

    Collection<Organization> getOrganizations() {
        return Stream.concat(Stream.concat(getAllowedBroadcasters().stream(), getAllowedPortals().stream()), getAllowedThirdParties().stream()).collect(Collectors.toSet());
    }

    String getOrganization() {
        return getEmployer().getId();
    }
}
