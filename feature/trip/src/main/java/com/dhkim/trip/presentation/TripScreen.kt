package com.dhkim.trip.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dhkim.trip.R
import com.dhkim.trip.domain.model.Trip
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@Composable
fun TripScreen(
    uiState: TripUiState,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_back_black),
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                    )
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
        Column(
            modifier = Modifier
                .padding(
                    top = paddingValues.calculateTopPadding() + 10.dp,
                    start = 10.dp,
                    end = 10.dp
                )
        ) {
            AddTripScheduleLayout()
            TripSchedules(
                title = "다음 여행",
                trips = uiState.nextTrips
            )
            TripSchedules(
                title = "지난 여행",
                trips = uiState.prevTrips
            )
        }
    }
}

@Composable
private fun AddTripScheduleLayout() {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .fillMaxWidth()
            .background(color = colorResource(id = R.color.light_gray))
            .clickable {

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


@Preview(showBackground = true)
@Composable
private fun AddTripScheduleLayoutPreview() {
    AddTripScheduleLayout()
}

@Composable
private fun TripSchedules(
    title: String,
    trips: ImmutableList<Trip>
) {
    if (trips.isEmpty()) {
        return
    }

    Column {
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier
                .padding(top = 10.dp)
        )
        Row(
            modifier = Modifier
        ) {
            LazyColumn(
                contentPadding = PaddingValues(vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(items = trips, key = {
                    it.id
                }) {
                    Row {
                        GlideImage(
                            imageModel = { R.drawable.ic_launcher_background },
                            previewPlaceholder = painterResource(id = R.drawable.ic_launcher_background),
                            modifier = Modifier
                                .clip(CircleShape)
                                .width(76.dp)
                                .aspectRatio(1f)
                                .align(Alignment.CenterVertically)
                        )
                        Column(
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                        ) {
                            val title = StringBuilder()
                            it.places.forEachIndexed { index, place ->
                                title.append(place)
                                if (index < it.places.size - 1) {
                                    title.append(" - ")
                                }
                            }
                            title.append(" 여행")
                            Text(
                                text = "$title",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                modifier = Modifier
                                    .padding(start = 10.dp)
                            )
                            Text(
                                text = "${it.startDate} - ${it.endDate}",
                                modifier = Modifier
                                    .padding(start = 10.dp, top = 6.dp)
                            )
                        }
                    }
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
                    places = listOf("서울", "부산"),
                    images = listOf(),
                    videos = listOf()
                )
            )
        }
    }.toImmutableList()

    val uiState = TripUiState(
        prevTrips = trips,
        nextTrips = trips
    )

    TripScreen(uiState = uiState)
}