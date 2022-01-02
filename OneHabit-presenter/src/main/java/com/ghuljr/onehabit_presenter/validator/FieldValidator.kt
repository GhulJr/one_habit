package com.ghuljr.onehabit_presenter.validator

import arrow.core.Either
import com.ghuljr.onehabit_error.ValidationError
import com.ghuljr.onehabit_tools.di.ComputationScheduler
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.subjects.BehaviorSubject
import javax.inject.Inject


interface FieldValidator {
    val validatedFieldEitherObservable: Observable<Either<ValidationError.EmptyField, String>>
    fun fieldChanged(field: String)

}

class FieldValidatorImpl @Inject constructor(@ComputationScheduler computationScheduler: Scheduler) :
    FieldValidator {

    private val fieldSubject = BehaviorSubject.create<String>()

    override val validatedFieldEitherObservable: Observable<Either<ValidationError.EmptyField, String>> =
        fieldSubject
            .map {
                it.trim().let { field ->
                    Either.conditionally(field.isNotBlank(), { ValidationError.EmptyField }, { field })
                }
            }
            .subscribeOn(computationScheduler)
            .replay(1)
            .refCount()

    override fun fieldChanged(field: String): Unit = fieldSubject.onNext(field)
}