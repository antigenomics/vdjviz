# VDJviz: a versatile immune repertoire browser

VDJviz is a web-based graphical user interface application that allows browsing and analyzing immune repertoire sequencing ([RepSeq](http://onlinelibrary.wiley.com/doi/10.1111/j.1365-2567.2011.03527.x/epdf)) data. It can be used to visualize results of [MITCR](mitcr.milaboratory.com), [MIGEC](https://github.com/mikessh/migec), [MIXCR](mixcr.milaboratory.com) and [MIGMAP](https://github.com/mikessh/migmap) RepSeq processing software as well as popular [IMGT/HighV-QUEST](http://www.imgt.org/HighV-QUEST/login.action) and [ImmunoSEQ Analyzer](http://www.adaptivebiotech.com/immunoseq/analyzer) services. VDJviz can be installed and used as a local server, alternatively you can use an online demo version available at [vdjviz.milaboratory.com](http://vdjviz.milaboratory.com) which currently has an upload limit of 25 datasets each having size at most 10,000 clonotypes. The list of VDJviz features at a glance:

- Clonotype table browsing with V/D/J markup.
- CDR3 pattern matching for a single sample and across multiple samples with flexible filters.
- Spectratype, V-Spectratype, V-J usage and clonality analysis. Those can be compared side-by-side using the Compare panel.
- Summary statistics and rarefaction for multiple samples.
- Clonotype sharing across samples (many-to-many intersection) with flexible clonotype matching criteria.
- Uploaded data sharing.

VDJviz uses [VDJtools API](https://github.com/mikessh/vdjtools) as a back-end. The software utilizes [Play framework](https://www.playframework.com/) for running the server instance and state-of-art web graphics libraries such as [D3js](http://d3js.org/) for visualization. 

## Installation

The most straightforward way to install VDJviz as a local server is to download the [latest release package](https://github.com/antigenomics/vdjviz/releases/latest).

After downloading unzip the package wherever you want, but please avoid long paths and spaces (Windows version is especially sensitive to it).

You can find the server executable in ``bin/`` directory. To set up the server:

- Run `vdjviz.bat` file (Windows)
- Run `bash vdjviz -Dconfig.file=application.conf` in your console (Linux/Mac OS)

Wait until the server is started, and go to ``localhost:9000`` URL in your browser to open VDJviz.
The user generator is enabled in the config by default, so you can login with `vdjviz1@vdjviz.com` as an email and `vdjviz1` as password,

To stop application just press `Ctrl-C` at any time in console.

## Server configuration

VDJviz server configuration can be performed by manually editing ``application.conf`` file in the ``bin/`` directory. The configuration file has the following fields:

- ``application.secret``
The secret key used in cryptographic hash functions.

- ``uploadPath``
Specifies the path that will be used by VDJviz to store user's uploaded files.
You can use '~' symbol as a shortcut for user home directory.
Default: `~/vdjviz/`

- ``maxFileSize``
File size limit in kB
Default: `0` (no limit)

- ``maxFilesCount``
Limit on the number of uploaded files per user.
Default: ``0`` (no limit)

- ``maxClonotypesCount``
Limit on the number of clonotypes for each uploaded file.
Default: ``0`` (no limit)

- ``allowSharing``
Disable or enable sharing feature
Default: ``true`` (enabled)

- ``maxSharedGroups``
Maximum number of shared analyses per user.
Default: ``0`` (no limit)
 
- ``deleteAfter``
Time period after which uploaded files are deleted from the server, in hours.
Default: ``0`` (never)

- ``applyNewLimitsToOldUsers``
If set to ``true`` the server will automatically update limtis of all existing user accounts according to the ones specified in config. If set to ``false``, the limits will only be applied to newly created users.
Default: ``true``

- ``createDefaultUsers``
Specifies whether the server will generate some default user accounts with predefined emails and passwords, setting their emails to ``<nameDefaultUser><id>@vdjviz.com`` (e.g. ``vdjviz1@vdjviz.com``) and passwords to ``<nameDefaultUser><id>`` (e.g. ``vdjviz1``). Set this option to ``false`` if you don't need this feature and prefer to use registration via SMTP.
Default: ``true``

- ``nDefaultUsers``
Number of default users to be created.
Default: ``1``

- ``nameDefaultUser``
Default user name prefix.
Default: `vdjviz`

- ``db.default.url``
Points to the path that will be used to store H2 database file.
Default value: ``~/vdjviz/h2.db``
Standalone version uses [H2 Database](http://www.h2database.com/html/main.html) for handling metadata by default, if you want to change H2 to another DBMS please see the corresponding Play documentation [section](https://www.playframework.com/documentation/2.2.4/JavaDatabase)
You can also use this database to manually modify user limits.

- ``allowRegistration``
Show the Register button in login screen.
Default: ``false``

- ``allowChangePasswords``
Show the Change Password button in login screen.
Default: ``false``

- ``smtp.*``
SMTP server configuration.
If you don't want to use registration features, you can leave ``smtp.*`` fields empty and generate default users.
