package com.badou.project.maas.maasfile.service.impl;

import java.io.Serializable;

import com.badou.project.maas.MaasConst;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.badou.brms.base.support.spring.BaseSpringService;

import com.badou.project.maas.maasfile.dao.IMaasAiContentTipDAO;
import com.badou.project.maas.maasfile.model.MaasAiContentTipEntity;
import com.badou.project.maas.maasfile.service.IMaasAiContentTipService;


/**
 * @author badousoft
 * @date 2025-02-05 17:51:01.635
 *  ai内容生成提示词 Service接口实现类
 **/
@Service
public class MaasAiContentTipServiceImpl extends BaseSpringService<MaasAiContentTipEntity, Serializable> implements IMaasAiContentTipService {

	@Autowired
	private IMaasAiContentTipDAO maasAiContentTipDAO;

	@Autowired
	public void setMaasAiContentTipDAO(IMaasAiContentTipDAO maasAiContentTipDAO) {
		this.maasAiContentTipDAO = maasAiContentTipDAO;
		super.setBaseDAO(maasAiContentTipDAO);
	}

	@Override
	public String getTipByContent(int type) {
		MaasAiContentTipEntity maasAiContentTipEntity = maasAiContentTipDAO.find(MaasConst.ROOT_ID);
		switch (type){
			case 0:
				return maasAiContentTipEntity.getPtTip();
			case 1:
				return maasAiContentTipEntity.getSftTip();
			case 2:
				return maasAiContentTipEntity.getPpoTip();
			case 3:
				return maasAiContentTipEntity.getKtoTip();
		}
		return null;
	}
}

