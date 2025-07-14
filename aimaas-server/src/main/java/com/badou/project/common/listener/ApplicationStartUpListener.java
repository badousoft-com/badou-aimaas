package com.badou.project.common.listener;


import com.badou.bpms.addressbook.BtHandlerRuleAddressBookQueryImpl;
import com.badou.brms.btaddressbook.BtAddressBookRegister;
import com.badou.brms.btaddressbook.IAddressBookRegister;
import com.badou.brms.system.container.InterfaceRegisterContainer;
import com.badou.designer.module.design.ModuleCacheContainer;
import com.badou.filter.XssAndSqlFilter;
import com.badou.platform.loader.ProjectPropertiesLoader;
import com.badou.tools.common.util.SpringHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import com.badou.brms.cfg.syscache.SystemCacheContainer;
import com.badou.brms.dictionary.DictionaryCacheContainer;
import com.badou.brms.system.security.UserRoleResourceCacheContainer;
import com.badou.brms.util.InstanceFactory;
import com.badou.logs.syslog.performace.config.ConfigFactory;
import com.badou.tools.common.cache.CacheFactory;
import com.badou.tools.common.cfg.ConfigContainer;
import com.badou.tools.common.cfg.system.DefaultPropertiesLoader;
import com.badou.tools.common.properties.CacheProperties;
import com.badou.uniapp.cache.AppUserRoleResourceCacheContainer;
import com.badou.database.DataBaseCacheContainer;

/**
 * @author chenjiabao
 * @date 2019年7月1日下午12:19:51
 * @todo 应用程序启动监听类
 * ApplicationEnvironmentPreparedEvent 在应用启动时进行监听
 * ApplicationReadyEvent在应用启动完成后进行监听
 * 取代原先框架中的APPstartuplistener
 */
@Component
public class ApplicationStartUpListener implements ApplicationListener<ApplicationStartedEvent>{

	@Autowired
    WebApplicationContext webApplicationContext;

	@Override
	public void onApplicationEvent(ApplicationStartedEvent event) {

 	 	InstanceFactory.setServletContent(webApplicationContext.getServletContext());
        ConfigContainer.getInstance().register(CacheProperties.class);
        CacheFactory.getInstance().init();

        ConfigContainer.getInstance().register(ProjectPropertiesLoader.class);
        ConfigContainer.getInstance().register(DefaultPropertiesLoader.class);

        DictionaryCacheContainer.getInstance().init();
        SystemCacheContainer.getInstance().init();

        // 注销未注销的登录日志
		//ILogonLogService logonLogService = SpringHelper.getBean(ILogonLogService.class);
		/*logonLogService.updateLogoutData(null);        */

        //PC菜单 按钮加载
        UserRoleResourceCacheContainer.getInstance().init();
        //APP菜单加载
        AppUserRoleResourceCacheContainer.getInstance().init();
        ModuleCacheContainer.getInstance().init();

        ConfigFactory.configure(this.getClass().getClassLoader().getResource("").getPath());
        try {
            DataBaseCacheContainer.getInstance().init();
        }catch (Exception e){

        }
//        ResourceCacheContainer.getInstance().init();
        IAddressBookRegister register = SpringHelper.getBean(BtAddressBookRegister.class);
        //注册抽象处理人地址本到系统中
        register.register(BtHandlerRuleAddressBookQueryImpl.getAddressBookType());

        //扫描系统中所有接口，并在数据库中进行注册
        InterfaceRegisterContainer.getInstance().register(webApplicationContext);

        //注册自定义的XSS和SQL过滤正则到过滤器中
        XssAndSqlFilter.registerRule();
	}
}
