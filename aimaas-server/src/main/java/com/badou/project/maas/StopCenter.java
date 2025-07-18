package com.badou.project.maas;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class StopCenter {

    private static final Map<String,Object> stopList = new HashMap<>();

    public static void startStop(String key,String desc){
        stopList.put(key,"");
        log.info("StopCenter收到停止任务![key:"+key+",desc:"+desc+"]");
    }

    public static boolean checkStopFlag(String key,String desc){
        if(stopList.get(key)!=null){
            log.info("StopCenter返回停止状态!要求收到的任务马上停止![key:"+key+",desc:"+desc+"]");
            return true;
        }
        return false;
    }

    public static boolean checkStopFlagQuiet(String key,String desc){
        if(stopList.get(key)!=null){
            return true;
        }
        return false;
    }

}
