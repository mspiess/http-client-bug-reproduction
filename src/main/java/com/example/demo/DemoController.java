package com.example.demo;

import eu.rekawek.toxiproxy.ToxiproxyClient;
import eu.rekawek.toxiproxy.model.ToxicDirection;
import org.springframework.http.client.ReactorClientHttpRequestFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import reactor.netty.http.client.HttpClient;

import java.io.IOException;

@RestController
public class DemoController {
    public DemoController() throws IOException {
        var toxiClient = new ToxiproxyClient("toxiproxy", 8474);
        if (toxiClient.getProxyOrNull("target") == null) {
            var proxy = toxiClient.createProxy("target", "toxiproxy:80", "example.org:80");
            proxy.toxics().limitData("limit", ToxicDirection.UPSTREAM, 500000);
        }
    }

    private final RestClient restClient = RestClient.builder()
        .requestFactory(new ReactorClientHttpRequestFactory(HttpClient.create().wiretap(true)))
        .build();

    static class Body {
        public final String str = "ABCDEFGHIJ".repeat(1_000_000);
    }

    @GetMapping("/demo")
    public void demo() {
        var obj = new Body();

        restClient
                .post()
                .uri("http://toxiproxy")
                .body(obj)
                .retrieve()
                .toEntity(Object.class);
    }
}
