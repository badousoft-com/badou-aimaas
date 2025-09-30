package com.badou.project.kubernetes.handler.impl;

import com.alibaba.fastjson.JSONObject;
import com.badou.project.kubernetes.client.KubernetesApiClient;
import com.badou.project.kubernetes.handler.KubernetesExecHandler;
import com.badou.project.kubernetes.vo.ProcessStatusVo;
import com.badou.project.maas.MaasConst;
import com.badou.tools.common.util.StringUtils;
import com.google.common.base.Preconditions;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.kubernetes.client.Exec;
import io.kubernetes.client.openapi.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName KubernetesExecHandlerImpl
 * @Description k8s命令处理实现类
 * @date 2024/2/19 14:20
 * @Version 1.0
 */
@Slf4j
@Component
public class KubernetesExecHandlerImpl implements KubernetesExecHandler {

    @Override
    public List<ProcessStatusVo> getPodProcessStatus(String podName, String namespace, KubernetesApiClient kubernetesApiClient,String filterStr) throws InterruptedException, ApiException, IOException {
        String[] defaultParams = new String[]{"ps", "-ef"};
//        if (StringUtils.isNotBlank(filterStr)){
            //ps -ef | grep -E 'vllm|python' | grep -v grep
            //如果包含过滤的条件
//            defaultParams = new String[]{"ps", "-ef","|","grep","-E",""+filterStr+"","|","grep","-v","grep"};
//        }
        JSONObject jsonObject = new KubernetesExecHandlerImpl().execCommandOnce(podName, namespace,kubernetesApiClient,defaultParams);
        if (jsonObject.getBoolean("success")) {
            return parseProcessInfo(jsonObject.getString("msg"),filterStr);
        }
        return null;
    }

    public static List<ProcessStatusVo> parseProcessInfo(String text,String filterStr) {
        List<ProcessStatusVo> processInfoList = new ArrayList<>();
        String[] lines = text.split("\n");
        // 跳过标题行
        for (int i = 1; i < lines.length; i++) {
            String line = lines[i];
            // 处理可能的多空格分隔
            String[] parts = line.trim().split("\\s+");
            String uid = parts[0];
            int pid = Integer.parseInt(parts[1]);
            int ppid = Integer.parseInt(parts[2]);
            int c = Integer.parseInt(parts[3]);
            String stime = parts[4];
            String tty = parts[5];
            String time = parts[6];

            // 处理命令部分，因为命令可能包含空格
            StringBuilder cmdBuilder = new StringBuilder();
            for (int j = 7; j < parts.length; j++) {
                cmdBuilder.append(parts[j]).append(" ");
            }
            String cmd = cmdBuilder.toString().trim();
            if (StringUtils.isNotBlank(filterStr)){
                String[] filters = filterStr.split(",");
                for (String filter : filters) {
                    if (cmd.contains(filter)){
                        for (int j = 7; j < parts.length; j++) {
                            cmdBuilder.append(parts[j]).append(" ");
                        }
                        ProcessStatusVo processInfo = new ProcessStatusVo(uid, pid, ppid, c, stime, tty, time, cmd);
                        processInfoList.add(processInfo);
                    }
                }
                continue;
            }
            ProcessStatusVo processInfo = new ProcessStatusVo(uid, pid, ppid, c, stime, tty, time, cmd);
            processInfoList.add(processInfo);
        }
        return processInfoList;
    }

    @Override
    public JSONObject execCommandOnce(String podName, String namespace, KubernetesApiClient kubernetesApiClient, String[] commands) throws IOException, ApiException, InterruptedException {
        String commandStrs = "";
        for (String commandStr : commands) {
            commandStrs += commandStr + "\t";
        }
        log.info("k8s命令执行:" + commandStrs);
        Exec exec = new Exec();
        // 命令类配置客户端 代表操作哪个服务器的服务
        exec.setApiClient(kubernetesApiClient.getApiClient());
        // 生成命令执行类与结果
//        String[] commands = new String[]{"redis-cli","-a","@8%M6uZ6@w8#@97","flushall"};
        final Process proc = exec.exec(namespace, podName, commands, true, true);
        // 获取执行结果
        String result = getResultByStream(proc.getInputStream());
        // 等待完成+关闭并生成结果返回
        proc.waitFor();
        proc.destroy();
        JSONObject resultJSON = new JSONObject();
        resultJSON.put("msg", result);
        if (proc.exitValue() != 0) {
            resultJSON.put("success", false);
        } else {
            resultJSON.put("success", true);
        }
        return resultJSON;
    }

    //kubernetesExecHandler.execCommandOnce(v1PodList.getItems().get(0).getMetadata().getName(),nameSpace,getDefaultClient(),
    // new String[]{"/home/servers/anaconda3/bin/wget",this.oauthUrl+"/attach/action/attachsupport/downloadAttach.do?attachId="+attachId,"-O",})

    /**
     * 根据in输入流获取字符内容
     *
     * @param from in输入流
     * @return in输入流包含的字符串内容
     */
    private String getResultByStream(InputStream from) {
        Preconditions.checkNotNull(from);
        BufferedReader in = null;
        Reader reader = null;
        StringBuilder sb = new StringBuilder();
        try {
            reader = new InputStreamReader(from);
            in = new BufferedReader(reader);
            String line;
            while ((line = in.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info(e.getMessage());
        } finally {
            try {
                if (from != null) {
                    from.close();
                }
                if (reader != null) {
                    reader.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
                log.info(e.getMessage());
            }
        }
        final String result = sb.toString();
        log.info("执行命令结果:" + result);
        return result;
    }

}
