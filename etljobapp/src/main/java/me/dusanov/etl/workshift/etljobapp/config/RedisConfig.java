package me.dusanov.etl.workshift.etljobapp.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.convert.*;
import org.springframework.data.redis.core.mapping.RedisMappingContext;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericToStringSerializer;

import java.util.Arrays;

@Configuration
@EnableRedisRepositories
@ConfigurationProperties(prefix = "redis.datasource")
public class RedisConfig {

    @Getter @Setter private String hostname;
    @Getter @Setter private int port;
    @Getter @Setter private int dbindex;

    @Bean
    public MappingRedisConverter redisConverter(RedisMappingContext mappingContext,
                                                RedisCustomConversions customConversions,
                                                ReferenceResolver referenceResolver) {

        MappingRedisConverter mappingRedisConverter = new MappingRedisConverter(mappingContext, null, referenceResolver,
                customTypeMapper());
        mappingRedisConverter.setCustomConversions(redisCustomConversions(new ESTDateToString(),new StringToESTDate()));
        return mappingRedisConverter;
    }

    @Bean
    public RedisTypeMapper customTypeMapper() {
        return new CustomRedisTypeMapper();
    }
    class CustomRedisTypeMapper extends DefaultRedisTypeMapper { }


    @Bean
    public RedisCustomConversions redisCustomConversions(ESTDateToString dateToString,
                                                         StringToESTDate stringToESTDate) {
        return new RedisCustomConversions(Arrays.asList(dateToString, stringToESTDate));
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        template.setKeySerializer(new GenericToStringSerializer<Object>(Object.class));
        template.setValueSerializer(new GenericToStringSerializer<Object>(Object.class));

        /*
        https://github.com/spring-projects/spring-data-redis/blob/master/src/main/asciidoc/reference/redis-repositories.adoc#tx.spring
        Redis Repositories require at least Redis Server version 2.8.0 and do not work with transactions.
        Make sure to use a RedisTemplate with disabled transaction support.
        */
        //template.setEnableTransactionSupport(true);

        return template;
    }

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(hostname, port);
        //redisStandaloneConfiguration.setPassword(RedisPassword.of("yourRedisPasswordIfAny"));
        redisStandaloneConfiguration.setDatabase(dbindex);
        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(redisStandaloneConfiguration);
        jedisConnectionFactory.getPoolConfig().setMaxTotal(50);
        jedisConnectionFactory.getPoolConfig().setMaxIdle(50);
        return jedisConnectionFactory;
    }
}