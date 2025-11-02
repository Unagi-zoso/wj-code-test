/*
   문제:   GlobalExceptionHandler 클래스의 패키지가 적절하지 않아 유지보수에 방해됨
   원인:   적절하지 않은 패키지에 존재함
   개선안:  exception 같은 패키지나 따로 패키지를 만드는게 성급하다하면 controller 패키지 하위에 위치시키는 것이 좋음
*/
package com.wjc.codetest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
/*
   문제:   REST API 형식임에도 일반 ControllerAdvice 사용해 @ResponseBody 일일이 명시 필요해 가독성 및 유지보수성 저하
   원인:   ControllerAdvice 사용
   개선안:  REST API 생성 서버라면  RestControllerAdvice 사용 권장
           [선택 근거]
           불필요한 코드를 줄여 어노테이션 누락에 따른 문제 예방 및 코드 가독성 향상
*/
@ControllerAdvice(value = {"com.wjc.codetest.product.controller"})
public class GlobalExceptionHandler {

    @ResponseBody
    /*
       문제:   RuntimeException 을 대상으로 모든 예외 처리 시 구체적인 예외 구분 및 응답 미흡
       원인:   RuntimeException 으로 포괄적 처리
       개선안:  비즈니스 예외와 시스템 예외를 구분한 커스텀 예외 생성 및 상황에 맞는 응답 형태 정의
               [선택 근거]
               예외 클래스를 관리하는 것에 시간이 들지만 클라이언트에게 구체적인 정보를 체계적으로 전달하기 위해서 필요
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<String> runTimeException(Exception e) {
        log.error("status :: {}, errorType :: {}, errorCause :: {}",
                HttpStatus.INTERNAL_SERVER_ERROR,
                "runtimeException",
                e.getMessage()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
