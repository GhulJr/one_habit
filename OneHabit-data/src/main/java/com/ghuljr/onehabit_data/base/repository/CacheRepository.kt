package com.ghuljr.onehabit_data.base.repository

import com.ghuljr.onehabit_data.DataSource
import com.ghuljr.onehabit_data.cache.memory.ClassKey
import com.ghuljr.onehabit_data.cache.memory.MemoryCache


/** Abstract repository used, to implement cache by default
 * @param classHolderFactory        cache holder, that will obtain the instance of MemoryCache
 **/
abstract class CacheRepository<K: Any, V, H : DataSource<V>>(private val classHolderFactory: MemoryCache.Factory<K, H>) {

    protected val cache: MemoryCache<K, H> by lazy { classHolderFactory.create(::createCacheHolder) }

    abstract fun createCacheHolder(key: ClassKey<K>): H
}


