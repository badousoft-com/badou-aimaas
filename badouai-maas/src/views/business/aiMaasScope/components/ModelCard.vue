<template>
  <div class="model-card" @mouseenter="hover = true" @mouseleave="hover = false" @click="$emit('click')">
    <h3 class="model-title" :title="modelTitle">
      <span>{{ modelTitle }}</span>
      <button class="action-button" @click.stop="handleButtonClick(modelPath)" title="更多操作" aria-label="更多操作">
        <bd-icon name="forward" />
      </button>
    </h3>

    <p class="model-path" :title="modelPath">{{ modelPath }}</p>

    <div class="model-tags">
      <span v-for="(tag, index) in combinedTags" :key="index" class="model-tag">
        <i class="fas fa-tag"></i> {{ tag }}
      </span>
    </div>

    <div class="model-footer">
      <span class="fullName">
        <i class="fas fa-user"></i> {{ fullName }}
      </span>
      <div class="right-aligned-info">
        <span class="lastUpdatedTime">
          <i class="fas fa-code"></i> {{ formatDate(model.LastUpdatedTime) }} 更新
        </span>
        <span class="downloads">
          <bd-icon name="download" class="download-icon" />
          {{ formatNumberWithUnit(animatedDownloads) }}
        </span>
        <span class="stars">
          <bd-icon name="save" class="star-icon" />
          {{ formatNumberWithUnit(model.Stars || 0) }}
        </span>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    model: {
      type: Object,
      required: true
    }
  },
  data() {
    return {
      isAnimating: false,
      animatedDownloads: 0,
    }
  },
  watch: {
    model: {
      immediate: true,
      deep: true,
      handler(newModel) {
        this.animatedDownloads = newModel?.Downloads || 0;
      }
    }
  },
  computed: {
    modelTitle() {
      return this.model.ChineseName || this.model.Name;
    },
    modelPath() {
      return `${this.model.Path || ''}/${this.model.Name || ''}`;
    },
    combinedTags() {
      const tags = [];

      // 处理Tasks的ChineseName
      if (this.model.Tasks) {
        this.model.Tasks.forEach(task => {
          if (task.ChineseName) tags.push(task.ChineseName);
        });
      }
      // 处理Frameworks（去重）
      const frameworks = [...new Set(this.model.Frameworks)];
      frameworks.forEach(fw => tags.push(fw));

      // 处理Libraries（去重且排除frameworks中的值）
      const libs = [...new Set(this.model.Libraries)].filter(
        lib => !frameworks.includes(lib)
      );
      libs.forEach(lib => tags.push(lib));

      // 处理ModelType
      if (this.model.ModelType?.length) {
        tags.push(...this.model.ModelType);
      }

      // 处理License
      if (this.model.License) {
        tags.push(`开源协议: ${this.model.License}`);
      }

      // 处理Domain/Metrics/Tags
      ['Domain', 'Metrics', 'Tags'].forEach(field => {
        if (this.model[field]?.length) {
          this.model[field].forEach(item => {
            tags.push(item);
          });
        }
      });
      return tags.filter(item => item && item.trim() !== '');
    },
    fullName() {
      const org = this.model.Organization;
      return org?.FullName || org?.Name || this.model.Path;
    }
  },
  mounted() {
    this.animatedDownloads = this.model.Downloads || 0;
  },
  methods: {
    handleButtonClick(modelPath) {
      let id = 'selectDownModel'
      this.$dialog.init({
        type: 'standerListCode',
        id: id,
        title: '选择模型同步服务器',
        mdCode: 'fbpt_k8s_server_conf_admin',
        customSetting: {
          multiple: true
        },
        handlerList: [
          {
            name: '取消',
            type: 'danger',
            icon: 'cancel',
            handler: function () {
              // 关闭弹窗表单
              this.$dialog.close()
            }
          }, {
            name: '保存',
            type: 'primary',
            icon: 'save',
            loading: false,
            handler: function (btnObj) {
              // getDialogConObj:全局封装，3为指定模型列表，返回该页面作用域
              let aimRef = this.getDialogConObj(id, 3)

              let serverId = aimRef.selection.map(item => item.id).join(',')
              let serverName = aimRef.selection.map(item => item.remark).join(',')
              btnObj.loading = true
              this.$confirm("是否将该模型同步至模型仓库.根据模型的大小不同.同步到模型仓库需要不同的时间.具体进度可前往一键部署模型->修改->同步任务查看进度", "提示", {
                iconClass: "el-icon-question",//自定义图标样式
                confirmButtonText: "已了解并确认开始",//确认按钮文字更换
                cancelButtonText: "取消",//取消按钮文字更换
                showClose: true,//是否显示右上角关闭按钮
                type: "warning",//提示类型  success/info/warning/error
              }).then(() => {
                let url = `${this.BASEURL}/project/maas/modelsync/modelsynctasksave/startByMsPage`
                // 提交接口
                this.post(url, {
                  serverId: serverId,
                  serverName: serverName,
                  modelPath: modelPath
                }).then((res) => {
                  if (res?.hasOk) {
                    this.$message.success('生成同步任务成功!')
                    this.pushPage({
                      path: `/module/stander/edit/maas_model_warehouse_main/` + res.message,
                      title: '模型部署方案修改'
                    })
                  } else {
                    this.$message.error(`保存失败！${res.message}`)
                  }
                }).finally(() => {
                  // 设置按钮状态
                  btnObj.loading = false
                })
              }).catch(() => {
                //取消操作
                btnObj.loading = false
              });
            }
          }
        ],
        fullHeight: true,
      })
    },
    formatNumberWithUnit(value) {
      if (value >= 1e6) {
        return (value / 1e6).toFixed(1) + 'm';
      } else if (value >= 1e3) {
        return (value / 1e3).toFixed(1) + 'k';
      } else {
        return value.toString();
      }
    },
    formatDate(timestamp) {
      const date = new Date(timestamp * 1000);
      return `${date.getFullYear()}.${String(date.getMonth() + 1).padStart(2, '0')}.${String(date.getDate()).padStart(2, '0')}`;
    },
  }
}
</script>

<style scoped>
.model-card {
  width: 500px;
  display: flex;
  flex-direction: column;
  background-color: #F7F9FD;
  border-radius: 0.75rem;
  padding: 1.2rem;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  cursor: pointer;
}

.model-card:hover {
  border: solid #3b82f6;
}

.action-button {
  font-size: 18px;
  position: absolute;
  right: 0;
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  color: #94a3b8;
  cursor: pointer;
  padding: 0.25rem;
  border-radius: 0.25rem;
  transition: all 0.2s;
}

.action-button:hover {
  color: #2563eb;
  background-color: #e0f2fe;
}

.action-button:focus {
  outline: 2px solid #bfdbfe;
  outline-offset: 2px;
}

.model-title {
  font-size: 1.1rem;
  font-weight: 600;
  margin-bottom: 0.25rem;
  position: relative;
  color: #1f2937;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  display: flex;
  justify-content: space-between;
}

.model-path {
  color: #6b7280;
  font-size: 0.875rem;
  margin-bottom: 0.5rem;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.model-tags {
  display: flex;
  flex-wrap: nowrap;
  gap: 0.375rem;
  margin-bottom: 0.5rem;
  margin-top: 1rem;
  max-width: 100%;
  overflow-x: auto;
}

.model-tag {
  background: #eff6ff;
  color: #2563eb;
  padding: 0.25rem 0.5rem;
  border-radius: 0.75rem;
  font-size: 0.8125rem;
  display: flex;
  gap: 0.25rem;
  flex-shrink: 0;
}

.model-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 0.8125rem;
  margin-top: 1.25rem;
}

.right-aligned-info {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 1rem;
}

.fullName {
  color: #6b7280;
  display: flex;
  align-items: center;
  gap: 0.25rem;
}

.lastUpdatedTime {
  color: #6b7280;
  display: flex;
  align-items: center;
  gap: 1rem;
}

.downloads,
.stars {
  color: #6b7280;
  display: flex;
  align-items: center;
  gap: 0.25rem;
}

.download-icon,
.star-icon {
  margin-right: 2px;
  font-size: 10px;
}

.fas {
  font-size: 0.8125rem;
}
</style>
