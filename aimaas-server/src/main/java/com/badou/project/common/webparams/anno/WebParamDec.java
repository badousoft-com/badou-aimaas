package com.badou.project.common.webparams.anno;


import io.swagger.annotations.ApiModelProperty;

import java.lang.annotation.*;

/**
 * @CLassName WebParamDec
 * @Decription web层入参实体描述注解
 * @Author lm
 * @Version 1.0
 * @Date 2021/07/14
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface WebParamDec {

    @ApiModelProperty("是否检查空值,默认检查,提供false则不检查")
    boolean isCheck();

}
