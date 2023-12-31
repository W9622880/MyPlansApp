
package com.example.myica.model.service.impl

import com.example.myica.R.xml as AppConfig
import com.example.myica.model.service.ConfigurationService
import com.example.myica.model.service.trace
import com.google.firebase.ktx.BuildConfig
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.get
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import javax.inject.Inject
import kotlinx.coroutines.tasks.await

class ConfigurationServiceImpl @Inject constructor() : ConfigurationService {
  private val remoteConfig
    get() = Firebase.remoteConfig

  init {
    if (BuildConfig.DEBUG) {
      val configSettings = remoteConfigSettings { minimumFetchIntervalInSeconds = 0 }
      remoteConfig.setConfigSettingsAsync(configSettings)
    }

    remoteConfig.setDefaultsAsync(AppConfig.remote_config_defaults)
  }

  override suspend fun fetchConfiguration(): Boolean {
    return remoteConfig.fetchAndActivate().await()
  }

  override val isShowTaskEditButtonConfig: Boolean
    get() = remoteConfig[SHOW_PLAN_EDIT_BUTTON_KEY].asBoolean()

  companion object {
    private const val SHOW_PLAN_EDIT_BUTTON_KEY = "show_plan_edit_button"
    private const val FETCH_CONFIG_TRACE = "fetchConfig"
  }
}
