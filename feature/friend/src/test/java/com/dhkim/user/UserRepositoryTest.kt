package com.dhkim.user

import com.dhkim.testing.FakeUserRepository
import com.dhkim.user.domain.model.Friend
import com.dhkim.user.domain.model.User
import com.dhkim.user.domain.usecase.GetMyInfoUseCase
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class UserRepositoryTest {

    private val userRepository = FakeUserRepository()

    private val getMyInfoUseCase = GetMyInfoUseCase(userRepository)

    @Test
    fun `remote 내 정보 가져오기`() = runTest {
        val data = getMyInfoUseCase().first()
        val friends = mutableListOf<Friend>().apply {
            repeat(10) {
                val friend = Friend(
                    id = "id$it",
                    nickname = "nickname$it",
                    profileImage = "$it",
                    uuid = "uuid$it"
                )
                add(friend)
            }
        }

        val user = User(
            id = "myId",
            profileImage = "1",
            uuid = "myUuid",
            friends = friends,
            requests = listOf()
        )
        assertEquals(data, user)
    }

    @Test
    fun `친구 추가(요청)`() = runTest {
        val myInfo = getMyInfoUseCase().first()
        val prevFriendSize = myInfo.friends.size

        userRepository.addFriend(
            userId = "id15",
            userProfileImage = "14"
        )

        val currentFriendSize = getMyInfoUseCase().first().friends.size

        assertEquals(prevFriendSize + 1, currentFriendSize)
    }

    @Test
    fun `친구 승낙`() = runTest {
        val myInfo = getMyInfoUseCase().first()
        val prevFriendSize = myInfo.friends.filter { it.isPending }.size

        userRepository.acceptFriend(
            userId = "id7",
            userProfileImage = "6",
            userUuid = "uuid7"
        ).first()

        val currentFriendSize = getMyInfoUseCase().first().friends.filter { it.isPending }.size
        assertEquals(prevFriendSize -1, currentFriendSize)
    }

    @Test
    fun `친구 삭제`() = runTest {
        val myInfo = getMyInfoUseCase().first()
        val prevFriendSize = myInfo.friends.size

        userRepository.deleteFriend("id7").first()

        val currentFriendSize = getMyInfoUseCase().first().friends.size
        assertEquals(prevFriendSize -1, currentFriendSize)
    }

    @Test
    fun `친구 정보 변경`() = runTest {
        userRepository.updateFriend(friend = Friend(id = "id7", profileImage = "0ef", uuid = "1038", nickname = "홍길동")).first()

        advanceTimeBy(1000L)
        assertEquals(getMyInfoUseCase().first().friends.first { it.id == "id7" }.nickname, "홍길동")
    }
}