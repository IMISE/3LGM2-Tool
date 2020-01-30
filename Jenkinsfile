pipeline {
  agent any
  tools {
    jdk "JDK-8"
    maven "Maven-3.6.3"
  }
  stages {
    stage('IMPORT AXSUTILS') {
      steps {
        script {
          echo '####### copying artifact from last stable build of axsutils #######'
        }
        step {
          copyArtifacts(projectName: 'axsutils')
        }
      }
    }
    stage('BUILD') {
      steps {
        step {
          sh 'echo "####### Compiling metamodel.original #######"'
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
          echo '####### Testing metamodel.original #######'
        }
        step {
          echo '####### Testing metamodel.service #######'
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
