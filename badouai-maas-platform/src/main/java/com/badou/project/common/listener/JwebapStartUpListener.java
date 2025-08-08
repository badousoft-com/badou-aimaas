package com.badou.project.common.listener;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.context.event.ApplicationStartingEvent;
import org.springframework.context.ApplicationListener;

/**
 * @author chenjiabao
 * @date 2019年7月1日下午12:21:01
 * @todo Jwebap监听器
 */
public class JwebapStartUpListener implements ApplicationListener<ApplicationStartingEvent> , ServletContextListener{

	private ServletContext servletContext;

	private static final Log LOG = LogFactory.getLog(JwebapStartUpListener.class);

	public static final String CONFIG_PARAM_NAME="jwebap-config";

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		this.servletContext = sce.getServletContext();
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onApplicationEvent(ApplicationStartingEvent event) {/*
		//String configPath = servletContext.getInitParameter(CONFIG_PARAM_NAME);
		String configPath = "jwebap.xml" ;
		try {
			Resource resource = new ClassPathResource("jwebap.xml");
			//Assert.assertNotNull(configPath,"please make sure your application
			cantains context-param '"+CONFIG_PARAM_NAME+"' in web.xml .");
			String path = resource.getFile().getPath();
			if(path!=null){
				//TODO
				LOG.error("需要重写加载jewbap的方法");
				//Startup.startup(path);
			}else{
				URL url = servletContext.getResource(configPath);
				Assert.assertNotNull(url,"jwebap config not found :
				context-param '"+CONFIG_PARAM_NAME+"'= "+configPath);
				Startup.startup(url);
			}
		} catch (Throwable e) {
			e.printStackTrace();
			LOG.warn(e.getMessage());
		}
	*/}

}
