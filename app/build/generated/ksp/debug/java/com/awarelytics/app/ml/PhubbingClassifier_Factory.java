package com.awarelytics.app.ml;

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
public final class PhubbingClassifier_Factory implements Factory<PhubbingClassifier> {
  private final Provider<Context> contextProvider;

  private final Provider<FeatureAggregator> featureAggregatorProvider;

  private final Provider<AwarelyticsRepository> repositoryProvider;

  public PhubbingClassifier_Factory(Provider<Context> contextProvider,
      Provider<FeatureAggregator> featureAggregatorProvider,
      Provider<AwarelyticsRepository> repositoryProvider) {
    this.contextProvider = contextProvider;
    this.featureAggregatorProvider = featureAggregatorProvider;
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public PhubbingClassifier get() {
    return newInstance(contextProvider.get(), featureAggregatorProvider.get(), repositoryProvider.get());
  }

  public static PhubbingClassifier_Factory create(Provider<Context> contextProvider,
      Provider<FeatureAggregator> featureAggregatorProvider,
      Provider<AwarelyticsRepository> repositoryProvider) {
    return new PhubbingClassifier_Factory(contextProvider, featureAggregatorProvider, repositoryProvider);
  }

  public static PhubbingClassifier newInstance(Context context, FeatureAggregator featureAggregator,
      AwarelyticsRepository repository) {
    return new PhubbingClassifier(context, featureAggregator, repository);
  }
}
