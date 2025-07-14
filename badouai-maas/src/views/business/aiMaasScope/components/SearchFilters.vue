<!-- SearchFilters.vue -->
<template>
    <div class="search-filters">
        <div class="filter-container">
            <div class="search-input">
              <span class="antd5-input-wrapper">
                <span class="antd5-input-affix-wrapper">
                    <!-- 搜索图标 -->
                  <span class="antd5-input-prefix">
                      <button class="antd5-btn-icon" @click="refreshData">
                            <bd-icon name="search"></bd-icon>
                      </button>
                  </span>
                    <!-- 搜索输入框 -->
                  <input
                      :value="searchQuery"
                      @input="handleSearchInputWithoutRefresh"
                      @keyup.enter="handleEnterKey"
                      type="text"
                      :placeholder="searchPlaceholder"
                      class="antd5-input"
                  >
                    <!-- 清除按钮 -->
                  <span v-if="searchQuery" span class="antd5-input-suffix" style="display: flex;">
                      <button class="antd5-input-clear-icon" @click="clearSearch">
                          <svg viewBox="0 0 24 24" style="width: 11px; height: 11px; fill: currentColor;">
                             <path d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z"/>
                          </svg>
                      </button>
                  </span>
                </span>
              </span>
            </div>

            <!-- 排序选择 -->
            <div class="sort-select">
                <div v-for=
                         "(category, index) in sortCategories"
                     :key="index"
                     class="sort-category"
                     @mouseenter="toggleDropdown(category)"
                     @mouseleave="hideDropdown(category)">
                    <div class="sort-title" @click="toggleDropdown(category)"
                    >
                        {{ category.name === '综合排序' ? displaySortName : category.name }}
                        <!-- 添加下拉框标识 -->
                        <i v-if="category.name !== '综合排序'" class="fas fa-caret-down dropdown-icon"></i>
                    </div>
                    <div v-if="category.open" class="dropdown-options">
                        <div v-for="option in category.options" :key="option.value" class="option-item" @click="handleOptionChange(category, option)">
                            <!-- 综合排序 -->
                            <input
                                v-if="category.name === '综合排序'"
                                type="radio"
                                :id="option.value"
                                :value="option.value"
                                v-model="selectedOptions['综合排序']"
                            >

                            <!-- 其他分类 -->
                            <input
                                v-else
                                type="checkbox"
                                :id="option.value"
                                :value="option.value"
                                v-model="selectedOptions[category.name]"
                            >

                            <div class="option-text">
                                <label :for="option.value" class="option-label">{{ option.label }}</label>
                                <p class="option-description">{{ option.description }}</p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- 重置按钮 -->
            <button
                class="reset-btn"
                @click="resetFilters"
            >
                <i class="fas fa-rotate"></i> 重置
            </button>
        </div>
    </div>
</template>

<script>
export default {
    props: ['searchQuery', 'selectedCategory', 'sortBy','allModels', 'totalCount','currentPage'],
    data() {
        return {
            displaySortName: '综合排序',
            sortCategories: [
                {
                    name: '支持体验',
                    open: false,
                    options: [
                        { label: '推理 API-Inference', value: 'inference', description: '支持通过标准推理API接口迅速调用' },
                        { label: '模型 Demo 体验', value: 'demo', description: '支持自定义输入内容，一键体验模型推理效果' },
                        { label: 'Restful API 体验', value: 'restful', description: '提供Restful API集成能力，快速实现概念验证' }
                    ]
                },
                {
                    name: '支持训练',
                    open: false,
                    options: [
                        { label: '零门槛训练微调 FlexTrain', value: 'flextrain', description: '结合特定数据集，基于预训练模型进行零代码云上训练' },
                        { label: 'SDK 编程训练微调', value: 'sdk', description: '通过魔搭 SDK 编程，结合给定数据集对预训练模型实现灵活定制化训练' },
                        { label: '阿里云PAI Model Gallery训练', value: 'pai', description: '使用阿里云PAI快速低代码训练模型' }
                    ]
                },
                {
                    name: '支持评测',
                    open: false,
                    options: [
                        { label: '阿里云PAI Model Gallery评测', value: 'pai_eval', description: '使用阿里云PAI快速低代码评测模型' }
                    ]
                },
                {
                    name: '支持部署',
                    open: false,
                    options: [
                        { label: '快速部署 SwingDeploy', value: 'swingdeploy', description: '支持零代码一键部署模型到指定云推理平台' },
                        { label: '阿里云PAI Model Gallery部署', value: 'pai_deploy', description: '使用阿里云PAI快速低代码部署模型' }
                    ]
                },
                {
                    name: '综合排序',
                    open: false,
                    options: [
                        { label: '综合排序', value: 'overall', description: '' ,sortBy: 'Default'},
                        { label: '下载量排序', value: 'downloads', description: '' ,sortBy: 'DownloadsCount'},
                        { label: '喜欢数排序', value: 'likes', description: '' ,sortBy: 'StarsCount' },
                        { label: '最近更新', value: 'recent', description: '' ,sortBy: 'GmtModified' }
                    ]
                }
            ],
            selectedOptions: {
                '支持体验': [],
                '支持训练': [],
                '支持评测': [],
                '支持部署': [],
                '综合排序': ''
            }
        };
    },
    computed: {
        searchPlaceholder() {
            return `输入关键字搜索模型（共${this.$props.totalCount || 0}个）`;
        }
    },
    methods: {
        handleSearchInputWithoutRefresh(e) {
            this.$emit('update:searchQuery', e.target.value);
        },
        handleEnterKey() {
            this.$emit('refreshData');
        },
        clearSearch() {
            this.$emit('update:searchQuery', '');
            this.$emit('refreshData');
        },
        refreshData() {
            this.$emit('refreshData');
        },
        toggleDropdown(category) {
            this.sortCategories.forEach(cat => {
                if (cat !== category) cat.open = false;
            });
            category.open = true;
        },
        hideDropdown(category) {
            category.open = false;
        },
        // 生成固定结构的 SingleCriterion 模板
        createSingleCriterion(category) {
            return {
                dateType: "int",
                intValue: 1,
                category: category,
                predicate: "equal",
            };
        },
        // 生成固定结构的 Criterion 模板
        createCriterion(category, values) {
            return {
                category: category,
                predicate: "contains",
                values: values
            };
        },
        // 选项与条件的映射关
        getConditionConfig(optionValue) {
            const configMap = {
                // 支持体验
                inference: {
                    singleCriterion: this.createSingleCriterion("inference_type")
                },
                demo: {
                    criterion: this.createCriterion("demo_service", [JSON.stringify({ enabled: true })]),
                    singleCriterion: this.createSingleCriterion("support_experience")
                },
                restful: {
                    singleCriterion: this.createSingleCriterion("support_api_inference")
                },
                // 支持训练
                flextrain: {
                    singleCriterion: this.createSingleCriterion("support_flex_train")
                },
                sdk: {
                    singleCriterion: this.createSingleCriterion("support_finetuning")
                },
                pai: {
                    criterion: this.createCriterion("support_pai_model_gallery", ["train"])
                },
                // 支持评测
                pai_eval: {
                    criterion: this.createCriterion("support_pai_model_gallery", ["evaluation"])
                },
                // 支持部署
                swingdeploy: {
                    singleCriterion: this.createSingleCriterion("support_deployment")
                },
                pai_deploy: {
                    criterion: this.createCriterion("support_pai_model_gallery", ["deploy"])
                }
            };
            return configMap[optionValue] || {};
        },

        generateCriteria() {
            let criterion = [];
            let singleCriterion = [];
            Object.values(this.selectedOptions).flat().forEach(value => {
                const config = this.getConditionConfig(value);
                if (config.criterion) criterion.push(config.criterion);
                if (config.singleCriterion) singleCriterion.push(config.singleCriterion);
            });
            return { criterion, singleCriterion };
        },
        handleOptionChange(category, option) {
            if (category.name === '综合排序') {
                this.selectedOptions['综合排序'] = option.value;
                this.displaySortName = option.label;
                this.sortCategories.forEach(cat => {
                    if (cat.name !== '综合排序') cat.open = false;
                });
                this.$emit('update:sortBy', {
                    value: option.value,
                    sortBy: option.sortBy || 'Default'
                });
            } else {
                const isChecked = this.selectedOptions[category.name].includes(option.value);
                if (!isChecked) {
                    this.$set(this.selectedOptions[category.name], this.selectedOptions[category.name].length, option.value);
                } else {
                    const index = this.selectedOptions[category.name].indexOf(option.value);
                    this.selectedOptions[category.name].splice(index, 1);
                    this.$set(this.selectedOptions, category.name, [...this.selectedOptions[category.name]]);
                }
            }
            const { criterion, singleCriterion } = this.generateCriteria();
            this.$emit('update:criterion', [...new Set(criterion)]);
            this.$emit('update:singleCriterion', [...new Set(singleCriterion)]);
            this.$emit('update:currentPage', 1);
            this.$emit('refreshData');
        },
        resetFilters() {
            this.sortCategories.forEach(category => {
                category.open = false;
                if (category.name !== '综合排序') {
                    this.selectedOptions[category.name] = [];
                } else {
                    this.selectedOptions[category.name] = '';
                }
            });
            this.displaySortName = '综合排序';
            const { criterion, singleCriterion } = this.generateCriteria();
            this.$emit('update:sortBy', {
                value: 'overall',
                sortBy: 'Default'
            });
            this.$emit('update:currentPage', 1);
            this.$emit('update:searchQuery', '');
            this.$emit('update:criterion', [...new Set(criterion)]);
            this.$emit('update:singleCriterion', [...new Set(singleCriterion)]);
            this.$emit('clear-filters');
            this.$emit('clear-category-filters');
            this.$emit('update:currentPage', 1);
            this.$nextTick(() => {
                this.$emit('refreshData');
            });
        }
    }
};
</script>


<style scoped>
.search-input {
    width: 100%;
}

.antd5-input-wrapper {
    display: flex;
    align-items: center;
    border: none;
    padding-right: 18px;
    border-radius: 0.75rem;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    background-color: #F7F9FD !important;
    background-color: white;
    width: 437px;
}

.antd5-input-wrapper:hover {
    border: 1px solid #3b82f6;
}

.antd5-input-wrapper:focus-within {
    border-color: #3b82f6;
    box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.2);
}

.antd5-input-affix-wrapper {
    position: relative;
    display: flex;
    align-items: center;
    width: 100%;
}

.antd5-input {
    flex: 1;
    height: 32px;
    font-size: 14px;
    line-height: 1.5;
    color: rgba(0, 0, 0, 0.85);
    background-color: transparent;
    border: none;
    outline: none;
}

.antd5-input-prefix {
    display: flex;
    padding: 0 9px;
    border: none;
}
.antd5-btn-icon {
    color: #3b82f6;
    font-size: 14px;
    border: none;
    background-color: #F7F9FD !important;
}

.antd5-input-suffix {
    display: flex;
    justify-content: center;
    width: 10px;
}

.antd5-input-clear-icon {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    width: 1px;
    border-radius: 50%;
    border: none;
    background-color: #80808069;
    color: white;
    font-size: 14px;
    cursor: pointer;
    transition: all 0.2s ease;
}

.antd5-input-clear-icon:hover {
    background-color: #6b7280;
}


.acss-84xg9m .acss-tfirdg {
    padding: 6px 12px;
    cursor: pointer;
}

.acss-84xg9m .acss-tfirdg:hover {
    background-color: #f0f0f0;
}

.acss-14yh6b8 {
    padding: 8px 12px;
    color: #666;
    cursor: pointer;
}

.acss-14yh6b8:hover {
    background-color: #e6e6e6;
}

.search-filters {
    position: sticky;
    padding: 1.5rem;
    margin-bottom: 1.5rem;
}

.filter-container {
    display: flex;
    gap: 0.5rem;
    flex-wrap: wrap;
    transform: translateX(-20px);
}

.search-input {
    position: relative;
    flex: 1;
    max-width: 900px;
}

.search-input input {
    width: 100%;
    font-size: 1rem;
    outline: none;
}

.sort-select {
    display: flex;
    align-items: center;
    white-space: nowrap;
}

.sort-category {
    position: relative;
}

.sort-title {
    background: white;
    font-size: 12px;
    cursor: pointer;
    display: flex;
    align-items: center;
    min-width: 80px;
}

.sort-title:hover, .sort-title:focus {
    color: #3b82f6;
}

.dropdown-options {
    position: absolute;
    top: 100%;
    left: 0;
    z-index: 1000;
    border: 1px solid #ddd;
    border-radius: 1rem;
    background: white;
    max-height: 300px;
    overflow-y: auto;
    padding-top: 1rem;
    padding-bottom: 0.8rem;
}

.option-item {
    display: flex;
    align-items: flex-start;
    padding: 0.5rem 1rem;
    border-bottom: 1px solid #ddd;
    gap: 0.5rem;
    margin-top: 0.5rem;
    margin-bottom: 0.25rem;
}

.option-text {
    display: flex;
    flex-direction: column;
}

.option-label {
    font-size: 0.9rem;
}

.option-description {
    font-size: 12px;
    color: #6b7280;
    margin-top: 0.4rem;
}

.option-item:last-child {
    border-bottom: none;
}

.option-item:hover .option-label,
.option-item:hover .option-description {
    color: #3b82f6;
}

.option-item:hover input[type="checkbox"],
.option-item:hover input[type="radio"] {
    border-color: #3b82f6;
}

.option-item input[type="checkbox"]:checked ~ .option-text .option-label,
.option-item input[type="radio"]:checked ~ .option-text .option-label,
.option-item input[type="checkbox"]:checked ~ .option-text .option-description,
.option-item input[type="radio"]:checked ~ .option-text .option-description {
    color: #3b82f6;
}

.option-item input[type="checkbox"]:checked::after,
.option-item input[type="radio"]:checked::after {
    background-color: #3b82f6;
}

.option-item input[type="checkbox"]:checked,
.option-item input[type="radio"]:checked {
    border-color: #3b82f6;
}

.option-item input[type="radio"],
.option-item input[type="checkbox"] {
    appearance: none;
    width: 16px;
    height: 16px;
    border: 1px solid #999;
    border-radius: 4px;
    cursor: pointer;
    position: relative;
}

.option-item input[type="radio"]:checked::after {
    content: "";
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    width: 8px;
    height: 8px;
    background-color: #3b82f6;
}

.option-item input[type="checkbox"]:checked::after {
    content: "";
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    width: 8px;
    height: 8px;
    background-color: #3b82f6;;
}

.option-item label {
    font-size: 14px;
    margin: 0;
}

.reset-btn {
    padding: 0.75rem 1.5rem;
    border: none;
    border-radius: 1rem;
    cursor: pointer;
    transition: background 0.3s ease;
}

.reset-btn:hover {
    background: #f0f0f0;
}
</style>

