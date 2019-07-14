// 注释掉的部分只做参考目前已经弃用这种方式（与Neo4j可以采用连接池交互，这种方式我们目前在用效果还可以）
// 通过节点ID从Neo4j图数据库请求数据
// function getByNodeId(id) {
//     d3.json("http://localhost:7476/knowledge-graph/hello/dataSource/node/id/" + id + "", function (error, json) { // 服务器加载知识图谱数据
//         if (error) {
//             return console.warn(error);
//         }
//         console.log(id);
//         console.log(json);
//         return json;
//     });
// }

// 通过节点ID从Ne4j图数据库请求数据
// 中文字符参数先在客户端encode然后再在服务端decode即可
// function getByNodeName(name) {
//     var url = "http://localhost:7476/knowledge-graph/hello/dataSource/node/name/" + name + "";
//     var eUrl = encodeURI(url);
//     d3.json(eUrl, function (error, json) { // 服务器加载知识图谱数据
//         if (error) {
//             return console.warn(error);
//         }
//         console.log(name);
//         console.log(json);
//         return json;
//     });
// }

// 请求当前节点扩展需要的NODE和EDGE
// function getRelationShipByNodeId(id) {
//     var url = "http://localhost:7476/knowledge-graph/hello/dataSource/node/extend/" + id + "";
//     d3.json(url, function (error, json) { // 服务器加载知识图谱数据
//         if (error) {
//             return console.warn(error);
//         }
//         console.log(id);
//         console.log(json);
//         console.log(json.nodes);
//         console.log(json.links);
//         return json;
//     });
// }

// 初始化图数据库配置信息
// function startNeo4j() {
//     var url = "http://localhost:7476/knowledge-graph/hello/neo4j";
//     d3.json(url, function (error, json) { // Neo4j服务器配置状态信息
//         if (error) {
//             return console.warn(error);
//         }
//         return json; // 连接配置的状态信息
//     });
// }

// 获取用户输入的数据
function getInput() {
    var input = document.getElementById("inputText").value;
    console.log("Input:");
    console.log(input);
    var maxIndex = Math.max.apply(null, nodesIndexValue);
    loadById(input, maxIndex, nodesIndexId); // 传入当前ID，当前ID的INDEX值，当前画布所有已加载的ID列表
}

// 判断值是否是整数
function isInteger(obj) {
    return obj % 1 == 0;
}

// 记录NODES的index值
var nodesIndexValue = [], mapNodes = new Map(), nodesIndexId = [];

function recordNodesIndex(nodesArr) {
    var maxIndex = Math.max.apply(null, nodesIndexValue);
    if (!isInteger(maxIndex)) {
        maxIndex = 0;
    }
    console.log("MaxIndex:" + maxIndex);
    if ((maxIndex - 1) < 0) {
        maxIndex = 0;
    }
    var temp;
    for (var i = 0; i < nodesArr.length; i++) {
        if (maxIndex == 0) {
            temp = maxIndex + i;
        } else {
            temp = maxIndex + i + 1;
        }
        nodesIndexValue.push(temp);
        nodesIndexId.push(nodesArr[i].id);
        mapNodes.set(nodesArr[i].id, temp);
    }
}