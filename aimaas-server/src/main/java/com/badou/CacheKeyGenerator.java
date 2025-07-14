package com.badou;

import com.alibaba.fastjson.JSONObject;
import com.badou.brms.dboperation.query.QueryCriterion;
import com.badou.brms.dboperation.query.QueryOrderby;
import com.badou.brms.dboperation.query.QueryParam;
import com.badou.brms.dboperation.query.QueryParamGroup;
import com.badou.brms.system.BadouConfigurationLoader;
import com.badou.core.runtime.thread.local.LogonCertificateHolder;
import com.badou.designer.report.commsearch.model.CommSearchEntity;
import com.badou.project.GlobalConsts;
import com.badou.tools.common.Globals;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Optional;

/**
 * @author chenjiabao
 * 缓存标识生成类
 * @date 2021年05月31日
 */
@Configuration
public class CacheKeyGenerator extends CachingConfigurerSupport {


    /**
     * key生成器
     * @return bean
     */
    @Bean(name="CommonReportKeyGenerator")
    public KeyGenerator commonReportKeyGenerator() {
        return (target, method, params) -> {
            //如果不开启面板缓存，则直接返回随机数
            if(!BadouConfigurationLoader.PANEL_CACHE_ENABLED){
               return Math.random();
            }
            StringBuilder sb = new StringBuilder();
            CommSearchEntity report = (CommSearchEntity) params[GlobalConsts.ZERO];
            sb.append(report.getId());
            sb.append(Globals.SEPARATOR_UNDERLINE);
            QueryCriterion queryCriterion = (QueryCriterion) params[GlobalConsts.TWO];
            List<QueryParam> paramList = queryCriterion.getQueryParams();
            sb.append(getQueryParamString(paramList));
            List<QueryParamGroup> groupList = queryCriterion.getQueryParamsGroup();
            groupList.forEach(queryParamGroup -> {
                sb.append(getQueryParamString(queryParamGroup.getParams()));
            });
            List<QueryOrderby> orderbyList = queryCriterion.getQueryOrderbys();
            orderbyList.forEach(queryOrderby -> {
                sb.append(queryOrderby.getPropertyName()).append(queryOrderby.getAscOrdesc());
            });
            if (params.length > GlobalConsts.THREE) {
                JSONObject json = (JSONObject) params[GlobalConsts.THREE];
                Optional.ofNullable(json).ifPresent(jo -> {
                    sb.append(Globals.SEPARATOR_UNDERLINE);
                    jo.forEach((k, v) -> {
                        sb.append(k);
                        sb.append(v);
                    });
                });
            }
            if (null != LogonCertificateHolder.getLogonCertificate()) {
                sb.append(LogonCertificateHolder.getLogonCertificate().getUserId());
            }
            return sb.toString().hashCode();
        };
    }

    /**
     *
     * @param paramList 参数列表
     * @return String
     */
    private String getQueryParamString(List<QueryParam> paramList) {
        StringBuilder sb = new StringBuilder();
        if (paramList != null && !paramList.isEmpty()) {
            paramList.forEach(queryParam -> {
                sb.append(queryParam.getPropertyName()).append(queryParam.getOp());
                Optional.ofNullable(queryParam.getValue1()).ifPresent(sb::append);
                Optional.ofNullable(queryParam.getValue2()).ifPresent(sb::append);
            });
        }
        return sb.toString();
    }
}
