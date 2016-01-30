package com.piemanpete.go.plugin.artifactorypackage;

import static org.assertj.core.api.Assertions.assertThat;

import com.piemanpete.go.plugin.artifactorypackage.message.CheckConnectionResultMessage;
import com.piemanpete.go.plugin.artifactorypackage.message.PackageMaterialProperties;
import com.piemanpete.go.plugin.artifactorypackage.message.PackageMaterialProperty;
import com.piemanpete.go.plugin.artifactorypackage.message.PackageRevisionMessage;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class PackageRepositoryPollerIntegrationTest {
    private PackageRepositoryPoller underTest;

    private PackageMaterialProperties repositoryConfiguration = new PackageMaterialProperties();
    private String artifactoryHost;

    @Before
    public void setup() {
        artifactoryHost = System.getProperty("artifactory.host", "http://127.0.0.1:8081");
        underTest = new PackageRepositoryPoller(new PackageRepositoryConfigurationProvider());

        repositoryConfiguration.addPackageMaterialProperty(Constants.REPO_URL, material(artifactoryHost + "/artifactory"));
        repositoryConfiguration.addPackageMaterialProperty(Constants.USERNAME, material("admin"));
        repositoryConfiguration.addPackageMaterialProperty(Constants.PASSWORD, material("password"));
    }

    @Test
    public void canCheckSnapshotPackage() {
        PackageMaterialProperties packageMaterialProperties = new PackageMaterialProperties();
        packageMaterialProperties.addPackageMaterialProperty(PackageRepositoryConfigurationProvider.PACKAGE_REPO_ID, material("ext-snapshot-local"));
        packageMaterialProperties.addPackageMaterialProperty(PackageRepositoryConfigurationProvider.PACKAGE_GROUP_ID, material("notification-gateway"));
        packageMaterialProperties.addPackageMaterialProperty(PackageRepositoryConfigurationProvider.PACKAGE_ARTIFACT_ID, material("notification-gateway"));
        packageMaterialProperties.addPackageMaterialProperty(PackageRepositoryConfigurationProvider.PACKAGE_CLASSIFIER_ID, material("jar"));

        CheckConnectionResultMessage checkConnectionResultMessage = underTest.checkConnectionToPackage(packageMaterialProperties, repositoryConfiguration);

        assertThat(checkConnectionResultMessage.success()).describedAs(checkConnectionResultMessage.getMessages().toString()).isTrue();
        assertThat(checkConnectionResultMessage.getMessages()).containsOnly("found 0.2.24.4-SNAPSHOT");
    }

    @Test
    public void canGetSnapshotPackage() {
        PackageMaterialProperties packageMaterialProperties = new PackageMaterialProperties();
        packageMaterialProperties.addPackageMaterialProperty(PackageRepositoryConfigurationProvider.PACKAGE_REPO_ID, material("ext-snapshot-local"));
        packageMaterialProperties.addPackageMaterialProperty(PackageRepositoryConfigurationProvider.PACKAGE_GROUP_ID, material("a.groupId"));
        packageMaterialProperties.addPackageMaterialProperty(PackageRepositoryConfigurationProvider.PACKAGE_ARTIFACT_ID, material("artifactId"));
        packageMaterialProperties.addPackageMaterialProperty(PackageRepositoryConfigurationProvider.PACKAGE_CLASSIFIER_ID, material("jar"));

        PackageRevisionMessage latestRevision = underTest.getLatestRevision(packageMaterialProperties, repositoryConfiguration);
        assertThat(latestRevision).isNotNull();
        assertThat(latestRevision.getTrackbackUrl()).matches(artifactoryHost + "/artifactory/ext-snapshot-local/a/groupId/artifactId/1.0-SNAPSHOT/artifactId-1.0-(.*)-3.jar");
    }

    @Test
    public void canCheckLibReleasePackage() {
        PackageMaterialProperties packageMaterialProperties = new PackageMaterialProperties();
        packageMaterialProperties.addPackageMaterialProperty(PackageRepositoryConfigurationProvider.PACKAGE_REPO_ID, material("libs-release"));
        packageMaterialProperties.addPackageMaterialProperty(PackageRepositoryConfigurationProvider.PACKAGE_GROUP_ID, material("commons-io"));
        packageMaterialProperties.addPackageMaterialProperty(PackageRepositoryConfigurationProvider.PACKAGE_ARTIFACT_ID, material("commons-io"));
//        packageMaterialProperties.addPackageMaterialProperty(PackageRepositoryConfigurationProvider.PACKAGE_CLASSIFIER_ID, material("jar"));

        CheckConnectionResultMessage checkConnectionResultMessage = underTest.checkConnectionToPackage(packageMaterialProperties, repositoryConfiguration);

        assertThat(checkConnectionResultMessage.success()).describedAs(checkConnectionResultMessage.getMessages().toString()).isTrue();
        assertThat(checkConnectionResultMessage.getMessages()).containsOnly("found 2.4");
    }

    @Test
    public void canCheckLibReleaseSourcePackage() {
        PackageMaterialProperties packageMaterialProperties = new PackageMaterialProperties();
        packageMaterialProperties.addPackageMaterialProperty(PackageRepositoryConfigurationProvider.PACKAGE_REPO_ID, material("libs-release"));
        packageMaterialProperties.addPackageMaterialProperty(PackageRepositoryConfigurationProvider.PACKAGE_GROUP_ID, material("commons-io"));
        packageMaterialProperties.addPackageMaterialProperty(PackageRepositoryConfigurationProvider.PACKAGE_ARTIFACT_ID, material("commons-io"));
        packageMaterialProperties.addPackageMaterialProperty(PackageRepositoryConfigurationProvider.PACKAGE_CLASSIFIER_ID, material("sources"));

        CheckConnectionResultMessage checkConnectionResultMessage = underTest.checkConnectionToPackage(packageMaterialProperties, repositoryConfiguration);

        assertThat(checkConnectionResultMessage.success()).describedAs(checkConnectionResultMessage.getMessages().toString()).isTrue();
        assertThat(checkConnectionResultMessage.getMessages()).containsOnly("found 2.4");
    }


    private PackageMaterialProperty material(String value) {
        return new PackageMaterialProperty().withValue(value);
    }

}