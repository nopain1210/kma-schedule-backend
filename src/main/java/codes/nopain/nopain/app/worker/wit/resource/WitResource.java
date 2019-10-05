package codes.nopain.nopain.app.worker.wit.resource;

import codes.nopain.nopain.app.config.WitProperties;
import codes.nopain.nopain.app.worker.wit.resource.response.WitResponse;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class WitResource {
    private final WitProperties witProperties;

    private static final String RESOURCE = "https://api.wit.ai/message";
    private static final String ENCODING = "UTF-8";

    private HttpRequestFactory requestFactory = new NetHttpTransport().createRequestFactory();

    public WitResponse fetch(String message) throws IOException {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
        String now = dtf.format(LocalDateTime.now());

        HttpRequest request = requestFactory.buildGetRequest(
                new GenericUrl(String.format(
                        "%s?v=%s&verbose=true&q=%s",
                        RESOURCE,
                        now,
                        URLEncoder.encode(message.toLowerCase(), ENCODING))
                )
        );
        HttpHeaders headers = request.getHeaders();
        headers.setAuthorization(String.format("Bearer %s", witProperties.getAccessToken()));
        request.setParser(new JsonObjectParser(new JacksonFactory()));

        return request.execute().parseAs(WitResponse.class);
    }
}
