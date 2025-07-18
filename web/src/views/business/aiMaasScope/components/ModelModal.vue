<template>
    <div class="modal-overlay">
      <div class="modal-content">
        <div class="modal-header">
          <h2>{{ model.name }}模型介绍</h2>
          <button class="close-btn" @click="$emit('close')">
            &times;
          </button>
        </div>
        <div class="modal-body">
          <vue-markdown :source="modelDetails" class="markdown-content" />
        </div>
      </div>
    </div>
  </template>

  <script>
  import VueMarkdown from 'vue-markdown'
  import hljs from 'highlight.js'

  export default {
    components: {
      VueMarkdown
    },
    props: {
      model: {
        type: Object,
        required: true
      }
    },
    data() {
      return {
          modelDetails: "",
      }
    },
    mounted() {
      console.log('Model received in modal:', this.model);
      hljs.highlightAll();
      this.fetchModelDetails();
    },
    methods: {
        fetchModelDetails() {
            const owner = this.model.Path;
            const name = this.model.Name;
            let params = { owner, name };
            let url = `${this.BASEURL}/project/maas/modelwarehouse/modelscopeproxylist/getModelDetails`
            this.isLoading = true;
            this.post(url, params, {
                headers: {
                    'Content-Type': 'application/json'
                }
            }).then(res => {
                if (res?.success && res.content) {
                    this.modelDetails = res.content;
                } else {
                    this.modelDetails = "# 暂无介绍";
                }
            }).catch(err => {
                console.error(err);
            }).finally(() => {
                this.isLoading = false;
            })
        }
    }
  }
  </script>

  <style>
  .modal-overlay {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0, 0, 0, 0.5);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 1000;
  }

  .modal-content {
    background: white;
    width: 80%;
    max-width: 1208px;
    max-height: 18604.5px;
    border-radius: 8px;
    overflow: hidden;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  }

  .modal-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 1rem;
    background: #f8f9fa;
    border-bottom: 1px solid #dee2e6;
  }

  .close-btn {
    font-size: 1.5rem;
    background: none;
    border: none;
    cursor: pointer;
    padding: 0 0.5rem;
  }

  .modal-body {
    padding: 1rem;
    overflow-y: auto;
    max-height: calc(90vh - 60px);
    width: 1208px;
  }

  .markdown-content {
    font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
    width: 100%;
    max-width: 100%;
  }

  .markdown-content h2 {
    color: #1a73e8;
    border-bottom: 1px solid #eee;
    padding-bottom: 0.3em;
  }

  .markdown-content pre {
    background: #f6f8fa;
    padding: 1rem;
    border-radius: 4px;
    overflow-x: auto;
  }

  .markdown-content code {
    font-family: "SFMono-Regular", Consolas, "Liberation Mono", Menlo, monospace;
  }
  </style>
