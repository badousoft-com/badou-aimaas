<template>
    <div class="navbar">
        <!-- <hamburger :is-active="sidebar.opened" class="hamburger-container" @toggleClick="toggleSideBar" /> -->
        <breadcrumb class="breadcrumb-container" />
        <!-- <div class="ppp">666</div> -->
        <!-- <bd-button type="primary"  icon="back" class="btn" size="small" @click="backLastPage">返回</bd-button> -->
        <!-- <div class="right-menu">
            <el-dropdown class="avatar-container" trigger="click">
                <div class="avatar-wrapper">
                <img :src="avatar+'?imageView2/1/w/80/h/80'" class="user-avatar">
                <i class="el-icon-caret-bottom" />
                </div>
                <el-dropdown-menu slot="dropdown" class="user-dropdown">
                <router-link to="/">
                    <el-dropdown-item>
                    Home
                    </el-dropdown-item>
                </router-link>
                <a target="_blank" href="https://github.com/PanJiaChen/vue-admin-template/">
                    <el-dropdown-item>Github</el-dropdown-item>
                </a>
                <a target="_blank" href="https://panjiachen.github.io/vue-element-admin-site/#/">
                    <el-dropdown-item>Docs</el-dropdown-item>
                </a>
                <el-dropdown-item divided>
                    <span style="display:block;" @click="logout">Log Out</span>
                </el-dropdown-item>
                </el-dropdown-menu>
            </el-dropdown>
        </div> -->
    </div>
</template>

<script>
import { mapGetters } from 'vuex'
import Breadcrumb from '@/components/frame/Breadcrumb'
// import Hamburger from '@/components/frame/Hamburger'

export default {
    components: {
        Breadcrumb,
        // Hamburger,
    },
    computed: {
        ...mapGetters(['sidebar', 'avatar']),
    },
    methods: {
        toggleSideBar () {
            this.$store.dispatch('app/toggleSideBar')
        },
        async logout () {
            await this.$store.dispatch('user/logout')
            this.pushPage({
                path: `/login?redirect=${this.$route.fullPath}`,
                title: '登录页'
            })
        },

        backLastPage () {
            this.$router.go(-1)
        }
    },
}
</script>

<style lang="scss" scoped>
.navbar {
    height: $locationHeight;
    line-height: $locationHeight + 4px;
    overflow: hidden;
    position: relative;
    padding: 0 $space;
    display: flex;
    justify-content: space-around;
    align-items: center;
    .hamburger-container {
        line-height: 46px;
        height: 100%;
        float: left;
        cursor: pointer;
        transition: background 0.3s;
        -webkit-tap-highlight-color: transparent;

        &:hover {
            background: rgba(0, 0, 0, 0.025);
        }
    }

    // .breadcrumb-container {
    //     float: left;
    // }

    .right-menu {
        float: right;
        height: 100%;
        line-height: 50px;

        &:focus {
            outline: none;
        }

        .right-menu-item {
            display: inline-block;
            padding: 0 8px;
            height: 100%;
            font-size: 18px;
            color: #5a5e66;
            vertical-align: text-bottom;

            &.hover-effect {
                cursor: pointer;
                transition: background 0.3s;

                &:hover {
                    background: rgba(0, 0, 0, 0.025);
                }
            }
        }

        .avatar-container {
            margin-right: 30px;

            .avatar-wrapper {
                margin-top: 5px;
                position: relative;

                .user-avatar {
                    cursor: pointer;
                    width: 40px;
                    height: 40px;
                    border-radius: 10px;
                }

                .el-icon-caret-bottom {
                    cursor: pointer;
                    position: absolute;
                    right: -20px;
                    top: 25px;
                    font-size: 12px;
                }
            }
        }
    }
}
.btn{
    position: absolute;
    right: 20px;
}
</style>
