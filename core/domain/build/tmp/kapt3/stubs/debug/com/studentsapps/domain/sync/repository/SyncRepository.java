package com.studentsapps.domain.sync.repository;

/**
 * Repository interface for data-synchronization operations.
 *
 * The implementation lives in the data layer ([com.studentsapps.data.sync.SyncRepositoryImpl]).
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0002\bf\u0018\u00002\u00020\u0001J\u000e\u0010\u0002\u001a\u00020\u0003H\u00a6@\u00a2\u0006\u0002\u0010\u0004J\u0016\u0010\u0005\u001a\u00020\u00032\u0006\u0010\u0006\u001a\u00020\u0007H\u00a6@\u00a2\u0006\u0002\u0010\b\u00a8\u0006\t"}, d2 = {"Lcom/studentsapps/domain/sync/repository/SyncRepository;", "", "startSyncIfNeeded", "", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "startSynchronization", "userId", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "domain_debug"})
public abstract interface SyncRepository {
    
    /**
     * Starts a full cloud→local synchronization only when all pre-conditions
     * are met:
     * - A Firebase user is authenticated.
     * - The device has a validated internet connection.
     * - There are no pending local write operations still awaiting upload.
     *
     * Safe to call from any coroutine scope; suspends until the sync completes
     * or a pre-condition is not met (in which case it returns immediately).
     */
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object startSyncIfNeeded(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    /**
     * Runs the full bidirectional sync for the given [userId]:
     * 1. Fetches all remote courses and schedules from Firestore.
     * 2. Compares each remote record with its local counterpart by [lastModified].
     * 3. Inserts or updates local records that are behind the remote version.
     * 4. Deletes local records that no longer exist remotely.
     * 5. Re-schedules all device alarms to reflect the updated local data.
     *
     * Callers that need finer control (e.g. a background Worker) can invoke
     * this directly, bypassing the pre-condition checks of [startSyncIfNeeded].
     */
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object startSynchronization(@org.jetbrains.annotations.NotNull()
    java.lang.String userId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
}