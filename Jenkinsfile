pipeline {
    agent any

    tools {
        maven 'Maven 3.8.5'
    }

    environment {
        SONAR_QUBE_URL = 'http://sonarqube:9000'
        SONAR_QUBE_TOKEN = credentials('sonar-token')
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean install'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    sh 'mvn sonar:sonar -Dsonar.projectKey=product-discount-api -Dsonar.host.url=${SONAR_QUBE_URL} -Dsonar.login=${SONAR_QUBE_TOKEN}'
                }
            }
        }

        stage('Deploy') {
            steps {
                echo 'Deploying...'
            }
        }
    }
}