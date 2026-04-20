pipeline {
    agent any

    tools {
        maven 'Maven_3'
        jdk 'JDK 21'
    }

    environment {
        IMAGE_NAME = 'dilmi222/shopping-cart-app'
        IMAGE_TAG = 'latest'
        SONARQUBE_SERVER = 'SonarQubeServer'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/Tharushika78910/ShoppingCartApplication.git'
            }
        }

        stage('Build') {
            steps {
                bat 'mvn clean compile'
            }
        }

        stage('Test') {
            steps {
                bat 'mvn test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Coverage Report') {
            steps {
                bat 'mvn jacoco:report'
            }
        }

        stage('Package') {
            steps {
                bat 'mvn package'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQubeServer') {
                    bat 'mvn sonar:sonar'
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                bat 'docker build -t %IMAGE_NAME%:%IMAGE_TAG% .'
            }
        }

        stage('Push to Docker Hub') {
            steps {
                withCredentials([usernamePassword(
                        credentialsId: 'Docker-Hub',
                        usernameVariable: 'DOCKER_USERNAME',
                        passwordVariable: 'DOCKER_PASSWORD'
                )]) {
                    bat 'echo %DOCKER_PASSWORD% | docker login -u %DOCKER_USERNAME% --password-stdin'
                    bat 'docker push %IMAGE_NAME%:%IMAGE_TAG%'
                }
            }
        }
    }

    post {
        success {
            echo 'Pipeline completed successfully.'
        }
        failure {
            echo 'Pipeline failed. Check the failed stage logs.'
        }
    }
}