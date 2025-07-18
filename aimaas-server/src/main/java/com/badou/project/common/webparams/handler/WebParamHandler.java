package com.badou.project.common.webparams.handler;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.badou.project.common.webparams.anno.WebParamDec;
import com.badou.project.common.webparams.util.JsonResultUtil;
import com.badou.project.common.webparams.util.ReflectTool;
import com.badou.tools.common.util.StringUtils;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;

import javax.persistence.Column;
import javax.persistence.Transient;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * @CLassName WebParamNullHandler
 * @Decription 按需检查实体类 是否字段空值
 * @Author lm
 * @Version 1.0
 * @Date 2021/10/16
 */
public class WebParamHandler {

    //空值的字段描述
    private static Map<String,String> nullFieldDec = new HashMap();

    /**
     * 检查是否包含关键字 任意一个没有返回false
     * @param values 值
     * @param keyWord 关键词
     * @return 是否包含
     */
    public static boolean noneKeyWordValue(String[] values,String keyWord){
        for(String v:values){
            if(!v.contains(keyWord)){
                return true;
            }
        }
        return false;
    }

    public static String createLimit(int pageIndex,int pageSize){
        if(pageIndex == 0){
            return "limit 0," + pageSize;
        }else {
           return " limit "+(pageIndex-1)*pageSize+","+pageSize;
        }
    }

    /**
     * 根据类注解识别自动生成映射关系
     * @param sql 执行的查询sql
     * @param clazz 映射的类
     * @param session 操作的会话
     * @param appFlags 是否获取id+创建人 更新人. 0.代表不获取 1.代表获取
     * @param replaceStr 希望替换的内容
     * @return 处理好的数据
     */
    public static SQLQuery createAutoQuery(String sql, Class<?> clazz, Session session,int appFlags,String replaceKey,String replaceStr){
        if(!sql.contains(":fields")){
            return null;
        }
        Field[] declaredFields = clazz.getDeclaredFields();
        StringBuilder stringBuilder = new StringBuilder();
        int n = 0;
        for(Field field:declaredFields){
            Column annotation = field.getAnnotation(Column.class);
            if(annotation == null){
                continue;
            }
            stringBuilder.append(annotation.name()+" as "+field.getName());
            if(n < (declaredFields.length-1)){
                stringBuilder.append(",");
            }
            n++;
        }
        if(appFlags == 1){
            stringBuilder.append("id,flg_deleted as flgDeleted,update_time as updateTime,creator as creator");
            stringBuilder.append(",updator_name as updatorName,updator as updator,create_time as createTime,creator_name as creatorName,");
        }
        String fields = stringBuilder.toString();
        sql = sql.replace(":fields",fields.substring(0,fields.length()-1));
        sql = sql.replace("Id AS id,",replaceStr);
        SQLQuery sqlQuery = session.createSQLQuery(sql);
        sqlQuery.setResultTransformer(Transformers.aliasToBean(clazz));
        for(Field field:declaredFields){
            Column annotation = field.getAnnotation(Column.class);
            Transient trans = field.getAnnotation(Transient.class);
            if(annotation == null || trans !=null){
                continue;
            }
            sqlQuery.addScalar(field.getName());
        }
        //是否获取id+创建人 更新人
        if(appFlags == 1){
            sqlQuery.addScalar("id");
            sqlQuery.addScalar("flgDeleted");
            sqlQuery.addScalar("updateTime");
            sqlQuery.addScalar("updatorName");
            sqlQuery.addScalar("updator");
            sqlQuery.addScalar("createTime");
            sqlQuery.addScalar("creatorName");
            sqlQuery.addScalar("creator");
        }if(appFlags == 2){
            //2只生成id
            sqlQuery.addScalar("id");
        }if(appFlags == 3){
            //3.允许自定义id字段
            sqlQuery.addScalar("id");
            sqlQuery.addScalar("flgDeleted");
            sqlQuery.addScalar("updateTime");
            sqlQuery.addScalar("updatorName");
            sqlQuery.addScalar("updator");
            sqlQuery.addScalar("createTime");
            sqlQuery.addScalar("creatorName");
            sqlQuery.addScalar("creator");
        }
        return sqlQuery;
    }

    /**
     * 根据类注解识别自动生成映射关系
     * @param sql 执行的查询sql
     * @param clazz 映射的类
     * @param session 操作的会话
     * @param appFlags 是否获取id+创建人 更新人. 0.代表不获取 1.代表获取
     * @return 处理好的数据
     */
    public static SQLQuery createAutoQuery(String sql, Class<?> clazz, Session session,int appFlags,String tagName){
        if(!sql.contains(":fields")){
            return null;
        }
        Field[] declaredFields = clazz.getDeclaredFields();
        StringBuilder stringBuilder = new StringBuilder();
        int n = 0;
        for(Field field:declaredFields){
            Column annotation = field.getAnnotation(Column.class);
            if(annotation == null){
                continue;
            }
            String name = annotation.name();
            //给字段增加别名
            if (StringUtils.isNotBlank(tagName)){
                name = tagName+"."+name;
            }
            stringBuilder.append(name+" as "+field.getName());
            if(n < (declaredFields.length-1)){
                stringBuilder.append(",");
            }
            n++;
        }
        if(appFlags == 1){
            stringBuilder.append("id,flg_deleted as flgDeleted,update_time as updateTime,creator as creator");
            stringBuilder.append(",updator_name as updatorName,updator as updator,create_time as createTime,creator_name as creatorName,");
        }
        String fields = stringBuilder.toString();
        sql = sql.replace(":fields",fields.substring(0,fields.length()-1));
        SQLQuery sqlQuery = session.createSQLQuery(sql);
        sqlQuery.setResultTransformer(Transformers.aliasToBean(clazz));
        for(Field field:declaredFields){
            Column annotation = field.getAnnotation(Column.class);
            Transient trans = field.getAnnotation(Transient.class);
            if(annotation == null || trans !=null){
                continue;
            }
            sqlQuery.addScalar(field.getName());
        }
        //是否获取id+创建人 更新人
        if(appFlags == 1){
            sqlQuery.addScalar("id");
            sqlQuery.addScalar("flgDeleted");
            sqlQuery.addScalar("updateTime");
            sqlQuery.addScalar("updatorName");
            sqlQuery.addScalar("updator");
            sqlQuery.addScalar("createTime");
            sqlQuery.addScalar("creatorName");
            sqlQuery.addScalar("creator");
        }if(appFlags == 2){
            //2只生成id
            sqlQuery.addScalar("id");
        }if(appFlags == 3){
            //3.允许自定义id字段
            sqlQuery.addScalar("id");
            sqlQuery.addScalar("flgDeleted");
            sqlQuery.addScalar("updateTime");
            sqlQuery.addScalar("updatorName");
            sqlQuery.addScalar("updator");
            sqlQuery.addScalar("createTime");
            sqlQuery.addScalar("creatorName");
            sqlQuery.addScalar("creator");
        }
        return sqlQuery;
    }


    /**检查是否字段空值
     * 有字段空值则返回true,全都有值则返回false
     * 只检查带WebParamDec注解的
     * @param object 检查对象
     * @return 是否成功
     */
    public static boolean isHasNullAtPart(Object object){
        if(object == null){
            return true;
        }
        Class<?> clazz = object.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for(Field field:fields){
            //检查是否有不需要检查的字段
            if(field.getAnnotation(WebParamDec.class)==null){
                continue;
            }
            Object value = ReflectTool.CallMethod(object,"get"+captureName(field.getName()));
            if(JsonResultUtil.isNull(value)){
                pullNullFiled(object,field);
                return true;
            }
        }

        return false;
    }

    public static boolean isHasBlankStr(String... value){
        for (String s : value) {
            if(StringUtils.isEmpty(s)){
                return true;
            }
        }
        return false;
    }

    /**检查是否字段空值
     * 有字段空值则返回true,全都有值则返回false
     * 只检查没带WebParamDec注解的
     * @param object 检查对象
     * @return 是否成功
     */
    public static boolean isHasNull(Object object){
        if(object == null){
            return true;
        }
        Class<?> clazz = object.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for(Field field:fields){
            //检查是否有不需要检查的字段
            if(field.getAnnotation(WebParamDec.class)!=null){
                continue;
            }
            Object value = ReflectTool.CallMethod(object,"get"+captureName(field.getName()));
            if(JsonResultUtil.isNull(value)){
                pullNullFiled(object,field);
                return true;
            }
        }

        return false;
    }

    /**
     * 放入null字段的描述
     * @param object
     * @param field
     */
    private static void pullNullFiled(Object object, Field field){
        String name = null;
        String key = object.getClass().toString();
        ApiModelProperty apiModelProperty = field.getAnnotation(ApiModelProperty.class);
        if(apiModelProperty!=null){
            name =  apiModelProperty.value();
        }else{
            name = field.getName();
        }
        nullFieldDec.put(key,name);
    }

    /**
     * 获取Null字段信息
     * @param object
     * @return
     */
    public static String getFailFieldSource(Object object){
        return nullFieldDec.get(object.getClass().toString());
    }

    /**
     * 获取Null字段信息
     * @param object
     * @return
     */
    public static String getFailField(Object object){
        String value = nullFieldDec.get(object.getClass().toString());
        if(value!=null){
           return value+"不能为空";
        }
        return value;
    }

    /**
     * 将字符串的首字母转大写
     * @param str 需要转换的字符串
     * @return
     */
    private static String captureName(String str) {
        // 进行字母的ascii编码前移，效率要高于截取字符串进行转换的操作
        char[] cs=str.toCharArray();
        cs[0]-=32;
        return String.valueOf(cs);
    }

    public static Map<String, Object> getWebParamsByMap(String searchParam){
        Map<String, Object> params = new HashMap<>();
        JSONArray jsonArray = JSONArray.parseArray(searchParam);
        jsonArray.forEach(jsonObj ->{
            JSONObject jsonObject = (JSONObject)jsonObj;
            params.put(jsonObject.getString("name"),jsonObject.getString("value"));
        });
        return params;
    }
}
