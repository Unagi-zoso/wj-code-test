package com.wjc.codetest.product.model.domain;

/*
   문제:   wildcard Import 로 인한 유지보수 어려움
   원인:   어떤 패키지에서 Import 되었는지 직관적으로 파악 어려워 의존성 관리 어려움
   개선안: 컨벤션적으로 개별 Import 를 사용하게 하고 IDE 설정을 통해 wildcard Import 방지
*/
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/*
  문제:   기본 생성자 직접 작성
  원인:   JPA 엔티티에 기본 생성자가 필요하지만, Lombok 의 @NoArgsConstructor 어노테이션을 활용하지 않고 직접 작성하여 코드 중복 발생
  개선안: Lombok 의 @NoArgsConstructor 어노테이션을 활용하여 기본 생성자 자동 생성
*/
@Entity
@Getter
@Setter
public class Product {

    @Id
    @Column(name = "product_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /*
      문제:   중복된 카테고리 문자열 관리 어려움
      원인:   카테고리를 문자열로 관리하여 오타나 일관성 없는 값이 들어갈 수 있음
             카테고리만을 조회 시 모든 product 를 스캔해야 하는 비효율 발생
      개선안: 카테고리를 별도의 엔티티나 Enum 으로 관리하여 일관성 유지 및 조회 성능 개선
    */
    @Column(name = "category")
    private String category;

    @Column(name = "name")
    private String name;

    protected Product() {
    }

    public Product(String category, String name) {
        this.category = category;
        this.name = name;
    }

    /*
      문제:   Lombok 을 활용하면서도 불필요한 getter 메서드 수동 작성
      원인:   @Getter 어노테이션을 사용하고 있음에도 불구하고 수동으로 getter 메서드를 작성하여 중복 코드 발생
      개선안: @Getter 어노테이션을 활용하여 불필요한 수동 getter 메서드 제거
    */
    public String getCategory() {
        return category;
    }

    public String getName() {
        return name;
    }
}
