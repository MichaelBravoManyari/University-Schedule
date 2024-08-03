package com.studentsapps.schedule.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.studentsapps.schedule.R
import com.studentsapps.schedule.databinding.ModalBottomSheetScheduleBinding
import com.studentsapps.schedule.viewmodels.BottomSheetScheduleViewModel
import com.studentsapps.ui.dialogs.BaseBottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ModalBottomSheetSchedule : BaseBottomSheetDialogFragment() {

    private var _binding: ModalBottomSheetScheduleBinding? = null
    private lateinit var navController: NavController
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
        navController = findNavController()
        val scheduleId = args.scheduleId
        viewModel.setScheduleDetails(scheduleId)
        binding.btnEditSchedule.setOnClickListener {
            navController.navigate(
                ModalBottomSheetScheduleDirections.actionModalBottomSheetScheduleToRegisterScheduleFragment(
                    scheduleId
                )
            )
        }
        binding.btnDeleteSchedule.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext()).setTitle(R.string.delete)
                .setMessage(R.string.delete_schedule)
                .setPositiveButton(R.string.accept_dialog) { _, _ ->
                    viewModel.deleteSchedule(args.scheduleId)
                    navController.previousBackStackEntry?.savedStateHandle?.set("updateScheduleList", true)
                    this.dismiss()
                }
                .setNegativeButton(R.string.cancel) { dialog, _ ->
                    dialog.dismiss()
                }.show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        deleteBinding()
    }

    private fun deleteBinding() {
        _binding = null
    }
}