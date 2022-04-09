package com.geekbrains.tests.presenter.search

import io.reactivex.Scheduler

internal interface SchedulerProvider {
    fun ui(): Scheduler
    fun io(): Scheduler
}