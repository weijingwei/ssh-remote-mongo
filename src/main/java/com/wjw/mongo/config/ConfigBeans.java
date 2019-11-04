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
    @Value("${qa1.remote.mongo.uri}")
    public String qa1MongoURI;
    @Value("${qa1.remote.mongo.host}")
    public String qa1MongoHost;
    @Value("${qa1.remote.mongo.port}")
    public int qa1MongoPort;

    @Value("${dev3.remote.bastion.host}")
    public String dev3BastionHost;
    @Value("${dev3.remote.mongo.uri}")
    public String dev3MongoURI;
    @Value("${dev3.remote.mongo.host}")
    public String dev3MongoHost;
    @Value("${dev3.remote.mongo.port}")
    public int dev3MongoPort;
}