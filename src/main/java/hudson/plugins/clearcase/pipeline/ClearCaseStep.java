package hudson.plugins.clearcase.pipeline;

import hudson.Extension;
import hudson.plugins.clearcase.AbstractClearCaseScm;
import hudson.plugins.clearcase.ClearCaseSCM;
import hudson.plugins.clearcase.viewstorage.DefaultViewStorage;
import hudson.plugins.clearcase.viewstorage.SpecificViewStorage;
import hudson.plugins.clearcase.viewstorage.ViewStorage;
import hudson.plugins.clearcase.viewstorage.ViewStorageFactory;
import hudson.scm.SCM;
import org.jenkinsci.plugins.workflow.steps.scm.SCMStep;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import javax.annotation.Nonnull;

public final class ClearCaseStep extends SCMStep {

    private String configSpec;
    private String viewTag = DescriptorImpl.defaultViewTag;
    private String viewPath = DescriptorImpl.defaultViewPath;
    private String changeset = DescriptorImpl.defaultChangeset;
    private ViewStorage viewStorage = DescriptorImpl.defaultViewStorage;
    private boolean useUpdate = DescriptorImpl.defaultUseUpdate;

    @DataBoundConstructor
    public ClearCaseStep(String configSpec) {
        this.configSpec = configSpec;
    }

    public String getConfigSpec() {
        return configSpec;
    }

    @DataBoundSetter
    public void setConfigSpec(String configSpec) {
        this.configSpec = configSpec;
    }

    public String getViewTag() {
        return viewTag;
    }

    @DataBoundSetter
    public void setViewTag(String viewTag) {
        this.viewTag = viewTag;
    }

    public String getViewPath() {
        return viewPath;
    }

    @DataBoundSetter
    public void setViewPath(String viewPath) {
        this.viewPath = viewPath;
    }

    public String getChangeset() {
        return changeset;
    }

    @DataBoundSetter
    public void setChangeset(String changeset) {
        this.changeset = changeset;
    }

    private AbstractClearCaseScm.ChangeSetLevel getChangesetClass() {
        switch (changeset) {
            case "branch":
                return AbstractClearCaseScm.ChangeSetLevel.BRANCH;
            case "updt":
                return AbstractClearCaseScm.ChangeSetLevel.UPDT;
            default:
                return AbstractClearCaseScm.ChangeSetLevel.NONE;
        }
    }

    public ViewStorage getViewStorage() {
        return viewStorage;
    }

    @DataBoundSetter
    public void setViewStorage(ViewStorage viewStorage) {
        this.viewStorage = viewStorage;
    }

    public boolean isUseUpdate() {
        return useUpdate;
    }

    @DataBoundSetter
    public void setUseUpdate(boolean useUpdate) {
        this.useUpdate = useUpdate;
    }

    @Nonnull
    @Override
    protected SCM createSCM() {
        ClearCaseSCM scm = new ClearCaseSCM("", "",
                true, configSpec,
                false, "", // refreshConfigSpec
                "", viewTag,
                useUpdate, // use update
                true, "", // loadRules
                false, "", // loadRules polling
                false, "", // dyn view
                "", // mkviewoptionalparam
                false, // filterOutDestroySubBranchEvent
                false, // doNotUpdateConfigSpec
                false, // rmviewonrename,
                "", // excludedRegions
                "", // multiSitePollBuffer
                false, // useTimeRule
                false, // createDynView
                viewPath,
                getChangesetClass(),
                viewStorage);

        return scm;
    }

    @Extension
    public static final class DescriptorImpl extends SCMStepDescriptor {
        public static final String defaultViewTag = "Jenkins_${USER_NAME}_${NODE_NAME}_${JOB_NAME}${DASH_WORKSPACE_NUMBER}";
        public static final String defaultViewPath = "view";
        public static final String defaultChangeset = "no";
        public static final ViewStorage defaultViewStorage = new DefaultViewStorage();
        public static final boolean defaultUseUpdate = true;

        public final String viewTag = defaultViewTag;
        public final String viewPath = defaultViewPath;
        public final String changeset = defaultChangeset;
        public final ViewStorage viewStorage = defaultViewStorage;
        public final boolean useUpdate = defaultUseUpdate;

        @Override
        public String getFunctionName() {
            return "clearcase";
        }

        @Override
        public String getDisplayName() {
            return "ClearCase snapshot view";
        }

    }

}
