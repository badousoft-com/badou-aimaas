export default {
    saveUrl: function (mdCode) {
        // this:指向moduleEdit/index.vue页面所在作用域
        return `${this.BASEURL}/project/server/k8sserverconfsave/saveIncludeFile?mdCode=${mdCode}`
    },
}