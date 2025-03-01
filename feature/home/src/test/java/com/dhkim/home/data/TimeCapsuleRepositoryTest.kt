package com.dhkim.home.data

import com.dhkim.database.entity.MyTimeCapsuleEntity
import com.dhkim.story.data.dataSource.toMyTimeCapsule
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner


@RunWith(RobolectricTestRunner::class)
class TimeCapsuleRepositoryTest {

    private val timeCapsuleRepository = FakeTimeCapsuleRepository()

    @Test
    fun `특정 타임캡슐 가져오기`() = runBlocking {
        val myTimeCapsule = timeCapsuleRepository.getMyTimeCapsule("id1")

        assertEquals(myTimeCapsule?.placeName, "광화문1")
    }

    @Test
    fun `특정 타임캡슐 업데이트`() = runBlocking {
        val myTimeCapsuleEntity = MyTimeCapsuleEntity(
            id = "id1",
            date = "2024-07-07",
            openDate = "2024-09-18",
            lat = "0.0",
            lng = "0.0",
            placeName = "업데이트 장소",
            address = "서울시 어딘가100",
            content = "안녕하세요100",
            images = listOf(),
            videos = listOf(),
            checkLocation = false,
            isOpened = false,
            sharedFriends = listOf()
        )

        timeCapsuleRepository.editMyTimeCapsule(myTimeCapsuleEntity.toMyTimeCapsule())
        val placeName = timeCapsuleRepository.getMyAllTimeCapsule().first().first { it.id == "id1" }.placeName

        assertEquals(placeName, "업데이트 장소")
    }

    @Test
    fun `특정 타임캡슐 저장`() = runBlocking {
        val myTimeCapsuleEntity = MyTimeCapsuleEntity(
            id = "id100",
            date = "2024-07-07",
            openDate = "2024-09-18",
            lat = "0.0",
            lng = "0.0",
            placeName = "업데이트 장소",
            address = "서울시 어딘가100",
            content = "안녕하세요100",
            images = listOf(),
            videos = listOf(),
            checkLocation = false,
            isOpened = false,
            sharedFriends = listOf()
        )

        timeCapsuleRepository.saveMyTimeCapsule(myTimeCapsuleEntity.toMyTimeCapsule())

        assertEquals(timeCapsuleRepository.getMyAllTimeCapsule().first().size, 11)
    }

    @Test
    fun `특정 타임캡슐 삭제하기`() = runBlocking {
        timeCapsuleRepository.deleteMyTimeCapsule(id = "id1")

        assertEquals(timeCapsuleRepository.getMyAllTimeCapsule().first().size, 9)
    }
}