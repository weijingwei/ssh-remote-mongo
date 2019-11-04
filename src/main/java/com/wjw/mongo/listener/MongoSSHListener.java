package com.wjw.mongo.listener;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.util.CollectionUtils;
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
    private List<Session> sessions;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        if (CollectionUtils.isEmpty(sessions)) {
            try {
            	List<Integer> localPorts = new ArrayList<>();
                JSch jsch = new JSch();
                File file = ResourceUtils.getFile("classpath:id_rsa_atlas_aws");
                System.out.println(file.getAbsolutePath());
                jsch.addIdentity(file.getAbsolutePath());
                Session session = jsch.getSession(configBeans.bastionUser, configBeans.qa1BastionHost, configBeans.bastionPort);
                Properties config = new Properties();
                config.put("StrictHostKeyChecking", "no");
                
                session.setConfig(config);
                session.connect();
                localPorts.add(session.setPortForwardingL("*", configBeans.qa1MongoPort, configBeans.qa1MongoHost,
                		configBeans.qa1MongoPort));
                System.out.println("mongoURI: " + configBeans.qa1MongoURI);

                session = jsch.getSession(configBeans.bastionUser, configBeans.dev3BastionHost, configBeans.bastionPort);
                session.setConfig(config);
                session.connect();
                localPorts.add(session.setPortForwardingL("*", configBeans.dev3MongoPort, configBeans.dev3MongoHost,
                		configBeans.dev3MongoPort));
                System.out.println("mongoURI: " + configBeans.dev3MongoURI);
                
                StringBuffer sb = new StringBuffer("docker run -it --name mysshmongo ");
                localPorts.forEach(localPort -> {
                	sb.append("-p " + localPort + ":" + localPort + " ");
                });
                sb.append(" -d biptwjw/mysshmongo");
                System.out.println(sb.toString());
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
        if (!CollectionUtils.isEmpty(sessions)) {
        	sessions.forEach(session -> {
        		session.disconnect();
        	});
        }
        System.out.println("Listener Destroyed.");
        ServletContextListener.super.contextDestroyed(sce);
    }
}
