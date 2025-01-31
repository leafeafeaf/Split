pipeline {
    agent any

    environment {
        CONTAINER_NAME = 'backend'
        DOCKER_IMAGE = 'backend-image:latest'
    }

    stages {
        stage('Prepare Environment') {
            steps {
                sh '''
                    rm -rf src/main/resources
                    mkdir -p src/main/resources
                    chmod 777 src/main/resources

                    # Docker 캐시 정리
                    docker system prune -f
                '''
            }
        }

        stage('Secrets Setup') {
            steps {
                withCredentials([
                     file(credentialsId: 'application-yaml', variable: 'applicationfile'),
                ]) {
                    sh '''
                        cp "$applicationfile" src/main/resources/application.yaml
                        chmod 644 src/main/resources/application.yaml
                    '''
                }
            }
        }

        stage('Build') {
            steps {
                sh '''
                    chmod +x ./backend/Split/gradlew
                    # gradle 빌드 시 테스트 스킵 (-x test)
                    # 메모리 제한 (256m)
                    # 데몬 비활성화 (--no-daemon)
                    ./backend/Split/gradlew clean build -x test --no-daemon
                '''
            }
        }

        stage('Docker Build & Deploy') {
            steps {
                script {
                    sh 'docker rm -f ${CONTAINER_NAME} || true'
                    sh 'docker rmi ${DOCKER_IMAGE} || true'

                    sh 'docker build -t ${DOCKER_IMAGE} .'

                    sh '''
                        docker run -d \
                            --name ${CONTAINER_NAME} \
                            --restart unless-stopped \
                            -p 8080:8080 \
                            ${DOCKER_IMAGE}
                    '''
                }
            }
        }
    }
}