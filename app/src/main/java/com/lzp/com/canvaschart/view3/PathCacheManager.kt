package com.lzp.com.canvaschart.view3

import android.graphics.Path

/**
 * Created by li.zhipeng on 2018/5/21.
 *
 *      Path缓存的管理器
 */
class PathCacheManager {

    /**
     * 正在使用的对象集合
     * */
    private val useSet = HashSet<Path>()

    /**
     * Path的缓存集合
     * */
    private val cache = HashSet<Path>()

    /**
     * 从缓存中取一个
     * */
    fun get(): Path {
        // 如果已经没有可用的缓存Path，创建Path，并添加到useSet
        return if (cache.size == 0) {
            val path = Path()
            useSet.add(path)
            path
        } else {
            // 如果缓存中有空闲的Path，取出第一个
            val path = cache.elementAt(0)
            // 重置path的设置
            path.reset()
            // path从缓存中移动到使用中
            useSet.add(path)
            cache.remove(path)
            return path
        }
    }

    /**
     * 重置缓存, 把使用中的Path添加到缓存中，并清空缓存
     * */
    fun resetCache() {
        cache.addAll(useSet)
        useSet.clear()
    }


}