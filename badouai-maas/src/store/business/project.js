const state = {
    projectName: '',
    projectId: '',
    projectCode: ''
}

const mutations = {
    // 更新选中的项目名
    UPDATE_PROJECT_NAME: (state, projectName) => {
        state.projectName = projectName
    },

    // 更新选中的项目ID
    UPDATE_PROJECT_ID: (state, projectId) => {
        state.projectId = projectId
    },

    // 更新选中的项目Code
    UPDATE_PROJECT_CODE: (state, projectCode) => {
        state.projectCode = projectCode
    }
}

export default {
    namespaced: true,
    state,
    mutations,
    // actions
}