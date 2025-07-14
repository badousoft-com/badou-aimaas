const getters = {
    sidebar: state => state.app.sidebar,
    device: state => state.app.device,
    token: state => state.user.token,
    userInfo: state => state.user.userInfo,
    avatar: state => state.user.userInfo.avatar,
    name: state => state.user.userInfo.name,
    // 获取面板id
    panelId: state => state.user.userInfo.panelId,
    // 获取首页地址
    homeUrl: state => state.user.userInfo.homeUrl,
    // 获取当前登录用户角色
    roleCode: state => state.user.userInfo.roleCode,
    // 获取当前登录用户可选角色列表
    roleCodeList: state => state.user.userInfo.roleCodeList || [],
    permissionRoutes: state => {
        // A系统没有退出就直接黏贴跳转B系统，可能会因为菜单数目不一致(选中菜单下角标是有做缓存处理的)而报错
        try {
            state.permission.routes[state.permission.routesActiveIndex].isActive = true
        } catch (e) {
            // 出错时默认使用第一个菜单
            state.permission.routes[0].isActive = true
            console.log('当前菜单数目发生变化，取消菜单的默认选中')
        }
        return state.permission.routes
    },
    routesActiveIndex: state => state.permission.routesActiveIndex,
    permissionButtons: state => state.permission.buttons,
    settingTitle: state => state.settings.projectSetting.title,
    settingHeadBgImg: state => state.settings.projectSetting.headBgImg,
    settingLoginBgImg: state => state.settings.projectSetting.loginBgImg,
    leftMenuBgImg: state => state.settings.projectSetting.leftMenuBgImg,
    settingLogoImg: state => state.settings.projectSetting.logoImg,
    settingLogo: state => state.settings.projectSetting.logo,
    // 获取表单列表
    formColumnNum: state => state.settings.projectSetting.formColumnNum,
    hasSettingStatus: state => state.settings.hasSettingStatus,
    location: state => state.settings.location,
    // 页面缓存树
    keepAliveList: state => state.settings.keepAliveList,
    // 启用验证码状态
    isUseVerifyCode: state => state.settings.projectSetting.isUseVerifyCode,
    // 正在上传中的文件信息
    uploadingList: state => state.settings.uploadingList,
}
export default getters
