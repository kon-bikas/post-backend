pipeline {
    agent any

    environment {
        DOCKER_TOKEN = credentials('docker-push-secrer')
        DOCKER_USER = 'kon-bikas'
        DOCKER_SERVER = 'ghcr.io'
        DOCKER_PREFIX = 'ghcr.io/kon-bikas/post-app'

        DIR_ANSIBLE_PROJECT = '/var/lib/jenkins/workspace/ansible'
    }

    stages {
        stage ('Cloning latest ansible repo') {
            steps {
                build job: 'ansible'
            }
        }
        stage ('Test') {
            options {
                timeout(time: 10, unit: 'MINUTES')
            }
            steps {
                sh """
                    echo "Start testing..."
                    ./mvnw test -Dspring.profiles.active=test
                """
            }
        }
        stage ('Build docker image and push') {
            options {
                timeout(time: 30, unit: 'MINUTES')
            }
            steps {
                script {
                    def headCommit = sh(
                            script: "git rev-parse --short HEAD",
                            returnStdout: true
                    ).trim()

                    env.TAG = "${headCommit}-${env.BUILD_ID}"
                }
                sh '''
                    docker build --rm -t $DOCKER_PREFIX:latest -t $DOCKER_PREFIX:$TAG -f spring.Dockerfile .
               '''

                sh '''
                    echo $DOCKER_TOKEN | docker login $DOCKER_SERVER -u $DOCKER_USER --password-stdin
                    docker push $DOCKER_PREFIX --all-tags
                '''
            }
        }
        stage ('Deploy to kubernetes') {
            steps {
                dir("${env.DIR_ANSIBLE_PROJECT}") {
                    ansiblePlaybook(
                            inventory: 'hosts.yml',
                            playbook: 'playbooks/spring-k8s.yml',
                            extraVars: [
                                    new_image: "${env.DOCKER_PREFIX}:${env.TAG}"
                            ]
                    )
                }
            }
        }
    }

    post {
        always {
            echo 'Slack notification!'
            slackSend (
                    channel: '#new-channel',
                    message: "*${currentBuild.currentResult}:* Job ${env.JOB_NAME} \n" +
                            " build ${env.BUILD_NUMBER} \n" +
                            "more info at ${env.BUILD_URL}}"
            )
        }
    }
}