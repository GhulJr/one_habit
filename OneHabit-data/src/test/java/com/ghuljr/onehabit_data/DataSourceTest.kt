package com.ghuljr.onehabit_data


import arrow.core.Either
import arrow.core.right
import com.ghuljr.onehabit_error.BaseEvent
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.processors.PublishProcessor
import io.reactivex.rxjava3.schedulers.TestScheduler
import io.reactivex.rxjava3.subscribers.TestSubscriber
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.concurrent.TimeUnit

class DataSourceTest {

    private val testScheduler = TestScheduler()
    private val sourceProcessor = PublishProcessor.create<DataSource.CacheWithTime<Int>>()

    private lateinit var sourceFlowable: Flowable<DataSource.CacheWithTime<Int>>
    private lateinit var dataSource: DataSource<Int>
    private lateinit var dataObserver: TestSubscriber<Either<BaseEvent, Int>>

    @Before
    fun before() {
        sourceFlowable = sourceProcessor.replay(1).refCount()
    }

    fun setUp(dueToInMillis: Long = 5L, timeUnit: TimeUnit = TimeUnit.SECONDS, fetch: () -> Single<Either<BaseEvent, Int>> = { Single.just(VALID_FETCH_RESULT_1.right()) }) {
        dataSource = DataSource(testScheduler, testScheduler, dueToInMillis, timeUnit, sourceFlowable, fetch, { sourceProcessor.onNext(it) })
        dataObserver = dataSource.dataFlowable.test()
    }

    @Test
    fun `when invalid time is passed, then return error`() {
        try {
            setUp(dueToInMillis = INVALID_CACHE_TIME)
            assertTrue(false)
        } catch (e:  IllegalArgumentException) {
            assertTrue(true)
        }
    }

    @Test
    fun `when there is no data yet, then fetch the data`() {

    }

    @Test
    fun `when data is not yet valid, then fetch the new data`() {

    }

    @Test
    fun `when refresh is called, then fetch data`() {

    }

    @Test
    fun `when cache time is reached, then fetch data`() {

    }

    @Test
    fun `when data source changed, but cache time is not reached, then return this data`() {

    }

    @Test
    fun `when there are multiple calls for update, then the last one is called`() {

    }

    @Test
    fun `when cached time is reached a little before refresh is called, then proceed refresh only once`() {

    }

    @Test
    fun `when fetch succeed, then update the data`() {

    }


    @Test
    fun `when fetch fails, then return cached data and then return an error`() {

    }



    companion object {
        private const val INVALID_CACHE_TIME = -100L
        private const val VALID_FETCH_RESULT_1 = 1
    }
}