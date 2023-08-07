package org.avokado2.rps.config;

import net.sf.ehcache.config.CacheConfiguration;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig implements CachingConfigurer {

    @Bean(destroyMethod="shutdown")
    public net.sf.ehcache.CacheManager ehCacheManager() {

        net.sf.ehcache.config.Configuration config = new net.sf.ehcache.config.Configuration();

        config.updateCheck(false)
                .monitoring(net.sf.ehcache.config.Configuration.Monitoring.AUTODETECT)
                .dynamicConfig(true);

        config.addCache(new CacheConfiguration()
                .name("setting")
                .timeToIdleSeconds(60000)
                .maxEntriesLocalHeap(1000));

        config.addCache(new CacheConfiguration()
                .name("player-message-ts")
                .timeToLiveSeconds(60)
                .maxEntriesLocalHeap(10000));

        return net.sf.ehcache.CacheManager.newInstance(config);
    }
}