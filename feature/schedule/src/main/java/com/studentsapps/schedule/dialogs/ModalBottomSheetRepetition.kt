package com.studentsapps.schedule.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.studentsapps.schedule.databinding.ModalBottomSheetRepetitionBinding
import com.studentsapps.schedule.viewmodels.RecurrenceOption
import com.studentsapps.ui.dialogs.BaseBottomSheetDialogFragment
import com.studentsapps.ui.timetable.animateOpacity

class ModalBottomSheetRepetition : BaseBottomSheetDialogFragment() {

    private var _binding: ModalBottomSheetRepetitionBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ModalBottomSheetRepetitionBinding.inflate(inflater, container, false)
        navController = findNavController()
        binding.fragment = this
        return binding.root
    }

    fun goToRegisterScheduleFragment(recurrenceOption: RecurrenceOption, view: View) {
        animateOpacity(view, 0.5f) {
            animateOpacity(view, 1.0f) {
                navController.previousBackStackEntry?.savedStateHandle?.set(
                    "repetition",
                    recurrenceOption
                )
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