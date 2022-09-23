package com.ghuljr.onehabit_data


import arrow.core.*
import com.ghuljr.onehabit_data.cache.synchronisation.DataSource
import com.ghuljr.onehabit_data.utils.TestData
import com.ghuljr.onehabit_error.BaseEvent
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.processors.PublishProcessor
import io.reactivex.rxjava3.schedulers.TestScheduler
import io.reactivex.rxjava3.subscribers.TestSubscriber
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.concurrent.TimeUnit

class DataSourceTest {

    private val testScheduler = TestScheduler()

    private val sourceProcessor = PublishProcessor.create<DataSource.CacheWithTime<Int>>()
    private val fetchData = mockk<() -> Single<Either<BaseEvent, Int>>>()
    private val invalidateAndUpdate = mockk<(DataSource.CacheWithTime<Int>) -> Unit>()
    private val cacheWithTimeSlot = slot<DataSource.CacheWithTime<Int>>()

    private lateinit var sourceFlowable: Flowable<DataSource.CacheWithTime<Int>>
    private lateinit var dataSource: DataSource<Int>
    private lateinit var dataObserver: TestSubscriber<Either<BaseEvent, Int>>


    @Before
    fun before() {
        sourceFlowable = sourceProcessor.replay(1).refCount()
    }

    fun setUp(
        dueToInMillis: Long = 5L,
        timeUnit: TimeUnit = TimeUnit.SECONDS,
        cacheWithTime: DataSource.CacheWithTime<Int> = TestData.cacheWithTime(VALID_FETCH_RESULT_1.some(), testScheduler.now(TimeUnit.MILLISECONDS) + TimeUnit.MINUTES.toMillis(5L)),
        fetch: List<Single<Either<BaseEvent, Int>>> = listOf(Single.just(VALID_FETCH_RESULT_1.right()))
    ) {

        every { invalidateAndUpdate.invoke(capture(cacheWithTimeSlot)) } returns Unit
        every { fetchData.invoke() } returnsMany fetch

        dataSource = DataSource(
            testScheduler,
            testScheduler,
            testScheduler,
            dueToInMillis,
            timeUnit,
            sourceFlowable,
            fetchData,
            {
                sourceProcessor.onNext(cacheWithTime)
                invalidateAndUpdate(it)
            })
        dataObserver = dataSource.dataFlowable.test()
    }

    @Test
    fun `when invalid time is passed, then return error`() {
        try {
            setUp(dueToInMillis = INVALID_CACHE_TIME)
            assertTrue(false)
        } catch (e: IllegalArgumentException) {
            assertTrue(true)
        }
    }

    @Test
    fun `when there is no data yet, then fetch the data`() {
        setUp()
        testScheduler.advanceTimeTo(500L, TimeUnit.MILLISECONDS)

        sourceProcessor.onNext(TestData.cacheWithTime())
        testScheduler.advanceTimeTo(2L, TimeUnit.SECONDS)

        verify(exactly = 1) { fetchData() }
        verify(exactly = 1) { invalidateAndUpdate(any()) }
        assertTrue(cacheWithTimeSlot.captured.value.orNull() == VALID_FETCH_RESULT_1)

        dataObserver.assertValueCount(1)
            .assertValueAt(0) { it.orNull()!! == VALID_FETCH_RESULT_1 }
    }

    @Test
    fun `when data is not yet valid, then fetch the new data`() {
        setUp()
        testScheduler.advanceTimeTo(500L, TimeUnit.MILLISECONDS)
        sourceProcessor.onNext(
            TestData.cacheWithTime(
                VALID_FETCH_RESULT_2.some(),
                testScheduler.now(TimeUnit.MILLISECONDS) + TimeUnit.SECONDS.toMillis(5L)
            )
        )

        testScheduler.advanceTimeTo(4L, TimeUnit.SECONDS)

        dataObserver.assertValueCount(1)
            .assertValueAt(0) { it.orNull()!! == VALID_FETCH_RESULT_2 }

        testScheduler.advanceTimeTo(6L, TimeUnit.SECONDS)

        verify(exactly = 1) { fetchData() }
        verify(exactly = 1) { invalidateAndUpdate(any()) }
        assertTrue(cacheWithTimeSlot.captured.value.orNull() == VALID_FETCH_RESULT_1)

        dataObserver.assertValueCount(2)
            .assertValueAt(1) { it.orNull()!! == VALID_FETCH_RESULT_1 }
    }

    @Test
    fun `when refresh is called, then fetch data`() {
        setUp()
        testScheduler.advanceTimeTo(500L, TimeUnit.MILLISECONDS)
        sourceProcessor.onNext(
            TestData.cacheWithTime(
                VALID_FETCH_RESULT_2.some(),
                testScheduler.now(TimeUnit.MILLISECONDS) + TimeUnit.MINUTES.toMillis(5L)
            )
        )

        testScheduler.advanceTimeTo(4L, TimeUnit.SECONDS)

        dataObserver.assertValueCount(1)
            .assertValueAt(0) { it.orNull()!! == VALID_FETCH_RESULT_2 }

        dataSource.refresh().test()

        testScheduler.advanceTimeTo(6L, TimeUnit.SECONDS)

        verify(exactly = 1) { fetchData() }
        verify(exactly = 1) { invalidateAndUpdate(any()) }
        assertTrue(cacheWithTimeSlot.captured.value.orNull() == VALID_FETCH_RESULT_1)

        dataObserver.assertValueCount(2)
            .assertValueAt(1) { it.orNull()!! == VALID_FETCH_RESULT_1 }
    }

    @Test
    fun `when data source changed, but cache time is not reached, then return this data`() {
        setUp()
        testScheduler.advanceTimeTo(500L, TimeUnit.MILLISECONDS)
        sourceProcessor.onNext(
            TestData.cacheWithTime(
                VALID_FETCH_RESULT_2.some(),
                testScheduler.now(TimeUnit.MILLISECONDS) + TimeUnit.MINUTES.toMillis(5L)
            )
        )

        testScheduler.advanceTimeTo(4L, TimeUnit.SECONDS)
        dataObserver.assertValueCount(1)
            .assertValueAt(0) { it.orNull()!! == VALID_FETCH_RESULT_2 }

        sourceProcessor.onNext(
            TestData.cacheWithTime(
                VALID_FETCH_RESULT_1.some(),
                testScheduler.now(TimeUnit.MILLISECONDS) + TimeUnit.MINUTES.toMillis(5L)
            )
        )

        testScheduler.advanceTimeTo(6L, TimeUnit.SECONDS)

        verify(exactly = 0) { fetchData() }
        verify(exactly = 0) { invalidateAndUpdate(any()) }
        assertFalse(cacheWithTimeSlot.isCaptured)

        dataObserver.assertValueCount(2)
            .assertValueAt(1) { it.orNull()!! == VALID_FETCH_RESULT_1 }
    }

    @Test
    fun `when data source changed, but emits the same, then emit only the first one`() {
        setUp()
        testScheduler.advanceTimeTo(500L, TimeUnit.MILLISECONDS)
        sourceProcessor.onNext(
            TestData.cacheWithTime(
                VALID_FETCH_RESULT_2.some(),
                testScheduler.now(TimeUnit.MILLISECONDS) + TimeUnit.MINUTES.toMillis(5L)
            )
        )

        testScheduler.advanceTimeTo(4L, TimeUnit.SECONDS)
        dataObserver.assertValueCount(1)
            .assertValueAt(0) { it.orNull()!! == VALID_FETCH_RESULT_2 }

        sourceProcessor.onNext(
            TestData.cacheWithTime(
                VALID_FETCH_RESULT_2.some(),
                testScheduler.now(TimeUnit.MILLISECONDS) + TimeUnit.MINUTES.toMillis(5L)
            )
        )

        testScheduler.advanceTimeTo(6L, TimeUnit.SECONDS)

        verify(exactly = 0) { fetchData() }
        verify(exactly = 0) { invalidateAndUpdate(any()) }
        assertFalse(cacheWithTimeSlot.isCaptured)

        dataObserver.assertValueCount(1)
    }

    @Test
    fun `when there are multiple calls for update, then the last one is called`() {
        setUp(
            fetch = listOf(
                Single.just(VALID_FETCH_RESULT_1.right() as Either<BaseEvent, Int>)
                    .delay(1L, TimeUnit.SECONDS, testScheduler)
            )
        )
        testScheduler.advanceTimeTo(500L, TimeUnit.MILLISECONDS)
        sourceProcessor.onNext(
            TestData.cacheWithTime(
                VALID_FETCH_RESULT_2.some(),
                testScheduler.now(TimeUnit.MILLISECONDS) + TimeUnit.MINUTES.toMillis(5L)
            )
        )

        testScheduler.advanceTimeTo(4L, TimeUnit.SECONDS)
        dataObserver.assertValueCount(1)
            .assertValueAt(0) { it.orNull()!! == VALID_FETCH_RESULT_2 }

        dataSource.refresh().test()
        dataSource.refresh().test()
        dataSource.refresh().test()
        dataSource.refresh().test()

        testScheduler.advanceTimeBy(100L, TimeUnit.MILLISECONDS)
        dataSource.refresh().test()


        testScheduler.advanceTimeTo(7L, TimeUnit.SECONDS)

        verify(exactly = 1) { fetchData() }
        verify(exactly = 1) { invalidateAndUpdate(any()) }
        assertTrue(cacheWithTimeSlot.captured.value.orNull() == VALID_FETCH_RESULT_1)

        dataObserver.assertValueCount(2)
            .assertValueAt(1) { it.orNull()!! == VALID_FETCH_RESULT_1 }

    }

    @Test
    fun `when cached time is reached a little before refresh is called, then proceed refresh only once`() {
        setUp()
        testScheduler.advanceTimeTo(500L, TimeUnit.MILLISECONDS)
        sourceProcessor.onNext(
            TestData.cacheWithTime(
                VALID_FETCH_RESULT_2.some(),
                testScheduler.now(TimeUnit.MILLISECONDS) + TimeUnit.SECONDS.toMillis(5L)
            )
        )
        dataSource.refresh().test()

        testScheduler.advanceTimeTo(6L, TimeUnit.SECONDS)

        verify(exactly = 1) { fetchData() }
        verify(exactly = 1) { invalidateAndUpdate(any()) }
        assertTrue(cacheWithTimeSlot.captured.value.orNull() == VALID_FETCH_RESULT_1)

        dataObserver.assertValueCount(2)
            .assertValueAt(0) { it.orNull()!! == VALID_FETCH_RESULT_2 }
            .assertValueAt(1) { it.orNull()!! == VALID_FETCH_RESULT_1 }
    }

    @Test
    fun `when fetch succeed, then update the data`() {
        setUp(
            cacheWithTime = TestData.cacheWithTime(
                value = VALID_FETCH_RESULT_1.some(),
                dueInMillis = testScheduler.now(TimeUnit.MILLISECONDS) + testScheduler.now(TimeUnit.MILLISECONDS) + TimeUnit.MINUTES.toMillis(
                    5L
                )
            )
        )

        val sourceObserver = sourceFlowable.test()

        testScheduler.advanceTimeTo(500L, TimeUnit.MILLISECONDS)

        sourceProcessor.onNext(TestData.cacheWithTime())

        testScheduler.advanceTimeTo(5L, TimeUnit.SECONDS)

        sourceObserver.assertValueCount(2)
            .assertValueAt(0) { it.value.isEmpty() }
            .assertValueAt(1) { it.value.orNull() == VALID_FETCH_RESULT_1 }

        dataObserver.assertValueCount(1)
            .assertValueAt(0) { it.orNull() == VALID_FETCH_RESULT_1 }
    }


    @Test
    fun `when fetch fails, then return cached data and then return an error`() {
        setUp(fetch = listOf(Single.just(TestData.TestError.left() as Either<BaseEvent, Int>)))
        testScheduler.advanceTimeTo(500L, TimeUnit.MILLISECONDS)
        sourceProcessor.onNext(TestData.cacheWithTime())
        dataSource.refresh().test()

        testScheduler.advanceTimeTo(6L, TimeUnit.SECONDS)

        verify(exactly = 1) { fetchData() }
        verify(exactly = 0) { invalidateAndUpdate(any()) }
        assertFalse(cacheWithTimeSlot.isCaptured)

        dataObserver.assertValueCount(1)
            .assertValueAt(0) { it.swap().orNull()!! == TestData.TestError }
    }

    @Test
    fun `when fetch fails, then refresh and return correct value`() {
        setUp(fetch = listOf(Single.just(TestData.TestError.left() as Either<BaseEvent, Int>), Single.just(VALID_FETCH_RESULT_1.right() as Either<BaseEvent, Int>)))
        testScheduler.advanceTimeTo(500L, TimeUnit.MILLISECONDS)
        sourceProcessor.onNext(TestData.cacheWithTime())

        testScheduler.advanceTimeTo(6L, TimeUnit.SECONDS)

        verify(exactly = 1) { fetchData() }
        verify(exactly = 0) { invalidateAndUpdate(any()) }
        assertFalse(cacheWithTimeSlot.isCaptured)

        dataSource.refresh().test()

        testScheduler.advanceTimeTo(8L, TimeUnit.SECONDS)

        verify(exactly = 2) { fetchData() }
        verify(exactly = 1) { invalidateAndUpdate(any()) }
        assertTrue(cacheWithTimeSlot.isCaptured)

        dataObserver.assertValueCount(2)
            .assertValueAt(0) { it.swap().orNull()!! == TestData.TestError }
            .assertValueAt(1) { it.orNull() == VALID_FETCH_RESULT_1 }
    }


    companion object {
        private const val INVALID_CACHE_TIME = -100L
        private const val VALID_FETCH_RESULT_1 = 1
        private const val VALID_FETCH_RESULT_2 = 2
    }
}