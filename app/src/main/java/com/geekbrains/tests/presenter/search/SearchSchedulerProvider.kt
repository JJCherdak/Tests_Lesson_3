package com.geekbrains.tests.presenter.search

import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import com.geekbrains.tests.presenter.SchedulerProvider

internal class SearchSchedulerProvider: SchedulerProvider {
    override fun ui(): Scheduler {
        return AndroidSchedulers.mainThread()
    }

    override fun io(): Scheduler {
        return Schedulers.io()
    }
}