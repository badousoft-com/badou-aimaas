# 										   				 AIMaaS平台-助力您快速落地私有化的AI能力，一键开启智能新时代，全面拥抱未来！ 🤖

   AIMaaS平台是由八斗AI技术团队研发的"一站式模型服务管理平台"，是一款集模型部署与管理、模型微调训练、算力资源管理等功能于一体的模型服务平台，目标是帮助企业快速实现大模型私有化部署与应用，支持将一些企业或者组织专业的问答只是微调至应用模型中，快速推进企业业务智能化的发展。
   
特点包含：
 - 一键部署模型：一键部署Qwen、Deepseek、Glm、LLama等大语言模型。可以灵活设定模型使用多少张显卡进行启动
 - 一键启停模型：一键启动与停止模型。
 - 在线监控模型运行状态：在线查看模型运行日志，了解模型运行情况。
 - 一键式模型微调：可以快速进行模型的微调训练，目前支持全参微调、qlora微调、lora微调等。
 - 多算力设备统一管理：支持管理多台算力服务器资源，便于自由拓展管理企业的算力。

如有问题或者商业合作，可以直接给我们email留言：aiservice@badousoft.com

# 案例介绍
  企业私有化模型部署应用案例，某企业通过AIMaaS平台，实现对3台6卡服务器进行部署，采用入门级显卡2080TI进行部署。

  服务器配置：2U、512G、1T磁盘 配置。千兆路由器实现服务器互联。

  - 服务器A：部署AIMaaS平台，实现对所有的算力设备进行管理。
  4卡：运行DeepSeek-r1-32b，提供语言推理服务。  【注：4卡2080TI建议用户数载10以内。可以升级到4090显卡，提升并发与会话响应速度】
  2卡：运行Qwen2.5-vl-7b，提供图像识别推理服务

  - 服务器B：（AIMaaS平台节点服务，只需元安装k8s加入AIMaaS平台管理即可）。
  4卡：运行Qwen3-32b，提供语言交互服务
  1卡：运行Bge-large*，向量化模型
  1卡：运行Bge-rerank模型

  - 服务器C：（AIMaaS平台节点服务，只需元安装k8s加入AIMaaS平台管理即可）
  2卡：运行Qwen2.5-14b，提供语言服务，用于问题分类，简单问题处理，工具调用等
  4卡：用于模型微调训练，支持对7b、14b以下模型进行微调【注：根据不同的训练参数，可能时间周期比较长，但如果只是lora模式且轮数较少，可以轻度微调模型，增加关键业务问题的解答能力与自我认知能力】

  通过以上配置启动的服务，可以接入Dify、Fastgpt、RagFlow、n8n进行集成应用服务。
  如：
  可以通过工作流引用快速构建智能客服、智能创作等应用。

# 前置环境配置
   部署AIMaaS平台，建议由独立算力的服务器，建议配置：
- 1、经济体验版本
   一台服务器2CPU、12G内存、1T磁盘，1-N张卡。建议2-4张显卡
- 2、专业训练版本
   2台以上的服务器，单台服务器配置：2CPU、512G内存、2T磁盘，4-8张显卡，推荐3090、4090、A100、L40等配置以上的显卡。

建议运行操作系统：centos7.9+，Ubuntu20+以上。


# 技术原理
   本项目主要即成了VLLM和LLamafactory，无须命令行操作，直接在线可视化管理模型。所有的操作都会转成任务进行命令的调度。
   
# 🚦 快速开始

```bash
docker run crpi-wfhl7cyuhi65rl7p.cn-guangzhou.personal.cr.aliyuncs.com/fadsii/badou-aimaas:1.0
```

源码启动

```python
git clone 
# 初始化和运行前端
cd badouai-maas
npm install --registry=https://registry.npm.taobao.org
# 启动服务
npm run dev
 
# 初始化和运行后端 回到项目根目录
cd aimaas-server
mvn package 
# 把工程放到8080端口的tomcat的webapps
# 运行成功后 访问http://127.0.0.1:8000/badouai-maas/#/
```

------

# 可提供的服务
 - 1、协助私有化组建算力服务器，例如：2台6卡服务器的算力集群。低成本快速落地企业私有化AI能力。
 - 2、可定制AIMaaS平台，融入企业服务中。
 - 3、协助微调模型，提供方案建议。
 （如有问题或者商业合作，可以直接给我们email留言：aiservice@badousoft.com）
 



# 支持训练的模型

| 模型                                                         | 模型大小                         |
| ------------------------------------------------------------ | -------------------------------- |
| [Baichuan 2](https://huggingface.co/baichuan-inc)            | 7B/13B                           |
| [ChatGLM3](https://huggingface.co/THUDM)                     | 6B                               |
| [Command R](https://huggingface.co/CohereForAI)              | 35B/104B                         |
| [BLOOM/BLOOMZ](https://huggingface.co/bigscience)            | 560M/1.1B/1.7B/3B/7.1B/176B      |
| [DeepSeek (Code/MoE)](https://huggingface.co/deepseek-ai)    | 7B/16B/67B/236B                  |
| [DeepSeek 2.5/3](https://huggingface.co/deepseek-ai)         | 236B/671B                        |
| [DeepSeek R1 (Distill)](https://huggingface.co/deepseek-ai)  | 1.5B/7B/8B/14B/32B/70B/671B      |
| [Falcon](https://huggingface.co/tiiuae)                      | 7B/11B/40B/180B                  |
| [Falcon-H1](https://huggingface.co/tiiuae)                   | 0.5B/1.5B/3B/7B/34B              |
| [Gemma/Gemma 2/CodeGemma](https://huggingface.co/google)     | 2B/7B/9B/27B                     |
| [Gemma 3/Gemma 3n](https://huggingface.co/google)            | 1B/4B/6B/8B/12B/27B              |
| [GLM-4/GLM-4-0414/GLM-Z1](https://huggingface.co/THUDM)      | 9B/32B                           |
| [GLM-4.1V](https://huggingface.co/THUDM)*                    | 9B                               |
| [GPT-2](https://huggingface.co/openai-community)             | 0.1B/0.4B/0.8B/1.5B              |
| [Granite 3.0-3.3](https://huggingface.co/ibm-granite)        | 1B/2B/3B/8B                      |
| [Hunyuan](https://huggingface.co/tencent/)                   | 7B                               |
| [Index](https://huggingface.co/IndexTeam)                    | 1.9B                             |
| [InternLM 2-3](https://huggingface.co/internlm)              | 7B/8B/20B                        |
| [InternVL 2.5-3](https://huggingface.co/OpenGVLab)           | 1B/2B/8B/14B/38B/78B             |
| [Kimi-VL](https://huggingface.co/moonshotai)                 | 16B                              |
| [Llama](https://github.com/facebookresearch/llama)           | 7B/13B/33B/65B                   |
| [Llama 2](https://huggingface.co/meta-llama)                 | 7B/13B/70B                       |
| [Llama 3-3.3](https://huggingface.co/meta-llama)             | 1B/3B/8B/70B                     |
| [Llama 4](https://huggingface.co/meta-llama)                 | 109B/402B                        |
| [Llama 3.2 Vision](https://huggingface.co/meta-llama)        | 11B/90B                          |
| [LLaVA-1.5](https://huggingface.co/llava-hf)                 | 7B/13B                           |
| [LLaVA-NeXT](https://huggingface.co/llava-hf)                | 7B/8B/13B/34B/72B/110B           |
| [LLaVA-NeXT-Video](https://huggingface.co/llava-hf)          | 7B/34B                           |
| [MiMo](https://huggingface.co/XiaomiMiMo)                    | 7B                               |
| [MiniCPM](https://huggingface.co/openbmb)                    | 0.5B/1B/2B/4B/8B                 |
| [MiniCPM-o-2.6/MiniCPM-V-2.6](https://huggingface.co/openbmb) | 8B                               |
| [Ministral/Mistral-Nemo](https://huggingface.co/mistralai)   | 8B/12B                           |
| [Mistral/Mixtral](https://huggingface.co/mistralai)          | 7B/8x7B/8x22B                    |
| [Mistral Small](https://huggingface.co/mistralai)            | 24B                              |
| [OLMo](https://huggingface.co/allenai)                       | 1B/7B                            |
| [PaliGemma/PaliGemma2](https://huggingface.co/google)        | 3B/10B/28B                       |
| [Phi-1.5/Phi-2](https://huggingface.co/microsoft)            | 1.3B/2.7B                        |
| [Phi-3/Phi-3.5](https://huggingface.co/microsoft)            | 4B/14B                           |
| [Phi-3-small](https://huggingface.co/microsoft)              | 7B                               |
| [Phi-4](https://huggingface.co/microsoft)                    | 14B                              |
| [Pixtral](https://huggingface.co/mistralai)                  | 12B                              |
| [Qwen (1-2.5) (Code/Math/MoE/QwQ)](https://huggingface.co/Qwen) | 0.5B/1.5B/3B/7B/14B/32B/72B/110B |
| [Qwen3 (MoE)](https://huggingface.co/Qwen)                   | 0.6B/1.7B/4B/8B/14B/32B/235B     |
| [Qwen2-Audio](https://huggingface.co/Qwen)                   | 7B                               |
| [Qwen2.5-Omni](https://huggingface.co/Qwen)                  | 3B/7B                            |
| [Qwen2-VL/Qwen2.5-VL/QVQ](https://huggingface.co/Qwen)       | 2B/3B/7B/32B/72B                 |
| [Seed Coder](https://huggingface.co/ByteDance-Seed)          | 8B                               |
| [Skywork o1](https://huggingface.co/Skywork)                 | 8B                               |
| [StarCoder 2](https://huggingface.co/bigcode)                | 3B/7B/15B                        |
| [TeleChat2](https://huggingface.co/Tele-AI)                  | 3B/7B/35B/115B                   |
| [XVERSE](https://huggingface.co/xverse)                      | 7B/13B/65B                       |
| [Yi/Yi-1.5 (Code)](https://huggingface.co/01-ai)             | 1.5B/6B/9B/34B                   |
| [Yi-VL](https://huggingface.co/01-ai)                        | 6B/34B                           |
| [Yuan 2](https://huggingface.co/IEITYuan)                    | 2B/51B/102B                      |



# 支持部署的模型

| 模型                                                  | 模型大小                     | 核心模态         |
| ----------------------------------------------------- | ---------------------------- | ---------------- |
| [Llama 3-3.3](https://huggingface.co/meta-llama)      | 1B/3B/8B/70B                 | 文本模态         |
| [Mixtral 8x7B](https://huggingface.co/mistralai)      | 8x7B                         | 文本模态         |
| [Qwen3 (MoE)](https://huggingface.co/Qwen)            | 0.6B/1.7B/4B/8B/14B/32B/235B | 文本模态         |
| [InternVL 2.5-3](https://huggingface.co/OpenGVLab)    | 1B/2B/8B/14B/38B/78B         | 多模态（文图）   |
| [LLaVA-NeXT](https://huggingface.co/llava-hf)         | 7B/8B/13B/34B/72B/110B       | 多模态（文图）   |
| [Qwen2.5-VL](https://huggingface.co/Qwen)             | 2B/3B/7B/32B/72B             | 多模态（文图）   |
| [Yi-VL](https://huggingface.co/01-ai)                 | 6B/34B                       | 多模态（文图）   |
| [StarCoder 2](https://huggingface.co/bigcode)         | 3B/7B/15B                    | 文本模态（代码） |
| [TeleChat2](https://huggingface.co/Tele-AI)           | 3B/7B/35B/115B               | 文本模态         |
| [Llama 3.2 Vision](https://huggingface.co/meta-llama) | 11B/90B                      | 多模态（文图）   |
| [Phi-3](https://huggingface.co/microsoft)             | 4B/14B                       | 文本模态         |
| [ChatGLM3](https://huggingface.co/THUDM)              | 6B                           | 文本模态         |

> 注：以上仅展示部分支持部署的模型，完整模型列表可前往 aimaas 平台查看。



# 演示

## 演示：模型一键部署流程

展示从「选择模型」到「启动服务」的全流程，包含版本选择、资源配置（CPU/GPU）、部署确认及启动成功提示。

![](assets/deploy-model.gif)

## 演示：在线模型微调操作和微调后模型效果对比

展示如何上传微调数据集、配置微调参数（学习率、迭代次数等）、启动微调任务及查看实时训练进度。





# 引用 

```markdown
@inproceedings{zheng2024llamafactory,
  title={LlamaFactory: Unified Efficient Fine-Tuning of 100+ Language Models},
  author={Yaowei Zheng and Richong Zhang and Junhao Zhang and Yanhan Ye and Zheyan Luo and Zhangchi Feng and Yongqiang Ma},
  booktitle={Proceedings of the 62nd Annual Meeting of the Association for Computational Linguistics (Volume 3: System Demonstrations)},
  address={Bangkok, Thailand},
  publisher={Association for Computational Linguistics},
  year={2024},
  url={http://arxiv.org/abs/2403.13372}
}

@inproceedings{kwon2023efficient,
  title={Efficient Memory Management for Large Language Model Serving with PagedAttention},
  author={Woosuk Kwon and Zhuohan Li and Siyuan Zhuang and Ying Sheng and Lianmin Zheng and Cody Hao Yu and Joseph E. Gonzalez and Hao Zhang and Ion Stoica},
  booktitle={Proceedings of the ACM SIGOPS 29th Symposium on Operating Systems Principles},
  year={2023}
}

@misc{glm2024chatglm,
      title={ChatGLM: A Family of Large Language Models from GLM-130B to GLM-4 All Tools}, 
      author={Team GLM and Aohan Zeng and Bin Xu and Bowen Wang and Chenhui Zhang and Da Yin and Diego Rojas and Guanyu Feng and Hanlin Zhao and Hanyu Lai and Hao Yu and Hongning Wang and Jiadai Sun and Jiajie Zhang and Jiale Cheng and Jiayi Gui and Jie Tang and Jing Zhang and Juanzi Li and Lei Zhao and Lindong Wu and Lucen Zhong and Mingdao Liu and Minlie Huang and Peng Zhang and Qinkai Zheng and Rui Lu and Shuaiqi Duan and Shudan Zhang and Shulin Cao and Shuxun Yang and Weng Lam Tam and Wenyi Zhao and Xiao Liu and Xiao Xia and Xiaohan Zhang and Xiaotao Gu and Xin Lv and Xinghan Liu and Xinyi Liu and Xinyue Yang and Xixuan Song and Xunkai Zhang and Yifan An and Yifan Xu and Yilin Niu and Yuantao Yang and Yueyan Li and Yushi Bai and Yuxiao Dong and Zehan Qi and Zhaoyu Wang and Zhen Yang and Zhengxiao Du and Zhenyu Hou and Zihan Wang},
      year={2024},
      eprint={2406.12793},
      archivePrefix={arXiv},
      primaryClass={id='cs.CL' full_name='Computation and Language' is_active=True alt_name='cmp-lg' in_archive='cs' is_general=False description='Covers natural language processing. Roughly includes material in ACM Subject Class I.2.7. Note that work on artificial languages (programming languages, logics, formal systems) that does not explicitly address natural-language issues broadly construed (natural-language processing, computational linguistics, speech, text retrieval, etc.) is not appropriate for this area.'}
}


@misc{glm2024chatglm,
      title={ChatGLM: A Family of Large Language Models from GLM-130B to GLM-4 All Tools},
      author={Team GLM and Aohan Zeng and Bin Xu and Bowen Wang and Chenhui Zhang and Da Yin and Diego Rojas and Guanyu Feng and Hanlin Zhao and Hanyu Lai and Hao Yu and Hongning Wang and Jiadai Sun and Jiajie Zhang and Jiale Cheng and Jiayi Gui and Jie Tang and Jing Zhang and Juanzi Li and Lei Zhao and Lindong Wu and Lucen Zhong and Mingdao Liu and Minlie Huang and Peng Zhang and Qinkai Zheng and Rui Lu and Shuaiqi Duan and Shudan Zhang and Shulin Cao and Shuxun Yang and Weng Lam Tam and Wenyi Zhao and Xiao Liu and Xiao Xia and Xiaohan Zhang and Xiaotao Gu and Xin Lv and Xinghan Liu and Xinyi Liu and Xinyue Yang and Xixuan Song and Xunkai Zhang and Yifan An and Yifan Xu and Yilin Niu and Yuantao Yang and Yueyan Li and Yushi Bai and Yuxiao Dong and Zehan Qi and Zhaoyu Wang and Zhen Yang and Zhengxiao Du and Zhenyu Hou and Zihan Wang},
      year={2024},
      eprint={2406.12793},
      archivePrefix={arXiv},
      primaryClass={id='cs.CL' full_name='Computation and Language' is_active=True alt_name='cmp-lg' in_archive='cs' is_general=False description='Covers natural language processing. Roughly includes material in ACM Subject Class I.2.7. Note that work on artificial languages (programming languages, logics, formal systems) that does not explicitly address natural-language issues broadly construed (natural-language processing, computational linguistics, speech, text retrieval, etc.) is not appropriate for this area.'}
}

@misc{2023xtuner,
    title={XTuner: A Toolkit for Efficiently Fine-tuning LLM},
    author={XTuner Contributors},
    howpublished = {\url{https://github.com/InternLM/xtuner}},
    year={2023}
}
```



**AIMAAS 项目许可证**



本项目（含源代码、文档等）受《GNU 通用公共许可证第 3 版》（GPLv3）及以下补充条款约束：

1. **授权范围**：个人非商业使用可免费获取、使用、复制、修改，衍生作品需适用相同许可证；商业使用（含集成到商业产品、商业服务等）须获项目方书面授权并支付费用。
2. **传播与修改**：传播时需保留原始版权及许可证信息；修改后的作品须明确标注修改内容，且受本许可证约束。
3. **限制与免责**：禁止用于违法或侵权活动；项目方不提供任何担保，使用风险由使用者自行承担。
4. **适用法律**：受中华人民共和国法律管辖，争议先协商，协商不成由项目方所在地法院管辖。

补充条款与 GPLv3 冲突时，以 GPLv3 强制性规定为准。项目方保留许可证解释及修改权，修改后通过官方渠道发布。
