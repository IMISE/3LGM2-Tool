pipeline {
  agent any
  tools {
    jdk "JDK-8"
    maven "Maven-3.6.3"
  }
  stages {
    stage('Build') {
      steps {
          echo 'Building..'
          copyArtifacts filter: 'target/de-axs-utils.1.0.0.jar',
            fingerprintArtifacts: true, projectName: 'AXSUtils/deploy',
            selector: lastSuccessful(), target: '/'
      }
    }
    stage('Test') {
      steps {
          echo 'Testing..'
      }
    }
    stage('Deploy') {
      steps {
          echo 'Deploying....'
      }
    }
  }
}
