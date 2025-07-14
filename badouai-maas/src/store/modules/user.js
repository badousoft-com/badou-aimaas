import { Login, Logout, Get_Info, Get_User_Routes } from '@/api/frame/user'
import { setToken, removeToken } from '@/service/auth'
import { resetRouter } from '@/router'
import { L_Storage } from '@/utils/storage'
import { S_Storage } from '@/utils/storage'
import CryptoUtils from '@/service/crypto-utils.js'

const state = {
    token: '',
    userInfo: {
        name: null, // 用户名
        avatar: null, // 头像
        homeUrl: null, // 该用户对应的首页地址
        panelId: null, // 面板id
        roleCode: null, // 用户当前角色编码
        roleCodeList: null, // 用户可切换角色列表
        // ...其他属性
    },
}

const mutations = {
    // 设置token
    SET_TOKEN: (state, token) => {
        state.token = token
        setToken(token) // 设置缓存token数据
    },
    // 清除token
    CLEAR_TOKEN: (state) => {
        state.token = ''
        removeToken() // 清空缓存中token数据
    },
    // 设置用户信息
    SET_USER_INFO: (state, data) => {
        state.userInfo = { ...state.userInfo, ...(data || {}) }
    },
    // 清除用户信息
    CLEAR_USER_INFO: (state) => {
        state.userInfo = {}
    }
}

const actions = {
    // user login
    login ({ commit }, userInfo) {
        return new Promise((resolve, reject) => {
            Login({ data: CryptoUtils.aesEncrypt(JSON.stringify(userInfo)) }).then(res => {
                let message = res ? res.message : ''
                // TODO: token逻辑在前端没有使用,使用的是后台直接操作cookies
                if (res.result) {
                    // 登录成功时才执行以下逻辑
                    commit('SET_TOKEN', message)
                }
                let result = {
                    status: res.result,
                    message: res.message || '登录异常'
                }
                resolve(result)
            }).catch(error => {
                reject(error)
            })
        })
    },

    // user logout
    logout ({ state, dispatch, commit, getters, rootGetters }) {
        return new Promise((resolve, reject) => {
            Logout(state.token).then(() => {
                dispatch('removeTokenUserFoot')
                resolve()
            }).catch(error => {
                reject(error)
            })
        })
    },

    // get user info
    getInfo ({ commit, state }) {
        return new Promise((resolve, reject) => {
            Get_Info().then(res => {
                // 即使获取用户信息接口200，也存在响应的用户信息的数据为{}的情况
                if (!(res && res.constructor === Object && Object.keys(res).length > 0)) {
                    reject('身份过期，请重新登录')
                    console.error('获取用户信息失败, 请重新登录')
                    return
                }
                // 更新用户信息
                commit('SET_USER_INFO', {
                    ...res,
                    avatar: res.headImg,
                    homeUrl: res.indexUrl || '/',
                    panelId: res.layoutId,
                    roleCodeList: res.roles || []
                })
                resolve(res)
            }).catch(error => {
                reject(error)
            })
        })
    },

    // 重置token与用户登录过的痕迹（如菜单记忆）
    removeTokenUserFoot ({ commit, dispatch }) {
        return new Promise(resolve => {
            dispatch('resetToken')
            // 注销清除session数据
            L_Storage.removeItem('routesActiveIndex')
            S_Storage.removeItem('projectCode')
            S_Storage.removeItem('projectId')
            S_Storage.removeItem('projectName')
            S_Storage.removeItem('CurrentProjectRole')
            // 设置活跃状态为0
            commit('permission/CLEAR_ROUTES_ACTIVE', null, { root: true })
            commit('settings/SET_SETTING_STATUS', false, { root: true })
            resolve()
        })
    },

    // 删除token事件
    resetToken ({ commit }) {
        return new Promise(resolve => {
            // 清除token后，路由守卫判断到则会重定向到登录页
            commit('CLEAR_TOKEN')
            // 清除用户数据，因为用户有可能在登录页登录其他账号
            commit('CLEAR_USER_INFO')
            // 清空已有路由
            resetRouter()
            resolve()
        })
    },

    // 获取用户权限路由菜单数据
    getUserRoutes ({ commit }) {
        return new Promise(resolve => {
            Get_User_Routes().then(response => {
                resolve(response)
            })
        })
    }
}

export default {
    namespaced: true,
    state,
    mutations,
    actions
}
