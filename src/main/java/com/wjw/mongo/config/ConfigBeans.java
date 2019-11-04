package com.wjw.mongo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ConfigBeans {
    @Value("${remote.ssh.active}")
    public boolean sshActive;
    @Value("${remote.ssh.host}")
    public String sshHost;
    @Value("${remote.ssh.port}")
    public int sshPort;
    @Value("${remote.ssh.user}")
    public String sshUser;
    @Value("${remote.ssh.identity}")
    public String sshIdentity;
    @Value("${remote.mongo.uri}")
    public String mongoURI;
    @Value("${remote.mongo.host}")
    public String mongoHost;
    @Value("${remote.mongo.port}")
    public int mongoPort;
}
