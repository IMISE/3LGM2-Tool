pipeline {
  agent any
  tools {
    jdk "JDK-8"
    maven "Maven-3.6.3"
  }
  stages {

    stage('IMPORT AXSUTILS') {
      steps {
        step {
          copyArtifacts(projectName: 'axsutils');
        }
      }
    }

    stage('BUILD') {
      steps {
        step {
          echo '####### Compiling metamodel.original #######'
        }
        step {
          echo '####### Compiling metamodel.service #######'
        }
        step {
          echo '####### Compiling tool3lgm.deploy #######'
        }
        step {
          echo '####### Compiling tool3lgm.template.ihe #######'
        }
        step {
          echo '####### Compiling tool3lgm #######'
        }
      }
    }

    stage('TEST') {
      steps {
        step {
          echo '####### Testing metamodel.original #######'
        }
        step {
          echo '####### Testing metamodel.service #######'
        }
        step {
          echo '####### Testing tool3lgm.deploy #######'
        }
        step {
          echo '####### Testing tool3lgm.template.ihe #######'
        }
        step {
          echo '####### Testing tool3lgm #######'
        }
      }
    }
  }
}
