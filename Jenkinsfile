pipeline {
  agent {
    docker {
      image 'niaquinto/gradle'
    }
    
  }
  stages {
    stage('Build') {
      steps {
        sh 'gradle clean build'
      }
    }
  }
}