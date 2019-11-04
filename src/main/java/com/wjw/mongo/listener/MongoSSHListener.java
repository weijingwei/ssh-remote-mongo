package com.wjw.mongo.listener;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.util.ResourceUtils;

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
        if (Objects.isNull(session)) {
            try {
                JSch jsch = new JSch();
                File file = ResourceUtils.getFile("classpath:id_rsa_atlas_aws");
                System.out.println(file.getAbsolutePath());
                jsch.addIdentity(file.getAbsolutePath());
                session = jsch.getSession(configBeans.bastionUser, configBeans.bastionHost, configBeans.bastionPort);
                Properties config = new Properties();
                config.put("StrictHostKeyChecking", "no");
                session.setConfig(config);
                session.connect();
                int localPort = session.setPortForwardingL("*", configBeans.mongoPort, configBeans.mongoHost,
                        configBeans.mongoPort);
                System.out.println("mongoURI: " + configBeans.mongoURI + " local port: " + localPort);
                Set<String> collectionNames = mongoTemplate.getCollectionNames();
                for (String name : collectionNames) {
                    System.out.println("--------------------- " + name);
                }
            } catch (JSchException | FileNotFoundException e) {
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
