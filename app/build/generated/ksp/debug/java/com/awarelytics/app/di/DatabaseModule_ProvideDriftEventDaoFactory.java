package com.awarelytics.app.di;

import com.awarelytics.app.data.local.AwarelyticsDatabase;
import com.awarelytics.app.data.local.DriftEventDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
    "KotlinInternalInJava"
})
public final class DatabaseModule_ProvideDriftEventDaoFactory implements Factory<DriftEventDao> {
  private final Provider<AwarelyticsDatabase> databaseProvider;

  public DatabaseModule_ProvideDriftEventDaoFactory(
      Provider<AwarelyticsDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public DriftEventDao get() {
    return provideDriftEventDao(databaseProvider.get());
  }

  public static DatabaseModule_ProvideDriftEventDaoFactory create(
      Provider<AwarelyticsDatabase> databaseProvider) {
    return new DatabaseModule_ProvideDriftEventDaoFactory(databaseProvider);
  }

  public static DriftEventDao provideDriftEventDao(AwarelyticsDatabase database) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideDriftEventDao(database));
  }
}
