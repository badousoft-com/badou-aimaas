const Verify_IDCard = {
    /* 省,直辖市代码表 */
    provinceAndCitys: {
        11: '北京',
        12: '天津',
        13: '河北',
        14: '山西',
        15: '内蒙古',
        21: '辽宁',
        22: '吉林',
        23: '黑龙江',
        31: '上海',
        32: '江苏',
        33: '浙江',
        34: '安徽',
        35: '福建',
        36: '江西',
        37: '山东',
        41: '河南',
        42: '湖北',
        43: '湖南',
        44: '广东',
        45: '广西',
        46: '海南',
        50: '重庆',
        51: '四川',
        52: '贵州',
        53: '云南',
        54: '西藏',
        61: '陕西',
        62: '甘肃',
        63: '青海',
        64: '宁夏',
        65: '新疆',
        71: '台湾',
        81: '香港',
        82: '澳门',
        91: '国外'
    },

    /* 每位加权因子 */
    powers: ['7', '9', '10', '5', '8', '4', '2', '1', '6', '3', '7', '9', '10', '5', '8', '4', '2'],

    /* 第18位校检码 */
    parityBit: ['1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'],

    /* 性别 */
    genders: { male: '男', female: '女' },

    /* 校验地址码 */
    checkAddressCode: function (addressCode) {
        let check = /^[1-9]\d{5}$/.test(addressCode)
        if (!check) return false
        if (Verify_IDCard.provinceAndCitys[parseInt(addressCode.substring(0, 2))]) {
            return true
        } else {
            return false
        }
    },

    /* 校验日期码 */
    checkBirthDayCode: function (birDayCode) {
        let check = /^[1-9]\d{3}((0[1-9])|(1[0-2]))((0[1-9])|([1-2][0-9])|(3[0-1]))$/.test(birDayCode)
        if (!check) return false
        let yyyy = parseInt(birDayCode.substring(0, 4), 10)
        let mm = parseInt(birDayCode.substring(4, 6), 10)
        let dd = parseInt(birDayCode.substring(6), 10)
        let xdata = new Date(yyyy, mm - 1, dd)
        if (xdata > new Date()) {
            return false // 生日不能大于当前日期
        } else if ((xdata.getFullYear() === yyyy) && (xdata.getMonth() === mm - 1) && (xdata.getDate() === dd)) {
            return true
        } else {
            return false
        }
    },

    /* 计算校检码 */
    getParityBit: function (idCardNo) {
        let id17 = idCardNo.substring(0, 17)
        /* 加权 */
        let power = 0
        for (let i = 0; i < 17; i++) {
            power += parseInt(id17.charAt(i), 10) * parseInt(Verify_IDCard.powers[i])
        }
        /* 取模 */
        let mod = power % 11
        return Verify_IDCard.parityBit[mod]
    },

    /* 验证校检码 */
    checkParityBit: function (idCardNo) {
        let parityBit = idCardNo.charAt(17).toUpperCase()
        if (Verify_IDCard.getParityBit(idCardNo) === parityBit) {
            return true
        } else {
            return false
        }
    },

    /* 校验15位或18位的身份证号码 */
    checkIdCardNo: function (idCardNo) {
        // 15位和18位身份证号码的基本校验
        let check = /^\d{15}|(\d{17}(\d|x|X))$/.test(idCardNo)
        if (!check) return false
        // 判断长度为15位或18位
        if (idCardNo.length === 15) {
            return Verify_IDCard.check15IdCardNo(idCardNo)
        } else if (idCardNo.length === 18) {
            return Verify_IDCard.check18IdCardNo(idCardNo)
        } else {
            return false
        }
    },

    // 校验15位的身份证号码
    check15IdCardNo: function (idCardNo) {
        // 15位身份证号码的基本校验
        let check = /^[1-9]\d{7}((0[1-9])|(1[0-2]))((0[1-9])|([1-2][0-9])|(3[0-1]))\d{3}$/.test(idCardNo)
        if (!check) return false
        // 校验地址码
        let addressCode = idCardNo.substring(0, 6)
        check = Verify_IDCard.checkAddressCode(addressCode)
        if (!check) return false
        let birDayCode = '19' + idCardNo.substring(6, 12)
        // 校验日期码
        return Verify_IDCard.checkBirthDayCode(birDayCode)
    },

    // 校验18位的身份证号码
    check18IdCardNo: function (idCardNo) {
        // 18位身份证号码的基本格式校验
        let check = /^[1-9]\d{5}[1-9]\d{3}((0[1-9])|(1[0-2]))((0[1-9])|([1-2][0-9])|(3[0-1]))\d{3}(\d|x|X)$/.test(idCardNo)
        if (!check) return false
        // 校验地址码
        let addressCode = idCardNo.substring(0, 6)
        check = Verify_IDCard.checkAddressCode(addressCode)
        if (!check) return false
        // 校验日期码
        let birDayCode = idCardNo.substring(6, 14)
        check = Verify_IDCard.checkBirthDayCode(birDayCode)
        if (!check) return false
        // 验证校检码
        return Verify_IDCard.checkParityBit(idCardNo)
    },

    formateDateCN: function (day) {
        let yyyy = day.substring(0, 4)
        let mm = day.substring(4, 6)
        let dd = day.substring(6)
        return yyyy + '-' + mm + '-' + dd
    },

    // 获取信息
    getIdCardInfo: function (idCardNo) {
        let idCardInfo = {
            gender: '', // 性别
            birthday: '' // 出生日期(yyyy-mm-dd)
        }
        if (idCardNo.length === 15) {
            let aday = '19' + idCardNo.substring(6, 12)
            idCardInfo.birthday = Verify_IDCard.formateDateCN(aday)
            if (parseInt(idCardNo.charAt(14)) % 2 === 0) {
                idCardInfo.gender = Verify_IDCard.genders.female
            } else {
                idCardInfo.gender = Verify_IDCard.genders.male
            }
        } else if (idCardNo.length === 18) {
            let aday = idCardNo.substring(6, 14)
            idCardInfo.birthday = Verify_IDCard.formateDateCN(aday)
            if (parseInt(idCardNo.charAt(16)) % 2 === 0) {
                idCardInfo.gender = Verify_IDCard.genders.female
            } else {
                idCardInfo.gender = Verify_IDCard.genders.male
            }
        }
        return idCardInfo
    },

    /* 18位转15位 */
    getId15: function (idCardNo) {
        if (idCardNo.length === 15) {
            return idCardNo
        } else if (idCardNo.length === 18) {
            return idCardNo.substring(0, 6) + idCardNo.substring(8, 17)
        } else {
            return null
        }
    },

    /* 15位转18位 */
    getId18: function (idCardNo) {
        if (idCardNo.length === 15) {
            let id17 = idCardNo.substring(0, 6) + '19' + idCardNo.substring(6)
            let parityBit = Verify_IDCard.getParityBit(id17)
            return id17 + parityBit
        } else if (idCardNo.length === 18) {
            return idCardNo
        } else {
            return null
        }
    }
}

export default Verify_IDCard.checkIdCardNo