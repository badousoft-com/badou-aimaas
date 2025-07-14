import { L_Storage } from '@/utils/storage'

const state = {
    sidebar: {
        opened: L_Storage.getItem('sidebarStatus') ? !!L_Storage.getItem('sidebarStatus') : false,
        withoutAnimation: false
    },
    device: 'desktop'
}

const mutations = {
    TOGGLE_SIDEBAR: state => {
        state.sidebar.opened = !state.sidebar.opened
        state.sidebar.withoutAnimation = false
        if (state.sidebar.opened) {
            L_Storage.setItem('sidebarStatus', 1)
        } else {
            L_Storage.setItem('sidebarStatus', 0)
        }
    },
    CLOSE_SIDEBAR: (state, withoutAnimation) => {
        L_Storage.setItem('sidebarStatus', 0)
        state.sidebar.opened = false
        state.sidebar.withoutAnimation = withoutAnimation
    },
    OPEN_SIDEBAR: (state, withoutAnimation) => {
        L_Storage.setItem('sidebarStatus', 1)
        state.sidebar.opened = true
        state.sidebar.withoutAnimation = withoutAnimation
    },
    TOGGLE_DEVICE: (state, device) => {
        state.device = device
    }
}

const actions = {
    toggleSideBar ({ commit }) {
        commit('TOGGLE_SIDEBAR')
    },
    closeSideBar ({ commit }, { withoutAnimation }) {
        commit('CLOSE_SIDEBAR', withoutAnimation)
    },
    openSider ({ commit }, { withoutAnimation }) {
        commit('OPEN_SIDEBAR', withoutAnimation)
    },
    toggleDevice ({ commit }, device) {
        commit('TOGGLE_DEVICE', device)
    }
}

export default {
    namespaced: true,
    state,
    mutations,
    actions
}
