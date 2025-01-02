# Trouble Shooting
프로젝트를 진행하면서 발생한 문제점들과 해결법 서술합니다.

## POINT 타입 문제점
프로젝트를 진행하면서 공간 데이터를 저장하고 처리하기 위해 Point 타입을 사용하였습니다. 
그러나 이를 사용하면서 다음과 같은 문제점이 발생했습니다.
1. 문제점 설명
클라이밍장 생성 API를 통해 Point 타입의 위치 데이터를 JSON으로 직렬화 하거나 역질렬화하려고 시도할 때 다음과 같은 오류가 발생했습니다.

    ```text
    [Unhandled Exception] Type definition error: [simple type, class org.locationtech.jts.geom.Point]
    ```
    이 오류는 Jakson이 ```org.locationtech.jts.geom.Point``` 클래스를 올바르게 처리하지 못해 발생한 문제 였습니다.


2. 원인 분석
- **Jackson**의 기본 설정 문제: Jackson은 기본적으로 JTS ``Point`` 클래스를 인식하지 못하여 직렬화/역직렬화 시 오류가 발생했습니다.
- **의존성 버전 불일치**: 기존에 사용하던 ``jackson-datatype-jts:2.4``는 **Jackson 2.15**와 호환되지 않아 문제가 지속되었습니다.

3. 해결 방법
   - build.gradle 의존성 수정: jackson-datatype-jts:2.4 의존성을 제거하여 버전 불일치 문제를 해결했습니다.
   - 커스텀 Serializer 및 Deserializer 구현: Point 타입의 직렬화와 역직렬화를 처리하기 위해 커스텀 Serializer와 Deserializer를 구현했습니다.
   - Jackson 설정 추가: JacksonConfig.java 파일을 수정하여 커스텀 모듈을 등록하고 Jackson이 Point 타입을 올바르게 처리할 수 있도록 설정했습니다.
 
