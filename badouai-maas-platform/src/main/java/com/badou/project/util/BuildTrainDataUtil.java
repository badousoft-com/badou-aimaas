package com.badou.project.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.badou.brms.attach.AttachFactory;
import com.badou.brms.attach.AttachHelper;
import com.badou.brms.attach.model.Attach;
import com.badou.brms.attach.service.IAttachService;
import com.badou.project.maas.trainfile.model.TrainMultiDetailFileEntity;
import com.badou.project.maas.trainfiledialogue.model.TrainFileDialogueEntity;
import com.badou.project.maas.tuningmodeln.model.TuningModelnEntity;
import com.badou.tools.common.util.SpringHelper;

import java.util.UUID;

/**
 * @author lingmeng
 * @date 2024-10-29 15:30:11
 * @todo 构建微调数据集工具类
 **/
public class BuildTrainDataUtil {

    public static void main(String[] args) {
        System.out.println("/attach/20240812/1723453254466-110336995.attach".split("/")[3]);
    }

    /**
     * 20241029 xtuner支持偏好训练集格式
     * @param tuningModelnEntity 任务信息
     * @return
     */
    public static JSONObject buildXtunerRewardData(TrainFileDialogueEntity trainFileDialogueEntity, TuningModelnEntity tuningModelnEntity, String roleDesc){
        /**
         * {
         *   "prompt": [
         *     {
         *       "role": "system",
         *       "content": "You are a helpful assistant."
         *     },
         *     {
         *       "role": "user",
         *       "content": "Who won the world series in 2020?"
         *     },
         *     {
         *       "role": "assistant",
         *       "content": "The Los Angeles Dodgers won the World Series in 2020."
         *     },
         *     {
         *       "role": "user",
         *       "content": "Where was it played?"
         *     }
         *   ],
         *   "chosen": [
         *     {
         *       "role": "assistant",
         *       "content": "The 2020 World Series was played at Globe Life Field in Arlington, Texas."
         *     }
         *   ],
         *   "rejected": [
         *     {
         *       "role": "assistant",
         *       "content": "I don't know."
         *     }
         *   ]
         * }
         */
        //构建偏好数据集
        JSONObject trainLine = new JSONObject();

        JSONArray prompts = new JSONArray();
        JSONObject prompt = new JSONObject();
        prompt.put("role","system");
        prompt.put("content",trainFileDialogueEntity.getQuestion());
        prompts.add(prompt);
        trainLine.put("prompt", prompts);

        JSONArray chosens = new JSONArray();
        JSONObject chosen = new JSONObject();
        chosen.put("role","assistant");
        chosen.put("content",trainFileDialogueEntity.getChosena());
        chosens.add(chosen);
        trainLine.put("chosen", chosens);

        JSONArray rejecteds = new JSONArray();
        JSONObject rejected = new JSONObject();
        rejected.put("role","assistant");
        rejected.put("content",trainFileDialogueEntity.getRejecteda());
        rejecteds.add(rejected);
        trainLine.put("rejected", rejecteds);

        return trainLine;
    }

    /**
     * 20241119 llamafactory支持微调多模态-图片训练集格式
     * @param tuningModelnEntity 任务信息
     * @return
     */
    public static JSONObject buildLlamafactoryImaData(TrainMultiDetailFileEntity trainMultiDetailFileEntity, TuningModelnEntity tuningModelnEntity, String roleDesc){
        /**
         * [
         *   {
         *     "messages": [
         *       {
         *         "content": "<image>Who are they?",
         *         "role": "user"
         *       },
         *       {
         *         "content": "They're Kane and Gretzka from Bayern Munich.",
         *         "role": "assistant"
         *       },
         *       {
         *         "content": "What are they doing?<image>",
         *         "role": "user"
         *       },
         *       {
         *         "content": "They are celebrating on the soccer field.",
         *         "role": "assistant"
         *       }
         *     ],
         *     "images": [
         *       "mllm_demo_data/1.jpg",
         *       "mllm_demo_data/1.jpg"
         *     ]
         *   },
         * ]
         */
        JSONObject row = new JSONObject();
        JSONArray messages  = new JSONArray();
        JSONArray images  = new JSONArray();
        row.put("messages",messages);
        //构建图片对象
        row.put("images",images);
        String attachId = trainMultiDetailFileEntity.getAttachId();
        //构建对话对象
        JSONObject messageQ = new JSONObject();
        messageQ.put("content",trainMultiDetailFileEntity.getQuestion()+"<image>");
        messageQ.put("role","user");
        messages.add(messageQ);
        JSONObject messageA = new JSONObject();
        messageA.put("content",trainMultiDetailFileEntity.getFeedback());
        messageA.put("role","assistant");
        messages.add(messageA);

        String definePath = "files/"+trainMultiDetailFileEntity.getImgDefind();
        images.add(definePath);
        return row;
    }

}
