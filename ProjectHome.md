Java Application for copying files using [SCP protocol](http://en.wikipedia.org/wiki/Secure_copy).

You can download the latest release 1.2 from this [url](http://scp-java-client.googlecode.com/svn/trunk/scp/build/scp_v1.2)

This java client uses the Apache Ant Task Scp modified (just the dependency from the library `jsch-0.1.44.jar`).

Some features:

  * Multiplatform (yes! it is written ALL in Java SE)
  * Sys logging (using `log4j-1.2.16.jar`, default log file is located on this path `$USER_HOME/.scp/log/scp.log`; you can change configuration on the file `$SCP_HOME/res/log4j.properties`)
  * Listener API (Java interface `uk.co.marcoratto.scp.listeners.Listener`, see [Listeners](Listeners.md) for more details)

The available parameters are the following:

| **Parameter** | **Mandatory** | **Default** | **Description** |
|:--------------|:--------------|:------------|:----------------|
| **-source**   | Yes           | n/d         | The source directory. This can be a local path or a remote path of the form `user[:password]@host:/directory/path`. :password can be omitted if you use key based authentication or specify the password attribute. The way remote path is recognized is whether it contains @ character or not. This will not work if your localPath contains @ character.|
| **-target**   | Yes           | n/d         | The target directory. This can be a local path or a remote path of the form `user[:password]@host:/directory/path`. :password can be omitted if you use key based authentication or specify the password attribute. The way remote path is recognized is whether it contains @ character or not. This will not work if your localPath contains @ character.|
| **-port**     | No            | 22          |The port to connect to on the remote host.|
| **-trust**    | No            | false       | This trusts all unknown hosts if set to true. Note If you set this to false (the default), the host you connect to must be listed in your knownhosts file, this also implies that the file exists.|
| **-knownhosts** | No            | ${user.home}/.ssh/known\_hosts | This sets the known hosts file to use to validate the identity of the remote host. This must be a SSH2 format file. SSH1 format is not supported.|
| **-password** | No            | No          | The password. Not if you are using key based authentication or the password has been given in the file or todir attribute.|
| **-keyfile**  | No            | No          | Location of the file holding the private key.|
| **-passphrase** | No            | empty string if -keyfile specified | Passphrase for your private key.|
| **-sftp**     | No            | false       | Determines whether SCP uses the sftp protocol. The sftp protocol is the file transfer protocol of SSH2. It is recommended that this be set to true if you are copying to/from a server that doesn't support scp1.|
| **-preserveLastModified** | n/a           | false       | Determines whether the last modification timestamp of downloaded files is preserved. It only works when transferring from a remote to a local system and probably doesn't work with a server that doesn't support SSH2.|
| **-v**        | No            | false       | Determines whether SCP outputs verbosely to the user. Currently this means outputting dots/stars showing the progress of a file transfer.|
| **-r**        | No            | false       | Search the file recursively.|
| **-ask**      | No            | false       | Ask to digit the password.|

[![](http://www2.clustrmaps.com/stats/maps-no_clusters/code.google.com-p-scp-java-client-thumb.jpg)](http://www2.clustrmaps.com/user/62210a608)