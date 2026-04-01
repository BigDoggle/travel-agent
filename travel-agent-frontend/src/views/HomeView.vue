<template>
  <div class="home-container">
    <!-- 左侧会话管理 -->
    <div class="sidebar">
      <div class="sidebar-header">
        <h3>会话管理</h3>
        <button class="new-chat-btn">+ 新建会话</button>
      </div>
      <div class="chat-list">
        <div class="chat-item active">
          <div class="chat-info">
            <div class="chat-title">旅行规划</div>
            <div class="chat-preview">我想去日本旅游，推荐一下...</div>
          </div>
        </div>
        <div class="chat-item">
          <div class="chat-info">
            <div class="chat-title">酒店推荐</div>
            <div class="chat-preview">东京的酒店有什么推荐...</div>
          </div>
        </div>
        <div class="chat-item">
          <div class="chat-info">
            <div class="chat-title">美食攻略</div>
            <div class="chat-preview">大阪的美食有哪些...</div>
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
      <div class="chat-messages">
        <div class="message ai-message">
          <div class="message-content">
            <p>你好！我是你的AI旅行助手，有什么可以帮助你的吗？</p>
          </div>
        </div>
        <div class="divider"></div>
        <div class="message user-message">
          <div class="message-content">
            <p>我想去日本旅游，推荐一下好玩的地方</p>
          </div>
        </div>
        <div class="divider"></div>
        <div class="message ai-message">
          <div class="message-content">
            <p>日本有很多好玩的地方，以下是一些推荐：</p>
            <p>1. 东京：东京塔、浅草寺、涩谷十字路口</p>
            <p>2. 京都：金阁寺、清水寺、伏见稻荷大社</p>
            <p>3. 大阪：大阪城、环球影城、道顿堀</p>
            <p>4. 北海道：札幌、函馆、旭川</p>
            <p>你对哪个地方特别感兴趣呢？</p>
          </div>
        </div>
      </div>

      <!-- 输入区域 -->
      <div class="chat-input-area">
        <input 
          type="text" 
          v-model="message" 
          placeholder="输入你的问题..."
          class="chat-input"
          @keyup.enter="sendMessage"
        />
        <button class="send-btn" @click="sendMessage">发送</button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const message = ref('')

const sendMessage = () => {
  if (message.value.trim()) {
    // 这里可以添加发送消息的逻辑
    console.log('发送消息:', message.value)
    message.value = ''
  }
}
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
