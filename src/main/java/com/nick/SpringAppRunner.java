package com.nick;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.management.remote.JMXConnectorServer;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.SimpleLayout;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.util.ResourceUtils;

public class SpringAppRunner {
	static Logger log = Logger.getLogger(SpringAppRunner.class);

	public static void run(String appName, String [] args) {
		try {
			// Set up a basic log4j appender if there is none
			if (!Logger.getRoot().getAllAppenders().hasMoreElements()) {
				ConsoleAppender a = new ConsoleAppender(new SimpleLayout());
				a.setThreshold(Priority.INFO);
				a.activateOptions();
				Logger.getRoot().addAppender(a);
			}
			
			doRun(appName, args);
		} catch (Throwable t) {
			System.err.println("Error running app");
			t.printStackTrace();
			log.fatal("Error running app", t);
		}
	}
	
	protected static void doRun(String appName, String [] args) 
	throws FileNotFoundException, IOException {
		
		// Load main xml config file (this is normally part of the 
		// distribution and not changed to configure per-system options)
		/*String configDirPath = System.getProperty("spring.config.dir","file:conf");

		String configFile = configDirPath + "/" + appName + "-config.xml";
		
		GenericApplicationContext ctx = new GenericApplicationContext();
		ctx.registerShutdownHook();

		Resource springCfg = ctx.getResource(configFile);

		File configDir = ResourceUtils.getFile(
				springCfg.getURI()).getParentFile();
		
		// Set up log4j
		File log4jConfig = new File(configDir, "log4j.properties");
		
		if (log4jConfig.exists()) {
			PropertyConfigurator.configure(log4jConfig.getPath());
		} else {
			System.err.println("Could not load log4j config: " + log4jConfig);
		}
		
		
		// load the beans
		XmlBeanDefinitionReader beanCfgReader = 
			new XmlBeanDefinitionReader(ctx);
		
		beanCfgReader.loadBeanDefinitions(springCfg);
		
		// Refresh the context to create/activate the beans
		ctx.refresh();
		
		// Log the JMX Url
		if (ctx.containsBean("jmxConnectorServer")) {
			JMXConnectorServer jmxConnectorServer = 
				(JMXConnectorServer) ctx.getBean("jmxConnectorServer");
			log.info("JMX Connector Server: " + 
					jmxConnectorServer.getAddress());
		}*/
		
		AbstractApplicationContext  context = new FileSystemXmlApplicationContext("C:/workspace/feedmanager/conf/feedmanager.xml");
		context.registerShutdownHook();
		
		System.out.println("Done");
		
	}
	
	public static void main(String[] args) {
		/*String appName = System.getProperty("cm.spring.appname");
		if (appName == null) {
			throw new RuntimeException("cm.spring.appname must be set");
		}*/
		
		SpringAppRunner.run("feedmanager", args);
	}
}
