package com.wbr.cache;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

// proxyTargetClass=true so classes advised by @Cacheable get a CGLIB subclass proxy rather
// than a JDK interface proxy - callers that depend on the concrete type (e.g. a self-injected
// bean used to route around self-invocation) would otherwise fail to get a proxy at all.
@Configuration
@EnableCaching(proxyTargetClass = true)
public class CacheConfiguration {}
