package com.awarelytics.app.ai;

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
public final class GeminiCoach_Factory implements Factory<GeminiCoach> {
  private final Provider<WeeklySummaryAggregator> weeklySummaryAggregatorProvider;

  public GeminiCoach_Factory(Provider<WeeklySummaryAggregator> weeklySummaryAggregatorProvider) {
    this.weeklySummaryAggregatorProvider = weeklySummaryAggregatorProvider;
  }

  @Override
  public GeminiCoach get() {
    return newInstance(weeklySummaryAggregatorProvider.get());
  }

  public static GeminiCoach_Factory create(
      Provider<WeeklySummaryAggregator> weeklySummaryAggregatorProvider) {
    return new GeminiCoach_Factory(weeklySummaryAggregatorProvider);
  }

  public static GeminiCoach newInstance(WeeklySummaryAggregator weeklySummaryAggregator) {
    return new GeminiCoach(weeklySummaryAggregator);
  }
}
