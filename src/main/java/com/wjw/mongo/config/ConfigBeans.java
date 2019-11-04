package com.wjw.mongo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ConfigBeans {
    @Value("${remote.bastion.host}")
    public String bastionHost;
    @Value("${remote.bastion.port}")
    public int bastionPort;
    @Value("${remote.bastion.user}")
    public String bastionUser;
    @Value("${remote.mongo.uri}")
    public String mongoURI;
    @Value("${remote.mongo.host}")
    public String mongoHost;
    @Value("${remote.mongo.port}")
    public int mongoPort;
}
