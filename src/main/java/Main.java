import java.io.IOException;

import org.apache.maven.cli.MavenCli;

/*
 * MRE for https://stackoverflow.com/questions/78041113/maven-embedder-maven-install-plugin-bug-second-install-file-overrides-first-one .
 */
public class Main {

    private static void maven(String... args) {
        final int result = new MavenCli().doMain(
            args,
            ".",
            System.out,
            System.out
        );

        if (result != 0) {
            throw new RuntimeException("mvn " + args[0] + "failed.");
        }
    }

    public static void main(String[] args) throws IOException {
        maven(
            "install:install-file",
            "-Dfile=just-a-not-maven-built.jar",
            "-DgroupId=somegroupid",
            "-DartifactId=someartifactid",
            "-Dversion=1.0.0",
            "-Dpackaging=jar",
            "-DgeneratePom=true"
        );

        /*
         * According to https://maven.apache.org/guides/mini/guide-3rd-party-jars-local.html ,
         * this should install an artifact using the META-INF/pom.xml :
         */
        maven(
            "org.apache.maven.plugins:maven-install-plugin:2.5.2:install-file",
            "-Dfile=just-a-random-maven-built.jar"
        );

        /*
         * Now `jar tf ~/.m2/repository/somegroupid/someartifactid/1.0.0/someartifactid-1.0.0.jar`
         * shows erronously the contents of just-a-random-maven-built.jar, NOT of just-a-not-maven-built.jar .
         */
    }
}
