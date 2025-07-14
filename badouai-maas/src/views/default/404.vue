<template>
    <div class="wscn-http404-container">
        <div class="wscn-http404">
            <div class="pic-404 floatL">
                <img class="pic-404__parent w-per-100" src="@/assets/image/frame/404.gif" alt="404">
            </div>
            <div class="bullshit">
                <div class="bullshit__oops">{{ error }}</div>
                <div class="bullshit__headline">{{ message }}</div>
                <div class="bullshit__info">{{suggestion}}</div>
                <a href="" class="bullshit__return-home">{{ back }}</a>
            </div>
        </div>
    </div>
</template>

<script>

export default {
    name: 'Page404',
    computed: {
        error () {
            return '404!'
        },
        message () {
            return '无法找到该页面...'
        },
        suggestion () {
            return '请检查输入地址是否正确,或者点击下面按钮返回首页'
        },
        back () {
            return '返回首页'
        }
    }
}
</script>

<style lang="scss" scoped>
.wscn-http404-container {
    transform: translate(-50%,-50%);
    position: absolute;
    top: 40%;
    left: 50%;
}
.wscn-http404 {
    position: relative;
    width: 1200px;
    padding: 0 50px;
    overflow: hidden;
    display: flex;
    align-items: center;
    .pic-404 {
        flex: 1;
    }
    .bullshit {
        position: relative;
        width: 350px;
        padding: 30px 0;
        overflow: hidden;
        &__oops {
            font-size: 45px;
            font-weight: bold;
            line-height: 40px;
            color: #1482f0;
            opacity: 0;
            margin-bottom: 20px;
            animation-name: slideUp;
            animation-duration: 0.5s;
            animation-fill-mode: forwards;
        }
        &__headline {
            font-size: 20px;
            line-height: 24px;
            color: #222;
            font-weight: bold;
            opacity: 0;
            margin-bottom: 10px;
            animation-name: slideUp;
            animation-duration: 0.5s;
            animation-delay: 0.1s;
            animation-fill-mode: forwards;
        }
        &__info {
            font-size: 13px;
            line-height: 21px;
            color: grey;
            opacity: 0;
            margin-bottom: 30px;
            animation-name: slideUp;
            animation-duration: 0.5s;
            animation-delay: 0.2s;
            animation-fill-mode: forwards;
        }
        &__return-home {
            display: block;
            float: left;
            width: 110px;
            height: 36px;
            background: #1482f0;
            border-radius: 100px;
            text-align: center;
            color: #ffffff;
            opacity: 0;
            font-size: 14px;
            line-height: 36px;
            cursor: pointer;
            animation-name: slideUp;
            animation-duration: 0.5s;
            animation-delay: 0.3s;
            animation-fill-mode: forwards;
        }
        @keyframes slideUp {
            0% {
                transform: translateY(60px);
                opacity: 0;
            }
            100% {
                transform: translateY(0);
                opacity: 1;
            }
        }

    }
}

</style>
