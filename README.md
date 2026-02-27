# Feign Composite Request 

Feign Composite Request allows you to define a single request object whose fields are automatically mapped to:

* HTTP headers
* Query parameters
* Request body

Instead of declaring headers, parameters, and body separately in a Feign client method, you define them once in a POJO and pass that object as a single argument.

This keeps client interfaces clean while preserving explicit HTTP structure.

## ⚠️ Experimental Project

* This project is experimental.
* APIs may change
* Edge cases may exist
* Backward compatibility is not guaranteed

**Do NOT use** this project for requests containing sensitive data
(e.g. authentication tokens, credentials, financial data).


## The Problem

#### Spring Cloud OpenFeign requires request concerns to be declared separately:

* headers → `@RequestHeader`

* query parameters → `@RequestParam`

* body → `@RequestBody`

This leads to verbose and hard-to-maintain client methods when a request logically belongs together:
````java
ResponseEntity<?> call(@RequestParam("userId") Long userId,
                       @RequestParam("size") Integer size,
                       @RequestHeader("X-Trace-Id") String traceId,
                       @RequestBody Payload payload);
````

Feign does not provide a way to pass one object and automatically split it into headers, parameters, and body.


## The Solution

#### Feign Composite Request allows you to define a single request object and annotate its fields.

Feign client methods remain clean and minimal:

````java
ResponseEntity<?> call(@CompositeRequest RequestDto request);
````

The library extracts request attributes based on field annotations and sends them appropriately.


## Requirements

* Java 17+
* Spring Boot 3 or 4
* Spring Cloud OpenFeign

You can use this [template configuration for spring boot](https://start.spring.io/#!type=maven-project&language=java&dependencies=cloud-feign)


## How to use

### 1. Add GitHub Repository

For Maven, add repository to `pom.xml`
```xml
<repositories>
    <repository>
        <id>github</id>
        <url>https://maven.pkg.github.com/ahmadehz/*</url>
    </repository>
</repositories>
```

For Gradle, add repository to `build.gradle`
```groovy
repositories {
    maven {
        url = uri("https://maven.pkg.github.com/ahmadehz/*")
    }
}
```


### 2. Add Dependency

For Maven, add dependency to `pom.xml`
```xml
<dependencies>
    <dependency>
        <groupId>io.openfeign.extensions</groupId>
        <artifactId>composite-request</artifactId>
        <version>0.1.0</version>
    </dependency>
</dependencies>
```

for Gradle, add dependency to `build.gradle`:
```groovy
dependencies {
    implementation("io.openfeign.extensions:composite-request:0.1.0")
}
```


### 3. Define a Request Class

Create a regular POJO and annotate its fields to indicate how they should be sent in the HTTP request.

* Fields annotated with `@Param` are sent as query parameters
* Fields annotated with `@Header` are sent as HTTP headers
* Field annotated with `@Body` is sent as HTTP body

**Example:**
```java
import io.openfeign.extensions.compositerequest.annotation.*;

public class RequestDto {

    @Header("X-Trace-Id")
    private String traceId;

    @Param("userId")
    private Long userId;
    
    @Param
    private Integer size;

    @Body
    private Payload payload;
}
```

#### Important
* The class can be any POJO.
* No interface required.
* No Spring annotations required.
* Only field-level annotations matter.

### 4. Use It in a Feign Client
```java
import io.openfeign.extensions.compositerequest.annotation.CompositeRequest;
import io.openfeign.extensions.compositerequest.CompositeRequestConfiguration;

@Component
@FeignClient(
    name = "example",
    url = "http://localhost:8080",
    configuration = CompositeRequestConfiguration.class
)
public interface ExampleClient {

    @PostMapping("/api/v1/example")
    ResponseEntity<?> call(@CompositeRequest RequestDto request);
}
```
**`CompositeRequestConfiguration` must be added to the Feign client configuration.**


### Supported Field Types

#### Headers

```java
@Header
private String traceId;

@Header
private Map<String, String> headers;

@Header
private MultiValueMap<String, String> multiHeaders; // Multi-value headers

```
If no value is specified in `@Header`, the field name is used as the HTTP header name.

Example:
```java
@Header
private String traceId = "111";

@Header("X-Forwarded-For")
private String forwardedIp = "192.168.1.1";
```

Produces:
```
traceId: 111
X-Forwarded-For: "192.168.1.1"
```
---
#### Parameters

```java
@Param
private String userId;

@Param
private Map<String, Object> params;

@Param
private MultiValueMap<String, Object> multiParams; // Multi-value parameters
```

If no value is specified in `@Param`, the field name is used as the query parameter name.  

Example:

```java
@Param
private Integer size = 10;

@Param("user")
private String userId = "123";
```
Produces:
```
?size=10&user=123
```
---
#### Body
```java
@Body
private Object body;

@Body
private Map<String, Object> body;

@Body
private Payload payload;
```
The body can be:

* Any POJO
* Map
* Generic object

Only one logical body should be defined.

### Supported Annotations

| Annotation          | Purpose                               |
|---------------------|---------------------------------------|
| `@CompositeRequest` | Marks the composite request parameter |
| `@Header`           | Sends field as HTTP headers           |
| `@Param`            | Sends field as query parameters       |
| `@Body`             | Sends field as request body           |

All annotations are located in:
```io.openfeign.extensions.compositerequest.annotation```


## Limitations
### 1. Incompatible with Custom Feign Contract

This library defines its own contract behavior.
You cannot register a custom Feign Contract alongside it.

If your project already customizes Feign contract behavior, this library will conflict.

### 2. Incompatible with Custom Feign InvocationHandlerFactory

This library provides its own InvocationHandlerFactory.

If your project overrides Feign’s invocation handling mechanism, this extension will not work.

### 3. Requires Spring Configuration

This library depends on CompositeRequestConfiguration.
It cannot be used as a pure Feign plugin without Spring.