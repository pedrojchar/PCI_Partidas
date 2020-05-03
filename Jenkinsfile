// Archivo Jenkinsfile para proyecto Java
//
// by: Jose Ortiz

def PROJECT_ID = "mbaas-desarrollo-229516"
def PROJECT_ID_QA = "mbaas-laboratorio"
def PROJECT_ID_PROD = "mbaas-produccion"
def JENKINS_MAIL = "jenkins@hugogiraldodevops.com"
def MAIL_CI = "combaasdesaci@davivienda.com"
def MAIL_CD_DEV = "combaasdesacd@davivienda.com"
def MAIL_CD_LAB = "combaaslabcd@davivienda.com"
def MAIL_CD_PROD = "cloudybancamovil@davivienda.com"
def imageTag = "gcr.io/${PROJECT_ID}/${JOB_NAME}:${env.BUILD_NUMBER}"

pipeline {
  options {
    timeout (time: 20, unit:"MINUTES")
  }
  agent {
    kubernetes {
      label "consultacliente-negocio-service"
      defaultContainer "jnlp"
      yaml """
apiVersion: v1
kind: Pod
metadata:
  labels:
    component: ci
spec:
  serviceAccount: cd-jenkins
  volumes:
  - name: dockersock
    hostPath:
      path: "/var/run/docker.sock"
  - name: docker
    hostPath:
      path: "/usr/bin/docker"
  - name: google-cloud-key
    secret:
      secretName: registry-jenkins
  containers:
  - name: gcloud
    image: gcr.io/cloud-builders/gcloud
    volumeMounts:
    - name: google-cloud-key
      readOnly: true
      mountPath: "/var/secrets/google"
    - name: docker
      mountPath: "/usr/bin/docker"
    - name: dockersock
      mountPath: "/var/run/docker.sock"
    command:
    - cat
    env:
    - name: GOOGLE_APPLICATION_CREDENTIALS
      value: /var/secrets/google/key.json
    tty: true
  - name: java
    image: maven:3.5-jdk-8-alpine
    env:
    - name: NO_PROXY
      value: "localhost, 0.0.0.0/4201, 0.0.0.0/9876"
    - name: CHROME_BIN
      value: /usr/bin/chromium-browser
    command:
    - cat
    tty: true
  - name: kubectl
    image: gcr.io/cloud-builders/kubectl
    volumeMounts:
    - name: google-cloud-key
      readOnly: true
      mountPath: "/var/secrets/google"
    command:
    - cat
    env:
    - name: GOOGLE_APPLICATION_CREDENTIALS
      value: /var/secrets/google/key.json
    tty: true
  - name: docker
    image: docker:17
    volumeMounts:
    - name: docker
      mountPath: "/usr/bin/docker"
    - name: dockersock
      mountPath: "/var/run/docker.sock"
    command:
    - cat
    tty: true
"""
    }
  }
  environment {
    COMMITTER_EMAIL = sh (script: "git --no-pager show -s --format=\'%ae\'", returnStdout: true).trim()
  }
  stages {
    stage("Initialize") {
      steps {
        container('gcloud') {
          slackSend (color: '#FFFF00', message: "STARTED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL})")
          sh 'gcloud auth activate-service-account --key-file=$GOOGLE_APPLICATION_CREDENTIALS'
          sh "gcloud config set project ${PROJECT_ID}"
        }
        container("java") {
          sh "mvn -v"
        }
      }
    }
    stage("Build") {
      steps {
        container("java") {
          sh "mvn install"

        }
      }
    }
    stage("Test") {
      steps {
        container("java") {
          echo "--------------------------"
          echo 'Estructura del directorio'
          sh 'ls'
          echo '--------------------------'
          sh 'mvn test'
        }
      }
    }
    stage ("Build Project") {
      stages {
        stage ('build develop') {
          when { branch 'develop'}
          steps{
            container("java") {
              sh "mvn clean verify"
            }
          }
        }
        stage ('build qa'){
          when { branch 'qa'}
          steps{
            container("java") {
              sh "mvn clean verify"
            }
          }
        }
        stage ('build production'){
          when { branch 'master'}
          steps {
            container("java") {
              echo "building master"
              sh "mvn clean verify"
            }
          }
        }
      }
    }
    stage ("Build-Image") {
      steps {
        container("docker"){
          sh "docker build --tag=${JOB_NAME}:${BUILD_NUMBER} ."
          sh "docker images"
        }
      }
    }
    stage("Publish-Image") {
      steps {
        container("docker") {
          sh "docker tag ${JOB_NAME}:${BUILD_NUMBER} gcr.io/${PROJECT_ID}/${JOB_NAME}:${BUILD_NUMBER}"
        }
        container("gcloud") {
          sh "gcloud docker -- push gcr.io/${PROJECT_ID}/${JOB_NAME}:${BUILD_NUMBER}"
        }
      }
    }
    stage("Publish-Report") {
      steps {
        echo "-------------------------"
        echo "Estructura del directorio"
        sh "ls"
        echo '-----------------------'
        // sh 'ls ./test'
        // echo '-----------------------'
        // sh 'ls ./test/coverage'
        // echo "-------------------------"
        // publishHTML(target: [
        //   allowMissing: false,
        //   alwaysLinkToLastBuild: false,
        //   keepAll: true,
        //   reportDir: './coverage/',
        //   reportFiles: 'index.html, lint.html',
        //   reportName: 'CI Report',
        //   reportTitles:''
        // ])
      }
    }
    stage("Deploy") {
      // Developer Branches
      when { branch 'develop' }
      steps {
        echo "COMIENZA EL DESPLIEGUE"
        container('kubectl') {
          sh 'gcloud auth activate-service-account --key-file=$GOOGLE_APPLICATION_CREDENTIALS'
          sh "gcloud config set project ${PROJECT_ID}"
          sh "gcloud container clusters get-credentials k8s-development --zone us-central1-a --project ${PROJECT_ID}"
          withCredentials([usernamePassword(credentialsId: 'Jenkins-GitLab', passwordVariable: 'password', usernameVariable: 'username')]) {
            sh "git clone https://$username:$password@git.desa.co.davivienda.com/mbaas/despliegues.git"
          }
          sh "sed -i.bak 's#gcr.io/mbaas-desarrollo/consultacliente-negocio-service/develop#${imageTag}#' ./despliegues/co/develop/consultacliente-negocio-service/deployment.yaml"
          sh 'kubectl apply -f ./despliegues/co/develop/consultacliente-negocio-service/deployment.yaml'
        }
        // mail from: "${JENKINS_MAIL}",
        //      to: "${MAIL_CD_DEV}",
        //      subject: "Despliegue realizado en Ambiente Desarrollo ${JOB_NAME}-${BUILD_DISPLAY_NAME}",
        //      body: "Se ha realizado despliegue automatico en el ambiente de desarrollo."
      }
    }
    // stage("Aprobacion QA") {
    //   when { branch 'qa'}
    //   steps {
    //     mail from: "${JENKINS_MAIL}",
    //         //  to: "${MAIL_CD_LAB}",
    //          to: "esteban.rodriguez@servinformacion.com",
    //          subject: "Aprobacion Requerida ${JOB_NAME}-${BUILD_DISPLAY_NAME}",
    //          body: "Se ha realizado la construccion de ${JOB_NAME}, se requiere su aprobacion para desplegar en ambiente de QA, para aprobar ${BUILD_URL}"
    //       timeout(time:5, unit:'DAYS'){
    //         input message: 'Aprueba Despliegue Ambiente QA?',
    //         submitter: 'DevOps'
    //       }
    //   }
    // }

    stage('Deploy CAM'){
      when { branch 'develop' }
      steps{
        container('kubectl'){
          sh 'gcloud auth activate-service-account --key-file=$GOOGLE_APPLICATION_CREDENTIALS'
          sh "gcloud config set project mbaascam-desarrollo"
          sh "gcloud container clusters get-credentials o3p-mbaascamk8spriv-d01 --zone us-central1-a --project mbaascam-desarrollo"
          // withCredentials([usernamePassword(credentialsId: 'Jenkins-GitLab', passwordVariable: 'password', usernameVariable: 'username')]) {
          //   sh "git clone https://$username:$password@git.desa.co.davivienda.com/mbaas/despliegues.git"
          // }
          sh "sed -i.bak 's#gcr.io/mbaas-desarrollo/consultacliente-negocio-service/develop#${imageTag}#' ./despliegues/cam/develop/consultacliente-negocio-service/deployment.yaml"
          sh 'kubectl apply -f ./despliegues/cam/develop/consultacliente-negocio-service/deployment.yaml'
        }
        script {
          try {
            //SendEmail("Se ha realizado despliegue automatico en el ambiente de desarrollo.")
          }
          catch (exec) {
            echo 'Problema al enviar el correo'
          }
        }
      }
    }


    stage("Deploy QA") {
      when { branch 'qa'}
      steps {
        // Deploy en bucket
        container('kubectl') {
          sh 'gcloud auth activate-service-account --key-file=$GOOGLE_APPLICATION_CREDENTIALS'
          sh "gcloud config set project ${PROJECT_ID}"
          sh "gcloud container clusters get-credentials k8s-private-qa --zone us-central1-a --project mbaas-laboratorio"
          withCredentials([usernamePassword(credentialsId: 'Jenkins-GitLab', passwordVariable: 'password', usernameVariable: 'username')]) {
            sh "git clone https://$username:$password@git.desa.co.davivienda.com/mbaas/despliegues.git"
          }
          sh "sed -i.bak 's#gcr.io/mbaas-desarrollo/consultacliente-negocio-service/qa#${imageTag}#' ./despliegues/co/qa/consultacliente-negocio-service/deployment.yaml"
          sh 'kubectl apply -f ./despliegues/co/qa/consultacliente-negocio-service/deployment.yaml'
        }
      }
    }
    stage ('Aprobacion Produccion') {
      when {
        branch 'master'
      }
      steps {
        timeout(time:5, unit:'DAYS'){
          input message: 'Aprueba Despliegue Ambiente Produccion?',
          submitter: 'pejarami@davivienda.com,searias@davivienda.com'
        }
      }
    }
    stage ('Despliegue Producción') {
      when {
        branch 'master'
      }
      steps {
        container ('kubectl') {
         sh 'gcloud auth activate-service-account --key-file=$GOOGLE_APPLICATION_CREDENTIALS'
         sh "gcloud config set project ${PROJECT_ID_PROD}"
         sh "gcloud container clusters get-credentials o3p-mbaask8spriv-p01 --region us-central1 --project ${PROJECT_ID_PROD}"
         withCredentials([usernamePassword(credentialsId: 'Jenkins-GitLab', passwordVariable: 'password', usernameVariable: 'username')]) {
           sh "git clone https://$username:$password@git.desa.co.davivienda.com/mbaas/dr-mbaas.git"
         }
         sh "sed -i.bak 's#gcr.io/mbaas-desarrollo/consultacliente-negocio-service/master#${imageTag}#' ./dr-mbaas/k8s/consultacliente-negocio-service/deployment.yaml"
         sh 'kubectl apply -f ./dr-mbaas/k8s/consultacliente-negocio-service/deployment.yaml'
        }
      }
    }
  }
  post {
    always {
      echo "Pipeline currentResult: ${currentBuild.currentResult}"
      echo "Pipeline Finalizado"
      slackSend (color: 'good', message: "DONE: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL})")
    }
    aborted {
      echo "Pipeline Abortado"
      //SendEmail("El Pipeline fue abortado, por favor revisar la informacion en ${BUILD_URL}console")
    }
    failure {
      echo "Pipeline Fallido"
      //SendEmail("Ocurrio un fallo en la ejecucion de Pipeline ${JOB_NAME}-${BUILD_DISPLAY_NAME}, por favor revisar la informacion en ${BUILD_URL}")
    }
    success {
      echo "Pipeline Exitoso!!"
      script {
        if (env.BRANCH_NAME == 'qa') {
          build(
            job: '/MBaaSCAM/MBaaSCAM-consultacliente-negocio-service',
            parameters: [string(name: 'imageTag', value: imageTag)]
          ) 
        } else {
          echo 'DONE!'
        }
      }
      //SendEmail("El Pipeline ${JOB_NAME}-${BUILD_DISPLAY_NAME} ha finalizado exitosamente. para mas informacion ver ${BUILD_URL}")
    }
  }
}

def SendEmail(body) {
  script {
    try {
        mail from: 'jenkins@hugogiraldodevops.com',
            to: "${COMMITTER_EMAIL}",
            subject: "Pipeline ${JOB_NAME}-${BUILD_DISPLAY_NAME}",
            body: body
    }
    catch (exc) {
            echo 'Problema al enviar el correo'
    }
  }
}
