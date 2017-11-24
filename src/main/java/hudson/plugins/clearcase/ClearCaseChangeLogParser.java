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
package hudson.plugins.clearcase;

import hudson.model.AbstractBuild;
import hudson.model.Run;
import hudson.scm.ChangeLogParser;
import hudson.scm.ChangeLogSet;

import java.io.File;
import java.io.IOException;

import hudson.scm.RepositoryBrowser;
import org.xml.sax.SAXException;

/***********************************************************************************************************************
 * ClearCase change log parser.
 * 
 * @author Erik Ramfelt
 */
public class ClearCaseChangeLogParser extends ChangeLogParser {
    @Override
    public ChangeLogSet<? extends ChangeLogSet.Entry> parse(Run build, RepositoryBrowser<?> browser, File changelogFile) throws IOException, SAXException {
        return ClearCaseChangeLogSet.parse(build, changelogFile);
    }

    @Override
    public ChangeLogSet<ClearCaseChangeLogEntry> parse(@SuppressWarnings("unchecked") AbstractBuild build, File changelogFile) throws IOException, SAXException {
        return ClearCaseChangeLogSet.parse(build, changelogFile);
    }
}
