<!--

-->
<template>
    <div class="active-contain" :class="{'border-outside': outside}" :style="styleObj">
        <slot></slot>
        <div v-if="active" class="ani-box">
            <span class="border-animation border-animation-top"></span>
            <span class="border-animation border-animation-right"></span>
            <span class="border-animation border-animation-bottom"></span>
            <span class="border-animation border-animation-left"></span>
        </div>
    </div>
</template>

<script>
export default {
    name: 'active-border',
    props: {
        // 是否为active：展示活跃边框
        active: {
            type: Boolean,
            default: true
        },
        // 活跃边框位置（true：在外侧，false：内部）
        outside: {
            type: Boolean,
            default: true
        },
        // 活跃边框距离内部容器的宽度（outside为true时有效）
        space: {
            type: String,
            default: '1px'
        },
        // 动画的宽度
        width: {
            type: String,
            default: '3px'
        },
        // 边框颜色（不传时为主题色）
        color: {
            type: String,
            default: ''
        }
    },
    computed: {
        styleObj () {
            return {
                '--ani-width': this.width,
                '--space': this.space,
                '--color': this.color
            }
        }
    },
    data () {
        return {

        }
    }
}
</script>

<style lang="scss" scoped>
// 字段正在操作时的动画颜色（默认）：当prop中的color，有值时，使用color的值
$field_active_color: $primary;

// 活跃变宽位于外部
.border-outside {
    display: inline-block;
    padding: calc(var(--ani-width) + var(--space));
}
.active-contain {
    border-radius: $borderRadius;
    display: inline-block;
    position: relative;
    overflow: hidden;
    $propColor: var(--color, $field_active_color)!global;
    .border-animation {
        display: block;
        position: absolute;
    }
}

@mixin ani($start, $end) {
    #{$start}: -100%;
    background: linear-gradient(to #{$end}, rgba(0,0,0,0), $propColor);
    border-radius: 2px;
    animation: #{$start}TO#{$end} 2s linear infinite;
    @keyframes #{$start}TO#{$end} {
        from {
            #{$start}: -100%;
        }
        to {
            #{$start}: 100%;
        }
    }
}
// 设置动画
.border-animation-top {
    @include ani(left, right);
}
.border-animation-right {
    @include ani(top, bottom);
}
.border-animation-bottom {
    @include ani(right, left);
}
.border-animation-left {
    @include ani(bottom, top);
}
// 循环设置所在位置
@each $member in top, right, bottom, left {
    .border-animation-#{$member} {
        #{$member}: 0px;
    }
}
// 设置块的宽高
@for $i from 1 through 4 {
    .ani-box {
        span:nth-child(#{$i}) {
            @if $i % 2 != 0 {
                height: var(--ani-width);
                width: 100%;
            }
            @if $i % 2 == 0 {
                width: var(--ani-width);
                height: 100%;
                animation-delay: 1s;
            }
        }
    }
}
</style>