package com.wjc.codetest.product.controller;

import com.wjc.codetest.product.model.request.CreateProductRequest;
import com.wjc.codetest.product.model.request.GetProductListRequest;
import com.wjc.codetest.product.model.domain.Product;
import com.wjc.codetest.product.model.request.UpdateProductRequest;
import com.wjc.codetest.product.model.response.ProductListResponse;
import com.wjc.codetest.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
/*
   문제:   wildcard Import 로 인한 유지보수 어려움
   원인:   어떤 패키지에서 Import 되었는지 직관적으로 파악 어려워 의존성 관리 어려움
   개선안: 컨벤션적으로 개별 Import 를 사용하게 하고 IDE 설정을 통해 wildcard Import 방지
*/
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
   문제:   외부 입력 유효성 검사 누락
   원인:   DTO 에 유효성 검사가 이루어지지 않아 잘못된 데이터가 전달될 수 있음
   개선안: DTO 데이터를 대상으로 객체를 생성하던가 관련 애너테이션을 이용해 유효성 검사를 진행
*/
@RestController
/*
   문제:   RequestMapping 으로 공통된 경로 지정 누락
   원인:   각 메서드마다 공통된 경로를 반복 작성하여 코드 중복 발생
   개선안: 클래스 레벨에 @RequestMapping 애너테이션을 사용해 공통 경로 지정
*/
@RequestMapping
@RequiredArgsConstructor

/*
   문제:   응답에 Entity 가 그대로 노출되어 불필요한 정보 노출 우려
   원인:   Product Entity 가 외부에 그대로 노출되어, 내부 구현 세부사항이 드러날 수 있음
   개선안: ProductResponse DTO 를 생성하여 필요한 정보만 응답에 포함시키도록 변경
          불필요한 정보를 클라이언트에 노출하는 것은 보안적으로 위험한 측면이 있어 지양해야 함
*/
public class ProductController {
    private final ProductService productService;

    /*
       문제:   API 주소 설계 개선 필요
       원인:   get 동사 사용, 자원 식별에 불필요한 by 사용, 단수형 product 사용으로
              REST API 관점에서 비효율적 설계
       개선안: get 동사 제거 후 HTTP 메서드에 역할 전담
              by 제거 후 자원 식별자를 통해 구분
              product -> products 복수형 사용
              /get/product/by/{productId} -> /products/{productId}
              URL 만으로 자원과 행위를 명확히 알 수 있음
     */
    @GetMapping(value = "/get/product/by/{productId}")
    public ResponseEntity<Product> getProductById(@PathVariable(name = "productId") Long productId){
        Product product = productService.getProductById(productId);
        return ResponseEntity.ok(product);
    }

    /*
       문제:   API 주소 설계개선 필요 (RESTful 하지 않음)
       원인:   create 동사 사용으로 RESTful 하지 않은 설계
       개선안: create 동사 제거 후 HTTP 메서드에 역할 전담
                /create/product -> /products
    */
    @PostMapping(value = "/create/product")
    public ResponseEntity<Product> createProduct(@RequestBody CreateProductRequest dto){
        Product product = productService.create(dto);
        return ResponseEntity.ok(product);
    }

    /*
       문제:   API 주소 설계개선 필요 (RESTful 하지 않음), HTTP DELETE 메서드 미사용
       원인:   delete 동사 사용으로 RESTful 하지 않은 설계, 삭제에 POST 메서드 사용
       개선안: delete 동사 제거 후 HTTP 메서드에 역할 전담, HTTP DELETE 메서드 사용
                /delete/product/{productId} -> /products/{productId}
              soft delete 의 경우 컨벤션에 따라 DELETE 메서드를 피할 수도 있지만 현재는 그렇지 않음.
    */
    @PostMapping(value = "/delete/product/{productId}")
    public ResponseEntity<Boolean> deleteProduct(@PathVariable(name = "productId") Long productId){
        productService.deleteById(productId);
        return ResponseEntity.ok(true);
    }

    /*
       문제:   API 주소 설계개선 필요 (RESTful 하지 않음), HTTP PUT/PATCH 메서드 미사용
       원인:   update 동사 사용으로 RESTful 하지 않은 설계, 수정에 POST 메서드 사용
       개선안: update 동사 제거 후 HTTP 메서드에 역할 전담, HTTP PUT/PATCH 메서드 사용
                /update/product -> /products/{productId}
              부분 수정의 경우 PATCH, 전체 수정을 하는 경우 PUT 메서드를 사용하는 것이 바람직함.
    */
    @PostMapping(value = "/update/product")
    public ResponseEntity<Product> updateProduct(@RequestBody UpdateProductRequest dto){
        Product product = productService.update(dto);
        return ResponseEntity.ok(product);
    }

    /*
       문제:   API 주소 설계개선 필요 (복수형 리소스 사용 권장)
       원인:   product 단수형 사용으로 RESTful 하지 않은 설계, 조회에 POST 메서드 사용
               /product/list -> /products
       개선안: product -> products 복수형 사용, 조회에 GET 메서드 사용
    */
    @PostMapping(value = "/product/list")
    public ResponseEntity<ProductListResponse> getProductListByCategory(@RequestBody GetProductListRequest dto){
        Page<Product> productList = productService.getListByCategory(dto);
        return ResponseEntity.ok(new ProductListResponse(productList.getContent(), productList.getTotalPages(), productList.getTotalElements(), productList.getNumber()));
    }

    /*
       문제:   API 주소 설계개선 필요 (복수형 리소스사용 권장)
       원인:   product 단수형 사용으로 RESTful 하지 않은 설계
               /product/category/list -> /products/categories
       개선안: product -> products 복수형 사용
    */
    @GetMapping(value = "/product/category/list")
    public ResponseEntity<List<String>> getProductListByCategory(){
        List<String> uniqueCategories = productService.getUniqueCategories();
        return ResponseEntity.ok(uniqueCategories);
    }
}