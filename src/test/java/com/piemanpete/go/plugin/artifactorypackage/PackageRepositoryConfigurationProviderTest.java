package com.piemanpete.go.plugin.artifactorypackage;

import static org.assertj.core.api.Assertions.assertThat;

import com.piemanpete.go.plugin.artifactorypackage.message.PackageMaterialProperties;
import org.junit.Before;
import org.junit.Test;

public class PackageRepositoryConfigurationProviderTest {
    PackageRepositoryConfigurationProvider underTest;

    @Before
    public void setup() {
        underTest = new PackageRepositoryConfigurationProvider();
    }

    @Test
    public void testValidatePackageConfiguration() throws Exception {
        PackageMaterialProperties packageMaterialProperties = underTest.packageConfiguration();

        assertThat(packageMaterialProperties).isNotNull();
    }
}