module.exports = {
    root: true,
    parserOptions: {
        parser: 'babel-eslint',
        sourceType: 'module'
    },
    env: {
        browser: true,
        node: true,
        es6: true,
    },
    //   extends: ['plugin:vue/recommended', 'eslint:recommended'],
    extends: ["plugin:vue/essential"],
    // add your custom rules here
    //it is base on https://github.com/vuejs/eslint-config-vue
    rules: {
        'vue/no-parsing-error': false,  // 关闭vue页面对html的检测

        // allow paren-less arrow functions
        'arrow-parens': 0,
        // allow async-await
        'generator-star-spacing': 0,
        // allow debugger during development
        'no-debugger': process.env.NODE_ENV === 'production' ? 2 : 0,
        // 使用缩进必须为4个空格space，禁止使用tab, { "SwitchCase": 1 }要求switch下的case必须缩进
        "indent": ["error", 4, {"SwitchCase": 1}],
        // 当对象，数组已换行模式展示时，允许末尾添加逗号
        /*例如 sex: 1后面可以添加逗号, 如果 {name: 'wang', sex: 1,}这样展示就会报错
        {
            name: 'wang',
            sex: 1,
        }*/
        "comma-dangle": ["error", "only-multiline"],
        // 禁止变量，函数和函数参数定义了但是未被使用， 需要过滤main.js(在.eslintignore中设置)
        'no-unused-vars': [2, {
            'vars': 'all',
            'args': 'none',
            // 忽略掉解构对象中的未使用变量，有时通过解构希望去除某些属性，此时去除的属性是未被使用的
            "ignoreRestSiblings": true,
        }],
        /*函数名称或function关键字与开始表达之间设置空格
            1.function foo () {
            2.var bar = function () {
            3.var bar = function foo () {
            4.class Foo {
            5.bar () {
            5.var foo = async () => 1*/
        "space-before-function-paren": ["error", "always"],
        // 禁止逻辑语句句尾使用分号结尾
        "semi": [2, 'never'],
        // 操作符左右必须有空格
        "space-infix-ops": ["error", { "int32Hint": false }],
        // 要求使用 === 和 !==， 不使用==和！=
        "eqeqeq": [2, 'allow-null'],
        // 禁止出现多行空行（此处设置最多出现1个空行）
        "no-multiple-empty-lines": [2, {
            "max": 1,
            "maxEOF": 1,
            "maxBOF": 1,
        }],
        // 【words】一元关键符使用空格隔开：如new，delete，typeof，void，yield
        // 【nonwords】一元运算符使用空格隔开：如-，+，--，++，!，!!
        // 【overrides】特殊规则
        "space-unary-ops": [2, {
            "words": true,
            "nonwords": false,
            "overrides": {
            }
        }],
        // 必须在声明变量时初始化变量
        "init-declarations": ["error", "always"],
        // 阻止使用var或鼓励使用const或let替代使用,前提环境支持es6
        "no-var": "error",
        // 注释的使用结合空格     /* 123 */      // 123
        "spaced-comment": ["error", "always", {
            "line": {
                "markers": ["/"],
                "exceptions": ["-", "+"]
            },
            "block": {
                "markers": ["!"],
                "exceptions": ["*"],
                "balanced": true
            }
        }],
        // 要求所有switch语句都有一个default大小写，即使默认情况为空
        "default-case": "error",
        // switch中的case值不能重复
        "no-duplicate-case": 2,
        // 禁止出现return/break/continue/throw后不可到达的代码,逻辑错误或代码冗余
        "no-unreachable": 2,
        // 禁止switch穿透，没有使用break
        "no-fallthrough": 2,
        // 强制逗号旁边的间距,逗号后面添加空格
        "comma-spacing": ["error", { "before": false, "after": true }],
        // 在条件语句中禁止赋值运算符, 一般是写错了
        "no-cond-assign": "error",
        // 禁止修改使用const关键字声明的变量
        "no-const-assign": "error",
        // 不允许空块语句
        "no-empty": "error",
        // 关键词后面必须添加空格
        "keyword-spacing": ["error", { "before": true }],
        // 禁止括号内最左侧和最右侧的空格
        "space-in-parens": ["error", "never"],
        // 1. 不能省略大括号，可能导致错误并降低代码清晰度
        // 2. 放宽规则，允许在单行中省略大括号
        "curly": ["error", "multi-line"],
        // 禁止使用未声明的变量
        "no-undef": "error",
        // 块前面需要设置空格 例如{}前面
        "space-before-blocks": "error",
        // 禁止在行尾添加尾随空格（无尾随空格）
        "no-trailing-spaces": "error",
        // 禁止在块中首尾填充空行，不考虑这个美观
        "padded-blocks": ["error", "never"],
        // 要求逻辑模块尽可能优先使用单引号,允许使用反引号，出现多重引号允许嵌套
        "quotes": ["error", "single", {
            "allowTemplateLiterals": true,
            "avoidEscape": true
        }],
        // 关闭了驼峰校验，后续再深究
        "camelcase": 0,
        // 关闭作为异常抛出的内容的限值，支持对象抛出，也可以字符串作为抛出内容
        "no-throw-literal": 0,
        // 不允许object中出现重复的键值key
        "no-dupe-keys": "error",
        // 大括号的风格统一，其中一个块的左大括号放在同一行及其相应的说明或声明中的一个
        "brace-style": "error"
    }
}
