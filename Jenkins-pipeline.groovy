pipeline{
    agent any
    tools{
        jdk 'java17'
        nodejs 'node16'
    }
    environment {
        SCANNER_HOME=tool 'sonar-scanner'
    }
    stages {
        stage('clean workspace'){
            steps{
                cleanWs()
            }
        }
        stage('Checkout from Git'){
            steps{
                git branch: 'main', url: 'https://github.com/Rahul8160/The-Wild-Oasis.git'
            }
        }
        stage("Sonarqube Analysis "){
            steps{
                withSonarQubeEnv('sonar-server') {
                    sh ''' $SCANNER_HOME/bin/sonar-scanner -Dsonar.projectName=TheWildOasis \
                    -Dsonar.projectKey=TheWildOasis '''
                }
            }
        }
        stage("quality gate"){
           steps {
                script {
                    waitForQualityGate abortPipeline: false, credentialsId: 'sonar-token' 
                }
            } 
        }
        stage('Install Dependencies') {
            steps {
                sh "npm install"
            }
        }
        stage('OWASP FS SCAN') {
            steps {
                dependencyCheck additionalArguments: '--scan ./ --disableYarnAudit --disableNodeAudit', odcInstallation: 'DC'
                dependencyCheckPublisher pattern: '**/dependency-check-report.xml'
            }
        }
        stage('TRIVY FS SCAN') {
            steps {
                sh "trivy fs . > trivyfs.txt"
            }
        }
        stage('docker build and push'){
            steps{
                script{
                    withDockerRegistry(credentialsId: 'docker', toolName: 'docker') 
                    {
                        sh ''' 
                        docker build -t thewiloasis .
                        docker tag thewiloasis:latest rudraksh69/thewiloasis:latest
                        docker push rudraksh69/thewiloasis:latest '''
                    }
                }
            }
        }
        stage("Trivy image scan"){
            steps{
                sh 'trivy image rudraksh69/thewiloasis:latest > trivyimagereport.txt'
            }
        }

        stage('Deploy to container'){
            steps{
                sh 'docker run -d --name thewild -p 3000:3000 rudraksh69/thewiloasis:latest'
            }
        }
        stage('Deploy to kubernets'){
            steps{
                script{
                    withKubeConfig(caCertificate: '', clusterName: '', contextName: '', credentialsId: 'k8s', namespace: 'thewildoasis', restrictKubeConfigAccess: false, serverUrl: '') {
                       sh '''kubectl apply -f deployment.yml \
                             -f Service.yml \
                             -f Secrets.yml \
                              -f ConfigMap.yml'''
                  }
                }
            }
        }





    }
}
