// 全局框架常量
const noValText = '--'
const noDataTip = '暂无数据'
let GlobalConst = {
    tip: {
        // 模块无数据时的文本提示
        noDataTip: noDataTip
    },
    // 历史记录
    history: {
        // 最大展示长度
        maxLength: 50,
    },
    // 移动端设置
    mobile: {
        // 列表高度
        tableHeight: 500,
        // 表单展示的列数
        columnNum: 1,
    },
    // 连接符
    separator: ',',
    // 地址本值分隔符
    addressbookValueSeparator: '!@!',
    // 定义分辨率连接符
    ratioSeparator: '*',
    form: {
        // 新增表单的临时id标识
        newId: 'add',
        // 表单标题（无数据时使用）
        title: '未命名表单',
        // 表单标签（无数据时使用）
        label: '未命名',
        // 标签宽度
        labelWidth: '110px',
        // 表单展示的列数.2021-4-15从3变更为2
        columnNum: 2,
        // 预输入文本提示
        placeholder: {
            // 输入框提示
            text: '请输入',
            // 下拉框提示
            select: '请选择'
        },
        // 值分隔符：用于数组提交接口前拼接符
        valueSeparator: ',',
        // 展示分隔符，用于同级数据在页面上展示的分割符
        showSeparator: ';',
        // 字段的组合终止字段名称,A字段配置endFieldName：B,则A与B将结合展示，展示为起始与终止
        endFieldName: 'endFieldName',
        // 范围分隔符，两个字段组合展示时值合并拼接符
        rangeSeparator: ',',
        // 一行拆分格子数（24等份）
        gridNum: 24,
        // 表单组默认名称
        groupName: '基本信息',
        // 除开基础组名后，下次组名的基础名称
        nextGroupName: '分组',
        // 表单组别默认图标
        groupIcon: 'point-fill',
        // 表单字段值默认路径 formItemVal['value']获取到最终值
        valuePath: 'value',
        // 目前用于单选框，复选框在编辑场景下无数据时的展示
        noDataTip: '当前暂无可选数据',
        // 其他自定义输入项的键名
        hasOtherOptionKeyName: 'hasOther'
    },
    // 表单查看页面无数据时展示文本
    view: {
        // 查看页面字段无数据时展示文本
        value: noValText
    },
    // 弹窗
    dialog: {
        // 标题
        title: '未命名模块',
        // 宽度
        width: '70%',
        // 高度
        height: '88%',
        // 距离顶部距离
        marginTop: '15vh'
    },
    // 流程
    flow: {
        // 按钮类型，1：弹窗上以tab的形式展现，0：在底部展示
        // 默认使用底部展示
        buttonType: 0,
        // 点击路由按钮是否需要弹窗标识符，!=1 时为不需要
        isNeedDialogKey: 'izNeedOpinion'
    },
    panel: {
        // 块之间的间距
        spaceSize: '7.5px',
        // 面板内容所对应的key
        contentKey: 'comboContentData',
    },
    // 文件
    file: {
        maxSize: '10MB'
    },
    // 文本域
    textarea: {
        // 行数
        rows: 5,
        // 最大行数
        maxRows: 999999
    },
    // 下拉
    select: {
        // 分页每页数目（作用于开启分页搜索的表单）
        perPageSize: 10
    },
    // 级联框
    cascader: {
        // 级别间分割符
        separator: '-',
        // 展示的分隔符
        showSeparator: '/',
        id: 'value',
        text: 'label',
        children: 'children',
    },
    // 地址级联
    address: {
        // 关联字段
        relatedField: ['province', 'city', 'area'],
        // 分割符
        separator: '-'
    },
    table: {
        // 序号列标签
        indexLabel: '序号',
        // 序号列宽度
        indexWidth: '50',
        // 可选框所占宽度
        selectionWidth: '55',
        // 列最小宽度
        minWidth: '145',
        // 表格默认最小高度
        minHeight: '100px',
        // 表格默认高度
        height: '100px',
        // 表格是否启用斑马纹
        stripe: true,
        // 表格无数据时展示文本
        value: noValText
    },
    pagination: {
        no: 1,
        // 单页请求数据量
        size: 10,
        // 页码字段名
        noName: 'pageNo',
        // 单页数目字段名
        sizeName: 'perPageSize',
        // 下拉选择单页请求数的类型总数，[10,20,30,40]这为4种
        sizeSelectNum: 4,
        // 可选单页请求数的增幅，[5,15,25,35]增幅为10
        sizeAddNum: 30,
        // (暂不使用)可选单页请求数据量数组：目前会根据size，sizeSelectNum，sizeAddNum动态计算返回
        sizes: [30, 40, 50, 60],
        // 一次请求完所有数据默认请求数
        maxSizeNum: 100000,
        // 目前作用于弹窗页面列表，设置允许放置的页码按钮的最大值
        // api规定为大于等于 5 且小于等于 21 的奇数
        maxBtnCount: 5

    },
    button: {
        // 参数排序key值
        sortKey: 'priority',
        // 按钮默认展示顺序
        sortIndex: 9999999999,
        // 列表按钮默认属性（根据id）
        listAttrs: {
            add: { name: '新增', icon: 'add', priority: 5, type: 'primary' },
            edit: { name: '修改', icon: 'edit', priority: 10, type: 'warning' },
            view: { name: '查看', icon: 'view', priority: 15, type: 'primary' },
            import: { name: '导入', icon: 'import', priority: 25, type: 'primary' },
            export: { name: '导出', icon: 'export', priority: 30, type: 'primary' },
            delete: { name: '删除', icon: 'delete', priority: 40, type: 'danger' },
        },
        // 模型编辑页按钮默认属性（根据id）
        editAttrs: {
            back: { name: '返回', icon: 'back', priority: 5, type: 'danger' },
            close: { name: '关闭', icon: 'back', priority: 5, type: 'danger' },
            save: { name: '保存', icon: 'save', priority: 10, type: 'primary' },
        }
    },
    // 滚动条
    scrollbar: {
        width: '18px'
    },
    // 数据字典相关
    dic: {
        // 默认请求数据字典的地址接口
        url: '/common/commoninterface/listDicJSON',
        // 数据字典编码键名
        codeName: 'dicCode',
    },
    // 搜索组件
    searchBar: {
        // 搜索组件块触发方式
        trigger: 'hover',
        // 搜索条件组件默认展示顺序
        sortIndex: 9999999999,
        // 无数据时文本提示
        noDataTip: noDataTip,
        // defaultSearchParams搜索默认处理成的类型
        //     精准搜索：exact-match
        //     模糊搜索：text-query
        type: 'exact-match'
    },
    // 无数据组件
    noData: {
        // 无数据时文本提示
        text: noDataTip
    },
    // 页面底部操作区域
    footer: {
        // 底部模块高度
        height: '44px'
    },
    // 富文本
    richText: {
        // 高度
        height: 200,
        // 工具栏默认展示工具项
        toolbars: ['fullscreen', 'source', '|', 'undo', 'redo', '|',
            'bold', 'italic', 'underline', 'fontborder', 'strikethrough', 'superscript', 'subscript', 'removeformat', 'formatmatch', 'autotypeset', 'blockquote', 'pasteplain', '|', 'forecolor', 'backcolor', 'insertorderedlist', 'insertunorderedlist', 'selectall', 'cleardoc', '|',
            'rowspacingtop', 'rowspacingbottom', 'lineheight', '|',
            'customstyle', 'paragraph', 'fontfamily', 'fontsize', '|',
            'directionalityltr', 'directionalityrtl', 'indent', '|',
            'justifyleft', 'justifycenter', 'justifyright', 'justifyjustify', '|', 'touppercase', 'tolowercase', '|',
            'link', 'unlink', 'anchor', '|', 'imagenone', 'imageleft', 'imageright', 'imagecenter', '|',
            'simpleupload', 'insertimage', 'emotion', 'scrawl', 'insertvideo', 'music', 'attachment', 'map', 'gmap', 'insertframe', 'insertcode', 'pagebreak', 'template', 'background', '|',
            'horizontal', 'date', 'time', 'spechars', 'snapscreen', 'wordimage', '|',
            'inserttable', 'deletetable', 'insertparagraphbeforetable', 'insertrow', 'deleterow', 'insertcol', 'deletecol', 'mergecells', 'mergeright', 'mergedown', 'splittocells', 'splittorows', 'splittocols', 'charts', '|',
            'print', 'preview', 'searchreplace', 'drafts', 'help'
        ]
    },
    // 提醒信息模块
    message: {
        tip: {
            // 成功提示文本
            success: '操作成功',
            // 失败提示文本
            fail: '操作失败'
        }
    },
    // 图标模块
    icon: {
        // 默认图标前缀
        prefix: '#bd-',
        // 默认展示图标名称
        default: 'default',
        // 默认的文件图标
        file: 'attachLogo',
    },
    // TODO: 检查有无使用的地方
    theme: {
        // disabled颜色设置：原有色的60%弱化展示
        disableOpacity: 0.6
    },
    // 常见图片类型
    imgTypeList: [ '.bmp', '.jpg', '.jpeg', '.JPG', '.JPEG', '.png', '.tif', '.gif', '.pcx', '.tga', '.exif', '.fpx', '.svg', '.psd', '.cdr', '.pcd', '.dxf', '.ufo', '.eps', '.ai', '.raw', '.WMF', '.webp', '.avif' ],
    // 常见word类型
    wordTypeList: [ '.doc', '.docx', '.wps', '.wpt', '.dot', '.dotx', '.docm', '.dotm' ],
    // 常见ppt类型
    pptTypeList: [ '.ppt', '.pot', '.pps', '.pptx', '.pptm', '.potx', '.potm', '.ppsx', '.ppsm' ],
    // 常见excle类型
    excleTypeList: [ '.csv', '.xls', '.xlsx', '.et', '.ett', '.xlt', '.xlsm' ],
    // 常见pdf类型
    pdfTypeList: [ '.pdf' ],
    // 常见视频类型
    videoTypeList: [ '.mp4', '.avi', '.mov', '.rmvb', '.rm', '.ASF' ],
    // 常见音频类型
    audioTypeList: [ '.mp3', '.ogg', '.wma', '.amr', '.avi', '.rm', '.rmvb', '.flv', '.mpg', '.mov', '.mkv', '.cd', '.wave', '.mpeg', '.mpeg4', '.midi', '.wma', '.vqf', '.ape' ],
    // CAD文件
    CADTypeList: [ '.dwg', '.dxf', '.dws', '.dwt' ],
    // 压缩包类型
    rarTypeList: [ '.rar', '.7z', '.zip', '.gz', '.gzip', '.jar' ],
    // 纯文本类型
    plainTextTypeList: [ '.txt', '.java', '.php', '.py', '.md', '.js', '.css' ],

    // Popover 弹出框:与 Tooltip 很类似
    popover: {
        // 显示延迟
        openDelay: 300,
        // 关闭延迟
        closeDelay: 50
    },
    // 类似数据字典项属性规定
    dicOption: {
        // 值-键名
        idName: 'id',
        // 文本-键名
        textName: 'text',
        // TODO:待删，兼容旧版数据
        idNameOld: 'value',
        // TODO:待删，兼容旧版数据
        textNameOld: 'label'
    },
    // 开启请求前参数加密逻辑
    openSafeRequestParams: true,
    // 模型数据存储的阙值-默认单位MB
    moduleStorageMax: 3.5,
    // token过期时是否弹窗提示： true-提示是否重新登录，可选择继续留在过期页面；false-检查过期直接跳登录页
    isAskWhenOutToken: false,
    // 流程表单编辑时是否开启流程意见。开启后可在编辑时填写意见，选择下一环节处理人时也会有填写意见；关闭后则只会在选择下一环节处理人时才填写意见
    openOpinionInFlowVerify: false,
}
export default GlobalConst