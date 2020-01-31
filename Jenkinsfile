pipeline {
  agent any
  tools {
    jdk "JDK-8"
    maven "Maven-3.6.3"
  }
  stages {
    stage('Importing AXS-Utils') {
      steps {
      echo '#################################'
      echo '##     Importing AXS-Utils     ##'
      echo '#################################'
        sh 'cd de.imise.tool3lgm/'
        echo '####### Getting axs-utils.jar #######'
        copyArtifacts(filter:'*', projectName: 'axsutils/deploy', selector: lastSuccessful())
        script {
          echo '####### create POM of de-axs-utils-1.0.0.jar #######'
          sh 'echo "<project xmlns=\\"http://maven.apache.org/POM/4.0.0\\" xmlns:xsi=\\"http://www.w3.org/2001/XMLSchema-instance\\" xsi:schemaLocation=\\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xs\\"> <groupId>de.axs</groupId> <artifactId>de-axs-utils</artifactId> <version>1.0.0</version> </project>" > axs-utils.pom'

          echo '####### install POM of de-axs-utils-1.0.0.jar #######'
          sh 'mvn install:install-file -Dfile=de-axs-utils-1.0.0.jar -DpomFile=axs-utils.pom'
        }
      }
    }
    stage('Build') {
      steps {
        echo '#################################'
        echo '##       Executing Build       ##'
        echo '#################################'

        echo '####### building tool3lgm #######'
        sh 'mvn -B -U -X -f de.imise.tool3lgm/pom.xml -Dmaven.test.skip clean install'

        echo '####### building metamodel.original #######'
        sh 'mvn -B -U -X -f de.imise.metamodel.original/pom.xml -Dmaven.test.skip clean install'

        echo '####### building metamodel.service #######'
        sh 'mvn -B -U -X -f de.imise.metamodel.service/pom.xml -Dmaven.test.skip clean install'

        echo '####### building template.ihe #######'
        sh 'mvn -B -U -X -f de.imise.tool3lgm.template.ihe/pom.xml -Dmaven.test.skip clean install'
      }
    }
    stage('Test') {
      steps {
        echo '#################################'
        echo '##       Executing Tests       ##'
        echo '#################################'

        echo '####### Testing tool3lgm #######'
        sh 'mvn -f de.imise.tool3lgm/pom.xml test'

        echo '####### Testing metamodel.original #######'
        sh 'mvn -f de.imise.metamodel.original/pom.xml test'

        echo '####### Testing metamodel.service #######'
        sh 'mvn -f de.imise.metamodel.service/pom.xml test'

        echo '####### Testing template.ihe #######'
        sh 'mvn -f de.imise.tool3lgm.template.ihe/pom.xml test'
      }
    }
    stage('Archiving') {
      steps {
        echo '#################################'
        echo '##      Executing Archiving    ##'
        echo '#################################'

        dir('de.imise.tool3lgm.deploy/Tool3lgm') {
          sh 'ls -lisa'
          archiveArtifacts artifacts: '**', fingerprint: true
        }

        sh 'apt-get install zip'
        sh 'zip -r tool3lgm.zip de.imise.tool3lgm.deploy/Tool3lgm' 

      }
    }
  }
}
