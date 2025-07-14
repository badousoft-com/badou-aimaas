import { Has_Value } from '@/utils/index'
import GlobalConst from '@/service/global-const'

export default function completeValue (value) {
    // 判断是否有值
    if (!Has_Value(value)) return GlobalConst.view.value
    return value
}