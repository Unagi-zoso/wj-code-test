package com.wjc.codetest.product.model.request;

import lombok.Getter;
import lombok.Setter;

/*
  문제:   Java17 이상의 Record 타입 활용 부족
  원인:   Record 가 지원됨에도 Lombok 에만 의존하여 DTO 작성
  개선안: Record 타입을 활용하여 DTO 작성 고려
*/
@Getter
@Setter
/*
  문제:   product 도메인의 DTO 가 개별 java 파일을 가져 관리 불편
  원인:   product 도메인의 DTO 들이 개별 java 파일로 존재하여 관리가 불편함
  개선안: 하나의 product DTO 파일에 nested record 로 묶어 관리
*/
public class GetProductListRequest {
    private String category;
    private int page;
    private int size;
}