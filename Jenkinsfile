pipeline {
    agent any

    environment {
        DOCKER_TOKEN = credentials('docker-push-secrer')
        DOCKER_USER = 'kon-bikas'
        DOCKER_SERVER = 'ghcr.io'
        DOCKER_PREFIX = 'ghcr.io/kon-bikas/postr-app'

        ANSIBLE_CONFIG = '/var/lib/jenkins/workspace/ansible/ansible.cfg'
        ANSIBLE_SSH_ARGS = '-F /var/lib/jenkins/.ssh/config'
        DIR_ANSIBLE_PROJECT = '/var/lib/jenkins/workspace/ansible'
    }

    stages {
        stage('Cloning latest ansible repo') {
            steps {
                build job: 'ansible'
            }
        }
        stage('Test') {
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
                sh '''
                    HEAD_COMMIT=$(git rev-parse --short HEAD)
                    TAG=$HEAD_COMMIT-$BUILD_ID
                    docker build --rm -t $DOCKER_PREFIX:latest -f spring.Dockerfile .
                '''

                sh '''
                    echo $DOCKER_TOKEN | docker login $DOCKER_SERVER -u $DOCKER_USER --password-stdin
                    docker push $DOCKER_PREFIX --all-tags
                '''
            }
        }
        stage ('Test connection to deploy server') {
            steps {
                sh '''
                    ansible -i ~/workspace/ansible/hosts.yml -m ping aws_app_server
                '''
            }
        }
        stage ('Start docker compose services') {
            steps {
                sshagent(credentials: ['jenkins-github']) {
                    dir("${env.DIR_ANSIBLE_PROJECT}") {
                        ansiblePlaybook(
                                inventory: 'hosts.yml',
                                playbook: 'playbooks/spring-docker.yml',
                                vaultCredentialsId: 'ansible-vault-pass',
                                extraVars: [
                                        docker_secret: "${env.DOCKER_TOKEN}"
                                ]
                        )
                    }
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