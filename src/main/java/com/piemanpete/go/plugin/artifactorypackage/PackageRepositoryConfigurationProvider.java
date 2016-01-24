/*************************GO-LICENSE-START*********************************
 * Copyright 2014 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *************************GO-LICENSE-END***********************************/
package com.piemanpete.go.plugin.artifactorypackage;


import com.piemanpete.go.plugin.artifactorypackage.message.PackageMaterialProperties;
import com.piemanpete.go.plugin.artifactorypackage.message.PackageMaterialProperty;
import com.piemanpete.go.plugin.artifactorypackage.message.ValidationResultMessage;


public class PackageRepositoryConfigurationProvider {
    public static final String PACKAGE_GROUP_ID = "GROUP_ID";
    public static final String PACKAGE_ARTIFACT_ID = "ARTIFACT_ID";
    public static final String PACKAGE_CLASSIFIER_ID = "CLASSIFIER_ID";
    public static final String PACKAGE_REPO_ID = "REPO_ID";
    public static final String PACKAGE_LOCATION = "LOCATION";

    public PackageMaterialProperties repositoryConfiguration() {
        PackageMaterialProperties repositoryConfigurationResponse = new PackageMaterialProperties();
        repositoryConfigurationResponse.addPackageMaterialProperty(Constants.REPO_URL, url());
        repositoryConfigurationResponse.addPackageMaterialProperty(Constants.USERNAME, username());
        repositoryConfigurationResponse.addPackageMaterialProperty(Constants.PASSWORD, password());
        return repositoryConfigurationResponse;
    }

    public PackageMaterialProperties packageConfiguration() {
        PackageMaterialProperties packageConfigurationResponse = new PackageMaterialProperties();

        packageConfigurationResponse.addPackageMaterialProperty(PACKAGE_REPO_ID,
                new PackageMaterialProperty()
                        .withDisplayName("Repository ID")
                        .withDisplayOrder("0")
                        .withRequired(true)
                        .withValue("ext-libs-release")
                        .withPartOfIdentity(true));

        packageConfigurationResponse.addPackageMaterialProperty(PACKAGE_GROUP_ID,
                new PackageMaterialProperty()
                        .withDisplayName("Group ID")
                        .withDisplayOrder("1")
                        .withRequired(true)
                        .withPartOfIdentity(true));

        packageConfigurationResponse.addPackageMaterialProperty(PACKAGE_ARTIFACT_ID,
                new PackageMaterialProperty()
                        .withDisplayName("Artifact ID")
                        .withDisplayOrder("2")
                        .withRequired(true)
                        .withPartOfIdentity(true));

        packageConfigurationResponse.addPackageMaterialProperty(PACKAGE_CLASSIFIER_ID,
                new PackageMaterialProperty()
                        .withDisplayName("Classifier")
                        .withDisplayOrder("3")
                        .withRequired(false)
                        .withPartOfIdentity(true)
                        .withValue("jar"));

        return packageConfigurationResponse;
    }

    public ValidationResultMessage validateRepositoryConfiguration(PackageMaterialProperties configurationProvidedByUser) {
        ValidationResultMessage validationResultMessage = new ValidationResultMessage();
        //validate configurationProvidedByUser and populate validationResultMessage
        return validationResultMessage;
    }

    public ValidationResultMessage validatePackageConfiguration(PackageMaterialProperties configurationProvidedByUser) {
        ValidationResultMessage validationResultMessage = new ValidationResultMessage();
        //validate configurationProvidedByUser and populate validationResultMessage
        return validationResultMessage;
    }

    private PackageMaterialProperty password() {
        return new PackageMaterialProperty().
                withRequired(false).
                withPartOfIdentity(false).
                withSecure(true).
                withDisplayName("Password").
                withDisplayOrder("2");
    }

    private PackageMaterialProperty username() {
        return new PackageMaterialProperty().
                withRequired(false).
                withPartOfIdentity(false).
                withDisplayName("User").
                withDisplayOrder("1");
    }

    private PackageMaterialProperty url() {
        return new PackageMaterialProperty().withDisplayName("Repository URL").withDisplayOrder("0");
    }
}
