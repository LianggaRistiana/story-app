package com.dicoding.storyapp.helper.service

import android.content.Intent
import android.widget.RemoteViewsService
import com.dicoding.storyapp.helper.factory.StackRemoteViewsFactory

class StackWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory {
        return StackRemoteViewsFactory(this.applicationContext)
    }
}