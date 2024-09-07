package com.dhkim.trip.presentation.schedule

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.dhkim.common.DateUtil
import com.dhkim.trip.R
import com.dhkim.trip.domain.model.TripPlace
import com.dhkim.trip.domain.model.TripType
import com.dhkim.trip.domain.model.toTripType
import com.dhkim.ui.noRippleClick
import com.dhkim.ui.onStartCollect
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun TripScheduleScreen(
    tripId: String,
    uiState: TripScheduleUiState,
    sideEffect: Flow<TripScheduleSideEffect>,
    onAction: (TripScheduleAction) -> Unit,
    onBack: () -> Unit
) {
    val lifecycle = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = {
        3
    })
    var currentPage by remember {
        mutableIntStateOf(1)
    }
    val progressAnimation by animateFloatAsState(
        targetValue = uiState.progress,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing), label = ""
    )
    var showStartDateDialog by rememberSaveable {
        mutableStateOf(false)
    }
    var showEndDateDialog by rememberSaveable {
        mutableStateOf(false)
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            currentPage = page + 1
        }
    }

    LaunchedEffect(tripId) {
        if (tripId.isNotEmpty()) {
            onAction(TripScheduleAction.Init(tripId))
        }
    }

    lifecycle.onStartCollect(sideEffect) {
        when (it) {
            TripScheduleSideEffect.Complete -> {
                onBack()
            }
        }
    }

    Scaffold {
        Column(
            modifier = Modifier
                .padding(10.dp)
        ) {
            LinearProgressIndicator(
                color = colorResource(id = R.color.primary),
                progress = { progressAnimation },
                modifier = Modifier
                    .clip(RoundedCornerShape(5.dp))
                    .fillMaxWidth()
                    .height(18.dp),
            )
            Text(
                text = "$currentPage / 3",
                modifier = Modifier
                    .padding(top = 8.dp)
                    .align(Alignment.End)
            )

            HorizontalPager(
                state = pagerState,
                userScrollEnabled = false
            ) { page ->
                when (page) {
                    0 -> {
                        TripTypeScreen(
                            uiState = uiState,
                            onAction = onAction,
                            onMoveToNextPage = {
                                scope.launch {
                                    pagerState.scrollToPage(it)
                                }
                            }
                        )
                    }

                    1 -> {
                        TripPlaceScreen(
                            uiState = uiState,
                            onAction = onAction,
                            onMoveToPage = {
                                scope.launch {
                                    pagerState.scrollToPage(it)
                                }
                            }
                        )
                    }

                    2 -> {
                        TripDateScreen(
                            tripId = tripId,
                            uiState = uiState,
                            onAction = onAction,
                            onMoveToPage = {
                                scope.launch {
                                    pagerState.scrollToPage(it)
                                }
                            },
                            onShowStartDateDialog = {
                                showStartDateDialog = true
                            },
                            onShowEndDateDialog = {
                                showEndDateDialog = true
                            }
                        )
                    }
                }
            }
        }

        if (showStartDateDialog) {
            Calender(
                uiState = uiState,
                isStartDate = true,
                onSave = {
                    onAction(TripScheduleAction.UpdateStartDate(it))
                },
                onDismiss = {
                    showStartDateDialog = false
                }
            )
        }

        if (showEndDateDialog) {
            Calender(
                uiState = uiState,
                isStartDate = false,
                onSave = {
                    onAction(TripScheduleAction.UpdateEndDate(it))
                },
                onDismiss = {
                    showEndDateDialog = false
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Calender(
    uiState: TripScheduleUiState,
    isStartDate: Boolean,
    onSave: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = DateUtil.dateToMills(DateUtil.dateAfterDays(1)),
        /*selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= DateUtil.dateToMills(DateUtil.todayDate())
            }
        }*/
    )

    DatePickerDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            Button(
                onClick = {
                    datePickerState.selectedDateMillis?.let {
                        val date = DateUtil.millsToDate(it)
                        if (isStartDate) {
                            if (uiState.endDate.isEmpty() || DateUtil.isBefore(
                                    date,
                                    uiState.endDate
                                )
                            ) {
                                onSave(date)
                                onDismiss()
                            } else {
                                Toast.makeText(context, "여행 종료 날짜 이전으로 설정해주세요.", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        } else {
                            if (uiState.startDate.isEmpty() || DateUtil.isAfter(
                                    date,
                                    uiState.startDate
                                )
                            ) {
                                onSave(date)
                                onDismiss()
                            } else {
                                Toast.makeText(context, "여행 시작 날짜 이후로 설정해주세요.", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    }
                }
            ) {
                Text(text = "확인")
            }
        },
        dismissButton = {
            Button(onClick = {
                onDismiss()
            }) {
                Text(text = "취소")
            }
        }
    ) {
        DatePicker(
            state = datePickerState
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TripScheduleScreenPreview() {
    TripScheduleScreen(
        tripId = "",
        uiState = TripScheduleUiState(),
        sideEffect = MutableSharedFlow(),
        onAction = {},
        onBack = {}
    )
}

@Composable
private fun TripTypeScreen(
    uiState: TripScheduleUiState,
    onAction: (TripScheduleAction) -> Unit,
    onMoveToNextPage: (Int) -> Unit
) {
    var selectedIndex by remember(uiState.type.type) {
        mutableIntStateOf(uiState.type.type)
    }

    Column(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxSize()
    ) {
        Text(
            text = "어떤 여행을 계획하고 있나요?",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

        LazyColumn(
            modifier = Modifier
                .padding(top = 10.dp)
                .fillMaxWidth()
                .height(0.dp)
                .weight(1f)
        ) {
            itemsIndexed(items = TripType.entries.toTypedArray(), key = { _, item ->
                item.type
            }) { index, item ->
                Text(
                    text = item.desc,
                    color = if (index == selectedIndex) {
                        colorResource(id = R.color.primary)
                    } else {
                        colorResource(id = R.color.black)
                    },
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .fillMaxWidth()
                        .run {
                            if (index == selectedIndex) {
                                border(
                                    width = 1.dp,
                                    color = colorResource(id = R.color.primary),
                                    shape = RoundedCornerShape(10.dp)
                                )
                            } else {
                                this
                            }
                        }
                        .background(
                            color = if (index == selectedIndex) {
                                colorResource(id = R.color.white)
                            } else {
                                colorResource(id = R.color.light_gray)
                            }
                        )
                        .padding(vertical = 14.dp)
                        .noRippleClick {
                            selectedIndex = index
                        }
                        .testTag("tripType$index")
                )
            }
        }

        Text(
            text = "다음",
            fontSize = 16.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .fillMaxWidth()
                .background(color = colorResource(id = R.color.primary))
                .padding(10.dp)
                .noRippleClick {
                    onAction(TripScheduleAction.UpdateType(selectedIndex.toTripType()))
                    onMoveToNextPage(1)
                    onAction(TripScheduleAction.UpdateProgress(0.66f))
                }
                .testTag("tripTypeNextBtn")
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TripTypeScreenPreview() {
    TripTypeScreen(
        uiState = TripScheduleUiState(),
        onAction = {},
        onMoveToNextPage = {}
    )
}

@Composable
private fun TripPlaceScreen(
    uiState: TripScheduleUiState,
    onAction: (TripScheduleAction) -> Unit,
    onMoveToPage: (Int) -> Unit
) {
    var selectedPlaceTypeIndex by remember {
        mutableIntStateOf(0)
    }

    Column(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxSize()
    ) {
        Text(
            text = "여행하려는 장소가 어디인가요?",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

        Row {
            Text(
                text = "국내",
                color = if (selectedPlaceTypeIndex == 0) {
                    colorResource(id = R.color.primary)
                } else {
                    colorResource(id = R.color.black)
                },
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(top = 10.dp, end = 10.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        color = colorResource(
                            id = if (selectedPlaceTypeIndex == 0) {
                                R.color.white
                            } else {
                                R.color.light_gray
                            }
                        )
                    )
                    .run {
                        if (selectedPlaceTypeIndex == 0) {
                            border(
                                width = 1.dp,
                                color = colorResource(id = R.color.primary),
                                shape = RoundedCornerShape(10.dp)
                            )
                        } else {
                            this
                        }
                    }
                    .padding(horizontal = 10.dp, vertical = 8.dp)
                    .noRippleClick {
                        selectedPlaceTypeIndex = 0
                    }
                    .testTag("domestic")
            )

            Text(
                text = "해외",
                color = if (selectedPlaceTypeIndex == 1) {
                    colorResource(id = R.color.primary)
                } else {
                    colorResource(id = R.color.black)
                },
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier
                    .padding(top = 10.dp, end = 10.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        color = colorResource(
                            id = if (selectedPlaceTypeIndex == 1) {
                                R.color.white
                            } else {
                                R.color.light_gray
                            }
                        )
                    )
                    .run {
                        if (selectedPlaceTypeIndex == 1) {
                            border(
                                width = 1.dp,
                                color = colorResource(id = R.color.primary),
                                shape = RoundedCornerShape(10.dp)
                            )
                        } else {
                            this
                        }
                    }
                    .padding(horizontal = 10.dp, vertical = 8.dp)
                    .noRippleClick {
                        selectedPlaceTypeIndex = 1
                    }
                    .testTag("abroad")
            )
        }

        if (selectedPlaceTypeIndex == 0) {
            DomesticPlaces(
                uiState = uiState,
                onAction = onAction,
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .fillMaxWidth()
                    .height(0.dp)
                    .weight(1f)
            )
        } else {
            AbroadPlaces(
                uiState = uiState,
                onAction = onAction,
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .fillMaxWidth()
                    .height(0.dp)
                    .weight(1f)
            )
        }

        val isCompleted = uiState.tripPlaces.isNotEmpty()

        val textColor = if (isCompleted) {
            colorResource(id = R.color.white)
        } else {
            colorResource(id = R.color.gray)
        }

        val backgroundColor = if (isCompleted) {
            colorResource(id = R.color.primary)
        } else {
            colorResource(id = R.color.light_gray)
        }

        Column {
            Text(
                text = "이전",
                fontSize = 16.sp,
                color = colorResource(id = R.color.white),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(bottom = 10.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .fillMaxWidth()
                    .background(color = colorResource(id = R.color.primary))
                    .padding(10.dp)
                    .noRippleClick {
                        onMoveToPage(0)
                        onAction(TripScheduleAction.UpdateProgress(0.33f))
                    }
                    .testTag("tripPlacePrevBtn")
            )

            Text(
                text = "다음",
                fontSize = 16.sp,
                color = textColor,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .fillMaxWidth()
                    .background(color = backgroundColor)
                    .padding(10.dp)
                    .noRippleClick {
                        if (isCompleted) {
                            onMoveToPage(2)
                            onAction(TripScheduleAction.UpdateProgress(1f))
                        }
                    }
                    .testTag("tripPlaceNextBtn")
            )
        }
    }
}

@Composable
private fun DomesticPlaces(
    uiState: TripScheduleUiState,
    onAction: (TripScheduleAction) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier
    ) {
        itemsIndexed(
            items = TripPlace.DomesticPlace.entries.toTypedArray(),
            key = { index, _ ->
                index
            }) { index, item ->
            val isDomesticSelected = uiState.tripPlaces
                .filterIsInstance<TripPlace.DomesticPlace>()
                .map { it.placeName }
                .contains(item.placeName)

            Row {
                Text(
                    text = item.placeName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(vertical = 10.dp)
                        .width(0.dp)
                        .weight(1f)
                        .align(Alignment.CenterVertically)
                )

                Text(
                    text = "선택",
                    color = if (isDomesticSelected) {
                        colorResource(id = R.color.primary)
                    } else {
                        colorResource(id = R.color.black)
                    },
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            color = colorResource(
                                id = if (isDomesticSelected) {
                                    R.color.white
                                } else {
                                    R.color.light_gray
                                }
                            )
                        )
                        .run {
                            if (isDomesticSelected) {
                                border(
                                    width = 1.dp,
                                    color = colorResource(id = R.color.primary),
                                    shape = RoundedCornerShape(20.dp)
                                )
                            } else {
                                this
                            }
                        }
                        .align(Alignment.CenterVertically)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .noRippleClick {
                            onAction(TripScheduleAction.UpdatePlaces(TripPlace.DomesticPlace.entries[index]))
                        }
                        .testTag(item.placeName)
                )
            }
        }
    }
}

@Composable
private fun AbroadPlaces(
    uiState: TripScheduleUiState,
    onAction: (TripScheduleAction) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier
    ) {
        itemsIndexed(
            items = TripPlace.AbroadPlace.entries.toTypedArray(),
            key = { index, _ ->
                index
            }) { index, item ->
            val isAbroadSelected = uiState.tripPlaces
                .filterIsInstance<TripPlace.AbroadPlace>()
                .map { it.placeName }
                .contains(item.placeName)

            Row {
                Text(
                    text = item.placeName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(vertical = 10.dp)
                        .width(0.dp)
                        .weight(1f)
                        .align(Alignment.CenterVertically)
                )

                Text(
                    text = "선택",
                    color = if (isAbroadSelected) {
                        colorResource(id = R.color.primary)
                    } else {
                        colorResource(id = R.color.black)
                    },
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            color = colorResource(
                                id = if (isAbroadSelected) {
                                    R.color.white
                                } else {
                                    R.color.light_gray
                                }
                            )
                        )
                        .run {
                            if (isAbroadSelected) {
                                border(
                                    width = 1.dp,
                                    color = colorResource(id = R.color.primary),
                                    shape = RoundedCornerShape(20.dp)
                                )
                            } else {
                                this
                            }
                        }
                        .align(Alignment.CenterVertically)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .noRippleClick {
                            onAction(TripScheduleAction.UpdatePlaces(TripPlace.AbroadPlace.entries[index]))
                        }
                )
            }
        }
    }
}

@Composable
private fun TripDateScreen(
    tripId: String,
    uiState: TripScheduleUiState,
    onAction: (TripScheduleAction) -> Unit,
    onMoveToPage: (Int) -> Unit,
    onShowStartDateDialog: () -> Unit,
    onShowEndDateDialog: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxSize()
    ) {
        Text(
            text = "여행 일정이 어떻게 되나요?",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(bottom = 10.dp)
        )

        Row(
            modifier = Modifier
                .height(0.dp)
                .weight(1f)
        ) {
            Text(
                text = uiState.startDate.ifEmpty { "시작일" },
                color = if (uiState.startDate.isEmpty()) {
                    colorResource(id = R.color.gray)
                } else {
                    colorResource(id = R.color.black)
                },
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .width(0.dp)
                    .weight(1f)
                    .border(
                        width = 1.dp,
                        color = colorResource(id = R.color.gray),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(10.dp)
                    .noRippleClick {
                        onShowStartDateDialog()
                    }
            )
            Text(
                text = " - ",
                color = colorResource(id = R.color.gray),
                fontSize = 16.sp,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
            )
            Text(
                text = uiState.endDate.ifEmpty { "종료일" },
                color = if (uiState.endDate.isEmpty()) {
                    colorResource(id = R.color.gray)
                } else {
                    colorResource(id = R.color.black)
                },
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .width(0.dp)
                    .weight(1f)
                    .border(
                        width = 1.dp,
                        color = colorResource(id = R.color.gray),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(10.dp)
                    .noRippleClick {
                        onShowEndDateDialog()
                    }
            )
        }

        val isCompleted = uiState.startDate.isNotEmpty() && uiState.endDate.isNotEmpty()
        val textColor = if (isCompleted) {
            colorResource(id = R.color.white)
        } else {
            colorResource(id = R.color.gray)
        }
        val backgroundColor = if (isCompleted) {
            colorResource(id = R.color.primary)
        } else {
            colorResource(id = R.color.light_gray)
        }

        Column {
            Text(
                text = "이전",
                fontSize = 16.sp,
                color = colorResource(id = R.color.white),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(bottom = 10.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .fillMaxWidth()
                    .background(color = colorResource(id = R.color.primary))
                    .padding(10.dp)
                    .noRippleClick {
                        onMoveToPage(1)
                        onAction(TripScheduleAction.UpdateProgress(0.66f))
                    }
                    .testTag("tripPlacePrevBtn")
            )

            Text(
                text = "완료",
                fontSize = 16.sp,
                color = textColor,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .fillMaxWidth()
                    .background(backgroundColor)
                    .padding(10.dp)
                    .noRippleClick {
                        if (isCompleted) {
                            if (tripId.isNotEmpty()) {
                                onAction(TripScheduleAction.UpdateTrip(tripId = tripId))
                            } else {
                                onAction(TripScheduleAction.SaveTrip)
                            }

                        }
                    }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TripDateScreenPreview() {
    TripDateScreen(
        tripId = "",
        uiState = TripScheduleUiState(),
        onAction = {},
        onMoveToPage = {},
        onShowStartDateDialog = {},
        onShowEndDateDialog = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun TripPlaceScreenPreview() {
    TripPlaceScreen(
        uiState = TripScheduleUiState(),
        onAction = {},
        onMoveToPage = {}
    )
}