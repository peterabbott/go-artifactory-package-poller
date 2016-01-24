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

import static com.google.common.collect.Lists.newArrayList;
import static com.piemanpete.go.plugin.artifactorypackage.message.CheckConnectionResultMessage.STATUS.FAILURE;
import static com.piemanpete.go.plugin.artifactorypackage.message.CheckConnectionResultMessage.STATUS.SUCCESS;

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.piemanpete.go.plugin.artifactorypackage.message.CheckConnectionResultMessage;
import com.piemanpete.go.plugin.artifactorypackage.message.PackageMaterialProperties;
import com.piemanpete.go.plugin.artifactorypackage.message.PackageRevisionMessage;
import com.thoughtworks.go.plugin.api.logging.Logger;
import org.jfrog.artifactory.client.Artifactory;
import org.jfrog.artifactory.client.ArtifactoryClient;
import org.jfrog.artifactory.client.ItemHandle;
import org.jfrog.artifactory.client.model.Item;
import org.jfrog.artifactory.client.model.impl.FolderImpl;

public class PackageRepositoryPoller {
    private static final Logger LOG = Logger.getLoggerFor(PackageRepositoryPoller.class);

    private PackageRepositoryConfigurationProvider configurationProvider;

    public PackageRepositoryPoller(PackageRepositoryConfigurationProvider configurationProvider) {
        this.configurationProvider = configurationProvider;
    }

    public CheckConnectionResultMessage checkConnectionToRepository(PackageMaterialProperties repositoryConfiguration) {
        Artifactory artifactory = null;
        try {
            artifactory = getArtifactoryClient(repositoryConfiguration);

            if (artifactory.system().ping()) {
                return new CheckConnectionResultMessage(SUCCESS, Collections.singletonList("Success!"));
            }
        } catch (Exception ex) {
            LOG.error("error checking connection", ex);
        } finally {
            if (artifactory != null ) {
                artifactory.close();
            }
        }

        return new CheckConnectionResultMessage(FAILURE, Collections.singletonList("Unable to connect!"));
    }

    public CheckConnectionResultMessage checkConnectionToPackage(final PackageMaterialProperties packageConfiguration, final PackageMaterialProperties repositoryConfiguration) {
        final Artifactory artifactory = Preconditions.checkNotNull(getArtifactoryClient(repositoryConfiguration), "no reference to artifactory");
        try {
            if (!artifactory.system().ping()) {
                return new CheckConnectionResultMessage(FAILURE, Collections.singletonList("no connection"));
            }

            final String repoId = getRequiredPropertyReferenceValue(PackageRepositoryConfigurationProvider.PACKAGE_REPO_ID, packageConfiguration);
            final String groupId = getRequiredPropertyReferenceValue(PackageRepositoryConfigurationProvider.PACKAGE_GROUP_ID, packageConfiguration);
            final String artifactId = getRequiredPropertyReferenceValue(PackageRepositoryConfigurationProvider.PACKAGE_ARTIFACT_ID, packageConfiguration);
            final String classifier = getOptionalPropertyReferenceValue(PackageRepositoryConfigurationProvider.PACKAGE_CLASSIFIER_ID, packageConfiguration);

            ArtifactVersion artifact = ArtifactoryHelper.findLatestVersion(artifactory, repoId, groupId, artifactId, classifier);
            if (artifact != null) {
                return new CheckConnectionResultMessage(SUCCESS, Collections.singletonList("found " + artifact.getVersion()));
            }
        } catch (Exception ex) {
            LOG.error("error checking package", ex);

            return new CheckConnectionResultMessage(FAILURE, Collections.singletonList("error checking version " + ex.getMessage()));
        } finally {
            if (artifactory != null) artifactory.close();
        }

        return new CheckConnectionResultMessage(FAILURE, Collections.singletonList("nothing found"));
    }

    public PackageRevisionMessage getLatestRevision(PackageMaterialProperties packageConfiguration, PackageMaterialProperties repositoryConfiguration) {
        final Artifactory artifactory = Preconditions.checkNotNull(getArtifactoryClient(repositoryConfiguration), "no reference to artifactory");
        try {
            if (!artifactory.system().ping()) {
                return null;

            }

            final String repoId = getRequiredPropertyReferenceValue(PackageRepositoryConfigurationProvider.PACKAGE_REPO_ID, packageConfiguration);
            final String groupId = getRequiredPropertyReferenceValue(PackageRepositoryConfigurationProvider.PACKAGE_GROUP_ID, packageConfiguration);
            final String artifactId = getRequiredPropertyReferenceValue(PackageRepositoryConfigurationProvider.PACKAGE_ARTIFACT_ID, packageConfiguration);
            final String classifier = getOptionalPropertyReferenceValue(PackageRepositoryConfigurationProvider.PACKAGE_CLASSIFIER_ID, packageConfiguration);

            ArtifactVersion artifactVersion = ArtifactoryHelper.findLatestVersion(artifactory, repoId, groupId, artifactId, classifier);
            if (artifactVersion != null) {
                String location = artifactory.getUri() + "/" + artifactory.getContextName() + "/" + artifactVersion.getItem().getRepo() + artifactVersion.getItem().getPath();
                PackageRevisionMessage packageRevision = new PackageRevisionMessage(
                        artifactVersion.getVersion(),
                        artifactVersion.getItem().getLastModified(),
                        artifactVersion.getItem().getModifiedBy(),
                        null,
                        location
                        );

                packageRevision.addData(PackageRepositoryConfigurationProvider.PACKAGE_LOCATION, location);

                return packageRevision;
            }
        } catch (Exception ex) {
            LOG.error("error checking package", ex);

            return new PackageRevisionMessage();
        } finally {
            if (artifactory != null) artifactory.close();
        }

        return new PackageRevisionMessage();
    }

    public PackageRevisionMessage getLatestRevisionSince(PackageMaterialProperties packageConfiguration, PackageMaterialProperties repositoryConfiguration, PackageRevisionMessage previousPackageRevision) {
//        return new PackageRevisionMessage();
        return getLatestRevision(packageConfiguration, repositoryConfiguration);
    }

    private Artifactory getArtifactoryClient(PackageMaterialProperties repositoryConfiguration) {
        return ArtifactoryClient.create(
                getRequiredPropertyReferenceValue(Constants.REPO_URL, repositoryConfiguration),
                getRequiredPropertyReferenceValue(Constants.USERNAME, repositoryConfiguration),
                getRequiredPropertyReferenceValue(Constants.PASSWORD, repositoryConfiguration)
        );
    }

    private String getRequiredPropertyReferenceValue(String id, PackageMaterialProperties props) {
        return Preconditions.checkNotNull(props.getProperty(id), "missing " + id).value();
    }

    private String getOptionalPropertyReferenceValue(String id, PackageMaterialProperties packageConfiguration) {
        if (packageConfiguration.hasKey(id)) {
            return packageConfiguration.getProperty(id).value();
        }

        return null;
    }

    static class ArtifactoryHelper {
        public static List<Item> find(final Artifactory artifactory, final String repoId, final String rootPath, final String classifier) {
            Item root = artifactory.repository(repoId).folder(rootPath).info();

            List<Item> result = newArrayList(root);
            for (Item item : MoreObjects.firstNonNull( ((FolderImpl) root).getChildren(), Lists.<Item>newArrayList())) {
                result.addAll(find(artifactory, repoId, rootPath + "/" + item.getUri(), classifier));
            }

            Iterable iterable = Iterables.filter(result, new Predicate<Item>() {
                @Override
                public boolean apply(Item input) {
                    return matchesClassifier(input.getPath());
                }

                private boolean matchesClassifier(String path) {
                    String regex = String.format("/(.*)/(.*)/(.*)/(.*)%s.jar", "-" + classifier);

                    if (Strings.isNullOrEmpty(classifier) || "jar".equals(classifier) ) {
                        regex = "/(.*)/(.*)/(.*)/(.*).jar";
                    }

                    return Pattern.compile(regex).matcher(path).matches();
                }
            });

            return Lists.newArrayList(iterable);
        }

        public static ArtifactVersion findLatestVersion(Artifactory artifactory, String repoId, String groupId, String artifactId, String classifier) {
            Iterable<Item> filtered = find(artifactory, repoId, groupId + "/" + artifactId, classifier);

            if (!Iterables.isEmpty(filtered)) {
                Item last = Iterables.getLast(filtered);
                ItemHandle matched = artifactory.repository(last.getRepo()).file(last.getPath());

                String identifier = String.format("/%s/%s/(.*)/(.*)", groupId, artifactId);

                Matcher matcher = Pattern.compile(identifier).matcher(matched.info().getPath());
                if (matcher.find()){
                    return new ArtifactVersion(matcher.group(1), last);
                }
            }

            return null;
        }
    }

    static class ArtifactVersion {
        private final String version;
        private final Item item;

        ArtifactVersion(String version, Item item) {
            this.version = version;
            this.item = item;
        }

        public String getVersion() {
            return version;
        }

        public Item getItem() {
            return item;
        }
    }

}
