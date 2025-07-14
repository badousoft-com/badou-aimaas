<template>
  <div>
      <el-row>
          <el-col :span="16">
              <v-shell
                  :banner="banner"
                  :shell_input="send_to_terminal"
                  :commands="commands"
                  @shell_output="prompt"
              ></v-shell>
          </el-col>
          <el-col :span="1">
              <div></div>
          </el-col>
          <el-col :span="7">
              <div class="grid-content bg-purple-light" style="background: #ffffff;">
              <el-tabs style="margin-left: 17px;" v-model="activeName" @tab-click="handleClick">
                  <el-tab-pane label="基础信息" name="first">
                      <div>
                        <div :key="index" v-for='(i, index) in instanceList'>
                          <el-tag >实例:</el-tag>{{i.instanceName}} <button>切换</button>
                          <div>CPU使用情况: {{ i.currentCpu  }}/ {{ i.maxCpu }}</div>
                          <div>内存使用情况: {{i.currentMemory }}/ {{i.maxMemory }}</div>
                        </div>
                      </div>
                      <p><img style="width: 15px;" src="https://ts1.cn.mm.bing.net/th?id=OIP-C.miGdww6yxcyIWzfQBvsN7QHaEJ&w=80&h=80&c=1&vt=9&bgcl=b9240d&r=0&o=6&pid=5.1"> 八斗容器云V3.3.79 2024-04-21</p>
                      <P>容器工作台V1.0.0 @lingmeng 未开发区域</P>
                  </el-tab-pane>`
              </el-tabs>
          </div>
          </el-col>
      </el-row>
  </div>
</template>

<script>
import { Get_App_Instance } from '@/api/business/projectInstance/projectInstance'

export default {
name: "App",
data() {
  return {
    // 当前命令结果
    currentResult: '',
    // 当前命令
    currentCommand: '',
    activeName: 'first',
    // 初始化命令
    initCommand: "ls",
    send_to_terminal: "",
    instanceName: '',
    instanceNameSpace: '',
    instanceList: [],
    envId: "",
    appId: "",
    init: true,
    // socket_url:'ws://192.168.5.116:32007/ws',
    socket_url:'ws://127.0.0.1:32007/ws',
    // socket_url: 'ws://192.168.1.240:30036/bddeplpoy_platform/ws',
    banner: {
      header: "八斗容器云-容器控制台V1.0.0",
      subHeader: "提供容器内部操作入口.注意运行容器大部分命令已阉割保证安全高效.可用命令较少.",
      helpHeader: '输入"shelp"或者compgen -c获取更多可使用的命令.',
      emoji: { 
        // first: "💡",
        // time: 11750
      },
      loading: {
        // first: "🔍",
        // second: "⏳",
        // three:"⌛",
        // time: 1750
      },
      sign: '',
      img: {
        align: "left",
        link: "https://ts1.cn.mm.bing.net/th?id=OIP-C.miGdww6yxcyIWzfQBvsN7QHaEJ&w=80&h=80&c=1&vt=9&bgcl=b9240d&r=0&o=6&pid=5.1",
        width: 100,
        height: 100
      },
    },
    commands: [

    ]
  };
},
async created() {
    // let query = this.$route?.query
    // if(query && query.appId && query.envId){
    //     let params =   {
    //         envId: query.envId,
    //         appId: query.appId
    //     }
    //     let res = await Get_App_Instance(params)
    //     if(res.bean.length==0){
    //        this.$router.go(-1)
    //        return "未存在可用的实例!请先发布应用!"
    //     }
    //     this.appId = query.appId
    //     this.envId = query.envId
    //     this.instanceList = res.bean
    //     // 默认设置第一个
    //     this.instanceName = this.instanceList[0].instanceName
    //     this.instanceNameSpace = this.instanceList[0].nameSpace
    // }else {
    //   this.$message.success('无效的转入参数!请联系管理员!')
    //   // 获取不到APPID 无效 直接返回上个页面
    //   this.$router.go(-1)
    // }
    // 创建WebSocket连接
    this.setupWebSocket(); 
},
beforeDestroy() {
    this.closeWebSocket(); // 在组件销毁前关闭WebSocket连接
},
methods: {
  setupWebSocket() {
      let url = this.socket_url+"/"+this.instanceNameSpace+"/"+this.instanceName+"/"+this.envId
      url = this.socket_url
      this.websocket = new WebSocket(url); // 创建WebSocket连接
      this.websocket.onopen = this.onWebSocketOpen; // WebSocket连接打开时的处理函数
      this.websocket.onmessage = this.onWebSocketMessage; // 收到WebSocket消息时的处理函数
      this.websocket.onclose = this.onWebSocketClose; // WebSocket连接关闭时的处理函数
    },
    onWebSocketOpen() {
      // 链接成功后 发送ls命令读取初始化目录
      this.sendMessage(this.initCommand);
      this.currentCommand = this.initCommand
    },
  onWebSocketMessage(event) {
    const message = event.data;
    if(message == this.currentCommand+"\n"){
       // 如果返回的结果与当前命令相等 则跳过显示
        return
    }else if(message.includes(this.currentCommand)){
      // 如果当前返回命令包含当前命令 进行处理.
      // 剪切当前命令前面的全部内容 放到标识
      if(this.banner.sign==''){
        // 首次保留输出
        this.send_to_terminal = message
      }
       this.banner.sign = message.substring(0,message.lastIndexOf(this.currentCommand))
       return
    }
    this.send_to_terminal = message
    // 处理从服务器接收的消息
    // console.log('Received message:', message);
    this.backEndTop()
  },
  sendMessage(message) {
      if (this.websocket && this.websocket.readyState === WebSocket.OPEN) {
         this.websocket.send(message); // 发送消息到WebSocket服务器
         this.currentCommand = message
      }
    },
    closeWebSocket() {
      if (this.websocket) {
        this.websocket.close(); // 关闭WebSocket连接
      }
    },
  // 收到消息后回到最底部
  backEndTop(){
    var container = this.$el.querySelector("#container");
    container.scrollTop = container.scrollHeight;
  },
  prompt(value) {
    if(value=='shelp'){
      value = 'help'
    }
    // 旧处理方式
    this.sendMessage(value)
    // let params = {
    //     id: '8a7480a5899bb86b0189a9933edc0228',
    //     currentPath: '',
    //     command: value,
    //     instanceName: ''
    //   }
    // let res =   await Commit_Command(params) 
    // if(res.hasOk){
      
    // }
    // this.banner.sign = res.bean
    // this.send_to_terminal = res.message
    // var container = this.$el.querySelector("#container");
    // container.scrollTop = container.scrollHeight;
    // // 切换成loading的状态
    // this.banner.tmpLoading  = this.banner.emoji
    // this.banner.emoji = this.banner.loading
    // if (value.trim() === "ifconfig") {
    //       this.send_to_terminal = `
    //           Wi-Fi wireless network card:
          
    //   Local link IPv6 address. . . : fe80 :: 340f: 6f02: 41e9: 477b% 24
    //   IPv4 address. . . . . . . . .: 192.168.1.2
    //   Subnet mask. . . . . . . . . : 255.255.255.0
    //   Default Gateway. . . . . . . : 192.168.1.1`;
    //   } else {
    //       this.send_to_terminal = `'${value}' is not recognized as an internal command or external,
    //           an executable program or a batch file`;
    //   }
     // 切换成默认完成的状态
  //   this.banner.loading = this.banner.emoji
  //   this.banner.emoji  = this.banner.tmpLoading
  },
  handleClick(tab, event) {
      console.log(tab, event);
  }
}
};
</script>

<style>

</style>