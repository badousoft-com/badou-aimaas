package com.badou.project.util;

import com.alibaba.fastjson.JSONObject;
import com.badou.brms.dictionary.DictionaryLib;
import com.badou.project.common.webparams.util.JsonResultUtil;
import com.badou.project.kubernetes.util.StringHandlerUtil;
import com.badou.project.maas.MaasConst;
import com.badou.tools.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static javax.swing.JSplitPane.LEFT;
import static javax.swing.JSplitPane.RIGHT;

/**
 * @ClassName ParamInvalidUtil
 * @Description 参数不合法工具类-如果返回String/true/false代表不合法!
 * @date 2023/6/28 11:58
 * @Version 1.0
 */
@Slf4j
public class ParamInvalidUtil {

    /**
     * 创建当前时间的字符串
     * @return 返回当前时间的字符串 例子:20230203161932
     */
    public static String createNowNo(){
        SimpleDateFormat sdf = new SimpleDateFormat(  "yyyyMMddHHmmss");
        return sdf.format(new Date());
    }

    public static JSONObject buildModelAllName(JSONObject row){
        String quantizationMethod = row.getString("quantizationMethod");
        String quantizationBits = row.getString("quantizationBits");
        String modelParamsSize = row.getString("modelParamsSize");
        if (modelParamsSize == null){
            modelParamsSize = "";
        }
        String name = row.getString("name");
        String allName = name+"-"+modelParamsSize+"b";
        if (StringUtils.isNotBlank(quantizationMethod) && StringUtils.isNotBlank(quantizationBits)){
            allName+="-"+quantizationBits+"bit";
        }
        String pubVersion = row.getString("pubVersion");
        if (StringUtils.isNotBlank(pubVersion)){
            allName+=":"+pubVersion;
        }
        row.put("modelAllName",allName);
        return row;
    }

    public static String findChatglm3Out(String content){
        int resultAll = content.indexOf("pytorch_model.bin");
        String beforeLine = content.substring(0, resultAll);
        int currentLineIdx = beforeLine.lastIndexOf("\n");
        //例子:  [INFO|modeling_utils.py:2474] 2024-05-28 08:45:14,633 >> Model weights saved in /fine_tuning/output/tool_alpaca_pt-PTV2_0000001-1716885883540/pytorch_model.bin
        String[] split = beforeLine.substring(currentLineIdx, resultAll).split("/");
        if(split.length==4){
            return split[3];
        }
        return null;
    }

    //全部的大写字母的正则表达式
    private static final Pattern pattern = Pattern.compile(".*[A-Z].*");

    public static String replaceVarValue(String value, HashMap<String,String> paramsMap){
        for (String key : paramsMap.keySet()) {
            value = ParamInvalidUtil.replaceVarValue(value, key, paramsMap.get(key));
            if (!value.contains("$")) {
                break;
            }
        }
        return value;
    }

    /**
     * 原文:   replaceVarValue("你好${name},我的一天。","name","人").结果是你好人,我的一天。
     * 替换${}里面的东西
     * @param text 原文
     * @param key 指定${}是什么内容 如果指定了必须相等才替换
     * @param newValue 希望替换的内容
     * @return
     */
    public static String replaceVarValue(String text,String key,String newValue) {
        String pattern = "\\$\\{(.+?)\\}";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(text);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            // 取第一个group
            String keyV = m.group(1);
            //如果key不是空的 则要求${xxx} 这个xxx必须和key相等才替换
            if (StringUtils.isNotEmpty(keyV)  && StringUtils.isNotEmpty(key)){
                if (keyV.equals(key)){
                    m.appendReplacement(sb, newValue);
                }
            }else {
                m.appendReplacement(sb, newValue);
            }
        }
        m.appendTail(sb);
        return sb.toString();
    }

    /**
     * 检查路由路径
     * @param routePath 路由路径
     * @return
     */
    public static String checkRoutePath(String routePath){
        if(StringUtils.isEmpty(routePath)){
            return "内容为空";
        }
        //不允许包含中文
        if(StringHandlerUtil.isContainChinese(routePath)){
            return routePath+"不允许包含中文";
        }
        //不允许存在大写字母
        // 如果正则表达式匹配成功，说明输入的字段包含大写，程序执行结束
        if (pattern.matcher(routePath).matches()) {
            return routePath+"不允许存在大写";
        }
        //不允许存在特殊符号
        String[] worngStrs = {"'","+","=",";",":","!","@","#","$","%","^","&","*","(",")","{","}","|",",",".","/","<",">"};
        for(String worngStr:worngStrs){
            if(routePath.contains(worngStr)){
                return routePath+"包含不合法特殊字符!请使用-或_拼接";
            }
        }
        //第一个位置 必须是字母作为开头
        if(!StringHandlerUtil.containsAlphabetic(routePath)){
            return routePath+"首个内容必须为字母!";
        }
        return null;
    }

    /**
     * 检查路径
     * @param path 路径
     * @return 返回有内容的字符串代表存在问题 返回null代表正常
     */
    public static String checkFilePath(String path){
        if(StringUtils.isEmpty(path)){
            return "内容为空";
        }
        //不允许包含中文
        if(StringHandlerUtil.isContainChinese(path)){
            return path+"包含中文";
        }
        //不能包含特殊字符 注意: 可以包含/
        String[] worngStrs = {"_","'","+","=",";",":","!","@","#","$","%","^","&","*","(",")","{","}","|",",",".","\\","<",">"};
        for(String worngStr:worngStrs){
            if(path.contains(worngStr)){
                return path+"包含不合法特殊字符!";
            }
        }
        String lastStr = path.charAt(path.length()-1)+"";
        //最后一个下划线不允许存在
        if("/".equals(lastStr)){
            return path+"的最后一个字符不允许是/";
        }
        return null;
    }

    /**
     * 检查应用编码是否合法
     * @param appCode 应用编码
     * @return
     */
    public static String checkAppCode(String appCode){
        if(StringUtils.isEmpty(appCode)){
            return "内容为空";
        }
        //不允许包含中文
        if(StringHandlerUtil.isContainChinese(appCode)){
            return appCode+"不允许包含中文";
        }
        //不允许存在大写字母
        // 如果正则表达式匹配成功，说明输入的字段包含大写，程序执行结束
        if (pattern.matcher(appCode).matches()) {
            return appCode+"不允许存在大写";
        }
        //不允许存在特殊符号
        String[] worngStrs = {"_","'","+","=",";",":","!","@","#","$","%","^","&","*","(",")","{","}","|",",",".","/","<",">"};
        for(String worngStr:worngStrs){
            if(appCode.contains(worngStr)){
                return appCode+"包含不合法特殊字符!请使用-拼接";
            }
        }
        //第一个位置 必须是字母作为开头
       if(!StringHandlerUtil.containsAlphabetic(appCode)){
           return appCode+"首个内容必须为字母!";
       }
        return null;
    }

    /**
     * 仅限字符串包含字母和数字
     * @param value 检查的字符串
     * @return 如果仅包含字母和数字 返回true 否则返回false
     */
    public static boolean onlyNumAndAlphabet(String value){
        return value.matches("^[0-9a-zA-Z]+$");
    }

    /**
     * 字符串不包含字母和数字
     * @param value 检查的字符串
     * @return 如果不包含字母和数字 返回true 否则返回false
     */
    public static boolean notOnlyNumAndAlphabet(String value){
        return !value.matches("^[0-9a-zA-Z]+$");
    }


    /**
     * 检查模板的配置值是否不合法
     * @param value
     * @return
     */
    public static String checkConfigValue(String value){
        //默认合法
        //如果值是null不合法
        if(JsonResultUtil.isNull(value) || value.length()==0){
            return "值为空";
        }
        if(value.contains("\\")){
            return "值不允许包含\\";
        }
        //不允许是中文
        if(!(value.contains("${") && value.contains("}"))){
            if(StringHandlerUtil.isContainChinese(value)){
                return value+"不允许存在中文!";
            }
        }
        String lastStr = value.charAt(value.length()-1)+"";
        if("/".equals(lastStr)){
            return value+"的最后一个字符不允许是/";
        }
        return null;
    }

}
