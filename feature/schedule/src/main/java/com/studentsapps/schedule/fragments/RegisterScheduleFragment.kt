package com.studentsapps.schedule.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.studentsapps.schedule.databinding.FragmentRegisterScheduleBinding
import com.studentsapps.schedule.viewmodels.RegisterScheduleViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterScheduleFragment : Fragment() {

    private var _binding: FragmentRegisterScheduleBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RegisterScheduleViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterScheduleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}