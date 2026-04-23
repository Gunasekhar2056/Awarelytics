package com.awarelytics.app.di;

import com.awarelytics.app.data.remote.FirebaseAuthManager;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
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
    "KotlinInternalInJava"
})
public final class DatabaseModule_ProvideFirebaseAuthManagerFactory implements Factory<FirebaseAuthManager> {
  @Override
  public FirebaseAuthManager get() {
    return provideFirebaseAuthManager();
  }

  public static DatabaseModule_ProvideFirebaseAuthManagerFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static FirebaseAuthManager provideFirebaseAuthManager() {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideFirebaseAuthManager());
  }

  private static final class InstanceHolder {
    private static final DatabaseModule_ProvideFirebaseAuthManagerFactory INSTANCE = new DatabaseModule_ProvideFirebaseAuthManagerFactory();
  }
}
