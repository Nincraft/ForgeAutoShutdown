node {
   // Mark the code checkout 'stage'....
   stage 'Checkout'

   // Get some code from a GitHub repository
   git url: 'https://github.com/Nincraft/ForgeAutoShutdown.git'

   stage 'Build'
   // Run the gradle build
   bat 'gradlew.bat clean setupCIWorkspace build'
   
   stage 'Archive'
   archive includes: 'build/libs/*.jar'
}
