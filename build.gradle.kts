plugins {
    kotlin("multiplatform") version "1.8.0"
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}


kotlin {
   jvm {}
   js(IR) {
       moduleName = "sudoku-generator"
       browser()
       binaries.library()
   }
   sourceSets {
       val commonMain by getting {
           dependencies {
               // common dependencies
           }
       }
       val commonTest by getting
       val jvmMain by getting
       val jvmTest by getting
       val jsMain by getting
       val jsTest by getting
   }
}
