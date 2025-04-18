<!--
  ~ Licensed to the Apache Software Foundation (ASF) under one
  ~ or more contributor license agreements.  See the NOTICE file
  ~ distributed with this work for additional information
  ~ regarding copyright ownership.  The ASF licenses this file
  ~ to you under the Apache License, Version 2.0 (the
  ~ "License"); you may not use this file except in compliance
  ~ with the License.  You may obtain a copy of the License at
  ~
  ~   http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing,
  ~ software distributed under the License is distributed on an
  ~ "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
  ~ KIND, either express or implied.  See the License for the
  ~ specific language governing permissions and limitations
  ~ under the License.
  -->

<script setup lang="ts">
  import { ref } from 'vue'
  import { useAiChatStore } from '@/store/ai-assistant'
  import { storeToRefs } from 'pinia'
  import { message } from 'ant-design-vue'
  import { useI18n } from 'vue-i18n'

  const { t } = useI18n()
  const aiChatStore = useAiChatStore()
  const { isSending, threads, chatRecords } = storeToRefs(aiChatStore)
  const chatMessage = ref('')

  const sendMessage = async () => {
    if (chatMessage.value === '') {
      message.error(t('aiAssistant.message_cannot_be_empty'))
      return
    }
    if (threads.value.length === 0) {
      chatRecords.value = []
    }
    aiChatStore.setChatRecordForSender('USER', chatMessage.value)
    aiChatStore.collectReceiveMessage(chatMessage.value)
    chatMessage.value = ''
  }
</script>
<template>
  <div class="chat-input">
    <a-textarea
      v-model:value="chatMessage"
      :bordered="false"
      :auto-size="{ minRows: 1, maxRows: 6 }"
      :placeholder="$t('aiAssistant.enter_question')"
    />
    <a-button :disabled="isSending" :type="isSending ? 'default' : 'text'" shape="circle" @click="sendMessage">
      <template #icon>
        <svg-icon name="send" />
      </template>
    </a-button>
  </div>
</template>

<style lang="scss" scoped>
  .chat-input {
    margin: 0 auto;
    width: 100%;
    max-width: 800px;
    display: flex;
    border: 1px solid $color-fill;
    border-radius: $space-sm;
    min-height: 40px;
    align-items: center;
    padding-inline-end: 10px;
  }
</style>
