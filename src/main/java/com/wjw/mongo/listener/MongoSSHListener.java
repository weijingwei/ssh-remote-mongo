package com.wjw.mongo.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.wjw.mongo.config.ConfigBeans;

@WebListener
public class MongoSSHListener implements ServletContextListener {
    @Autowired
    private ConfigBeans configBeans;
    @Autowired
    private MongoTemplate mongoTemplate;
    private Session session;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        if (Objects.isNull(session) && configBeans.sshActive) {
            try {
                JSch jsch = new JSch();
                jsch.addIdentity(configBeans.sshIdentity);
                session = jsch.getSession(configBeans.sshUser, configBeans.sshHost, configBeans.sshPort);
                Properties config = new Properties();
                config.put("StrictHostKeyChecking", "no");
                session.setConfig(config);
                session.connect();
                List<Integer> ports = new ArrayList<>();
                for (int i = 0; i < configBeans.mongoReplica.split(",").length; i++) {
                    if (configBeans.mongoURI.contains(configBeans.mongoReplica.split(",")[i])) {
                        ports.add(session.setPortForwardingL("*", configBeans.mongoPort, configBeans.mongoReplica.split(",")[i],
                                configBeans.mongoPort));
                    } else {
                        ports.add(session.setPortForwardingL("*", 0, configBeans.mongoReplica.split(",")[i],
                                configBeans.mongoPort));
                    }
                    configBeans.mongoURI = configBeans.mongoURI.replace(configBeans.mongoReplica.split(",")[i], "localhost");
                }
                System.out.println("mongoURI: " + configBeans.mongoURI + " local ports: " + ports);
                Set<String> collectionNames = mongoTemplate.getCollectionNames();
                for (String name : collectionNames) {
                    System.out.println("--------------------- " + name);
                }
            } catch (JSchException e) {
                e.printStackTrace();
            }
        }
        ServletContextListener.super.contextInitialized(sce);
        System.out.println("--------------------- Listener Started.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (!Objects.isNull(session)) {
            session.disconnect();
        }
        System.out.println("Listener Destroyed.");
        ServletContextListener.super.contextDestroyed(sce);
    }
}
