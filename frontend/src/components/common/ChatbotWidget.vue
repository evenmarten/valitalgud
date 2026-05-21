<template>
  <div class="chatbot">
    <!-- Vestlusaken -->
    <transition name="chat-panel">
      <div v-if="isOpen" class="chat-window" role="dialog" aria-label="AI vestlusrobot">
        <div class="chat-header">
          <span class="chat-title">Talgubot</span>
          <button class="chat-close" aria-label="Sulge vestlus" @click="close">×</button>
        </div>

        <div ref="messages" class="chat-messages">
          <div
            v-for="(message, index) in messages"
            :key="index"
            class="chat-bubble"
            :class="bubbleClass(message)"
          >
            {{ message.content }}
          </div>
          <div v-if="isLoading" class="chat-bubble chat-bubble-bot chat-typing">
            <span></span><span></span><span></span>
          </div>
        </div>

        <form class="chat-input" @submit.prevent="sendMessage">
          <input
            v-model="draft"
            type="text"
            class="chat-field"
            placeholder="Küsi midagi…"
            :disabled="isLoading"
            aria-label="Sinu sõnum"
          />
          <button type="submit" class="chat-send" :disabled="isLoading || !draft.trim()">
            Saada
          </button>
        </form>
      </div>
    </transition>

    <!-- Avamise nupp -->
    <button
      class="chat-toggle"
      :class="{ 'is-open': isOpen }"
      :aria-label="isOpen ? 'Sulge vestlus' : 'Ava AI vestlus'"
      @click="toggle"
    >
      {{ isOpen ? '×' : '💬' }}
    </button>
  </div>
</template>

<script>
import ChatService from '@/api-services/ChatService.js'

const MAX_HISTORY = 10

export default {
  name: 'ChatbotWidget',
  data() {
    return {
      isOpen: false,
      isLoading: false,
      draft: '',
      messages: [
        {
          role: 'assistant',
          content: 'Tere! Olen Talgubot. Küsi minult valitalgud kohta — sündmused, registreerumine, e-pood.',
        },
      ],
    }
  },
  methods: {
    toggle() {
      this.isOpen = !this.isOpen
      if (this.isOpen) {
        this.scrollToBottom()
      }
    },
    close() {
      this.isOpen = false
    },
    sendMessage() {
      const text = this.draft.trim()
      if (!text || this.isLoading) {
        return
      }
      const history = this.messages.slice(-MAX_HISTORY).map((message) => ({
        role: message.role,
        content: message.content,
      }))
      this.messages.push({ role: 'user', content: text })
      this.draft = ''
      this.isLoading = true
      this.scrollToBottom()

      ChatService.sendChatMessage(text, history)
        .then((response) => this.handleReply(response.data))
        .catch(() => this.handleError())
        .finally(() => {
          this.isLoading = false
          this.scrollToBottom()
        })
    },
    handleReply(data) {
      this.messages.push({ role: 'assistant', content: data.reply })
    },
    handleError() {
      this.messages.push({
        role: 'assistant',
        content: 'Vabandust, midagi läks valesti. Proovi hetke pärast uuesti.',
        isError: true,
      })
    },
    bubbleClass(message) {
      if (message.role === 'user') {
        return 'chat-bubble-user'
      }
      return message.isError ? 'chat-bubble-error' : 'chat-bubble-bot'
    },
    scrollToBottom() {
      this.$nextTick(() => {
        const container = this.$refs.messages
        if (container) {
          container.scrollTop = container.scrollHeight
        }
      })
    },
  },
}
</script>

<style scoped>
.chatbot {
  position: fixed;
  right: 24px;
  bottom: 24px;
  z-index: 1050;
}

/* Avamise nupp — paistab silma, kuid jääb nurka */
.chat-toggle {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  border: var(--nb-border);
  background: var(--nb-pink);
  color: #fff;
  font-size: 1.6rem;
  line-height: 1;
  cursor: pointer;
  box-shadow: var(--nb-shadow);
  transition: transform 0.12s ease, box-shadow 0.12s ease;
}

.chat-toggle:hover {
  transform: translate(-2px, -2px);
  box-shadow: var(--nb-shadow-lg);
}

.chat-toggle.is-open {
  background: var(--nb-black);
}

/* Vestlusaken */
.chat-window {
  position: absolute;
  right: 0;
  bottom: 76px;
  width: 360px;
  max-width: calc(100vw - 48px);
  height: min(70vh, 540px);
  display: flex;
  flex-direction: column;
  background: var(--nb-white);
  border: var(--nb-border);
  box-shadow: var(--nb-shadow-lg);
  overflow: hidden;
}

.chat-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0.6rem 0.9rem;
  background: var(--nb-black);
  border-bottom: var(--nb-border);
}

.chat-title {
  color: var(--nb-yellow);
  font-family: 'Archivo Black', sans-serif;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.chat-close {
  background: none;
  border: none;
  color: #fff;
  font-size: 1.5rem;
  line-height: 1;
  cursor: pointer;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 0.9rem;
  display: flex;
  flex-direction: column;
  gap: 0.6rem;
}

.chat-bubble {
  max-width: 85%;
  padding: 0.55rem 0.75rem;
  border: 2px solid var(--nb-black);
  font-size: 0.92rem;
  line-height: 1.35;
  white-space: pre-wrap;
  word-break: break-word;
}

.chat-bubble-bot {
  align-self: flex-start;
  background: #f3f3f3;
}

.chat-bubble-user {
  align-self: flex-end;
  background: var(--nb-blue);
  color: #fff;
}

.chat-bubble-error {
  align-self: flex-start;
  background: var(--nb-pink);
  color: #fff;
}

/* "Kirjutab…" indikaator */
.chat-typing {
  display: flex;
  gap: 4px;
  align-items: center;
}

.chat-typing span {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: var(--nb-black);
  animation: chat-blink 1.2s infinite ease-in-out both;
}

.chat-typing span:nth-child(2) {
  animation-delay: 0.2s;
}

.chat-typing span:nth-child(3) {
  animation-delay: 0.4s;
}

@keyframes chat-blink {
  0%, 80%, 100% { opacity: 0.2; }
  40% { opacity: 1; }
}

.chat-input {
  display: flex;
  gap: 0.5rem;
  padding: 0.7rem;
  border-top: var(--nb-border);
  background: var(--nb-white);
}

.chat-field {
  flex: 1;
  min-width: 0;
  padding: 0.5rem 0.6rem;
  border: 2px solid var(--nb-black);
  font-size: 0.92rem;
}

.chat-field:focus {
  outline: none;
  box-shadow: 2px 2px 0 var(--nb-black);
}

.chat-send {
  padding: 0.5rem 0.9rem;
  border: 2px solid var(--nb-black);
  background: var(--nb-yellow);
  color: var(--nb-black);
  font-weight: 700;
  cursor: pointer;
}

.chat-send:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* Sisse-/väljalibisemine paremalt */
.chat-panel-enter-active,
.chat-panel-leave-active {
  transition: transform 0.18s ease, opacity 0.18s ease;
}

.chat-panel-enter-from,
.chat-panel-leave-to {
  transform: translateX(20px);
  opacity: 0;
}

@media (prefers-reduced-motion: reduce) {
  .chat-toggle,
  .chat-panel-enter-active,
  .chat-panel-leave-active {
    transition: none;
  }
  .chat-typing span {
    animation: none;
  }
}
</style>
