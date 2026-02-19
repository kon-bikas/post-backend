pipeline {
    agent any

    environment {
        ANSIBLE_CONFIG = '/var/lib/jenkins/workspace/terraform/ansible.cfg'
        ANSIBLE_SSH_ARGS = '-F /var/lib/jenkins/.ssh/config -o StrictHostKeyChecking=no'
        DIR_ANSIBLE_PROJECT = '/var/lib/jenkins/workspace/terraform'
    }

    stages {
        stage ('Testing spring application') {
            options {
                timeout(time: 10, unit: 'MINUTES')
            }
            steps {
                sh '''
                    ./mvnw test -Dspring.profiles.active=test
                '''
            }
        }
        stage ('Deploying the spring application') {
            steps {
                dir("${env.DIR_ANSIBLE_PROJECT}") {
                    sh '''
                        ./ansible_hosts.sh
                    '''
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