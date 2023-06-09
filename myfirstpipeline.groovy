pipeline {
    agent any

    stages {
        stage('Secret Scanning') {
            steps {
                // Pull Git repository
                git branch: 'main', url: 'https://github.com/agrawalsmart7/jenkins_project.git'
                sh 'echo Git Pulled successfully, now running trufflehog'
                sh 'trufflehog filesystem --directory=$WORKSPACE'
                findText(regexp: 'Found unverified result', alsoCheckConsoleOutput: true, buildResult: 'UNSTABLE')
            }
        }

        stage('Dependencies Scanning') {
            when {
                expression { currentBuild.previousBuild.result == 'SUCCESS' }
            }
            steps {
                git branch: 'main', url: 'https://github.com/agrawalsmart7/jenkins_project.git'
                echo 'Performing dependency check...'
                bat "C:\\Users\\shubh\\.jenkins\\tools\\org.jenkinsci.plugins.DependencyCheck.tools.DependencyCheckInstallation\\Depend_Check\\bin\\dependency-check.bat --scan $WORKSPACE -o results.html"

                script {
                    def validateCmd = "python C:\\Users\\shubh\\.jenkins\\tools\\org.jenkinsci.plugins.DependencyCheck.tools.DependencyCheckInstallation\\Depend_Check\\bin\\dependency_check_validate.py results.html"
                    def exitCode = bat returnStatus: true, script: validateCmd
                    
                    if (exitCode == 0) {
                        echo "Dependency check passed. Marking the build as SUCCESS."
                        currentBuild.result = 'SUCCESS'
                    } else {
                        echo "Dependency check failed. Marking the build as FAILURE."
                        currentBuild.result = 'FAILURE'
                    }
                }

                // Add any additional steps here
            }

        }

        stage('Static Analysis Check'){
           when {
                expression { currentBuild.previousBuild.result == 'SUCCESS' }
            } 
            steps{
                // git branch: 'main', url:-*'https://github.com/agrawalsmart7/jenkins_project.git'
                echo 'Performing Static Analysis of Source Code...'

                script {
                // Define a variable in the 'Staging Deployment' stage
                    env.STAGING_URL = 'http://testphp.vulnweb.com/artists.php?artist=1'
                }
                
                // sh ""
                // findText(regexp: 'Found unverified result', alsoCheckConsoleOutput: true, buildResult: 'UNSTABLE')
            }
        }

        stage('Staging Deployment'){
           when {
                expression { currentBuild.previousBuild.result == 'SUCCESS' }
            } 
            steps{
                // git branch: 'main', url:-*'https://github.com/agrawalsmart7/jenkins_project.git'
                echo 'Deploying Code on Staging EC2 Instance...'

                // sh ""
                // findText(regexp: 'Found unverified result', alsoCheckConsoleOutput: true, buildResult: 'UNSTABLE')
            }
        }

        stage('Dynamic Analysis - Nuclei Check'){
           when {
                expression { currentBuild.previousBuild.result == 'SUCCESS' }
            } 
            steps{
                // git branch: 'main', url:-*'https://github.com/agrawalsmart7/jenkins_project.git'
                echo 'Performing Nuclei Check...'
                
                script{
                    def stagingurl = env.STAGING_URL
                    bat "nuclei -u $stagingurl"
                    env.ANSWER = input message: 'Do you want to pass the "Dynamic Analysis - Nuclei Check?"', parameters:[choice(name:'NAME1', choices:'Pass\nFail', description:'Select the Build Results')]
                    
                    // Check user input and set the build result accordingly
                    if (env.ANSWER == 'Pass') {
                        currentBuild.result = 'SUCCESS'
                    } else {
                        currentBuild.result = 'FAILURE'
                    }
                    
                    // Check user input and set the build result accordingly
                }
                // findText(regexp: 'Found unverified result', alsoCheckConsoleOutput: true, buildResult: 'UNSTABLE')
            }
        }

        stage('Dynamic Analysis - BurpSuite Check') {
            when {
                expression { currentBuild.previousBuild.result == 'SUCCESS' }
            }
            steps {
                // Perform the steps for the next stage
                echo 'This stage will only execute if the previous stage passed.'
                echo 'Performing Burp Suite Check'
            }
        }

        stage('Prod Deploy on EC2') {
            when {
                expression { currentBuild.previousBuild.result == 'SUCCESS' }
            }
            steps {
                // Perform the steps for the next stage
                echo 'Deploying Code on EC2'
            }
        }
    }

    post {
        unstable {
            always {
                script {
                    sh 'echo Sending files to Slack...'
                    // Add code to send files to Slack here
                }
            }
        }
    }
}
