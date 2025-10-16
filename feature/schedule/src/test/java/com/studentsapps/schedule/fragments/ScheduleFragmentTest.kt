package com.studentsapps.schedule.fragments

/*
@Config(application = HiltTestApplication::class)
@HiltAndroidTest
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class ScheduleFragmentTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val mainDispatcherRule = MainDispatcherRule()

    @BindValue
    val timetableUtils = spyk<TimetableUtils>()

    @BindValue
    val fakeTimetableUserPreferencesRepository = FakeTimetableUserPreferencesRepository()

    private lateinit var navController: TestNavHostController

    @Before
    fun setup() {
        navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        navController.setGraph(R.navigation.schedule_navigation)
    }

    @Test
    fun verifyCurrentMonthDisplayedInAppBar() {
        every { timetableUtils.getMonth(any()) } returns "July"
        val expectedMonth = "July"
        createScheduleFragment()
        onView(withId(R.id.toolbar)).check(matches(hasDescendant(withText(expectedMonth))))
    }

    @Test
    fun verifyAppBarMenuOptionsDisplayed() {
        createScheduleFragment()
        onView(withId(R.id.change_timetable_view)).check(matches(isDisplayed()))
    }

    @Test
    fun verifyTimetableViewsChange() = runTest {
        createScheduleFragment()
        fakeTimetableUserPreferencesRepository.init()
        verifyTimetableGridViewIsDisplayed()
        onView(withId(R.id.change_timetable_view)).perform(click())
        verifyTimetableListViewIsDisplayed()
        onView(withId(R.id.change_timetable_view)).perform(click())
        verifyTimetableGridViewIsDisplayed()
    }

    @Test
    fun testCorrectMonthDisplayedOnCurrentWeekChange() {
        val expectedMonth = "September"
        every { timetableUtils.getCurrentDate() } returns LocalDate.of(2023, 8, 26)
        createScheduleFragment()
        onView(withId(R.id.toolbar)).check(matches(hasDescendant(withText(expectedMonth))))
    }

    @Test
    fun verifyNavigationToTimetableSettingsOnActionClick() {
        createScheduleFragment()
        openContextualActionModeOverflowMenu()
        onView(withText(R.string.configuration)).perform(click())
        assertThat(navController.currentDestination?.id, `is`(R.id.scheduleConfigurationFragment))
    }

    @Test
    fun testRepeatedSchedulesDisplayedInGridMode() = runTest {
        mockUtilsGetCurrentDate(LocalDate.of(2023, 11, 20))
        fakeTimetableUserPreferencesRepository.init()
        createScheduleFragment()
        onView(withContentDescription("8")).perform(scrollTo()).check(matches(isDisplayed()))
        onView(withContentDescription("9")).perform(scrollTo()).check(matches(isDisplayed()))
    }

    @Test
    fun testSchedulesDisplayedInGridModeForSpecificDate() = runTest {
        mockUtilsGetCurrentDate(LocalDate.of(2023, 11, 20))
        fakeTimetableUserPreferencesRepository.init()
        createScheduleFragment()
        onView(withContentDescription("1")).perform(scrollTo()).check(matches(isDisplayed()))
        onView(withContentDescription("2")).perform(scrollTo()).check(matches(isDisplayed()))
        onView(withContentDescription("3")).perform(scrollTo()).check(matches(isDisplayed()))
        onView(withContentDescription("4")).perform(scrollTo()).check(matches(isDisplayed()))
        onView(withContentDescription("5")).perform(scrollTo()).check(matches(isDisplayed()))
        onView(withContentDescription("6")).perform(scrollTo()).check(matches(isDisplayed()))
        onView(withContentDescription("7")).perform(scrollTo()).check(matches(isDisplayed()))
        onView(withContentDescription("8")).perform(scrollTo()).check(matches(isDisplayed()))
        onView(withContentDescription("9")).perform(scrollTo()).check(matches(isDisplayed()))
        onView(withContentDescription("10")).perform(scrollTo()).check(matches(isDisplayed()))
        onView(withContentDescription("11")).check(doesNotExist())
    }


    @Test
    fun testCheckSchedulesDisplayedInListModeForSpecificDate() = runTest {
        mockUtilsGetCurrentDate(LocalDate.of(2023, 11, 20))
        fakeTimetableUserPreferencesRepository.setShowAsGrid(false)
        fakeTimetableUserPreferencesRepository.init()
        createScheduleFragment()
        verifyTimetableListViewIsDisplayed()
        onView(withId(com.studentsapps.ui.R.id.schedule_list_container)).perform(
            RecyclerViewActions.scrollTo<TimetableListAdapter.TimetableListViewHolder>(
                hasDescendant(withText("Math"))
            )
        )
        onView(withText("Math")).check(matches(isDisplayed()))
        onView(withId(com.studentsapps.ui.R.id.schedule_list_container)).perform(
            RecyclerViewActions.scrollTo<TimetableListAdapter.TimetableListViewHolder>(
                hasDescendant(withText("Math7"))
            )
        )
        onView(withText("Math7")).check(matches(isDisplayed()))
        onView(withId(com.studentsapps.ui.R.id.schedule_list_container)).perform(
            RecyclerViewActions.scrollTo<TimetableListAdapter.TimetableListViewHolder>(
                hasDescendant(withText("Math8"))
            )
        )
        onView(withText("Math8")).check(matches(isDisplayed()))
    }

    @Test
    fun testNavigateToScheduleDetailsDialogOnClickInGridMode() = runTest {
        mockUtilsGetCurrentDate(LocalDate.of(2023, 11, 20))
        fakeTimetableUserPreferencesRepository.init()
        createScheduleFragment()
        onView(withContentDescription("1")).perform(scrollTo(), click())
        assertThat(navController.currentDestination?.id, `is`(R.id.modalBottomSheetSchedule))
    }

    private fun createScheduleFragment() {
        launchFragmentInHiltContainer<ScheduleFragment>(navigation = {
            Navigation.setViewNavController(requireView(), navController)
        })
    }

    private fun mockUtilsGetCurrentDate(date: LocalDate) {
        every { timetableUtils.getCurrentDate() } returns date
    }

    private fun verifyTimetableGridViewIsDisplayed() {
        onView(withId(com.studentsapps.ui.R.id.schedule_container_and_grid))
            .check(matches(isDisplayed())).check(
                matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE))
            )
        onView(withId(com.studentsapps.ui.R.id.schedule_list_container))
            .check(matches(not(isDisplayed()))).check(
                matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.GONE))
            )
    }

    private fun verifyTimetableListViewIsDisplayed() {
        onView(withId(com.studentsapps.ui.R.id.schedule_list_container))
            .check(
                matches(
                    ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)
                )
            )
        onView(withId(com.studentsapps.ui.R.id.schedule_container_and_grid))
            .check(matches(not(isDisplayed())))
            .check(
                matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.GONE))
            )
    }
}*/
