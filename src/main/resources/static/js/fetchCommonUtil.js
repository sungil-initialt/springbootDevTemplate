/*
//rest api 성공 응답 규격
HttpStatus.OK(200)
{
  "resultCode": "S000",
  "resultMessage": "success",
  "requestTime" : "2024-12-27T14:29:31.827941",
  "responseTime" : "2024-12-27T14:29:31.848168",
  "durationMsec" : "20",
  "result": { -->> T 객체에 선언된 내용으로 구성됨.
    "name": "myProject",
    "version": "v1",
    "description": "sptek web framework"
  }
}
 */

/*
//rest api 에러 응답 규격
{
    "resultCode": "GE011",
    "resultMessage": "NOT_VALID_ERROR",
    "requestTime" : "2024-12-27T14:29:31.827941",
    "responseTime" : "2024-12-27T14:29:31.848168",
    "durationMsec" : "20",
    "inValidFieldInfos": [
    {
        "field": "userName",
        "value": "s",
        "reason": "Size error"
    }
],
    "exceptionMessage": "Validation failed for argument [0] in protected org.springframework.http.ResponseEntity&lt;com.sptek.webfw.dto.ApiCommonSuccessResponseDto&lt;com.sptek.webfw.example.dto.ValidationTestDto&gt;&gt; com.sptek.webfw.example.api.api1.ApiTestController.validationAnnotationPost(com.sptek.webfw.example.dto.ValidationTestDto): [Field error in object 'validationTestDto' on field 'userName': rejected value [s]; codes [Size.validationTestDto.userName,Size.userName,Size.java.lang.String,Size]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [validationTestDto.userName,userName]; arguments []; default message [userName],20,2]; default message [Size error]] "
}
 */



function test1() {
    alert("test1");
}

