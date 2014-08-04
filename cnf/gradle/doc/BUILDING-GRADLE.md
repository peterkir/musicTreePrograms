# <a name="Introduction"/>Introduction

This bnd workspace is setup to be built with [Gradle](http://www.gradle.org).

The build is setup in such a way that bnd projects are automatically
included in the build; no editing of Gradle build scripts is needed.

# <a name="History"/>History

The bndtools Gradle plugin was originally delivered with bndtools 2.3.0 but has
since been replaced with a different implemention in bndtools. The plugin was
thereon forked and now lives on through the support
of [Pelagic](http://www.pelagic.nl).

The project is fully Open Source and can be found
at [GitHub](https://github.com/fhuberts/bndtoolsPlugins).

Contributions are welcome!

# <a name="TableOfContents"/>Table Of Contents

* [Introduction](#Introduction)
* [History](#History)
* [Table Of Contents](#TableOfContents)
* [Installing Gradle](#InstallingGradle)
  * [On The System](#InstallingGradleOnTheSystem)
  * [In The Workspace](#InstallingGradleInTheWorkspace)
* [Configuring The Gradle Daemon](#ConfiguringTheGradleDaemon)
* [Projects & Workspaces](#ProjectsAndWorkspaces)
  * [Root Project](#ProjectsAndWorkspacesRootProject)
  * [Sub-Projects](#ProjectsAndWorkspacesSubProjects)
  * [Gradle Workspace](#ProjectsAndWorkspacesGradleWorkspace)
  * [Bnd Workspace](#ProjectsAndWorkspacesBndWorkspace)
  * [Configuration Project](#ProjectsAndWorkspacesCnf)
  * [Bnd Project Layout](#ProjectsAndWorkspacesBndProjectLayout)
* [Build Flow](#BuildFlow)
* [Build Tasks](#BuildTasks)
  * [Bnd Projects](#BuildTasksBndProjects)
    * [jar](#BuildTasksJar)
    * [check](#BuildTasksCheck)
    * [checkNeeded](#BuildTasksCheckNeeded)
    * [release](#BuildTasksRelease)
    * [releaseNeeded](#BuildTasksReleaseNeeded)
    * [export.<name>](#BuildTasksExportName)
    * [export](#BuildTasksExport)
    * [echo](#BuildTasksEcho)
    * [bndproperties](#BuildTasksBndProperties)
    * [clean](#BuildTasksClean)
  * [All Projects](#BuildTasksAllProjects)
    * [index](#BuildTasksIndex)
    * [cleanNeeded](#BuildTasksCleanNeeded)
    * [distClean](#BuildTasksDistClean)
    * [distcleanNeeded](#BuildTasksDistCleanNeeded)
  * [Java Projects](#BuildTasksJavaProjects)
    * [Findbugs](#BuildTasksFindbugs)
      * [findbugsMain](#BuildTasksFindbugsMain)
      * [findbugsTest](#BuildTasksFindbugsTest)
      * [findbugs](#BuildTasksfindbugs)
      * [findbugstest](#BuildTasksfindbugstest)
    * [javadoc](#BuildTasksJavadoc)
  * [Root Project](#BuildTasksRootProject)
    * [wrapper](#BuildTasksWrapper)
* [Build Options](#BuildOptions)
  * [Bnd Projects](#BuildOptionsBndProjects)
  * [Findbugs](#BuildOptionsFindbugs)
* [Customising The Build](#CustomisingTheBuild)
  * [Gradle](#CustomisingTheBuildGradle)
  * [Bnd](#CustomisingTheBuildBnd)
* [Adding Java Projects To The Build](#AddingJavaProjectsToTheBuild)


# <a name="InstallingGradle"/>Installing Gradle

## <a name="InstallingGradleOnTheSystem"/>On The System

Obviously, Gradle must be installed on the system before the workspace can be
built with Gradle.

This description assumes a Linux machine. Details may vary on other OSes.

* Download Gradle from [http://www.gradle.org](http://www.gradle.org).

* Unpack the downloaded archive and put it in ```/usr/local/lib```
  as ```/usr/local/lib/gradle-1.11``` (assuming Gradle 1.11 was downloaded).

* Put the Gradle executable ```/usr/local/lib/gradle-1.11/bin/gradle``` on
  the search path by linking to it from ```/usr/local/bin```:

  ```
  rm -f /usr/local/bin/gradle
  ln -s /usr/local/lib/gradle-1.11/bin/gradle /usr/local/bin/
  ```

## <a name="InstallingGradleInTheWorkspace"/>In The Workspace

Gradle can be installed in the workspace so that the workspace can be built on
systems that do not have Gradle installed (like build servers).

The procedure is:

* Open a shell and go into the workspace root directory.

* Assuming Gradle is properly installed on the system, run:

  ```
  gradle wrapper
  ```

* Commit the files that were created in the workspace to your version control
  system.


# <a name="ConfiguringTheGradleDaemon"/>Configuring The Gradle Daemon

Startup times of a Gradle build can be much improved by using the Gradle daemon.

The Gradle daemon works well when the Gradle build scripts are not changed,
which makes it well suited to regular (where the build scripts are not changed!)
development but **not** for build servers.

The daemon can be easily setup by adding the following line
to ```~/.gradle/gradle.properties```:

```
org.gradle.daemon=true
```


# <a name="ProjectsAndWorkspaces"/>Projects & Workspaces

## <a name="ProjectsAndWorkspacesRootProject"/>Root Project

The Gradle root project is the directory that contains the ```settings.gradle```
file.

Gradle locates the root project by first looking for the ```settings.gradle```
file in the directory from which it was run, and - when not found - then by
searching up in the directory tree.

## <a name="ProjectsAndWorkspacesSubProjects"/>Sub-Projects

The build will include all projects in the build that are:

* **bnd** projects: Directories directly below the root project with
                    a ```bnd.bnd``` file.

* **Gradle** projects: Directories directly below the root project with
                       a ```build.gradle``` file.

## <a name="ProjectsAndWorkspacesGradleWorkspace"/>Gradle Workspace

The Gradle workspace is rooted in the root project and consists of all included
projects - **bnd** *and* **Gradle** projects.

## <a name="ProjectsAndWorkspacesBndWorkspace"/>Bnd Workspace

The bnd workspace is rooted in the root project and contains a single
configuration project, and zero or more **bnd** projects.

For it to be a *useful* bnd workspace, it will have to contain at least one bnd
project.

## <a name="ProjectsAndWorkspacesCnf"/>Configuration Project

The configuration project is the directory that contains the ```build.bnd```
file, and is - by default - the ```cnf``` directory.

It contains:

* Placeholder source and classes directories (```src``` and ```bin```
  respectively).

* Bnd workspace configuration.

  * &nbsp;```ext/*.bnd```

    The ```ext``` directory contains bnd settings files that are loaded
    **before** the ```build.bnd``` file.

    The directory typically contains:

    * &nbsp;```junit.bnd```

      This file defines a bnd variable for the libraries that are needed on the
      classpath when running junit tests.

    * &nbsp;```pluginpaths.bnd```

      This file instructs bnd to load a number of plugin libraries when it
      runs. Typically it will instruct bnd to load repository plugins. However,
      custom plugins can also be loaded by bnd by adding them to
      the ```-pluginpath``` instruction in this file.

    * &nbsp;```repositories.bnd```

      This file configures the plugins that bnd loads. Typically it will
      configure the repository plugins that are loaded. However, if any built-in
      plugins or custom plugins are loaded then these also will have to be
      configured here. This file also defines which repository is the release
      repository.

  * &nbsp;```build.bnd```

    This file contains workspace-wide settings for bnd and will override
    settings that are defined in either of the ```ext/*.bnd``` files.

* Repositories.

  * &nbsp;```buildrepo```

    This repository contains libraries that are intended **only for build-time**
    usage. None are intended to be deployed as bundles into a running OSGi
    framework, and indeed they may cause unexpected errors if they are used
    in such a fashion.

  * &nbsp;```localrepo```

    This repository contains no libraries by default. It is intended for
    external libraries that are needed by one or more of the projects.

  * &nbsp;```releaserepo```

    This repository contains no libraries by default. Bundles end up in this
    repository when they are released.

* Cache.

  The ```cache``` directory contains libraries that are downloaded by the build.
  If the build is self-contained then this cache only contain libraries that are
  retrieved from the workspace itself (during the build).

* Build files.

  * <a name="BuildProperties"/>```gradle.properties```

    This file is used to bootstrap the build and (among other things) defines
    the build dependencies:

    * All ```*.url``` settings are considered to be build dependencies.

    * An ```example.url``` setting will make the build script add the file
      indicated by the URL to the build dependencies when the file exists on the
      local filesystem. If the file doesn't exist on the local filesystem, then
      the build script will download the build dependency from the specified URL
      into the ```cnf/cache``` directory and add it to the build dependencies.

      Using a ```*.url``` setting that points to an external location is
      not **not recommended** since the build will then no longer be
      self-contained (because it needs network access).

  * &nbsp;```cnf/gradle```

    This directory contains all build script files that are used by the build,
    and documentation pertaining to the build.

    * &nbsp;```template```

      This directory contains build script files that define the build. These
      are **not** meant to be changed.

    * &nbsp;```custom```

      This directory contains build script files that are hooks into the
      build process. They allow specification of overrides for various settings,
      additions to the build, modifications to the build, etc.

      These **are** meant to be changed (when the build customisations are
      needed).

    * &nbsp;```dependencies```

      This directory contains libraries that are used by the build.

    * &nbsp;```doc```

      This directory contains documentation pertaining to the build. The
      document you're now reading is located in this directory.

      <a name="svg"/>
      Also found in this directory is a diagram ([template.svg](template.svg))
      that provides an overview of the build setup, much like the Gradle User
      Guide shows for the Java Plugin.

      The diagram shows all tasks of the build and their dependencies:

      * The arrows depict **execution flow** (so the dependencies are in the
        reverse direction).

      * The **red** arrows depict flows from (dependencies on) dependent
        projects.

        For example:

        The ```compileJava``` task of a project is dependent on the ```jar```
        task of another project if the latter project is on the build path of
        the former project.

      * The **blue** arrows depict flows/dependencies that are only present
        when the task from which the flows originate is present in the project.

      * The **green** arrows depict flows/dependencies that are disabled by
        default.

## <a name="ProjectsAndWorkspacesBndProjectLayout"/>Bnd Project Layout

A bnd project has a well defined layout with two source sets and one output
directory:

* main sources: located in the ```src``` directory. Compiled sources will be
  placed in the ```bin``` directory.

* test sources: located in the ```test``` directory. Compiled sources will be
  placed in the ```bin_test``` directory.

* output directory ```generated```. Built OSGi bundle(s) will be placed here.

All bnd project layout directories can be customised by adjusting the following
settings in the project's ```bnd.bnd``` file:

* &nbsp;```src```: directory for main sources

* &nbsp;```bin```: directory for compiled main sources

* &nbsp;```testsrc```: directory for test sources

* &nbsp;```testbin```: directory for compiled test sources

* &nbsp;```target-dir```: directory for the built bundle(s)


# <a name="BuildFlow" />Build Flow

Understanding the build flow is important if extra tasks must be added to the
build, properties must be overridden, etc.

The build has the following flow:

* Gradle loads all properties defined in the ```gradle.properties``` file in
  the root of the workspace.

* Gradle invokes the ```settings.gradle``` file in the root of the workspace.
  This file initialises the build:

  * Detect the location of the configuration project
    (see [Configuration Project](#ProjectsAndWorkspacesCnf)).

  * initialise the bnd workspace.

  * The build dependencies are determined from the (bootstrap) build
    properties that were loaded from the ```gradle.properties``` file
    (see [the explanation of the build properties file](#BuildProperties).

  * Include all **bnd** projects and all **Gradle** projects
    (see [Sub-Projects](#ProjectsAndWorkspacesSubProjects) for an explanation).

* Gradle loads the ```build.gradle``` file from the root project. This file
  sets up the build:

  * The bnd plugin is loaded.

  * The hook ```cnf/gradle/custom/workspace-pre.gradle``` is invoked.

  * From now on the build template has 3 sections which are applied in the order:

    * All projects
    * Sub-Projects
    * Root Project

    **All Projects**

    This section sets up the build (defaults) for all included projects by
    iterating over all included projects and performing the actions described
    below.

    * The hook ```cnf/gradle/custom/allProject-pre.gradle``` is invoked.

    * Load project specific build settings from the ```build-settings.gradle```
      file in the project directory if the file is present.

      A project specific ```build-settings.gradle``` is placed in the
      root of an included project and allows overrides of the build
      settings, additions to the build, modifications of the build, etc, on a
      project-by-project basis.

    * Index tasks are added to the project.

    * Clean tasks are added to the project.

    * The hook ```cnf/gradle/custom/allProject-post.gradle``` is invoked.

    **Sub-Projects**

    This section sets up the build (defaults) for all included projects,
    (excluding the root project) by iterating over all included sub-projects.
    A distinction is made between **bnd** projects and **Gradle** projects.

    * The hook ```cnf/gradle/custom/subProject-pre.gradle``` is invoked.

    * Gradle projects (Non-Bnd Projects)

      * The hook ```cnf/gradle/custom/nonBndProject-pre.gradle``` is invoked.

      * The default tasks are setup (specified by the ```others_defaultTask```
        property). This is a comma separated list of task names.

    * bnd projects

      * The bnd project instance is save in the project as the ```bndProject```
        property.

      * The hook ```cnf/gradle/custom/bndProject-pre.gradle``` is invoked.
      
      * The bnd plugin is applied.

      * The hook ```cnf/gradle/custom/bndProject-post.gradle``` is invoked.

    * For all projects that have applied the Gradle Java plugin the
      buildscript ```cnf/gradle/template/javaProject.gradle``` is applied,
      thereby adding tasks that are relevant to Java projects.

      * The hook ```cnf/gradle/custom/javaProject-pre.gradle``` is invoked.

      * Setup the library directory (when generated artifacts will be placed).

      * Setup the test options.

      * Setup the javaDoc task.

      * Setup the findbugs tasks.

      * Setup the jacoco task.

      * Adjust the distclean task to clean all output directories of all
        sourceSets.

      * The hook ```cnf/gradle/custom/javaProject-post.gradle``` is invoked.

    * The hook ```cnf/gradle/custom/subProject-post.gradle``` is invoked.

    **Root Project**

    This section sets up the build (defaults) for the root project.

    * The hook ```cnf/gradle/custom/rootProject-pre.gradle``` is invoked.

    * The default tasks are setup (specified by the ```root_defaultTask```
      property).  This is a comma separated list of task names.

    * Setup the wrapper task
    
    * Setup the distclean task to also clean the ```cnf/cache```
      and ```.gradle``` directories .

    * The hook ```cnf/gradle/custom/rootProject-post.gradle``` is invoked.

  * The hook ```cnf/gradle/custom/workspace-post.gradle``` is invoked.

* For every included project with a ```build.gradle``` file Gradle loads that
  file.

* Gradle resolves the build setup.

* Gradle can now build the project by running the specified (or default) tasks.


# <a name="BuildTasks"/>Build Tasks

The discussion of the build tasks below is split per project type/category.

## <a name="BuildTasksBndProjects"/>Bnd Projects

### <a name="BuildTasksJar"/>jar

This task instructs bnd to construct an OSGi bundle.

This is comparable to the ```jar``` task that is defined by the Java plugin,
which instructs the Java compiler to construct a standard jar.

A bnd project completely replaces the ```jar``` task.

The ```bnd.bnd``` file describes how the OSGi bundle must be constructed and is
therefore taken as input by bnd.

### <a name="BuildTasksCheck"/>check

This task instructs bnd to run bundle (integration) tests.

This is comparable to the ```test``` task that is defined by the Java plugin,
which instructs the Java runtime to run unit tests.

Refer to the bnd manual/website for more details on how to setup bundle tests.

This task is automatically disabled when no bundle tests have been defined.

### <a name="BuildTasksCheckNeeded"/>checkNeeded

This task will invoke the ```check``` task on all projects on which the
project is dependent, after which the ```check``` task is invoked on the
project itself.

### <a name="BuildTasksRelease"/>release

This task instructs bnd to copy the constructed OSGi bundle into the release
repository.

This task is automatically disabled when no release repository is defined.

### <a name="BuildTasksReleaseNeeded"/>releaseNeeded

This task will invoke the ```release``` task on all projects on which the
project is dependent, after which the ```release``` task is invoked on the
project itself.

### <a name="BuildTasksExportName"/>export.<name>

This task will export the ```<name>.bndrun``` file in the project to an
executable jar.

This task is only setup the project contains a ```<name>.bndrun``` file.

### <a name="BuildTasksExport"/>export

This task will export all ```*.bndrun``` files in the project to executable
jars.

This task is automatically disabled when the project contains no ```*.bndrun```
files.

### <a name="BuildTasksEcho"/>echo

This task displays some key bnd properties of the project.

### <a name="BuildTasksBndProperties"/>bndproperties

This task - analogous to the Gradle ```properties``` task - displays the bnd
properties that are defined for the project.

These properties are defined in the ```bnd.bnd``` file in the root of the
project (and optionally other ```*.bnd``` files when using the ```-sub```
instruction for sub-bundles).

Properties that are defined in workspace-wide ```*.bnd``` files that are loaded
from the configuration project (```cnf```) are also displayed as they obviously
also apply to the project (unless overridden by the project, in which case the
overridden values are shown).

### <a name="BuildTasksClean"/>clean

This task instructs bnd to clean up the project, which removes
the output directory and the directories that hold the class files.

This is in addition to the ```clean``` task that is defined by the Java plugin.


## <a name="BuildTasksAllProjects"/>All Projects

### <a name="BuildTasksIndex"/>index

This task can create one or more of the following:
* an uncompressed OBR index
* an uncompressed R5 index
* a compressed OBR index
* a compressed R5 index

These indexes are generated from/for one or more configured directories.

Which directories are indexed is controlled by
the ```indexDirectories``` property. Its **syntax** is:

```
<root directory>;<name>;<name of fileTree property>, ...
```

* &nbsp;```root directory```: This is the root/base directory
  from where the relative URLs in the index file are calculated, and where
  the index file will be placed. Must be specified but doesn't need to exist.

* &nbsp;```name```: This is the name of the repository. Can be empty, in which
  case the name (*basename*) of the ```root directory``` is used.

* &nbsp;```name of fileTree property```: This is the name of a project property
  that must be an instance of a FileTree. This file tree determines which
  files will be indexed. If not specified then the all ```*.jar``` files
  below the ```root directory``` are indexed.

Multiple indexes can be generated by specifying (syntax as displayed in the box
above):

```
syntax,syntax,...
```

This task is automatically disabled when no index directories have been defined
or when no OBR indexes **and** no R5 indexes are configured to be created
(either uncompressed or compressed).

OBR index generation is controlled by the properties

* &nbsp;```indexOBRUncompressed```: if set to ```true``` then an
  uncompressed OBR index is generated.

* &nbsp;```indexOBRCompressed``` if set to ```true``` then a
  compressed OBR index is generated.

R5 index generation is controlled by the properties

* &nbsp;```indexR5Uncompressed```: if set to ```true``` then an
  uncompressed R5 index is generated.

* &nbsp;```indexR5Compressed``` if set to ```true``` then a
  compressed R5 index is generated.

### <a name="BuildTasksCleanNeeded"/>cleanNeeded

This task will invoke the ```clean``` task on all projects on which the
project is dependent, after which the ```clean``` task is invoked on the
project itself.

Note that invoking this task on the root project will invoke the task on all
projects.

### <a name="BuildTasksDistClean"/>distClean

This task performs additional cleanup compared to the ```clean``` task, but is
empty by default.

For bnd projects and Java projects it removes:

* The class files output directory of all defined sourcesets.

* The resources output directory of all defined sourcesets.

For the root project it removes:

* The cache directory in the configuration project.

* The Gradle cache directory.

### <a name="BuildTasksDistCleanNeeded"/>distcleanNeeded

This task will invoke the ```distClean``` task on all projects on which the
project is dependent, after which the ```distClean``` task is invoked on the
project itself.

Note that invoking this task on the root project will invoke the task on all
projects.


## <a name="BuildTasksJavaProjects"/>Java Projects

### <a name="BuildTasksFindbugs"/>Findbugs

The findbugs plugin is applied to all bnd projects and to all Java projects.
The plugin adds the tasks ```findbugsMain``` and ```findbugsTest```.

These two tasks are disabled by default since running findbugs is an expensive
operation and is not needed for most builds.

Note that the reports that are generated by the findbugs tasks will only have
line numbers when the tasks are run on a build that produces artefacts with
debug information.

#### <a name="BuildTasksFindbugsMain"/>findbugsMain

This task will run findbugs on the main source code.

#### <a name="BuildTasksFindbugsTest"/>findbugsTest

This task will run findbugs on the test source code.

#### <a name="BuildTasksfindbugs"/>findbugs

Specifying this task will **enable** the ```findbugsMain``` task.

Note: it is needed to specify a task that has a dependency on
the ```findbugsMain``` that to actually run the task. The tasks ```check```
and ```build``` are examples is such a task.

#### <a name="BuildTasksfindbugstest"/>findbugstest

Specifying this task will **enable** the ```findbugsTest``` task.

Note: it is needed to specify a task that has a dependency on
the ```findbugsTest``` that to actually run the task. The tasks ```check```
and ```build``` are examples is such a task.

### <a name="BuildTasksJavadoc"/>javadoc

This task generates javadoc for the main source code.


## <a name="BuildTasksRootProject"/>Root Project

### <a name="BuildTasksWrapper"/>wrapper

This task downloads Gradle and installs it in the workspace,
see [Installing Gradle In The Workspace](#InstallingGradleInTheWorkspace).


# <a name="BuildOptions"/>Build Options

## <a name="BuildOptionsBndProjects"/>Bnd Projects

* The ```jar``` task can be disabled by:

  * Presence of the ```-nobundles``` instruction in the ```bnd.bnd``` file.

* The ```test``` task can be disabled by:

  * Presence of the ```-nojunit``` instruction in the ```bnd.bnd``` file.

  * Presence of the ```no.junit```  property in the ```bnd.bnd``` file.

* The ```check``` task can be disabled by:

  * Presence of the ```-nojunitosgi``` instruction in the ```bnd.bnd``` file.

## <a name="BuildOptionsFindbugs"/>Findbugs

The findbugs task will - by default - generate HTML reports, but can be
instructed to generate XML reports by setting the ```CI``` Gradle system
property (```-PCI``` on the command line).

&nbsp;```CI``` = ```C```ontinous ```I```ntegration
                 (since XML reports are typically used on build servers)


# <a name="CustomisingTheBuild"/>Customising The Build

## <a name="CustomisingTheBuildGradle"/>Gradle

The build be can easily customised by putting overrides and additions in any of
the ```cnf/gradle/custom/*.gradle``` build script files,
see [Build Flow](#BuildFlow).

Also, any project can - on an individual basis - customise build settings or
specify additions by placing a ```build-settings.gradle``` file in its
root directory.

The ```build-settings.gradle``` file is meant for settings and their overrides,
the ```build.gradle``` file is meant for tasks.

An example of a ```build-settings.gradle``` file is shown below. This example
shows how a project instructs the build to index its ```bundles``` directory
to generate indexes named ```example project```.

```
assert(project != rootProject)

/* Index task overrides */
ext.bndDistIndexRoot        = 'bundles'
ext.bndDistIndexDirectories = fileTree(bndDistIndexRoot).include('**/*.jar').exclude('**/*-latest.jar')
ext.indexDirectories        = "${bndDistIndexRoot};example project;bndDistIndexDirectories"
ext.indexOBRUncompressed    = true
ext.indexOBRCompressed      = true
ext.indexR5Uncompressed     = true
ext.indexR5Compressed       = true
```

## <a name="CustomisingTheBuildBnd"/>Bnd

The bnd default settings are shown in the ```cnf/build.bnd``` file.
Overrides to workspace-wide bnd settings can be placed in that same file.

If a setting must be overridden or defined for a specific project, then that
setting must be defined in the ```bnd.bnd``` file of that specific project.


# <a name="AddingJavaProjectsToTheBuild"/>Adding Java Projects To The Build

The build automatically includes all bnd projects.

However, regular Java projects are not included automatically,
a ```build.gradle``` file in the root directory of the project is needed to make
that happen.

Such projects only need to apply the Gradle Java plugin and setup their
sourceSets, the template will then automatically apply the
buildscript ```cnf/gradle/template/javaProject.gradle```.

The ```build.gradle``` file shown below can be used as the basis. This will
setup the Java project with the default bnd layout and add tasks that are
relevant to a Java project (```javadoc```, findbugs tasks and ```distclean```).

```
/*
 * A java project with bnd layout
 */

assert(project != rootProject)

apply plugin: 'java'

/* We use the same directory for java and resources. */
sourceSets {
  main {
    java.srcDirs      = resources.srcDirs   = files('src')
    output.classesDir = output.resourcesDir =       'bin'
  }
  test {
    java.srcDirs        = resources.srcDirs   = files('test'    )
    output.classesDir   = output.resourcesDir =       'test_bin'
  }
}
```

