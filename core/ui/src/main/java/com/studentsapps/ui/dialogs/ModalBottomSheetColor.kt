package com.studentsapps.ui.dialogs

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.studentsapps.ui.databinding.ModalBottomSheetColorBinding
import kotlin.math.floor

class ModalBottomSheetColor : BaseBottomSheetDialogFragment() {
    private var _binding: ModalBottomSheetColorBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController

    private var color = 0xffffff00.toInt()
    private var currentColorHsv = FloatArray(3)
    private val alpha = Color.alpha(color)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ModalBottomSheetColorBinding.inflate(inflater, container, false)
        navController = findNavController()
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.setCancelable(false)
        color = arguments?.getInt("colorCourse") ?: 0
        Color.colorToHSV(color, currentColorHsv)
        binding.colorPicker.setHue(getHue())
        binding.selectedColor.backgroundTintList = ColorStateList.valueOf(getColor())

        binding.selectHue.setOnTouchListener(View.OnTouchListener { viewHue, event ->
            if (event.action == MotionEvent.ACTION_MOVE || event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_UP) {
                var y = event.y
                if (y < 0f) y = 0f
                if (y > viewHue.measuredHeight) {
                    y =
                        viewHue.measuredHeight - 0.001f
                }
                var hue = 360f - 360f / viewHue.measuredHeight * y
                if (hue == 360f) hue = 0f
                setHue(hue)

                // update view
                binding.colorPicker.setHue(getHue())
                moveCursor()
                binding.selectedColor.backgroundTintList = ColorStateList.valueOf(getColor())
                return@OnTouchListener true
            }
            false
        })

        binding.colorPicker.setOnTouchListener(View.OnTouchListener { picker, event ->
            if (event.action == MotionEvent.ACTION_MOVE || event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_UP) {
                var x = event.x
                var y = event.y
                if (x < 0f) x = 0f
                if (x > picker.measuredWidth) x = picker.measuredWidth.toFloat()
                if (y < 0f) y = 0f
                if (y > picker.measuredHeight) y = picker.measuredHeight.toFloat()
                setSat(1f / picker.measuredWidth * x)
                setVal(1f - 1f / picker.measuredHeight * y)

                moveTarget()
                binding.selectedColor.backgroundTintList = ColorStateList.valueOf(getColor())
                return@OnTouchListener true
            }
            false
        })

        binding.accept.setOnClickListener {
            navController.previousBackStackEntry?.savedStateHandle?.set("color", getColor())
            dialog?.dismiss()
        }

        val vto = view.viewTreeObserver
        vto.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                moveCursor()
                moveTarget()
                view.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }

    private fun getColor(): Int {
        val argb = Color.HSVToColor(currentColorHsv)
        return alpha shl 24 or (argb and 0x00ffffff)
    }


    private fun getHue(): Float {
        return currentColorHsv[0]
    }

    private fun setHue(hue: Float) {
        currentColorHsv[0] = hue
    }

    private fun setSat(sat: Float) {
        currentColorHsv[1] = sat
    }

    private fun setVal(va: Float) {
        currentColorHsv[2] = va
    }

    private fun getSat(): Float {
        return currentColorHsv[1]
    }

    private fun getVal(): Float {
        return currentColorHsv[2]
    }

    private fun moveCursor() {
        var y =
            binding.selectHue.measuredHeight - getHue() * binding.selectHue.measuredHeight / 360f
        if (y == binding.selectHue.measuredHeight.toFloat()) y = 0f
        val layoutParams = binding.hueCursor.layoutParams as ConstraintLayout.LayoutParams
        if (y == 0f) {
            layoutParams.topMargin =
                (binding.selectHue.top + y - binding.layoutColorPalette.paddingTop).toInt()
        } else {
            layoutParams.topMargin =
                (binding.selectHue.top + y - binding.hueCursor.measuredHeight - binding.layoutColorPalette.paddingTop).toInt()
        }
        binding.hueCursor.layoutParams = layoutParams
    }

    private fun moveTarget() {
        val x: Float = getSat() * binding.colorPicker.measuredWidth
        val y: Float = (1f - getVal()) * binding.colorPicker.measuredHeight
        val layoutParams = binding.pickerCursor.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.leftMargin =
            (binding.colorPicker.left + x - floor((binding.pickerCursor.measuredWidth / 2).toDouble()) - binding.layoutColorPalette.paddingLeft).toInt()
        layoutParams.topMargin =
            (binding.colorPicker.top + y - floor((binding.pickerCursor.measuredHeight / 2).toDouble()) - binding.layoutColorPalette.paddingTop).toInt()
        binding.pickerCursor.layoutParams = layoutParams
    }

    override fun onDestroy() {
        super.onDestroy()
        deleteBinding()
    }

    private fun deleteBinding() {
        _binding = null
    }
}