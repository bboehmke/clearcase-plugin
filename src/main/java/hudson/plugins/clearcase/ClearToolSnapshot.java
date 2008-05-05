package hudson.plugins.clearcase;

import hudson.FilePath;
import hudson.util.ArgumentListBuilder;

import java.io.File;
import java.io.IOException;

public class ClearToolSnapshot extends ClearToolExec {

    private String optionalMkviewParameters;
    
    public ClearToolSnapshot(ClearToolLauncher launcher, String clearToolExec) {
        super(launcher, clearToolExec);
    }

    public ClearToolSnapshot(ClearToolLauncher launcher, String clearToolExec, String optionalParameters) {
        this(launcher, clearToolExec);
        this.optionalMkviewParameters = optionalParameters;
    }

    public void setcs(String viewName, String configSpec) throws IOException,
            InterruptedException {
        FilePath workspace = launcher.getWorkspace();
        FilePath configSpecFile = workspace.createTextTempFile("configspec", ".txt", configSpec);

        ArgumentListBuilder cmd = new ArgumentListBuilder();
        cmd.add(clearToolExec);
        cmd.add("setcs");
        cmd.add(".." + File.separatorChar + configSpecFile.getName());
        launcher.run(cmd.toCommandArray(), null, null, workspace.child(viewName));

        configSpecFile.delete();
    }

    public void mkview(String viewName, String streamSelector) throws IOException, InterruptedException {
        ArgumentListBuilder cmd = new ArgumentListBuilder();
        cmd.add(clearToolExec);
        cmd.add("mkview");
        cmd.add("-snapshot");
        if (streamSelector != null) {
            cmd.add("-stream");
            cmd.add(streamSelector);
        }
        cmd.add("-tag");
        cmd.add(viewName);
        if ((optionalMkviewParameters != null) && (optionalMkviewParameters.length() > 0)) {
            cmd.addTokenized(optionalMkviewParameters);
        }
        cmd.add(viewName);
        launcher.run(cmd.toCommandArray(), null, null, null);
    }

    public void rmview(String viewName) throws IOException, InterruptedException {
        ArgumentListBuilder cmd = new ArgumentListBuilder();
        cmd.add(clearToolExec);
        cmd.add("rmview");
        cmd.add("-force");
        cmd.add(viewName);
        launcher.run(cmd.toCommandArray(), null, null, null);
        FilePath viewFilePath = launcher.getWorkspace().child(viewName);
        if (viewFilePath.exists()) {
            launcher.getListener().getLogger().println(
                    "Removing view folder as it was not removed when the view was removed.");
            viewFilePath.deleteRecursive();
        }
    }

    public void update(String viewName, String loadRules) throws IOException, InterruptedException {
        ArgumentListBuilder cmd = new ArgumentListBuilder();
        cmd.add(clearToolExec);
        cmd.add("update");
        cmd.add("-force");
        cmd.add("-log", "NUL");
        if (loadRules == null) {
            cmd.add(viewName);
        } else {
            cmd.add("-add_loadrules");
            cmd.add(viewName + File.separator + loadRules);
        }
        launcher.run(cmd.toCommandArray(), null, null, null);
    }

    @Override
    protected FilePath getRootViewPath(ClearToolLauncher launcher) {
        return launcher.getWorkspace();
    }

    public void startView(String viewTag) throws IOException, InterruptedException {
        launcher.getListener().fatalError("Snapshot view does not support startview");
    }
}
