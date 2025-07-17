package com.badou.project.common;

import javax.servlet.ServletContextEvent;

import com.badou.brms.util.InstanceFactory;
import com.badou.tools.common.cache.CacheFactory;
import com.badou.tools.common.cfg.ConfigContainer;
import com.badou.tools.common.listener.ServletContextLoaderListener;
import com.badou.tools.common.properties.CacheProperties;

/**
 * @author chenjiabao
 * @date 2019年7月2日下午2:18:27
 * @todo servlet容器启动类监听器
 */
public class ServletStartupLoaderListener extends ServletContextLoaderListener {

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		super.contextDestroyed(arg0);
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		super.contextInitialized(arg0);
		InstanceFactory.setServletContent(arg0.getServletContext());
		/*SpringHelper.setServletContent(arg0.getServletContext());
		SpringHelper.setServletContent(arg0.getServletContext());*/
		ConfigContainer.getInstance().register(CacheProperties.class);
		CacheFactory.getInstance().init();
 
	/*	// cas配置文件
		ConfigContainer.getInstance().register(com.shengdai.ssl.cas.CASPropertiesLoader.class);
		// version配置文件
		ConfigContainer.getInstance().register(com.shengdai.version.VersionPropertiesLoader.class);*/
		
	}
	
}
