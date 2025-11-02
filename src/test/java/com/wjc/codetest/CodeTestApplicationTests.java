package com.wjc.codetest;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CodeTestApplicationTests {

    /*
       문제:   테스트 코드가 빈약해 리팩토링이나 배포에 있어 안전성을 보장할 수 없음
       원인:   테스트 코드가 거의 작성되어 있지 않음
       개선안: 최소한의 단위 테스트 및 통합 테스트 작성
              테스트 작성에 대한 사전 지식과 시간이 소요되지만 비즈니스가 복잡해지고
              코드가 많아질수록 테스트 코드의 중요성은 더욱 커지기에 선택
    */
    @Test
    void contextLoads() {
    }

}
