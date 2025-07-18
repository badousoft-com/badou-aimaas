import { Has_Value } from '@/utils'
import { Find_By_ClassName } from '@/utils/find-dom'

export default function fixedFormWidth (el, binding) {
    if (!Has_Value(binding?.value)) return
    // 查看页面不需要设置label固定宽度，但是一种情况特殊，就是查看模式下的表格展示形式需要固定label宽度
    if (!(typeof el.className === 'string' &&
          el.className.includes('is-view') &&
          !el.className.includes('is-table'))) {
        // 设置透明度：
        //     先将透明度置0，等待排版结束后再置1，避免页面显示后还抖动调整排版
        //     结合设置css上的transition：确保切换时过渡动效
        // 获取当前值是否变更状态，避免滚动页面时频繁触发，忽闪忽闪
        let noChangeStatus = binding.oldValue && binding.value === binding.oldValue
        if (!noChangeStatus) {
            el.style.opacity = 0
        }
        setTimeout(() => {
            el.querySelectorAll('.el-form-item').forEach(i => {
                // 获取标签dom对象
                let labelEl = Find_By_ClassName(i, 'el-form-item__label')
                // 根据标签对象下的innerText判断是否存在标签
                //     存在则使用传入的指定标签宽度，不存在则设置为0
                let labelWidth = labelEl?.innerText ? `${parseInt(binding.value)}px` : '0px'
                labelEl && (labelEl.style.width = `${labelWidth}`)
                Find_By_ClassName(i, 'el-form-item__content').style.width = `calc(100% - ${labelWidth})`
            })
            if (!noChangeStatus) {
                el.style.opacity = 1
            }
        }, 10) // 加个延时，确保元素存在，才可以修改标签宽度
    }
}