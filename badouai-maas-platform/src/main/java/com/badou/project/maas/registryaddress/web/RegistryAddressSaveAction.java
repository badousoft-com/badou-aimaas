package com.badou.project.maas.registryaddress.web;

import com.badou.designer.jdbc.common.web.BaseCommonSaveAction;
import com.badou.project.exception.DataErrorException;
import com.badou.project.exception.DataValidException;
import com.badou.project.kubernetes.util.StringHandlerUtil;
import com.badou.project.maas.registryaddress.model.RegistryAddressEntity;
import com.badou.project.maas.registryaddress.service.IRegistryAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * @author badousoft
 * @created 2024-05-08 14:41:26.637
 * @todo 微调计划方案 保存实现类
 */
@Controller
public class RegistryAddressSaveAction extends BaseCommonSaveAction {

    @Autowired
    private IRegistryAddressService registryAddressService;

    @Override
    public void exeBeforeSave() throws Exception {
        //执行父方法的唯一逻辑
        super.exeBeforeSave();
        //检查是否还有其他的默认仓库
        RegistryAddressEntity defaultRegistryAddress = registryAddressService.getDefaultRegistryAddress();
        if (defaultRegistryAddress!=null && !this.getCustForm().getId().equals(defaultRegistryAddress.getId())){
            throw new DataErrorException("已有其他的默认仓库.请先取消目标仓库的默认仓库设定");
        }
        String[] address = this.getCustForm().getDetails().get("address");
        if (address == null || address.length != 1){
            throw new DataValidException("镜像仓库地址是必填项");
        }
        this.custForm.getDetails().put("address",new String[]{address[0].replace("/","")
                .replace("https","").replace("http","").replace(":","")});
    }

}
