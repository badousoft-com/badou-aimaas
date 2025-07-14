import defaultSettings from '@/settings'
import { Get_Project_Setting } from '@/api/frame/systemSetting'
import GlobalConst from '@/service/global-const'
import { Update_Version } from '@/service/module-storage'

const { showSettings, fixedHeader, sidebarLogo } = defaultSettings
const state = {
    theme: '',
    showSettings: showSettings,
    fixedHeader: fixedHeader,
    sidebarLogo: sidebarLogo,
    hasSettingStatus: false,
    projectSetting: {
        title: '',
        headBgImg: '',
        logoImg: '',
        loginBgImg: '',
        // 左侧菜单背景图
        leftMenuBgImg: '',
        // logo图片
        logo: '',
        // 标签页显示项目主名称
        rootTitle: '',
        // 标签页显示icon
        favicon: '',
        // 是否启用验证码
        isUseVerifyCode: false,
        // 表单列数
        formColumnNum: GlobalConst.form.columnNum,
        // 是否开启分片上传
        openFragmentUpload: false,
        // 分片文件大小
        fragmentFileSize: 10,
        // 达到分片上传上传阈值
        fragmentUploadLimit: 200,
        // 是否可切换角色
        openSwitchRole: true,
        // 系统编码
        systemCode: '',
    },
    // 面包屑地址
    location: [],
    // 缓存树
    keepAliveList: [],
    // 正在上传中的文件信息
    uploadingList: [],
}

const mutations = {
    CHANGE_SETTING: (state, { key, value }) => {
        if (state.hasOwnProperty(key)) {
            state[key] = value
        }
    },
    SET_PROJECT_SETTING: (state, obj) => {
        state.projectSetting = obj
    },
    SET_SETTING_STATUS: (state, status) => {
        state.hasSettingStatus = status
    },
    SET_LOCATION: (state, value) => {
        let locationList = []
        if (typeof value === 'string') {
            locationList = value.split(',').map(i => {
                return {
                    title: i
                }
            })
            state.location = locationList
        } else {
            state.location = value
        }
    },
    // 清空需要缓存页面的页面集合数据
    CLEAR_KEEPALIVE_LIST: (state) => {
        state.keepAliveList = []
    },
    /**
     * 将需要缓存的页面添加进缓存列表
     * @param {*} state
     * @param {*} value 页面路由name
     */
    SET_KEEPALIVE_LIST: (state, value) => {
        if (value) {
            // 清空树
            state.keepAliveList = []
            // 添加当前需要缓存的页面
            state.keepAliveList.push(value)
        }
    },
    // 添加上传中文件
    ADD_UPLOADING_LIST: (state, value) => {
        if (value) {
            state.uploadingList.push({
                done: 0,
                falls: [],
                ...value,
            })
        }
    },
    // 更改上传进度（已完成切片上传的文件数）
    UPDATE_UPLOADING_PROGRESS: (state, {id, value, failParams}) => {
        let uploadingList = state.uploadingList || []
        // 获取当前上传中文件信息索引
        let fileIndex = uploadingList.findIndex(f => f.id === id)
        if (~fileIndex) {
            // 获取上传中文件
            let fileItem = uploadingList[fileIndex]
            // 如果失败，添加失败的参数进fall
            if (failParams) fileItem.falls.push(failParams)
            // 完成数 = 执行完毕的数量 - 失败数量
            let successValue = value - fileItem.falls.length
            // 如果已完成数 == 总数，清楚此待完成文件
            if (successValue === fileItem.all) {
                state.uploadingList.splice(fileIndex, 1)
            } else {
                uploadingList[fileIndex].done = successValue || 0
            }
        }
    },
}

const actions = {
    changeSetting ({ commit }, data) {
        commit('CHANGE_SETTING', data)
    },
    getProjectSetting ({ commit }) {
        return new Promise((resolve, reject) => {
            Get_Project_Setting({}).then(res => {
                // res返回属性对应值存在null字符串，需要注意优先转化为对象null，避免后续相关逻辑判断异常
                Object.keys(res).forEach(i => res[i] = res[i] === 'null' ? null : res[i])
                // let textImg = 'https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1577285208258&di=e65f8fb2913d2f0fdbec286f168896ba&imgtype=0&src=http%3A%2F%2Fimg.mp.itc.cn%2Fupload%2F20170601%2Fb9f35bff9a4a4d3fafe735e09a45cff0_th.jpg'
                let data = {
                    title: res.systemTitle || '',
                    headBgImg: res.headBgImg || '',
                    leftMenuBgImg: res.leftMenuBgImg || '',
                    loginBgImg: res.loginBackground || '',
                    logoImg: res.loginLogo || '',
                    // logo图片
                    logo: res.logo || '',
                    // 标签页显示项目主名称
                    rootTitle: res.rootTitle || '',
                    // 标签页显示icon
                    favicon: res.favicon || '',
                    // 是否启用验证码
                    isUseVerifyCode: res.isVerify === 1,
                    // 表单列数
                    formColumnNum: res.formColumnNum || GlobalConst.form.columnNum,
                    // 是否开启分片上传
                    openFragmentUpload: res.openFragmentUpload === 1,
                    // 分片文件大小
                    fragmentFileSize: res.fragmentFileSize,
                    // 达到分片上传上传阈值
                    fragmentUploadLimit: res.fragmentUploadLimit,
                    // 是否可切换角色
                    openSwitchRole: res.openSwitchRole === 1,
                    // 系统编码
                    systemCode: res.systemCode,
                }
                // 项目版本号，目前主要用于检测本地模型混存是否需要更新
                Update_Version(res.systemVersion)
                commit('SET_SETTING_STATUS', true)
                commit('SET_PROJECT_SETTING', data)
                // 获取初始资源加载框
                let _initShadow = document.getElementById('init-shadow')
                // 当获取配置信息接口结束时，设置其透明度为0
                _initShadow.style.opacity = 0
                // 透明度过渡变化效果为2s,2秒后去掉资源加载框
                setTimeout(() => {
                    _initShadow.style.display = 'none'
                }, 1000)
                resolve(res)
            }).catch(err => {
                reject(err)
            })
        })
    },
}

export default {
    namespaced: true,
    state,
    mutations,
    actions
}
