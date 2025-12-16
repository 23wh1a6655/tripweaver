package com.tripweaver.controller;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api")
public class PhotoController {

    @Value("${google.places.api.key:}")
    private String googleApiKey;

    @GetMapping(value = "/photo-legacy", produces = MediaType.ALL_VALUE)
    public ResponseEntity<byte[]> fetchPhotoLegacy(@RequestParam String ref) {
        String decoded = java.net.URLDecoder.decode(ref, java.nio.charset.StandardCharsets.UTF_8);
        String url = "https://maps.googleapis.com/maps/api/place/photo"
                + "?maxwidth=800"
                + "&photo_reference=" + decoded
                + "&key=" + googleApiKey;
        RestTemplate rt = new RestTemplate();
        org.springframework.http.ResponseEntity<byte[]> resp = rt.getForEntity(url, byte[].class);
        org.springframework.http.MediaType ct = resp.getHeaders().getContentType();
        org.springframework.http.HttpHeaders out = new org.springframework.http.HttpHeaders();
        if (ct != null) out.setContentType(ct);
        out.setCacheControl("public, max-age=86400");
        return new ResponseEntity<>(resp.getBody(), out, resp.getStatusCode());
    }
}
