# Bug reproduction

Run the following commands line by line:
```sh
./gradlew bootBuildImage
docker network create demo
docker run -d --name toxiproxy --rm --network demo ghcr.io/shopify/toxiproxy
docker run -d --name demo1 -p 8080:8080 --rm --network demo demo1:0.0.1-SNAPSHOT
curl localhost:8080/demo
# Press Ctrl+C to interrupt, as it never stops
docker stop toxiproxy
docker stop demo1
docker network rm demo
```
