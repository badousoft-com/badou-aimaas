export default function returnSrc (id, type = '') {
    if (!id || id === 'null') return ''
    let url = ''
    let mainSrc = `${process.env.VUE_APP_BASE_API}/cfg/basedefineedit/downloadImgCache.do?tempId=${id}`
    switch (type) {
        // 返回img:src地址
        case 'favicon':
        case 'img':
            url = mainSrc
            break
        // 作为背景样式使用,返回背景url地址,用于style{background: 返回值}
        case 'bg':
            url = `url(${mainSrc})`
            break
        case 'url':
            url = id
            break
        // 默认使用图片地址
        default:
            url = mainSrc
    }
    return url
}