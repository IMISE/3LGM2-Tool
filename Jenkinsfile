pipeline {
  agent any
  tools {
    jdk "JDK-8"
    maven "Maven-3.6.3"
  }
  stages {
    stage('Build') {
      steps {
          echo 'Getting axs-utils.jar'
          copyArtifacts(filter:'*', projectName: 'axsutils/deploy', selector: lastSuccessful())
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
