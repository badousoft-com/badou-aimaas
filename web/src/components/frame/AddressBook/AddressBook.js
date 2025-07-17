import Vue from 'vue'
const Instance = Vue.extend(require('./index.vue').default)

/**
 * 返回默认的 options 配置
 */
function getDefaultConfig() {
    return {
        chooseValue: '', // 地址本已选数据，一般传入格式为'20!@!超级管理员!@!U00001, 20!@!系统管理员!@!ff80808153c624e60153c62d40670002'
        addressTypes: '', // 地址本类型，优先级大于type，值为'x-x-x-x,y-y-y-y'
        title: '地址本选择', // 弹窗标题
        type: '', // 需要显示的地址本，不填的时候会加载全部，多个使用分号分割 如：2;600
        indexType: '', // 默认展开的地址本,根据type的值来填，不填默认展开第一个
        userPermission: false, // 权限校验，默认关闭
        selectType: null, // 确定数据范围，根据地址本的注册类有所区别，默认地址本注册类（BtAddressBookRegister）
        singleChoose: false, // 单选或多选 默认为多选
        includeParent: 1, // 点击节点时，是否包含父节点在待选框 1包含 0不包含 默认包含
        treeDefaultParams: null, // 树的默认查询参数，数据格式参考搜索组件的数据格式
        optionDefaultParams: null, // 选项的默认查询参数，数据格式参考搜索组件的数据格式
        registerDicCode: null, // 自定义注册bean的名称的数据字典值（如果需要自定义地址本注册类，模仿BtAddressBookRegister，并将改bean的名字保存在数据字典ADDRESS_BOOK_REGISTER中）
        treeLevels: null, // 待选框默认显示的层级 -1:全部  0:当前节点（默认） "2;3":显示2 3级
    }
}

let addressbook = null
function init (config) {
    return new Promise((resolve, reject) => {
        addressbook = new Instance({
            data: {
                config: Object.assign({}, getDefaultConfig(), config),
                callback: (data) => {
                    resolve(data)
                },
            }
        }).$mount()
        // TODO:缺少了关闭弹窗时，remove掉新建的标签，这样随着每次打开都会新增一个，会内存溢出
        document.querySelector('body').appendChild(addressbook.$el)
    })
}
function close () {
    if (!addressbook) return
    let vm = addressbook.$mount()
    let addressbookEl = vm.$el
    document.body.removeChild(addressbookEl)
    addressbook.$destroy()
    vm = null
}

export default init
export {
    init,
    close as closeAddressBook
}