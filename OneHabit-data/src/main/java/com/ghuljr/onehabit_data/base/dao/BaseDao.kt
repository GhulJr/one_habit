package com.ghuljr.onehabit_data.base.dao

import com.ghuljr.onehabit_data.DataSource
import com.ghuljr.onehabit_data.cache.memory.ClassKey
import com.ghuljr.onehabit_data.cache.memory.MemoryCache

abstract class BaseDao<K: Any, V, H : DataSource<V>>(private val classHolderProvider: MemoryCache<K, H>.Provider) {

    protected val cache: MemoryCache<K, H> by lazy { classHolderProvider.create(::createCacheHolder) }

    abstract fun createCacheHolder(key: ClassKey<K>): H
}


