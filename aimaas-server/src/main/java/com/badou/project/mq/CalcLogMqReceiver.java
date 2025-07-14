package com.badou.project.mq;

import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.badou.brms.dboperation.query.QueryCriterion;
import com.badou.brms.dboperation.query.QueryOperSymbolEnum;
import com.badou.brms.dboperation.query.support.QueryHibernatePlaceholderParam;
import com.badou.project.common.webparams.util.JsonResultUtil;
import com.badou.project.kubernetes.client.KubernetesApiClient;
import com.badou.project.kubernetes.handler.KubernetesPodHandler;
import com.badou.project.maas.MaasConst;
import com.badou.project.maas.common.FileControllerService;
import com.badou.project.maas.tuningmodeln.model.TuningModelnEntity;
import com.badou.project.maas.tuningmodeln.service.ITuningModelnService;
import com.badou.tools.common.util.StringUtils;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.record.DVALRecord;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Component;

import java.io.File;
import java.security.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Component
@Slf4j
/**
 * 计算微调的日志情况 并得出分析结果
 * 比如日志花费时间 xxx 丢失率xxx
 */
public class CalcLogMqReceiver {

    @Autowired
    private KubernetesPodHandler kubernetesPodHandler;
    @Autowired
    private ITuningModelnService tuningModelnService;

    public Map<String, List> getStatus(String id) throws ParseException {
        TuningModelnEntity tuningModelnEntity = tuningModelnService.find(id);
        if (JsonResultUtil.isNull(tuningModelnEntity)) {
            return null;
        }
        String oldMsg = tuningModelnEntity.getPlanMsg();
        try {
            KubernetesApiClient defaultClient = FileControllerService.getCustomClient(tuningModelnService.getServerId(tuningModelnEntity));
            TuningModelnEntity podLogsUnUpdate = tuningModelnService.getPodLogsUnUpdate(defaultClient, tuningModelnEntity.getWorkSpace(), tuningModelnEntity.getCode(), tuningModelnEntity, true);
            String main = podLogsUnUpdate.getPlanMsg();
            if (tuningModelnEntity.getDoStatus() == MaasConst.DOPLAN_SCORE_STATUS){
                main = oldMsg;
            }
            if (StringUtils.isEmpty(main)) {
                return null;
            }
//            main = "2024/06/18 01:51:59 - mmengine - WARNING - \"FileClient\" will be deprecated in future. Please use io functions in https://mmengine.readthedocs.io/en/latest/api/fileio.html#file-io\n" +
//                "2024/06/18 01:51:59 - mmengine - WARNING - \"HardDiskBackend\" is the alias of \"LocalBackend\" and the former will be deprecated in future.\n" +
//                "2024/06/18 01:51:59 - mmengine - INFO - Checkpoints will be saved to /fine_tuning/work_dir/qianwen-8b-qlora_pth.\n" +
//                "2024/06/18 01:52:44 - mmengine - INFO - Iter(train) [ 10/858]  lr: 7.5001e-05  eta: 1:03:57  time: 4.5256  data_time: 0.0138  memory: 11164  loss: 3.1591\n" +
//                "2024/06/18 01:53:29 - mmengine - INFO - Iter(train) [ 20/858]  lr: 1.5833e-04  eta: 1:03:18  time: 4.5402  data_time: 0.0129  memory: 11469  loss: 2.9765  grad_norm: 1.8471\n" +
//                "2024/06/18 01:54:15 - mmengine - INFO - Iter(train) [ 30/858]  lr: 1.9999e-04  eta: 1:02:32  time: 4.5289  data_time: 0.0123  memory: 11469  loss: 2.7564  grad_norm: 1.8471\n" +
//                "2024/06/18 01:55:03 - mmengine - INFO - Iter(train) [ 40/858]  lr: 1.9986e-04  eta: 1:02:43  time: 4.8072  data_time: 0.0126  memory: 11469  loss: 2.1901  grad_norm: 2.1338\n" +
//                "2024/06/18 01:55:52 - mmengine - INFO - Iter(train) [ 50/858]  lr: 1.9959e-04  eta: 1:02:49  time: 4.9257  data_time: 0.0127  memory: 11469  loss: 2.1685  grad_norm: 1.7943\n" +
//                "2024/06/18 01:56:42 - mmengine - INFO - Iter(train) [ 60/858]  lr: 1.9918e-04  eta: 1:02:48  time: 5.0053  data_time: 0.0125  memory: 11469  loss: 2.0711  grad_norm: 1.7943\n" +
//                "2024/06/18 01:57:32 - mmengine - INFO - Iter(train) [ 70/858]  lr: 1.9863e-04  eta: 1:02:35  time: 5.0286  data_time: 0.0126  memory: 11469  loss: 1.8334  grad_norm: 1.5060\n" +
//                "2024/06/18 01:58:23 - mmengine - INFO - Iter(train) [ 80/858]  lr: 1.9793e-04  eta: 1:02:14  time: 5.0430  data_time: 0.0127  memory: 11469  loss: 1.8239  grad_norm: 1.3360\n" +
//                "2024/06/18 01:59:13 - mmengine - INFO - Iter(train) [ 90/858]  lr: 1.9710e-04  eta: 1:01:48  time: 5.0530  data_time: 0.0120  memory: 11469  loss: 1.7486  grad_norm: 1.3360\n" +
//                "2024/06/18 02:00:04 - mmengine - INFO - Iter(train) [100/858]  lr: 1.9613e-04  eta: 1:01:16  time: 5.0450  data_time: 0.0124  memory: 11469  loss: 1.8148  grad_norm: 1.2235\n" +
//                "2024/06/18 02:00:54 - mmengine - INFO - Iter(train) [110/858]  lr: 1.9502e-04  eta: 1:00:42  time: 5.0695  data_time: 0.0127  memory: 11469  loss: 1.7357  grad_norm: 1.2235\n" +
//                "2024/06/18 02:01:45 - mmengine - INFO - Iter(train) [120/858]  lr: 1.9378e-04  eta: 1:00:05  time: 5.0533  data_time: 0.0124  memory: 11469  loss: 1.6518  grad_norm: 1.1374\n" +
//                "2024/06/18 02:02:36 - mmengine - INFO - Iter(train) [130/858]  lr: 1.9241e-04  eta: 0:59:30  time: 5.1281  data_time: 0.0122  memory: 11469  loss: 1.6946  grad_norm: 1.0672\n" +
//                "2024/06/18 02:03:27 - mmengine - INFO - Iter(train) [140/858]  lr: 1.9090e-04  eta: 0:58:48  time: 5.0505  data_time: 0.0121  memory: 11469  loss: 1.6262  grad_norm: 1.0672\n" +
//                "2024/06/18 02:04:17 - mmengine - INFO - Iter(train) [150/858]  lr: 1.8926e-04  eta: 0:58:05  time: 5.0453  data_time: 0.0125  memory: 11469  loss: 1.6219  grad_norm: 1.0048\n" +
//                "2024/06/18 02:05:08 - mmengine - INFO - Iter(train) [160/858]  lr: 1.8750e-04  eta: 0:57:25  time: 5.1265  data_time: 0.0124  memory: 11469  loss: 1.5520  grad_norm: 0.9474\n" +
//                "2024/06/18 02:05:59 - mmengine - INFO - Iter(train) [170/858]  lr: 1.8561e-04  eta: 0:56:41  time: 5.0651  data_time: 0.0125  memory: 11469  loss: 1.4264  grad_norm: 0.9474\n" +
//                "2024/06/18 02:06:50 - mmengine - INFO - Iter(train) [180/858]  lr: 1.8360e-04  eta: 0:55:57  time: 5.0891  data_time: 0.0122  memory: 11469  loss: 1.4453  grad_norm: 0.8110\n" +
//                "2024/06/18 02:07:41 - mmengine - INFO - Iter(train) [190/858]  lr: 1.8147e-04  eta: 0:55:11  time: 5.0720  data_time: 0.0126  memory: 11469  loss: 1.7287  grad_norm: 0.8110\n" +
//                "2024/06/18 02:08:32 - mmengine - INFO - Iter(train) [200/858]  lr: 1.7923e-04  eta: 0:54:26  time: 5.0848  data_time: 0.0126  memory: 11469  loss: 1.1579  grad_norm: 0.6223\n" +
//                "2024/06/18 02:09:22 - mmengine - INFO - Iter(train) [210/858]  lr: 1.7687e-04  eta: 0:53:40  time: 5.0855  data_time: 0.0124  memory: 11469  loss: 1.5693  grad_norm: 0.5561\n" +
//                "2024/06/18 02:10:13 - mmengine - INFO - Iter(train) [220/858]  lr: 1.7441e-04  eta: 0:52:53  time: 5.0756  data_time: 0.0123  memory: 11469  loss: 1.4590  grad_norm: 0.5561\n" +
//                "2024/06/18 02:11:04 - mmengine - INFO - Iter(train) [230/858]  lr: 1.7183e-04  eta: 0:52:07  time: 5.0837  data_time: 0.0124  memory: 11469  loss: 1.3930  grad_norm: 0.5353\n" +
//                "2024/06/18 02:11:55 - mmengine - INFO - Iter(train) [240/858]  lr: 1.6916e-04  eta: 0:51:20  time: 5.1036  data_time: 0.0123  memory: 11469  loss: 1.3258  grad_norm: 0.5165\n" +
//                "2024/06/18 02:12:46 - mmengine - INFO - Iter(train) [250/858]  lr: 1.6639e-04  eta: 0:50:33  time: 5.1103  data_time: 0.0126  memory: 11469  loss: 1.2393  grad_norm: 0.5165\n" +
//                "2024/06/18 02:13:37 - mmengine - INFO - Iter(train) [260/858]  lr: 1.6352e-04  eta: 0:49:45  time: 5.0507  data_time: 0.0133  memory: 11469  loss: 1.5600  grad_norm: 0.5010\n" +
//                "2024/06/18 02:14:28 - mmengine - INFO - Iter(train) [270/858]  lr: 1.6056e-04  eta: 0:48:58  time: 5.1211  data_time: 0.0121  memory: 11469  loss: 1.1753  grad_norm: 0.5010\n" +
//                "2024/06/18 02:14:28 - mmengine - INFO - Iter(train) [270/858]  lr: 1.6056e-04  eta: 0:48:58  time: 5.1211  data_time: 0.0121  memory: 11469  loss: 99.1753  grad_norm: 0.5010\n" +
//                "2024/06/18 02:15:18 -|mmengine - INFO - Iter(train) [280/858]  lr: 1.5752e-04  eta: 0:48:09  time: 5.0489  data_time: 0.0128  memory: 11469  loss: 1.3503  grad_norm: "+new Random().nextInt(99)+"\n";
//            main = "1%|          | 1/100 [00:01<02:40,  1.62s/it]\n" +
////                    "  2%|▏         | 2/100 [00:01<01:17,  1.26it/s]\n" +
////                    "                                               \n" +
////                    "{'loss': 0.0, 'grad_norm': 0.0, 'learning_rate': 0.0196, 'epoch': 0.0}\n" +
////                    "\n" +
////                    "  2%|▏         | 2/100 [00:01<01:17,  1.26it/s]\n" +
////                    "  3%|▎         | 3/100 [00:02<00:53,  1.83it/s]\n" +
////                    "                                               \n" +
////                    "{'loss': 0.0, 'grad_norm': 0.0, 'learning_rate': 0.0194, 'epoch': 0.0}\n" +
////                    "\n" +
////                    "  3%|▎         | 3/100 [00:02<00:53,  1.83it/s]\n" +
////                    "  4%|▍         | 4/100 [00:02<00:41,  2.30it/s]\n" +
////                    "                                               \n" +
////                    "{'loss': 0.0, 'grad_norm': 0.0, 'learning_rate': 0.0192, 'epoch': 0.0}\n" +
////                    "\n" +
////                    "  4%|▍         | 4/100 [00:02<00:41,  2.30it/s]\n" +
////                    "  5%|▌         | 5/100 [00:02<00:34,  2.73it/s]\n" +
////                    "                                               \n" +
////                    "{'loss': 0.0, 'grad_norm': 0.0, 'learning_rate': 0.019, 'epoch': 0.0}\n" +
////                    "\n" +
////                    "  5%|▌         | 5/100 [00:02<00:34,  2.73it/s]\n" +
////                    "  6%|▌         | 6/100 [00:02<00:30,  3.09it/s]\n" +
////                    "                                               \n" +
////                    "{'loss': 0.0, 'grad_norm': 0.0, 'learning_rate': 0.0188, 'epoch': 0.0}\n" +
////                    "\n" +
////                    "  6%|▌         | 6/100 [00:02<00:30,  3.09it/s]\n" +
////                    "  7%|▋         | 7/100 [00:03<00:27,  3.39it/s]\n" +
////                    "                                               \n" +
////                    "{'loss': 0.0, 'grad_norm': 0.0, 'learning_rate': 0.018600000000000002, 'epoch': 0.01}\n" +
////                    "\n" +
////                    "  7%|▋         | 7/100 [00:03<00:27,  3.39it/s]\n" +
////                    "  8%|▊         | 8/100 [00:03<00:26,  3.52it/s]\n" +
////                    "                                               \n" +
////                    "{'loss': 0.0, 'grad_norm': 0.0, 'learning_rate': 0.0184, 'epoch': 0.01}\n" +
////                    "\n" +
////                    "  8%|▊         | 8/100 [00:03<00:26,  3.52it/s]\n" +
////                    "  9%|▉         | 9/100 [00:03<00:24,  3.65it/s]\n" +
////                    "                                               \n" +
////                    "{'loss': 0.0, 'grad_norm': 0.0, 'learning_rate': 0.0182, 'epoch': 0.01}\n" +
////                    "\n" +
////                    "  9%|▉         | 9/100 [00:03<00:24,  3.65it/s]\n" +
////                    " 10%|█         | 10/100 [00:03<00:24,  3.66it/s]\n" +
////                    "                                                \n" +
////                    "{'loss': 0.0, 'grad_norm': 0.0, 'learning_rate': 0.018000000000000002, 'epoch': 0.01}\n" +
////                    "\n" +
////                    " 10%|█         | 10/100 [00:03<00:24,  3.66it/s]\n" +
////                    " 11%|█         | 11/100 [00:04<00:24,  3.60it/s]\n" +
////                    "                                                \n" +
////                    "{'loss': 0.0, 'grad_norm': 0.0, 'learning_rate': 0.0178, 'epoch': 0.01}\n" +
////                    "\n" +
////                    " 11%|█         | 11/100 [00:04<00:24,  3.60it/s]\n" +
////                    " 12%|█▏        | 12/100 [00:04<00:24,  3.61it/s]\n" +
////                    "                                                \n" +
////                    "{'loss': 0.0, 'grad_norm': 0.0, 'learning_rate': 0.0176, 'epoch': 0.01}\n" +
////                    "\n" +
////                    " 12%|█▏        | 12/100 [00:04<00:24,  3.61it/s]\n" +
////                    " 13%|█▎        | 13/100 [00:04<00:23,  3.66it/s]\n" +
////                    "                                                \n" +
////                    "{'loss': 0.0, 'grad_norm': 0.0, 'learning_rate': 0.0174, 'epoch': 0.01}\n" +
////                    "\n" +
////                    " 13%|█▎        | 13/100 [00:04<00:23,  3.66it/s]\n" +
////                    " 14%|█▍        | 14/100 [00:04<00:22,  3.74it/s]\n" +
////                    "                                                \n" +
////                    "{'loss': 0.0, 'grad_norm': 0.0, 'learning_rate': 0.0172, 'epoch': 0.01}\n" +
////                    "\n" +
////                    " 14%|█▍        | 14/100 [00:04<00:22,  3.74it/s]\n" +
////                    " 15%|█▌        | 15/100 [00:05<00:22,  3.77it/s]\n" +
////                    "                                                \n" +
////                    "{'loss': 0.0, 'grad_norm': 0.0, 'learning_rate': 0.017, 'epoch': 0.01}\n" +
////                    "\n" +
////                    " 15%|█▌        | 15/100 [00:05<00:22,  3.77it/s]\n" +
////                    " 16%|█▌        | 16/100 [00:05<00:22,  3.78it/s]\n" +
////                    "                                                \n" +
////                    "{'loss': 0.0, 'grad_norm': 0.0, 'learning_rate': 0.0168, 'epoch': 0.01}\n" +
////                    "\n" +
////                    " 16%|█▌        | 16/100 [00:05<00:22,  3.78it/s]\n" +
////                    " 17%|█▋        | 17/100 [00:05<00:21,  3.81it/s]\n" +
////                    "                                                \n" +
////                    "{'loss': 0.0, 'grad_norm': 0.0, 'learning_rate': 0.0166, 'epoch': 0.01}\n" +
////                    "\n" +
////                    " 17%|█▋        | 17/100 [00:05<00:21,  3.81it/s]\n" +
////                    " 18%|█▊        | 18/100 [00:05<00:21,  3.80it/s]\n" +
////                    "                                                \n" +
////                    "{'loss': 0.0, 'grad_norm': 0.0, 'learning_rate': 0.016399999999999998, 'epoch': 0.01}\n" +
////                    "\n" +
////                    " 18%|█▊        | 18/100 [00:05<00:21,  3.80it/s]\n" +
////                    " 19%|█▉        | 19/100 [00:06<00:21,  3.80it/s]\n" +
////                    "                                                \n" +
////                    "{'loss': 0.0, 'grad_norm': 0.0, 'learning_rate': 0.016200000000000003, 'epoch': 0.02}\n" +
////                    "\n" +
////                    " 19%|█▉        | 19/100 [00:06<00:21,  3.80it/s]\n" +
////                    " 20%|██        | 20/100 [00:06<00:21,  3.80it/s]\n" +
////                    "                                                \n" +
////                    "{'loss': 0.0, 'grad_norm': 0.0, 'learning_rate': 0.016, 'epoch': 0.02}\n" +
////                    "\n" +
////                    " 20%|██        | 20/100 [00:06<00:21,  3.80it/s]\n" +
////                    " 21%|██        | 21/100 [00:06<00:21,  3.68it/s]\n" +
////                    "                                                \n" +
////                    "{'loss': 0.0, 'grad_norm': 0.0, 'learning_rate': 0.";
            String searchKey = "Iter(train)";
            int train = main.indexOf(searchKey);
            Map<String, List> values = new HashMap<>();
            values.put("lrs_time", new ArrayList<>());
            values.put("times_time", new ArrayList<>());
            values.put("grad_norms_time", new ArrayList<>());
            values.put("losss_time", new ArrayList<>());
            values.put("memorys_time", new ArrayList<>());
            values.put("data_times_time", new ArrayList<>());
            values.put("etas_time", new ArrayList<>());
            values.put("epoch_time", new ArrayList<>());

            values.put("lrs_data", new ArrayList<>());
            values.put("times_data", new ArrayList<>());
            values.put("grad_norms_data", new ArrayList<>());
            values.put("losss_data", new ArrayList<>());
            values.put("memorys_data", new ArrayList<>());
            values.put("data_times_data", new ArrayList<>());          //查询结束
            values.put("etas_data", new ArrayList<>());          //查询结束
            values.put("epoch_data", new ArrayList<>());          //查询结束

            //如果是llamafacory框架 则按llamafactory框架的格式处理
            /**
             * llamafactory框架格式
             * {'loss': 2.5501, 'grad_norm': 2.4182686805725098, 'learning_rate': 4.894973780788722e-05, 'epoch': 0.27}
             * {'loss': 2.026, 'grad_norm': 1.6082991361618042, 'learning_rate': 4.588719528532342e-05, 'epoch': 0.54}
             * {'loss': 1.6525, 'grad_norm': 0.8220518231391907, 'learning_rate': 4.1069690242163484e-05, 'epoch': 0.81}
             * {'loss': 1.462, 'grad_norm': 0.5667403936386108, 'learning_rate': 3.490199415097892e-05, 'epoch': 1.07}
             * {'loss': 1.5507, 'grad_norm': 0.5217065215110779, 'learning_rate': 2.7902322853130757e-05, 'epoch': 1.34}
             * {'loss': 1.4319, 'grad_norm': 0.5096935033798218, 'learning_rate': 2.0658795558326743e-05, 'epoch': 1.61}
             * {'loss': 1.2786, 'grad_norm': 0.52418053150177, 'learning_rate': 1.3780020494988446e-05, 'epoch': 1.88}
             * {'loss': 1.2521, 'grad_norm': 0.5887366533279419, 'learning_rate': 7.843959053281663e-06, 'epoch': 2.15}
             * {'loss': 1.3594, 'grad_norm': 0.4510204493999481, 'learning_rate': 3.3493649053890326e-06, 'epoch': 2.42}
             * {'loss': 1.1371, 'grad_norm': 0.4880461096763611, 'learning_rate': 6.738782355044049e-07, 'epoch': 2.68}
             */
            if (tuningModelnEntity.getDoFrame() == 0) {
                // 定义提取JSON的正则表达式模式
                Pattern jsonPattern = Pattern.compile("\\{(?=.*\\'loss')([^}]*)\\}");
                // 创建匹配器对象
                Matcher matcher = jsonPattern.matcher(main);
                while (matcher.find()) {
                    JSONObject obj = JSONObject.parseObject(matcher.group(0).replace("nan", "0"));
                    values.get("losss_data").add(obj.getString("loss"));
                    values.get("epoch_data").add(obj.getString("epoch"));
                    values.get("grad_norms_data").add(obj.getString("grad_norm"));
                    values.get("lrs_data").add(obj.getString("learning_rate"));
                }
                //如果找不到
                if (values.get("losss_data").size()==0){
                    int trainMetrics = main.indexOf("train metrics");
                    String[] newBody = main.substring(trainMetrics+"train metrics".length()).split("\n");
                    if (newBody.length>0)
                    for (int i = 1; i < newBody.length; i++) {
                        String line = newBody[i];
                        /**
                         *    epoch                    =        3.0
                         *   total_flos               =        0GF
                         *   train_loss               =     0.1039
                         *   train_runtime            = 0:00:03.50
                         *   train_samples_per_second =      0.856
                         *   train_steps_per_second   =      0.856
                         */
                        if (line.contains("=")){
                            String[] split = line.split("=");
                            if (split.length == 2){
                                String fieldName = split[0].trim();
                                String fieldValue = split[1].trim();
                                if ("train_loss".equals(fieldName)){
                                    values.get("losss_data").add(fieldValue);
                                }else if ("train_loss".equals(fieldName)){
                                    values.get("epoch_data").add(fieldValue);
                                }
                            }
                        }else {
                            break;
                        }
                    }
                }
                return values;
            }

            /**
             *  "2024/06/18 02:15:18 -|mmengine - INFO - Iter(train) [280/858]  lr: 1.5752e-04  eta: 0:48:09  time: 5.0489  data_time: 0.0128  memory: 11469  loss: 1.3503  grad_norm: "+new Random().nextInt(99)+"\n";
             *             main = "1%|          | 1/100 [00:01<02:40,  1.62s/it]\n" +
             *                     "  2%|▏         | 2/100 [00:01<01:17,  1.26it/s]\n" +
             *                     "                                               \n" +
             *                     "{'loss': 0.0, 'grad_norm': 0.0, 'learning_rate': 0.0196, 'epoch': 0.0}\n" +
             */
            if (tuningModelnEntity.getModelName().contains("chatglm")) {
                searchKey = "{'loss':";
                int startIdx = main.indexOf("{'loss':");
                while (startIdx != -1) {
                    int endIdx = main.indexOf("}", startIdx);

                    String line = "";
                    try {
                        line = main.substring(startIdx + 1, endIdx).substring(1).replace("'", "");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
//                    System.out.println("line:"+line.substring(1).replace("'",""));
                    main = main.substring(endIdx + 1);
                    startIdx = main.indexOf(searchKey);
                    String[] mainFields = line.split(",");
                    for (String fields : mainFields) {
                        String[] fieldSplit = fields.trim().split(":");
                        if (fieldSplit.length == 2) {
                            String field = fieldSplit[0];
                            String v = fieldSplit[1];
                            if ("learning_rate".equals(field)) {
                                values.get("lrs_data").add(v);
//                                values.get("lrs_time").add(new Date());
                            } else if ("loss".equals(field)) {
//                                values.get("losss_time").add(new Date());
                                values.get("losss_data").add(v);
                            } else if ("epoch".equals(field)) {
//                                values.get("epoch_time").add(new Date());
                                values.get("epoch_data").add(v);
                            } else if ("grad_norm".equals(field)) {
                                values.get("grad_norms_data").add(v);
//                                values.get("grad_norms_time").add(new Date());

                            }
                        }
                    }
                }
                return values;
            }
            while (train != -1) {
                String currentHandlerTime = main.substring(main.substring(0, train).lastIndexOf("\n"), train).split("-")[0].replace("\n", "");
                String length = main.substring(main.indexOf(searchKey) + searchKey.length());
                int endTag = length.indexOf("\n");
                if (endTag == -1) {

                }
                String line = main.substring(train, (train + (endTag + searchKey.length())));
//                System.out.println(line);
                if (line.contains("eta") || line.contains("grad_norm")) {
                    String[] mainSplit = line.substring(line.indexOf("]") + 2).trim().split("  ");
                    if (mainSplit != null && mainSplit.length > 5) {
                        for (String dataMain : mainSplit) {
                            if (dataMain.contains(":")) {
                                String[] fields = dataMain.trim().split(":");
                                //eta格式特殊 特殊处理 用空格分割
                                if (dataMain.contains("eta")) {
                                    fields = dataMain.split(" ");
                                }
                                if (fields != null && fields.length == 2) {
                                    currentHandlerTime = currentHandlerTime.replace("/", "-");
                                    String field = fields[0];
                                    String v = fields[1];
                                    if ("memory".equals(field)) {
                                        values.get("memorys_data").add(v);
                                        values.get("memorys_time").add(currentHandlerTime);
                                    } else if ("loss".equals(field)) {
                                        values.get("losss_time").add(currentHandlerTime);
                                        values.get("losss_data").add(v);
                                    } else if ("data_time".equals(field)) {
                                        values.get("data_times_time").add(currentHandlerTime);
                                        values.get("data_times_data").add(v);
                                    } else if ("grad_norm".equals(field)) {
                                        values.get("grad_norms_data").add(v);
                                        values.get("grad_norms_time").add(currentHandlerTime);
                                    } else if ("lr".equals(field)) {
                                        values.get("lrs_data").add(v);
                                        values.get("lrs_time").add(currentHandlerTime);
                                    } else if ("time".equals(field)) {
                                        values.get("times_time").add(currentHandlerTime);
                                        values.get("times_data").add(v);
                                    } else if ("eta".equals(field.replace(":", ""))) {
                                        values.get("etas_time").add(currentHandlerTime);
                                        //处理时间
                                        String[] split = v.split(":");
                                        if (split.length > 2) {
                                            int totalSeconds = Integer.parseInt(split[2]);
                                            totalSeconds = totalSeconds + (Integer.parseInt(split[1]) * 60);
                                            totalSeconds = totalSeconds + (Integer.parseInt(split[0]) * 3600);
                                            values.get("etas_data").add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis() + (totalSeconds * 1000l)));
                                        } else {
                                            values.get("etas_data").add(v);
                                        }
                                    }
//                            System.out.println("字段名字是:" + fields[0]);
                                }
                            }
                        }
                    }
                    main = length;
                    train = main.indexOf(searchKey);
                }
            }

//        int currentIndex = main.indexOf("mmengine");
//        int lastPostion  = 0;
//        while (currentIndex!=-1){
//            main = main.substring(currentIndex);
//            int endPostion = main.indexOf("\n");k'd'd
//            if(endPostion == -1){
//                break;
//            }
//            String currentLine = main.substring(currentIndex,endPostion);
//            lastPostion = endPostion;
//            main = main.substring(lastPostion);
//            System.out.println("剪切"+currentLine);
//        }
            return values;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
//

    }

}
