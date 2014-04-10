name := "JXTable"

version := "1.0"

scalaVersion := "2.10.3"

libraryDependencies += "junit" % "junit" % "4.8" % "test"

libraryDependencies += "org.swinglabs.swingx" % "swingx-all" % "1.6.5" excludeAll ExclusionRule(name = "commons-logging")

libraryDependencies += "it.tidalwave.betterbeansbinding" % "betterbeansbinding-core" % "1.3.0" excludeAll ExclusionRule(name = "commons-logging")

libraryDependencies += "it.tidalwave.betterbeansbinding" % "betterbeansbinding-swingbinding" % "1.3.0" excludeAll ExclusionRule(name = "commons-logging")

libraryDependencies += "net.java.dev.timingframework" % "timingframework" % "1.0"

libraryDependencies += "jgoodies" % "forms" % "1.0.5"

libraryDependencies += "log4j" % "log4j" % "1.2.17"
