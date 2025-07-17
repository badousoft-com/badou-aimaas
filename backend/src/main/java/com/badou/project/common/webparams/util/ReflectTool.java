package com.badou.project.common.webparams.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 反射，调用Object中的函数、获取Object中的指定变量 scimence
 */
public class ReflectTool {

    /**
     * 调用类静态方法
     * @param className 类名
     * @param methodName 方法名字
     * @param param 方法参数
     * @return
     */
    public static Object CallStaticMethod(String className, String methodName, Object...param) {
        Object result = null;
        Method methodObj = null;
        Class<?> classType = null;
        try {
            classType = Class.forName(className);
            methodObj = classType.getMethod(methodName); 	// 获取类中的函数方法
            methodObj.setAccessible(true);
            result = methodObj.invoke(null, param);					// 反射调用函数方法classObj.methodName()
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (classType == null){
            throw new IllegalStateException("Could not find Class " + className);
        }
        if (methodObj == null) {
            throw new IllegalStateException("Could not find Static method " + methodName + "() in the class " + classType.getName());
        }

        return result;
    }

    /**
     * 反射调用，Obj对象的methodName()函数
     * @param Obj 对象
     * @param methodName 方法名
     * @return
     */
    public static Object CallMethod(Object Obj, String methodName) {
        if(Obj==null){
            throw new NullPointerException("Obj == null");
        }

        Object result = null;
        Method methodObj = null;
        Class<?> classType = null;
        try {
            classType = Obj.getClass();
            methodObj = classType.getMethod(methodName); 	// 获取类中的函数方法
            result = methodObj.invoke(Obj);					// 反射调用函数方法classObj.methodName()
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (methodObj == null) {
            throw new IllegalStateException("Could not find a method " + methodName + "() in the class " + classType.getName());
        }

        return result;
    }

    /**
     * 将字符串的首字母转大写
     * @param str 需要转换的字符串
     * @return 处理完的字符串
     */
    public static String captureName(String str) {
        // 进行字母的ascii编码前移，效率要高于截取字符串进行转换的操作
        char[] cs=str.toCharArray();
        cs[0]-=32;
        return String.valueOf(cs);
    }

    /**
     * 反射调用函数methodName(argClass, argObj)
     * @param Obj 对象
     * @param methodName 方法名字
     * @param argClass 方法对象
     * @param argObj 方法参数
     * @return
     */
    public static Object CallMethod(Object Obj, String methodName, Class<?> argClass, Object argObj) {
        if(Obj==null) {
            throw new NullPointerException("Obj == null");
        }

        Object result = null;
        Method methodObj = null;
        Class<?> classType = null;
        try {
            classType = Obj.getClass();
            methodObj = classType.getMethod(methodName, argClass); 	// 获取类中的函数方法
            result = methodObj.invoke(Obj, argObj);					// 反射调用函数方法classObj.methodName(argObj)
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (methodObj == null){
            throw new IllegalStateException("Could not find a method " + methodName + "(" + argClass.getSimpleName() + ") in the class " + classType.getName());
        }

        return result;
    }

    /**
     * 反射调用函数methodName(...argObj)
     * @param Obj 对象
     * @param methodName 方法名字
     * @param argObj 方法参数
     * @return
     */
    public static Object CallMethodX(Object Obj, String methodName, Object... argObj) {
        if(Obj==null) {
            throw new NullPointerException("Obj == null");
        }

        Object result = null;
        Method methodObj = null;
        Class<?> classType = null;

        Class<?>[] argClass = null;
        try {
            // 获取参数类型
            List<Class<?>> list = new ArrayList<Class<?>>();
            for (Object arg : argObj) {
                list.add(arg.getClass());
            }
            argClass = new Class<?>[list.size()];
            list.toArray(argClass);

            classType = Obj.getClass();
            methodObj = classType.getMethod(methodName, argClass); 	// 获取类中的函数方法
            result = methodObj.invoke(Obj, argObj);					// 反射调用函数方法classObj.methodName(argObj)
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (methodObj == null) {
            String argsType = "";
            for (Class<?> cls : argClass) {
                argsType += cls.getSimpleName() + ", ";
            }
            if (argsType.length() > 2)  {
                argsType = argsType.substring(0, argsType.length() - 2);
            }

            throw new IllegalStateException("Could not find a method " +
                    methodName + "(" + argsType + ") in the class " + classType.getName());
        }

        return result;
    }


    /**
     * 反射获取obj的成员变量fieldName（成员变量名获取，可在调试时动态查看）
     * @param obj 操作对象
     * @param fieldName 字段名字
     * @return 获取到的字段
     */
    public static Object getSubField(Object obj, String fieldName) {
        if(obj==null) {
            throw new NullPointerException("obj == null");
        }

        Object subObj = null;
        try {
            if (obj != null) {
                // 获取成员变量对应的Field方法
                Field field = obj.getClass().getDeclaredField(fieldName);
                // 设置为可访问
                field.setAccessible(true);
                // 通过Field方法从Object中提取子变量
                subObj = field.get(obj);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return subObj;
    }

    /**
     * 反射获取obj的成员变量fieldName（成员变量名获取，可在调试时动态查看
     * @param obj obj
     * @param cls cls
     * @param fieldName fieldName
     * @return 获取到的字段对象
     */
    public static Object getSubField(Object obj, Class<?> cls, String fieldName) {
        if(obj==null) {
            throw new NullPointerException("obj == null");
        }
        Object subObj = null;
        try {
            if (obj != null) {
                // 获取成员变量对应的Field方法
                Field field = cls.getDeclaredField(fieldName);
                // 设置为可访问
                field.setAccessible(true);
                // 通过Field方法从Object中提取子变量
                subObj = field.get(obj);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return subObj;
    }

}