export default {

    buttons: [
        { id: 'export', name: '导出', icon: 'export', type: 'primary', isHide: true },
        { id: 'import', name: '导入', icon: 'import', type: 'primary', isHide: true },
        { id: 'add', name: '导出', icon: 'export', type: 'primary', isHide: true },
        { id: 'edit', name: '导入', icon: 'import', type: 'primary', isHide: true },
        { id: 'delete', name: '导入', icon: 'import', type: 'primary', isHide: true },
    ],
    // 2023/05/17 更新的框架代码
    paginationInfo: {
        // 自定义列表每页数可选值
        sizes: [200, 400, 600],
        // 初始进入列表每页数据量
        size: 200,
        // ps：当没有配置size，但存在sizes时，默认取sizes的第一项
    },
    paramsBeforeRequest: function (params) {
        let url = `${this.BASEURL}/project/maas/modelwarehouse/modelwarehouselist/getVLLMSelectOption`
        this.post(url, {}).then((res) => {
            if (res.hasOk == false) {
                this.$message.error(res.message)
            }
            // 将字符串转换为JSON对象
            const jsonObj = JSON.parse(res.message);
            this.planParamsSelectData = jsonObj
        }).finally(() => {
        })
        return params
    },
    renderField: {
        explainName: {
            /**
             * 格式化展示
             * @param {Object} row 所在行对象
             * @param {Object} column 所在列对象
             * @param {*} cellValue 当前表格项值
             * @param {Number} index 所在行下角标
             * @param {Object} fieldObj 字段所在列的配置数据
             */
            formatter: function (row, column, cellValue, index, fieldObj) {
                // this: 作用域指代核心列表[mList/index]所在页面作用域
                return `<div style='text-align: left;'">${cellValue}</div>`
            },
            // 点击事件（若没有可以不写，去除整块click）
            // click: function (row, column, cellValue, index, fieldObj) {
            //     // this: 作用域指代核心列表[mList/index]所在页面作用域
            //     this.$message.warning("Don't click me")
            // }
        },
        value: {
            render: function (h, context, row, column, cellValue, index, fieldObj, scope) {
                // let dataMap = new Map([
                //     ["ENV_FP16", [{
                //         props: {
                //             value: 'false',
                //             label: 'false'
                //         }
                //     }, {
                //         props: {
                //             value: 'true',
                //             label: 'true'
                //         }
                //     }]],
                //     ["ENV_BF16", [{
                //         props: {
                //             value: 'false',
                //             label: 'false'
                //         }
                //     }, {
                //         props: {
                //             value: 'true',
                //             label: 'true'
                //         }
                //     }]],
                //     ["ENV_BF16", [{
                //         props: {
                //             value: 'false',
                //             label: 'false'
                //         }
                //     }, {
                //         props: {
                //             value: 'true',
                //             label: 'true'
                //         }
                //     }]],
                //     ["ENV_LR_SCHEDULER_TYPE", [{
                //         props: {
                //             value: 'cosine',
                //             label: 'cosine'
                //         }
                //     }, {
                //         props: {
                //             value: 'linear',
                //             label: 'linear'
                //         }
                //     },
                //     {
                //         props: {
                //             value: 'cosine_with_restarts',
                //             label: 'cosine_with_restarts'
                //         }
                //     },
                //     {
                //         props: {
                //             value: 'polynomial',
                //             label: 'polynomial'
                //         }
                //     },
                //     {
                //         props: {
                //             value: 'constant',
                //             label: 'constant'
                //         }
                //     },
                //     {
                //         props: {
                //             value: 'constant_with_warmup',
                //             label: 'constant_with_warmup'
                //         }
                //     },
                //     {
                //         props: {
                //             value: 'inverse_sqrt',
                //             label: 'inverse_sqrt'
                //         }
                //     },
                //     {
                //         props: {
                //             value: 'reduce_lr_on_plateau',
                //             label: 'reduce_lr_on_plateau'
                //         }
                //     },
                //     {
                //         props: {
                //             value: 'cosine_with_min_lr',
                //             label: 'cosine_with_min_lr'
                //         }
                //     },
                //     {
                //         props: {
                //             value: 'warmup_stable_decay',
                //             label: 'warmup_stable_decay',
                //             selected: true
                //         }
                //     }]]
                // ])
                // let selection = dataMap.get(code);
                if(row.value){
                    if (row.value.includes("$")){
                        return (
                            <el-input disabled="true" vModel={row.value} class="primary">
                            </el-input>
                        )
                    }    
                }

                let code = row.code
                let selectOption = this.planParamsSelectData.selectionMap[code]
                let booleanOption = this.planParamsSelectData.booleanMap[code]
                let selection = selectOption;
                if (!selection){
                    selection = booleanOption;
                }
                if (selection) {
                    // console.log("获取到选项")
                    // console.log(selection)
                    //初始化值
                    // if (row.value) {

                    // }
                    //设置选项
                    let options = []
                    for (let i = 0; i < selection.length; i++) {
                        options.push(h('el-option', selection[i]))
                    }
                    return h('el-select', {
                        model: {
                            value: row.value,
                            callback: (val) => {
                                row.value = val;
                                this.post('/project/maas/modelwarehouse/modelwarehousesave/updateValue', {
                                    id: row.id,
                                    newValue: row.value
                                }).then((res) => {
                                    if (res?.hasOk) {

                                    } else {
                                        console.log("保存数值失败!")
                                    }
                                }).finally(() => {

                                })
                            }
                        }
                    }, options
                        // [
                        //     h('el-option', {
                        //         props: {
                        //             label: '选项1',
                        //             value: 'option1'
                        //         }
                        //     }),
                        //     h('el-option', {
                        //         props: {
                        //             label: '选项2',
                        //             value: 'option2'
                        //         }
                        //     }),
                        //     h('el-option', {
                        //         props: {
                        //             label: '选项3',
                        //             value: 'option3'
                        //         }
                        //     })
                        // ]
                    );
                } else {
                    return (
                        <el-input vModel={row.value} class="primary" on-input={(e) => {
                            this.post('/project/maas/modelwarehouse/modelwarehousesave/updateValue', {
                                id: row.id,
                                newValue: e
                            }).then((res) => {
                                if (res?.hasOk) {

                                } else {
                                    console.log("保存数值失败!")
                                }
                            }).finally(() => {

                            })
                        }}>
                        </el-input>
                    )
                }
                // return (
                //     <el-input vModel={row.value} class="primaryC" on-change={(e) => {
                //         this.post('/project/maas/tuningplanparams/tuningplanparamsedit/updateValue', {
                //             id: row.id,
                //             newValue: e
                //         }).then((res) => {
                //             if (res?.hasOk) {

                //             } else {
                //                 console.log("保存数值失败!")
                //             }
                //         }).finally(() => {

                //         })
                //     }}></el-input>
                // )
            },
        }
    }
}