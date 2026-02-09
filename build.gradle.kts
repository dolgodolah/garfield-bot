plugins {
	kotlin("jvm") version "2.2.21"
	kotlin("plugin.spring") version "2.2.21"
	id("org.springframework.boot") version "4.0.2"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.garfield"
version = "0.0.1-SNAPSHOT"
description = "garfield"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-webmvc")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("tools.jackson.module:jackson-module-kotlin")
	implementation("org.jsoup:jsoup:1.18.3")

	val slackSdkVersion = "1.47.0"
	implementation("com.slack.api:bolt-socket-mode:$slackSdkVersion")
	implementation("javax.websocket:javax.websocket-api:1.1")
	implementation("org.glassfish.tyrus.bundles:tyrus-standalone-client:1.20")

	val jdaVersion = "5.0.0"
	implementation("net.dv8tion:JDA:$jdaVersion")

	testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
