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
public final class SignInWithEmailUseCase_Factory implements Factory<SignInWithEmailUseCase> {
  private final Provider<AuthRepository> authRepositoryProvider;

  public SignInWithEmailUseCase_Factory(Provider<AuthRepository> authRepositoryProvider) {
    this.authRepositoryProvider = authRepositoryProvider;
  }

  @Override
  public SignInWithEmailUseCase get() {
    return newInstance(authRepositoryProvider.get());
  }

  public static SignInWithEmailUseCase_Factory create(
      Provider<AuthRepository> authRepositoryProvider) {
    return new SignInWithEmailUseCase_Factory(authRepositoryProvider);
  }

  public static SignInWithEmailUseCase newInstance(AuthRepository authRepository) {
    return new SignInWithEmailUseCase(authRepository);
  }
}
