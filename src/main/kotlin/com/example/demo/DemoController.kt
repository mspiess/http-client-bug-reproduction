package com.example.demo

import eu.rekawek.toxiproxy.ToxiproxyClient
import eu.rekawek.toxiproxy.model.ToxicDirection
import org.springframework.http.client.ReactorClientHttpRequestFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestClient
import reactor.netty.http.client.HttpClient

@RestController
class DemoController {

    private val restClient: RestClient = RestClient.builder()
        .requestFactory(ReactorClientHttpRequestFactory(HttpClient.create().wiretap(true)))
        .build()

    init {
        val toxiClient = ToxiproxyClient("toxiproxy", 8474)
        if (toxiClient.getProxyOrNull("target") == null) {
            val proxy = toxiClient.createProxy("target", "toxiproxy:80", "example.org:80")
            proxy.toxics().limitData("limit", ToxicDirection.UPSTREAM, 500000)
        }
    }

    @GetMapping("/demo")
    fun demo() {
        val obj = object {
            val str = "ABCDEFGHIJ".repeat(1_000_000)
        }

        restClient
            .post()
            .uri("http://toxiproxy")
            .body(obj)
            .retrieve()
            .toEntity(Any::class.java)
    }
}
