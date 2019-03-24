# Ecotourism

The API for ecotourism

## 목표

한국 관광공사에서 제공하는 생태 관광 정보를 활용해 기능 명세에 부합하는 API를 구현한다.

## 개발 프레임워크 및 라이브러리

### Spring Boot

DI 컨테이너와 Web MVC 사용을 위해 Spring Framework를 사용하고 있으며, 그 중에서도 Spring Boot를 활용하여 의존성 간의 안전성을 확보하고 생산성을 확보하였다.

Spring Framework 중에서도 다음과 같은 라인업이 각각의 목적을 위해 사용되었다:
* spring-boot-starter-web: DI 컨테이너 및 Web MVC
* spring-boot-starter-security: 웹 인증을 위한 보안 프레임워크
* spring-security-oauth2-autoconfigure: OAuth 2.0 지원 프로바이더
* spring-boot-starter-data-jpa: JPA 사용
* spring-boot-starter-test, spring-security-test: Spring 기반 테스트 지원(spring-security 테스트 포함)
* spring-restdocs-mockmvc: API 문서화

### QueryDSL

Join 쿼리 중에 발생할 수 있는 N+1 문제를 해결하고자 QueryDSL를 도입하였다.

### Spock Framework

기본 테스트 프레임워크로 사용하였으며 Spring MockMvc와도 연결하여 사용하였다.

### Flyway

데이터베이스 마이그레이션을 위해 Gradle 및 Spring과 연결하여 사용하였다.

### Gradle

빌드 툴로 Gradle 5.3을 사용하며, 기본 기능인 의존성 관리, 빌드는 물론 QueryDSL를 위한 Q클래스 자동 생성, 테스트 실행, Flyway 연동 등을 지원하도록 구성하였다.

## 문제 해결 전략

### 데이터베이스

기본 데이터베이스는 MySQL을 사용하도록 했으며, 테스트 코드 중 ```ApiDocumentationSpec```을 상속하는 테스트 코드를 통합 테스트로 규정하고 테스트 실행 시 H2를 사용하도록 구성하였다.

#### MySQL

개발 환경을 실제 운영 환경과 비슷한 환경(H2가 운영 환경에 사용될리는 없기 때문에)으로 만들고자 MySQL을 사용했다.

MySQL 내 데이터베이스는 다음과 같이 *utf8mb4* 캐릭터 셋과 *utf8mb4_general_ci* 컬레이션을 사용하도록 했다: 

```
mysql> CREATE DATABASE ecotourism CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci; 
``` 

계정 정보는 *src/main/resources/application.yml*을 참고한다.

#### 마이그레이션

데이터베이스 마이그레이션 관리를 위해 Flyway를 도입했으며 Gradle을 인터페이스로 하여 사용한다:

```
$ ./gradlew flywayMigrate
```

마이그레이션 SQL 파일은 *src/main/resources/db* 아래에 존재하며 하위 디렉토리인 *migration* 안에는 마이그레이션을 위한 DDL 쿼리 파일을 두었고, *data* 디렉토리 내에는 초기 데이터를 위한 DML 쿼리 파일을 두었다.

테스트 코드에 대한 마이그레이션 실행은 Gradle을 사용할 수 없으므로 Flyway API와 Spring Configuration을 활용하여 ```FlywayConfig``` 클래스에서 ```FlywayMigrationStrategy``` 타입의 Spring Bean을 생성해 클린업과 마이그레이션이 실행되도록 구성하였다.
그러므로, 테스트 실행 시 초기에 Flyway을 통한 마이그레이션이 수행된다.

#### H2

통합 테스트 환경에서 MySQL을 사용할 경우 데이터베이스의 상태나 초기 데이터에 대한 제어 등을 외부에 일임해야 하므로 테스트가 항상 제대로 동작하게 만들기 어렵다.
또한, CI/CD와 같은 도구에서는 외부 데이터베이스와의 연결이 어려우므로 H2를 사용하도록 하였다.

메모리 기반으로 동작시키고 MySQL과의 호환성을 확보하기 위해 다음과 같은 JDBC Url을 구성하였다:

```
jdbc:h2:mem:testdb;MODE=MySQL
```

### 데이터 액세스 레이어

INSERT, UPDATE 그리고 DELETE는 순수하게 spring-data-jpa를 사용하여 해결한다. 또, 엔티티가 간단하므로 직접 ```@Entity``` 클래스를 구현하였다.

SELECT 쿼리 중 Join이 필요한 부분에서 발생할 수 있는 N+1 문제를 해결하기 위해 QueryDSL을 사용하고 있다. QueryDSL의 QClass는 JPA ```@Entity``` 클래스를 기반으로 자동 생성한다. 자동 생성은 다음과 같은 Gradle task를 이용한다:

```
$ ./gradlew compileQuerydsl
```

위 compileQuerydsl task는 빌드 혹은 compileJava task 등이 실행될 때 하위 task로써 함께 실행되므로 직접할 필요는 없다. 참고로 compileQuerydsl과 같은 task는 [com.ewerk.gradle.plugins.querydsl](https://plugins.gradle.org/plugin/com.ewerk.gradle.plugins.querydsl)로부터 만들어진다.   

### OAuth2

웹 인증은 spring-security를 사용하고, OAuth2 지원을 위해 spring-security-oauth2를 사용 중이다.

편의성을 위해 authorization-server, resource-server를 함께 구현해두었으며, *auth.AccountService*가 *UserDetailsService*를 상속하여 계정 정보에 대한 데이터를 제공하도록 구성하였다.

계정 암호의 암복호화를 위해 ```BCryptPasswordEncoder```를 사용하도록 구성했으며, 해당 인코더의 Spring Bean을 authorization-server에서도 사용하도록 구성했다.

OAuth2 클라이언트의 경우, 데이터베이스에 초기값을 넣어두고 활용하는 방식을 택했으며, *client-id*는 **client**, *client-secret*은 **Y2xpZW50OnBhc3N3b3Jk**으로 설정된다.

생성된 OAuth2 토큰을 데이터베이스에 보관하기 위해 ```JdbcTokenStore```를 사용하도록 했다.

### 테스트 코드

테스트는 크게 단위 테스트와 통합 테스트로 구분하였으며, 단위 테스트의 경우 Spock에서 제공하는 ```Specification``` 클래스를 확장하고, 통합 테스트의 경우 ```Specification```을 확장한 ```ApiDocumentationSpec```을 확장하도록 했다.

```ApiDocumentationSpec```은 ```@SpringBootTest```로 애노테이션되어 있으며, Spring RestDocs 및 MockMvc와 관련된 설정들이 포함되어 있다. 그러므로 ```ApiDocumentationSpec```을 구현한 테스트 코드의 경우 실행 시 Spring 컨텍스트 내에서 실행되므로 Spring Bean을 주입받는 것이 가능해 Spring 기반으로 구현된 API 엔드포인트에 대한 테스트를 수행할 목적을 가지고 있다. 또, 정상 상태 실행 검증 시 API에 대한 문서화를 진행하도록 Spring RestDocs를 접속시켰다.

테스트 및 문서화를 동시에 진행하려면 다음 Gradle task를 실행하면 된다:

```
$ ./gradlew integrationTest
```

### 엔티티

생태 관광 정보 레코드는 생태 관광에 대한 안내 정보와 해당 관광의 지역 정보로 나눌 수 있다.

생태 관광 프로그램에 대한 엔티티는 다음과 같다:

```
@Entity
@Table(name="programs")
@Data public class Program {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;

    @NotNull
    private String name;

    @NotNull
    private String theme;

    @ManyToOne()
    @JoinColumn(name="region_id")
    private Region region;

    @NotNull
    private String regionDetails;

    private String intro;

    private String details;
}
```

프로그램 정보 엔티티는 지역 정보 엔티티와 N:1 관계를 맺고 있다:

```
@Entity
@Table(name="regions")
@Data public class Region {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;

    private String name;
}
```

원본 레코드로부터 프로그램 엔티티의 레코드를 만들어낼 때 지역 정보의 제일 앞부분이 주소 체계에서의 시/도 구분이라는 점과 그 다음에 오는 주소가 행정 구역(시/구/군 등)이라는 점을 이용해 추출하였다.

> 조금 더 시간적 여유가 있다면(즉, 이러한 업무가 실제로 필요하다면) term-tokenization 및 불용어를 통해 얻어낸 단어들을 행정안전부에서 제공하는 실제 주소 정보와 비교해 가장 근사한 지역을 찾는 로직을 구현할 수도 있을 것 같다.    


# 빌드 및 실행 방법

Gradle을 이용해 빌드가 가능하다:

```
$ ./gradlew build
``` 

Lombok과 QueryDSL을 사용 중이기 때문에, IntelliJ와 같은 IDE를 사용할 경우 **애노테이션 프로세싱(annotaiton processing)** 기능을 활성화해야 한다. IntelliJ의 경우, *Preferences | Build, Execution, Deployment | Compiler | Annotation Processors*에서 *Enable annotation processing*을 체크하면 활성화가 가능하다.

spring-boot Gradle 플러그인이 제공하는 *bootRun* task를 실해하면 애플리케이션을 시작할 수 있다:

```
$ ./gradlew bootRun
```

또한, Jar 파일을 만들려면 다음과 같이 *bootJar* task를 실행하면 된다:

```
$ ./gradlew bootJar
```

# API

## OAuth2

기본적으로 주어지는 OAuth2 클라이언트의 아이디와 시크릿은 각각 *client*와 *Y2xpZW50OnBhc3N3b3Jk다*.

### 계정 등록

계정을 등록하려면 계정 이름과 비밀번호가 필요하다:

```
POST /accounts/signup?username=<user_name>&password=<password>
```

계정 등록 시 바로 액세스 토큰이 발급된다:

```
{"access_token":"15110601-6d99-4035-9591-ff4d8f5c949b","token_type":"bearer","refresh_token":"c661416c-ac98-4292-9a96-5ff0942cdf63","expires_in":2999,"scope":"read"}
```

### OAuth2 액세스 토큰

등록된 계정이 있다면 액세스 토큰을 발급할 수 있다:

```
POST /oauth/token?client_id=<client_id>&username=<user_name>&password=<password>&grant_type=password&scope=read
Authorization: Basic <client_secret>
Content-Type: application/x-www-form-urlencoded
```

### OAuth2 리프레쉬 토큰

```
POST /oauth/token?client_id=<client_id>&username=<user_name>&password=<password>&grant_type=refresh_token
Authorization: Basic <client_secret>
Content-Type: application/x-www-form-urlencoded
```

## 생태 관광 정보

### 데이터 파일의 레코드를 데이터베이스에 저장

> 데이터 파일은 *src/main/resources/data.csv*에 위치하고 있다. 

```
POST /ecotourism/programs/regions
Authorization: Bearer <access_token>
```

### 생태 관광 정보의 조회/추가/수정

지역 아이디를 이용한 조회:

```
GET /ecotourism/programs/regions/{regionId}
Authorization: Bearer <access_token>
Accept: application/json
```

추가:

```
POST /ecotourism/programs
Authorization: Bearer <access_token>
Content-type: application/json
Accept: application/json
{
  "name": "<프로그램 이름>",
  "theme": "<테마>",
  "region_details": "<지역>",
  "intro": "<프로그램 소개>",
  "details": "<프로그램 상세 정보>"
}
```

수정:

```
PUT /ecotourism/programs
Authorization: Bearer <access_token>
Content-type: application/json
Accept: application/json
{
  "id": "<프로그램 아이디>",
  "name": "<프로그램 이름>",
  "theme": "<테마>",
  "region_details": "<지역>",
  "intro": "<프로그램 소개>",
  "details": "<프로그램 상세 정보>"
}
```

### 특정 지역에서 진행되는 프로그램명과 테마 출력

```
PUT /ecotourism/programs/regions
Authorization: Bearer <access_token>
Content-type: application/json
Accept: application/json
{
  "regoin": "<지역>"
}
```

### "프로그램 소개"에 주어진 키워드가 포함된 프로그램의 지역별 개수

```
POST /ecotourism/programs/numbers
Authorization: Bearer <access_token>
Content-type: application/json
Accept: application/json
{
  "keyword": "<키워드>"
}
``` 

### 전체 프로그램에서 주어진 키워드가 출현하는 빈도수

```
POST /ecotourism/programs/tf
Authorization: Bearer <access_token>
Content-type: application/json
Accept: application/json
{
  "keyword": "<키워드>"
}
``` 

### 지역명과 관광 키워드를 활용한 추천

```
POST /ecotourism/programs/recommendation
Authorization: Bearer <access_token>
Content-type: application/json
Accept: application/json
{
  "region": "<지역>",
  "keyword": "<키워드">
}
```
