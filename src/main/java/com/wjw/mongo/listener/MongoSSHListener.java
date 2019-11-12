package com.wjw.mongo.listener;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
	private ConfigBeans cb;
	@Autowired
	@Qualifier(value = "qa1MongoTemplate")
	private MongoTemplate qa1MongoTemplate;
	@Autowired
	@Qualifier(value = "dev3SspMongoTemplate")
	private MongoTemplate dev3SspMongoTemplate;
	@Autowired
	@Qualifier(value = "dev3CoreMongoTemplate")
	private MongoTemplate dev3CoreMongoTemplate;
	private List<Session> sessions = new ArrayList<>();

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		if (CollectionUtils.isEmpty(sessions)) {
			try {
				List<Integer> localPorts = new ArrayList<>();
				JSch jsch = new JSch();
				if (new File("/opt/app/conf/id_rsa_atlas_aws").exists()) {
					jsch.addIdentity("/opt/app/conf/id_rsa_atlas_aws");
				} else if (new File(
						"D:/Program Files/sts-4.2.0.RELEASE/workspace/CM/ssh-remote-mongo/target/classes/id_rsa_atlas_aws")
								.exists()) {
					jsch.addIdentity(
							"D:/Program Files/sts-4.2.0.RELEASE/workspace/CM/ssh-remote-mongo/target/classes/id_rsa_atlas_aws");
				} else {
					File file = ResourceUtils.getFile("classpath:id_rsa_atlas_aws");
					jsch.addIdentity(file.getPath());
				}
				Session session = null; 
				Properties config = new Properties();
				config.put("StrictHostKeyChecking", "no");
				try {
					session = jsch.getSession(cb.bastionUser, cb.qa1BastionHost, cb.bastionPort);
					session.setConfig(config);
					session.connect();
					localPorts.add(session.setPortForwardingL("*", cb.qa1CoreLocalPort, cb.qa1CoreMongoHost, cb.qa1CoreMongoPort));
					sessions.add(session);
					System.out.println("--------------------------------------- QA1 ---------------------------------------------");
					qa1MongoTemplate.getCollectionNames().forEach(e -> System.out.println("----------- " + e));
				} catch (Exception e) {
					if (Objects.isNull(session) && session.isConnected()) {
						session.disconnect();
					}
					e.printStackTrace();
				}
				
				try {
					session = jsch.getSession(cb.bastionUser, cb.dev3BastionHost, cb.bastionPort);
					session.setConfig(config);
					session.connect();
					localPorts.add(session.setPortForwardingL("*", cb.dev3SspLocalPort, cb.dev3SspMongoHost, cb.dev3SspMongoPort));
					localPorts.add(session.setPortForwardingL("*", cb.dev3CoreLocalPort, cb.dev3CoreMongoHost, cb.dev3CoreMongoPort));
					sessions.add(session);
					System.out.println("--------------------------------------- DEV3 SSP ---------------------------------------------");
					dev3SspMongoTemplate.getCollectionNames().forEach(e -> System.out.println("----------- " + e));
					System.out.println("--------------------------------------- DEV3 CORE ---------------------------------------------");
					dev3CoreMongoTemplate.getCollectionNames().forEach(e -> System.out.println("----------- " + e));
				} catch (Exception e) {
					if (Objects.isNull(session) && session.isConnected()) {
						session.disconnect();
					}
					e.printStackTrace();
				}

				StringBuffer sb = new StringBuffer("docker run -it --name mysshmongo ");
				localPorts.forEach(localPort -> {
					sb.append("-p " + localPort + ":" + localPort + " ");
				});
				sb.append(" -d biptwjw/mysshmongo");
				System.out.println(sb.toString());
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
				if (!Objects.isNull(session) && session.isConnected()) {
					session.disconnect();
				}
			});
		}
		System.out.println("Listener Destroyed.");
		ServletContextListener.super.contextDestroyed(sce);
	}
}
