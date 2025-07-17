// import defaultSettings from '@/settings'
// 获取项目默认取的系统名称-八斗开发框架4.1
// let title = defaultSettings.title || store.state.settings.projectSetting.rootTitle
let title = '标题'
export default function getPageTitle (pageTitle) {
    if (pageTitle) {
        return `${pageTitle} - ${title}`
    }
    return `${title}`
}
