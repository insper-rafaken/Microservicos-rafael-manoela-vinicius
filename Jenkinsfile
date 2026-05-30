pipeline {
    agent any

    environment {
        DOCKERHUB_USER = 'rafaelken'
        EKS_CLUSTER    = 'eks-store'
        AWS_REGION     = 'us-east-2'
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build & Push Order') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'dockerhub-credentials',
                    usernameVariable: 'DOCKER_USER',
                    passwordVariable: 'DOCKER_PASS'
                )]) {
                    sh '''
                        echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
                        docker build -t $DOCKERHUB_USER/order:latest -f api/order/order-service/Dockerfile .
                        docker push $DOCKERHUB_USER/order:latest
                    '''
                }
            }
        }

        stage('Deploy to EKS') {
            steps {
                withCredentials([
                    string(credentialsId: 'aws-access-key-id',     variable: 'AWS_ACCESS_KEY_ID'),
                    string(credentialsId: 'aws-secret-access-key', variable: 'AWS_SECRET_ACCESS_KEY')
                ]) {
                    sh '''
                        aws eks update-kubeconfig --region $AWS_REGION --name $EKS_CLUSTER
                        kubectl rollout restart deployment/order
                    '''
                }
            }
        }
    }

    post {
        success { echo 'Deploy realizado com sucesso!' }
        failure { echo 'Pipeline falhou.' }
    }
}
