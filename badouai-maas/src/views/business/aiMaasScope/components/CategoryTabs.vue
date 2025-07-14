<template>
    <div class="category-tabs">
        <svg style="display: none;">
            <symbol id="icon-maasshouqi" viewBox="0 0 24 24">
                <path d="M7 10l5 5 5-5z" fill="currentColor"/>
            </symbol>
        </svg>

        <div class="tab-header">
            <div
                v-for="tab in tabs"
                :key="tab.key"
                :class="['tab-item', { active: activeTab === tab.key }]"
                @click="activateTab(tab.key)"
            >
                {{ tab.label }}
            </div>
        </div>

        <div class="tab-content">
            <!-- 搜索框 -->
            <div class="search-box" v-if="['tasks', 'popularTasks', 'libraries', 'others'].includes(activeTab)">
                <div class="search-box-wrapper">
                    <bd-icon name="search" class="custom-search-icon"></bd-icon>
                    <input
                        type="text"
                        placeholder="快速搜索"
                        v-model="searchKeyword"
                        @input="handleSearchInput"
                    />
                </div>
            </div>

            <!-- 任务分类 -->
            <div v-if="activeTab === 'tasks'" class="tab-pane">
                <div class="sub-category" v-if="filteredPopularTasks.length">
                    <h4 class="sub-category-title">热门任务</h4>
                    <ul class="category-list tag-list">
                        <li
                            v-for="(task, idx) in filteredPopularTasks"
                            :key="idx"
                            :title="task.label"
                            :class="[
                                'antd5-tag antd5-tag-checkable',
                                { 'antd5-tag-checkable-checked': isSelected('popularTasks', task.key) },
                                'task-color-hot'
                            ]"
                            @click="toggleSelection('popularTasks', task.key)"
                        >
                <span class="antd5-typography antd5-typography-ellipsis antd5-typography-ellipsis-single-line">
                    {{ task.label }}
                </span>
                        </li>
                    </ul>
                </div>
                <!-- 子分类列表 -->
                <div class="sub-category" v-for="(group, index) in filteredTaskGroups" :key="index">
                    <h4 class="sub-category-title">
                        {{ group.DomainChineseName }}
                    </h4>
                    <ul class="category-list tag-list">
                        <li
                            v-for="(task, idx) in group.Tasks"
                            :key="idx"
                            :title="task.label"
                            :class="[
                                'antd5-tag antd5-tag-checkable',
                                { 'antd5-tag-checkable-checked': isSelected('tasks', task.key) },
                                getTaskColorClass(group.DomainChineseName)
                                ]"
                            @click="toggleSelection('tasks', task.key)"
                        >
                <span class="antd5-typography antd5-typography-ellipsis antd5-typography-ellipsis-single-line">
                    {{ task.label }}
                </span>
                        </li>
                    </ul>
                </div>
            </div>

            <!-- 框架分类 -->
            <div v-if="activeTab === 'libraries'" class="tab-pane">
                <ul class="category-list tag-list">
                    <li
                        v-for="(libraries, idx) in filteredLibraries"
                        :key="idx"
                        :title="libraries"
                        :class="[
                            'antd5-tag antd5-tag-checkable',
                            { 'antd5-tag-checkable-checked': isMultiSelected('libraries', libraries) },
                            'framework-color'
                            ]"
                        @click="toggleMultiSelection('libraries', libraries)"
                    >
            <span class="antd5-typography antd5-typography-ellipsis antd5-typography-ellipsis-single-line">
                {{ libraries }}
            </span>
                    </li>
                </ul>
            </div>

            <!-- 其他分类 -->
            <div v-if="activeTab === 'others'" class="tab-pane">
                <div class="sub-category"
                     v-for="category in filteredOtherCategories"
                     :key="category.key"
                     v-if="category.options.length > 0"
                >
                    <!-- 添加折叠图标标题部分 -->
                    <h4 class="sub-category-title">
                        <!-- 左侧文本 -->
                        <span>{{category.label}}</span>
                        <!-- 右侧图标:仅对指定分类添加折叠图标 -->
                        <span v-if="['license', 'modelType', 'languages'].includes(category.key) &&
                                    (searchKeyword ? getCategoryOptions(category).length > category.maxDisplay : category.options.length > category.maxDisplay)"
                              class="collapse-icon"
                              @click.stop="toggleCollapse(category.key)">
                            <span class="acss-14363l7" :class="{ 'collapsed': isCollapsed(category.key) }">
                                <span role="img" class="anticon acss-z7bc5f">
                                    <svg width="1.3em" height="1.3em" fill="currentColor" aria-hidden="true" focusable="false">
                                        <use xlink:href="#icon-maasshouqi"></use>
                                    </svg>
                                </span>
                            </span>
                        </span>
                    </h4>
                    <ul class="category-list tag-list">
                        <!-- 根据分类类型处理选项渲染 -->
                        <template v-if="category.type === 'single'">
                            <!-- 对于有maxDisplay属性的单选分类（即license和modelType） -->
                            <template v-if="['license', 'modelType'].includes(category.key)">
                                <li
                                    v-for="(item, idx) in getCategoryOptions(category)"
                                    :key="idx"
                                    :title="item.label || item.value"
                                    :class="[
                                        'antd5-tag antd5-tag-checkable',
                                        { 'antd5-tag-checkable-checked': isSelected(category.key, item.value) },
                                        getCategoryColorClass(category.key)
                                        ]"
                                    @click="toggleSelection(category.key, item.value)"
                                >
                                    <span class="antd5-typography antd5-typography-ellipsis antd5-typography-ellipsis-single-line">
                                        <template v-if="item.icon">
                                            <i class="icon" :class="['icon-' + item.icon.value]"></i>
                                        </template>
                                        {{ item.label || item.value }}
                                    </span>
                                </li>
                            </template>
                            <!-- 其他单选分类保持原样 -->
                            <template v-else>
                                <li
                                    v-for="(item, idx) in getCategoryOptions(category)"
                                    :key="idx"
                                    :title="item.label || item.value"
                                    :class="[
                                        'antd5-tag antd5-tag-checkable',
                                        { 'antd5-tag-checkable-checked': isSelected(category.key, item.value) },
                                        getCategoryColorClass(category.key)
                                        ]"
                                    @click="toggleSelection(category.key, item.value)"
                                >
                                    <span class="antd5-typography antd5-typography-ellipsis antd5-typography-ellipsis-single-line">
                                        <template v-if="item.icon">
                                            <i class="icon" :class="['icon-' + item.icon.value]"></i>
                                        </template>
                                        {{ item.label || item.value }}
                                    </span>
                                </li>
                            </template>
                        </template>
                    </ul>
                    <!-- 多选类型处理 -->
                    <template v-if="category.type === 'multiple'">
                        <!-- 语种类别单独处理 -->
                        <ul v-if="category.key === 'languages'" class="category-list tag-list languages-list">
                            <li
                                v-for="(item, idx) in getCategoryOptions(category)"
                                :key="idx"
                                :title="item.label || item.value"
                                :class="[
                                    'antd5-tag antd5-tag-checkable',
                                    { 'antd5-tag-checkable-checked': isMultiSelected(category.key, item.value) },
                                    getCategoryColorClass(category.key)
                                    ]"
                                @click="toggleMultiSelection(category.key, item.value)"
                            >
                                <span class="antd5-typography antd5-typography-ellipsis antd5-typography-ellipsis-single-line">
                                    {{ item.label || item.value }}
                                </span>
                            </li>
                        </ul>
                        <!-- 其他多选分类保持原样 -->
                        <template v-else>
                            <ul class="category-list tag-list">
                                <li
                                    v-for="(item, idx) in getCategoryOptions(category)"
                                    :key="idx"
                                    :title="item.label || item.value"
                                    :class="[
                                        'antd5-tag antd5-tag-checkable',
                                        { 'antd5-tag-checkable-checked': isMultiSelected(category.key, item.value) },
                                        getCategoryColorClass(category.key)
                                        ]"
                                    @click="toggleMultiSelection(category.key, item.value)"
                                >
                                    <span class="antd5-typography antd5-typography-ellipsis antd5-typography-ellipsis-single-line">
                                        {{ item.label || item.value }}
                                    </span>
                                </li>
                            </ul>
                        </template>
                    </template>
                </div>
            </div>

            <!-- 组织分类 -->
            <div v-if="activeTab === 'organizations'" class="tab-pane">
                <div class="sub-category" v-for="(org, index) in organizations" :key="index">
                    <div class="organization-item"
                         :class="{ selected: isSelected('organizations', org.key) }"
                         @click="toggleSelection('organizations', org.key)">
                        <img :src="org.avatar" alt="Avatar" class="avatar">
                        <div class="org-details">
                            <h4 class="org-name">{{ org.label }}</h4>
                            <p class="org-model-count">已发布{{ org.modelNums }}个模型</p>
                        </div>
                    </div>
                </div>
                <!-- 添加组织分页控件 -->
                <div class="pagination-controls" v-if="activeTab === 'organizations'">
                    <div class="pagination-buttons">
                        <!-- 左箭头 -->
                        <button
                            class="page-button"
                            :disabled="orgCurrentPage <= 1"
                            @click="changePage(orgCurrentPage - 1)"
                        >
                            &lt;
                        </button>
                        <!-- 当前页 / 总页数 -->
                        <span class="current-page">
                            <input
                                type="number"
                                class="current-page-input"
                                v-model="orgCurrentPage"
                                @input="handlePageInput"
                            >
                            <span class="current-page-divider"> / </span>
                            <span class="total-pages">{{ orgTotalPages }}</span>
                        </span>
                        <!-- 右箭头 -->
                        <button
                            class="page-button"
                            :disabled="orgCurrentPage >= orgTotalPages"
                            @click="changePage(orgCurrentPage + 1)"
                        >
                            &gt;
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
export default {
    props: {
        models: {
            type: Array,
            default: () => []
        }
    },
    data() {
        return {
            tabs: [
                { key: 'tasks', label: '任务' },
                { key: 'libraries', label: '框架' },
                { key: 'others', label: '其他' },
                { key: 'organizations', label: '组织' }
            ],
            activeTab: 'tasks',
            searchKeyword: '',
            selectedCriteria: {
                popularTasks: null,
                tasks: null,
                license: null,
                tags: null,
                organizations: null
            },
            multiSelectedCriteria: {},
            taskDomains: [],
            popularTasks: [],
            organizations: [],
            orgPageSize: 12,
            orgCurrentPage: 1,
            orgTotalPages: 0,
            orgTotalItems: 0,
            orgFilteredOrganizations: [],
            libraries: [
                "GGUF", "Diffusers", "LoRA", "Llamafile", "ONNX", "PyTorch",
                "Safetensors", "TensorFlow", "Transformers", "Xinference", "MLX", "sentence-transformers"
            ],
            otherCategories: [
                {
                    key: 'standbyStatus',
                    label: '推理API状态',
                    options: [
                        { value: 'HOT', label: '热可用' },
                        { value: 'COLD', label: '待机态' }
                    ],
                    maxDisplay: 2,
                    type: 'single'
                },
                {
                    key: 'license',
                    label: '开源协议',
                    options: [],
                    maxDisplay: 12,
                    type: 'single'
                },
                {
                    key: 'modelType',
                    label: '结构',
                    options: [
                        { value: "qwen2", icon: { type: "color", value: "Qwen_Green" } },
                        { value: "qwen3", icon: { type: "color", value: "Qwen_Green" } },
                        { value: "llama", icon: { type: "color", value: "Deco_Green" } },
                        { value: "llama2", icon: { type: "color", value: "Deco_Green" } },
                        { value: "llama3", icon: { type: "color", value: "Deco_Green" } },
                        { value: "llama4", icon: { type: "color", value: "Deco_Green" } },
                        { value: "deepseek_v2", icon: { type: "color", value: "Qwen_Green" } },
                        { value: "deepseek_v3", icon: { type: "color", value: "Qwen_Green" } },
                        { value: "bert", icon: { type: "color", value: "Deco_Yellow" } },
                        { value: "qwen", icon: { type: "color", value: "Qwen_Green" } },
                        { value: "stable_diffusion", icon: { type: "color", value: "Text_1" } },
                        { value: "chatglm", icon: { type: "color", value: "Deco_Purple" } },
                        { value: "qwen2_5_vl", icon: { type: "color", value: "Deco_Purple" } },
                        { value: "internlm2", icon: { type: "color", value: "Deco_Green2" } },
                        { value: "mistral", icon: { type: "color", value: "Deco_Orange" } },
                        { value: "structbert", icon: { type: "color", value: "Deco_Red" } },
                        { value: "baichuan" },
                        { value: "xlm-roberta" },
                        { value: "internlm", icon: { type: "color", value: "Deco_Green2" } },
                        { value: "gemma" },
                        { value: "t5", icon: { type: "color", value: "Deco_Purple" } },
                        { value: "glm4" },
                        { value: "kimi_vl" }
                    ],
                    maxDisplay: 15,
                    type: 'single'
                },
                {
                    key: 'languages',
                    label: '语种',
                    options: [
                        { label: '中文', value: 'zh' },
                        { label: '英语', value: 'en' },
                        { label: '日语', value: 'ja' },
                        { label: '韩语', value: 'ko' },
                        { label: '俄语', value: 'ru' },
                        { label: '德语', value: 'de' },
                        { label: '法语', value: 'fr' },
                        { label: '意大利语', value: 'it' },
                        { label: '西班牙语', value: 'es' },
                        { label: '葡萄牙语', value: 'pt' },
                        { label: '荷兰语', value: 'nl' },
                        { label: '泰语', value: 'th' },
                        { label: '越南语', value: 'vi' },
                        { label: '波斯语', value: 'fa' },
                        { label: '土耳其语', value: 'tr' },
                        { label: '孟加拉语', value: 'bn' },
                        { label: '印尼语', value: 'id' },
                        { label: '印地语', value: 'hi' }
                    ],
                    maxDisplay: 6,
                    type: 'multiple'
                },
                {
                    key: 'tags',
                    label: '标签',
                    options: [
                        { label: "Alibaba", value: "Alibaba" },
                        { label: "chat", value: "chat" },
                        { label: "code", value: "code" },
                        { label: "ner", value: "ner" },
                        { label: "tts", value: "tts" },
                        { label: "mteb", value: "mteb" },
                        { label: "gptq", value: "gptq" },
                        { label: "4bit", value: "4bit" },
                        { label: "8bit", value: "8bit" }
                    ],
                    maxDisplay: Infinity,
                    type: 'multiple'
                }
            ],
            collapsedCategories: {
                license: true,
                modelType: true,
                languages: true
            }
        }
    },
    computed: {
        taskGroups() {
            return this.taskDomains.map(domain => ({
                DomainChineseName: domain.DomainChineseName,
                Tasks: domain.Tasks
            }))
        },

        //分页后的组织列表
        orgTotalPages() {
            return Math.ceil(this.orgTotalItems / this.orgPageSize);
        },
        filteredPopularTasks() {
            if (!this.searchKeyword) return this.popularTasks;
            const keyword = this.searchKeyword.toLowerCase();
            return this.popularTasks.filter(task =>
                task.label.toLowerCase().includes(keyword)
            );
        },
        filteredTaskGroups() {
            if (!this.searchKeyword) return this.taskGroups;
            const keyword = this.searchKeyword.toLowerCase();
            return this.taskGroups.map(group => ({
                ...group,
                Tasks: group.Tasks.filter(task =>
                    task.label.toLowerCase().includes(keyword)
                )
            })).filter(group => group.Tasks.length > 0);
        },
        filteredLibraries() {
            if (!this.searchKeyword) return this.libraries;
            const keyword = this.searchKeyword.toLowerCase();
            return this.libraries.filter(lib => lib.toLowerCase().includes(keyword));
        },
        filteredOtherCategories() {
            if (!this.searchKeyword) return this.otherCategories;

            const keyword = this.searchKeyword.toLowerCase();

            return this.otherCategories.map(category => {
                // 对所有类型分类统一处理选项过滤
                if (category.options && Array.isArray(category.options)) {
                    const filteredOptions = category.options.filter(option => {
                        const label = option.label?.toLowerCase() || '';
                        const value = option.value?.toLowerCase() || '';
                        return label.includes(keyword) || value.includes(keyword);
                    });

                    return {
                        ...category,
                        options: filteredOptions
                    };
                }
                return category;
            });
        }
    },
    methods: {
        // 获取任务和热门任务分类接口数据
        fetchTaskCategories() {
            let url = `${this.BASEURL}/project/maas/modelwarehouse/modelscopeproxylist/getTasks`;
            this.isLoading = true;
            this.post(url)
                .then(res => {
                    if (res && res.Success && res.Data && Array.isArray(res.Data.Domains)) {
                        const domains = res.Data.Domains || [];
                        this.taskDomains = domains.map(domain => ({
                            DomainChineseName: domain.DomainChineseName,
                            Tasks: (domain.Tasks || []).map(task => ({
                                key: task.Name,
                                label: task.ChineseName
                            }))
                        }));
                    } else {
                        console.error('接口返回异常:', errorMessage);
                    }
                })
                .catch(error => {
                    console.error('请求失败:', error);
                });
        },
        fetchPopularTasks() {
            let url = `${this.BASEURL}/project/maas/modelwarehouse/modelscopeproxylist/getPopularTasks`;
            this.post(url)
                .then(res => {
                    if (res && res.Success && res.Data && Array.isArray(res.Data.Tasks)) {
                        this.popularTasks = res.Data.Tasks.map(task => ({
                            key: task.Name,
                            label: task.ChineseName,
                        }));
                    } else {
                        console.error('接口返回异常:', res.Message || '未知错误');
                    }
                })
                .catch(error => {
                    console.error('请求失败:', error);
                });
        },
        //获取其他（开源协议）分类接口数据
        fetchLicenses() {
            let url = `${this.BASEURL}/project/maas/modelwarehouse/modelscopeproxylist/getLicenses`;
            this.post(url)
                .then(res => {
                    if (res && res.Success && res.Data && Array.isArray(res.Data.Licenses)) {
                        // 直接处理 Licenses 并更新 license 分类的 options
                        const licenseOptions = res.Data.Licenses.map(license => ({
                            key: license.Name,
                            label: license.Name,
                            value: license.Name
                        }));
                        const licenseCategory = this.otherCategories.find(cat => cat.key === 'license');
                        if (licenseCategory) {
                            licenseCategory.options = licenseOptions;
                        }
                    } else {
                        console.error('接口返回异常:', res.Message || '未知错误');
                    }
                })
                .catch(error => {
                    console.error('请求失败:', error);
                });
            },
        fetchOrgTags(pageSize = 12, pageNumber = 1) {
            let url = `${this.BASEURL}/project/maas/modelwarehouse/modelscopeproxylist/getOrganizations`;
            this.isLoading = true;
            this.post(url,{ pageSize, pageNumber })
                .then(res => {
                    if (res && res.Success && res.Data && Array.isArray(res.Data.list)) {
                        this.organizations = res.Data.list.map(org => ({
                            key: org.Name,
                            label: org.FullName,
                            avatar: org.Avatar,
                            modelNums: org.ModelNums
                        }));
                        this.orgTotalItems = res.Data.total;
                        this.orgTotalPages = Math.ceil(res.Data.total / this.orgPageSize);
                    } else {
                        console.error('接口返回异常:', res.Message || '未知错误');
                    }
                })
                .catch(error => {
                    console.error('请求失败:', error);
                })
                .finally(() => {
                    this.isLoading = false;
                });
        },
        changePage(page) {
            if (page < 1) {
                page = 1;
            } else if (page > this.orgTotalPages) {
                page = this.orgTotalPages;
            }
            this.orgCurrentPage = page;
            this.fetchOrgTags(this.orgPageSize, this.orgCurrentPage);
        },
        handlePageInput(event) {
            const page = parseInt(event.target.value, 10);
            if (!isNaN(page)) {
                this.changePage(page);
            } else {
                event.target.value = this.orgCurrentPage;
            }
        },
        activateTab(tabKey) {
            this.activeTab = tabKey;
            this.searchKeyword = '';
        },

        // 切换单选选择
        toggleSelection(category, value) {
            // 如果已经选中同一个，则取消选择
            if (this.isSelected(category, value)) {
                this.$set(this.selectedCriteria, category, null);
            } else {
                if (category === 'popularTasks' || category === 'tasks') {
                    this.$set(this.selectedCriteria, 'popularTasks', null);
                    this.$set(this.selectedCriteria, 'tasks', null);
                }
                this.$set(this.selectedCriteria, category, value);
            }
            this.applyFilters();
        },

        // 判断是否已选中某个选项（单选）
        isSelected(category, value) {
            return this.selectedCriteria[category] === value;
        },

        // 切换多选选择
        toggleMultiSelection(category, value) {
            if (!this.multiSelectedCriteria[category]) {
                this.$set(this.multiSelectedCriteria, category, []);
            }
            const index = this.multiSelectedCriteria[category].indexOf(value);
            if (index > -1) {
                // 移除已选项
                this.multiSelectedCriteria[category].splice(index, 1);
            } else {
                // 添加新选项
                this.multiSelectedCriteria[category].push(value);
            }
            this.applyFilters();
        },

        // 判断是否已选中某个选项（多选）
        isMultiSelected(category, value) {
            return this.multiSelectedCriteria[category] &&
                Array.isArray(this.multiSelectedCriteria[category]) &&
                this.multiSelectedCriteria[category].includes(value);
        },

        // 处理搜索输入
        handleSearchInput(e) {
            this.searchKeyword = e.target.value;
        },

        // 应用筛选条件
        applyFilters() {
            let criteria = [];
            // 处理单选条件
            Object.keys(this.selectedCriteria).forEach(category => {
                const value = this.selectedCriteria[category];
                if (value) {
                    let mappedCategory = category;
                    let predicate = "contains";
                    switch (category) {
                        case 'standbyStatus':
                            mappedCategory = 'standby_status';
                            break;
                        case 'modelType':
                            mappedCategory = 'model_type';
                            break;
                        case 'license':
                            mappedCategory = 'license';
                            break;
                        default:
                            if (category === 'popularTasks' || category === 'tasks') {
                                mappedCategory = 'tasks';
                            }
                            break;
                    }
                    criteria.push({
                        category: mappedCategory,
                        predicate: predicate,
                        values: [value]
                    });
                }
            });
            // 处理多选条件
            Object.keys(this.multiSelectedCriteria).forEach(category => {
                const values = this.multiSelectedCriteria[category];
                if (values && Array.isArray(values) && values.length > 0) {
                    let mappedCategory = category;
                    if (category === 'libraries') {
                        values.forEach(value => {
                            criteria.push({
                                category: 'libraries',
                                predicate: 'contains',
                                values: [value.toLowerCase()]
                            });
                        });
                    } else {
                        values.forEach(value => {
                            criteria.push({
                                category: mappedCategory,
                                predicate: 'contains',
                                values: [value]
                            });
                        });
                    }
                }
            });
            this.$emit('filter-changed', criteria);
        },
        getTaskColorClass(DomainChineseName) {
            switch (DomainChineseName) {
                case '多模态':
                    return 'task-color-multimodal';
                case '自然语言处理':
                    return 'task-color-nlp';
                case '语音':
                    return 'task-color-speech';
                case '计算机视觉':
                    return 'task-color-cv';
                case '科学计算':
                    return 'task-color-scientific';
                default:
                    return '';
            }
        },
        getCategoryColorClass(categoryKey) {
            switch (categoryKey) {
                case 'standbyStatus':
                    return 'task-color-standby';
                case 'license':
                    return 'task-color-license';
                case 'modelType':
                    return 'task-color-model-type';
                case 'languages':
                    return 'task-color-languages';
                case 'tags':
                    return 'task-color-tags';
                default:
                    return '';
            }
        },
        clearSelections() {
            // 清除单选状态
            Object.keys(this.selectedCriteria).forEach(key => {
                this.$set(this.selectedCriteria, key, null);
            });

            // 清除多选状态
            Object.keys(this.multiSelectedCriteria).forEach(key => {
                this.$set(this.multiSelectedCriteria, key, []);
            });
            this.applyFilters();
        },
        toggleCollapse(categoryKey) {
            this.$set(this.collapsedCategories, categoryKey, !this.collapsedCategories[categoryKey]);
        },
        isCollapsed(categoryKey) {
            return this.collapsedCategories[categoryKey] === true;
        },
        getCategoryOptions(category) {
            const targetCategory = this.filteredOtherCategories.find(c => c.key === category.key);
            if (!targetCategory) return [];
            const optionsToUse = targetCategory.options
            // 如果是折叠状态且选项数量超过maxDisplay，则只显示maxDisplay个
            if (this.isCollapsed(category.key) && optionsToUse.length > category.maxDisplay) {
                return optionsToUse.slice(0, category.maxDisplay);
            }
            return optionsToUse;
        }
    },
    watch: {
        models() {
        }
    },
    created() {
        this.fetchTaskCategories();
        this.fetchPopularTasks();
        this.fetchLicenses();
        this.fetchOrgTags(this.orgPageSize, this.orgCurrentPage);
    }
}
</script>

<style scoped>
.tab-pane {
    margin-top: 1rem;
}

.sub-category-title {
    height: 2.5rem;
    font-size: 1.1rem;
    font-weight: 600;
    color: #374151;
    margin-bottom: 0.75rem;
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.tag-list .antd5-tag-checkable {
    display: inline-block;
    padding: 0.4rem 0.8rem;
    background-color: #F7F9FD;
    border-radius: 0.5rem;
    cursor: pointer;
    font-size: 0.85rem;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    max-width: 100%;
}

.tag-list .antd5-tag-checkable:hover {
    background-color: #2563eb;
    color: white;
}

.tag-list .antd5-tag-checkable-checked {
    background-color: #2563eb;
    color: white;
}

.category-tabs {
    width: 301px;
}

.tab-header {
    font-size: 1.1rem;
    display: flex;
    justify-content: space-between;
    margin-bottom: 1rem;
}

.tab-item {
    flex: 1;
    text-align: center;
    padding: 0.5rem 0;
    cursor: pointer;
    font-weight: 500;
    color: #6b7280;
    border-bottom: 2px solid transparent;
    transition: all 0.2s ease;
}

.tab-item.active {
    color: #2563eb;
    border-bottom-color: #2563eb;
}

.tab-content {
    max-height: none;
}

.organization-item {
    display: flex;
    align-items: center;
    margin-bottom: 1rem;
    padding: 0.75rem;
    background-color: #F7F9FD ;
    border-radius: 0.5rem;
    transition: all 0.2s ease;
}

.organization-item:hover {
    background-color: #2563eb;
    color: white;
}

.organization-item.selected {
    background-color: #2563eb;
    color: white;
}

.organization-item:hover .org-name,
.organization-item:hover .org-model-count,
.organization-item.selected .org-name,
.organization-item.selected .org-model-count {
    color: white;
}

.avatar {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    margin-right: 1rem;
    filter: brightness(1);
    transition: filter 0.2s ease;
}
.org-details {
    flex: 1;
}

.org-name {
    font-size: 1rem;
    margin: 0;
}

.org-model-count {
    font-size: 0.85rem;
    color: #6b7280;
    margin: 0.5rem 0 0;
}

.pagination-controls {
    margin-top: 2rem;
    padding: 1.5rem;
    background: white;
    border-radius: 1rem;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    display: flex;
    justify-content: center;
    align-items: center;
}

.pagination-buttons {
    display: flex;
    align-items: center;
    gap: 0.5rem;
}

.page-button {
    background: #eff6ff;
    color: black;
    border: none;
    border-radius: 0.5rem;
    cursor: pointer;
    transition: all 0.2s ease;
    padding: 0.5rem 1rem;
}

.page-button:hover:not(:disabled) {
    background: #3b82f6;
    color: white;
}

.page-button:disabled {
    background: #f3f4f6;
    color: #d1d5db;
    cursor: not-allowed;
}

.current-page {
    color: #374151;
    font-weight: 500;
    display: flex;
    align-items: center;
}

.current-page-input {
    margin: 0 0.5rem;
    font-size: 1rem;
    width: 50px;
    height: 100%;
    border: 1px solid #d9d9d9;
    border-radius: 6px;
    outline: none;
    text-align: center;
    padding: 0 6px;
    transition: border-color 0.2s;
}

.current-page-input::-webkit-outer-spin-button,
.current-page-input::-webkit-inner-spin-button {
    -webkit-appearance: none;
    margin: 0;
}

.current-page-divider {
    margin: 0 0.5rem;
    font-size: 1rem;
}

.total-pages {
    font-size: 1rem;
}

.search-box {
    margin-bottom: 1rem;
}

.search-box .search-box-wrapper {
    display: flex;
    align-items: center;
    position: relative;
    height: 40px;
}

.custom-search-icon {
    position: absolute;
    left: 1rem;
    pointer-events: none;
    color: #3b82f6;
    font-size: 1rem;
    z-index: 1;
}

.search-box input {
    width: 270px;
    height: 30px;
    padding-left: 2.5rem;
    border: none;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    border-radius: 0.75rem;
    font-size: 0.9rem;
    background: #F7F9FD !important;
}

.search-box input:focus {
    outline: none;
    border: 1px solid #3B82F6;
    box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.2);
}

.search-box input:hover {
    border: 1px solid #3B82F6;
}

.sub-category {
    margin-bottom: 1.5rem;
    min-height: 4rem;
    overflow: hidden;
}

.sub-category h4 {
    font-size: 0.9rem;
    font-weight: 600;
    color: #374151;
    margin-bottom: 0.75rem;
}

.category-list {
    list-style: none;
    padding: 0;
    margin: 0;
    display: flex;
    flex-wrap: wrap;
    gap: 0.5rem;
}

.category-list.multi-select {
    flex-direction: column;
}

.category-item {
    padding: 0.4rem 0.75rem;
    background: #f3f4f6;
    color: #4b5563;
    border-radius: 0.5rem;
    cursor: pointer;
    font-size: 0.85rem;
    transition: all 0.2s ease;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    max-width: 100%;
    display: inline-block;
}

.category-item:hover {
    background: #e5e7eb;
}

.category-item.selected {
    background: #2563eb;
    color: white;
}

/* 热门任务 */
.task-color-hot {
    color: #3B29B3;
}

/* 多模态 */
.task-color-multimodal {
    color: #CB8C0F;
}

/* 自然语言处理 */
.task-color-nlp {
    color: #3B29B3;
}

/* 语音 */
.task-color-speech {
    color: #329E87;
}

/* 计算机视觉 */
.task-color-cv {
    color: #CE642E;
}

/* 科学计算 */
.task-color-scientific {
    color: #5B5980;
}

.tag-list .antd5-tag-checkable {
    display: inline-block;
    padding: 0.4rem 0.8rem;
    background-color: #F7F9FD;
    border-radius: 0.5rem;
    cursor: pointer;
    font-size: 0.85rem;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    max-width: 100%;
}

.tag-list .antd5-tag-checkable:hover {
    background-color: #2563eb;
    color: white;
}

.tag-list .antd5-tag-checkable-checked {
    background-color: #2563eb;
    color: white;
}


.acss-14363l7 {
    display: flex;
    width: 1rem;
    height: 1rem;
    align-items: center;
    margin-left: 0.5rem;
    transition: transform 0.3s ease , background-color 0.3s ease;
    transform: rotate(0deg); /* 默认就是倒三角 */
}

.anticon {
    color: grey;
    font-size: 1rem;
    transition: transform 0.3s ease;
}

.acss-14363l7.collapsed {
    transform: rotate(0deg);
}

/* 添加展开状态时图标的背景样式 */
.acss-14363l7:not(.collapsed) {
    transform: rotate(180deg); /* 展开时变为正三角 */
    background-color: #f4eff9;
    width: 1rem;
    height: 1rem;
    display: inline-flex;
    align-items: center;
    justify-content: center;
    border-radius: 0.25rem; /* 展开状态有圆角 */
}

/* 推理API状态 */
.task-color-standby {
    color: #3B29B3;
}

/* 开源协议 */
.task-color-license {
    color: #CB8C0F;
}

/* 结构 */
.task-color-model-type {
    color: #3B29B3;
}

/* 语种 */
.task-color-languages {
    color: #329E87;
}

/* 标签 */
.task-color-tags {
    color: #CE642E;
}

.languages-list {
    display: flex;
    flex-wrap: wrap;
    gap: 0.5rem;
}

.languages-list .antd5-tag-checkable {
    flex: 0 0 calc(33.333% - 0.833rem);
    max-width: calc(33.333% - 0.833rem);
    box-sizing: border-box;
}

.framework-color {
    color: #3B29B3;
}
</style>
