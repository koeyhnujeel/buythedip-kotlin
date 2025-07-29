plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	id("org.springframework.boot") version "3.5.4"
	id("io.spring.dependency-management") version "1.1.7"
	id("org.asciidoctor.jvm.convert") version "3.3.2"
	kotlin("plugin.jpa") version "1.9.25"
}

group = "com.zunza"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

repositories {
	mavenCentral()
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

val snippetsDir by extra { file("build/generated-snippets") }

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
	create("asciidoctorExt")
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-data-redis")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")

	// Kotest
	testImplementation("io.kotest:kotest-runner-junit5-jvm:5.9.1")
	testImplementation("io.kotest:kotest-assertions-core-jvm:5.9.1")
	implementation("io.kotest.extensions:kotest-extensions-spring:1.3.0")

	// MockK
	testImplementation("io.mockk:mockk-jvm:1.14.5")
	testImplementation("com.ninja-squad:springmockk:4.0.2")

	// Asciidoctor
	"asciidoctorExt"("org.springframework.restdocs:spring-restdocs-asciidoctor")

	runtimeOnly("com.mysql:mysql-connector-j")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
	testImplementation("org.springframework.security:spring-security-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

allOpen {
	annotation("jakarta.persistence.Entity")
	annotation("jakarta.persistence.MappedSuperclass")
	annotation("jakarta.persistence.Embeddable")
}

tasks.withType<Test> {
	useJUnitPlatform()
	outputs.dir(project.extra["snippetsDir"]!!)

	doFirst {
		delete(project.extra["snippetsDir"]!!)
	}
}

// Asciidoctor 태스크 설정
tasks.asciidoctor {
	inputs.dir(project.extra["snippetsDir"]!!)
	dependsOn(tasks.test)
	configurations("asciidoctorExt")
	baseDirFollowsSourceDir()

	doFirst {
		delete("src/main/resources/static/docs")
	}

	doLast {
		copy {
			from("build/docs/asciidoc")
			into("src/main/resources/static/docs")
		}
	}
}

tasks.bootJar {
	dependsOn(tasks.asciidoctor)
	from("${tasks.asciidoctor.get().outputDir}") {
		into("static/docs")
	}
}

tasks.build {
	dependsOn(tasks.asciidoctor)
}
