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
import androidx.compose.foundation.lazy.items
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

            val onMoveToNextPage: (Int) -> Unit = remember {
                {
                    scope.launch {
                        pagerState.scrollToPage(it)
                    }
                }
            }

            HorizontalPager(
                state = pagerState,
                userScrollEnabled = false
            ) { page ->
                when (page) {
                    0 -> {
                        TripTypeScreen(
                            type = uiState.type.type,
                            onAction = onAction,
                            onMoveToNextPage = onMoveToNextPage
                        )
                    }

                    1 -> {
                        var selectedPlaceTypeIndex by rememberSaveable {
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

                            TripPlaceType(
                                isDomesticSelected = selectedPlaceTypeIndex == 0,
                                onDomesticClick = {
                                    selectedPlaceTypeIndex = 0
                                },
                                onAbroadClick = {
                                    selectedPlaceTypeIndex = 1
                                }
                            )

                            var selectedCount by rememberSaveable {
                                mutableIntStateOf(0)
                            }
                            var selectedDomesticPlaces by rememberSaveable {
                                mutableStateOf("")
                            }
                            var selectedAbroadPlaces by rememberSaveable {
                                mutableStateOf("")
                            }

                            if (selectedPlaceTypeIndex == 0) {
                                LazyColumn(
                                    verticalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier
                                        .padding(vertical = 10.dp)
                                        .fillMaxWidth()
                                        .height(0.dp)
                                        .weight(1f)
                                ) {
                                    items(
                                        items = TripPlace.DomesticPlace.entries.toTypedArray(),
                                        key = { item ->
                                            item.placeName
                                        }
                                    ) { item ->
                                        PlaceItem(
                                            initIsSelected = selectedDomesticPlaces.split(",")
                                                .filter { it.isNotBlank() }
                                                .contains(item.placeName),
                                            placeName = item.placeName,
                                            onClick = { isSelected, placeName ->
                                                if (isSelected) {
                                                    selectedDomesticPlaces += "$placeName,"
                                                    selectedCount++
                                                } else {
                                                    selectedDomesticPlaces = selectedDomesticPlaces.replace("$placeName,", "")
                                                    selectedCount--
                                                }
                                                onAction(TripScheduleAction.SelectTripPlace(isSelected, placeName))
                                            }
                                        )
                                    }
                                }
                            } else {
                                LazyColumn(
                                    verticalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier
                                        .padding(vertical = 10.dp)
                                        .fillMaxWidth()
                                        .height(0.dp)
                                        .weight(1f)
                                ) {
                                    items(
                                        items = TripPlace.AbroadPlace.entries.toTypedArray(),
                                        key = { item ->
                                            item.placeName
                                        }
                                    ) { item ->
                                        PlaceItem(
                                            initIsSelected = selectedAbroadPlaces.split(",")
                                                .filter { it.isNotBlank() }
                                                .contains(item.placeName),
                                            placeName = item.placeName,
                                            onClick = { isSelected, placeName ->
                                                if (isSelected) {
                                                    selectedCount++
                                                    selectedAbroadPlaces += "$placeName,"
                                                } else {
                                                    selectedCount--
                                                    selectedAbroadPlaces = selectedAbroadPlaces.replace("$placeName,", "")
                                                }

                                                onAction(TripScheduleAction.SelectTripPlace(isSelected, placeName))
                                            }
                                        )
                                    }
                                }
                            }

                            TripPlaceBottom(
                                enableNextButton = selectedCount > 0,
                                onPrevClick = {
                                    onMoveToNextPage.invoke(0)
                                    onAction(TripScheduleAction.UpdateProgress(0.33f))
                                },
                                onNextClick = {
                                    onMoveToNextPage.invoke(2)
                                    onAction(TripScheduleAction.UpdateProgress(1f))
                                }
                            )
                        }
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

@Composable
fun TripPlaceType(
    isDomesticSelected: Boolean,
    onDomesticClick: () -> Unit,
    onAbroadClick: () -> Unit
) {
    Row {
        Text(
            text = "국내",
            color = if (isDomesticSelected) {
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
                            shape = RoundedCornerShape(10.dp)
                        )
                    } else {
                        this
                    }
                }
                .padding(horizontal = 10.dp, vertical = 8.dp)
                .noRippleClick {
                    onDomesticClick()
                }
                .testTag("domestic")
        )

        Text(
            text = "해외",
            color = if (!isDomesticSelected) {
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
                        id = if (!isDomesticSelected) {
                            R.color.white
                        } else {
                            R.color.light_gray
                        }
                    )
                )
                .run {
                    if (!isDomesticSelected) {
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
                    onAbroadClick()
                }
                .testTag("abroad")
        )
    }
}

@Composable
fun TripPlaceBottom(
    enableNextButton: Boolean,
    onPrevClick: () -> Unit,
    onNextClick: () -> Unit
) {
    val textColor = if (enableNextButton) {
        colorResource(id = R.color.white)
    } else {
        colorResource(id = R.color.gray)
    }

    val backgroundColor = if (enableNextButton) {
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
                    onPrevClick()
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
                    if (enableNextButton) {
                        onNextClick()
                    }
                }
                .testTag("tripPlaceNextBtn")
        )
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
    type: Int,
    onAction: (TripScheduleAction) -> Unit,
    onMoveToNextPage: (Int) -> Unit
) {
    var selectedIndex by remember(type) {
        mutableIntStateOf(type)
    }

    val onClick: (Int) -> Unit = remember {
        {
            selectedIndex = it
        }
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
                TripTypeItem(
                    isSelected = selectedIndex == index,
                    index = index,
                    desc = item.desc,
                    onClick = onClick
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

@Composable
fun TripTypeItem(
    isSelected: Boolean,
    index: Int,
    desc: String,
    onClick: (Int) -> Unit,
) {
    Text(
        text = desc,
        color = if (isSelected) {
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
                if (isSelected) {
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
                color = if (isSelected) {
                    colorResource(id = R.color.white)
                } else {
                    colorResource(id = R.color.light_gray)
                }
            )
            .padding(vertical = 14.dp)
            .noRippleClick(index = index, onClick = onClick)
            .testTag("tripType$index")
    )
}

@Preview(showBackground = true)
@Composable
private fun TripTypeScreenPreview() {
    TripTypeScreen(
        type = TripType.Alone.type,
        onAction = {},
        onMoveToNextPage = {}
    )
}

@Composable
fun PlaceItem(
    initIsSelected: Boolean,
    placeName: String,
    onClick: (Boolean, String) -> Unit,
) {
    var isSelected by rememberSaveable {
        mutableStateOf(initIsSelected)
    }

    Row {
        Text(
            text = placeName,
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
            color = if (isSelected) {
                colorResource(id = R.color.primary)
            } else {
                colorResource(id = R.color.black)
            },
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(
                    color = colorResource(
                        id = if (isSelected) {
                            R.color.white
                        } else {
                            R.color.light_gray
                        }
                    )
                )
                .run {
                    if (isSelected) {
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
                    isSelected = !isSelected
                    onClick(isSelected, placeName)
                }
                .testTag(placeName)
        )
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
                                onAction(TripScheduleAction.UpdateTrip)
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