package  com.badou.project.maas.maasfile.web.form;

import java.util.Date;
import java.sql.Timestamp;

import com.badou.brms.base.support.struts.form.BaseStrutsEntityForm;
import com.badou.project.maas.maasfile.model.MaasAiContentTipEntity;

/**
 * @author badousoft
 * @date 2025-02-05 17:51:01.635
 *  ai内容生成提示词form
 */
public class MaasAiContentTipForm extends BaseStrutsEntityForm<MaasAiContentTipEntity> {

	/**
     * 指令监督生成提示词
     */
    protected String  stfTip;
	/**
     * 偏好数据生成提示词
     */
    protected String  rlhf;
	/**
     * 预训练数据生成提示词
     */
    protected String  ptTip;

        /**
     *  获取指令监督生成提示词
     */
    public String getStfTip() {
        return stfTip;
    }

	/**
     *  设置指令监督生成提示词
     */
    public void setStfTip(String stfTip) {
        this.stfTip = stfTip;
    }
    /**
     *  获取偏好数据生成提示词
     */
    public String getRlhf() {
        return rlhf;
    }

	/**
     *  设置偏好数据生成提示词
     */
    public void setRlhf(String rlhf) {
        this.rlhf = rlhf;
    }
    /**
     *  获取预训练数据生成提示词
     */
    public String getPtTip() {
        return ptTip;
    }

	/**
     *  设置预训练数据生成提示词
     */
    public void setPtTip(String ptTip) {
        this.ptTip = ptTip;
    }
}