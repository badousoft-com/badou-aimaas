package com.badou.project.maas.evaluationinstan.web;

import com.badou.brms.base.support.struts.JsonReturnBean;
import com.badou.brms.dictionary.DictionaryLib;
import com.badou.project.common.webparams.util.JsonResultUtil;
import com.badou.project.maas.evaluationinstan.service.IEvaluationInstanService;
import com.badou.project.maas.modelapp.service.IModelAppService;
import com.badou.project.maas.trainfiledialogue.model.TrainFileDialogueEntity;
import com.badou.project.maas.trainfiledialogue.service.ITrainFileDialogueService;
import com.badou.project.mq.EvaluationMqReceiver;
import com.badou.project.mq.ModelAppMqReceiver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.badou.designer.jdbc.common.web.BaseCommonSaveAction;
import org.springframework.web.bind.annotation.PostMapping;

import javax.xml.bind.DataBindingException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

/**
 * @author badousoft
 * @created 2024-06-06 15:58:38.064
 * @todo 模型评价实例 保存实现类
 */
@Controller
public class EvaluationInstanSaveAction extends BaseCommonSaveAction {

}
