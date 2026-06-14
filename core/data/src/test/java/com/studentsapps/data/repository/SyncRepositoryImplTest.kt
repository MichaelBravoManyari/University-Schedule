package com.studentsapps.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.studentsapps.database.model.CourseEntity
import com.studentsapps.domain.sync.model.NetworkConnectivityChecker
import com.studentsapps.network.datasources.CourseNetworkDataSource
import com.studentsapps.network.datasources.ScheduleNetworkDataSource
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

/**
 * Unit tests for [SyncRepositoryImpl].
 */
class SyncRepositoryImplTest {

    // ── Mocks ─────────────────────────────────────────────────────────────────

    private val fakeAuth = mockk<FirebaseAuth>()
    private val fakeCurrentUser = mockk<FirebaseUser>()
    private val fakePendingOps = mockk<PendingOperationRepository>()
    private val fakeCourseNetwork = mockk<CourseNetworkDataSource>()
    private val fakeScheduleNetwork = mockk<ScheduleNetworkDataSource>()
    private val fakeCourseRepo = mockk<CourseRepository>()
    private val fakeScheduleRepo = mockk<ScheduleRepository>()
    private val fakeConnectivity = mockk<NetworkConnectivityChecker>()

    private lateinit var sut: SyncRepositoryImpl

    @Before
    fun setUp() {
        sut = SyncRepositoryImpl(
            auth = fakeAuth,
            pendingOperations = fakePendingOps,
            courseNetworkDataSource = fakeCourseNetwork,
            scheduleNetworkDataSource = fakeScheduleNetwork,
            courseRepository = fakeCourseRepo,
            scheduleRepository = fakeScheduleRepo,
            connectivityChecker = fakeConnectivity,
        )
    }

    // ── startSyncIfNeeded — pre-condition guard ───────────────────────────────

    @Test
    fun givenNoAuthenticatedUser_whenStartSyncIfNeeded_thenDoesNothing() = runTest {
        every { fakeAuth.currentUser } returns null

        sut.startSyncIfNeeded()

        coVerify(exactly = 0) { fakeCourseNetwork.getAllCourses(any()) }
        coVerify(exactly = 0) { fakeScheduleNetwork.getAllSchedules(any()) }
    }

    @Test
    fun givenDeviceIsOffline_whenStartSyncIfNeeded_thenDoesNothing() = runTest {
        every { fakeAuth.currentUser } returns fakeCurrentUser
        every { fakeCurrentUser.uid } returns "user-123"
        every { fakeConnectivity.isConnected() } returns false

        sut.startSyncIfNeeded()

        coVerify(exactly = 0) { fakeCourseNetwork.getAllCourses(any()) }
    }

    @Test
    fun givenAuthenticatedUserAndOnlineAndNoPendingOps_whenStartSyncIfNeeded_thenRunsSync() =
        runTest {
            every { fakeAuth.currentUser } returns fakeCurrentUser
            every { fakeCurrentUser.uid } returns "user-123"
            every { fakeConnectivity.isConnected() } returns true
            every { fakePendingOps.getPendingOperations("PENDING", "user-123") } returns
                    flowOf(emptyList())

            coEvery { fakeCourseNetwork.getAllCourses("user-123") } returns emptyList()
            coEvery { fakeScheduleNetwork.getAllSchedules("user-123") } returns emptyList()
            every { fakeCourseRepo.getAllCourseEntity("user-123") } returns flowOf(emptyList())
            every { fakeScheduleRepo.getAllScheduleEntity("user-123") } returns flowOf(emptyList())
            coEvery { fakeScheduleRepo.scheduleAllUserAlarms("user-123") } just Runs

            sut.startSyncIfNeeded()

            coVerify(exactly = 1) { fakeCourseNetwork.getAllCourses("user-123") }
            coVerify(exactly = 1) { fakeScheduleNetwork.getAllSchedules("user-123") }
            coVerify(exactly = 1) { fakeScheduleRepo.scheduleAllUserAlarms("user-123") }
        }

    // ── startSynchronization — diff logic ─────────────────────────────────────

    @Test
    fun givenRemoteCourseMissingLocally_whenStartSynchronization_thenInsertsRemoteCourse() = runTest {
        val remoteCourse = buildNetworkCourse(id = "c1")
        coEvery { fakeCourseNetwork.getAllCourses("user-123") } returns listOf(remoteCourse)
        coEvery { fakeScheduleNetwork.getAllSchedules("user-123") } returns emptyList()
        every { fakeCourseRepo.getAllCourseEntity("user-123") } returns flowOf(emptyList())
        every { fakeScheduleRepo.getAllScheduleEntity("user-123") } returns flowOf(emptyList())
        coEvery { fakeCourseRepo.registerCourseEntity(any()) } just Runs
        coEvery { fakeScheduleRepo.scheduleAllUserAlarms("user-123") } just Runs

        sut.startSynchronization("user-123")

        coVerify(exactly = 1) { fakeCourseRepo.registerCourseEntity(any()) }
        coVerify(exactly = 0) { fakeCourseRepo.updateCourseEntity(any()) }
    }

    @Test
    fun givenRemoteCourseIsNewer_whenStartSynchronization_thenUpdatesLocalCourse() = runTest {
        val now = LocalDateTime.now()
        val remoteCourse = buildNetworkCourse(id = "c1", lastModified = now)
        val localCourse = buildCourseEntity(id = "c1", lastModified = now.minusDays(1))

        coEvery { fakeCourseNetwork.getAllCourses("user-123") } returns listOf(remoteCourse)
        coEvery { fakeScheduleNetwork.getAllSchedules("user-123") } returns emptyList()
        every { fakeCourseRepo.getAllCourseEntity("user-123") } returns flowOf(listOf(localCourse))
        every { fakeScheduleRepo.getAllScheduleEntity("user-123") } returns flowOf(emptyList())
        coEvery { fakeCourseRepo.updateCourseEntity(any()) } just Runs
        coEvery { fakeScheduleRepo.scheduleAllUserAlarms("user-123") } just Runs

        sut.startSynchronization("user-123")

        coVerify(exactly = 1) { fakeCourseRepo.updateCourseEntity(any()) }
        coVerify(exactly = 0) { fakeCourseRepo.registerCourseEntity(any()) }
    }

    @Test
    fun givenLocalCourseAbsentFromRemote_whenStartSynchronization_thenDeletesLocalCourse() = runTest {
        val localCourse = buildCourseEntity(id = "old-course")

        coEvery { fakeCourseNetwork.getAllCourses("user-123") } returns emptyList()
        coEvery { fakeScheduleNetwork.getAllSchedules("user-123") } returns emptyList()
        every { fakeCourseRepo.getAllCourseEntity("user-123") } returns flowOf(listOf(localCourse))
        every { fakeScheduleRepo.getAllScheduleEntity("user-123") } returns flowOf(emptyList())
        coEvery { fakeCourseRepo.deleteCourseEntity("old-course") } just Runs
        coEvery { fakeScheduleRepo.scheduleAllUserAlarms("user-123") } just Runs

        sut.startSynchronization("user-123")

        coVerify(exactly = 1) { fakeCourseRepo.deleteCourseEntity("old-course") }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun buildNetworkCourse(
        id: String = "course-id",
        lastModified: LocalDateTime = LocalDateTime.now(),
    ) = com.studentsapps.network.model.NetworkCourse(
        id = id,
        name = "Test Course",
        nameProfessor = "Prof",
        color = 0xFF0000,
        lastModified = lastModified,
        userId = "user-123",
    )

    private fun buildCourseEntity(
        id: String = "course-id",
        lastModified: LocalDateTime = LocalDateTime.now(),
    ) = CourseEntity(
        id = id,
        name = "Test Course",
        nameProfessor = "Prof",
        color = 0xFF0000,
        lastModified = lastModified,
        userId = "user-123",
    )
}
