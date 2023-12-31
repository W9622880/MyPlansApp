package com.example.myica.screens.edit_plan

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myica.common.composable.ActionToolbar
import com.example.myica.common.composable.BasicField
import com.example.myica.common.composable.CardSelector
import com.example.myica.common.composable.RegularCardEditor
import com.example.myica.common.ext.card
import com.example.myica.common.ext.fieldModifier
import com.example.myica.common.ext.spacer
import com.example.myica.common.ext.toolbarActions
import com.example.myica.model.Priority
import com.example.myica.model.Plan
import com.example.myica.model.service.PlanNotificationService
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.example.myica.R.drawable as AppIcon
import com.example.myica.R.string as AppText

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
@ExperimentalMaterialApi
fun EditPlanScreen(
    popUpScreen: () -> Unit,
    taskId: String,
    modifier: Modifier = Modifier,
    viewModel: EditPlanViewModel = hiltViewModel()
) {
    val task by viewModel.plan

    LaunchedEffect(Unit) { viewModel.initialize(taskId) }
    val postNotificationPermission=
        rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)
    val planNotificationService= PlanNotificationService(LocalContext.current)
    LaunchedEffect(key1 = true ){
        if(!postNotificationPermission.status.isGranted){
            postNotificationPermission.launchPermissionRequest()
        }
    }
    Column(
        modifier = modifier.fillMaxWidth().fillMaxHeight().verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ActionToolbar(
            title = AppText.edit_plan,
            modifier = Modifier.toolbarActions(),
            endActionIcon = AppIcon.ic_check,
            endAction = { viewModel.onDoneClick(popUpScreen,planNotificationService ) }
        )

        Spacer(modifier = Modifier.spacer())

        val fieldModifier = Modifier.fieldModifier()
        BasicField(AppText.title, task.title, viewModel::onTitleChange, fieldModifier)
        BasicField(AppText.description, task.description, viewModel::onDescriptionChange, fieldModifier)
        BasicField(AppText.more_details, task.url, viewModel::onUrlChange, fieldModifier)

        Spacer(modifier = Modifier.spacer())
        CardEditors(task, viewModel::onDateChange, viewModel::onTimeChange)
        CardSelectors(task, viewModel::onPriorityChange, viewModel::onFlagToggle)

        Spacer(modifier = Modifier.spacer())

    }
}

@ExperimentalMaterialApi
@Composable
private fun CardEditors(
    task: Plan,
    onDateChange: (Long) -> Unit,
    onTimeChange: (Int, Int) -> Unit
) {
    val activity = LocalContext.current as AppCompatActivity

    RegularCardEditor(AppText.date, AppIcon.ic_calendar, task.dueDate, Modifier.card()) {
        showDatePicker(activity, onDateChange)
    }

    RegularCardEditor(AppText.time, AppIcon.ic_clock, task.dueTime, Modifier.card()) {
        showTimePicker(activity, onTimeChange)
    }
}

@Composable
@ExperimentalMaterialApi
private fun CardSelectors(
    task: Plan,
    onPriorityChange: (String) -> Unit,
    onFlagToggle: (String) -> Unit
) {
    val prioritySelection = Priority.getByName(task.priority).name
    CardSelector(AppText.priority, Priority.getOptions(), prioritySelection, Modifier.card()) {
            newValue ->
        onPriorityChange(newValue)
    }

    val flagSelection = EditFlagOption.getByCheckedState(task.flag).name
    CardSelector(AppText.flag, EditFlagOption.getOptions(), flagSelection, Modifier.card()) { newValue
        ->
        onFlagToggle(newValue)
    }
}

private fun showDatePicker(activity: AppCompatActivity?, onDateChange: (Long) -> Unit) {
    val picker = MaterialDatePicker.Builder.datePicker().build()

    activity?.let {
        picker.show(it.supportFragmentManager, picker.toString())
        picker.addOnPositiveButtonClickListener { timeInMillis -> onDateChange(timeInMillis) }
    }
}

private fun showTimePicker(activity: AppCompatActivity?, onTimeChange: (Int, Int) -> Unit) {
    val picker = MaterialTimePicker.Builder().setTimeFormat(TimeFormat.CLOCK_24H).build()

    activity?.let {
        picker.show(it.supportFragmentManager, picker.toString())
        picker.addOnPositiveButtonClickListener { onTimeChange(picker.hour, picker.minute) }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@ExperimentalMaterialApi
@Composable
private fun showNotification(){
    val postNotificationPermission=
        rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)
    val planNotificationService= PlanNotificationService(LocalContext.current)
    LaunchedEffect(key1 = true ){
        if(!postNotificationPermission.status.isGranted){
            postNotificationPermission.launchPermissionRequest()
        }
    }
    Column {
        Button(
            onClick = {
                planNotificationService.showBasicNotification()
            }
        ) {
            Text(text = "Plan created successfully.")
        }
    }
}

