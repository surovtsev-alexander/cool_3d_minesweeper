/*
MIT License

Copyright (c) [2022] [Alexander Surovtsev]

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */


package com.surovtsev.settingsscreen.viewmodel.helpers.finitestatemachine

import com.surovtsev.core.room.dao.SettingsList
import com.surovtsev.core.room.entities.Settings
import com.surovtsev.finitestatemachine.state.data.InitializationIsNotFinished
import com.surovtsev.settingsscreen.viewmodel.helpers.uicontrolsinfo.SettingsUIControlsInfo
import com.surovtsev.templateviewmodel.finitestatemachine.screendata.ViewModelData
import kotlinx.coroutines.flow.MutableStateFlow


sealed interface SettingsScreenData: ViewModelData.UserData {

    interface SettingsLoaded: SettingsScreenData {
        val settingsList: SettingsList
    };

    open class SettingsLoadedData(
        override val settingsList: SettingsList,
    ): SettingsLoaded, InitializationIsNotFinished

    open class SettingsDataIsSelected(
        settingsLoaded: SettingsLoaded,
        settingsData: Settings.SettingsData,
    ): SettingsLoaded {
        override val settingsList: SettingsList = settingsLoaded.settingsList

        val uiControls = SettingsUIControlsInfo()

        val selectedSettingsId = MutableStateFlow(-1L)

        init {
            uiControls.updateInfo(settingsData)
        }
    }
}