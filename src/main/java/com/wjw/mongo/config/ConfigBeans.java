package com.wjw.mongo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ConfigBeans {
    @Value("${remote.bastion.port}")
    public int bastionPort;
    @Value("${remote.bastion.user}")
    public String bastionUser;
    
    @Value("${qa1.remote.bastion.host}")
    public String qa1BastionHost;
    @Value("${qa1.core.remote.mongo.host}")
    public String qa1CoreMongoHost;
    @Value("${qa1.core.remote.mongo.port}")
    public int qa1CoreMongoPort;
    @Value("${qa1.core.local.port}")
    public int qa1CoreLocalPort;

    @Value("${dev3.remote.bastion.host}")
    public String dev3BastionHost;
    @Value("${dev3.ssp.remote.mongo.host}")
    public String dev3SspMongoHost;
    @Value("${dev3.ssp.remote.mongo.port}")
    public int dev3SspMongoPort;
    @Value("${dev3.ssp.local.port}")
    public int dev3SspLocalPort;
    
    @Value("${dev3.core.remote.mongo.host}")
    public String dev3CoreMongoHost;
    @Value("${dev3.core.remote.mongo.port}")
    public int dev3CoreMongoPort;
    @Value("${dev3.core.local.port}")
    public int dev3CoreLocalPort;
    
}