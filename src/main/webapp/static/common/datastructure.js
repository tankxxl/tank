/*
 * MAP对象，实现MAP功能
 *
 * 接口：
 * size()     获取MAP元素个数
 * isEmpty()    判断MAP是否为空
 * clear()     删除MAP所有元素
 * put(key, value)   向MAP中增加元素（key, value)
 * remove(key)    删除指定KEY的元素，成功返回True，失败返回False
 * get(key)    获取指定KEY的元素值VALUE，失败返回NULL
 * element(index)   获取指定索引的元素（使用element.key，element.value获取KEY和VALUE），失败返回NULL
 * containsKey(key)  判断MAP中是否含有指定KEY的元素
 * containsValue(value) 判断MAP中是否含有指定VALUE的元素
 * values()    获取MAP中所有VALUE的数组（ARRAY）
 * keys()     获取MAP中所有KEY的数组（ARRAY）
 *
 * 例子：
 * var map = new Map();
 *
 * map.put("key", "value");
 * var val = map.get("key")
 * ……
 *
 */

function Map() {
    // this.elements = new Array();
    // or
    this.elements = [];

    // 获取map元素个数
    this.size = function () {
        return this.elements.length;
    };

    // 判断map是否为空
    this.isEmpty = function () {
        return (this.elements.length < 1);
    };

    // 删除map所有元素
    this.clear = function () {
        this.elements = new Array();
    };

    // 向map中增加元素(key，value)
    this.put = function (_key, _value) {
        this.elements.push(
            {key: _key,
            value: _value}
        );
    };

    // 删除指定key的元素，成功返回true，失败返回false
    this.removeByKey = function (_key) {
        var bln = false;
        try {
            for (i = 0; i < this.elements.length; i++) {
                if (this.elements[i].key == _key) {
                    this.elements.splice(i , 1);
                    return true;
                }
            }
        } catch (e) {
            bln = false;
        }
        return bln;
    };

    // 删除指定value的元素，成功返回true，失败返回false
    this.removeByValue = function (_value) {
        var bln = false;
        try {
            for (i = 0; i < this.elements.length; i++) {
                if (this.elements[i].value == _value) {
                    this.elements.splice(i , 1);
                    return true;
                }
            }
        } catch (e) {
            bln = false;
        }
        return bln;
    };

    // 删除指定value的元素，成功返回true，失败返回false
    this.removeByValueAndKey = function (_key, _value) {
        var bln = false;
        try {
            for (i = 0; i < this.elements.length; i++) {
                if (this.elements[i].value == _value && this.elements[i].key == _key) {
                    this.elements.splice(i, 1);
                    return true;
                }
            }
        } catch (e) {
            bln = false;
        }
        return bln;
    };

    // 获取指定key的元素值value，失败返回 null
    this.get = function (_key) {
        try {
            for (i = 0; i < this.elements.length; i++) {
                if (this.elements[i].key == _key) {
                    return this.elements[i].value;
                }
            }
        } catch (e) {
            return null;
        }
        return null;
    };

    // 获取指定索引的元素（使用element.key, element.value获取key和value），失败返回null
    this.element = function (_index) {
        if (_index < 0 || _index >= this.elements.length) {
            return null;
        }
        return this.elements[_index];
    };

    // 判断map中是否含有指定key的元素
    this.containsKey = function (_key) {
        var bln = false;
        try {
            for (i = 0; i < this.elements.length; i++) {
                if (this.elements[i].key == _key) {
                    bln = true;
                }
            }
        } catch (e) {
            bln = false;
        }
        return bln;
    };

    // 判断map中是否含有指定value的元素
    this.containsValue = function (_value) {
        var bln = false;
        try {
            for (i = 0; i < this.elements.length; i++) {
                if (this.elements[i].value == _value) {
                    bln = true;
                }
            }
        } catch (e) {
            bln = false;
        }
        return bln;
    };

    // 判断map中是否含有指定value的元素
    this.containsObj = function (_key, _value) {
        var bln = false;
        try {
            for (i = 0; i < this.elements.length; i++) {
                if (this.elements[i].value == _value && this.elements[i].key == _key) {
                    bln = true;
                }
            }
        } catch (e) {
            bln = false;
        }
        return bln ;
    };

    // 获取map中所有value的数组(array)
    this.values = function () {
        var arr = new Array();
        for (i = 0; i < this.elements.length; i++) {
            arr.push(this.elements[i].value);
        }
        return arr;
    };

    // 获取map中所有value的数组(array)
    this.valuesByKey = function (_key) {
        var arr = new Array();
        for (i = 0; i < this.elements.length; i++) {
            if (this.elements[i].key == _key) {
                arr.push(this.elements[i].value);
            }
        }
        return arr;
    };

    // 获取map中所有key的数组(array)
    this.keys = function () {
        var arr = new Array();
        for (i = 0; i < this.elements.length; i++) {
            arr.push(this.elements[i].key);
        }
        return arr;
    };

    // 获取key通过value
    this.keysByValue = function (_value) {
        var arr = new Array();
        for (i = 0; i < this.elements.length; i++) {
            if (_value == this.elements[i].value) {
                arr.push(this.elements[i].key);
            }
        }
        return arr;
    };

    // 获取map中所有key的数组(array)
    this.keysRemoveDuplicate = function () {
        var arr = new Array();
        for (i = 0; i < this.elements.length; i++) {
            var flag = true;
            for (var j = 0; j < arr.length; j++) {
                if (arr[j] == this.elements[i].key) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                arr.push(this.elements[i].key);
            }
        }
        return arr;
    };
}

// 以上代码直接在类函数体里面定义成员方法，将导致每个实例都有副本，重复占用了内存。
// 最为优雅简洁的方式应该算是基于原型（prototype）继承。
// 接下来我们再次来看看javascript框架prototype里是如何利用apply来创建一个定义类的模式：
// Class定义的另一种写法：
var Class = {
    // Class的create方法返回的是对象的构造函数，即create方法的作用就是进行对象实例的初始化
    create: function() {
        return function() {
            // call和apply看作是某个对象的方法，目的是通过间接方式进行调用函数。
            this.initialize.apply(this, arguments);
        }
    }
};
var JSMap = Class.create();
JSMap.prototype = {
    // 构造函数就是initialize函数
    initialize: function (type) {
        this.type = type;
    },
    showSelf: function () {
        alert("this value is " + this.type);
    }
};

var aMap = new JSMap("map-type");
aMap.showSelf();