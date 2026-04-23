package com.awarelytics.app.di;

import com.awarelytics.app.data.remote.FirestoreSyncManager;
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
public final class DatabaseModule_ProvideFirestoreSyncManagerFactory implements Factory<FirestoreSyncManager> {
  @Override
  public FirestoreSyncManager get() {
    return provideFirestoreSyncManager();
  }

  public static DatabaseModule_ProvideFirestoreSyncManagerFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static FirestoreSyncManager provideFirestoreSyncManager() {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideFirestoreSyncManager());
  }

  private static final class InstanceHolder {
    private static final DatabaseModule_ProvideFirestoreSyncManagerFactory INSTANCE = new DatabaseModule_ProvideFirestoreSyncManagerFactory();
  }
}
