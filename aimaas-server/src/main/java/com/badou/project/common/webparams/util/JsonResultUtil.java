package com.badou.project.common.webparams.util;

import com.badou.brms.base.support.struts.JsonReturnBean;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * @CLassName JsonResultUtil
 * @Decription web层返回数据工具类
 * @Author lm
 * @Version 1.0
 * @Date 2021/07/14
 */
public class JsonResultUtil {

    private static final String MESSAGE = "ok";
    private static final String ERROEMSG = "服务器错误";
    private static final String PARAMSERROR = "参数错误";
    private static final String ERROEDATAMSG = "数据异常";

    /**
     * 判断是否是非空 如果是择通过 如果是抛出错误
     * @param name 出现空的提示音
     * @param value 检查的住
     * @param <T>
     * @return 非空返回值
     */
    public static <T> T notNull(String name, T value) {
        if (isNull(value)) {
            throw new IllegalArgumentException(name + " 未匹配到数据");
        } else {
            return value;
        }
    }

    /**
     * 检查参数是否是空的
     * @param params
     * @return
     */
    public static boolean isNull(Object... params){
        for(Object param:params){
            if(param instanceof String){
                if(StringUtils.isEmpty(String.valueOf(param))){
                    return true;
                }
            }else if(param instanceof BigDecimal){
                BigDecimal bd = (BigDecimal)param;
                if(bd == null || bd.compareTo(BigDecimal.ZERO) <= 0){
                    return true;
                }
            }else if(param instanceof List){
                if(((List) param).isEmpty()){
                    return true;
                }
            }else{
                if(Objects.isNull(param) || "null".equals(param.toString().trim())){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 检查数组数据是否为空 长度>0
     * @param array 数组
     * @return true代表空 false代表不为空
     */
    public static boolean arrayNotEmpty(Object array){
        if(Objects.isNull(array)){
            return false;
        }
        if(array instanceof List){
            List value = (List)array;
            if(value.size() > 0){
                return true;
            }
        }else if(array instanceof Object[]){
            Object[] value = (Object[])array;
            if(value.length > 0){
                return true;
            }
        }
        return false;
    }

    /**
     * 检查数组是否只有一个元素
     * @param array 数组
     * @return true代表是 false代表不是
     */
    public static boolean arrayOneElement(Object array){
        if(Objects.isNull(array)){
            return false;
        }
        if(array instanceof List){
            List value = (List)array;
            if(value.size() ==1){
                return true;
            }
        }else if(array instanceof Object[]){
            Object[] value = (Object[])array;
            if(value.length ==1){
                return true;
            }
        }
        return false;
    }

    /**
     * 检查数组是否为空
     * @param array 数组
     * @return true代表空 false代表不为空
     */
    public static boolean arrayEmpty(Object array){
        if(Objects.isNull(array)){
            return true;
        }
        if(array instanceof List){
            List value = (List)array;
            if(value.size() == 0){
                return true;
            }
        }else if(array instanceof Object[]){
            Object[] value = (Object[])array;
            if(value.length == 0){
                return true;
            }
        }
        return false;
    }

    /**
     * 检查多个参数 是否都是非空
     * @param params
     * @return
     */
    public static boolean isNotNull(Object... params){
        for(Object param:params){
            if(Objects.isNull(param)){
                return false;
            }
            if(param instanceof String){
                if(StringUtils.isEmpty(String.valueOf(param))){
                    return false;
                }
            }else if(param instanceof BigDecimal){
                BigDecimal bd = (BigDecimal)param;
                if(bd == null || bd.compareTo(BigDecimal.ZERO) <= 0){
                    return false;
                }
            }else if(param instanceof List){
                if(((List) param).isEmpty()){
                    return false;
                }
            }else{
                if(param==null){
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 返回调用成功的信息
     * 场景:针对于接口调用成功的通用返回信息
     * @return
     */
    public static JsonReturnBean success(){
        JsonReturnBean jsonReturnBean = new JsonReturnBean();
        jsonReturnBean.setMessage(MESSAGE);
        jsonReturnBean.setHasOk(true);
        return jsonReturnBean;
    }

    /**
     * 返回调用成功的信息
     * 场景:针对于接口调用成功!需要定制返回的数据
     * @return
     */
    public static JsonReturnBean success(Object data){
        JsonReturnBean jsonReturnBean = new JsonReturnBean();
        jsonReturnBean.setBean(data);
        jsonReturnBean.setMessage(MESSAGE);
        jsonReturnBean.setHasOk(true);
        return jsonReturnBean;
    }

    /**
     * 返回调用成功的信息
     * 场景:针对于接口调用成功!需要定制成功信息和返回的数据
     * @return
     */
    public static JsonReturnBean success(Object data, String message){
        JsonReturnBean jsonReturnBean = new JsonReturnBean();
        jsonReturnBean.setBean(data);
        jsonReturnBean.setMessage(message);
        jsonReturnBean.setHasOk(true);
        return jsonReturnBean;
    }

    /**
     * 返回调用成功的信息
     * 场景:针对于接口调用成功!需要定制成功信息
     * @return
     */
    public static JsonReturnBean success(String message){
        JsonReturnBean jsonReturnBean = new JsonReturnBean();
        jsonReturnBean.setMessage(message);
        jsonReturnBean.setHasOk(true);
        return jsonReturnBean;
    }

    /**
     * 返回服务器错误的信息
     * 场景:针对于服务器接口调用后 出现错误的通用错误信息
     * @return
     */
    public static JsonReturnBean error(){
        JsonReturnBean jsonReturnBean = new JsonReturnBean();
        jsonReturnBean.setMessage(ERROEMSG);
        jsonReturnBean.setHasOk(false);
        return jsonReturnBean;
    }

    /**
     * 返回数据错误的信息
     * 场景:针对于根据前端的参数 做数据检查时发现不满足要求时返回的错误
     * @return
     */
    public static JsonReturnBean errorData(){
        JsonReturnBean jsonReturnBean = new JsonReturnBean();
        jsonReturnBean.setMessage(ERROEDATAMSG);
        jsonReturnBean.setHasOk(false);
        return jsonReturnBean;
    }

    /**
     * 返回参数错误的信息
     * 场景:一般针对于前端提供的参数不符合后端接口的要求.比如参数没提供 参数提供了错误的格式等
     * @return
     */
    public static JsonReturnBean paramsError(){
        JsonReturnBean jsonReturnBean = new JsonReturnBean();
        jsonReturnBean.setMessage(PARAMSERROR);
        jsonReturnBean.setHasOk(false);
        return jsonReturnBean;
    }

    /**
     * 返回自定义错误的信息
     * 场景:针对需要自定义返回前端的错误信息的情况
     * @param message 错误信息
     * @return
     */
    public static JsonReturnBean errorMsg(String message){
        JsonReturnBean jsonReturnBean = new JsonReturnBean();
        jsonReturnBean.setMessage(message);
        jsonReturnBean.setHasOk(false);
        return jsonReturnBean;
    }
}
