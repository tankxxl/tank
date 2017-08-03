/**
 [{
    id: 6,
    any: 'opps'
}, {
    id: 2,
    parent: 5,
    any: 'foo',
}, {
    id: 1,
    parent: 2,
    any: 'bar'
}, {
    id: 5,
    any: 'hello'
}, {
    id: 3,
    parent: 2,
    any: 'other'
}]

 // to

 [{
    id: 6,
    any: 'opps',
    children: []
}, {
    id: 5,
    any: 'hello',
    children: [{
        id: 2,
        parent: 5,
        any: 'foo',
        children: [{
            id: 1,
            parent: 2,
            any: 'bar',
            children: []
        }, {
            id: 3,
            parent: 2,
            any: 'other',
            children: []
        }]
    }]
}]

 * @param data
 * @param options
 * @returns {Array}
 */

function listToTree(data, options) {
    // options为可选参数，防止options为空
    options = options || {};
    var ID_KEY = options.idKey || 'id';
    var PARENT_KEY = options.parentKey || 'parent';
    var CHILDREN_KEY = options.childrenKey || 'children';

    var tree = [],
        childrenOf = {}; // 保存所有节点的children列表，所以childrenOf是个对象，key为节点，value为children列表。
    // 在其它语言实现的话，childrenOf相当于Map，Map<String, List<TreeNode>>，这是一个以空间换时间的方案

    var item, id, parentId;

    for (var i = 0, length = data.length; i < length; i++) {
        // { id: 2, parent: 5, any: 'foo'}
        item = data[i];
        // 2
        id = item[ID_KEY];
        // 5
        parentId = item[PARENT_KEY] || 0;
        // every item may have children，自己的孩子列表
        childrenOf[id] = childrenOf[id] || [];
        // init its children，把自己的孩子列表挂到自己身上
        item[CHILDREN_KEY] = childrenOf[id];
        if ( parentId != 0 ) {
            // init its parent's children，初始化父结点的children列表
            childrenOf[parentId] = childrenOf[parentId] || [];
            // push it into its parent's children，把自己加入到父结点的children列表中
            childrenOf[parentId].push(item);
        } else {
            // 只有树根节点直接加入树中，其它都是叶子节点，要依附添加在各自父结点的children上
            tree.push(item);
        }
    } // end for
    return tree;
}