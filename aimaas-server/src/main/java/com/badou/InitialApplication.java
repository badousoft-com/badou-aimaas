
package com.badou;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 启动主程序类
 * EntityScan 扫描所有实体
 * EnableJpaRepositories 扫描指定包下所有dao层bean
 * @author chenjiabao
 * @date 2019年7月1日下午12:20:04
 */

/**
 * 请保证本地开发时连接自己环境的Redis,否则会导致连接到其他环境的Redis引发任务混合消费导致各种应用任务不能正常完成!!!
 * 请保证本地开发时连接自己环境的Redis,否则会导致连接到其他环境的Redis引发任务混合消费导致各种应用任务不能正常完成!!!
 * 请保证本地开发时连接自己环境的Redis,否则会导致连接到其他环境的Redis引发任务混合消费导致各种应用任务不能正常完成!!!
 */
@SpringBootApplication(exclude = JpaRepositoriesAutoConfiguration.class)
@EnableJpaRepositories(basePackages={"com.badou"})
@EntityScan(basePackages={"com.badou"})
@EnableCaching
@EnableTransactionManagement(proxyTargetClass = true)
@EnableEncryptableProperties
@EnableScheduling
@EnableAsync
//是否启动CAS，若需要CAS则开启该配置
//@EnableCasClient
public class InitialApplication extends SpringBootServletInitializer{

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(InitialApplication.class);
	}


	/**
	 * 程序主入口方法
	 * @author chenjiabao
	 * @date 2019年7月2日下午3:36:22
	 * @param args 启动参数
	 */
	public static void main (String[] args) {
 		SpringApplication.run(InitialApplication.class, args);
	}

	/**
	 * 绑定线程到JPA中的entityManager
	 * @author chenjiabao
	 * @date 2019年7月2日下午3:39:07
	 * @return 绑定线程过滤器
	 */
	@Bean
    public OpenEntityManagerInViewFilter openEntityManagerInViewFilter() {
            return new OpenEntityManagerInViewFilter();
    }

}
