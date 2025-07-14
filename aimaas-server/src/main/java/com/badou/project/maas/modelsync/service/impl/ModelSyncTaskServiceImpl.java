package com.badou.project.maas.modelsync.service.impl;

import java.io.Serializable;
import java.util.*;

import com.alibaba.fastjson.JSONObject;
import com.badou.brms.base.support.struts.JsonReturnBean;
import com.badou.brms.dboperation.query.QueryCriterion;
import com.badou.brms.dboperation.query.QueryOperSymbolEnum;
import com.badou.brms.dboperation.query.QueryParam;
import com.badou.brms.dboperation.query.support.QueryHibernatePlaceholderParam;
import com.badou.brms.dictionary.DictionaryLib;
import com.badou.brms.dictionary.form.DictionaryCacheObject;
import com.badou.core.runtime.thread.local.LogonCertificateHolder;
import com.badou.project.GlobalConsts;
import com.badou.project.common.webparams.util.JsonResultUtil;
import com.badou.project.exception.DataEmptyException;
import com.badou.project.kubernetes.client.KubernetesApiClient;
import com.badou.project.kubernetes.client.KubernetesApiClientFactory;
import com.badou.project.kubernetes.handler.KubernetesExecHandler;
import com.badou.project.kubernetes.handler.KubernetesNameSpaceHandler;
import com.badou.project.kubernetes.handler.KubernetesPodHandler;
import com.badou.project.kubernetes.handler.KubernetesYamlHandler;
import com.badou.project.kubernetes.util.StringHandlerUtil;
import com.badou.project.maas.MaasConst;
import com.badou.project.maas.common.FileControllerService;
import com.badou.project.maas.modelsync.util.StringUtil;
import com.badou.project.maas.modelwarehouse.model.ModelWarehouseEntity;
import com.badou.project.maas.modelwarehouse.service.IModelWarehouseService;
import com.badou.project.mq.TaskMqSender;
import com.badou.project.server.model.K8sServerConfEntity;
import com.badou.project.server.service.IK8sServerConfService;
import com.badou.project.server.web.ServerGpuListAction;
import com.badou.tools.common.util.SpringHelper;
import com.badou.tools.common.util.StringUtils;
import io.kubernetes.client.openapi.models.V1Pod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.badou.brms.base.support.spring.BaseSpringService;

import com.badou.project.maas.modelsync.dao.IModelSyncTaskDAO;
import com.badou.project.maas.modelsync.model.ModelSyncTaskEntity;
import com.badou.project.maas.modelsync.service.IModelSyncTaskService;


/**
 * @author badousoft
 * @date 2025-04-11 10:33:39.208
 *  模型同步任务 Service接口实现类
 **/
@Service
public class ModelSyncTaskServiceImpl extends BaseSpringService<ModelSyncTaskEntity, Serializable> implements IModelSyncTaskService {

	@Autowired
	private IModelSyncTaskDAO modelSyncTaskDAO;
	@Autowired
	private KubernetesPodHandler kubernetesPodHandler;
	@Autowired
	private KubernetesExecHandler kubernetesExecHandler;
	@Autowired
	private IK8sServerConfService k8sServerConfService;
	@Autowired
	private FileControllerService fileControllerService;
	@Autowired
	private IModelSyncTaskService modelSyncTaskService;
	@Autowired
	private KubernetesNameSpaceHandler kubernetesNameSpaceHandler;
	@Autowired
	private KubernetesYamlHandler kubernetesYamlHandler;
	@Autowired
	private TaskMqSender taskMqSender;
	@Autowired
	private IModelWarehouseService modelWarehouseService;

	@Autowired
	public void setModelSyncTaskDAO(IModelSyncTaskDAO modelSyncTaskDAO) {
		this.modelSyncTaskDAO = modelSyncTaskDAO;
		super.setBaseDAO(modelSyncTaskDAO);
	}

	@Override
	public Object loadModelByModelScope(String paramServerId,String serverName,String path) {
		//构建模型仓库同步需要的信息
		ModelWarehouseEntity modelWarehouseEntity = new ModelWarehouseEntity();
		modelWarehouseEntity.setServerId(paramServerId);
		modelWarehouseEntity.setServerName(serverName);
		modelWarehouseEntity.setModelScopePath(path);
		modelWarehouseEntity.setName("同步"+path.split("/")[1]+"模型");
		modelWarehouseEntity.setFlgDeleted(GlobalConsts.ZERO);
		modelWarehouseEntity.setContentLength(0.0);
		modelWarehouseEntity.setUpdateTime(new Date());
		modelWarehouseEntity.setUpdator(LogonCertificateHolder.getLogonCertificate().getUserId());
		modelWarehouseEntity.setUpdatorName(LogonCertificateHolder.getLogonCertificate().getUserName());

		String[] serverIdArr = paramServerId.split(",");
		String[] serverNameArr = serverName.split(",");
		//检查任务
		QueryCriterion queryCriterion = new QueryCriterion();
		queryCriterion.addParam(new QueryHibernatePlaceholderParam("serverIds", serverIdArr, null, QueryOperSymbolEnum.in, QueryParam.PARAM_PLACEHOLDER_NAME));
		queryCriterion.addParam(new QueryHibernatePlaceholderParam("status",GlobalConsts.ONE,null, QueryOperSymbolEnum.eq, QueryParam.PARAM_PLACEHOLDER_NAME));
		queryCriterion.addParam(new QueryHibernatePlaceholderParam("targetObject",modelWarehouseEntity.getModelScopePath(),null, QueryOperSymbolEnum.eq, QueryParam.PARAM_PLACEHOLDER_NAME));
		List<ModelSyncTaskEntity> modelSyncTaskEntities = modelSyncTaskService.find(queryCriterion);


		//转Map 根据服务器ID放入服务器名称
		Map<String,String> serverConfigMap = new HashMap<>();
		for (int i = 0; i < serverIdArr.length; i++) {
			serverConfigMap.put(serverIdArr[i],serverNameArr[i]);
		}
		List<String> existList = new ArrayList<>();
		for (ModelSyncTaskEntity modelSyncTaskEntity : modelSyncTaskEntities) {
			existList.add(serverConfigMap.get(modelSyncTaskEntity.getServerIds()));
		}
		if (modelSyncTaskEntities.size()!=0){
			return String.join(",", existList)+"服务器存在的同步任务.请先等待任务完成或者取消同步任务";
		}

		modelWarehouseService.create(modelWarehouseEntity);

		DictionaryCacheObject dictionary = DictionaryLib.getDictionaryByCode(MaasConst.DIC_MODEL_SCOPE_CONFIG);
		String[] serverIds = modelWarehouseEntity.getServerId().split(",");
		for (String serverId : serverIds) {
			KubernetesApiClient kubernetesApiClient = null;
			try {
				kubernetesApiClient = KubernetesApiClientFactory.build(k8sServerConfService.find(serverId));
				String modelPath = kubernetesApiClient.getServer().getModelPaths()+"/"+modelWarehouseEntity.getModelScopePath();
				//判断对应的服务器是否已同步该模型
				JSONObject jsonObject = fileControllerService.execModelCommand(kubernetesApiClient.getServer(), new String[]{"ls", modelPath});
				if (!jsonObject.getString("msg").contains("No such file or directory")){
					return "该模型已存在服务器"+kubernetesApiClient.getServer().getRemark()+".请勿重复同步!";
				}
			} catch (Exception e) {
				e.printStackTrace();
				return "加载客户端异常!请联系管理员!";
			}
			try {
				//检查磁盘状态
				boolean b = fileControllerService.checkServerDiskLimit(kubernetesApiClient);
				if (b){
					return "磁盘已达到安全存储要求范围.任务拒绝,请联系管理员!";
				}
				kubernetesNameSpaceHandler.createNameSpace(kubernetesApiClient,MaasConst.MODEL_SYNC_NS);
			}catch (Exception e){
				e.printStackTrace();
				return "检查状态异常!请联系管理员!";
			}

			//设置任务消息
			ModelSyncTaskEntity modelSyncTaskEntity = new ModelSyncTaskEntity();
			modelSyncTaskEntity.setWarehouseId(modelWarehouseEntity.getId());
			modelSyncTaskEntity.setExecMsg("同步模型市场"+modelWarehouseEntity.getModelScopePath()+"的模型到本地");
			modelSyncTaskEntity.setWarehouseName(modelWarehouseEntity.getName());
			modelSyncTaskEntity.setServerIds(modelWarehouseEntity.getServerId());
			modelSyncTaskEntity.setServerNames(modelWarehouseEntity.getServerName());
			modelSyncTaskEntity.setStatus(MaasConst.DOPLAN_WAIT_STATUS);
			modelSyncTaskEntity.setTargetObject(modelWarehouseEntity.getModelScopePath());
			modelSyncTaskEntity.setType(GlobalConsts.ZERO);
			modelSyncTaskService.create(modelSyncTaskEntity);

			//实际创建模型任务
			try {
				kubernetesYamlHandler.startPythonModelScope(kubernetesApiClient,modelWarehouseEntity.getModelScopePath(),modelSyncTaskEntity,dictionary.getValue());
			} catch (DataEmptyException e) {
				e.printStackTrace();
				return "创建服务异常!请联系管理员!";
			}
			//发送到MQ 实时检查同步状态
			taskMqSender.sendModelSyncMessage(modelSyncTaskEntity);
		}

		return modelWarehouseEntity;
	}

	@Override
	public void closeTask(ModelSyncTaskEntity modelSyncTaskEntity) {
//		if (GlobalConsts.ZERO.equals(modelSyncTaskEntity.getType())){
//            try {
//                KubernetesApiClient kubernetesApiClient = FileControllerService.getCustomClient(modelSyncTaskEntity.getServerIds());
//				V1Pod pod = kubernetesPodHandler.getPod(kubernetesApiClient, MaasConst.MODEL_SYNC_NS, modelSyncTaskEntity.getCode());
//				if (pod!=null){
//					//当移除模型同步时 也会把已下载的模型进度也移除 无论下载进度如何
//					kubernetesPodHandler.deleteOnePod(kubernetesApiClient, MaasConst.MODEL_SYNC_NS, modelSyncTaskEntity.getCode());
//					K8sServerConfEntity server = kubernetesApiClient.getServer();
//					fileControllerService.execModelCommand(server,new String[]{"rm","-rf",server.getModelPaths()+"/"+modelSyncTaskEntity.getTargetObject().split("/")[0]});
//				}
//				modelSyncTaskMqReceiver.getRunningMap().remove(modelSyncTaskEntity.getTargetObject());
//			} catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//		modelSyncTaskEntity.setStatus(MaasConst.DOPLAN_CLOSE_STATUS);
//		update(modelSyncTaskEntity);
	}

	@Override
	public void updateTaskRunning(ModelSyncTaskEntity modelSyncTaskEntity) {
		modelSyncTaskEntity.setStatus(MaasConst.DOPLAN_RUN_STATUS);
		modelSyncTaskDAO.update(modelSyncTaskEntity);
	}

	@Override
	public void updateTaskSuccess(ModelSyncTaskEntity modelSyncTaskEntity, String msg) {
		//在成功或者失败的时候 都增加执行日志
//		if (StringUtils.isEmpty(msg)){
//			msg = "下载完成!已转成本地模型!";
//		}
//		//查询本次同步模型容量
//		try {
//			SpringHelper.getBean(ServerGpuListAction.class).buildModelSize(modelSyncTaskEntity.getTargetObject(),modelSyncTaskEntity.getServerIds());
//		} catch (Exception e) {
//			throw new RuntimeException(e);
//		}
//		modelSyncTaskEntity.setExecMsg(msg);
//		modelSyncTaskEntity.setExecPercentage(100);
//		modelSyncTaskEntity.setFinishTime(new Date());
//		modelSyncTaskEntity.setStatus(MaasConst.DOPLAN_SUCCESS_STATUS);
//		modelSyncTaskDAO.update(modelSyncTaskEntity);
//		modelSyncTaskMqReceiver.getRunningMap().remove(modelSyncTaskEntity.getTargetObject());
	}

	@Override
	public void updateTaskFail(ModelSyncTaskEntity modelSyncTaskEntity, String msg) {
//		modelSyncTaskEntity.setExecMsg(msg);
//		modelSyncTaskEntity.setStatus(MaasConst.DOPLAN_FAIL_STATUS);
//		modelSyncTaskDAO.update(modelSyncTaskEntity);
//		modelSyncTaskMqReceiver.getRunningMap().remove(modelSyncTaskEntity.getTargetObject());
//		K8sServerConfEntity server = null;
//        try {
//			//当任务失败时 移除已下载的资源
//            server = FileControllerService.getCustomClient(modelSyncTaskEntity.getServerIds()).getServer();
//			//不删父目录 只删子目录
//			fileControllerService.execModelCommand(server,new String[]{"rm","-rf",server.getModelPaths()+"/"+modelSyncTaskEntity.getTargetObject()});
//		} catch (Exception e) {
//            e.printStackTrace();
//        }
	}

	@Override
	public JsonReturnBean calcModelSyncStatus(ModelWarehouseEntity modelWarehouseEntity) {
		//注意 需要要求客户先选服务器 再选模型
		if (StringUtils.isEmpty(modelWarehouseEntity.getServerId())){
			 return JsonResultUtil.errorMsg("请先选择服务器");
		}
		//查看当前服务器是否有这个模型 如果没有 则去其他服务器找 如果其他服务器找不到 则返回错误.该模型不在任何服务器
		//根据模型目录 调用AIMAAS文件服务 并且基于服务器的模型存储路径检查该服务器是否有该模型
        try {
            KubernetesApiClient kubernetesApiClient = FileControllerService.getCustomClient(modelWarehouseEntity.getServerId());
			KubernetesApiClient currentK8sClient = FileControllerService.getCacheK8sClient(modelWarehouseEntity.getServerId());
			K8sServerConfEntity server = currentK8sClient.getServer();
			String result = checkServerHasModel(kubernetesApiClient, server, modelWarehouseEntity.getPath());

			if (StringUtils.isNoneBlank(result) || "当前服务器没有该模型 请选择其他服务器!".equals(result)){
				return JsonResultUtil.errorMsg(result);
			}


			List<String> emptyServer = new ArrayList<>();
			String emptySeverIds = "";
			String emptySeverNames = "";
			List<String> hadServer = new ArrayList<>();
			String hadSeverIds = server.getId();
			String hadSeverNames = server.getRemark();

			//获取所有的服务器列表 判断哪个服务器有该模型 当emptyServer=0 代表目前所有服务器都已经有该模型 结束即可
			QueryCriterion queryCriterion = new QueryCriterion();
			queryCriterion.addParam(new QueryHibernatePlaceholderParam("FLG_DELETED",0,null, QueryOperSymbolEnum.eq));
			queryCriterion.addParam(new QueryHibernatePlaceholderParam("ID",modelWarehouseEntity.getServerId(),null, QueryOperSymbolEnum.ne));
			List<K8sServerConfEntity> k8sServerConfEntities = k8sServerConfService.find(queryCriterion);
			for (K8sServerConfEntity k8sServerConfEntity : k8sServerConfEntities) {
				KubernetesApiClient execClient = FileControllerService.getCustomClient(k8sServerConfEntity.getId());
				result = checkServerHasModel(execClient, k8sServerConfEntity, modelWarehouseEntity.getPath());
				//有模型则跳过
				if (StringUtils.isEmpty(result)){
					hadServer.add(k8sServerConfEntity.getId());
					hadSeverIds = StringUtils.join(hadServer,",");
					hadSeverNames = StringUtils.join(hadServer,",");
					continue;
				}
				//没有模型则加入到空服务器列表中
				if ("当前服务器没有该模型 请选择其他服务器!".equals(result)){
					emptyServer.add(k8sServerConfEntity.getId());
					emptySeverIds = StringUtils.join(emptyServer,",");
					emptySeverNames = StringUtils.join(emptyServer,",");
				}else {
					//执行报错 并不是没有模型 任务结束
					return JsonResultUtil.errorMsg(result);
				}
			}
			//去掉最后一个,
			emptySeverIds = StringHandlerUtil.removeLastComma(emptySeverIds);
			hadSeverNames = StringHandlerUtil.removeLastComma(hadSeverNames);
			hadSeverIds = StringHandlerUtil.removeLastComma(hadSeverIds);
			//所有服务器都有模型
			if (emptyServer.size() == 0){
				return JsonResultUtil.success();
			}
			//存在空/无模型的服务器 形成同步模型任务实体
			//如果当前服务有 则拼接到源服务器 如果没有 则拼接到目标服务器 确认清楚执行目标
			ModelSyncTaskEntity modelSyncTaskEntity = new ModelSyncTaskEntity();
			modelSyncTaskEntity.setSourceServerIds(hadSeverIds);
			modelSyncTaskEntity.setSourceServerNames(hadSeverNames);
			modelSyncTaskEntity.setServerIds(emptySeverIds);
			modelSyncTaskEntity.setServerNames(emptySeverNames);
			modelSyncTaskEntity.setWarehouseId(modelWarehouseEntity.getId());
			modelSyncTaskEntity.setWarehouseName(modelWarehouseEntity.getName());
			modelSyncTaskEntity.setStatus(0);
			modelSyncTaskEntity.setExecPercentage(0);
			modelSyncTaskEntity.setExecMsg("");
			//统计信息 空模型服务器数量*模型大小 = 模型数据总量
			modelSyncTaskEntity.setTotalSize(
					StringHandlerUtil.getNetFileSizeToMB(StringHandlerUtil.parseNetFileSizeToByte(modelWarehouseEntity.getSize())* emptyServer.size()));
			modelSyncTaskEntity.setCode(StringUtil.buildTaskCode(currentK8sClient,modelWarehouseEntity));
			//预计多少时间 是需要拿镜像里面的时间和网速算出啦的 创建时候计算不了
			//整理好任务信息 告诉用户需要启动同步任务
			//然后经过客户的二次元确认 任务就可以开始了
			JsonReturnBean success = JsonResultUtil.success(modelSyncTaskEntity);
			success.setMessage("同步模型前置确认");
			return success;
		} catch (Exception e) {
            return JsonResultUtil.errorMsg("服务异常!请联系系统管理员!");
        }
	}

	@Override
	public JsonReturnBean startServerSyncTask(ModelWarehouseEntity modelWarehouseEntity, String[] targetServers) {
		return null;
	}

	@Override
	public String checkValidTask(ModelWarehouseEntity modelWarehouseEntity) {
		//查询任务 如果有正在(非成功)运行/失败的任务 则拒绝用户启动模型
		QueryCriterion queryCriterion = new QueryCriterion();
		queryCriterion.addParam(new QueryHibernatePlaceholderParam("status",GlobalConsts.TWO,null,QueryOperSymbolEnum.ne));
		queryCriterion.addParam(new QueryHibernatePlaceholderParam("warehouseId",modelWarehouseEntity.getId(),null,QueryOperSymbolEnum.eq));
		List<ModelSyncTaskEntity> modelSyncTaskEntities = modelSyncTaskDAO.find(queryCriterion);
		if (modelSyncTaskEntities.size() != GlobalConsts.ZERO){
			return "该模型存在未同步完成的任务...请等待任务完成/重试该任务";
		}
		//如果是同步任务 目前查看这个任务状态即可
		return null;
	}

	/**
	 * 检查对应的服务器 是否有该模型
	 * @param kubernetesApiClient K8S客户端
	 * @param server 服务器信息
	 * @param currentModelPath 当前用户选择的服务器路径
	 * @return 执行结果 如果返回null 代表该服务器有模型 如果返回字符串不存在模型 则准备开启同步工作 如果返回其他字符串 代表执行异常
	 */
	private String checkServerHasModel(KubernetesApiClient kubernetesApiClient,K8sServerConfEntity server,String currentModelPath) {
        List<V1Pod> filePods = null;
        try {
            filePods = kubernetesPodHandler.getPodByLabelApp(kubernetesApiClient, FileControllerService.deploymentName, FileControllerService.deploymentName + "-" +server.getCode());
			//如果该服务不存在的时候 就无法做同步任务了 直接报错
			V1Pod v1Pod = filePods.get(0);
			JSONObject jsonObject = kubernetesExecHandler.execCommandOnce(v1Pod.getMetadata().getName(), FileControllerService.nameSpace, kubernetesApiClient, new String[]{
					"ls", currentModelPath.replace("/home",server.getMainLocalPath()==null?"/home":server.getMainLocalPath())
			});
			if (!jsonObject.getBoolean("success")){
//				代表当前服务器没有该模型 拒绝执行 一般当前服务器至少需要拥有该模型
				return "当前服务器没有该模型 请选择其他服务器!";
			}
			logger.info(server.getCode()+"存在模型"+currentModelPath);
			return null;
        } catch (Exception e) {
			e.printStackTrace();
            return "识别仓库信息异常!请联系管理员!";
        }
	}

}

