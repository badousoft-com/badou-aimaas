import { Get_Attach_Type } from '@/service/attach/index.js'
import ContentVue from './index.vue'
import Vue from 'vue'
import GlobalConst from '@/service/global-const'

// 定义内容
let contentEl = null

/**
 * 获取实例
 * @param {Object} option 弹窗的配置数据
 * @returns 
 */
function getInstance (option) {
    let ContentComponent = Vue.extend(ContentVue)
    let _contentEl = new ContentComponent({
        propsData: {
            ...option,
        },
    }).$mount()
    return _contentEl
}

/**
 * 弹窗关闭事件
 * @param {String} name 弹窗标识名
 * @returns 
 */
function closeFun () {
    return new Promise((resolve, reject) => {
        if (!contentEl) return
        // 销毁实例
        contentEl.$destroy()
        // 删除添加的dom（避免不断弹窗，没有回收，内存溢出）
        document.body.removeChild(contentEl.$el)
        // 上一步是从dom移出位置，这里执行内存真正的清除
        contentEl = null
        resolve()
    })
}

/**
 * 判断是否允许预览
 *      预览目前除了pdf与图片外，其余类型都是借助另一个文件预览服务进行，限定部分支持部分文件类型
 * @param {Object} item 附件对象
 * @returns {Boolean} 是否允许预览
 */
function Allow_Preview (item) {
    if (!item) return
    let { isImg, icon } = item
    // 若为图片，默认支持预览
    if (isImg) return true
    // 每个文件都会获得对应的特殊类型图标，若依旧为默认图标则表示不支持使用预览
    return icon !== GlobalConst.icon.file
}

/**
 * 文件预览
 */
function Show_Preview () {
    // 获取函数参数
    let _args = Array.from(arguments)
    let _errorTip = `使用Show_Preview请传入有效参数，如Show_Preview(url,name),或者多图片Show_Preview([url1,url2])`
    if (_args.length === 0) {
        console.error(_errorTip)
        return
    }
    // 获取预览文件的url
    let _url = _args[0]
    switch (_url.constructor) {
        case String:
            // 面向所有文件类型
            let [ url, name ] = _args
            if (!(url && name)) {
                console.error(_errorTip)
                return
            }
            // 根据文件名获取附件类型
            let _type = Get_Attach_Type(name)
            contentEl = getInstance({
                // 附件类型
                type: _type,
                // 地址
                url,
                // 文件名
                name,
                // 关闭函数
                closeFun: () => closeFun()
            })
            // 插入弹窗dom
            contentEl && document.body.appendChild(contentEl.$el)
            break
        case Array:
            // 面向多图片
            //     {Array} urlArr 图片数组格式为[{url:'', name''}]
            //     {Number} index 在图片集中展示哪一张的下角标，可不传默认使用从第一张开始
            let [urlArr, index = 0] = _args
            // 获取弹窗实例
            contentEl = getInstance({
                // 附件类型
                type: 'imgs',
                // 地址
                url: urlArr,
                // 文件名
                index,
                // 关闭函数
                closeFun: () => closeFun()
            })
            // 插入弹窗dom
            contentEl && document.body.appendChild(contentEl.$el)
            break
        default:
            // 其他异常数据类型
            console.error(_errorTip)
    }
}

// import { Show_Preview } from '@/components/frame/Preview/index.js'
export {
    // 判断是否允许预览
    Allow_Preview,
    // 文件预览
    Show_Preview
}