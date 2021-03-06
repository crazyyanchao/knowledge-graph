// 客户端传入需要可视化的数据，然后实时更新知识图谱可视化效果
// serverD:客户端请求的节点数据
// serverE:客户端请求的关系数据
// function update(serverD, serverE) {
//     // d3.select("#svgGraph").remove(); // 删除整个SVG
//     // d3.select("#svgGraph").select("#svgOne").selectAll("*").remove(); // 清空SVG中的内容
//     // d3.select("#svgGraph").select("#svgOne").reset();
// }

function update() {
    // d3.select("#svgGraph").remove(); // 删除整个SVG
    // d3.select("#svgGraph").select("#svgOne").selectAll("*").remove(); // 清空SVG中的内容
    // d3.select("#svgGraph").select("#svgOne").reset();

    d3.selectAll('#svgOne').remove(); //删除整个SVG
    d3.selectAll('#svgOne').selectAll('*').remove(); //清空SVG中的内容
}

function getItemData() {
    // 获取缓存ITEM
    var data = sessionStorage.getItem("itemData");
    if (data == null || data == "") {
        $("#A").val("load-all");
    } else {
        $("#A").val(data);
    }

    // 默认执行一次search
    // $('#search').click();
}

