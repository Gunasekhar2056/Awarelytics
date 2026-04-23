package com.awarelytics.app.ai;

import com.awarelytics.app.data.repository.AwarelyticsRepository;
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
public final class WeeklySummaryAggregator_Factory implements Factory<WeeklySummaryAggregator> {
  private final Provider<AwarelyticsRepository> repositoryProvider;

  public WeeklySummaryAggregator_Factory(Provider<AwarelyticsRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public WeeklySummaryAggregator get() {
    return newInstance(repositoryProvider.get());
  }

  public static WeeklySummaryAggregator_Factory create(
      Provider<AwarelyticsRepository> repositoryProvider) {
    return new WeeklySummaryAggregator_Factory(repositoryProvider);
  }

  public static WeeklySummaryAggregator newInstance(AwarelyticsRepository repository) {
    return new WeeklySummaryAggregator(repository);
  }
}
