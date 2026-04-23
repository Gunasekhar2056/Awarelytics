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
public final class FirebaseAuthManager_Factory implements Factory<FirebaseAuthManager> {
  @Override
  public FirebaseAuthManager get() {
    return newInstance();
  }

  public static FirebaseAuthManager_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static FirebaseAuthManager newInstance() {
    return new FirebaseAuthManager();
  }

  private static final class InstanceHolder {
    private static final FirebaseAuthManager_Factory INSTANCE = new FirebaseAuthManager_Factory();
  }
}
