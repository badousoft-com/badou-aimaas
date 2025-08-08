package  com.badou.project.model.modelfile.web.form;

import java.util.Date;
import java.sql.Timestamp;

import com.badou.brms.base.support.struts.form.BaseStrutsEntityForm;
import com.badou.project.model.modelfile.model.modelEntity;

/**
 * @author badousoft
 * @date 2024-04-01 17:57:13.932
 * @todo 模型文件管理form
 */
public class modelForm extends BaseStrutsEntityForm<modelEntity> {

	/**
     * 最后更新时间
     */
    protected Date  lastUpdate;
	/**
     * 文件名字
     */
    protected String  name;
	/**
     * 文件大小
     */
    protected String  fileSize;
	/**
     * 控制权限
     */
    protected String  roleAuth;

        /**
     *  获取最后更新时间
     */
    public Date getLastUpdate() {
        return lastUpdate;
    }

	/**
     *  设置最后更新时间
     */
    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
    /**
     *  获取文件名字
     */
    public String getName() {
        return name;
    }

	/**
     *  设置文件名字
     */
    public void setName(String name) {
        this.name = name;
    }
    /**
     *  获取文件大小
     */
    public String getFileSize() {
        return fileSize;
    }

	/**
     *  设置文件大小
     */
    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }
    /**
     *  获取控制权限
     */
    public String getRoleAuth() {
        return roleAuth;
    }

	/**
     *  设置控制权限
     */
    public void setRoleAuth(String roleAuth) {
        this.roleAuth = roleAuth;
    }
}