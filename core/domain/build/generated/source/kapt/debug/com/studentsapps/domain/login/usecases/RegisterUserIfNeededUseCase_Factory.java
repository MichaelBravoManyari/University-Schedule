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
public final class RegisterUserIfNeededUseCase_Factory implements Factory<RegisterUserIfNeededUseCase> {
  private final Provider<AuthRepository> authRepositoryProvider;

  public RegisterUserIfNeededUseCase_Factory(Provider<AuthRepository> authRepositoryProvider) {
    this.authRepositoryProvider = authRepositoryProvider;
  }

  @Override
  public RegisterUserIfNeededUseCase get() {
    return newInstance(authRepositoryProvider.get());
  }

  public static RegisterUserIfNeededUseCase_Factory create(
      Provider<AuthRepository> authRepositoryProvider) {
    return new RegisterUserIfNeededUseCase_Factory(authRepositoryProvider);
  }

  public static RegisterUserIfNeededUseCase newInstance(AuthRepository authRepository) {
    return new RegisterUserIfNeededUseCase(authRepository);
  }
}
