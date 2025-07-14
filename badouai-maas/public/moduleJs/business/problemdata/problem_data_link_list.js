export default {
    dataUrl: function () {
        // this:指向moduleList/index.vue所在页面作用域
        return `${this.BASEURL}/project/maas/problemdata/problemdatalist/listLinkJSON?mdCode=maas_problem_data_new`
    },
}