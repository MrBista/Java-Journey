main materi: https://docs.google.com/presentation/d/1s-LEPwfLfOVLcfcbtlGQAk1W1gwhUeVgilX-0-g97U4/edit?slide=id.p#slide=id.p



# Panduan Lengkap Apache Maven untuk Pemula

Halo! Saya akan mengajarkan Apache Maven secara detail dari dasar. Mari kita mulai dari konsep fundamental.

## Apa itu Apache Maven?

Maven adalah **build automation tool** dan **project management tool** untuk project Java. Bayangkan Maven seperti asisten yang membantu kamu:
- Mengelola library/dependency yang dibutuhkan project
- Mengkompilasi kode Java
- Menjalankan testing
- Membuat file JAR/WAR untuk deployment
- Mengelola struktur project secara konsisten

## Mengapa Maven Penting?

Sebelum Maven, developer Java harus:
- Download library secara manual
- Mengelola classpath sendiri
- Membuat build script sendiri

Dengan Maven, semua ini otomatis!

## Instalasi Maven

**Cek apakah Maven sudah terinstall:**
```bash
mvn -version
```

Jika belum terinstall, download dari https://maven.apache.org dan setup environment variable `MAVEN_HOME`.

## Struktur Project Maven

Maven menggunakan struktur direktori standar:

```
my-project/
├── pom.xml                    (file konfigurasi utama)
├── src/
│   ├── main/
│   │   ├── java/             (kode Java aplikasi)
│   │   └── resources/        (file konfigurasi, properties)
│   └── test/
│       ├── java/             (kode testing)
│       └── resources/        (resource untuk testing)
└── target/                   (hasil kompilasi, jar, dll)
```

## File POM.xml - Jantung Maven

POM (Project Object Model) adalah file XML yang berisi semua konfigurasi project. Ini contoh POM sederhana:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <!-- Identitas Project -->
    <groupId>com.belajar</groupId>
    <artifactId>aplikasi-saya</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>
    
    <name>Aplikasi Belajar</name>
    <description>Project untuk belajar Maven</description>
    
    <!-- Properties -->
    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>
    
    <!-- Dependencies -->
    <dependencies>
        <!-- Contoh dependency -->
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.13.2</version>
            <scope>test</scope>
        </dependency>
    </dependencies>
</project>
```

### Penjelasan Elemen POM:

**1. GAV Coordinates (GroupId, ArtifactId, Version)**
- `groupId`: Identitas organisasi/grup (biasanya domain terbalik)
- `artifactId`: Nama project
- `version`: Versi project
- `packaging`: Format output (jar, war, pom, dll)

**2. Properties**
Variabel yang bisa digunakan di seluruh POM, seperti versi Java yang digunakan.

**3. Dependencies**
Library eksternal yang dibutuhkan project kamu.

## Maven Dependencies - Mengelola Library

Ini salah satu fitur paling powerful Maven. Contoh menambahkan dependency:

```xml
<dependencies>
    <!-- Spring Boot -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
        <version>3.2.0</version>
    </dependency>
    
    <!-- MySQL Driver -->
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
        <version>8.0.33</version>
    </dependency>
    
    <!-- Lombok (hanya saat kompilasi) -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <version>1.18.30</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

### Dependency Scopes:

- `compile` (default): Tersedia di semua fase (kompilasi, testing, runtime)
- `test`: Hanya untuk testing
- `provided`: Disediakan oleh JDK atau container (tidak di-package ke JAR)
- `runtime`: Hanya saat runtime, tidak saat kompilasi
- `system`: Mirip provided, tapi harus specify path manual (jarang dipakai)

### Transitive Dependencies

Jika kamu tambahkan Spring Boot, Maven otomatis download semua library yang dibutuhkan Spring Boot. Ini disebut transitive dependencies.

## Maven Build Lifecycle

Maven punya 3 built-in lifecycle:

### 1. Default Lifecycle (untuk build project)
Fase-fase penting:
- `validate`: Validasi project sudah benar
- `compile`: Kompilasi source code
- `test`: Jalankan unit test
- `package`: Buat JAR/WAR
- `verify`: Jalankan integration test
- `install`: Install ke local repository
- `deploy`: Deploy ke remote repository

### 2. Clean Lifecycle
- `clean`: Hapus folder target dan hasil build sebelumnya

### 3. Site Lifecycle
- `site`: Generate dokumentasi project

## Command Maven yang Sering Dipakai

```bash
# Membuat project baru dari archetype
mvn archetype:generate -DgroupId=com.belajar -DartifactId=my-app

# Membersihkan project
mvn clean

# Kompilasi kode
mvn compile

# Jalankan test
mvn test

# Buat package (JAR/WAR)
mvn package

# Install ke local repository
mvn install

# Kombinasi (yang paling sering dipakai)
mvn clean install
mvn clean package
mvn clean test

# Skip test
mvn clean install -DskipTests

# Update dependencies
mvn clean install -U

# Lihat dependency tree
mvn dependency:tree

# Lihat versi terbaru dependency
mvn versions:display-dependency-updates
```

## Maven Repository

Maven download dependencies dari repository:

**1. Local Repository**
Lokasi: `~/.m2/repository/` (di komputer kamu)
Semua dependency yang pernah di-download disimpan di sini.

**2. Central Repository**
Repository publik Maven: https://repo.maven.apache.org/maven2/
Di sinilah Maven cari dependency secara default.

**3. Remote Repository**
Repository private perusahaan atau third-party seperti Nexus atau Artifactory.

### Konfigurasi Custom Repository

```xml
<repositories>
    <repository>
        <id>spring-milestones</id>
        <name>Spring Milestones</name>
        <url>https://repo.spring.io/milestone</url>
    </repository>
</repositories>
```

## Maven Plugins

Plugin menambahkan functionality ke Maven. Contoh plugin umum:

```xml
<build>
    <plugins>
        <!-- Compiler Plugin -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.11.0</version>
            <configuration>
                <source>17</source>
                <target>17</target>
            </configuration>
        </plugin>
        
        <!-- Surefire Plugin (untuk testing) -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-surefire-plugin</artifactId>
            <version>3.0.0</version>
        </plugin>
        
        <!-- JAR Plugin (untuk create executable JAR) -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-jar-plugin</artifactId>
            <version>3.3.0</version>
            <configuration>
                <archive>
                    <manifest>
                        <mainClass>com.belajar.Main</mainClass>
                    </manifest>
                </archive>
            </configuration>
        </plugin>
    </plugins>
</build>
```

## Multi-Module Project

Untuk project besar, kamu bisa buat multi-module:

```
parent-project/
├── pom.xml (parent)
├── module-core/
│   └── pom.xml
├── module-web/
│   └── pom.xml
└── module-api/
    └── pom.xml
```

**Parent POM:**
```xml
<packaging>pom</packaging>

<modules>
    <module>module-core</module>
    <module>module-web</module>
    <module>module-api</module>
</modules>
```

**Child POM:**
```xml
<parent>
    <groupId>com.belajar</groupId>
    <artifactId>parent-project</artifactId>
    <version>1.0.0</version>
</parent>

<artifactId>module-core</artifactId>
```

## Dependency Management

Untuk manage versi dependency di multi-module project:

```xml
<!-- Di Parent POM -->
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-dependencies</artifactId>
            <version>3.2.0</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

Dengan ini, child module tidak perlu specify versi:

```xml
<!-- Di Child POM -->
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
        <!-- Tidak perlu versi, ambil dari parent -->
    </dependency>
</dependencies>
```

## Tips Best Practices

1. **Selalu gunakan version untuk dependencies** (kecuali managed di dependencyManagement)
2. **Gunakan properties untuk versi** yang sering dipakai
3. **Regularly update dependencies** dengan `mvn versions:display-dependency-updates`
4. **Gunakan `.gitignore`** untuk folder `target/` dan `.idea/`
5. **Organize dependencies** berdasarkan kategori dengan comments
6. **Hindari SNAPSHOT versions** di production

## Contoh Project Lengkap

Saya bisa buatkan contoh project Maven sederhana kalau kamu mau. Project tersebut bisa include:
- Struktur direktori yang benar
- POM.xml dengan beberapa dependencies umum
- Contoh class Java sederhana
- Unit test

Apakah ada bagian spesifik dari Maven yang ingin kamu pelajari lebih dalam? Misalnya tentang plugin tertentu, Spring Boot dengan Maven, atau cara handle dependency conflicts?