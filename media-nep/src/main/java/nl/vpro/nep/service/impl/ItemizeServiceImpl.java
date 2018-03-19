package nl.vpro.nep.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import nl.vpro.jackson2.Jackson2Mapper;
import nl.vpro.nep.domain.NEPItemizeRequest;
import nl.vpro.nep.domain.NEPItemizeResponse;
import nl.vpro.nep.service.ItemizeService;

/**
 * @author Michiel Meeuwissen
 * @since 5.6
 */
@Service("ItemizeService")
public class ItemizeServiceImpl implements ItemizeService {


    private final String itemizeKey;
    private final String itemizeUrl;

    @Inject
    public ItemizeServiceImpl(
        @Value("${nep.player.itemize.key}") String itemizeKey,
        @Value("${nep.player.itemize.url}") String itemizeUrl) {
        this.itemizeKey = itemizeKey;
        this.itemizeUrl = itemizeUrl;
    }

    @Override
    public NEPItemizeResponse itemize(NEPItemizeRequest request) {
        CloseableHttpClient httpClient = HttpClients.custom()
            .build();
        HttpClientContext clientContext = HttpClientContext.create();

        try {
            String json = Jackson2Mapper.getLenientInstance().writeValueAsString(request);
            StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
            HttpPost httpPost = new HttpPost(itemizeUrl);
            httpPost.addHeader(new BasicHeader("Authentication", itemizeKey));
            httpPost.setEntity(entity);
            HttpResponse response = httpClient.execute(httpPost, clientContext);

            if (response.getStatusLine().getStatusCode() >= 300) {
                ByteArrayOutputStream body = new ByteArrayOutputStream();
                IOUtils.copy(response.getEntity().getContent(), body);
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode() + "\n" + json + "\n->\n" + body);
            }

            return Jackson2Mapper.getLenientInstance().readValue(response.getEntity().getContent(), NEPItemizeResponse.class);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        } finally {
            IOUtils.closeQuietly(httpClient);
        }

    }

    @Override
    public String toString() {
        return itemizeUrl;
    }
}
