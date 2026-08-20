package com.studentsapps.domain.login.usecases;

import com.studentsapps.domain.login.repository.AuthRepository;
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
public final class SignUpWithEmailUseCase_Factory implements Factory<SignUpWithEmailUseCase> {
  private final Provider<AuthRepository> authRepositoryProvider;

  public SignUpWithEmailUseCase_Factory(Provider<AuthRepository> authRepositoryProvider) {
    this.authRepositoryProvider = authRepositoryProvider;
  }

  @Override
  public SignUpWithEmailUseCase get() {
    return newInstance(authRepositoryProvider.get());
  }

  public static SignUpWithEmailUseCase_Factory create(
      Provider<AuthRepository> authRepositoryProvider) {
    return new SignUpWithEmailUseCase_Factory(authRepositoryProvider);
  }

  public static SignUpWithEmailUseCase newInstance(AuthRepository authRepository) {
    return new SignUpWithEmailUseCase(authRepository);
  }
}
