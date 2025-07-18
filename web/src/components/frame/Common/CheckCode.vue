/*
* 父调用方法
* <check-code ref="checkCode" :auto="false" :code="code" @refresh="getCode"/>
* auto为true时自动生成验证码 
* auto为false时使用传入验证码，code属性为传入值
*/
<template>
    <canvas
        id="canvas"
        ref="canvasRef"
        class="check-box"
        width="120"
        height="40"
        @click="refreshCode">
    </canvas>
</template>

<script>
import { Filter_AList_Without_BList } from '@/utils/list'
export default {
    name: 'CheckCode',
    data () {
        return {
            bgColorNum: 177,
            aCode: [
                '0',
                '1',
                '2',
                '3',
                '4',
                '5',
                '6',
                '7',
                '8',
                '9',
                'q',
                'w',
                'e',
                'r',
                't',
                'y',
                'u',
                'i',
                'o',
                'p',
                'a',
                's',
                'd',
                'f',
                'g',
                'h',
                'j',
                'k',
                'l',
                'z',
                'x',
                'c',
                'v',
                'b',
                'n',
                'm'
            ],
            // 过滤掉的元素
            abandon: [
                '0',
                '1',
                'i',
                'l',
                'o'
            ],
        }
    },
    props: {
        code: {
            type: String,
            default: ''
        },
        // 是否自动获取配置项，true将自动生成验证码，false将使用传入验证码
        auto: {
            type: Boolean,
            default: true
        },
        // 自动获取时设置验证码长度
        length: {
            type: [Number, String],
            default: 4
        }
    },
    watch: {
        code: {
            immediate: true,
            handler: function (newVal, oldVal) {
                // 判断是否自动获取
                if (!this.auto && newVal) {
                    // 使用传入值渲染验证码
                    this.draw(newVal)
                }
            }
        }
    },
    mounted () {
        if (this.auto) {
            // 自动获取检验码
            this.draw(this.autoGetCode())
        }
    },
    methods: {
        // 刷新验证码
        refreshCode () {
            // 判断是否自动获取
            if (this.auto) {
                // 自动获取检验码
                let val = this.autoGetCode()
                // 渲染当前组件
                this.draw(val)
                // 将值传给父亲进行自定义操作
                this.$emit('refresh', val)
            } else {
                // 调用父组件刷新方法传入新值
                this.$emit('refresh')
            }
        },
        // 生成随机数
        random (min, max) {
            return Math.floor(Math.random() * (max - min))
        },
        // 获取随机颜色函数
        getColor () {
            var r = this.random(this.bgColorNum, 256)
            var g = this.random(this.bgColorNum, 256)
            var b = this.random(this.bgColorNum, 256)
            return `rgb(${r}, ${g}, ${b})`   
        },
        // 自动获取验证码
        autoGetCode () {
            let codeList = []
            let realCodeList = Filter_AList_Without_BList(this.aCode, this.abandon)
            for (let i = 0; i < this.length; i++) {
                let index = Math.floor(Math.random() * realCodeList.length) // 随机索引值
                codeList.push(realCodeList[index])
            }
            return codeList.join('')   
        },
        // 生成图形验证码
        draw (data) {
            const canvas = this.$refs.canvasRef // 演员
            const context = canvas.getContext('2d') // 舞台，getContext() 方法可返回一个对象，该对象提供了用于在画布上绘图的方法和属性。
            context.clearRect(0, 0, 120, 40) // 清除之前的矩形
            context.fillStyle = `rgb(${this.bgColorNum}, ${this.bgColorNum}, ${this.bgColorNum})`
                //   context.strokeRect(0, 0, 120, 40) // 绘制矩形（无填充）
            context.rect(0, 0, 120, 40)
            context.fill()
            // 绘制字母
            data.split('').forEach((i, index) => {
                const x = 20 + index * 20 // 每个字母之间间隔20
                const y = 20 + 10 * Math.random() // y轴方向位置为20-30随机
                context.font = 'bold 26px 思源黑体' // 设置或返回文本内容的当前字体属性
                context.fillStyle = this.getColor() // 设置或返回用于填充绘画的颜色、渐变或模式，随机
                context.translate(x, y) // 重新映射画布上的 (0,0) 位置，字母不可以旋转移动，所以移动容器
                const deg = -45 + (90 * Math.random() * Math.PI) / 180 // 0-90度随机旋转
                context.rotate(deg) // 	旋转当前绘图
                context.fillText(i, 0, 0) // 在画布上绘制“被填充的”文本
                context.rotate(-deg) // 将画布旋转回初始状态
                context.translate(-x, -y) // 将画布移动回初始状态
            })
            // for (var i = 0; i < this.code.length; i++) {
            //     const index = Math.floor(Math.random() * realCodeList.length) // 随机索引值
            //     const txt = i
                
            //     this.arr[i] = txt // 接收产生的随机数
            // }
            // this.num = this.arr[0] + this.arr[1] + this.arr[2] + this.arr[3] // 将产生的验证码放入num
            // this.$emit('checkCode', this.num)
            // 绘制干扰线
            // for (let i = 0; i < 8; i++) {
            //     context.beginPath() // 起始一条路径，或重置当前路径
            //     context.moveTo(Math.random() * 120, Math.random() * 40) // 把路径移动到画布中的随机点，不创建线条
            //     context.lineTo(Math.random() * 120, Math.random() * 40) // 添加一个新点，然后在画布中创建从该点到最后指定点的线条
            //     context.strokeStyle = this.getColor() // 随机线条颜色
            //     context.stroke() // 	绘制已定义的路径
            // }
            // 绘制干扰点，此处用长度为1的线代替点
            for (let i = 0; i < 20; i++) {
                context.beginPath()
                const x = Math.random() * 120
                const y = Math.random() * 40
                context.moveTo(x, y)
                context.lineTo(x + 1, y + 1)
                context.strokeStyle = this.getColor()
                context.stroke()
            } 
        }
    }
}
</script>

<style lang="scss" scoped>
  .check-box {
    position: absolute;
    right: 0;
    bottom: 0;
    width: 25%;
    height: 40px;
    cursor: pointer;
  }
</style>
