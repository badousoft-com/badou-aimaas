package com.badou.project.maas.common.util;

import com.badou.brms.attach.AttachFactory;
import com.badou.brms.attach.model.Attach;
import com.badou.brms.util.InstanceFactory;
import com.badou.core.runtime.thread.local.LogonCertificate;
import com.badou.core.runtime.thread.local.LogonCertificateHolder;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class AttachUtil {
    /**
     * 通用附件创建方法
     * @param attachId 上传的附件对象
     * @param targetEntity  设置附件的实体
     * @param attachFile 实体中附件名称字段
     * @param fileId 附件ID字段
     * @param parentId 附件父ID字段
     * @param parentIdValue 附件父ID字段值
     * @throws Exception
     */
    public static void createAttach(MultipartFile attachId, Object targetEntity,
                                    String attachFile, String fileId,
                                    String parentId, String parentIdValue) throws Exception {
        //1.创建附件对象(Attach)
        Attach attach = buildAttach(attachId);
        //2.为目标实体动态设置字段
        Map<String, Object> fieldMap = new HashMap<>();
        fieldMap.put(attachFile, attachId.getOriginalFilename());
        fieldMap.put(fileId, attach.getId());
        fieldMap.put(parentId, parentIdValue);
        //3.反射调用setter方法
        for (Map.Entry<String, Object> entry : fieldMap.entrySet()) {
            setProperty(targetEntity, entry.getKey(), entry.getValue());
        }
    }

    /**
     * 通过反射动态设置对象属性
     * @param obj 对象
     * @param fieldName 字段名
     * @param value 字段值
     * @throws Exception
     */
    private static void setProperty(Object obj, String fieldName, Object value) throws Exception {
        String methodName = "set" + toUpperFirst(fieldName);
        Class<?> paramType = value.getClass();
        //处理原始类型和包装类型的映射
        if (paramType == Long.class) {
            paramType = long.class;
        } else if (paramType == Integer.class) {
            paramType = int.class;
        }
        Method method = obj.getClass().getMethod(methodName, paramType);
        method.invoke(obj, value);
    }

    //设置字段名首字母大写
    private static String toUpperFirst(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    //创建附件
    private static Attach buildAttach(MultipartFile attachId) throws Exception {
        Attach attach = new Attach();
        AttachFactory attachFactory = InstanceFactory.getInstance(AttachFactory.class);
        LogonCertificate logon = LogonCertificateHolder.getLogonCertificate();
        //通过反射调用setter 设置公共字段
        setProperty(attach,"name",attachId.getOriginalFilename());
        setProperty(attach,"extendName",attachId.getOriginalFilename().substring(attachId.getOriginalFilename().indexOf(".")));
        setProperty(attach,"genPersonId",logon.getUserId());
        setProperty(attach,"genPersonName",logon.getUserName());
        try{
            //long-->Long自动转换
            setProperty(attach,"fileSize",attachId.getSize());
            setProperty(attach,"fileContent",attachId.getBytes());
            attachFactory.uploadFile(attach);
        } catch (Exception var10) {
            throw new Exception(var10);
        }
        return attach;
    }

}
