package com.studentsapps.schedule.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.studentsapps.schedule.databinding.ModalBottomSheetDayBinding
import com.studentsapps.ui.dialogs.BaseBottomSheetDialogFragment
import com.studentsapps.ui.timetable.animateOpacity

class ModalBottomSheetDay : BaseBottomSheetDialogFragment() {

    private var _binding: ModalBottomSheetDayBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = ModalBottomSheetDayBinding.inflate(inflater, container, false)
        navController = findNavController()
        binding.bottomSheetDay = this
        return binding.root
    }

    fun goToRegisterScheduleFragment(day: Int, view: View) {
        animateOpacity(view, 0.5f) {
            animateOpacity(view, 1.0f) {
                navController.previousBackStackEntry?.savedStateHandle?.set("day", day)
                dismiss()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        deleteBinding()
    }

    private fun deleteBinding() {
        _binding = null
    }
}