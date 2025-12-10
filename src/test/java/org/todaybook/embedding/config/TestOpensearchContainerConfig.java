package org.todaybook.embedding.config;

import org.opensearch.testcontainers.OpensearchContainer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

/**
 * OpenSearch Testcontainer를 생성하고 Spring 테스트 환경에 등록하기 위한 기본 설정 클래스입니다.
 *
 * <p>이 클래스는 다음 역할을 수행합니다:
 *
 * <ul>
 *   <li>테스트 실행 시 OpenSearch 컨테이너를 자동으로 생성 및 실행
 *   <li>컨테이너의 HTTP 주소를 Spring Environment에 주입하여, Spring AI VectorStore가 컨테이너에 연결할 수 있도록 설정
 *   <li>테스트 클래스에서 @Import 하여 사용
 * </ul>
 */
@TestConfiguration
@Testcontainers
public class TestOpensearchContainerConfig {

  /**
   * 테스트 전체에서 공유되는 정적 OpenSearch Testcontainer 인스턴스입니다.
   *
   * <p>정적으로 선언되기 때문에:
   *
   * <ul>
   *   <li>테스트 실행당 한 번만 컨테이너가 실행됩니다.
   *   <li>여러 테스트 클래스가 동일한 컨테이너를 사용합니다.
   * </ul>
   */
  @Container
  public static OpensearchContainer OPENSEARCH =
      new OpensearchContainer(DockerImageName.parse("opensearchproject/opensearch:2.0.0"));

  /**
   * OpenSearch 컨테이너의 설정 값을 Spring 환경 변수로 등록합니다.
   *
   * <p>Spring ApplicationContext가 생성되기 전에 실행되므로, 컨테이너의 URI가 VectorStore 설정에 정상적으로 반영됩니다.
   */
  @DynamicPropertySource
  static void registerProperties(DynamicPropertyRegistry registry) {
    String uri = OPENSEARCH.getHttpHostAddress();
    registry.add("spring.ai.vectorstore.opensearch.uris", () -> uri);
    System.out.println("Opensearch Container start: " + uri);
  }

  /**
   * 각 테스트 클래스에서 Opensearch 인덱스를 정리하려면 아래 예시를 테스트 클래스 내부에 직접 추가해야 합니다.
   *
   * <p>삭제 대상 인덱스:
   *
   * <pre>
   *   book-embedding-index
   * </pre>
   *
   * <p>이 정리 작업의 목적은 다음과 같습니다:
   *
   * <ul>
   *   <li>각 테스트가 완전히 독립적인 환경에서 수행되도록 보장
   *   <li>문서가 누적되는 것을 방지하여 테스트 결과가 서로 영향을 주지 않도록 함
   *   <li>테스트의 신뢰성과 재현성을 향상
   * </ul>
   *
   * <p><b>주의:</b><br>
   * TestOpenSearchContainerConfig 내부에서는 동작하지 않습니다.
   *
   * @throws Exception OpenSearch와의 HTTP 통신 중 오류가 발생한 경우
   */

  // 예시:
  //  @Value("${spring.ai.vectorstore.opensearch.uris}")
  //  private String uris;
  //
  //  @AfterEach
  //  void cleanup() throws Exception {
  //
  //    // 인덱스 존재 여부 확인
  //    HttpRequest existsRequest = HttpRequest.newBuilder()
  //        .uri(URI.create(uris + "/book-embedding-index"))
  //        .method("HEAD", HttpRequest.BodyPublishers.noBody())
  //        .build();
  //
  //    HttpResponse<Void> existsResponse =
  //        HttpClient.newHttpClient().send(existsRequest, HttpResponse.BodyHandlers.discarding());
  //
  //    if (existsResponse.statusCode() == 200) {
  //
  //      // 인덱스 삭제 요청
  //      HttpRequest deleteRequest = HttpRequest.newBuilder()
  //          .uri(URI.create(uris + "/book-embedding-index"))
  //          .DELETE()
  //          .build();
  //
  //      HttpResponse<String> deleteResponse =
  //          HttpClient.newHttpClient().send(deleteRequest, HttpResponse.BodyHandlers.ofString());
  //
  //      System.out.println("인덱스를 삭제했습니다. status: " + deleteResponse.statusCode());
  //
  //    } else {
  //      System.out.println("삭제할 인덱스를 찾을 수 없습니다.");
  //    }
  //  }
}
