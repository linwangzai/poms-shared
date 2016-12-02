/*
 * Copyright (C) 2014 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.media.broadcaster;

import java.net.URI;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.vpro.domain.user.Broadcaster;
import nl.vpro.domain.user.BroadcasterService;
import nl.vpro.util.URLResource;

/**
 * @author rico
 * @since 3.0
 */
public class BroadcasterServiceImpl implements BroadcasterService {

    private static final Logger LOG = LoggerFactory.getLogger(BroadcasterServiceImpl.class);


    private Map<String, Broadcaster> broadcasterMap = new HashMap<>();
    private URLResource<Map<String, String>> displayNameResource;
    private URLResource<Map<String, String>> misResource;
    private URLResource<Map<String, String>> whatsonResource;


    public BroadcasterServiceImpl(String configFile) {
        this(configFile, true);
    }


    public BroadcasterServiceImpl(String configFile, boolean async) {
        this.displayNameResource = getURLResource(configFile, async);
        URI uri = URI.create(configFile);
        if (uri.getScheme().startsWith("http")) {
            setMisResource(configFile + "mis");
            setWhatsonResource(configFile + "whats_on");
        }


    }

    public void setMisResource(String configFile) {
        LOG.info("Using {} for mis ids", configFile);
        this.misResource = getURLResource(configFile, displayNameResource.isAsync());
    }


    public void setWhatsonResource(String configFile) {
        LOG.info("Using {} for what'son ids", configFile);
        this.whatsonResource = getURLResource(configFile, displayNameResource.isAsync());
    }

    protected URLResource<Map<String, String>> getURLResource(String configFile, boolean async) {
        return URLResource.map(URI.create(configFile), this::fillMap)
            .setMinAge(Duration.of(1, ChronoUnit.HOURS))
            .setAsync(async);
    }

    @Override
    public Broadcaster find(String id) {
        return getRepository().get(id);
    }

    @Override
    public List<Broadcaster> findAll() {
        return new ArrayList<>(getRepository().values());
    }

    @Override
    public Broadcaster update(Broadcaster organization) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void delete(Broadcaster organization) {
        throw new UnsupportedOperationException();
    }

    Map<String, Broadcaster> getRepository() {
        displayNameResource.get();
        if (misResource != null) {
            misResource.get();
        }
        if (whatsonResource!= null) {
            whatsonResource.get();
        }
        return Collections.unmodifiableMap(broadcasterMap);
    }

    protected void fillMap(Map<String, String> properties) {
        Map<String, Broadcaster> result = new HashMap<>();
        for (Map.Entry<String, String> entry : displayNameResource.get().entrySet()) {
            String id = entry.getKey();
            String name = entry.getValue();
            String misId = null;
            if (misResource != null) {
                misId = misResource.get().get(id);
            }
            String whatsonId = null;
            if (whatsonResource != null) {
                whatsonId = whatsonResource.get().get(id);
            }

            String neboId = null;

            Broadcaster broadcaster = new Broadcaster(id.trim(), name.trim(), whatsonId, neboId, misId);
            result.put(broadcaster.getId(), broadcaster);

        }
        broadcasterMap = result;
    }

}
