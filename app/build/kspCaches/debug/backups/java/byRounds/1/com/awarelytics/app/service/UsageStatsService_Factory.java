package com.awarelytics.app.service;

import android.content.Context;
import com.awarelytics.app.data.repository.AwarelyticsRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class UsageStatsService_Factory implements Factory<UsageStatsService> {
  private final Provider<Context> contextProvider;

  private final Provider<AwarelyticsRepository> repositoryProvider;

  public UsageStatsService_Factory(Provider<Context> contextProvider,
      Provider<AwarelyticsRepository> repositoryProvider) {
    this.contextProvider = contextProvider;
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public UsageStatsService get() {
    return newInstance(contextProvider.get(), repositoryProvider.get());
  }

  public static UsageStatsService_Factory create(Provider<Context> contextProvider,
      Provider<AwarelyticsRepository> repositoryProvider) {
    return new UsageStatsService_Factory(contextProvider, repositoryProvider);
  }

  public static UsageStatsService newInstance(Context context, AwarelyticsRepository repository) {
    return new UsageStatsService(context, repository);
  }
}
