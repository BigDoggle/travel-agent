<template>
  <div class="home-container">
    <!-- 左侧会话管理 -->
    <div class="sidebar">
      <div class="sidebar-header">
        <h3>会话管理</h3>
        <button class="new-chat-btn" @click="createNewSession">+ 新建会话</button>
      </div>
      <div class="chat-list">
        <div 
          v-for="session in sessions" 
          :key="session.id" 
          class="chat-item" 
          :class="{ active: currentSessionId === session.id }"
          @click="switchSession(session.id)"
        >
          <div class="chat-info">
            <div class="chat-title">{{ session.title }}</div>
            <div class="chat-preview">{{ session.preview }}</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 右侧对话区域 -->
    <div class="chat-container">
      <!-- 对话头部 -->
      <div class="chat-header">
        <h2>AI智能旅行规划</h2>
      </div>

      <!-- 对话内容 -->
      <div class="chat-messages" ref="chatMessagesRef">
        <div 
          v-for="(msg, index) in messages" 
          :key="index" 
          class="message" 
          :class="msg.type === 'user' ? 'user-message' : 'ai-message'"
        >
          <div class="message-content" v-if="msg.type === 'user'">
            <p v-for="(line, lineIndex) in msg.content.split('\n')" :key="lineIndex">{{ line }}</p>
          </div>
          <div class="message-content" v-else v-html="parseMarkdown(msg.content)"></div>
        </div>
        <div v-if="isLoading" class="message ai-message">
          <div class="message-content">
            <p>AI正在思考...</p>
          </div>
        </div>
      </div>

      <!-- 输入区域 -->
      <div class="chat-input-area">
        <input 
          type="text" 
          v-model="inputMessage" 
          placeholder="输入你的问题..."
          class="chat-input"
          @keyup.enter="sendMessage"
          :disabled="isLoading"
        />
        <button class="send-btn" @click="sendMessage" :disabled="isLoading">发送</button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed, nextTick } from 'vue'

// 会话管理
const sessions = ref([
  { id: '1', title: '旅行规划', preview: '我想去日本旅游，推荐一下...' },
  { id: '2', title: '酒店推荐', preview: '东京的酒店有什么推荐...' },
  { id: '3', title: '美食攻略', preview: '大阪的美食有哪些...' }
])
const currentSessionId = ref('1')

// 消息管理
const messages = ref([
  {
    type: 'ai',
    content: '你好！我是你的AI旅行助手，有什么可以帮助你的吗？'
  },
  {
    type: 'user',
    content: '我想去日本旅游，推荐一下好玩的地方'
  },
  {
    type: 'ai',
    content: '日本有很多好玩的地方，以下是一些推荐：\n1. 东京：东京塔、浅草寺、涩谷十字路口\n2. 京都：金阁寺、清水寺、伏见稻荷大社\n3. 大阪：大阪城、环球影城、道顿堀\n4. 北海道：札幌、函馆、旭川\n你对哪个地方特别感兴趣呢？'
  }
])
const inputMessage = ref('')
const isLoading = ref(false)
const chatMessagesRef = ref<HTMLElement | null>(null)

// 滚动到底部的函数
const scrollToBottom = () => {
  nextTick(() => {
    if (chatMessagesRef.value) {
      chatMessagesRef.value.scrollTop = chatMessagesRef.value.scrollHeight
    }
  })
}

// Markdown解析函数
const parseMarkdown = (text: string): string => {
  if (!text) return ''
  
  // 替换标题
  text = text.replace(/^# (.*$)/gim, '<h1>$1</h1>')
  text = text.replace(/^## (.*$)/gim, '<h2>$1</h2>')
  text = text.replace(/^### (.*$)/gim, '<h3>$1</h3>')
  
  // 替换粗体
  text = text.replace(/\*\*(.*?)\*\*/gim, '<strong>$1</strong>')
  
  // 替换斜体
  text = text.replace(/\*(.*?)\*/gim, '<em>$1</em>')
  
  // 替换列表
  text = text.replace(/^\* (.*$)/gim, '<li>$1</li>')
  text = text.replace(/(<li>.*<\/li>)/s, '<ul>$1</ul>')
  
  // 替换有序列表
  text = text.replace(/^\d+\. (.*$)/gim, '<li>$1</li>')
  text = text.replace(/(<li>.*<\/li>)/s, '<ol>$1</ol>')
  
  // 替换链接
  text = text.replace(/\[(.*?)\]\((.*?)\)/gim, '<a href="$2" target="_blank">$1</a>')
  
  // 替换图片
  text = text.replace(/!\[(.*?)\]\((.*?)\)/gim, '<img src="$2" alt="$1">')
  
  // 替换代码块
  text = text.replace(/```([\s\S]*?)```/gim, '<pre><code>$1</code></pre>')
  
  // 替换行内代码
  text = text.replace(/`(.*?)`/gim, '<code>$1</code>')
  
  // 替换引用
  text = text.replace(/^> (.*$)/gim, '<blockquote>$1</blockquote>')
  
  return text
}

// 新建会话
const createNewSession = () => {
  const newSessionId = (sessions.value.length + 1).toString()
  sessions.value.push({
    id: newSessionId,
    title: '新会话',
    preview: '开始新的对话...'
  })
  currentSessionId.value = newSessionId
  messages.value = [
    {
      type: 'ai',
      content: '你好！我是你的AI旅行助手，有什么可以帮助你的吗？'
    }
  ]
}

// 切换会话
const switchSession = (sessionId: string) => {
  currentSessionId.value = sessionId
  // 这里可以添加加载对应会话历史的逻辑
}

// 发送消息
const sendMessage = async () => {
  if (!inputMessage.value.trim() || isLoading.value) return

  // 添加用户消息到消息列表
  const userMessage = inputMessage.value.trim()
  messages.value.push({
    type: 'user',
    content: userMessage
  })
  inputMessage.value = ''
  isLoading.value = true

  try {
    // 模拟用户ID，实际应该从登录状态获取
    const userId = 1
    const sessionId = currentSessionId.value

    // 添加AI消息占位
    const aiMessageIndex = messages.value.length;
    messages.value.push({
      type: 'ai',
      content: ''
    });
    
    let aiResponse = '';

    // 使用fetch API来发送POST请求，处理返回的流式响应
    const response = await fetch(`/api/ai/chat/stream?userId=${userId}&sessionId=${sessionId}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'text/plain'
      },
      body: userMessage
    });

    if (!response.ok) {
      throw new Error('API请求失败');
    }

    // 处理流式响应
    const reader = response.body?.getReader();
    if (!reader) {
      throw new Error('无法获取响应流');
    }

    // 处理SSE格式的流式数据
    let buffer = '';
    while (true) {
      const { done, value } = await reader.read();
      if (done) break;

      // 解码流式数据
      const chunk = new TextDecoder('utf-8').decode(value);
      buffer += chunk;

      // 解析SSE格式
      const lines = buffer.split('\n');
      buffer = '';

      for (const line of lines) {
        if (line.startsWith('data:')) {
          // 提取data部分，保留原始格式
          const data = line.substring(5);
          aiResponse += data;
          
          // 更新AI消息内容（使用Vue的响应式更新）
          // 先创建一个新的消息对象
          const updatedMessage = {
            ...messages.value[aiMessageIndex],
            content: aiResponse
          };
          // 然后替换整个messages数组，确保Vue能够检测到变化
          messages.value = [
            ...messages.value.slice(0, aiMessageIndex),
            updatedMessage,
            ...messages.value.slice(aiMessageIndex + 1)
          ];
          
          // 滚动到底部
          scrollToBottom();
        }
      }
    }

    // 更新会话预览
    const currentSession = sessions.value.find(s => s.id === sessionId)
    if (currentSession) {
      currentSession.preview = userMessage.length > 20 ? userMessage.substring(0, 20) + '...' : userMessage
    }

  } catch (error) {
    console.error('发送消息失败:', error)
    // 添加错误消息
    messages.value.push({
      type: 'ai',
      content: '抱歉，处理您的请求时出现错误，请稍后重试。'
    })
  } finally {
    isLoading.value = false
  }
}

// 页面加载时初始化
onMounted(() => {
  // 这里可以添加加载历史会话的逻辑
})
</script>

<style scoped>
.home-container {
  display: flex;
  height: 100vh;
  background: white;
}

/* 左侧会话管理 */
.sidebar {
  width: 20%;
  min-width: 250px;
  background: white;
  border-right: 1px solid rgba(255, 228, 196, 0.3);
  padding: 20px;
  box-shadow: 2px 0 10px rgba(139, 69, 19, 0.1);
}

.sidebar-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.sidebar-header h3 {
  color: black;
  font-weight: 500;
  margin: 0;
}

.new-chat-btn {
  background: linear-gradient(to right, #f5e6c8, #f8f0d8);
  border: 1px solid rgba(255, 228, 196, 0.5);
  border-radius: 6px;
  color: black;
  padding: 8px 12px;
  font-size: 14px;
  cursor: pointer;
  transition: opacity 0.3s ease;
}

.new-chat-btn:hover {
  opacity: 0.9;
}

.chat-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.chat-item {
  padding: 12px;
  border-radius: 8px;
  background: white;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid transparent;
}

.chat-item:hover {
  background: rgba(255, 248, 220, 0.5);
  border-color: rgba(255, 228, 196, 0.3);
}

.chat-item.active {
  background: rgba(255, 248, 220, 0.7);
  border-color: rgba(255, 228, 196, 0.5);
}

.chat-item.active .chat-title,
.chat-item.active .chat-preview {
  color: #8b5a2b;
}

.chat-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.chat-title {
  font-size: 14px;
  font-weight: 500;
  color: black;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.chat-preview {
  font-size: 12px;
  color: black;
  opacity: 0.7;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 右侧对话区域 */
.chat-container {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.chat-header {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 10px;
  background: white;
  box-shadow: none;
  border-bottom: 1px solid rgba(255, 228, 196, 0.3);
}

.chat-header h2 {
  color: black;
  font-weight: 500;
  margin: 0;
  padding: 5px 15px;
  background: transparent;
  border-radius: 20px;
  font-size: 1.2rem;
}

.chat-messages {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.message {
  max-width: 80%;
  padding: 12px 16px;
  border-radius: 12px;
  word-wrap: break-word;
}

.ai-message {
  align-self: flex-start;
  background: white;
  border: none;
  color: black;
  padding: 12px 16px;
  margin: 10px 0;
  max-width: 80%;
  border-radius: 12px;
  word-wrap: break-word;
}

.user-message {
  align-self: flex-end;
  background: rgba(255, 248, 220, 0.7);
  color: black;
  border: none;
  padding: 12px 16px;
  margin: 10px 0;
  max-width: 80%;
  border-radius: 12px;
  word-wrap: break-word;
}

.chat-messages {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 20px;
  position: relative;
}

.message {
  max-width: 80%;
  padding: 12px 16px;
  border-radius: 12px;
  word-wrap: break-word;
  margin: 10px 0;
}

/* 分割线 */
.divider {
  height: 1px;
  width: 100%;
  margin: 10px 0;
}

.divider::after {
  content: '';
  display: block;
  width: 80%;
  height: 100%;
  margin: 0 auto;
  background: rgba(139, 90, 43, 0.4);
}

.message-content p {
  margin: 0 0 8px 0;
}

.message-content p:last-child {
  margin-bottom: 0;
}

/* 输入区域 */
.chat-input-area {
  padding: 20px;
  background: transparent;
  border-top: none;
  box-shadow: none;
  display: flex;
  gap: 10px;
  justify-content: center;
  align-items: center;
}

.chat-input {
  flex: 1;
  padding: 12px 16px;
  border: 1px solid rgba(255, 228, 196, 0.5);
  border-radius: 24px;
  background: white;
  color: #333;
  font-size: 16px;
  transition: border-color 0.3s ease;
}

.chat-input:focus {
  outline: none;
  border-color: #d2b48c;
  box-shadow: 0 0 0 2px rgba(255, 248, 220, 0.5);
}

.send-btn {
  background: linear-gradient(to right, #f5e6c8, #f8f0d8);
  border: 1px solid rgba(255, 228, 196, 0.5);
  border-radius: 24px;
  color: black;
  padding: 12px 24px;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.send-btn:hover {
  opacity: 0.9;
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(139, 90, 43, 0.2);
}

.send-btn:active {
  opacity: 1;
  transform: translateY(0);
  box-shadow: 0 1px 4px rgba(139, 90, 43, 0.1);
  background: linear-gradient(to right, #e6d3b0, #f0e6c8);
}

/* 滚动条样式 */
.chat-messages::-webkit-scrollbar {
  width: 8px;
}

.chat-messages::-webkit-scrollbar-track {
  background: rgba(255, 255, 245, 0.5);
  border-radius: 4px;
}

.chat-messages::-webkit-scrollbar-thumb {
  background: rgba(210, 180, 140, 0.5);
  border-radius: 4px;
}

.chat-messages::-webkit-scrollbar-thumb:hover {
  background: rgba(210, 180, 140, 0.7);
}
</style>
