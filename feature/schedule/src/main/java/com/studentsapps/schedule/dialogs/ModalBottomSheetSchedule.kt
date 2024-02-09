package com.studentsapps.schedule.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.studentsapps.schedule.databinding.ModalBottomSheetScheduleBinding
import com.studentsapps.schedule.viewmodels.BottomSheetScheduleViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ModalBottomSheetSchedule : BottomSheetDialogFragment() {

    private var _binding: ModalBottomSheetScheduleBinding? = null
    private val binding get() = _binding!!
    private val args: ModalBottomSheetScheduleArgs by navArgs()
    private val viewModel: BottomSheetScheduleViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ModalBottomSheetScheduleBinding.inflate(inflater, container, false)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@ModalBottomSheetSchedule.viewModel
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val scheduleId = args.scheduleId
        viewModel.setScheduleDetails(scheduleId)
    }

    override fun onDestroy() {
        super.onDestroy()
        deleteBinding()
    }

    private fun deleteBinding() {
        _binding = null
    }
}