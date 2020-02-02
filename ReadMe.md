#Deriver15Public

[Code compiles and runs with Eclipse 2019-09R (4.1.3.0) 1/31/2020]

Software for instruction in logic (as used on https://softoption.us ). 
This is the full source code for Deriver15, which is java code 
for displaying proofs, intelligent editing of derivations, tactics, 
drawing derivation trees, drawing semantic interpretations, symbolization 
etc. This code is pretty much unchanged since 2012. In 2015 the license 
notices were added, hence Deriver15.

The Java Build Path should have in it the source code and the JRE System 
Library, say Java SE 1.7 (although it won't be sensitive on that). It 
should also have JUnit 5 as a Library (as there is some JUnit test code).

The code can then be run within Eclipse as a Java Application with the main

   TDeriverApplication -- us.softoption.editor

The documentation on https://softoption.us would help here on what the 
application can do.

Eclipse can export the application as a Runnable JAR file, say Deriver15.jar.

It would be typical then to run this through Proguard, say 6.2.2 which will
obfuscate and compress it (from about 3.6 meg to 1 meg). It is important here
that the right configuration file be used for Proguard. Basically the 
consideration is that Java Beans are used to save to file, and this means
that when windows and contents are re-created from file the names of 
the classes cannot have been obfuscated. Some sample Proguard configuration 
files are included. Proguard may produce hundreds of warnings but yet run 
through successfully.

Then it would be usual to sign the compressed jar using jarsigner and Java 
key tool. It is possible to timestamp this.

Deriver15 has various 'switches' in it controlled through its preferences.
Sample logic instruction files are included.

The Deriver15 jar/application can be automatically downloaded and launched
using jnlp and Java Web Start. It also can configure itself when doing
this. Some sample files are included.

The source code for twenty or more applets is included. These used to be
embedded in web pages so as to have Deriver functionality without having
to have the application installed or downloaded. The applet code used to
be obfuscated, compressed, and signed.

This technology is dead. [Most of the applets have been rewritten as
javascript widgets, and that code is available elsewhere.]

However, you can still run the applets in Eclipse. The applets have
'applet' in their name somewhere. For example, us.softoption.gameApplets
has as one of its source files

   GamesQuiz.java

If you select that file and 'Run As Java Applet' Eclipse will do exactly 
that for you (and similarly for the other applets).
