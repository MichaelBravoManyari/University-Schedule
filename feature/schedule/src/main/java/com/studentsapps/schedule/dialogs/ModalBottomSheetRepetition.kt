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

class ModalBottomSheetRepetition : BottomSheetDialogFragment() {

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

    fun goToRegisterScheduleFragment(recurrenceOption: RecurrenceOption) {
        navController.previousBackStackEntry?.savedStateHandle?.set("repetition", recurrenceOption)
        dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        deleteBinding()
    }

    private fun deleteBinding() {
        _binding = null
    }
}