<template>
    <div class="bg-white rounded-3xl shadow-xl overflow-hidden">
        <div class="p-4">
            <div class="animate-pulse">
                <div class="h-8 bg-gray-200 rounded-full w-3/4 mb-2"></div>
                <div class="h-6 bg-gray-200 rounded-full w-2/3 mb-3"></div>

                <div class="flex flex-wrap gap-2 mb-4">
                    <div class="h-4 bg-gray-200 rounded-full w-1/6" v-for="i in 3" :key="i"></div>
                </div>

                <div class="flex justify-between items-center text-sm">
                    <div class="h-4 bg-gray-200 rounded-full w-1/4"></div>
                    <div class="h-4 bg-gray-200 rounded-full w-1/6"></div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
export default {}
</script>    

<style scoped>
.model-skeleton {
  background: #fff;
  border-radius: 1.5rem;
  padding: 1.5rem;
  margin-bottom: 1.5rem;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.skeleton-header {
  height: 1.5rem;
  width: 70%;
  background: #f3f4f6;
  border-radius: 0.5rem;
  margin-bottom: 1rem;
  animation: pulse 1.5s infinite;
}

.skeleton-line {
  height: 0.875rem;
  width: 85%;
  background: #f3f4f6;
  border-radius: 0.5rem;
  margin-bottom: 1.5rem;
  animation: pulse 1.5s infinite;
}

.skeleton-tags {
  display: flex;
  gap: 0.5rem;
  margin-bottom: 1.5rem;
}

.skeleton-tag {
  width: 4rem;
  height: 1.5rem;
  background: #f3f4f6;
  border-radius: 1rem
}
</style>