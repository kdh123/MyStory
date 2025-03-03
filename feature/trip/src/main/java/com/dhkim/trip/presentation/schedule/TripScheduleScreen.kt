package com.dhkim.trip.presentation.schedule

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.dhkim.common.DateUtil
import com.dhkim.core.trip.domain.model.TripPlace
import com.dhkim.core.trip.domain.model.TripType
import com.dhkim.core.trip.domain.model.toTripType
import com.dhkim.designsystem.MyStoryTheme
import com.dhkim.trip.R
import com.dhkim.ui.noRippleClick
import com.dhkim.ui.onStartCollect
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun TripScheduleScreen(
    isEdit: Boolean,
    uiState: TripScheduleUiState,
    sideEffect: Flow<TripScheduleSideEffect>,
    onAction: (TripScheduleAction) -> Unit,
    onBack: () -> Unit
) {
    val lifecycle = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { 3 })
    var currentPage by remember { mutableIntStateOf(1) }
    val progressAnimation by animateFloatAsState(
        targetValue = uiState.progress,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing), label = ""
    )
    var showStartDateDialog by rememberSaveable { mutableStateOf(false) }
    var showEndDateDialog by rememberSaveable { mutableStateOf(false) }

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
                style = MyStoryTheme.typography.labelLarge,
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
                            isEdit = isEdit,
                            startDate = uiState.startDate,
                            endDate = uiState.endDate,
                            onAction = onAction,
                            onMoveToPage = {
                                scope.launch {
                                    pagerState.scrollToPage(it)
                                }
                            },
                            onShowStartDateDialog = { showStartDateDialog = true },
                            onShowEndDateDialog = { showEndDateDialog = true }
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
                            if (uiState.endDate.isEmpty() || DateUtil.isBefore(date, uiState.endDate)) {
                                onSave(date)
                                onDismiss()
                            } else {
                                Toast.makeText(context, "여행 종료 날짜 이전으로 설정해주세요.", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            if (uiState.startDate.isEmpty() || DateUtil.isAfter(date, uiState.startDate)) {
                                onSave(date)
                                onDismiss()
                            } else {
                                Toast.makeText(context, "여행 시작 날짜 이후로 설정해주세요.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            ) {
                Text(
                    text = "확인",
                    style = MyStoryTheme.typography.bodyLarge,
                )
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text(
                    text = "취소",
                    style = MyStoryTheme.typography.bodyLarge,
                )
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

@Composable
private fun TripTypeScreen(
    uiState: TripScheduleUiState,
    onAction: (TripScheduleAction) -> Unit,
    onMoveToNextPage: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxSize()
    ) {
        Text(
            text = "어떤 여행을 계획하고 있나요?",
            style = MyStoryTheme.typography.bodyMediumBold
        )

        LazyColumn(
            modifier = Modifier
                .padding(top = 10.dp)
                .fillMaxWidth()
                .height(0.dp)
                .weight(1f)
        ) {
            itemsIndexed(items = TripType.entries.toTypedArray(), key = { _, item -> item.type }) { index, item ->
                TripTypeItem(
                    index = index,
                    desc = item.desc,
                    isSelected = item.type == uiState.type.type,
                    onAction = onAction
                )
            }
        }

        val onNextClick = remember {
            {
                onMoveToNextPage(1)
                onAction(TripScheduleAction.UpdateProgress(0.66f))
            }
        }

        TripTypeNextButton(onNextClick = onNextClick)
    }
}

@Composable
fun TripTypeNextButton(onNextClick: () -> Unit) {
    Text(
        text = "다음",
        style = MyStoryTheme.typography.labelLargeBold,
        color = MaterialTheme.colorScheme.onPrimary,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.primary)
            .padding(10.dp)
            .noRippleClick(onClick = onNextClick)
            .testTag("tripTypeNextBtn")
    )
}

@Composable
fun TripTypeItem(
    index: Int,
    desc: String,
    isSelected: Boolean,
    onAction: (TripScheduleAction) -> Unit
) {
    Text(
        text = desc,
        style = MyStoryTheme.typography.labelLargeBold,
        color = if (isSelected) {
            MaterialTheme.colorScheme.onPrimary
        } else {
            MaterialTheme.colorScheme.onSurface
        },
        textAlign = TextAlign.Center,
        modifier = Modifier
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(10.dp))
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = colorResource(id = R.color.primary),
                shape = RoundedCornerShape(10.dp)
            )
            .background(
                color = if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.surfaceContainer
                }
            )
            .padding(vertical = 14.dp)
            .noRippleClick { onAction(TripScheduleAction.UpdateType(index.toTripType())) }
            .testTag("tripType$index")
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
    val onTripPlaceTypeClick: (Int) -> Unit = remember {
        {
            selectedPlaceTypeIndex = it
        }
    }

    Column(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxSize()
    ) {
        Text(
            text = "여행하려는 장소가 어디인가요?",
            style = MyStoryTheme.typography.bodyMediumBold
        )

        TripPlaceTypes(
            isDomestic = selectedPlaceTypeIndex == 0,
            onClick = onTripPlaceTypeClick
        )

        if (selectedPlaceTypeIndex == 0) {
            DomesticPlaces(
                tripPlaces = uiState.tripPlaces,
                onAction = onAction,
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .fillMaxWidth()
                    .height(0.dp)
                    .weight(1f)
            )
        } else {
            AbroadPlaces(
                tripPlaces = uiState.tripPlaces,
                onAction = onAction,
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .fillMaxWidth()
                    .height(0.dp)
                    .weight(1f)
            )
        }

        val isCompleted = uiState.tripPlaces.isNotEmpty()
        val onPrevClick = remember {
            {
                onMoveToPage(0)
                onAction(TripScheduleAction.UpdateProgress(0.33f))
            }
        }
        val onNextClick = remember(isCompleted) {
            {
                if (isCompleted) {
                    onMoveToPage(2)
                    onAction(TripScheduleAction.UpdateProgress(1f))
                }
            }
        }
        TripPlaceBottom(
            isCompleted = isCompleted,
            onPrevClick = onPrevClick,
            onNextClick = onNextClick
        )
    }
}

@Composable
fun TripPlaceTypes(isDomestic: Boolean, onClick: (Int) -> Unit) {
    Row {
        Text(
            text = "국내",
            style = MyStoryTheme.typography.labelLargeBold,
            color = if (isDomestic) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                MaterialTheme.colorScheme.onSurface
            },
            modifier = Modifier
                .padding(top = 10.dp, end = 10.dp)
                .clip(RoundedCornerShape(10.dp))
                .border(
                    width = 1.dp,
                    color = colorResource(id = R.color.primary),
                    shape = RoundedCornerShape(10.dp)
                )
                .background(
                    color = if (isDomestic) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.surfaceContainer
                    }
                )
                .padding(horizontal = 10.dp, vertical = 8.dp)
                .noRippleClick { onClick(0) }
                .testTag("domestic")
        )

        Text(
            text = "해외",
            style = MyStoryTheme.typography.labelLargeBold,
            color = if (!isDomestic) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                MaterialTheme.colorScheme.onSurface
            },
            modifier = Modifier
                .padding(top = 10.dp, end = 10.dp)
                .clip(RoundedCornerShape(10.dp))
                .border(
                    width = 1.dp,
                    color = colorResource(id = R.color.primary),
                    shape = RoundedCornerShape(10.dp)
                )
                .background(
                    color = if (!isDomestic) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.surfaceContainer
                    }
                )
                .padding(horizontal = 10.dp, vertical = 8.dp)
                .noRippleClick { onClick(1) }
                .testTag("abroad")
        )
    }
}

@Composable
fun TripPlaceBottom(
    isCompleted: Boolean,
    onPrevClick: () -> Unit,
    onNextClick: () -> Unit
) {
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
            style = MyStoryTheme.typography.labelLargeBold,
            color = MaterialTheme.colorScheme.onPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(bottom = 10.dp)
                .clip(RoundedCornerShape(10.dp))
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.primary)
                .padding(10.dp)
                .noRippleClick(onClick = onPrevClick)
                .testTag("tripPlacePrevBtn")
        )

        Text(
            text = "다음",
            style = MyStoryTheme.typography.labelLargeBold,
            color = textColor,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .fillMaxWidth()
                .background(color = backgroundColor)
                .padding(10.dp)
                .noRippleClick(onClick = onNextClick)
                .testTag("tripPlaceNextBtn")
        )
    }
}

@Composable
private fun DomesticPlaces(
    tripPlaces: ImmutableList<TripPlace>,
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
            val isDomesticSelected = tripPlaces
                .filterIsInstance<TripPlace.DomesticPlace>()
                .map { it.placeName }
                .contains(item.placeName)
            PlaceItem(
                isDomestic = true,
                index = index,
                placeName = item.placeName,
                isSelected = isDomesticSelected,
                onAction = onAction
            )
        }
    }
}

@Composable
fun PlaceItem(
    isDomestic: Boolean,
    index: Int,
    placeName: String,
    isSelected: Boolean,
    onAction: (TripScheduleAction) -> Unit
) {
    Row {
        Text(
            text = placeName,
            style = MyStoryTheme.typography.bodyMediumBold,
            modifier = Modifier
                .padding(vertical = 10.dp)
                .width(0.dp)
                .weight(1f)
                .align(Alignment.CenterVertically)
        )

        Text(
            text = "선택",
            style = MyStoryTheme.typography.labelLargeBold,
            color = if (isSelected) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                MaterialTheme.colorScheme.onSurface
            },
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .border(
                    width = 1.dp,
                    color = colorResource(id = R.color.primary),
                    shape = RoundedCornerShape(20.dp)
                )
                .background(
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.surfaceContainer
                    }
                )
                .align(Alignment.CenterVertically)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .noRippleClick {
                    val entries = if (isDomestic) TripPlace.DomesticPlace.entries else TripPlace.AbroadPlace.entries
                    onAction(TripScheduleAction.UpdatePlaces(entries[index]))
                }
                .testTag(placeName)
        )
    }
}

@Composable
private fun AbroadPlaces(
    tripPlaces: ImmutableList<TripPlace>,
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
            val isAbroadSelected = tripPlaces
                .filterIsInstance<TripPlace.AbroadPlace>()
                .map { it.placeName }
                .contains(item.placeName)

            PlaceItem(
                isDomestic = false,
                index = index,
                placeName = item.placeName,
                isSelected = isAbroadSelected,
                onAction = onAction
            )
        }
    }
}

@Composable
private fun TripDateScreen(
    isEdit: Boolean,
    startDate: String,
    endDate: String,
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
            style = MyStoryTheme.typography.bodyMediumBold,
            modifier = Modifier
                .padding(bottom = 10.dp)
        )

        Box(
            modifier = Modifier
                .height(0.dp)
                .weight(1f)
        ) {
            Row {
                StartDate(
                    startDate = startDate,
                    onClick = onShowStartDateDialog,
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
                )

                Text(
                    text = " - ",
                    style = MyStoryTheme.typography.bodyMediumGray,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                )

                EndDate(
                    endDate = endDate,
                    onClick = onShowEndDateDialog,
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
                )
            }
        }

        val isCompleted = startDate.isNotEmpty() && endDate.isNotEmpty()
        val onPrevClick = remember {
            {
                onMoveToPage(1)
                onAction(TripScheduleAction.UpdateProgress(0.66f))
            }
        }
        val onNextClick = remember(isCompleted) {
            {
                if (isCompleted) {
                    if (isEdit) {
                        onAction(TripScheduleAction.UpdateTrip)
                    } else {
                        onAction(TripScheduleAction.SaveTrip)
                    }
                }
            }
        }

        SelectTripDate(
            isCompleted = isCompleted,
            onPrevClick = onPrevClick,
            onNextClick = onNextClick
        )
    }
}

@Composable
fun SelectTripDate(
    isCompleted: Boolean,
    onPrevClick: () -> Unit,
    onNextClick: () -> Unit
) {
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
            style = MyStoryTheme.typography.labelLargeBold,
            color = MaterialTheme.colorScheme.onPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(bottom = 10.dp)
                .clip(RoundedCornerShape(10.dp))
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.primary)
                .padding(10.dp)
                .noRippleClick(onClick = onPrevClick)
                .testTag("tripPlacePrevBtn")
        )

        Text(
            text = "완료",
            style = MyStoryTheme.typography.labelLargeBold,
            color = textColor,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .fillMaxWidth()
                .background(backgroundColor)
                .padding(10.dp)
                .noRippleClick(onClick = onNextClick)
        )
    }
}

@Composable
fun StartDate(
    startDate: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Text(
        text = startDate.ifEmpty { "시작일" },
        style = MyStoryTheme.typography.labelLarge,
        color = if (startDate.isEmpty()) {
            colorResource(id = R.color.gray)
        } else {
            MaterialTheme.colorScheme.onBackground
        },
        modifier = modifier
            .noRippleClick(onClick = onClick)
    )
}

@Composable
fun EndDate(
    endDate: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Text(
        text = endDate.ifEmpty { "종료일" },
        style = MyStoryTheme.typography.labelLarge,
        color = if (endDate.isEmpty()) {
            colorResource(id = R.color.gray)
        } else {
            MaterialTheme.colorScheme.onBackground
        },
        modifier = modifier
            .noRippleClick(onClick = onClick)
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun TripScheduleScreenDarkPreview() {
    MyStoryTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            TripScheduleScreen(
                isEdit = false,
                uiState = TripScheduleUiState(),
                sideEffect = MutableSharedFlow(),
                onAction = {},
                onBack = {}
            )
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun TripScheduleScreenPreview() {
    MyStoryTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            TripScheduleScreen(
                isEdit = false,
                uiState = TripScheduleUiState(),
                sideEffect = MutableSharedFlow(),
                onAction = {},
                onBack = {}
            )
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun TripTypeScreenDarkPreview() {
    MyStoryTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            TripTypeScreen(
                uiState = TripScheduleUiState(),
                onAction = {},
                onMoveToNextPage = {}
            )
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun TripTypeScreenPreview() {
    MyStoryTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            TripTypeScreen(
                uiState = TripScheduleUiState(),
                onAction = {},
                onMoveToNextPage = {}
            )
        }
    }
}


@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun TripDateScreenDarkPreview() {
    MyStoryTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            TripDateScreen(
                isEdit = false,
                startDate = "2025-08-31",
                endDate = "",
                onAction = {},
                onMoveToPage = {},
                onShowStartDateDialog = {},
                onShowEndDateDialog = {}
            )
        }
    }
}


@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun TripDateScreenPreview() {
    MyStoryTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            TripDateScreen(
                isEdit = false,
                startDate = "2025-08-31",
                endDate = "",
                onAction = {},
                onMoveToPage = {},
                onShowStartDateDialog = {},
                onShowEndDateDialog = {}
            )
        }
    }
}


@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun TripPlaceScreenDarkPreview() {
    MyStoryTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            TripPlaceScreen(
                uiState = TripScheduleUiState(
                    startDate = "2024-07-03",
                    endDate = "2024-08-04",
                    tripPlaces = persistentListOf(TripPlace.DomesticPlace.Gyeongi)
                ),
                onAction = {},
                onMoveToPage = {}
            )
        }
    }
}


@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun TripPlaceScreenPreview() {
    MyStoryTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            TripPlaceScreen(
                uiState = TripScheduleUiState(
                    startDate = "2024-07-03",
                    endDate = "2024-08-04",
                    tripPlaces = persistentListOf(TripPlace.DomesticPlace.Gyeongi)
                ),
                onAction = {},
                onMoveToPage = {}
            )
        }
    }
}