package com.awarelytics.app.ml;

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
public final class FeatureAggregator_Factory implements Factory<FeatureAggregator> {
  private final Provider<AwarelyticsRepository> repositoryProvider;

  public FeatureAggregator_Factory(Provider<AwarelyticsRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public FeatureAggregator get() {
    return newInstance(repositoryProvider.get());
  }

  public static FeatureAggregator_Factory create(
      Provider<AwarelyticsRepository> repositoryProvider) {
    return new FeatureAggregator_Factory(repositoryProvider);
  }

  public static FeatureAggregator newInstance(AwarelyticsRepository repository) {
    return new FeatureAggregator(repository);
  }
}
