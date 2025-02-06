pipeline {
    agent any

    environment {
        CONTAINER_NAME = 'backend'
        DOCKER_IMAGE = 'backend-image:latest'
    }

    stages {
        /* sh별로 루트 디렉토리로 자동으로 이동함함 */
        stage('Prepare Environment') {
            steps {
                sh '''
                    cd ./backend/Split
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
                     file(credentialsId: 'env-file', variable: 'EnvFile'),
                ]) {
                    sh '''
                        cp "$EnvFile" .env
                        chmod 644 .env
                    '''
                }
            }
        }

        stage('Build') {
            steps {
                sh '''
                    cd ./backend/Split

                    # 빌드를 위한 실행권한 부여
                    chmod +x ./gradlew

                    # gradle 빌드 시 테스트 스킵 (-x test)
                    # 데몬 비활성화 (--no-daemon)
                    ./gradlew clean build -x test --no-daemon
                '''
            }
        }

        stage('Docker Build & Deploy') {
            steps {
                script {
                    sh 'docker compose down'
                    sh 'docker compose up -d --build'

                    sh '''
                        # 컨테이너 삭제 (실행중이어도 강제로)
                        docker rm -f ${CONTAINER_NAME} || true
                        # 이미지 삭제
                        docker rmi ${DOCKER_IMAGE} || true
                        # -t 이름 부여
                        docker build -t ${DOCKER_IMAGE} .

                        # 이미지 바탕으로 컨테이너 생성 및 실행행
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