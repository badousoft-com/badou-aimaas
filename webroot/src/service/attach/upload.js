/*
 * @FilePath: @/service/attach/upload.js
 * @Description: 附件上传
 */
import { Get_Fragment_Info, Upload_Fragment_Child } from '@/api/frame/attach'
import store from '@/store'
import SparkMD5 from 'spark-md5'

/**
 * @description: 分片上传文件列表
 * @param {Array} fileList 文件列表
 * @param {Object} params  除附件相关的额外参数
 */
function Upload_Fragment_Files (fileList, params) {
    // 没有需要分片上传的附件，无需执行
    if (!fileList.length) return
    fileList.forEach(async f => {
        let fileHash = await calculateHash(f.file)
        // 获取文件上传信息参数
        let fileParams = Object.assign({
            fileType: f.fieldKey,
            fileName: f.file.name,
            fileSize: f.file.size,
            fileHash,
            priority: f.index,
        }, params)
        let splitFileRes = await Get_Fragment_Info(fileParams)
        if (splitFileRes && splitFileRes.bean) {
            let {fragmentInfo = {}, detail = []} = splitFileRes?.bean || {}
            // 文件上传进度信息
            let progressInfo = { id: fragmentInfo.id, name: fileParams.fileName, all: detail.length }
            // 往上传中附件添加信息
            store.commit('settings/ADD_UPLOADING_LIST', progressInfo)
            handlerFragmentChild(f.file, detail, fragmentInfo)
        }
    })
}

// 计算文件的hash值
function calculateHash (file) {
    return new Promise((resolve, reject) => {
        const spark = new SparkMD5.ArrayBuffer()
        const reader = new FileReader()
        reader.readAsArrayBuffer(file)
        reader.onload = event => {
            const hash = spark.append(event.target.result).end()
            resolve(hash)
        }
        reader.onerror = error => {
            reject(error)
        }
    })
}

// 上传子切片
async function handlerFragmentChild (file, splitChildren, fragmentInfo) {
    // 分片上传每片大小（M）
    const fragmentFileSize = store.state.settings?.projectSetting?.fragmentFileSize || 10
    // const fragmentFileSize = 1
    // 分片单位转化（k）
    const CHUNK_SIZE = fragmentFileSize * 1024 * 1024
    const spark = new SparkMD5.ArrayBuffer()
    // 总切片数
    const chunksCount = Math.ceil(file.size / CHUNK_SIZE)
    for (let i = 0; i < chunksCount; i++) {
        // 开始切片位置
        const start = i * CHUNK_SIZE
        // 结束切片位置
        const end = Math.min(start + CHUNK_SIZE, file.size)
        const chunk = file.slice(start, end)
        const chunkReader = new FileReader()
        chunkReader.readAsArrayBuffer(chunk)
        // 获取上传结果
        let uploadRes = await uploadChildFragment(spark, chunkReader, { detailId: splitChildren[i].id, file: chunk })
        // 进度上传信息
        let statusInfo = {id: fragmentInfo.id, value: i + 1 }
        // 如果当前附件分片上传失败，添加失败参数
        if (!uploadRes.status) statusInfo.failParams = uploadRes.params
        // 更新文件上传进度信息
        store.commit('settings/UPDATE_UPLOADING_PROGRESS', statusInfo)
    }
}
// 上传切片子项
function uploadChildFragment (spark, chunkReader, params) {
    // 允许上传失败的次数，在此范围内，重复上传，超过标识为失败
    let allowFallNum = 3
    return new Promise(resolve => {
        chunkReader.onload = async event => {
            const chunkData = {
                ...params,
                fileHash: spark.append(event.target.result).end(),
            }
            for (let index = 0; index < allowFallNum; index++) {
                let uploadRes = await sendChildFragment(chunkData)
                // 上传成功，直接跳出循环
                if (uploadRes.status) {
                    resolve({ status: true })
                    break
                }
                // 最后一次上传 && 上传失败
                if (index === allowFallNum - 1) {
                    resolve({ status: false, params: chunkData })
                    break
                }
            }
        }
    })
}
// 发送附件分片上传请求
function sendChildFragment (params) {
    return new Promise(resolve => {
        Upload_Fragment_Child(params).then(res => {
            resolve({ status: res?.hasOk || false })
        }).catch(res => {
            resolve({ status: false })
        })
    })
}

export {
    // 分片上传文件列表
    Upload_Fragment_Files
}