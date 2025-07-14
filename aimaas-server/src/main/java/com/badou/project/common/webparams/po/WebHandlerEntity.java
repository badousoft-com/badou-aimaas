package com.badou.project.common.webparams.po;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.badou.project.common.webparams.GlobalConstans;
import io.swagger.annotations.ApiModelProperty;

/**
 * @ClassName WebHandlerEntity
 * @Description TODO
 * @date 2021/10/29 15:50
 * @Version 1.0
 */
public class WebHandlerEntity {

    @ApiModelProperty("页码")
    private Integer pageNo;
    @ApiModelProperty("页数")
    private Integer perPageSize;
    @ApiModelProperty("搜索参数集合-字符串")
    private String searchParam;
    @ApiModelProperty("参数集合")
    private JSONObject params;
    @ApiModelProperty("精准匹配集合")
    private JSONObject exactMatch;
    @ApiModelProperty("模糊匹配集合")
    private JSONObject textQuery;
    @ApiModelProperty("通用集合")
    private JSONObject commonQuery;
    @ApiModelProperty("时间匹配集合")
    private JSONObject dateType;

    public WebHandlerEntity(Integer pageNo, Integer perPageSize, String searchParam) {
        this.pageNo = pageNo;
        this.perPageSize = perPageSize;
        params = new JSONObject();
        exactMatch = new JSONObject();
        textQuery = new JSONObject();
        dateType = new JSONObject();
        commonQuery = new JSONObject();
        JSONArray jsonArray = JSONArray.parseArray(searchParam);
        for(int i=0;i<jsonArray.size();i++){
            JSONObject object = jsonArray.getJSONObject(i);
            String name = object.getString("name");
            String type = object.getString("type");
            String value = String.valueOf(object.get("value"));
            switch (type){
                case GlobalConstans.EXACTMATCH:
                    exactMatch.put(name,value);
                    commonQuery.put(name,value);
                    break;
                case GlobalConstans.TEXTQUERY:
                    textQuery.put(name,value);
                    commonQuery.put(name,value);
                    break;
                case GlobalConstans.DATEQUERY:
                    JSONObject jsonObject = JSONObject.parseObject(object.getString("value"));
                    TimeObject timeObject = new TimeObject();
                    timeObject.setDateType(jsonObject.getString("dateType"));
                    timeObject.setStartTime(jsonObject.getString("startTime"));
                    timeObject.setEndTime(jsonObject.getString("endTime"));
                    timeObject.setName(name);
                    dateType.put(name,timeObject);
                    break;
                default:
                    break;
            }
        }
        this.searchParam = searchParam;
    }

    public int getInt(String key){
        return commonQuery.getIntValue(key);
    }

    public String getString(String key){
        return String.valueOf(commonQuery.get(key));
    }

    public String getStartTime(String key){
        TimeObject timeObject = (TimeObject) dateType.get(key);
        return timeObject.getStartTime();
    }

    public String getEndTime(String key){
        TimeObject timeObject = (TimeObject) dateType.get(key);
        return timeObject.getEndTime();
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getPerPageSize() {
        return perPageSize;
    }

    public void setPerPageSize(Integer perPageSize) {
        this.perPageSize = perPageSize;
    }

    public String getSearchParam() {
        return searchParam;
    }

    public void setSearchParam(String searchParam) {
        this.searchParam = searchParam;
    }

}
