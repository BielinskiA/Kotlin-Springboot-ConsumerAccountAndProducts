plugins {
	id("org.springframework.boot") version "3.3.0"
	id("io.spring.dependency-management") version "1.1.5"
	kotlin("jvm") version "1.9.24"
	kotlin("plugin.spring") version "1.9.24"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation ("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation ("com.h2database:h2")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-security")
	testImplementation("org.springframework.boot:spring-boot-starter-test")

	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

	implementation("org.mariadb.jdbc:mariadb-java-client:3.3.1")

	implementation("io.jsonwebtoken:jjwt-api:0.11.5")
	runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
	runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")

	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.exposed:exposed-core:0.51.1")
	implementation("org.jetbrains.exposed:exposed-crypt:0.51.1")
	implementation("org.jetbrains.exposed:exposed-dao:0.51.1")
	implementation("org.jetbrains.exposed:exposed-jdbc:0.51.1")
	implementation("org.jetbrains.exposed:exposed-kotlin-datetime:0.51.1")
	implementation("org.jetbrains.exposed:exposed-json:0.51.1")
	implementation("org.jetbrains.exposed:exposed-money:0.51.1")
	implementation("org.jetbrains.exposed:exposed-spring-boot-starter:0.51.1")

	implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-csv")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
