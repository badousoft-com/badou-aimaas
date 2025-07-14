/**
 * 复制内容到黏贴板
 * ！！！！需要有手动事件触发，不能在初始生命周期中触发会无效
 * @param [String] message: 需要复制的信息
 * @return [Promise]
 * 使用方式：copyToClip('xxx需要复制的内容xxx').then(() => {
 *     复制成功之后的回调事件 do something you like
 * })
 */
function Copy_To_Clip (message) {
    return new Promise((resolve, reject) => {
        try {
            // 创建input元素，利用其内容进行复制进粘贴板
            // 创建textarea元素，可以支持复制html时保留换行符；input会默认不保留换行符
            let copyArea = document.createElement('textarea')
            // 设置input value属性值
            // copyArea.setAttribute("value", message)  // input标签使用
            copyArea.value = message  // textarea标签使用
            document.body.appendChild(copyArea)
            // 设置选中input默认值
            copyArea.select()
            // 复制选中内容
            document.execCommand('copy')
            // 复制成功，删除input元素
            document.body.removeChild(copyArea)
            resolve('ok')
        } catch (e) {
            console.log(`copyToClip函数使用报错：${e}`)
            reject()
        }
    })
}
// import { Copy_To_Clip } from '@/utils/copy-clip'
export {
    // 复制内容到黏贴板
    Copy_To_Clip,
    Copy_To_Clip as copy_To_Clip, // TODO:不推荐使用，待删除
}