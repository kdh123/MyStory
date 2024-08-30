package com.dhkim.trip

import android.content.ContentUris
import android.net.Uri
import android.provider.MediaStore
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dhkim.common.DateUtil

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.dhkim.trip.test", appContext.packageName)
    }

    @Test
    fun getImages() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val imageUris = mutableListOf<Uri>()
        val startDate = "2024-08-09"
        val endDate = "2024-08-09"

        // 시작 날짜와 끝 날짜를 밀리초로 변환
        val startMillis = DateUtil.dateToMills(startDate)
        val endMillis = DateUtil.dateToMills(endDate)

        // MediaStore에서 가져올 컬럼 지정
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATE_TAKEN
        )

        // 조건문 작성 (날짜 범위 내)
        val selection = "${MediaStore.Images.Media.DATE_TAKEN} >= ? AND ${MediaStore.Images.Media.DATE_TAKEN} <= ?"
        val selectionArgs = arrayOf(startMillis.toString(), endMillis.toString())

        // 정렬
        val sortOrder = "${MediaStore.Images.Media.DATE_TAKEN} ASC"

        // 쿼리 실행
        val query = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )

        // 결과 처리
        query?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val dateTakenColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val dateTaken = cursor.getLong(dateTakenColumn)

                // URI 생성
                val uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                imageUris.add(uri)
            }
        }

        println("imageUris : $imageUris")
    }
}