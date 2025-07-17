export default {
    beforeRender: function () {
        let modelName = this.fieldList.find(i => i.name === 'modelName')
        if (!modelName) {
            modelName.type = 'hidden'
        }
        let serverId = this.fieldList.find(i => i.name === 'serverId').value

        let customGpuCardName = this.fieldList.find(i => i.name === 'customGpuCardName')
        if (customGpuCardName){
            if(!serverId){
                let modelName = this.fieldList.find(i => i.name === 'modelName')
                if(!modelName.value){
                    customGpuCardName.type = 'hidden'
                }
            }
        }
    },
    // valueChange是固定的名称，height是发生变更的字段键名
    valueChange: {
        customGpuCardName: function (formScope, fieldName, value, fieldObj) {
            let cardStatusTip = this.fieldList.find(i => i.name === 'cardStatusTip')
            if(cardStatusTip){
                cardStatusTip.value = ''
            }
            const cardArray = fieldObj.customGpuCard.split(',');
            if(cardArray.length > 0 && cardArray[0] == ''){
                let gpuCount = this.fieldList.find(i => i.name === 'gpuCount')
                gpuCount.destroyed = false
                gpuCount.disabled = false
                gpuCount.validationRule = 'required'
                gpuCount.rules = 'required'
            }else{
                let gpuCount = this.fieldList.find(i => i.name === 'gpuCount')
                gpuCount.destroyed = true
                gpuCount.disabled = true
                gpuCount.value = 1
                gpuCount.validationRule = ''
                gpuCount.rules = ''
                this.$message.success("用户关闭自动显卡模式.启用自定义显卡模式")
            }
            const customGpuCardNames = fieldObj.customGpuCardName.split(',');
            let sizeValue = this.fieldList.find(i => i.name === 'size');
            if(!sizeValue){
                return
            }
            let size = parseFloat(sizeValue.value,sizeValue.value.slice(0, -1));
            // console.log("预估总大小为:"+size)
            let startNeedGpum = size/cardArray.length
            cardArray.forEach((card, index) => {
                let gpuCacheSplit = card.split('=')[1].split('-')
                let currentGpuCache = gpuCacheSplit[0]/1000
                let maxGpuCache = gpuCacheSplit[1]/1000
                let per =  ((currentGpuCache+startNeedGpum)/maxGpuCache)*100
                // console.log("当前使用显存:"+currentGpuCache+",最大显存"+maxGpuCache)
                console.log("计算得到百分比:"+per)
                //当前已用 + 预估 占 总显卡显存大小的百分比超过95%时警示
                if (per > 95){
                    let cardStatusTip = this.fieldList.find(i => i.name === 'cardStatusTip')
                    if (!cardStatusTip) {
                        this._fieldList.splice(24,0,{ groupName: "2、部署对象", name: 'cardStatusTip', type: 'textarea', label: '部署状态展示', disabled: true, value: '', isHide: true })
                    }
                    cardStatusTip = this.fieldList.find(i => i.name === 'cardStatusTip')
                    cardStatusTip.value += customGpuCardNames[index]+' 您所选的显卡运行该模型可能发生显存溢出\n'
                }
            });
        },
        /**
         * 字段键为height的值变更事件
         * @param {Object} formScope 表单所在页面作用域
         * @param {String} fieldName 字段键名
         * @param {*} value 字段值
         * @param {Object} fieldObj 字段对象
         * @param ...更多参数 有组件内自定义传回
         */
        serverId: function (formScope, fieldName, value, fieldObj) {
            if (value) {
                let modelName = this.fieldList.find(i => i.name === 'modelName')

                modelName.type = "dialogList"
                this.$route.params.serverId = value
                modelName.initDialogParams = function () {
                    return {
                        defaultParamsObj: { serverId: value }
                    }
                }

            } else {
                let modelName = this.fieldList.find(i => i.name === 'modelName')
                modelName.type = 'hidden'
                modelName.value = ''
            }

        },
        /**
         * 字段键为height的值变更事件
         * @param {Object} formScope 表单所在页面作用域
         * @param {String} fieldName 字段键名
         * @param {*} value 字段值
         * @param {Object} fieldObj 字段对象
         * @param ...更多参数 有组件内自定义传回
         */
        startType: function (formScope, fieldName, value, fieldObj) {
            // 获取当前字段对象
            if (1 == value) {
                //关闭
                // 获取当前字段对象
                let allocateMemory = this.fieldList.find(i => i.name === 'allocateMemory')
                allocateMemory.type = 'text'

            } else {
                //显示
                // 获取当前字段对象
                let allocateMemory = this.fieldList.find(i => i.name === 'allocateMemory')
                allocateMemory.type = 'hidden'
            }
        },
        modelId: function (formScope, fieldName, value, fieldObj) {
        },
        modelName: function (formScope, fieldName, value, fieldObj) {
            // 获取当前字段对象
            let size = this.fieldList.find(i => i.name === "size")
            let path = this.fieldList.find(i => i.name === "path")
            let modelPrecision = this.fieldList.find(i => i.name === "modelPrecision")
            let startNeedGpum = this.fieldList.find(i => i.name === "startNeedGpum")
            let serverId = this.fieldList.find(i => i.name === "serverId")

            //获取文件大小
            // 获取表单保存接口地址
            let url = `${this.BASEURL}/project/server/servergpulist/buildModelSize`
            // 提交接口
            this.post(url, {
                path: path.value,
                serverId: serverId.value
            }).then((res) => {
                if (res?.hasOk) {
                    let customGpuCardName = this.fieldList.find(i => i.name === 'customGpuCardName')
                    customGpuCardName.type = "dialogList"
                    customGpuCardName.initDialogParams = function () {
                        return {
                            defaultParamsObj: { serverId: serverId.value }
                        }
                    }
                    //根据模型名称计算精度
                    if (path.value.includes('int4')) {
                        modelPrecision.value = 3;
                    } else if (path.value.includes('int8')) {
                        modelPrecision.value = 4;
                    } else {
                        modelPrecision.value = 0;
                    }
                    if (!size) {
                        return
                    }
                    size.value = res.message
                    if (size.value.includes('G')) {
                        const unit = size.value.slice(-1);
                        const value = parseFloat(size.value.slice(0, -1));
                        if (!startNeedGpum.value) {
                            if (unit === 'G') {
                                startNeedGpum.value = 22
                            } else {
                                startNeedGpum.value = parseInt(value*1.3)
                            }
                        }
                    }


                } else {
                    console.log("获取失败")
                }
            }).finally(() => {
            })
        },
    },
    saveUrl: function (mdCode) {
        // this:指向moduleEdit/index.vue页面所在作用域
        return `${this.BASEURL}/project/maas/modelwarehouse/modelwarehousesave/saveIncludeFile?mdCode=${mdCode}`
    }
}

function deduplicateAndJoinGpuNames(gpuString) {
    // 检查输入是否为字符串
    if (typeof gpuString !== 'string') {
      return gpuString;
    }
    
    // 分割字符串为数组
    const gpuArray = gpuString.split(',');
    
    // 使用 Set 去重（保留首次出现的元素）
    const uniqueGpuNames = [...new Set(gpuArray)];
    
    // 重新拼接为字符串
    return uniqueGpuNames.join(',');
  }
  