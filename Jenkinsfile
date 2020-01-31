pipeline {
  agent any
  tools {
    jdk "JDK-8"
    maven "Maven-3.6.3"
  }
  stages {
    stage('BUILD') {
      steps {
        step {
          copyArtifacts filter: 'target/de-axs-utils.1.0.0.jar',
            fingerprintArtifacts: true, projectName: 'AXSUtils',
            selector: lastSuccessful(), target: '/'
        }
        step {
          echo '####### Compiling metamodel.original #######'
          sh 'mvn -B -U -X -f de.imise.metamodel.original/pom.xml -Dmaven.test.skip clean install'
        }
        step {
          echo '####### Compiling metamodel.service #######'
          sh 'mvn -B -U -X -f de.imise.metamodel.service/pom.xml -Dmaven.test.skip clean install'
        }
        step {
          echo '####### Compiling tool3lgm.template.ihe #######'
          sh 'mvn -B -U -X -f de.imise.tool3lgm.template.ihe/pom.xml -Dmaven.test.skip clean install'
        }
        step {
          echo '####### Compiling tool3lgm #######'
          sh 'mvn -B -U -X -f de.imise.tool3lgm/pom.xml -Dmaven.test.skip clean install'
        }
      }
    }
    stage('TEST') {
      steps {
        step {
          echo 'Hello There'
        }
      }
    }
  }
}
