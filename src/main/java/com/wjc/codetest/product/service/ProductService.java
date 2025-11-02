package com.wjc.codetest.product.service;

import com.wjc.codetest.product.model.request.CreateProductRequest;
import com.wjc.codetest.product.model.request.GetProductListRequest;
import com.wjc.codetest.product.model.domain.Product;
import com.wjc.codetest.product.model.request.UpdateProductRequest;
import com.wjc.codetest.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

/*
   문제:   wildcard Import 로 인한 유지보수 어려움
   원인:   어떤 패키지에서 Import 되었는지 직관적으로 파악 어려워 의존성 관리 어려움
   개선안: 컨벤션적으로 개별 Import 를 사용하게 하고 IDE 설정을 통해 wildcard Import 방지
*/
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Product create(CreateProductRequest dto) {
        Product product = new Product(dto.getCategory(), dto.getName());
        return productRepository.save(product);
    }

    public Product getProductById(Long productId) {
        Optional<Product> productOptional = productRepository.findById(productId);
        /*
           문제:   ! 연산자에 의한 가독성 저하
           원인:   ! 연산자가 포함된 조건문은 직관적이지 않음
           개선안: Optional의 isEmpty() 메서드 사용하여 ! 연산자 제거
                  코드 가독성을 향상 시킴
         */
        if (!productOptional.isPresent()) {
            throw new RuntimeException("product not found");
        }
        return productOptional.get();
    }

    public Product update(UpdateProductRequest dto) {
        Product product = getProductById(dto.getId());
        product.setCategory(dto.getCategory());
        product.setName(dto.getName());
        /*
           문제:   무의미한 변수 할당, 성능 및 일관성 저하
           원인:   이후 큰 처리가 없고 단순히 반환함에도 변수를 할당함
                  (결과를 바로 반환하는 다른 메소드와 비교했을 때 일관성 부족의 문제도 존재)
           개선안: productRepository.save(product)의 결과를 바로 반환
                  다른 코드와 일관성을 맞춰 가독성과 유지보수성 향상
           성능:  성능 향상 (불필요한 변수 할당 제거), Intellij Bytecode뷰 기준 바이트코드 4줄 감소
         */
        Product updatedProduct = productRepository.save(product);
        return updatedProduct;

    }

    public void deleteById(Long productId) {
        Product product = getProductById(productId);
        productRepository.delete(product);
    }

    public Page<Product> getListByCategory(GetProductListRequest dto) {
        PageRequest pageRequest = PageRequest.of(dto.getPage(), dto.getSize(), Sort.by(Sort.Direction.ASC, "category"));
        return productRepository.findAllByCategory(dto.getCategory(), pageRequest);
    }

    public List<String> getUniqueCategories() {
        return productRepository.findDistinctCategories();
    }
}