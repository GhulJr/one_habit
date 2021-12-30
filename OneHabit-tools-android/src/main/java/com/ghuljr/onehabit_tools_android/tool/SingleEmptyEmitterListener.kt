package com.ghuljr.onehabit_tools_android.tool

import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.SingleEmitter

class SingleEmptyEmitterListener(private val emitter: SingleEmitter<Unit>) :
    OnCompleteListener<Void> {
    override fun onComplete(task: Task<Void>) = try {
        emitter.onSuccess(Unit)
    } catch (exception: Exception) {
        emitter.onError(exception)
    }
}

fun Task<Void>.asUnitSingle(): Single<Unit> =
    if (isComplete) Single.just(Unit) else Single.create { emitter ->
        this.addOnCompleteListener(SingleEmptyEmitterListener(emitter))
    }
