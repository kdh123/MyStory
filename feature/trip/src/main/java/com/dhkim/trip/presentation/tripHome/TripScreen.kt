package com.dhkim.trip.presentation.tripHome

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dhkim.trip.R
import com.dhkim.core.trip.domain.model.Trip
import com.dhkim.ui.Popup
import com.dhkim.ui.noRippleClick
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import java.util.UUID

@Composable
fun TripScreen(
    uiState: TripUiState,
    onAction: (TripAction) -> Unit,
    modifier: Modifier = Modifier,
    onNavigateToSchedule: (String) -> Unit,
    onNavigateToDetail: (String) -> Unit,
    showPopup: (Popup) -> Unit
) {
    Scaffold(
        topBar = {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .width(0.dp)
                            .weight(1f)
                            .align(Alignment.CenterVertically)
                    ) {
                        Text(
                            text = "여행",
                            modifier = Modifier
                                .align(Alignment.Center),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                }
                Divider(
                    thickness = 1.dp,
                    color = colorResource(id = R.color.light_gray)
                )
            }
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(modifier = modifier.fillMaxSize()) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            return@Scaffold
        }

        if (uiState.trips.isNullOrEmpty()) {
            EmptyTripScheduleLayout(
                onNavigateToSchedule = {
                    onNavigateToSchedule(" ")
                }
            )
            return@Scaffold
        }

        Column(
            modifier = modifier
                .padding(
                    top = paddingValues.calculateTopPadding() + 10.dp,
                    start = 10.dp,
                    end = 10.dp
                )
        ) {
            AddTripScheduleLayout(
                onNavigateToSchedule = {
                    onNavigateToSchedule(" ")
                }
            )
            TripSchedules(
                items = uiState.trips ?: persistentListOf(),
                showDeleteDialog = {
                    showPopup(
                        Popup.Warning(
                            title = "일정 삭제",
                            desc = "정말 삭제하겠습니까?",
                            onPositiveClick = {
                                onAction(TripAction.DeleteTrip(it))
                            }
                        )
                    )
                },
                onNavigateToDetail = onNavigateToDetail,
            )
        }
    }
}

@Composable
private fun AddTripScheduleLayout(
    onNavigateToSchedule: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .fillMaxWidth()
            .background(color = colorResource(id = R.color.light_gray))
            .clickable {
                onNavigateToSchedule()
            }
    ) {
        Row(
            modifier = Modifier
                .padding(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .width(42.dp)
                    .aspectRatio(1f)
                    .background(color = colorResource(id = R.color.primary))
                    .align(Alignment.CenterVertically)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_add_white),
                    contentDescription = null,
                    modifier = Modifier
                        .background(color = colorResource(id = R.color.primary))
                        .align(Alignment.Center)
                )
            }
            Column(
                modifier = Modifier
                    .padding(start = 10.dp)
                    .align(Alignment.CenterVertically)
            ) {
                Text(
                    text = "여행 일정 만들기",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(text = "새로운 여행을 떠나볼까요?")
            }
        }
    }
}

@Composable
private fun EmptyTripScheduleLayout(
    onNavigateToSchedule: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_calendar_gray),
                contentDescription = null,
                modifier = Modifier
                    .width(58.dp)
                    .aspectRatio(1f)
                    .align(Alignment.CenterHorizontally)
            )
            Text(
                text = "여행을 계획하고 있나요?",
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .align(Alignment.CenterHorizontally)
            )
            Text(
                text = "새로운 여행 일정을 등록해보세요",
                textAlign = TextAlign.Center,
                color = colorResource(id = R.color.gray),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            )
            Text(
                text = "일정 만들기",
                color = colorResource(id = R.color.white),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(top = 10.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(color = colorResource(id = R.color.primary))
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 10.dp, horizontal = 16.dp)
                    .noRippleClick {
                        onNavigateToSchedule()
                    }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AddTripScheduleLayoutPreview() {
    EmptyTripScheduleLayout(
        onNavigateToSchedule = {}
    )
}

@Composable
fun TripSchedules(
    items: ImmutableList<TripItem>,
    modifier: Modifier = Modifier,
    showDeleteDialog: (String) -> Unit,
    onNavigateToDetail: (String) -> Unit,
) {
    if (items.isEmpty()) {
        return
    }

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(items = items, key = {
            it.id
        }) {
            TripScheduleItem(
                item = it,
                showDeleteDialog = showDeleteDialog,
                onNavigateToDetail = onNavigateToDetail
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TripScheduleItem(
    item: TripItem,
    showDeleteDialog: (String) -> Unit,
    onNavigateToDetail: (String) -> Unit,
) {
    when (item.data) {
        is String -> {
            Text(
                text = item.data,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier
                    .padding(top = 10.dp)
            )
        }

        is Trip -> {
            val tripSchedule = item.data

            val res = if (tripSchedule.isDomestic) {
                R.drawable.ic_domestic_white
            } else {
                R.drawable.ic_abroad_white
            }

            val backgroundColor = if (tripSchedule.isDomestic) {
                R.color.teal_200
            } else {
                R.color.sky_blue
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .combinedClickable(
                        onLongClick = {
                            showDeleteDialog(tripSchedule.id)
                        },
                        onClick = {
                            onNavigateToDetail(tripSchedule.id)
                        }
                    )
            ) {
                GlideImage(
                    imageModel = { res },
                    previewPlaceholder = painterResource(id = res),
                    modifier = Modifier
                        .clip(CircleShape)
                        .width(76.dp)
                        .aspectRatio(1f)
                        .background(color = colorResource(id = backgroundColor))
                        .align(Alignment.CenterVertically)
                        .padding(12.dp)
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                ) {
                    val title = StringBuilder()
                    tripSchedule.places.forEachIndexed { index, place ->
                        title.append(place)
                        if (index < tripSchedule.places.size - 1) {
                            title.append(" - ")
                        }
                    }
                    title.append(" 여행")
                    Text(
                        text = "$title",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 2,
                        modifier = Modifier
                            .padding(start = 10.dp)
                    )
                    Text(
                        text = "${tripSchedule.startDate} - ${tripSchedule.endDate}",
                        modifier = Modifier
                            .padding(start = 10.dp, top = 6.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TripScreenPreview() {
    val trips = mutableListOf<Trip>().apply {
        repeat(5) {
            add(
                Trip(
                    id = "id$it",
                    startDate = "2024-05-30",
                    endDate = "2024-06-10",
                    places = if (it % 2 == 0) {
                        listOf("프랑스", "독일")
                    } else {
                        listOf("서울", "부산")
                    },
                    images = listOf(),
                    videos = listOf(),
                    isDomestic = it % 2 != 0
                )
            )
        }
    }.toImmutableList()

    val items = mutableListOf<TripItem>()
    items.add(TripItem(id = "${UUID.randomUUID()}", data = "다음 여행"))
    items.addAll(trips.map { TripItem(id = it.id, data = it) })

    val uiState = TripUiState(trips = items.toImmutableList())

    TripScreen(
        uiState = uiState,
        onNavigateToSchedule = {},
        onNavigateToDetail = {},
        onAction = {},
        showPopup = {}
    )
}