package nl.vpro.nep.service.impl;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.Properties;
import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.springframework.beans.factory.annotation.Value;

import nl.vpro.jackson2.Jackson2Mapper;
import nl.vpro.nep.domain.NEPItemizeRequest;
import nl.vpro.nep.domain.NEPItemizeResponse;
import nl.vpro.nep.service.NEPItemizeService;

import static org.apache.http.entity.ContentType.APPLICATION_OCTET_STREAM;

/**
 * See also https://jira.vpro.nl/browse/MSE-4435
 * @author Michiel Meeuwissen
 * @since 5.6
 */
@Named("NEPItemizeService")
@Slf4j
public class NEPItemizeServiceImpl implements NEPItemizeService {
    private final Supplier<String> itemizeKey;
    private final String itemizeUrl;

    static final ContentType JSON = ContentType.APPLICATION_JSON.withCharset(Charset.forName("UTF-8"));

    @Inject
    public NEPItemizeServiceImpl(
        @Value("${nep.itemizer.baseUrl}") @Nonnull String itemizeUrl,
        @Named("NEPItemizeServiceAuthenticator") @Nonnull Supplier<String> itemizeKey) {
        this.itemizeKey = itemizeKey;
        this.itemizeUrl = itemizeUrl;
    }

    protected NEPItemizeServiceImpl(Properties properties) {
        this(properties.getProperty("nep.itemizer.baseUrl"), () -> properties.getProperty("nep.itemizer.key"));
    }

    @Override
    @SneakyThrows
    public NEPItemizeResponse itemize(NEPItemizeRequest request) {
        try(CloseableHttpClient httpClient = HttpClients.custom()
            .build()) {
            String playerUrl = itemizeUrl + "/api/itemizer/job";
            log.info("Itemizing {} @ {}", request, playerUrl);
            HttpClientContext clientContext = HttpClientContext.create();
            String json = Jackson2Mapper.getLenientInstance().writeValueAsString(request);
            StringEntity entity = new StringEntity(json, JSON);
            HttpPost httpPost = new HttpPost(playerUrl);
            authenticate(httpPost);
            httpPost.addHeader(new BasicHeader(HttpHeaders.ACCEPT, JSON.toString()));
            log.debug("curl -XPOST -H'Content-Type: application/json' -H'Authorization: {}' -H'Accept: {}' {} --data '{}'", itemizeKey, JSON.toString(), itemizeUrl, json);
            httpPost.setEntity(entity);
            HttpResponse response = httpClient.execute(httpPost, clientContext);

            if (response.getStatusLine().getStatusCode() >= 300) {
                ByteArrayOutputStream body = new ByteArrayOutputStream();
                IOUtils.copy(response.getEntity().getContent(), body);
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode() + "\n" + json + "\n->\n" + body);
            }

            return Jackson2Mapper.getLenientInstance().readValue(response.getEntity().getContent(), NEPItemizeResponse.class);
        }

    }

    @Override
    @SneakyThrows
    public void grabScreen(String identifier, String time, OutputStream outputStream) {
        try(CloseableHttpClient httpClient = HttpClients.custom().build()) {

            HttpClientContext clientContext = HttpClientContext.create();
            String framegrabber = itemizeUrl + "/api/framegrabber?identifier=" + identifier + "&time=" + time;
            HttpGet get = new HttpGet(framegrabber);
            authenticate(get);
            get.addHeader(new BasicHeader(HttpHeaders.ACCEPT, APPLICATION_OCTET_STREAM.toString()));
            log.info("Getting {}", framegrabber);
            try (CloseableHttpResponse execute = httpClient.execute(get, clientContext)) {
                if (execute.getStatusLine().getStatusCode() == 200) {
                    IOUtils.copy(execute.getEntity().getContent(), outputStream);
                } else {
                    StringWriter result = new StringWriter();
                    IOUtils.copy(execute.getEntity().getContent(), result, Charset.defaultCharset());
                    throw new RuntimeException(result.toString());
                }
            }

        }
    }

    private void authenticate(HttpUriRequest request) {
        request.addHeader(new BasicHeader(HttpHeaders.AUTHORIZATION, itemizeKey.get()));


    }

    @Override
    public String toString() {
        return itemizeUrl;
    }
}
