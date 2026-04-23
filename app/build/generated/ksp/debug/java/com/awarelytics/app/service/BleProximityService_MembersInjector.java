package com.awarelytics.app.service;

import com.awarelytics.app.data.repository.AwarelyticsRepository;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class BleProximityService_MembersInjector implements MembersInjector<BleProximityService> {
  private final Provider<AwarelyticsRepository> repositoryProvider;

  public BleProximityService_MembersInjector(Provider<AwarelyticsRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  public static MembersInjector<BleProximityService> create(
      Provider<AwarelyticsRepository> repositoryProvider) {
    return new BleProximityService_MembersInjector(repositoryProvider);
  }

  @Override
  public void injectMembers(BleProximityService instance) {
    injectRepository(instance, repositoryProvider.get());
  }

  @InjectedFieldSignature("com.awarelytics.app.service.BleProximityService.repository")
  public static void injectRepository(BleProximityService instance,
      AwarelyticsRepository repository) {
    instance.repository = repository;
  }
}
