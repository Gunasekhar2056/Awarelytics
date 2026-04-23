package com.awarelytics.app.data.repository;

import com.awarelytics.app.data.local.DriftEventDao;
import com.awarelytics.app.data.local.TelemetryDao;
import com.awarelytics.app.data.remote.FirebaseAuthManager;
import com.awarelytics.app.data.remote.FirestoreSyncManager;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class AwarelyticsRepository_Factory implements Factory<AwarelyticsRepository> {
  private final Provider<TelemetryDao> telemetryDaoProvider;

  private final Provider<DriftEventDao> driftEventDaoProvider;

  private final Provider<FirebaseAuthManager> authManagerProvider;

  private final Provider<FirestoreSyncManager> firestoreSyncProvider;

  public AwarelyticsRepository_Factory(Provider<TelemetryDao> telemetryDaoProvider,
      Provider<DriftEventDao> driftEventDaoProvider,
      Provider<FirebaseAuthManager> authManagerProvider,
      Provider<FirestoreSyncManager> firestoreSyncProvider) {
    this.telemetryDaoProvider = telemetryDaoProvider;
    this.driftEventDaoProvider = driftEventDaoProvider;
    this.authManagerProvider = authManagerProvider;
    this.firestoreSyncProvider = firestoreSyncProvider;
  }

  @Override
  public AwarelyticsRepository get() {
    return newInstance(telemetryDaoProvider.get(), driftEventDaoProvider.get(), authManagerProvider.get(), firestoreSyncProvider.get());
  }

  public static AwarelyticsRepository_Factory create(Provider<TelemetryDao> telemetryDaoProvider,
      Provider<DriftEventDao> driftEventDaoProvider,
      Provider<FirebaseAuthManager> authManagerProvider,
      Provider<FirestoreSyncManager> firestoreSyncProvider) {
    return new AwarelyticsRepository_Factory(telemetryDaoProvider, driftEventDaoProvider, authManagerProvider, firestoreSyncProvider);
  }

  public static AwarelyticsRepository newInstance(TelemetryDao telemetryDao,
      DriftEventDao driftEventDao, FirebaseAuthManager authManager,
      FirestoreSyncManager firestoreSync) {
    return new AwarelyticsRepository(telemetryDao, driftEventDao, authManager, firestoreSync);
  }
}
