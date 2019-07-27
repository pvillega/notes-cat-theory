addSbtPlugin("com.dwijnand"      % "sbt-dynver"      % "4.0.0")
addSbtPlugin("com.dwijnand"      % "sbt-travisci"    % "1.2.0")
addSbtPlugin("org.scalameta"     % "sbt-scalafmt"    % "2.0.2")
addSbtPlugin("de.heikoseeberger" % "sbt-header"      % "5.0.0")
addSbtPlugin("org.wartremover"   % "sbt-wartremover" % "2.4.2")
addSbtPlugin("com.timushev.sbt"  % "sbt-updates"     % "0.4.2")

libraryDependencies += "org.slf4j" % "slf4j-nop" % "1.7.25" // Needed by sbt-git
