import { request } from '@/service/request'

// 申请应用
function New_Application (data) {
    return request(
        {
            url: '/project/project/projectappedit/addApplication',
            method: 'post',
            params: data
        },
        {
            withCredentials: true,
            headers: {
                'Content-Type': 'application/json'
            }
        })
}

export {
    New_Application,
}