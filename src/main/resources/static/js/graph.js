// 定义画布 （radius是鼠标点击生成圆形分区图的半径）
// var width = 1345, height = 750, color = d3.scale.category20();
var width = 1920, height = 800, color = d3.scale.category20();

var svg = d3.select("body")
    .append("svg")
    .attr("id", "svgGraph")
    .attr("width", width)
    .attr("height", height)
    .append("g")
    .attr("id", "svgOne")
    .call(d3.behavior.zoom() // 自动创建事件侦听器
        .scaleExtent([0.1, 10]) // 缩放允许的级数
        .on("zoom", zoom)
    )
    .on("dblclick.zoom", null); // remove双击缩放

// 实时获取SVG画布坐标
function printPosition() {
    var position = d3.mouse(svg.node());
    return position;
}

// 缩放函数
function zoom() {
    // translate变换矢量（使用二元组标识）scale当前尺度的数字
    svg.attr("transform", "translate(" + d3.event.translate + ")scale(" + d3.event.scale + ")"); // 画布缩放与移动
    // svg.attr("transform", "scale(" + d3.event.scale + ")"); // 画布缩放
}

// 设置连线箭头属性
function setMarkers() {
    svg.append("g")
        .attr("id", "lineAndText")
        .selectAll("marker")
        .data(edges)
        .enter()
        .append("marker")
        .attr("id", function (d) {
            return d.index;
        })
        .attr("viewBox", "0 -5 10 10") // 坐标系的区域
        .attr("class", "arrow")
        .attr("refX", 27) // refX,refY在viewBox内的基准点，绘制时此点在直线端点上（要注意大小写）
        .attr("refY", 0)
        .attr("markerWidth", 10) // 标识的大小
        .attr("markerHeight", 18) // 标识的大小
        .attr("markerUnits", "userSpaceOnUse") // 标识大小的基准，有两个值：strokeWidth（线的宽度）和userSpaceOnUse（图形最前端的大小）
        .attr("orient", "auto") // 绘制方向，可设定为：auto（自动确认方向）和 角度值
        .append("path")
        .attr("d", "M0,-5L10,0L0,5")
        .attr("fill", "#ccc");
}

// 添加连线
function add_edges() {
    setMarkers(); // 设置连线箭头属性
    var svg_edges = svg.select("#lineAndText")
        .selectAll("line")
        .data(edges)
        .enter()
        .append("line")
        .attr("id", function (d) {
            return d.index;
        })
        .style("stroke", "#ccc")
        .style("stroke_width", 1)
        .attr("marker-end", function (d) {
            return "url(#" + d.index + ")";
        })
        .attr("stroke", "#999")
        .on("mouseover", function (d) { // 鼠标选中时触发
            mouseSelectLine(d);
            addToolTip(d); //添加提示框的div
        })
        .on("mouseout", function () {
            d3.select("#relation").remove();
            d3.select("#tooltip").remove();
        });
    return svg_edges;
}

// 求直线与圆的交点
// 函数参数说明:cx:圆X轴坐标 cy:圆y轴坐标  r:圆半径 stx:起点直线的X轴坐标 sty:起点直线的轴坐标 edx:终点直线的X轴坐标 edy:终点直线的Y轴坐标
// 返回值:交点坐标(x,y)
function getPoint(cx, cy, r, stx, sty, edx, edy) {
    // 求直线
    var k = (edy - sty) / (edx - stx);
    var b = edy - k * edx;
    //列方程
    var x1, y1, x2, y2;
    var c = cx * cx + (b - cy) * (b - cy) - r * r;
    var a = (1 + k * k);
    var b1 = (2 * cx - 2 * k * (b - cy));

    var tmp = Math.sqrt(b1 * b1 - 4 * a * c);
    x1 = (b1 + tmp) / (2 * a);
    y1 = k * x1 + b;
    x2 = (b1 - tmp) / (2 * a);
    y2 = k * x2 + b;

    // 过滤距离最近的坐标
    var p = {};

    function lineIf(lx, ly, lxx, lyy) {
        var d = Math.sqrt((lx - lxx) * (lx - lxx) + (ly - lyy) * (ly - lyy));
        return d;
    }

    if (cx != stx) { // stx, sty
        var d1 = lineIf(x1, y1, stx, sty);
        var d2 = lineIf(x2, y2, stx, sty);
        if (d1 < d2) {
            p.x = x1;
            p.y = y1;
        } else {
            p.x = x2;
            p.y = y2;
        }
    } else { // edx, edy
        var d1 = lineIf(x1, y1, edx, edy);
        var d2 = lineIf(x2, y2, edx, edy);
        if (d1 < d2) {
            p.x = x1;
            p.y = y1;
        } else {
            p.x = x2;
            p.y = y2;
        }
    }
    return p;
}

// 鼠标选中关系添加显示效果
function mouseSelectLine(d) {
    var p1 = getPoint(d.source.x, d.source.y, 20, d.source.x, d.source.y, d.target.x, d.target.y);
    var p2 = getPoint(d.target.x, d.target.y, 20, d.source.x, d.source.y, d.target.x, d.target.y);
    var json = [p1, p2];
    //构造默认线性生成器
    var line = d3.svg.line()
        .x(function (d) { //指定x存取器为：取每个数据元素的x属性的值
            return d.x;
        })
        .y(function (d) { //指定y存取器为：取每个数据元素的y属性的值
            return d.y;
        });
    svg.append('path')
        .attr({
            "d": function () { //生成路径数据
                return line(json);
            },
            "id": "relation"
        })
        .style({
            "stroke": "#87CEFA",  //path颜色
            "stroke-width": 6 //path粗细
        });
}

// 添加节点
function add_nodes() {
    var svg_nodes = svg.append("g")
        .attr("id", "circleAndText")
        .selectAll("circle")
        .data(nodes)
        .enter()
        .append("g")
        .call(force.drag().on("dragstart", function (d) {
                d3.select("#eee").remove(); // 删除节点扇形
                d3.select("#sel").remove(); // 删除节点选中
                d3.event.sourceEvent.stopPropagation(); // 画布拖动与节点拖动分离
                d3.select(this).attr("r", 20 * 2);
            })
                .on("dragend", function (d) {
                    d3.select("#eee").remove(); // 删除节点扇形
                    d3.select("#sel").remove(); // 删除节点选中
                    d.fixed = true; // 拖动结束后节点固定
                    d3.select(this).attr("r", 20);
                })
        )
        .on("click", function (d) { // 鼠标点击时触发
            // 在当前节点处画三页扇形
            d3.select("#eee").remove();
            drawCirclePartition(d);
        })
        .on("mouseover", function (d) { // 光标放在某元素上s
            mouseSelect(d); // 鼠标选中效果
            addToolTip(d); //添加提示框的div
        })
        .on("mouseout", function (d) {
            d3.select("#sel").remove(); // 删除节点选中
            d3.select("#tooltip").remove();
            d3.select("#tooltipCir").remove();
        });
    svg_nodes.append("circle")
        .attr("id", function (d) {
            return d.index;
        })
        .attr("r", 20)
        .attr("fill", function (d, i) {
            return color(i);
        });
    svg_nodes.append("image")
        .attr("class", "circle")
        .attr("xlink:href", function (d) {
            var img = d.image;
            if (img != undefined) {
                return "http://localhost:7476/knowledge-graph/path/images/" + d.image
            } else {
                return null;
            }
        })
        .attr("x", "-20px")
        .attr("y", "-20px")
        .attr("width", "40px")
        .attr("height", "40px");
    svg_nodes.append("svg:text")
        .style("fill", "#ccc")
        .attr("dx", 20)
        .attr("dy", 8)
        .text(function (d) {
            return d.name
        });
    return svg_nodes;
}

//添加提示框的div
function addToolTip(d) {
    var htmlStr;
    if (d.source && d.target && d.type) {
        htmlStr = "name:" + d.type + "<br/>";
    } else {
        htmlStr = "id:" + d.id + "<br/>" + "name:" + d.name + "<br/>";
    }
    var position = printPosition(d);
    var tooltip = d3.select("body").append("div")
        .attr("class", "tooltip") //用于css设置类样式
        .attr("opacity", 0.0)
        .attr("id", "tooltip");
    htmlStr = htmlStr + "locx:" + position[0] + "<br/>" + "locy:" + position[1] + "<br/>";
    if (d.image != undefined) {
        htmlStr = htmlStr + "<img src=\"http://localhost:7476/knowledge-graph/path/images/" + d.image + "\" height=\"100\" width=\"100\" />";
    }
    tooltip.html(htmlStr)
        .style("left", (d3.event.pageX) + "px")
        .style("top", (d3.event.pageY + 20) + "px")
        .style("opacity", 0.75);
}

function addToolTipCir(d) {
    var htmlStr;
    if (d.name == "☿") {
        htmlStr = "notes:解锁当前节点<br/>";
    }
    if (d.name == "✂") {
        htmlStr = "notes:裁剪当前节点与关系<br/>";
    }
    if (d.name == "✠") {
        htmlStr = "notes:拓展当前节点与关系<br/>";
    }
    if (d.name == "◎") {
        htmlStr = "notes:释放所有锁定的节点<br/>";
    }
    if (d.name == "오") {
        htmlStr = "notes:锁定所有节点<br/>";
    }
    var tooltip = d3.select("body").append("div")
        .attr("class", "tooltip") //用于css设置类样式
        .attr("opacity", 0.0)
        .attr("id", "tooltipCir");
    tooltip.html(htmlStr)
        .style("left", (d3.event.pageX) + "px")
        .style("top", (d3.event.pageY + 20) + "px")
        .style("opacity", 0.75);
}

// 生成圆弧需要的角度数据
var arcDataTemp = [{startAngle: 0, endAngle: 2 * Math.PI}];
var arc_temp = d3.svg.arc().outerRadius(26).innerRadius(20);

// 鼠标选中节点添加显示效果
var svg_selectNode;

function mouseSelect(d) {
    svg_selectNode = svg.append("g")
        .attr("id", "sel")
        .attr("transform", "translate(" + d.x + "," + d.y + ")")
        .selectAll("path.arc")
        .data(arcDataTemp)
        .enter()
        .append("path")
        .attr("fill", "#87CEFA")
        .attr("d", function (d, i) {
            return arc_temp(d, i);
        });
}

// 全局停止力作用之间的影响
function stopForce() {
    for (var i = 0; i < nodes.length; i++) {
        var obj = nodes[i];
        obj.fixed = true;
    }
}

// 全局开始力作用之间的影响
function startForce() {
    for (var i = 0; i < nodes.length; i++) {
        var obj = nodes[i];
        obj.fixed = false;
    }
    force.resume();
}

var re_line, re_circle, re_cir_text, re_line_text; // 扩展节点同步更新
// 节点添加圆形分区（添加三页扇形）
function drawCirclePartition(d) {
    // 圆形分区布局（数据转换）
    var radius = 40;
    var partition = d3.layout.partition()
        .sort(null)
        .size([2 * Math.PI, radius * radius]) // 第一个值域时2 PI，第二个值时圆半径的平方
        .value(function (d) {
            return 1;
        });

    // 绘制圆形分区图
    // 如果以圆形的形式来转换数据那么d.x和d.y分别代表圆弧的绕圆心
    // 方向的起始位置和由圆心向外的起始位置d.dx和d.dy分别代表各自的宽度
    var arc = d3.svg.arc()
        .startAngle(function (d) {
            return d.x;
        })
        .endAngle(function (d) {
            return d.x + d.dx;
        })
        .innerRadius(function (d) {
            return 26;
        })
        .outerRadius(function (d) {
            return 80;
        });
    var circlePart = partition.nodes(dataCirclePartition);

    // "☿" 释放固定的节点
    function releaseNode() {
        d.fixed = false;
        // force.start(); // 开启或恢复结点间的位置影响
        force.resume();
    }

    // "✂" 删除当前节点以及当前节点到其它节点之间的关系
    function removeNode() {
        var newNodes = [];
        for (var i = 0; i < nodes.length; i++) {
            var obj = nodes[i];
            if (obj.id != d.id) {
                newNodes.push(obj);
            }
        }
        var newedges = [];
        for (var i = 0; i < edges.length; i++) {
            var obj = edges[i];
            if ((d.index != obj.source.index) && (d.index != obj.target.index)) {
                newedges.push(obj);
            }
        }
        nodes = newNodes;
        edges = newedges;

        var nIndex = function (d) {
            return d.index;
        };
        var lIndex = function (d) {
            return d.index;
        };
        // 通过添加'g'元素分组删除
        svg.select("#circleAndText").selectAll("circle")
            .data(nodes, nIndex)
            .exit()
            .remove();
        svg.select("#circleAndText").selectAll("image")
            .data(nodes, nIndex)
            .exit()
            .remove();
        svg.select("#circleAndText").selectAll("text")
            .data(nodes, nIndex)
            .exit()
            .remove();
        svg.select("#lineAndText").selectAll("line")
            .data(edges, lIndex)
            .exit()
            .remove();
        svg.select("#lineAndText").selectAll("text")
            .data(edges, lIndex)
            .exit()
            .remove();
    }

    // 判断元素是否在ARRAY中
    function isInArray(arr, value) {
        for (var i = 0; i < arr.length; i++) {
            if (value === arr[i]) {
                return true;
            }
        }
        return false;
    }

    //  扩展当前节点，距离为1
    function extendNode() {
        var index = d.index;
        var arrEdges = [], arrIndex = [], arrNodes = [];
        for (var i = 0; i < rawEdges.length; i++) {
            if ((index == rawEdges[i].source.index) || (index == rawEdges[i].target.index)) {
                arrEdges.push(rawEdges[i]);
                if (index != rawEdges[i].source.index) {
                    arrIndex.push(rawEdges[i].source.index);
                } else if (index != rawEdges[i].target.index) {
                    arrIndex.push(rawEdges[i].target.index);
                }
            }
        }
        for (var i = 0; i < rawNodes.length; i++) {
            for (var j = 0; j < arrIndex.length; j++) {
                var obj = arrIndex[j];
                if (rawNodes[i].index == obj) {
                    arrNodes.push(rawNodes[i]);
                }
            }
        }
        // nodes.push(arrNodes);
        // edges.push(arrEdges);
        var nodesRemoveIndex = [];
        for (var i = 0; i < arrNodes.length; i++) {
            var obj = arrNodes[i];
            for (var j = 0; j < nodes.length; j++) {
                var obj2 = nodes[j];
                if (obj.index == obj2.index) {
                    nodesRemoveIndex.push(i);
                }
            }
        }
        var edgesRemoveIndex = [];
        for (var i = 0; i < arrEdges.length; i++) {
            var obj = arrEdges[i];
            for (var j = 0; j < edges.length; j++) {
                var obj2 = edges[j];
                if (obj.index == obj2.index) {
                    edgesRemoveIndex.push(i);
                }
            }
        }
        var coverNodes = [];
        for (var i = 0; i < arrNodes.length; i++) {
            var obj = arrNodes[i];
            if (!isInArray(nodesRemoveIndex, i)) {
                nodes.push(obj);
                coverNodes.push(obj);
            }
        }
        var coverEdges = [];
        for (var i = 0; i < arrEdges.length; i++) {
            var obj = arrEdges[i];
            if (!isInArray(edgesRemoveIndex, i)) {
                edges.push(obj);
                coverEdges.push(obj);
            }
        }
        // console.log("找出需要扩展的数据");
        // console.log(arrEdges);
        // console.log(arrNodes);
        // console.log("添加到原始数据集");
        // console.log(nodes);
        // console.log(edges);

        // d3.select("#svgGraph").remove(); // 删除整个SVG
        d3.select("#svgGraph").select("#svgOne").selectAll("*").remove(); // 清空SVG中的内容
        buildGraph();
    }

    var arcs = svg.append("g")
        .attr("id", "eee")
        .attr("transform", "translate(" + d.x + "," + d.y + ")")
        .selectAll("g")
        .data(circlePart)
        .enter()
        .append("g")
        .on("click", function (d) { // 圆形分区绑定Click事件
            if (d.name == "☿") {
                releaseNode();
            }
            if (d.name == "✂") {
                removeNode();
            }
            if (d.name == "✠") {
                extendNode();
            }
            if (d.name == "◎") {
                startForce();
            }
            if (d.name == "오") {
                stopForce();
            }
            d3.select("#eee").remove();
            d3.select("#tooltipCir").remove();
        });
    arcs.append("path")
        .attr("display", function (d) {
            return d.depth ? null : "none"; // hide inner ring
        })
        .attr("d", arc)
        .style("stroke", "#fff")
        .style("fill", "#A9A9A9")
        .on("mouseover", function (d) {
            d3.select(this).style("fill", "#747680");
            addToolTipCir(d); //添加提示框的div
        })
        .on("mouseout", function () {
            d3.select("#tooltipCir").remove();
            d3.select(this).transition()
                .duration(200)
                .style("fill", "#ccc")
            var array = printPosition();
            var distance = Math.sqrt(Math.pow((d.x - array[0]), 2) + Math.pow((d.y - array[1]), 2));
            if (distance > 80) {
                d3.select("#eee").remove(); // 删除节点扇形
            }
        });
    arcs.append("text")
        .style("font-size", "16px")
        .style("font-family", "simsun")
        .style("fill", "white")
        .attr("text-anchor", "middle")
        .attr("transform", function (d, i) {
            // 平移和旋转
            var r = 0;
            if ((d.x + d.dx / 2) / Math.PI * 180 < 180) // 0-180度以内的
                r = 180 * ((d.x + d.dx / 2 - Math.PI / 2) / Math.PI);
            else // 180-360度
                r = 180 * ((d.x + d.dx / 2 + Math.PI / 2) / Math.PI);
            return "translate(" + arc.centroid(d) + ")" + "rotate(" + r + ")";
        })
        .text(function (d) {
            return d.name;
        });
    return arcs;
}

// 添加描述关系文字
function add_text_edges() {
    var svg_text_edges = svg.select("#lineAndText")
        .selectAll("line.text")
        .data(edges)
        .enter()
        .append("text")
        .attr("id", function (d) {
            return d.index;
        })
        .style("fill", "#ccc")
        .attr("x", function (d) {
            return (d.source.x + d.target.x) / 2
        })
        .attr("y", function (d) {
            return (d.source.y + d.target.y) / 2
        })
        .text(function (d) {
            return d.type;
        })
        .on("mouseover", function (d) { // 鼠标选中时触发
            mouseSelectLine(d);
            addToolTip(d); //添加提示框的div
        })
        .on("mouseout", function () {
            d3.select("#relation").remove();
            d3.select("#tooltip").remove();
        })
        .on("click", function () {

        });
    return svg_text_edges;
}

// 对于每一个时间间隔进行更新
function refresh() {
    force.on("tick", function () { // 对于每一个时间间隔
        // 更新连线坐标·
        svg_edges.attr("x1", function (d) {
            return d.source.x;
        })
            .attr("y1", function (d) {
                return d.source.y;
            })
            .attr("x2", function (d) {
                return d.target.x;
            })
            .attr("y2", function (d) {
                return d.target.y;
            });
        // 更新节点以及文字坐标
        svg_nodes.attr("transform", function (d) {
            return "translate(" + d.x + "," + d.y + ")";
        });
        // 更新关系文字坐标
        svg_text_edges.attr("x", function (d) {
            return (d.source.x + d.target.x) / 2
        })
            .attr("y", function (d) {
                return (d.source.y + d.target.y) / 2
            });
    });
}


var force, nodes = [], edges = [], rawNodes, rawEdges; // 构建知识图谱需要操作的数据 (rawNodes, rawEdges将加载的原始构图数据缓存一份)

// 知识图谱可视化构建
function graph(data) {
    // 定义力布局（数据转换）
    nodes = nodes.concat(data.nodes); // 多数组连接
    edges = edges.concat(data.links);
    rawNodes = nodes;
    rawEdges = edges;
    for (var i = 0; i < edges.length; i++) { // 关系数据添加INDEX值（为了方便对应图形元素）
        var obj = edges[i];
        obj.index = i;
    }
    force = d3.layout.force()
        .nodes(nodes) // 指定节点数组
        .links(edges) // 指定连线数组
        .size([width, height]) // 指定范围
        .linkDistance(150) // 指定连线长度
        // .gravity(0.02) // 设置引力避免跃出布局
        .friction(0.9) // 设置摩擦力速度衰减
        .charge(-400); // 相互之间的作用力
    force.start(); // 开始作用
    buildGraph();
}

var svg_edges, svg_nodes, svg_text, svg_text_edges; // 需要动态更新的函数(dynamic update function)
// Strat build Knowledge Graph/Vault

function buildGraph() {
    console.log("开始构建可视化知识图谱：");
    console.log(nodes);
    console.log(edges);
    svg_edges = add_edges(); // 添加连线与箭头
    svg_nodes = add_nodes(); // 添加节点与文字
    svg_text_edges = add_text_edges(); // 添加描述关系的文字
    refresh();  // 对于每一个时间间隔进行更新
    force.resume(); // 必须添加否则图形元素更新不及时
}

// 服务器加载数据
var dataCirclePartition;

function load() {
    d3.json("http://localhost:7476/knowledge-graph/hello/dataSource/type/1", function (error, root) { // 服务器加载节点圆形分区数据
        if (error) {
            return console.warn(error);
        }
        dataCirclePartition = root;
    });
    d3.json("http://localhost:7476/knowledge-graph/hello/dataSource/type/2", function (error, json) { // 服务器加载知识图谱数据
        if (error) {
            return console.warn(error);
        }
        console.log("初始加载：");
        console.log(json.nodes);
        console.log(json.links);
        graph(json);
    });
    // d3.json("http://localhost:7476/knowledge-graph/hello/dataSource/node/extend/99717", function (error, json) { // 服务器加载知识图谱数据
    //     if (error) {
    //         return console.warn(error);
    //     }
    //     console.log("初始加载：");
    //     console.log(json);
    //     graph(json);
    // });
}

// 初始化图数据库配置信息
// startNeo4j();

function loadSearchGraph(json) {
    d3.json("http://localhost:7476/knowledge-graph/hello/dataSource/type/1", function (error, root) { // 服务器加载节点圆形分区数据
        if (error) {
            return console.warn(error);
        }
        dataCirclePartition = root;
    });

    graph(json);

}

// 执行知识图谱数据可视化
// load();
