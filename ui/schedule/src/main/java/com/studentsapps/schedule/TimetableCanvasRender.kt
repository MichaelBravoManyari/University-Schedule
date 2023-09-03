package com.studentsapps.schedule

import android.graphics.Bitmap
import javax.inject.Inject

class TimetableCanvasRender @Inject constructor() {

    fun createTimetableBitmap(bitmapWith: Int, bitmapHeight: Int): Bitmap {
        return Bitmap.createBitmap(bitmapWith, bitmapHeight, Bitmap.Config.ARGB_8888)
    }
}