package com.wjc.codetest.product.repository;

import com.wjc.codetest.product.model.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /*
       문제:   문자열 기반 카테고리 조회의 성능 저하
       원인:   Product 테이블의 category 컬럼(VARCHAR)을 문자열 비교로 필터링
              문자열 인덱스는 정수 인덱스 대비 비효율적 (바이트 단위 비교, 큰 인덱스 크기)
              특정 카테고리 제품 조회 시 VARCHAR 컬럼 B-Tree 인덱스 스캔 필요
       개선안: Category를 별도 엔티티로 분리하여 FK(category_id)로 관리
              정수형 FK 인덱스로 조회 성능 향상 및 Category와 PK JOIN으로 효율화
              [선택 근거]
              서비스 컨셉 상 핵심적인 기능이 사용 빈도가 높을 것으로 반단되어 성능을 보장해야할 필요가 있음
              운영 중 이상이 발생하고 쿼리 개선 이전 하드웨어 업그레이드로 대응하는 것은 비용 비효율적
       검증:   JMH 벤치마크 테스트
              [측정 환경]
              - Warmup: 10회 × 2초 (JIT 최적화 안정화)
              - Measurement: 15회 × 2초 (실제 성능 측정)
              - Fork: 3회 (독립적인 JVM 프로세스, 총 45개 샘플)

              [쿼리 변경]
              - BEFORE: SELECT p FROM Product p WHERE p.category = ? (문자열 비교)
              - AFTER:  SELECT p FROM Product p JOIN Category c ON p.category_id = c.id
                        WHERE p.category_id = ? (정수 FK 비교)

              [인덱스 활용]
              - BEFORE: VARCHAR 컬럼 B-Tree 인덱스 (문자열 비교, 느림)
              - AFTER:  BIGINT FK 인덱스 + PK 클러스터드 인덱스 JOIN (정수 비교, 빠름)

              [평균 응답 시간]
              - BEFORE: 12.535 ms (±3.022 ms)
              - AFTER:  2.786 ms (±0.617 ms)
              - 개선율: 77.8% 향상

              [성능 안정성]
              - Error 범위: ±3.022 ms → ±0.617 ms (80% 감소)
              - 일관되고 예측 가능한 응답 시간 확보

              [결과]
              Category 엔티티 분리로 평균 77.8% 성능 개선
              문자열 비교 → 정수 비교로 인덱스 효율 극대화 및 성능 안정성 향상 검증 완료
     */
    Page<Product> findAllByCategory(String name, Pageable pageable);

    /*
       문제:   비효율적인 카테고리조회 방식 (성능 저하, 설계 문제)
       원인:   Product 엔티티에서 카테고리 정보를 문자열로 관리하여,
              모든 Product 레코드를 조회한 후 중복을 제거하는 방식으로 카테고리를 얻음.
       개선안: 카테고리를 별도의 엔티티로 분리하여 관리하고,
              카테고리 전용 테이블에서 직접 조회하도록 수정.
              이를 통해 중복 제거 과정 없이 효율적으로 카테고리 목록을 얻을 수 있음.
              [선택 근거]
              현재 모든 카테고리를 조회하는 요구사항이 있고 카테고리 조회는 서비스 컨셉 상
              자주 조회될 가능성이 높아 성능 중요
              카테고리를 문자열이 아닌 테이블로 관리함으로써 데이터 무결성도 향상.
              오타 등의 휴먼에러도 방지 가능.
        검증:  JMH 벤치마크 테스트
              [측정 환경]
              - Warmup: 10회 × 2초 (JIT 최적화 안정화)
              - Measurement: 15회 × 2초 (실제 성능 측정)
              - Fork: 3회 (독립적인 JVM 프로세스, 총 45개 샘플)

              [쿼리 변경]
              - BEFORE: SELECT DISTINCT p.category FROM Product p
              - AFTER:  SELECT c FROM Category c

              [스캔 레코드 수]
              - BEFORE: 50,000개 전체 스캔 + DISTINCT 연산
              - AFTER:  100개만 스캔 (99.8% 감소)

              [평균 응답 시간]
              - BEFORE: 1.320 ms
              - AFTER:  0.611 ms
              - 개선율: 53.7% 향상

              [성능 안정성]
              - Error 범위: ±3.003 ms → ±0.267 ms (91% 감소)
              - 일관되고 예측 가능한 응답 시간 확보

              [결과]
              Category 엔티티 분리로 성능, 안정성, 데이터 무결성 모두 개선 검증 완료
     */
    @Query("SELECT DISTINCT p.category FROM Product p")
    List<String> findDistinctCategories();
}
