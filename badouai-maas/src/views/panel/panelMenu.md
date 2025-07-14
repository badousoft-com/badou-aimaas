# 面板全目录

## public

### panelJs：配置自定义js时的开始目录

- XXX

## src

### api

- frame

	- panel

		- designer：面板设计器相关请求api
		- user：用户面板相关请求api
		- view：面板预览相关请求api

### components/frame/Panel

- ViewBox.vue：一个容器，传入isError，当此值为true时，显示暂无数据图片

- PanelCode
- index.vue：面板页面展示块的中转站，传入可只传入panelId
	- PanelTitle.vue：面板标题（当前20220923仅大屏时使用，可切换全屏）
	- utils.js：面板数据处理方法
	- theme

	  - index.js：获取主题信息的一些方法

	  - default.js：默认主题js，默认加载
	  - bluePlane：主题文件夹（示例）

	    - index.js： 此主题下的一些主题变量
	    - assets：此主题下的资源文件夹
	      - angle：此主题下的四个角的图片文件夹
      - borderTheme：此主题下的边框信息文件夹
	      - .... 除了边角以及边框信息外的其余资源
- PanelBlock
  - index.vue：面板块的展示组件
  - BlockTitle.vue：块标题
  - utils.js：块数据的处理方法
- BlockContent
  - ContentItem.vue：渲染内容前的过渡组件
  - ContentTitle.vue：内容标题
  - MultiContent.vue：多内容渲染组件
  - utils.js：内容处理方法
  - items：内容组件文件夹
    - index.js：items组件导出文件js
    - mixins
      - ReportCommonAttrs.vue：一些公用的vue文件信息
    - PCard
      - index.vue：卡片组件
      - items
        - DefaultCard.vue：每个卡片的壳组件，默认使用这个
    - PChart
      - opts：echart 的默认数据获取js文件夹
        - ..... 图表js，文件名为配置面板时的reportType报表类型
      - index.vue：图表组件
    - PMap
      - opts：echart 的默认数据获取js文件夹
        - ..... 地图js，文件名为配置面板时的reportType报表类型
      - index.vue：地图组件
    - PList.vue：列表组件
    - PMultiGraph.vue：多图
    - PPage.vue：页面
    - PQuickMenu.vue：快捷菜单
    - PText.vue：文本类型
    - PVideo.vue：视频类型
- ViewBox.vue
- NewPanel：面板拖拽组件 ---- 后续可能还会调整目录结构

### views/panel

- panelDesigner  ------- 设计器暂时只展示这个目录，后续结构会稍微调整，后面再补上

	- components

		- 设计器块展示页面：BlockDesignView.vue
		- 拖拽组件：LayoutDrag.vue
		- 面板设置组件：PanelSetting.vue
		- blockSetting

			- 块设置组件：index.vue
			- components

				- 基础设置：Base.vue
				- 内容设置：content.vue
				- 事件设置：Even.vue
				- 样式设置：Style.vue
				- 分享设置：Share.vue
				- 用于设置表单更改的js：js

					- base.js
					- content.js
					- style.js
- 设计器页面：index.vue
- View.vue：面板预览页

*XMind - Trial Version*