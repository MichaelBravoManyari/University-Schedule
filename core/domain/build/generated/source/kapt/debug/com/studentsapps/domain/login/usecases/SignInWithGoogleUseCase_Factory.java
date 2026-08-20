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
public final class SignInWithGoogleUseCase_Factory implements Factory<SignInWithGoogleUseCase> {
  private final Provider<AuthRepository> authRepositoryProvider;

  public SignInWithGoogleUseCase_Factory(Provider<AuthRepository> authRepositoryProvider) {
    this.authRepositoryProvider = authRepositoryProvider;
  }

  @Override
  public SignInWithGoogleUseCase get() {
    return newInstance(authRepositoryProvider.get());
  }

  public static SignInWithGoogleUseCase_Factory create(
      Provider<AuthRepository> authRepositoryProvider) {
    return new SignInWithGoogleUseCase_Factory(authRepositoryProvider);
  }

  public static SignInWithGoogleUseCase newInstance(AuthRepository authRepository) {
    return new SignInWithGoogleUseCase(authRepository);
  }
}
