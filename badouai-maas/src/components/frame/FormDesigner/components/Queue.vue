
<script>
import { Deep_Clone } from '@/utils/clone'
/**
 * 表单的操作事件队列类
 * 范围：面向编辑表单区域，表单项的新增/删除/顺序更换 + 组别的新增/删除，不包括表单项的数据变更
 * 属性：
 *      list: [Array]操作树
 *      unDoList: [Array]撤销树
 *      runItem: [Array]当前操作项:存储每一个表单项（表单项为Array，子集为每个属性具体值）
 *      currentFormData: [Array]页面展示项（也等同于操作树的最后一条数据，操作数数据为0时currentFormData为[]）
 * 规则：
 *      1. 撤销按钮可操作 = list.length >1; 重做按钮可操作 = unDoList.length > 0
 *          当操作树list存在数据时，表示当前可以执行撤销；当执行了撤销，撤销树unDoList有数据时表示可执行向前步骤，恢复上一步的操作
 *          撤销按钮为何是大于1,主要是初始进入页面的状态需要作为起点，且不再允许往后退，所以必然存在一条数据（初始状态）
 *      2. 每一次新的操作都加入操作树list
 *      3. 点击撤销按钮时，将list最新一条操作数据删除，并且将该条数据添加给unDoList，页面展示list剩余数据中的最新一条
 *      4. 当重做按钮可操作时，若此时执行新的操作添加list数据时，unDoList数据将重置为[]，表示当前为最新状态不存在向前步骤【类似生活常识撤销回退操作】
 */
class formQueue {
    constructor (list, unDoList, runItem, currentFormData) {
        // 操作树
        this.list = list
        // 撤销树
        this.unDoList = unDoList
        // 当前活跃（操作）项
        this.runItem = runItem
        // 当前展示（展示页面）项
        this.currentFormData = currentFormData
    }
    // 添加历史
    push (item) {
        this.list.push(item)
        // 每次添加历史都将清除重做列表，因为添加操作为最新操作，没有向前步骤
        this.unDoList = []
    }
    // 撤销操作
    undo () {
        // 获取最新操作步骤
        this.runItem = this.list.pop()
        let len = this.list.length
        // 展示项为去除最新操作后的下一项
        // !!!: currentFormData取出时必须使用深拷贝Deep_Clone，currentFormData是用于页面展示的，存在被更改的可能，使用浅拷贝将可能会影响到这里list项的数据
        this.currentFormData = len > 0 ? Deep_Clone(this.list[len - 1]) : []
        // 将最新项添加进撤销树
        this.unDoList.push(this.runItem)
    }
    // 重做操作
    redo () {
        // 获取最新一条撤销数据，准备恢复
        this.runItem = this.unDoList.pop()
        // 将最新的撤销步骤还给操作列表
        this.list.push(this.runItem)
        // 设置展示项
        // !!!: currentFormData取出时必须使用深拷贝Deep_Clone，currentFormData是用于页面展示的，存在被更改的可能，使用浅拷贝将可能会影响到这里list项的数据
        this.currentFormData = Deep_Clone(this.runItem)
    }
}
export default {
    name: '',
    components: {},
    data () { // 定义页面变量
        return {
            // 操作历史
            queue: new formQueue([], []),
        }
    },
    computed: {},
    methods: { // 定义函数
        addDoQueue () {
            if (!this.formFieldData) return
            // 队列添加数据
            this.queue.push(Deep_Clone(this.formFieldData))
        },
        // 执行向后撤销动作
        handleUnDo () {
            // 获取操作树长度,从实际可知listLen >= 1
            let listLen = this.queue.list.length
            // 获取撤销过程中数据长度是否发生变化
            let dragTranstionState = false
            // 只有一种情况启动动画： 变化的前后数据项长度一致
            if (listLen > 1) {
                // 前一项数据
                let preItem = this.queue.list[listLen - 1]
                // 目前数据
                let nowItem = this.queue.list[listLen - 2]
                // 当前后两项数据长度一致时才开始动画，长度不一致不开启动画（增删元素容易触发元素的闪动消失）
                dragTranstionState = nowItem.length === preItem.length
            }
            // 调用动画
            this.openDragTransition(() => {
                // 执行队列的撤销操作
                this.queue.undo()
                // 还原当前页面数据
                this.formFieldData = this.queue.currentFormData
            }, dragTranstionState)  
        },
        // 执行向前重做动作
        handleReDo () {
            // 获取撤销树长度,从实际可知unDoLen >= 1
            let unDoLen = this.queue.unDoList.length
            // 获取操作树长度
            let listLen = this.queue.list.length
            // 获取撤销过程中数据长度是否发生变化
            let dragTranstionState = false
            // 只有一种情况，变化的前后项数据长度一致，其他情况dragTranstionState：false
            if (listLen > 0) {
                // 前一项数据
                let preItem = this.queue.list[listLen - 1]
                // 目前数据
                let nowItem = this.queue.unDoList[unDoLen - 1]
                // 当前后两项数据长度一致时才开始动画，长度不一致不开启动画（增删元素容易闪动消失）
                dragTranstionState = nowItem.length === preItem.length
            }
            // 调用动画
            this.openDragTransition(() => {
                // 执行队列的撤销操作
                this.queue.redo()
                // 还原当前页面数据
                this.formFieldData = this.queue.currentFormData
            }, dragTranstionState)
        },
        // 开启动画，执行事件结束之后，关闭动画
        openDragTransition (callback, status) {
            return new Promise((resolve, reject) => {
                // 设置开启拖拽释放的交互动画
                if (status) {
                    this.useDragTransition = true
                }
                // callback为需要执行的事件
                callback()
                // 执行结束之后关闭动画
                setTimeout(() => {
                    this.useDragTransition = false
                }, 300)
                resolve()
            })
        },
    },
}
</script>