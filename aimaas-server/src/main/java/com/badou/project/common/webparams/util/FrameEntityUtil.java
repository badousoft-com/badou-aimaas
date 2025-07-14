package com.badou.project.common.webparams.util;

import com.badou.designer.jdbc.core.vo.BaseDetailVO;
import com.badou.designer.jdbc.core.vo.BaseVO;

import java.lang.reflect.Field;
import java.util.Map;

import static com.badou.project.common.webparams.util.ReflectTool.captureName;

/**
 * @ClassName FrameEntityUtil
 * @Description TODO
 * @date 2022/9/23 9:46
 * @Version 1.0
 */

public class FrameEntityUtil {

    public static Object convert(BaseVO baseVO,Class clazz) throws IllegalAccessException, InstantiationException {
        Object instance = clazz.newInstance();
        for (int i = 0; i < baseVO.getDetail().size(); i++) {
            BaseDetailVO e = baseVO.getDetail().get(i);
            try {
                Field field = clazz.getDeclaredField(e.getEntityName());
                ReflectTool.CallMethod(instance,"set"+captureName(e.getEntityName()),field.getType(),e.getFieldValue());
            } catch (NoSuchFieldException ex) {
                ex.printStackTrace();
                return instance;
            }
        }
        return instance;
    }

    public static Object convert(BaseVO baseVO, Class clazz, Map filterMap) throws IllegalAccessException, InstantiationException {
        Object instance = clazz.newInstance();
        for (int i = 0; i < baseVO.getDetail().size(); i++) {
            BaseDetailVO e = baseVO.getDetail().get(i);
            try {
                if(filterMap.containsKey(e.getEntityName())){
                    continue;
                }
                Field field = clazz.getDeclaredField(e.getEntityName());
                ReflectTool.CallMethod(instance,"set"+captureName(e.getEntityName()),field.getType(),e.getFieldValue());
            } catch (NoSuchFieldException ex) {
                ex.printStackTrace();
                return instance;
            }
        }
        return instance;
    }

}
