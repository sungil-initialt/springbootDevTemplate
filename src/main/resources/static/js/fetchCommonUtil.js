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


const API_BASE_URL = "https://localhost/api/v1"; // API 기본 URL

function test1() {
    alert("test1");
}

async function apiRequest(endpoint, method = "GET", body = null, customHeaders = {}) {

    const url = `${API_BASE_URL}${endpoint}`;

    const headers = {
        "Content-Type": "application/json",
        ...customHeaders // 추가적인 헤더가 필요할 경우 병합
    };

    const options = {
        method,
        headers,
        cache: "default", // 캐싱 정책 설정 가능
        credentials: "include" // 요청에 credentials 포함
    };

    if (body) {
        options.body = JSON.stringify(body);
    }

    try {
        const response = await fetch(url, options);

        return await response.json(); // 자동으로 JSON 변환
    } catch (error) {
        console.error("API request error:", error);
        //alert('API request error: ${error.message}');
        throw error; // 필요하면 에러를 다시 던질 수도 있음
    } 
}