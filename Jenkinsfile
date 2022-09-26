pipeline {         
        agent none
        stages { 
	   stage('Gradle build'){
		agent any
		steps{
			dir('Backend/assemble_Server') {sh 'chmod +x ./gradlew'}
			dir('Backend/assemble_Server') {sh './gradlew clean build'}
		}
	   }             
                stage('Docker build') {
                        agent any
                        steps {                                                     
                                sh 'docker build -t backimg .Backend/assemble_Server'
                        }
                }
                stage('Docker run') {
                        agent any
                        steps {
                                sh 'docker ps -f name=back -q \
                                        | xargs --no-run-if-empty docker container stop'

                                sh 'docker container ls -a -f name=back -q \
                                        | xargs -r docker container rm'

                                sh 'docker run -d --name back -p 8090:8090 backimg'
                        }
                }
        }

}
