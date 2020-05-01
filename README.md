# 使用d3.js可视化知识图谱
=============================

## data目录
- bg.jpg可视化背景图片数据
- CircularPartition.json节点圆形分区图工具栏需要加载的数据
- test.json可视化需要展示的数据格式

## images
- 此目录存储节点属性图片数据

## js
- d3.js version-3.2.8

## src
- JS以及其它HTML等源码

## index.html
- 知识图谱可视化入口文件

## 可视化数据接口
- 访问地址：http://localhost:7476/knowledge-graph/hello/search

## 启动部署文件
- ./knowledge-graph-web

## CSV文件导入接口
data.lab.knowledgegraph.service.DataServiceImplTest
默认标签‘Person’，可自定义
```json
{
    "label": "Person"
}
```
## 清除图库数据
```
MATCH (n) 
OPTIONAL MATCH (n)-[r]->() DELETE n,r
```
![图](images/graph-1.png)
![图](images/graph-2.png)
![图](images/import-csv.png)

