package com.ghuljr.onehabit_data.base.dao

import com.ghuljr.onehabit_data.cache.memory.CacheHolder
import com.ghuljr.onehabit_data.cache.memory.ClassHolder
import com.ghuljr.onehabit_data.cache.memory.ClassKey

abstract class BaseDao<K, V, H : CacheHolder<K, V>>(private val classHolderProvider: ClassHolder<K, H>.Provider) {

    protected val cache: ClassHolder<K, H> by lazy { classHolderProvider.create(::createCacheHolder) }

    abstract fun createCacheHolder(key: ClassKey<K>): H
}


