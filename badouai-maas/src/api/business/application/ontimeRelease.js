import { request } from '@/service/request'

// 获取上一次定时发布的配置信息
function Get_Last_Application_Schedule_Config (data) {
    return request ({
        url: '/project/project/projectappscheduleedit/getLastAppScheduleConfig',
        method: 'get',
        params: data
    })
}

// 代码仓库定时发布
function Code_Ontime_Release (data) {
    return request (
        {
            url: '/project/project/projectappedit/onTimePub',
            method: 'post',
            params: data
        },
        {
            withCredentials: true,
            headers: {
                'Content-Type': 'application/json'
            }
        }
    )
}

// 暂停任务
function Pause_Schedule (data) {
    return request ({
        url: '/project/project/projectappscheduleedit/pauseSchedule',
        method: 'post',
        params: data
    })
}

// 重启任务
function Restart_Schedule (data) {
    return request ({
        url: '/project/project/projectappscheduleedit/restartSchedule',
        method: 'post',
        params: data
    })
}

export {
    Code_Ontime_Release,
    Pause_Schedule,
    Restart_Schedule,
    Get_Last_Application_Schedule_Config
}