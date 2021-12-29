package com.ghuljr.onehabit.ui.intro.fill_data

import com.ghuljr.onehabit_tools.di.ActivityScope
import com.ghuljr.onehabit_tools.di.ForActivity
import com.ghuljr.onehabit_tools_android.base.di.BaseActivityModule
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class FillUserDataModule: BaseActivityModule<FillUserDataActivity>() {

    @Provides
    @ActivityScope
    @Named("isEmailVerified")
    fun isEmailVerified(@ForActivity activity: FillUserDataActivity): Boolean = activity.intent.extras!!.getBoolean(FillUserDataActivity.EXTRA_IS_EMAIL_VERIFIED)

    @Provides
    @ActivityScope
    @Named("isDisplayNameSet")
    fun isDisplayNameSet(@ForActivity activity: FillUserDataActivity): Boolean = activity.intent.extras!!.getBoolean(FillUserDataActivity.EXTRA_IS_DISPLAY_NAME_SET)
}