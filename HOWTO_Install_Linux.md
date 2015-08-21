# HOWTO: Install on a Linux System #

## Requirements ##

  1. Linux
  1. Java (1.5.x or newer, Sun-Oracle, OpenJDK, or other)

## Naming Convention ##

  * root: Linux Administrator user
  * user Linux Non-Administrator user

## Install ##

**Become root:**
```
[user] sudo su - 
[sudo] password for user: 
```

**Change to the directory where you want to install the utility:**
```
[root] cd /usr/local/
```

**Download the package:**
```
[root] wget -q http://scp-java-client.googlecode.com/svn/trunk/scp/build/scp_v1.2/scp_v1.2.bin.tar.gz
```

**Verify the .tar.gz package:**
```
[root] tar tvzf scp_v1.2.bin.tar.gz
```

**Download the hash SHA1 of the package:**
```
[root] wget -q http://scp-java-client.googlecode.com/svn/trunk/scp/build/scp_v1.2/scp_v1.2.bin.tar.gz.sha
```

**Verify the checksum:**
```
$sha1sum -c scp_v1.2.bin.tar.gz.sha 
scp_v1.2.bin.tar.gz: OK
```

**Import the GPG Public Key:**
```
[root] wget -O- -q http://scp-java-client.googlecode.com/svn/trunk/scp/KEYS | gpg --import
gpg: /root/.gnupg/trustdb.gpg: trustdb created
gpg: key 5C79EE7A: public key "Marco Ratto (key for Google Code Projects) <marcoratto@gmail.com>" imported
gpg: Total number processed: 1
gpg:               imported: 1

```

**Download the GPG signature:**
```
[root] wget -q http://scp-java-client.googlecode.com/svn/trunk/scp/build/scp_v1.2/scp_v1.2.bin.tar.gz.asc
```

**Check the GPG signature:**
```
[root] gpg --verify scp_v1.2.bin.tar.gz.asc 
gpg: Signature made Sun 25 Aug 2013 10:47:00 AM CEST using DSA key ID 5C79EE7A
gpg: Good signature from "Marco Ratto (key for Google Code Projects) <marcoratto@gmail.com>"
gpg: WARNING: This key is not certified with a trusted signature!
gpg:          There is no indication that the signature belongs to the owner.
Primary key fingerprint: 66CC 4C24 D606 7E65 6E44  B7FE FA32 157C 5C79 EE7A
```

**Now, if all the checks are OK, you can install it:**
```
[root] tar xvzf scp_v1.2.bin.tar.gz
```

**For managing the release update, it is better to create a sym-link:**
```
[root] rm scp
[root] ln -s scp-1.2 scp
```

**Change the GRANT to the unix-shell-script for enabling to execute to everyone:**
```
[root] chmod 755 scp/scp.sh
```

**Delete files downloaded:**
```
[root] rm scp_v1.2.bin.tar.gz
[root] rm scp_v1.2.bin.tar.gz.sha
[root] rm scp_v1.2.bin.tar.gz.asc
```

**Check the File System correctly configured:**
```
[root] ls -al | grep scp
lrwxrwxrwx   1 root     root        7 Aug 25 10:58 scp -> scp-1.2
drwxr-xr-x   7 root     root     4096 Jul 24  2012 scp-1.1
drwxr-xr-x   7 root     root     4096 Aug 25 10:58 scp-1.2
```

Note:
In this case it was an upgrade from the release 1.1 to 1.2.

**Create the local configuration for the user:**
```
[user] mkdir $HOME/.scp
[user] mkdir $HOME/.scp/conf
```

**Copy the default appender from the installation:**
```
[user] cp /usr/local/scp/res/scp_config_file.properties $HOME/.scp/conf
```

**Test it with a non-root user:**
```
[user] /usr/local/scp/scp.sh
log4j: Trying to find [log4j.xml] using context classloader sun.misc.Launcher$AppClassLoader@45a877.
log4j: Trying to find [log4j.xml] using sun.misc.Launcher$AppClassLoader@45a877 class loader.
log4j: Trying to find [log4j.xml] using ClassLoader.getSystemResource().
log4j: Trying to find [log4j.properties] using context classloader sun.misc.Launcher$AppClassLoader@45a877.
log4j: Using URL [file:/usr/local/scp-1.2/res/log4j.properties] for automatic log4j configuration.
log4j: Reading configuration from URL file:/usr/local/scp-1.2/res/log4j.properties
log4j: Parsing for [root] with value=[warn, file].
log4j: Level token is [warn].
log4j: Category root set to WARN
log4j: Parsing appender named "file".
log4j: Parsing layout options for "file".
log4j: Setting property [conversionPattern] to [%d{ABSOLUTE} %5p %c{1}:%L - %m%n].
log4j: End of parsing for "file".
log4j: Setting property [file] to [/home/user/.scp/scp.log].
log4j: Setting property [maxBackupIndex] to [7].
log4j: Setting property [maxFileSize] to [1024MB].
log4j: setFile called: /home/user/.scp/scp.log, true
log4j: setFile ended
log4j: Parsed "file" options.
log4j: Parsing for [uk.co.marcoratto.scp] with value=[trace, file].
log4j: Level token is [trace].
log4j: Category uk.co.marcoratto.scp set to TRACE
log4j: Parsing appender named "file".
log4j: Appender "file" was already parsed.
log4j: Handling log4j.additivity.uk.co.marcoratto.scp=[null]
log4j: Finished configuring.
 * Parameters:
-source
	This is an alternative to the file attribute. But this must always point to a local file. The reason this was added was that when you give file attribute it is treated as remote if it contains @ character. This character can exist also in local paths. Alternative to file attribute.
-target
	This is an alternative to the file attribute. But this must always point to a remote file. Alternative to file attribute.
-port
	The port to connect to on the remote host (default to 22).
-trust
	This trusts all unknown hosts if set to yes/true (default to false).
	Note If you set this to false (the default), the host you connect to must be listed in your knownhosts file, this also implies that the file exists. 	
-knownhosts
	This sets the known hosts file to use to validate the identity of the remote host. This must be a SSH2 format file. SSH1 format is not supported (default to ${user.home}/.ssh/known_hosts).
-password
	The password. Not if you are using key based authentication or the password has been given in the file or todir attribute.
-keyfile
	Location of the file holding the private key.
-passphrase
	Passphrase for your private key (default to an empty string).
-v
	Determines whether SCP outputs verbosely to the user. Currently this means outputting dots/stars showing the progress of a file transfer (default to false).
-sftp
	Determines whether SCP uses the sftp protocol. The sftp protocol is the file transfer protocol of SSH2. It is recommended that this be set to true if you are copying to/from a server that doesn't support scp1 (default to false).
-preserveLastModified
	Determines whether the last modification timestamp of downloaded files is preserved. It only works when transferring from a remote to a local system and probably doesn't work with a server that doesn't support SSH2 (default to false).-retry #
	 Number of retry in case of errors.
-r
	 Traverse recursive the directory and send the files.
-ask
	 Ask to the use to digit the password.

ERROR! java return error code 3.
```

**Test it:**
```
[user] /usr/local/scp/scp.sh -source $HOME/tmp/001.pdf -target user:password@127.0.0.1:/tmp -trust
log4j: Trying to find [log4j.xml] using context classloader sun.misc.Launcher$AppClassLoader@45a877.
log4j: Trying to find [log4j.xml] using sun.misc.Launcher$AppClassLoader@45a877 class loader.
log4j: Trying to find [log4j.xml] using ClassLoader.getSystemResource().
log4j: Trying to find [log4j.properties] using context classloader sun.misc.Launcher$AppClassLoader@45a877.
log4j: Using URL [file:/usr/local/scp-1.2/res/log4j.properties] for automatic log4j configuration.
log4j: Reading configuration from URL file:/usr/local/scp-1.2/res/log4j.properties
log4j: Parsing for [root] with value=[warn, file].
log4j: Level token is [warn].
log4j: Category root set to WARN
log4j: Parsing appender named "file".
log4j: Parsing layout options for "file".
log4j: Setting property [conversionPattern] to [%d{ABSOLUTE} %5p %c{1}:%L - %m%n].
log4j: End of parsing for "file".
log4j: Setting property [file] to [/home/user/.scp/scp.log].
log4j: Setting property [maxBackupIndex] to [7].
log4j: Setting property [maxFileSize] to [1024MB].
log4j: setFile called: /home/user/.scp/scp.log, true
log4j: setFile ended
log4j: Parsed "file" options.
log4j: Parsing for [uk.co.marcoratto.scp] with value=[trace, file].
log4j: Level token is [trace].
log4j: Category uk.co.marcoratto.scp set to TRACE
log4j: Parsing appender named "file".
log4j: Appender "file" was already parsed.
log4j: Handling log4j.additivity.uk.co.marcoratto.scp=[null]
log4j: Finished configuring.
Start upload file 1/1 from /home/user/tmp/001.pdf to user:password@127.0.0.1:/tmp
End upload file 1/1 from /home/user/tmp/001.pdf to user:password@127.0.0.1:/tmp
```

**That's all folks!**