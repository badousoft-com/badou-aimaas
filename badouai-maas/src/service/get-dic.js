import { Get_Dic_List } from '@/api/frame/dic'
import GlobalConst from '@/service/global-const'

export default function getDicList (url, params) {
    return new Promise((resolve, reject) => {
        if (!(url || params)) {
            resolve([])
        } else {
            // 以下可能为5种传入情况
            //     1. (url, dicCode)
            //     2. (url, { dicCode })
            //     3. (dicCode)
            //     4. ({ dicCode })
            //     5. ({ url, params })

            // 定义请求地址
            let _url = null
            // 定义请求参数
            let _params = null
            // 判断请求地址是否为字符串，由此判断是情况123，还是情况4
            if (typeof url === 'string') {
                // 以下为情况123
                let _dicCodeName = GlobalConst.dic.codeName
                // 判断是否含有/
                if (~url.indexOf('/')) {
                    // 传入的url参数为真实的请求地址
                    // 接下来判断传入的参数params
                    if (!params) {
                        resolve([])
                        return
                    } else {
                        _url = url
                        // 判断参数是dicCode还是完整的参数对象
                        _params = typeof params === 'string' ? { [_dicCodeName]: params } : params
                    }
                } else {
                    // 传入的url参数是字符串且不带有/,则表示传入的是数据字典编码
                    _params = { [_dicCodeName]: url }
                }
            } else {
                // 传入的url不是字符串
                if ('url' in url) {
                    // 若含有url属性，则表示传入的第一个参数是完整请求对象（情况5），含有url与params属性
                    _url = url.url
                    _params = url.params
                } else {
                    // 当前为情况4，url传入的是请求参数完整对象
                    _params = url
                }
            }
            // 请求数据字典列表数据
            Get_Dic_List(_url, _params).then(res => {
                resolve(res || [])
            }).catch(err => {
                resolve([])
                console.error(`获取数据字典数据失败：`, err)
            })
        }
    })
}