// src/utils/index.js
/**
 * 日期格式化
 * @param {Date|string|number} date 日期
 * @param {string} fmt 格式字符串，例如：'yyyy-MM-dd hh:mm:ss'
 * @return {string} 格式化后的日期字符串
 */
export function formatDate(date, fmt = 'yyyy-MM-dd hh:mm:ss') {
  if (!date) {
    return ''
  }
  
  // 如果是数字或字符串，转为Date对象
  if (typeof date === 'string' || typeof date === 'number') {
    date = new Date(date)
  }
  
  const o = {
    'M+': date.getMonth() + 1, // 月份
    'd+': date.getDate(), // 日
    'h+': date.getHours(), // 小时
    'm+': date.getMinutes(), // 分
    's+': date.getSeconds(), // 秒
    'q+': Math.floor((date.getMonth() + 3) / 3), // 季度
    'S': date.getMilliseconds() // 毫秒
  }
  
  if (/(y+)/.test(fmt)) {
    fmt = fmt.replace(RegExp.$1, (date.getFullYear() + '').substr(4 - RegExp.$1.length))
  }
  
  for (let k in o) {
    if (new RegExp('(' + k + ')').test(fmt)) {
      fmt = fmt.replace(
        RegExp.$1,
        RegExp.$1.length === 1 ? o[k] : ('00' + o[k]).substr(('' + o[k]).length)
      )
    }
  }
  
  return fmt
}

/**
 * 将查询对象转换为url参数字符串
 * @param {Object} query 查询对象
 * @return {string} url参数字符串
 */
export function toQueryString(query) {
  return Object.keys(query)
    .filter(key => query[key] !== null && query[key] !== undefined && query[key] !== '')
    .map(key => `${encodeURIComponent(key)}=${encodeURIComponent(query[key])}`)
    .join('&')
}

/**
 * 深拷贝对象
 * @param {Object} source 源对象
 * @return {Object} 拷贝后的对象
 */
export function deepClone(source) {
  if (!source || typeof source !== 'object') {
    return source
  }
  
  const targetObj = Array.isArray(source) ? [] : {}
  
  Object.keys(source).forEach(key => {
    if (source[key] && typeof source[key] === 'object') {
      targetObj[key] = deepClone(source[key])
    } else {
      targetObj[key] = source[key]
    }
  })
  
  return targetObj
}

/**
 * 节流函数
 * @param {Function} fn 需要节流的函数
 * @param {number} wait 等待时间
 * @return {Function} 节流后的函数
 */
export function throttle(fn, wait) {
  let timeout = null
  let previous = 0
  
  return function(...args) {
    const now = Date.now()
    const remaining = wait - (now - previous)
    const context = this
    
    if (remaining <= 0) {
      if (timeout) {
        clearTimeout(timeout)
        timeout = null
      }
      previous = now
      fn.apply(context, args)
    } else if (!timeout) {
      timeout = setTimeout(() => {
        previous = Date.now()
        timeout = null
        fn.apply(context, args)
      }, remaining)
    }
  }
}

/**
 * 防抖函数
 * @param {Function} fn 需要防抖的函数
 * @param {number} wait 等待时间
 * @param {boolean} immediate 是否立即执行
 * @return {Function} 防抖后的函数
 */
export function debounce(fn, wait, immediate = false) {
  let timeout = null
  
  return function(...args) {
    const context = this
    
    if (timeout) clearTimeout(timeout)
    
    if (immediate) {
      const callNow = !timeout
      timeout = setTimeout(() => {
        timeout = null
      }, wait)
      if (callNow) fn.apply(context, args)
    } else {
      timeout = setTimeout(() => {
        fn.apply(context, args)
      }, wait)
    }
  }
}

/**
 * 生成随机ID
 * @param {number} length ID长度
 * @return {string} 随机ID
 */
export function randomId(length = 8) {
  const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789'
  let result = ''
  
  for (let i = 0; i < length; i++) {
    result += chars.charAt(Math.floor(Math.random() * chars.length))
  }
  
  return result
}

/**
 * 树形数据转换
 * @param {Array} data 数据源
 * @param {string} id ID字段名
 * @param {string} parentId 父ID字段名
 * @param {string} children 子节点字段名
 * @param {number|string} rootId 根节点ID
 * @return {Array} 树形结构数据
 */
export function listToTree(data, id = 'id', parentId = 'parentId', children = 'children', rootId = 0) {
  // 深拷贝数据
  const cloneData = JSON.parse(JSON.stringify(data))
  // 存储结果
  const result = []
  // 存储每一项的哈希表
  const map = {}
  
  // 先将数据存入哈希表
  cloneData.forEach(item => {
    map[item[id]] = item
  })
  
  cloneData.forEach(item => {
    // 获取当前项的父节点
    const parent = map[item[parentId]]
    
    if (parent) {
      // 如果存在父节点，将当前项添加到父节点的children中
      if (!parent[children]) {
        parent[children] = []
      }
      parent[children].push(item)
    } else if (item[parentId] == rootId) {
      // 如果不存在父节点，并且parentId等于rootId，则为根节点
      result.push(item)
    }
  })
  
  return result
}

/**
 * 树形数据扁平化
 * @param {Array} tree 树形数据
 * @param {string} children 子节点字段名
 * @return {Array} 扁平化后的数据
 */
export function treeToList(tree, children = 'children') {
  const result = []
  
  const flatten = (data) => {
    data.forEach(item => {
      // 创建一个不包含children的新对象
      const node = { ...item }
      delete node[children]
      result.push(node)
      
      // 递归处理子节点
      if (item[children] && item[children].length > 0) {
        flatten(item[children])
      }
    })
  }
  
  flatten(tree)
  return result
}

/**
 * 处理接口响应数据，统一返回格式
 * @param {Object} response 接口响应数据
 * @return {Object} 处理后的数据
 */
export function handleResponse(response) {
  if (!response) return { success: false, data: null, message: '请求失败' }
  
  const { code, data, message } = response
  return {
    success: code === 200,
    data: data || null,
    message: message || (code === 200 ? '请求成功' : '请求失败')
  }
}

/**
 * 是否为外部链接
 * @param {string} path 路径
 * @return {boolean} 是否为外部链接
 */
export function isExternal(path) {
  return /^(https?:|mailto:|tel:)/.test(path)
}