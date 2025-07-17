<template>
    <div class="ai-chat-container">
      <div class="chat-header">
        <h3>AI助手</h3>
      </div>
      
      <div class="chat-messages" ref="messagesContainer">
        <div 
          v-for="(message, index) in messages" 
          :key="index" 
          :class="['message', message.type, message.role]"
        >
          <div class="message-content">
            <!-- 文本消息 -->
            <div v-if="message.contentType === 'text'" class="text-content">
              <div v-if="message.role === 'assistant' && message.isStreaming" class="streaming-text">
                {{ message.content }}
                <span class="streaming-cursor">|</span>
              </div>
              <div v-else>
                {{ message.content }}
              </div>
            </div>
            
            <!-- 图片消息 -->
            <div v-if="message.contentType === 'image'" class="image-content">
              <img :src="message.content" :alt="'AI生成的图片'" class="chat-image" @click="previewImage(message.content)"/>
            </div>
            
            <!-- 视频消息 -->
            <div v-if="message.contentType === 'video'" class="video-content">
              <video controls class="chat-video">
                <source :src="message.content" type="video/mp4">
                您的浏览器不支持视频播放
              </video>
            </div>
          </div>
          
          <div class="message-meta">
            <span class="message-time">{{ formatTime(message.timestamp) }}</span>
            <span class="message-role">{{ message.role === 'user' ? '你' : 'AI助手' }}</span>
          </div>
        </div>
      </div>
      
      <div class="chat-input-area">
        <div class="input-toolbar">
          <el-button-group>
            <el-button size="small" @click="showImageUpload = true" title="上传图片">
              <i class="el-icon-picture"></i>
            </el-button>
            <el-button size="small" @click="showVideoUpload = true" title="上传视频">
              <i class="el-icon-video-camera"></i>
            </el-button>
          </el-button-group>
        </div>
        
        <div class="input-main">
          <el-input
            type="textarea"
            :rows="3"
            placeholder="输入您的问题..."
            v-model="inputMessage"
            @keyup.enter.native="sendMessage"
            resize="none"
          ></el-input>
        </div>
        
        <div class="input-actions">
          <el-button 
            type="primary" 
            @click="sendMessage" 
            :disabled="!inputMessage.trim() || isLoading"
            :loading="isLoading"
          >
            发送
          </el-button>
        </div>
      </div>
      
      <!-- 图片上传对话框 -->
      <el-dialog title="上传图片" :visible.sync="showImageUpload" width="30%">
        <el-upload
          class="upload-demo"
          action="#"
          :on-change="handleImageChange"
          :auto-upload="false"
          :show-file-list="false"
        >
          <el-button size="small" type="primary">点击上传</el-button>
          <div slot="tip" class="el-upload__tip">只能上传jpg/png文件，且不超过2MB</div>
        </el-upload>
      </el-dialog>
      
      <!-- 视频上传对话框 -->
      <el-dialog title="上传视频" :visible.sync="showVideoUpload" width="30%">
        <el-upload
          class="upload-demo"
          action="#"
          :on-change="handleVideoChange"
          :auto-upload="false"
          :show-file-list="false"
        >
          <el-button size="small" type="primary">点击上传</el-button>
          <div slot="tip" class="el-upload__tip">只能上传mp4文件，且不超过10MB</div>
        </el-upload>
      </el-dialog>
      
      <!-- 图片预览 -->
      <el-dialog :visible.sync="imagePreviewVisible" width="80%">
        <img :src="previewImageUrl" style="width: 100%">
      </el-dialog>
    </div>
  </template>
  
  <script>
  import axios from 'axios';
  import dayjs from 'dayjs';
  
  export default {
    name: 'AIChat',
    data() {
      return {
        messages: [],
        inputMessage: '',
        showImageUpload: false,
        showVideoUpload: false,
        imagePreviewVisible: false,
        previewImageUrl: '',
        streamingMessage: null,
        isLoading: false,
        apiEndpoint: 'http://192.168.1.240:27786/v1/chat/completions',
        model: 'glm-4-9b-chat'
      };
    },
    mounted() {
      this.loadHistory();
    },
    methods: {
      formatTime(timestamp) {
        return dayjs(timestamp).format('HH:mm');
      },
      
      loadHistory() {
        // 这里可以加载历史消息
        this.messages = [
          {
            role: 'assistant',
            contentType: 'text',
            content: '你好！我是AI助手，请问有什么可以帮您的？',
            timestamp: Date.now()
          }
        ];
      },
      
      sendMessage() {
        const message = this.inputMessage.trim();
        if (!message) return;
        
        // 添加用户消息
        this.addMessage({
          role: 'user',
          contentType: 'text',
          content: message,
          timestamp: Date.now()
        });
        
        this.inputMessage = '';
        
        // 发送到AI服务
        this.sendToAI(message);
      },
      
      async sendToAI(message) {
        this.isLoading = true;
        
        // 创建AI消息占位符
        const aiMessage = {
          role: 'assistant',
          contentType: 'text',
          content: '',
          isStreaming: true,
          timestamp: Date.now()
        };
        this.messages.push(aiMessage);
        this.streamingMessage = aiMessage;
        
        try {
          const response = await axios.post(this.apiEndpoint, {
            model: this.model,
            messages: [
              ...this.messages
                .filter(msg => msg.contentType === 'text')
                .map(msg => ({
                  role: msg.role,
                  content: msg.content
                }))
            ]
          }, {
            headers: {
              'Content-Type': 'application/json'
            }
          });
          
          // 处理AI响应
          if (response.data && response.data.choices && response.data.choices.length > 0) {
            const aiResponse = response.data.choices[0].message.content;
            
            // 模拟流式效果
            this.simulateStreamingResponse(aiResponse);
          } else {
            this.streamingMessage.content = '未收到有效回复';
            this.streamingMessage.isStreaming = false;
          }
        } catch (error) {
          console.error('API请求失败:', error);
          this.streamingMessage.content = '请求AI服务失败: ' + (error.response?.data?.message || error.message);
          this.streamingMessage.isStreaming = false;
        } finally {
          this.isLoading = false;
        }
      },
      
      simulateStreamingResponse(response) {
        let i = 0;
        const interval = setInterval(() => {
          if (i < response.length) {
            this.streamingMessage.content += response[i];
            this.scrollToBottom();
            i++;
          } else {
            clearInterval(interval);
            this.streamingMessage.isStreaming = false;
          }
        }, 30);
      },
      
      addMessage(message) {
        this.messages.push(message);
        this.scrollToBottom();
      },
      
      scrollToBottom() {
        this.$nextTick(() => {
          const container = this.$refs.messagesContainer;
          if (container) {
            container.scrollTop = container.scrollHeight;
          }
        });
      },
      
      handleImageChange(file) {
        if (file.size > 2 * 1024 * 1024) {
          this.$message.error('图片大小不能超过2MB');
          return;
        }
        
        const reader = new FileReader();
        reader.onload = (e) => {
          this.addMessage({
            role: 'user',
            contentType: 'image',
            content: e.target.result,
            timestamp: Date.now()
          });
          this.showImageUpload = false;
        };
        reader.readAsDataURL(file.raw);
      },
      
      handleVideoChange(file) {
        if (file.size > 10 * 1024 * 1024) {
          this.$message.error('视频大小不能超过10MB');
          return;
        }
        
        const reader = new FileReader();
        reader.onload = (e) => {
          this.addMessage({
            role: 'user',
            contentType: 'video',
            content: e.target.result,
            timestamp: Date.now()
          });
          this.showVideoUpload = false;
        };
        reader.readAsDataURL(file.raw);
      },
      
      previewImage(url) {
        this.previewImageUrl = url;
        this.imagePreviewVisible = true;
      }
    }
  };
  </script>
  
  <style scoped>
  /* 样式部分与之前相同，保持不变 */
  .ai-chat-container {
    display: flex;
    flex-direction: column;
    height: 100%;
    border: 1px solid #ebeef5;
    border-radius: 4px;
    background-color: #fff;
  }
  
  .chat-header {
    padding: 15px;
    border-bottom: 1px solid #ebeef5;
    background-color: #f5f7fa;
  }
  
  .chat-header h3 {
    margin: 0;
    font-size: 16px;
    color: #303133;
  }
  
  .chat-messages {
    flex: 1;
    padding: 15px;
    overflow-y: auto;
    background-color: #fafafa;
  }
  
  .message {
    margin-bottom: 15px;
  }
  
  .message-content {
    max-width: 80%;
    padding: 10px 15px;
    border-radius: 4px;
    word-wrap: break-word;
  }
  
  .message.user .message-content {
    margin-left: auto;
    background-color: #409eff;
    color: white;
  }
  
  .message.assistant .message-content {
    margin-right: auto;
    background-color: #f2f6fc;
    color: #303133;
  }
  
  .message-meta {
    font-size: 12px;
    color: #909399;
    margin-top: 5px;
  }
  
  .message.user .message-meta {
    text-align: right;
  }
  
  .streaming-text {
    display: inline;
  }
  
  .streaming-cursor {
    animation: blink 1s infinite;
  }
  
  @keyframes blink {
    0%, 100% { opacity: 1; }
    50% { opacity: 0; }
  }
  
  .text-content {
    white-space: pre-wrap;
  }
  
  .image-content, .video-content {
    margin-top: 10px;
  }
  
  .chat-image {
    max-width: 100%;
    max-height: 300px;
    border-radius: 4px;
    cursor: pointer;
  }
  
  .chat-video {
    max-width: 100%;
    max-height: 300px;
    border-radius: 4px;
  }
  
  .chat-input-area {
    padding: 15px;
    border-top: 1px solid #ebeef5;
    background-color: #f5f7fa;
  }
  
  .input-toolbar {
    margin-bottom: 10px;
  }
  
  .input-actions {
    margin-top: 10px;
    text-align: right;
  }
  </style>