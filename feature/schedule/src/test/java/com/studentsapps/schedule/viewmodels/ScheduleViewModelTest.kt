package com.studentsapps.schedule.viewmodels

/*
@ExperimentalCoroutinesApi
class ScheduleViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var timetableUserPreferencesRepository: FakeTimetableUserPreferencesRepository
    private lateinit var scheduleRepository: ScheduleRepository
    private lateinit var subject: ScheduleViewModel
    private lateinit var firebaseAuth: FirebaseAuth

    @Before
    fun setup() {
        timetableUserPreferencesRepository = FakeTimetableUserPreferencesRepository()
        scheduleRepository = FakeScheduleRepository()
        firebaseAuth = mockk(relaxed = true)
        subject = ScheduleViewModel(
            timetableUserPreferencesRepository, scheduleRepository,
            TimetableUtils(), firebaseAuth
        )
    }

    @Test
    fun stateIsInitiallyLoading() = runTest {
        timetableUserPreferencesRepository.init()
        assertEquals(
            ScheduleUiState.Success(
                TimetableUserPreferences(
                    showAsGrid = true,
                    is12HoursFormat = true,
                    showSaturday = true,
                    showSunday = true,
                    isMondayFirstDayOfWeek = true
                ),
                mapOf(),
            ), subject.uiState.value
        )
    }

    @Test
    fun setShowAsGrid_trueAndFalse() = runTest {
        timetableUserPreferencesRepository.init()
        val collectJob1 = launch(UnconfinedTestDispatcher()) { subject.uiState.collect() }

        subject.setShowAsGrid()

        assertEquals(
            ScheduleUiState.Success(
                TimetableUserPreferences(
                    showAsGrid = false,
                    is12HoursFormat = true,
                    showSaturday = true,
                    showSunday = true,
                    isMondayFirstDayOfWeek = true
                ),
                mapOf()
            ), subject.uiState.value
        )

        collectJob1.cancel()
    }
}*/
