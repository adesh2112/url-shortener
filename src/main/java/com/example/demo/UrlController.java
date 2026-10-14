package com.example.demo;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class UrlController {

    private final UrlService service;

    public UrlController(UrlService service) {
        this.service = service;
    }

    @PostMapping("/api/urls")
    public ResponseEntity<Map<String, String>> create(@Valid @RequestBody CreateUrlRequest request) {
        String code = service.shorten(request.longUrl());
        String shortUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/" + code).toUriString();
        return ResponseEntity.status(201).body(Map.of("shortCode", code, "shortUrl", shortUrl));
    }

    @GetMapping("/api/urls/{code}/stats")
    public Map<String, Object> stats(@PathVariable String code) {
        UrlMapping m = service.stats(code);
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("shortCode", m.getShortCode());
        out.put("longUrl", m.getLongUrl());
        out.put("clickCount", m.getClickCount());
        out.put("createdAt", m.getCreatedAt());
        return out;
    }

    @GetMapping("/{code}")
    public ResponseEntity<Void> redirect(@PathVariable String code) {
        String target = service.resolve(code);
        return ResponseEntity.status(302).location(URI.create(target)).build();
    }
}