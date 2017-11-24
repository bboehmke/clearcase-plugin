/**
 * The MIT License
 *
 * Copyright (c) 2007-2009, Sun Microsystems, Inc., Kohsuke Kawaguchi, Erik Ramfelt,
 *                          Henrik Lynggaard, Peter Liljenberg, Andrew Bayer
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package hudson.plugins.clearcase.util;

import hudson.EnvVars;
import hudson.FilePath;
import hudson.Util;
import hudson.model.*;
import hudson.util.LogTaskListener;
import hudson.util.VariableResolver;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;

/**
 * A {@link VariableResolver} that resolves certain Build variables.
 * <p>
 * The build variable resolver will resolve the following:
 * <ul>
 * <li>HOST - The name of the computer it is running on</li>
 * <li>OS - Shorthand to "os.name" system property</li>
 * <li>USER_NAME - The system property "user.name" on the Node that the Launcher is being executed on (slave or master)</li>
 * <li>NODE_NAME - The name of the node that the Launcher is being executed on</li>
 * <li>Any environment variable (system or build-scoped) that is set on the Node that the Launcher is being executed on (slave or master)</li>
 * </ul>
 * Implementation note: This class is modelled after Erik Ramfelt's work in the Team Foundation Server Plugin. Maybe they should be merged and moved to the
 * hudson core
 * 
 * @author Henrik Lynggaard Hansen
 */
public class BuildVariableResolver implements VariableResolver<String> {

    private static final Logger           LOGGER = Logger.getLogger(BuildVariableResolver.class.getName());

    private Run<?, ?>           build;

    private transient Computer            computer;

    private transient String              nodeName;

    private boolean                       restricted;

    private transient Map<Object, Object> systemProperties;

    public BuildVariableResolver(final Run<?, ?> build) {
        this.build = build;
        this.computer = build.getExecutor().getOwner();
        this.nodeName = this.computer.getNode().getNodeName();
    }

    public BuildVariableResolver(final AbstractBuild<?, ?> build, boolean restricted) {
        this(build);
        this.restricted = restricted;
    }

    @Override
    public String resolve(String key) {
        try {
            if (systemProperties == null) {
                systemProperties = computer.getSystemProperties();
            }
            LogTaskListener ltl = new LogTaskListener(LOGGER, Level.INFO);
            if ("JOB_NAME".equals(key) && build != null) {
                return build.getParent().getFullName();
            }

            if ("HOST".equals(key)) {
                return (Util.fixEmpty(computer.getHostName()));
            }

            if ("OS".equals(key)) {
                return (String) systemProperties.get("os.name");
            }

            if ("NODE_NAME".equals(key)) {
                return (Util.fixEmpty(StringUtils.isEmpty(nodeName) ? "master" : nodeName));
            }

            if ("USER_NAME".equals(key)) {
                return (String) systemProperties.get("user.name");
            }
            if ("DASH_WORKSPACE_NUMBER".equals(key)) {
                FilePath workspace = build.getExecutor().getCurrentWorkspace();
                if (workspace == null && build.getParent() instanceof WorkflowJob) {
                    workspace = this.computer.getNode().getWorkspaceFor((WorkflowJob) build.getParent());
                }
                if (workspace == null) {
                    return "";
                }
                String[] split = workspace.getName().split("@");
                if (split.length > 1) {
                    return "-" + split[split.length - 1];
                }
                return "";
            }

            // build parameters map
            Map<String, String> buildVariables = new HashMap<>();

            // check for parametrized build
            ParametersAction parameters = build.getAction(ParametersAction.class);
            if (parameters != null) {
                for (ParameterValue p : parameters.getAllParameters()) {
                    if (p.getValue() instanceof String) {
                        buildVariables.put(p.getName(), (String) p.getValue());
                    }
                }
            }
            if (buildVariables.containsKey(key)) {
                return buildVariables.get(key);
            }
            EnvVars compEnv = computer.getEnvironment();
            if (compEnv.containsKey(key)) {
                return compEnv.get(key);
            }
            if (!restricted) {
                EnvVars env = build.getEnvironment(ltl);
                if (env.containsKey(key)) {
                    return env.get(key);
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Variable name '" + key + "' look up failed", e);
        }
        return null;
    }
}
