<template>
    <div class="content">
        <!-- class="left-icons" -->
        <div>
            <div class="main-content">
                <div class="icon-wrapper">
                    <img class="icon-content" src="@/assets/image/bussiness/PT-.png" alt="" @click="goAddPage(0)">
                    <div class="font-color" style="color: #1E90FF;" @click="goAddPage(0)">PT</div>
                </div>
                <div class="icon-wrapper">
                    <img class="icon-content" src="@/assets/image/bussiness/SFT-.png" alt="" @click="goAddPage(1)">
                    <div class="font-color" style="color: #2E8B57;" @click="goAddPage(1)">SFT</div>
                </div>
                <div class="icon-wrapper">
                    <img class="icon-content" src="@/assets/image/bussiness/PPO-.png" alt="" @click="goAddPage(2)">
                    <div class="font-color" style="color: #800080;" @click="goAddPage(2)">PPO</div>
                </div>
                <div class="icon-wrapper">
                    <img class="icon-content" src="@/assets/image/bussiness/KTO-.png" alt="" @click="goAddPage(3)">
                    <div class="font-color" style="color: #333333;" @click="goAddPage(3)">KTO</div>
                </div>
            </div>
            <!-- <div class="main-content" style="justify-content: space-around;">
                <span class="font-color" @click="goAddPage(1)">PT</span>
                <span class="font-color" @click="goAddPage(1)">SFT</span>
                <span class="back-color" @click="goAddPage(0)">PPO</span>
                <span class="back-color" @click="goAddPage(0)">KTO</span>
            </div> -->

        </div>
        <!-- <div class="right-icons">

        </div> -->
    </div>
</template>

<script>
import ScopeMixin from '@/components/frame/ScopeMixin'
export default {
    name: 'select-pub-way',
    props: {
        listPageRef: {
            type: Object
        },
        outScope: {
            type: Object
        },
        exexData: {
            type: Object
        }
    },
    // 2. 使用混淆组件
    mixins: [ScopeMixin],
    data() {
        return {
            radio: '1',
            isAdmin: false
        }
    },
    methods: {
        goAddPage(type) {
            this.post('/project/maas/maasfile/maastreefilesave/startCoverData', {
                data: this.exexData,
                type: type
            },
                {
                    headers: {
                        'Content-Type': 'application/json'
                    }
                }
            ).then((res) => {
                if (res?.hasOk) {
                    this.$message.success(res.message)
                    //刷新列表页面
                    this.listPageRef.init()
                } else {
                    this.$message.error(res.message)
                }
            }).finally(() => {
                this.$pageDialog.close()
            })
        }
    },
    created() {
    },
}
</script>

<style lang="scss" scoped>
.main-content {
    width: 100%;
    display: flex;
    justify-content: space-between;
}

.icon-wrapper {
    display: flex;
    flex-direction: column;
    align-items: center;
    margin: 0 10px;
}

.font-color {
    font-size: 15px;
    font-weight: 600;
}

.select-pub-way {
    height: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
}

.icon-content {
    margin: 5px;
    margin-bottom: 12px;
    width: 100px;
    height: 100px;
    cursor: pointer;
}

.content {
    display: flex;
    justify-content: center;

    .left-icons,
    .right-icons {
        // font-size: 82px;
        height: 82px;
        margin-top: 10px;
        display: flex;
        flex-direction: column;
        align-items: center;

    }

    .right-icons {
        margin-left: 30px;
    }

    .font-color {
        text-align: center;
        color: orange;
        cursor: pointer;
    }

    .back-color {
        text-align: center;
        color: #3d9fff;
        cursor: pointer;
    }
}
</style>
