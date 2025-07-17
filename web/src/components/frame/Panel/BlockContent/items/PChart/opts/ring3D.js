/*
 * @FilePath: @/components/frame/Panel/BlockContent/items/PChart/opts/ring3D.js
 * @Description: 3D 环形图
 */
import { Set_Ring_3D } from '@/components/frame/Panel/BlockContent/items/PChart/setUtils/setRing3D.js'
export default {
    use3d: true,
    getOptions (data) {
        let series = data?.data?.series
        if (!series) return null
        let config = {
            fontSize: parseFloat(this.getRootFontSize())
        }
        let option = {
            ...Set_Ring_3D.call(this, series, config),
            tooltip: {
                trigger: 'item',
                confine: true // 将此限制打开后tooltip将不再溢出
            },
        }
        return option
    }
}