package com.wjc.codetest.product.model.request;

import lombok.Getter;
import lombok.Setter;

/*
  문제:   Java17 이상의 Record 타입 활용 부족, 불필요한 코드 작성
  원인:   Record 가 지원됨에도 Lombok 에만 의존하여 DTO 작성
  개선안: Record 타입을 활용하여 DTO 작성 고려
         [선택 근거]
         @Getter 같은 어노테이션을 누락할 우려가 줄어들고, DTO 의 불변성을 보장할 수 있음
*/
@Getter
@Setter
/*
  문제:   product 도메인의 DTO 가 개별 java 파일을 가져 관리 불편
  원인:   product 도메인의 DTO 들이 개별 java 파일로 존재하여 관리가 불편함
  개선안: 하나의 product DTO 파일에 nested record 로 묶어 관리
         [선택 근거]
         관련 DTO 들이 한 파일에 모여 있어 찾거나 수정 등 관리가 용이하고, 코드 가독성 향상
*/
public class CreateProductRequest {
    private String category;
    private String name;

    /*
      문제:   당장 사용하지 않는 생성자 존재해 관리 어려움
      원인:   사용하지 않는 생성자가 DTO 에 포함되어 있어 코드 가독성과 유지보수성 저하
      개선안: 현재 사용되는 생성자만 남기고 불필요한 생성자 제거
             현재 방식도 이후 기획에 따라 사용될 수 있지만 그렇지 않다면 오버엔지니어링의 우려 존재
    */
    public CreateProductRequest(String category) {
        this.category = category;
    }

    public CreateProductRequest(String category, String name) {
        this.category = category;
        this.name = name;
    }
}

