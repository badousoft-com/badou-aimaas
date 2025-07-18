'use strict'
const path = require('path')
const defaultSettings = require('./src/settings.js')
const webpack = require('webpack')
// 引入js分析插件
let BundleAnalyzerPlugin = require('webpack-bundle-analyzer').BundleAnalyzerPlugin
const CompressionPlugin = require('compression-webpack-plugin') // 引入gzip压缩插件
const TerserPlugin = require('terser-webpack-plugin')
function resolve(dir) {
    return path.join(__dirname, dir)
}


const name = defaultSettings.title || 'vue Admin Template' // page title

// If your port is set to 80,
// use administrator privileges to execute the command line.
// For example, Mac: sudo npm run
// You can change the port by the following methods:
// port = 9528 npm run dev OR npm run dev --port = 9528
const port = process.env.port || process.env.npm_config_port || 8000 // dev port
// 环境区分主要为开发环境与其他环境（其他：生产，uat，测试等等）
const isNotDevelopMentEnv = process.env.NODE_ENV !== 'development'
// 除开本地开发不使用cdn，其余场景皆开启CDN;  // 1-使用cdn，0-不使用
const isUseCDN = isNotDevelopMentEnv && process.env.VUE_APP_OPEN_CDN != 0
// 非正式环境：包括开发环境与测试环境
const isNotRegularEnv = process.env.VUE_APP_ENV === 'development' ||
                        process.env.VUE_APP_ENV === 'test'

const isK8s = process.env.VUE_APP_ENV === 'k8s'

if (isK8s) {
    // 接口访问地址
    process.env.VUE_APP_BASE_API = process.env.platformUrl
    // 打包后输出的包名
    process.env.VUE_APP_PUBLICPATH = process.env.packageName
    // 是否开启模型缓存 0-否，1-是
    process.env.VUE_APP_OPEN_MODULE_STORAGE = process.env.enabledModuleCache
    // 是否开启CDN托管 0-否，1-是
    process.env.VUE_APP_OPEN_CDN = process.env.enabledCdn
    // 在线预览地址
    process.env.VUE_APP_PREVIEW_URL = process.env.previewUrl
}

const cdnData = {
    externals: {
        'vue': 'Vue',
        'vue-router': 'VueRouter',
        'element-ui': 'ELEMENT',
        'vuex': 'Vuex',
        'axios': 'axios',
        'vee-validate': 'VeeValidate',
        'echarts': 'echarts',
        // 'echarts-gl': 'echartsGl',
        // 'jQuery': "jquery",
        // 'jquery': 'window.$',
        // 'dagre-d3': 'dagreD3', // 流程设计图相关
        // 'd3': 'd3', // 流程设计图相关
        // 'crypto-js': 'CryptoJS', // 账号登录的信息加解密
        // 'jsencrypt': 'JSEncrypt', // 请求加密
        // 'sql-formatter': 'sqlFormatter', // 表单组件-codeMirror sql语句格式化
        // 'vuedraggable': 'vuedraggable', // 表单设计器拖拽组件/流程定义调序拖拽组件
    }
}
// 动态获取本地打包项目生成的包名
let envSetting = function () {
    let publicPath = process.env.VUE_APP_PUBLICPATH
    let packageName = process.env.VUE_APP_OUTPUTDIR
    publicPath = publicPath ? `/${publicPath.replace(/\//g, '')}` : '/'
    packageName = publicPath &&
                  publicPath.replace(/\//g,  '') ||
                  packageName ||
                  'ROOT'
    return {
        publicPath,
        packageName
    }
}()

// 定义获取代理背后发出的真正请求地址
let getProxyUrl = function (url) {
    // 获取代理背后的实际请求接口地址
    let _url = new URL(url || '', process.env.VUE_APP_BASE_API)
    let realUrl = _url && _url.href || ''
    // 定义正则，替换请求中的加密参数sign，方便接口数据输出时容易查阅些(sign数据太长)
    const removeSign = (data) => `${data}&`.replace(/((&|\?)?(sign=.*)?&+)/, '')
    try {
        // 获取去除sign加密参数后的url
        realUrl = removeSign(realUrl)
    } catch (e) {
        realUrl = null
        console.log(`代理：实际请求地址输出异常，请检查vue.config.js中代理模块相关逻辑`)
    }
    return realUrl
}
let timeStamp = new Date().getTime()
// All configuration item explanations can be find in https://cli.vuejs.org/config/
module.exports = {
  /**
   * You will need to set publicPath if you plan to deploy your site under a sub path,
   * for example GitHub Pages. If you plan to deploy your site to https://foo.github.io/bar/,
   * then publicPath should be set to "/bar/".
   * In most cases please use '/' !!!
   * Detail: https://cli.vuejs.org/config/#publicpath
   */
    publicPath: envSetting.publicPath,
    outputDir: envSetting.packageName,
    assetsDir: 'static',
    lintOnSave: !isNotDevelopMentEnv,
    // TODO 是否启动问题源码追踪
    // productionSourceMap: isNotRegularEnv,
    productionSourceMap: false,
    css: {
        loaderOptions: {
            sass: {
                data: `@import "@/styles/theme.scss";`
            }
        },
        extract: {
            // 打包后的css文件名添加时间戳【解决发布后的浏览器缓存】
            filename: `static/css/[name].${timeStamp}.css`,
            chunkFilename: `static/css/[name].${timeStamp}.css`
        }
    },
    // 兼容ie浏览器
    // entry: ['babel-polyfill', './app/js'],

    devServer: {
        port: port,
        host: '0.0.0.0',
        compress: true,
        overlay: {
            warnings: false,
            errors: true
        },
        proxy: {
            '/proxy': {
                target: process.env.VUE_APP_BASE_API,
                changeOrigin: true,
                ws: true,
                pathRewrite: {
                    '^/proxy': ''
                },
                // 请求
                onProxyReq: function (proxyReq, req, res) {
                    // 获取请求地址
                    let url = getProxyUrl(req.url)
                    // 输出到终端，方便查阅
                    url && console.log(`代理接口真正发出的请求地址为：${url}`)
                },
                // 响应
                onProxyRes: function (proxyRes, req, res) {
                    // 在响应头部添加属性proxy-real-url，存放真正的请求地址，可在请求network的Response中查阅
                    let url = getProxyUrl(req.url)
                    proxyRes.headers['proxy-real-url'] = url
                }
            }
        }
        // proxy: {
        //     '/proxy': {
        //         target: process.env.VUE_APP_BASE_API,
        //         ws: true,
        //         changeOrigin: true,
        //         pathRewrite: {
        //             '^/proxy': ''
        //         },
        //         cookiePathRewrite: {
        //             "/init_platform/": "/proxy/"
        //         },
        //         onProxyRes: function (proxyRes, req, res) {
        //             // 只能在终端输出，调试比较困难
        //         }
        //     },
        // }
    },
    configureWebpack: {
        // provide the app's title in webpack's name field, so that
        // it can be accessed in index.html to inject the correct title.
        name: name,
        resolve: {
            alias: {
                '@': resolve('src')
            }
        },
        plugins: [
            // 配置忽略md文件不解析
            new webpack.IgnorePlugin(/\.md/),
            // new webpack.ProvidePlugin({
            //     $:"jquery",
            //     jQuery:"jquery",
            //     "windows.jQuery":"jquery"
            // }),
        ],
        // 性能提醒
        performance: {
            // 提醒方式:暂时关闭文件大小
            hints: false,
            // hints: "warning",
            // 文件大小峰值控制（单位：kb）
            maxAssetSize: 10000
        },
        externals: isUseCDN ? cdnData.externals : {},
        // 打包后的js文件名添加时间戳（解决打包后部分浏览器需要刷新缓存方能生效的问题）
        output: {
            filename: `static/js/[name].${timeStamp}.js`, // index文件重命名为+时间戳的格式
            chunkFilename: `static/js/[name].${timeStamp}.js`
        }
    },
    chainWebpack (config) {
        config.plugins.delete('preload') // TODO: need test
        config.plugins.delete('prefetch') // TODO: need test
        // 配置启用打包文件分析
        if (isNotDevelopMentEnv) {
            // 开启图片压缩-使用异常，暂时注释
            // config.module.rule('images')
            //     .test(/\.(png|jpe?g|gif|svg)(\?.*)?$/)
            //     .use('image-webpack-loader')
            //     .loader('image-webpack-loader')
            //     .options({ bypassOnDebug: true })
            // js文件包分析
            if (process.env.npm_config_report) {
                config
                    .plugin('webpack-bundle-analyzer')
                    .use(BundleAnalyzerPlugin)
                    .end()
            }
            // 配置html文件引入变量
            if (isUseCDN) {
                config.plugin('html')
                    .tap(args => {
                        args[0].isUseCDN = isUseCDN
                        return args
                    })
            }
            // gzip压缩
            config.plugin('compression').use(CompressionPlugin, [
                {
                  algorithm: 'gzip',
                  test: new RegExp('\\.(js|css)$'),
                  threshold: 10240, //超过多少字节进行压缩
                  minRatio: 0.8 //至少压缩到原来体积的0.8,才会进行压缩
                }
            ])
            // 设置压缩混淆规则
            config.optimization.minimizer([new TerserPlugin({
                terserOptions: {
                    // 混淆规则
                    mangle: {
                        // 配置不被混淆处理的变量
                        reserved: ['resolve']
                    },
                    // 压缩规则
                    compress: {
                        drop_debugger: true, // 干掉debugger
                        pure_funcs: ['console.log'], // 移除console.log
                    },
                    output: {
                        comments: false, // 移除注释
                    },
                },
                extractComments: false, // 移除注释
            })])
        }
        // set svg-sprite-loader
        config.module
            .rule('svg')
            .exclude.add(resolve('src/icons'))
            .end()
        config.module
            .rule('icons')
            .test(/\.svg$/)
            .include.add(resolve('src/icons'))
            .end()
            .use('svg-sprite-loader')
                .loader('svg-sprite-loader')
                .options({
                    symbolId: 'bd-[name]'
                })
            .end()
        // 新增去除svg loader，去除svg默认fill
        config.module
            .rule('deleteSvgFill')
            .test(/\.svg$/)
            .include.add(resolve('src/icons'))
            .end()
            // 排除框架多色图标文件夹，保留fill属性
            .exclude.add(resolve('src/icons/svg/frame/mixed'))
            .end()
            // 排除业务多色图标文件夹，保留fill属性
            .exclude.add(resolve('src/icons/svg/business/mixed'))
            .end()
            .use('svgo-loader')
                .loader('svgo-loader')
                .tap(options => ({
                    ...options, plugins: [{removeAttrs: {attrs: 'fill'}}]
                }))
            .end()

        // set preserveWhitespace
        config.module
            .rule('vue')
            .use('vue-loader')
            .loader('vue-loader')
            .tap(options => {
                options.compilerOptions.preserveWhitespace = true
                return options
            })
            .end()

        config
            // https://webpack.js.org/configuration/devtool/#development
            .when(!isNotDevelopMentEnv,
                config => config.devtool('cheap-source-map')
            )

        config
            .when(isNotDevelopMentEnv,
                config => {
                    config
                        .plugin('ScriptExtHtmlWebpackPlugin')
                        .after('html')
                        .use('script-ext-html-webpack-plugin', [{
                            // `runtime` must same as runtimeChunk name. default is `runtime`
                            inline: /runtime\..*\.js$/
                        }])
                        .end()
                    config
                        .optimization.splitChunks({
                            chunks: 'all',
                            cacheGroups: {
                                libs: {
                                    name: 'chunk-libs',
                                    test: /[\\/]node_modules[\\/]/,
                                    priority: 10,
                                    chunks: 'initial' // only package third parties that are initially dependent
                                },
                                elementUI: {
                                    name: 'chunk-elementUI', // split elementUI into a single package
                                    priority: 20, // the weight needs to be larger than libs and app or it will be packaged into libs or app
                                    test: /[\\/]node_modules[\\/]_?element-ui(.*)/ // in order to adapt to cnpm
                                },
                                commons: {
                                    name: 'chunk-commons',
                                    test: resolve('src/components'), // can customize your rules
                                    minChunks: 3, //  minimum common number
                                    priority: 5,
                                    reuseExistingChunk: true
                                }
                            }
                        })
                    config.optimization.runtimeChunk('single')
                }
            )
    }
}