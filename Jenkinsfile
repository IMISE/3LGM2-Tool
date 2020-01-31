pipeline {
  agent any
  tools {
    jdk "JDK-8"
    maven "Maven-3.6.3"
  }
  stages {
    stage('IMPORT AXSUTILS') {
      steps{
        step([$class: 'CopyArtifact',
            projectName: 'AXSUtils',
            filter: 'target/de-axs-utils-1.0.0.jar']);
        }
    }
    stage('BUILD') {
      steps {
        step {
          sh 'echo "####### Compiling metamodel.original #######"'
          sh 'mvn -B -U -X -f de.imise.metamodel.original/pom.xml -Dmaven.test.skip clean install'
        }
        step {
          sh 'echo "####### Compiling metamodel.service #######"'
          sh 'mvn -B -U -X -f de.imise.metamodel.service/pom.xml -Dmaven.test.skip clean install'
        }
        step {
          sh 'echo "####### Compiling tool3lgm.template.ihe #######"'
          sh 'mvn -B -U -X -f de.imise.tool3lgm.template.ihe/pom.xml -Dmaven.test.skip clean install'
        }
        step {
          sh 'echo "####### Compiling tool3lgm #######"'
          sh 'mvn -B -U -X -f de.imise.tool3lgm/pom.xml -Dmaven.test.skip clean install'
        }
      }
    }
    stage('TEST') {
      steps {
        step {
          sh 'echo "Hello There"'
        }
      }
  }
}
