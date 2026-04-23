package com.awarelytics.app.nudge;

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
public final class NudgeManager_Factory implements Factory<NudgeManager> {
  private final Provider<Context> contextProvider;

  private final Provider<AwarelyticsRepository> repositoryProvider;

  public NudgeManager_Factory(Provider<Context> contextProvider,
      Provider<AwarelyticsRepository> repositoryProvider) {
    this.contextProvider = contextProvider;
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public NudgeManager get() {
    return newInstance(contextProvider.get(), repositoryProvider.get());
  }

  public static NudgeManager_Factory create(Provider<Context> contextProvider,
      Provider<AwarelyticsRepository> repositoryProvider) {
    return new NudgeManager_Factory(contextProvider, repositoryProvider);
  }

  public static NudgeManager newInstance(Context context, AwarelyticsRepository repository) {
    return new NudgeManager(context, repository);
  }
}
