package com.badou.project;

import java.io.InputStream;
import java.util.Properties;

/**
 * @author chenjiabao
 * @date 2019年7月2日下午2:13:53
 * @todo 通用常量类
 */
public final class CommonConst{

	private static Properties properties = new Properties();

	private static  CommonConst instance;

	private static final String COMMONT_PEOPERTIES_NAME = "/common.properties";

	/**
	 * @auth chenjiabao
	 * @date 2019年7月2日下午6:05:36
	 * @todo 无参构造函数
	 * @throws Exception if has error(直接往外抛)
	 */
	private CommonConst() throws Exception {
		try {
			java.net.URL u = CommonConst.class.getClassLoader().getResource(COMMONT_PEOPERTIES_NAME);
			InputStream is = (InputStream) u.getContent();
			properties.load(is);
		} catch(Exception ex) {
			throw ex;
		}
	}

	/**
	 * @return 杩斿洖閰嶇疆淇℃伅鐨勫疄渚�
	 * @throws Exception if has error(直接往外抛)
	 */
	public static CommonConst getInstance() throws Exception {
		if(instance == null) {
			instance = new CommonConst();
		}
		return instance;
	}

	/**
	 * @auth chenjiabao
	 * @date 2019年7月2日下午6:06:05
	 * @todo 获取节点内容
	 * @param param
	 * @return 节点内容
	 */
	public static String getConstantString(String param) {
		String str = null;
		try {
			str = properties.getProperty(param);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return str;
	}
}
