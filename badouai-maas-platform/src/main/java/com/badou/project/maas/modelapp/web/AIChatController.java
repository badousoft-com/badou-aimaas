package com.badou.project.maas.modelapp.web;

import cn.hutool.json.JSONUtil;
import com.badou.project.exception.DataEmptyException;
import com.badou.project.maas.modelapp.model.ModelAppEntity;
import com.badou.project.maas.modelapp.service.IModelAppService;
import com.badou.tools.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
public class AIChatController {

    @Autowired
    private IModelAppService modelAppService;

    // 配置最大超时时间为1小时（3600秒）
    private static final long MAX_TIMEOUT_SECONDS = 3600;
    private static final long HEARTBEAT_INTERVAL_MS = 25000; // 25秒心跳间隔

    // 使用静态单例 OkHttpClient
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(MAX_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(MAX_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(MAX_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .connectionPool(new ConnectionPool(100, 5, TimeUnit.MINUTES))
            .retryOnConnectionFailure(true)
            .build();

    @PostMapping(path = "chatCompletion", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public DeferredResult<ResponseEntity<StreamingResponseBody>> chatCompletion(
            @org.springframework.web.bind.annotation.RequestBody Map<String, Object> requestBody) throws DataEmptyException {

        // 创建DeferredResult并设置1小时超时
        DeferredResult<ResponseEntity<StreamingResponseBody>> deferredResult =
                new DeferredResult();

        try {
            log.info("开始处理AI请求");

            // 1. 准备请求数据
            requestBody.put("stream", true);
//            requestBody.put("max_tokens",40000);
            //最大超时1小时 原本默认是30秒
            requestBody.put("timeout", 600000L);

            Object id = requestBody.get("id");
            if (id == null || StringUtils.isEmpty(id.toString())) {
                throw new DataEmptyException("未存在目标对象");
            }

            requestBody.remove("id");
            ModelAppEntity modelAppEntity = modelAppService.find(id.toString());
            log.info("使用模型: {}", modelAppEntity.getModelName());

            requestBody.put("model", modelAppEntity.getModelName());
            String json = JSONUtil.toJsonStr(requestBody);

            // 2. 构建请求
            RequestBody body = RequestBody.create(
                    okhttp3.MediaType.parse("application/json; charset=utf-8"),
                    json
            );

            String url = modelAppEntity.getTotalApiPath();
            log.info("请求AI服务: {}", url);

            Request request = new Request.Builder()
                    .url(url.contains("http") ? url : "http://" + url)
                    .post(body)
                    .addHeader("Accept", MediaType.TEXT_EVENT_STREAM_VALUE)
                    .build();

            // 3. 执行请求
            Response response = client.newCall(request).execute();

            if (!response.isSuccessful()) {
                String errorMsg = "AI服务响应错误: " + response.code() + " - " + response.message();
                log.error(errorMsg);
                throw new IOException(errorMsg);
            }

            ResponseBody responseBody = response.body();
            if (responseBody == null) {
                log.error("AI服务返回空响应");
                throw new IOException("AI服务返回空响应");
            }

            // 4. 创建流式响应
            StreamingResponseBody streamingResponseBody = new StreamingResponseBody() {
                @Override
                public void writeTo(java.io.OutputStream outputStream) throws IOException {
                    long startTime = System.currentTimeMillis();
                    long lastActivityTime = System.currentTimeMillis();

                    try (InputStream inputStream = responseBody.byteStream()) {
                        log.info("开始流式传输响应");

                        byte[] buffer = new byte[8192];
                        int bytesRead;

                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                            outputStream.flush();
                            lastActivityTime = System.currentTimeMillis();

                            // 记录已用时间
                            long elapsedSeconds = (System.currentTimeMillis() - startTime) / 1000;
                            if (elapsedSeconds % 60 == 0) {
//                                log.info("流式传输已持续 {} 分钟", elapsedSeconds / 60);
                            }

                            // 心跳保持（每25秒发送空行）
                            if (System.currentTimeMillis() - lastActivityTime > HEARTBEAT_INTERVAL_MS) {
                                log.debug("发送心跳保持连接");
                                outputStream.write("\n".getBytes());
                                outputStream.flush();
                                lastActivityTime = System.currentTimeMillis();
                            }
                        }

                        log.info("流式传输完成，总耗时: {} 秒", (System.currentTimeMillis() - startTime) / 1000);
                    } catch (IOException e) {
                        long elapsedSeconds = (System.currentTimeMillis() - startTime) / 1000;
                        log.error("流式传输中断，已运行 {} 秒", elapsedSeconds, e);
                        throw e;
                    } finally {
                        try {
                            response.close();
                            log.info("已关闭响应资源");
                        } catch (Exception e) {
                            log.warn("关闭响应时出错", e);
                        }
                    }
                }
            };

            // 设置成功结果
            deferredResult.setResult(ResponseEntity.ok()
                    .contentType(MediaType.TEXT_EVENT_STREAM)
                    .body(streamingResponseBody));

        } catch (IOException e) {
            log.error("AI服务通信失败", e);
            // 设置错误结果
            deferredResult.setErrorResult(
                    ResponseEntity.status(500)
                            .body("AI服务通信失败: " + e.getMessage())
            );
        }

        return deferredResult;
    }
}