package com.awarelytics.app.data.remote;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class FirestoreSyncManager_Factory implements Factory<FirestoreSyncManager> {
  @Override
  public FirestoreSyncManager get() {
    return newInstance();
  }

  public static FirestoreSyncManager_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static FirestoreSyncManager newInstance() {
    return new FirestoreSyncManager();
  }

  private static final class InstanceHolder {
    private static final FirestoreSyncManager_Factory INSTANCE = new FirestoreSyncManager_Factory();
  }
}
