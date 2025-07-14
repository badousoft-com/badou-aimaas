/* 基础业务相关模块 */
import { Get_Data_By_Path, Variable_Replaced_String } from '@/utils/index.js'
import { dynamicForm } from '@/views/common/modelDesigner/fieldDic'
import { Is_External_Url } from '@/utils/validate/index'
import { Has_Value } from '@/utils'
import { Copy_To_Clip } from '@/utils/copy-clip'
import { Message } from 'element-ui'
import { finalRequest } from '@/service/request'
import { Get_Full_Url } from '@/service/url'
import { Has_Array_Data } from '@/utils/list'
import GlobalConst from '@/service/global-const'
import Vue from 'vue'
/**
 * 弹窗设置面板
 * @param {Array} selection 列表选中数据
 * @param {Object} option 配置项
 */
function Set_Panel_Dialog (selection, option) {
    // 获取列表页面所在作用域
    let listPageRef = this.listPageRef()
    let { type } = option || {}
    // 定义标识符
    let dialogId = 'layoutDialog'
    // 存储作用域：bd-module-list组件所在作用域
    // let _this = this
    let _params = {
        type,
        id: selection[0].id
    }
    // 动态调用弹窗
    this.$dialog.init({
        type: 'list',
        id: dialogId,
        title: '设置【' + selection[0].name + '】默认面板',
        height: '430px',
        isSelection: true,
        optionResPath: 'Rows',
        fieldList: [
            { name: 'name', label: '名称' },
            { name: 'code', label: '编码' },
            { name: 'isDefaultDesc', label: '当前是否启用' },
            { name: 'remark', label: '备注' }
        ],
        // 列表数据请求地址
        url: `${this.BASEURL}/panel/layout/layoutlist/panelListJSON.do`,
        // 列表数据请求参数
        params: _params,
        // 是否一次请求所有数据
        isLoadAll: true,
        // 是否展示分页
        showPagination: false,
        handlerList: [
            {
                name: '取消',
                icon: 'cancel',
                type: 'danger',
                click: function (btnObj) {
                    this.$dialog.close()
                }
            }, {
                name: '保存',
                icon: 'save',
                type: 'primary',
                loading: false,
                click: function (btnObj) {
                    let selection = this.getDialogConObj(dialogId, 1).selection
                    if (selection.length !== 1) {
                        this.$message.warning('请选择一条数据!')
                        return
                    } else if (selection.length !== 1) {
                        this.$message.warning('最多选择一条数据')
                        return
                    }
                    // 定义设置面板参数
                    let _panelParams = JSON.parse(JSON.stringify(_params))
                    // 更新参数
                    _panelParams.panelId = selection[0].id
                    _panelParams.groupId = _panelParams.id
                    this.post(`${this.BASEURL}/panel/grouppanel/grouppanelsave/setDefultPanel.do`, _panelParams).then(res => {
                        if (res.hasOk) {
                            // 关闭弹窗表单
                            this.$dialog.close()
                            // 更新列表页面数据
                            listPageRef.init()
                            this.$message.success('操作成功')
                        } else {
                            this.$message.error('操作失败')
                        }
                    })
                }
            }
        ]
    })
}

/**
 * 根据钻取信息对应进行跳转
 * @param {Object} drillFieldObj 含有钻取相关属性的字段对象
 * @param {Object} valueObj 字段值对象
 */
function Jump_Drill_Url (drillFieldObj, valueObj) {
    // 钻取的地址配置时有两种情况：
    //     1. 不带变量：https://www.baidu.com?name=小明
    //     2. 带变量：https:www.baidu.com?name=${name},这里的name需要获取表单下的name属性值替换这里
    let {
        drillUrl, // 钻取路径URL
        drillUrlOpenType, // URL打开类型：0-当前跳转；1-新窗口；2-弹窗
        // drillPageTitle, // 钻取模块标题
    } = drillFieldObj
    if (!drillUrl) return
    // 若钻取地址含有变量则转化为对应值
    drillUrl = Variable_Replaced_String(drillUrl, valueObj)
    // 判断钻取地址是否为外部第三方链接
    switch (drillUrlOpenType) {
        case 1: // 新窗口
            if (Is_External_Url(drillUrl)) {
                // 第三方链接，则直接新窗口打开
                window.open(drillUrl)
            } else {
                // 获取当前域名+跳转地址
                let _url = location.href.slice(0, location.href.indexOf('#') + 1) + drillUrl
                // 去除重复的/符号
                _url = _url.replace(/[^\:]\/\//g, '/')
                // 打开新窗口
                window.open(_url)
            }
            break
        case 0: // 当前页跳转
        default:
            // 弹窗中可能出现钻取，所以跳转前先尽可能关闭弹窗
            try {
                this.$dialog?.close()
            } catch (e) {
                console.log(e)
            }
            if (Is_External_Url(drillUrl)) {
                window.location.href = drillUrl
            } else {
                // 钻取地址为当前项目页面地址时
                this.$router.push({
                    path: drillUrl,
                    query: {
                        // 最好转化，单纯传对象时第一次有效但页面刷新之后会失效
                        // row: JSON.stringify(row)
                    }
                })
            }
    }
}

/**
 * 根据传入值返回图标对象
 * @param {String, Object} icon 图标相关信息
 * @returns 图标对象
 */
function setIconObj (icon) {
    if (!icon) return
    if (typeof icon === 'string') return { name: icon }
    if (icon.constructor === Object) return icon
    return null
}

/**
 * 格式化渲染字段
 * @param {*} h 渲染函数createElement
 * @param {*} fieldSetting 字段的自定义配置项
 * @param {*} drillUrl 钻取地址
 * @param {*} cellValue 字段值
 * @param {*} drillFun 钻取函数
 * @param {Object} fieldObj 字段对象
 * @param {Object} row 行数据
 * @returns
 */
function Render_Field (h, fieldSetting, drillUrl, cellValue, drillFun, fieldObj, row) {
    // 定义图标对象
    let iconObj = null
    // 定义字段样式类
    let valueClass = null
    // 定义字段样式
    let styleObj = null
    // 定义数据字典文本对象
    let descObj = null
    // 判断是否存在自定义配置
    if (fieldSetting) {
        // 获取数据字典文本配置项对象（数据字典字段可以配置customOption）
        descObj = getDescObj()
        // 更新-图标对象数据
        iconObj = setIconObj(fieldSetting.icon || descObj.icon)
        // 更新-字段样式类
        valueClass = fieldSetting.valueClass || descObj.class
        // 更新-字段样式
        styleObj = fieldSetting.style || descObj.style
    }
    // 使用了数据字典的字段xx在列表上会使用xxxDesc字段进行展示,配置customOptions配置数据字典文本的展示时
    //     是在xxx字段上面进行配置的
    // 获取数据字典文本对象
    function getDescObj () {
        // 判断当前字段是否有自定义配置数据字典的展示
        if (fieldSetting?.desc) {
            // 获取当前字段键名，期望为xxxDesc
            let fieldDescName = fieldObj?.name
            // 获取原字段键名，去除Desc，返回xxx字段键名
            let fieldName = fieldDescName?.match(/(.*)Desc/)?.[1]
            if (fieldName) {
                // 定义获取字段的数据字典值
                let value = row[fieldName]
                if (Has_Value(value)) {
                    // fieldSetting.desc的配置数据格式为：{
                    //     "0": {class,icon},
                    //     "1": {class,icon}
                    // }
                    // 根据值的类型获取相关配置
                    // value值可能为int类型，使用[value + '']转字符串
                    return fieldSetting.desc[value + ''] || {}
                }
                return {}
            }
            return {}
        }
        return {}
    }
    return h(
        'span',
        {
            'class': [
                // 优先使用自定义配置中的样式类
                // 其次有钻取则使用主题样式类
                valueClass || (drillUrl ? 'primaryC' : ''),
                // 有钻取则添加手势类
                drillUrl ? 'pointer' : ''
            ],
            style: styleObj || {},
            on: {
                click: (event) => {
                    // 钻取跳转事件
                    if (drillUrl) {
                        drillFun.call(this, {event})
                    }
                }
            }
        },
        [
            h('bd-icon', {
                'class': {
                    'none': !iconObj
                },
                props: {
                    size: 'small',
                    ...(iconObj || {})
                }
            }),
            h('span', cellValue)
        ]
    )
}

// 复制
function Copy_Serve () {
    Copy_To_Clip.apply(this, arguments).then(() => {
        Message.success(`复制路径成功`)
    })
}

/**
 * 统一options的键（统一修改为id、text）；模型设计器可以配置取options数据中的那些字段作为id与text，没配置的默认id/text
 * @param {Array} options 可选options数据
 * @param {Object} fieldField 字段对象
 * @returns 修改后的options数据
 */
function Change_Option_Key (options, fieldField) {
    if (!Has_Array_Data(options)) return options
    let _options = [...options]
    let { valueFieldIdSrc, valueFieldTextSrc } = fieldField
    let wrongTip = 'noValue'
    _options.forEach(i => {
        // 更新键值
        Vue.set(i, GlobalConst.dicOption.idName, i[valueFieldIdSrc] || i[GlobalConst.dicOption.idName])
        Vue.set(i, GlobalConst.dicOption.textName, i[valueFieldTextSrc] || i[GlobalConst.dicOption.textName] || wrongTip)
    })
    return _options
}

/**
 * 获取字段下的options数据源（异步请求）
 * @param {Array} fieldList 字段数组
 * @param {Object} allDicObj 数据字典集合对象
 */
function Get_Async_Options_OnEachItem (fieldList, allDicObj) {
    // 问题：
    //     以下options表示： 选项数据源（例如下拉的数据源）
    //     对存在options数据的场景，必须满足options值存在时，value设置选中才能生效
    //     如果options需要请求获取
    //         分别异步async请求时无法确认请求状态何时结束（不能确保进入页面时options有数据）
    //         如果使用await等待的话可以确认结束状态，进入页面时一定有数据可选，但同步使耗时很长不合理
    // 方案：
    //     遍历所有表单字段，将options需要请求的同时异步请求，结合Promise.allSettled确保监听所有请求结束
    //     接下来再对应更新回字段的options属性数据
    return new Promise((resolve, reject) => {
        try {
            // 定义承诺列表
            let promiseFieldList = []
            // 定义请求状态语，占位符
            let ajaxStatusTip = 'noAjax'
            // 根据表单数据获取表单对象返回{'表单项键':'表单项值'}
            let fieldValueObj = fieldList.reduce((valueObj, i) => {
                valueObj[i.name] = i.value
                return valueObj
            }, {})
            fieldList.forEach(i => {
                // 在设计器中：切换编辑类型时可能没有清除字段数据
                // 这里根据字段的编辑类型匹配设计器下的特殊展示字段，其中若不含有dataSource，则表示字段编辑类型为非option类型，则这里清空3个相关字段的数据
                if (!dynamicForm.editType.data[i.type]?.includes('dataSource')) {
                    i.mdCode = null
                    i.fun = null
                    i.dic = null
                }
                // 配置options数据有三种（优先级：模型编码 > 自定义 > 数据字典）
                //     1. dic: 数据字典，此处不用请求，配合allDicObj获取对应数据字典
                //     2. mdCode： 模型编码（填写模型编码，Bean）
                //     3. fun: 自定义url (不用填编码，填写Bean方法，也就是接口地址)
                let { mdCode, fun, dic } = i
                let { autoPagination } = i.customOptions ? JSON.parse(i.customOptions) : {}
                // mdCode与dic同时存在时，以mdCode为主，所以判断语句mdCode要放置在dic前面
                if (mdCode) {
                    promiseFieldList.push(allDicObj?.[mdCode])
                // 如果设置了自动分页，则请求在 bd-select 组件内部
                // TODO: 这里后面测试下，确认后可以与else中进行合并
                } else if (autoPagination) {
                    promiseFieldList.push(ajaxStatusTip)
                } else if (fun) {
                    // fun: 使用自定义接口获取options
                    // 将fun地址中的变量名转成对应变量
                    fun = Variable_Replaced_String(fun, fieldValueObj)
                    // 判断地址是否含有域名，没有则补齐
                    fun = Get_Full_Url(fun)
                    let params = {}
                    promiseFieldList.push(finalRequest({ url: fun, params, method: 'get' }))
                } else if (dic) {
                    // dic：使用数据字典
                    promiseFieldList.push(allDicObj?.[dic])
                } else {
                    // 非options的字段，直接添加占位符
                    promiseFieldList.push(ajaxStatusTip)
                }
            })
            Promise.allSettled(promiseFieldList).then(res => {
                fieldList.forEach((i, index) => {
                    // 获取Promise返回值
                    let itemResult = res[index]?.value || []
                    if (itemResult !== ajaxStatusTip) {
                        // 判断是否存在options值路径，存在则更新获取最终的options值
                        if (i.optionResPath && itemResult) {
                            itemResult = Get_Data_By_Path(itemResult, i.optionResPath)
                        }
                        if (itemResult && itemResult.constructor === Object) {
                            console.error(`留意下字段${i.name}获取到的options值，当前为Object,期望为Array，检查是否忘记配置值路径optionResPath，指向真正的数据`)
                            console.log(JSON.stringify(itemResult))
                        }
                        // 更新options字段
                        Vue.set(i, 'options', Change_Option_Key(itemResult || [], i))
                    }
                })
                resolve(fieldList)
            })
        } catch (e) {
            console.error(`获取options数据函数getAsyncOptionsOnEachItem异常：错误捕获为${JSON.stringify(e)}`)
            resolve(fieldList)
        }
    })
}

/**
 * 获取表单项元素在一行中的占比份数（总共24份）
 * @param {Object} itemObj 字段对象数据
 * @param {Object} groupObj 字段所在的组别对象
 * @param {Number} formColumnNum 表单默认的列数（24/列数 = 每项所占份数）
 * @param {Object} setting 配置项
 *      @param {Boolean} ignoreColumnPer 是否忽略表单子项的配置占比数，默认否
 * @returns {Number} 占比份数
 */
function Get_Column_Span (itemObj, groupObj, formColumnNum, setting) {
    // isBlock: {Boolean}是否块级展示，label与内容各占一行
    // isOneLine: {Boolean} label跟内容是否独占一行（此时label与内容同在一行）
    // columnPer [Number] 表单子模块子项在一行中所占份数（共24份）
    let { isBlock, isOneLine, columnPer } = itemObj
    // groupColumnNum [Number] 表单子模块（一个表单支持多个模块）通用一行列数
    let { columnNum: groupColumnNum } = groupObj
    let {
        ignoreColumnPer = false,
    } = setting || {}
    // 定义返回结果
    let span = 0
    if (isBlock) {
        // 若为块级，则模块位置铺满一行
        // columnPer有值则使用该值（通过样式控制，不在当前处理），无值则默认铺满
        span = Has_Value(columnPer) ? columnPer : GlobalConst.form.gridNum
    } else if (isOneLine) {
        // 若设置标签与内容同一行，同样只管理位置，确保该行只会有其一个子项，元素宽度由columnPer控制
        // columnPer有值则使用该值，无值默认铺满
        span = columnPer || GlobalConst.form.gridNum
    } else {
        // 以下元素非独占一行
        // 优先级： 各模块表单子项配置 > 各模块表单配置 > 表单配置
        if (!ignoreColumnPer && columnPer) {
            span = columnPer
        } else if (groupColumnNum) {
            span = GlobalConst.form.gridNum / parseInt(groupColumnNum)
        } else {
            span = GlobalConst.form.gridNum / parseInt(formColumnNum)
        }
    }
    // 向下取整
    return Math.floor(span)
}

// import { Copy_Serve } from '@/service/base-service'

export {
    // 弹窗设置面板
    Set_Panel_Dialog,
    // 根据钻取信息对应进行跳转
    Jump_Drill_Url,
    // 字段渲染与事件
    Render_Field,
    // 服务类的copy
    Copy_Serve,
    // 获取字段下的options数据源（异步请求）
    Get_Async_Options_OnEachItem,
    // 获取表单项元素在一行中的占比份数（总共24份）
    Get_Column_Span,
}