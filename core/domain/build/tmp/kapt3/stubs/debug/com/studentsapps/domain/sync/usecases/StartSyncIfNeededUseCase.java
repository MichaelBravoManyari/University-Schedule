package com.studentsapps.domain.sync.usecases;

/**
 * Use case that triggers a cloud→local synchronization when all pre-conditions
 * are satisfied.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u000e\u0010\u0005\u001a\u00020\u0006H\u0086B\u00a2\u0006\u0002\u0010\u0007R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\b"}, d2 = {"Lcom/studentsapps/domain/sync/usecases/StartSyncIfNeededUseCase;", "", "syncRepository", "Lcom/studentsapps/domain/sync/repository/SyncRepository;", "(Lcom/studentsapps/domain/sync/repository/SyncRepository;)V", "invoke", "", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "domain_debug"})
public final class StartSyncIfNeededUseCase {
    @org.jetbrains.annotations.NotNull()
    private final com.studentsapps.domain.sync.repository.SyncRepository syncRepository = null;
    
    @javax.inject.Inject()
    public StartSyncIfNeededUseCase(@org.jetbrains.annotations.NotNull()
    com.studentsapps.domain.sync.repository.SyncRepository syncRepository) {
        super();
    }
    
    /**
     * Delegates to [SyncRepository.startSyncIfNeeded].
     *
     * Suspends until the sync completes or a pre-condition is not met.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object invoke(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
}