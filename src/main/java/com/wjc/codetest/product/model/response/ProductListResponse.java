package com.wjc.codetest.product.model.response;

import com.wjc.codetest.product.model.domain.Product;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author : 변영우 byw1666@wjcompass.com
 * @since : 2025-10-27
 */

/*
  문제:   Java17 이상의 Record 타입 활용 부족
  원인:   Record 가 지원됨에도 Lombok 에만 의존하여 DTO 작성
  개선안: Record 타입을 활용하여 DTO 작성 고려
*/
@Getter
@Setter
/*
    문제:   페이지네이션 기반 응답 DTO 가 구체적인 도메인에 종속적임
    원인:   ProductListResponse 가 Product 엔티티에 직접 의존하고 있어, 다른 도메인에서 재사용이 어려움
    개선안: 제네릭 타입을 활용한 공통 페이지네이션 응답 DTO 생성
           카테고리가 다양해져 페이지네이션이 필요할 경우 재사용이 필요할 수 있음 그렇지 않다면 오버엔지니어링이 될 수 있어 주의
*/
public class ProductListResponse {
     /*
       문제:   Entity 직접 노출
       원인:   사용자에게 불필요한 정보까지 노출될 수 있음
       개선안: 필요한 정보만 담은 단건 응답 DTO 처럼 ProductResponse DTO 생성 후 사용
     */
    private List<Product> products;
    private int totalPages;
    private long totalElements;
    private int page;

    public ProductListResponse(List<Product> content, int totalPages, long totalElements, int number) {
        this.products = content;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
        this.page = number;
    }
}
