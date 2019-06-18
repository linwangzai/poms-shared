package nl.vpro.nep.service.impl;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.client.Client;

import org.apache.http.HttpHeaders;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.springframework.beans.factory.annotation.Value;

import nl.vpro.nep.sam.api.AccessApi;
import nl.vpro.nep.sam.model.StreamAccessItem;
import nl.vpro.nep.sam.model.StreamAccessResponseItem;
import nl.vpro.nep.service.NEPSAMService;

/**
 * https://jira.vpro.nl/browse/MSE-3754
 * @author Michiel Meeuwissen
 * @since 5.11
 */
@Named("NEPSAMService")
@Slf4j
public class NEPSAMServiceImpl implements NEPSAMService {

    private String provider = "npo";
    private String platform = "npo";
    private String profile = "dash";


    final Supplier<String> authenticator;

    final String baseUrl;

    private Duration connectTimeout = Duration.ofMillis(1000);
    private Duration connectionRequestTimeout =  Duration.ofMillis(1000);
    private Duration socketTimeout = Duration.ofMillis(1000);

    Client httpClient = null;


    @Inject
    public NEPSAMServiceImpl(
        @Value("${nep.sam-api.baseUrl}") @Nonnull String baseUrl,
        @Value("${nep.sam-api.provider}") String provider,
        @Value("${nep.sam-api.platform}") String platform,
        @Value("${nep.sam-api.profile}") String profile,
        @Named("NEPSAMAuthenticator") @Nonnull Supplier<String> authenticator) {
        this.authenticator = authenticator;
        this.baseUrl = baseUrl;
        this.provider = provider == null ? this.provider : provider;
        this.platform = platform == null ? this.platform : platform;
        this.profile = profile == null ? this.profile : profile;
    }



    @Override
    @SneakyThrows
    public String streamAccess(String streamId, StreamAccessItem request) {
        AccessApi streamApi = getStreamApi();
        StreamAccessResponseItem streamAccessResponseItem = streamApi.v2AccessProviderProviderNamePlatformPlatformNameProfileProfileNameStreamStreamIdPost(provider, platform, profile, streamId, request);
        Map<String, Object> attributes = (Map<String, Object>) streamAccessResponseItem.getData().getAttributes();
        return (String) attributes.get("url");
    }


    AccessApi getStreamApi() {
        AccessApi streamApi = new AccessApi();
        streamApi.getApiClient().addDefaultHeader(HttpHeaders.AUTHORIZATION, authenticator.get());
        streamApi.getApiClient().setBasePath(baseUrl);
        streamApi.getApiClient().setHttpClient(getHttpClient());
        return streamApi;
    }
    private Client getHttpClient() {
        if (httpClient == null) {
            ResteasyClientBuilder builder = new ResteasyClientBuilder();
            builder.connectTimeout(connectTimeout.toMillis(), TimeUnit.MILLISECONDS);
            builder.readTimeout(socketTimeout.toMillis(), TimeUnit.MILLISECONDS);
            httpClient = builder.build();
        }
        return httpClient;
     }
}
