<template>
    <div class="m-tab" :class="{'noBorder': noBorder}">
        <el-tabs 
            v-model="activeName" 
            @tab-click="handleClick"
            :class="{'border': border}"
            :before-leave="beforeLeave">
            <el-tab-pane 
                v-for="(i, index) in tabData"
                :key="index"
                :label="i.label" 
                :name="i.name">
                <slot></slot>
                <slot :name="i.name"></slot>
            </el-tab-pane>
        </el-tabs>
        <div class="operateArea">
            <slot name="fun"></slot>
        </div>
        
    </div>   
</template>

<script>
export default {
    components: {},
    props: {
        tabData: {
            type: Array,
            default: []
        },
        activeTab: {
            type: String,
            default: ''
        },
        border: {
            type: Boolean,
            default: false
        },
        noBorder: {
            type: Boolean,
            default: false
        },
        beforeLeave: {
            type: Function,
            default: () => { return true }
        }
    },
    data () {
        return {
            activeName: ''
        }
    },
    methods: {
        handleClick(tab, event) {
            if (this.beforeLeave()) {
                this.$emit('update:activeTab', tab.name)
            }
            try {
                this.$emit('tab-click', tab, event)
            } catch (e) {
                console.log(`子组件没有提供tab-click事件`)
            } 
        }
    },
    watch: {
        activeTab: {
            deep: true,
            immediate: true,
            handler: function (newVal, oldVal) {
                this.activeName = newVal
            }
        }
    }
}
</script>

<style lang="scss" scoped>
    .m-tab >>> {
        position: relative;
        .operateArea {
            position: absolute;
            top: 3px;
            height: 40px;
            right: 3px;
        }
    }
    
</style>
