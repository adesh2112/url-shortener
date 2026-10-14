package com.example.demo;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class UrlService {

    private static final Duration CACHE_TTL = Duration.ofHours(1);

    private final UrlRepository repo;
    private final StringRedisTemplate redis;

    public UrlService(UrlRepository repo, StringRedisTemplate redis) {
        this.repo = repo;
        this.redis = redis;
    }

    public String shorten(String longUrl) {
        UrlMapping m = new UrlMapping();
        m.setLongUrl(longUrl);
        m = repo.save(m);
        m.setShortCode(Base62.encode(m.getId()));
        repo.save(m);
        return m.getShortCode();
    }

    public String resolve(String code) {
        String url = cacheGet(code);
        if (url == null) {
            url = repo.findByShortCode(code)
                    .map(UrlMapping::getLongUrl)
                    .orElseThrow(() -> new NotFoundException("Short code not found: " + code));
            cachePut(code, url);
        }
        repo.incrementClicks(code);
        return url;
    }

    public UrlMapping stats(String code) {
        return repo.findByShortCode(code)
                .orElseThrow(() -> new NotFoundException("Short code not found: " + code));
    }

    private String cacheGet(String code) {
        try {
            return redis.opsForValue().get("url:" + code);
        } catch (Exception e) {
            return null;
        }
    }

    private void cachePut(String code, String url) {
        try {
            redis.opsForValue().set("url:" + code, url, CACHE_TTL);
        } catch (Exception ignored) {
        }
    }
}