<!-- App.vue -->
<template>
    <div id="app">
        <nav class="app-nav">
            <div class="nav-container">
                <span class="logo-text">AIMaaS-模型市场</span>
                <span class="logo-suffix"></span>
            </div>
        </nav>

        <div class="container">
            <!-- 左侧分类筛选 -->
            <div class="left-panel">
                <category-tabs
                    ref="categoryTabsRef"
                    :models="allModels"
                    @filter-changed="handleCategoryFilterChange"
                />
            </div>
            <!-- 右侧原有内容 -->
            <div class="right-panel">
               <search-filters
                   :search-query="searchQuery"
                   :selected-category="selectedCategory"
                   :sort-by="sortBy"
                   :all-models="allModels"
                   :total-count="totalItems"
                   :currentPage="currentPage"
                   @update:searchQuery="val => searchQuery = val"
                   @update:selectedCategory="val => selectedCategory = val"
                   @update:sortBy="val => {
                        this.sortBy = { value: val.value, sortBy: val.sortBy };
                   }"
                   @update:criterion="val => criterion = val"
                   @update:singleCriterion="val => singleCriterion = val"
                   @update:currentPage="val => currentPage = val"
                   @clear-filters="handleClearFilters"
                   @refreshData="fetchModelData"
                />
                <div v-if="isLoading" class="loading-spinner">
                    <div class="spinner"></div>
                    <p>加载中...</p>
                </div>
               <div class="model-list">
                        <template>
                            <model-card v-for="(model, index) in filteredModels"
                                :key="index"
                                :model="model"
                                @click="showModelDetail(model)" />
                        </template>
                    </div>
               <!-- 添加分页控件 -->
               <div class="pagination-controls">
                        <div class="pagination-info">
                            显示 {{ startItem }}-{{ endItem }} 条，共 {{ totalItems }} 条
                        </div>
                        <div class="pagination-buttons">
                            <button @click="prevPage" :disabled="currentPage === 1" class="page-button">
                                上一页
                            </button>
                            <span class="current-page">第 {{ currentPage }} 页</span>
                            <button @click="nextPage" :disabled="currentPage === totalPages" class="page-button">
                                下一页
                            </button>
                        </div>
                    </div>
            </div>
        </div>
        <!-- 添加弹窗组件 -->
        <model-modal v-if="selectedModel" :model="selectedModel" @close="selectedModel = null" />
        <div class="pointer returnBottom" title="返回" @click="back">
            <bd-icon name="back" style="font-size: 20px;"></bd-icon>
        </div>
    </div>
</template>

<script>
import ModelCard from './components/ModelCard.vue'
import SearchFilters from './components/SearchFilters.vue'
import ModelSkeleton from './components/ModelSkeleton.vue'
import ModelModal from './components/ModelModal.vue'
import CategoryTabs from './components/CategoryTabs.vue'

export default {
    components: {
        ModelModal,
        ModelCard,
        SearchFilters,
        ModelSkeleton,
        CategoryTabs
    },
    data() {
        return {
            allModels: [],
            selectedModel: null,
            currentPage: 1,
            itemsPerPage: 30,
            filteredModels: [],
            searchQuery: '',
            selectedCategory: 'all',
            sortBy: {
                value: 'overall',
                sortBy: 'Default'
            },
            isLoading: true,
            totalItems: 0,
            sortField: 'Default',
            isFetching: false,
            criterion: [],
            singleCriterion: [],
            currentTaskFilter: []
        }
    },
    computed: {
        totalPages() {
            return Math.ceil(this.totalItems / this.itemsPerPage)
        },
        paginatedModels() {
            return this.filteredModels;
        },
        startItem() {
            return Math.min((this.currentPage - 1) * this.itemsPerPage + 1, this.totalItems)
        },
        endItem() {
            return Math.min(this.currentPage * this.itemsPerPage, this.totalItems)
        }
    },
    mounted() {
        this.fetchModelData();
    },
    watch: {
        sortBy: {
            deep: true,
            handler() {
                this.currentPage = 1;
                this.fetchModelData();
            }
        },
        criterion: {
            handler() {
                this.currentPage = 1;
                this.fetchModelData();
            },
            deep: true
        },
        singleCriterion: {
            handler() {
                this.currentPage = 1;
                this.fetchModelData();
            },
            deep: true
        },
        currentPage(newVal) {
            this.fetchModelData();
        }
    },
    methods: {
        fetchModelData() {
            if (this.isFetching) return;
            this.isFetching = true;
            this.isLoading = true;
            let params = {
                page: this.currentPage,
                pageSize: this.itemsPerPage,
                searchKeyword: this.searchQuery,
                sortBy: this.sortBy?.sortBy || 'Default',
                criterion: [...this.criterion, ...this.currentTaskFilter], // 合并所有筛选条件
                singleCriterion: this.singleCriterion
            }
            let url = `${this.BASEURL}/project/maas/modelwarehouse/modelscopeproxylist/getPaginatedModels`
            this.post(url, params, {
                headers: {
                    'Content-Type': 'application/json'
                }
            }).then(res => {
                    if (res && res.models) {
                        this.filteredModels = res.models;
                        this.totalItems = res.totalCount;
                        // 滚动到顶部
                        const container = document.querySelector('.container');
                        if (container) {
                            container.scrollTop = 0;
                        }
                        console.log('接口返回数据:', res.models);
                    }
            })
            .finally(() => {
                this.isLoading = false;
                this.isFetching = false;
            });
        },
        back() {
            this.pushPage({
                path: `/module/stander/list/maas_model_warehouse_main/placeholder`,
                title: '上一页'
            })
        },
        showModelDetail(model) {
            this.selectedModel = model
        },
        nextPage() {
            if (this.currentPage < this.totalPages) {
                this.currentPage++;
                this.fetchModelData();
            }
        },
        prevPage() {
            if (this.currentPage > 1) {
                this.currentPage--;
                this.fetchModelData();
            }
        },
        handleClearFilters() {
            this.clearCategoryFilters();
            this.criterion = [];
            this.singleCriterion = [];
            this.searchQuery = '';
            this.sortBy = { value: 'overall', sortBy: 'Default' };
            this.currentTaskFilter = [];
        },
        clearCategoryFilters() {
            if (this.$refs.categoryTabsRef) {
                this.$refs.categoryTabsRef.clearSelections();
            }
        },
        handleCategoryFilterChange(criteria) {
            this.currentTaskFilter = criteria;
            this.currentPage = 1;
            this.fetchModelData();
        },
    }
}
</script>

<style scoped>
/* 全局样式 */
body {
    margin: 0;
    font-family: 'Inter', sans-serif;
    background-color: #f5f7fa;
    height: 100%;
}

.returnBottom {
    position: absolute;
    top: 4%;
    right: 0;
    transform: translateX(-50%) translateY(-50%);
    background-color: #c0c4cc;
    border-radius: 50%;
    opacity: 0.8;
    transition: all 0.3s;

    .bd-icon {
        font-size: 20px;
        width: 2em;
        height: 2em;
    }

    &:hover {
        opacity: 1;
    }
}

.app-nav {
    background: linear-gradient(90deg, #60a5fa, #2563eb, #1e40af);
    padding: 1rem;
    color: white;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.nav-container {
    max-width: 1200px;
    margin: 0 auto;
    display: flex;
    align-items: center;
}

.logo-text {
    font-size: 1.5rem;
    font-weight: 700;
}

.logo-suffix {
    margin-left: 0.5rem;
    color: #bfdbfe;
}

#app {
    display: flex;
    flex-direction: column;
    min-height: 100vh;
}

.container {
    max-width: 1536px;
    margin: 0 auto;
    padding: 1rem;
    display: flex;
    overflow: scroll;
    gap: 3rem;
    align-items: flex-start;
}

.left-panel {
    width: 301px;
    height: fit-content;
    line-height: 22px;
    padding-top: 1.5rem;
    position:sticky;
    margin-right: 2rem;
}

.right-panel {
    flex-grow: 1;
    height: auto;
    width: 1069px;
    overflow-y: auto;
}

.model-list {
    display: grid;
    gap: 1.5rem;
    margin-top: 1.5rem;
    grid-template-columns: repeat(2, 1fr);
}

@media (min-width: 768px) {
    .model-list {
        grid-template-columns: repeat(2, 1fr);
    }
}

/* 添加分页样式 */
.pagination-controls {
    margin-top: 2rem;
    padding: 1.5rem;
    background: white;
    border-radius: 1rem;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.pagination-info {
    color: #6b7280;
    font-size: 0.9rem;
}

.pagination-buttons {
    display: flex;
    align-items: center;
    gap: 1rem;
}

.page-button {
    padding: 0.5rem 1rem;
    background: #eff6ff;
    color: #2563eb;
    border: none;
    border-radius: 0.5rem;
    cursor: pointer;
    transition: all 0.2s ease;
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
    font-weight: 500
}

/* 加载画面样式 */
.loading-spinner {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(255, 255, 255, 0.8);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 9999;
}

.spinner {
    border: 4px solid #f3f3f3;
    border-top: 4px solid #3498db;
    border-radius: 50%;
    width: 40px;
    height: 40px;
    animation: spin 1s linear infinite;
}

@keyframes spin {
    0% { transform: rotate(0deg); }
    100% { transform: rotate(360deg); }
}

.loading-spinner p {
    margin-top: 10px;
    font-size: 16px;
}
</style>
