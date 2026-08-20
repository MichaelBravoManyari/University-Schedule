package com.studentsapps.domain.sync.usecases;

import com.studentsapps.domain.sync.repository.SyncRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast"
})
public final class StartSyncIfNeededUseCase_Factory implements Factory<StartSyncIfNeededUseCase> {
  private final Provider<SyncRepository> syncRepositoryProvider;

  public StartSyncIfNeededUseCase_Factory(Provider<SyncRepository> syncRepositoryProvider) {
    this.syncRepositoryProvider = syncRepositoryProvider;
  }

  @Override
  public StartSyncIfNeededUseCase get() {
    return newInstance(syncRepositoryProvider.get());
  }

  public static StartSyncIfNeededUseCase_Factory create(
      Provider<SyncRepository> syncRepositoryProvider) {
    return new StartSyncIfNeededUseCase_Factory(syncRepositoryProvider);
  }

  public static StartSyncIfNeededUseCase newInstance(SyncRepository syncRepository) {
    return new StartSyncIfNeededUseCase(syncRepository);
  }
}
