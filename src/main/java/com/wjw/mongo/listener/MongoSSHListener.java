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
	private ConfigBeans cb;
	@Autowired
	private MongoTemplate mongoTemplate;
	private List<Session> sessions;

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
				Session session = jsch.getSession(cb.bastionUser, cb.qa1BastionHost, cb.bastionPort);
				Properties config = new Properties();
				config.put("StrictHostKeyChecking", "no");
				session.setConfig(config);
				session.connect();
				localPorts.add(session.setPortForwardingL("*", cb.qa1CoreLocalPort, cb.qa1CoreMongoHost, cb.qa1CoreMongoPort));
				
				session = jsch.getSession(cb.bastionUser, cb.dev3BastionHost, cb.bastionPort);
				session.setConfig(config);
				session.connect();
				localPorts.add(session.setPortForwardingL("*", cb.dev3SspLocalPort, cb.dev3SspMongoHost, cb.dev3SspMongoPort));
				localPorts.add(session.setPortForwardingL("*", cb.dev3CoreLocalPort, cb.dev3CoreMongoHost, cb.dev3CoreMongoPort));

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
