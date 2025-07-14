
export default {
    buttons: [
        { id: 'export', name: '导出', icon: 'export', type: 'primary', isHide: true },
        { id: 'import', name: '导入', icon: 'import', type: 'primary', isHide: true },
        { id: 'add', name: '导出', icon: 'export', type: 'primary', isHide: true },
        { id: 'edit', name: '导入', icon: 'import', type: 'primary', isHide: true },
        { id: 'view', name: '导入', icon: 'import', type: 'primary', isHide: true },
        { id: 'delete', name: '导入', icon: 'import', type: 'primary', isHide: true }
    ],
    renderField: {
        value: {
            render: function (h, context, row, column, cellValue, index, fieldObj, scope) {
                let that = this
                return (
                    <el-input vModel={row.value} class="primaryC" on-change={(e) => {
                        this.post('/project/maas/tuningprogramparams/tuningprogramparamsedit/updateValue', {
                            id: row.id,
                            newValue: e
                        }).then((res) => {
                            if (res?.hasOk) {

                            } else {
                                console.log("保存数值失败!")
                            }
                        }).finally(() => {
                           
                        })
                    }}></el-input>
                )
            },
        }
    }
}