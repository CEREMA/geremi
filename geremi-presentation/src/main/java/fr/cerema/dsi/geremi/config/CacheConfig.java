package fr.cerema.dsi.geremi.config;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.benmanes.caffeine.cache.Caffeine;

@Configuration
public class CacheConfig {

  @Bean
  public Caffeine caffeineConfig() {
    return Caffeine.newBuilder().expireAfterWrite(24, TimeUnit.HOURS);
  }

  @Bean
  public CacheManager cacheManager(Caffeine caffeine) {
    CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
    caffeineCacheManager.setCacheNames(Arrays.asList("REGION","DEPARTEMENT","COMMUNE","BASSINVIE", "ZONEEMPLOI", "EPCI","ETABLISSEMENT_ORIGINE","ETABLISSEMENT_ETUDE"));
    caffeineCacheManager.setCaffeine(caffeine);
    return caffeineCacheManager;
  }
}
