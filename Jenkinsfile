pipeline {
    agent any

    environment {
        CONTAINER_NAME = 'backend'
        DOCKER_IMAGE = 'backend-image:latest'
    }

    stages {
        stage('Prepare Environment') {
            steps {
                //cicd/html 아래 파일 모두 삭제
                sh 'rm -rf ./cicd/html/*'
                sh 'docker system prune -f'
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

        stage('Build Backend') {
            steps {
                //백엔드 빌드
                sh '''
                    cd ./backend/Split

                    chmod +x ./gradlew

                    # gradle 빌드 시 테스트 스킵 (-x test)
                    # 데몬 비활성화 (--no-daemon)
                    ./gradlew clean build -x test --no-daemon
                '''
            }
        }
         stage('Build Frontend') {
            steps {
                sh '''
                    cd ./Frontend/spilt_FE/

                    # 의존성 설치 (기존 node_modules 유지)
                    npm ci || npm install

                    # Next.js 빌드 및 정적 변환
                    npm run build
                '''

                //프론트엔드 빌드 파일 cicd/html 아래로 이동
                sh 'cp -r ./Frontend/spilt_FE/dist/* ./cicd/html/'
            }
        }

        stage('Docker Build & Deploy') {
            steps {
                script {
                    sh 'docker-compose down -v'
                    sh 'docker-compose up -d --build'
                }
            }
        }
    }
}