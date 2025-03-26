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



/**
 * 공통 fetch 함수
 *
 * @param {string} url - 요청할 API URL
 * @param {Object} options - 사용자 정의 옵션 (method, headers, body, query 등)
 * @returns {Promise<any>} 응답 JSON 또는 필요한 데이터
 */
const API_BASE_URL = document.querySelector('meta[name="apiBaseUrl"]').content;
//console.log("API_BASE_URL: " + API_BASE_URL);

async function requestFetch(url, options = {}) {
    const {
        method = 'GET',
        headers = {},
        body,
        query = {},
        baseUrl = API_BASE_URL,       // 전역 API prefix가 있다면 사용
        timeout = 10000,    // 기본 timeout 10초
        rawResponse = false // JSON 파싱 말고 원본 응답 받고 싶을 때
    } = options;

    // 1. 쿼리 파라미터 붙이기
    const queryString = new URLSearchParams(query).toString();
    const fullUrl = `${baseUrl}${url}${queryString ? `?${queryString}` : ''}`;

    // 2. 요청 옵션 구성
    const fetchOptions = {
        method,
        headers: {
            'Content-Type': 'application/json',
            ...headers,
        },
    };

    // 3. body 처리 (GET/HEAD에는 넣지 않음)
    if (body && method !== 'GET' && method !== 'HEAD') {
        fetchOptions.body = typeof body === 'string' ? body : JSON.stringify(body);
    }

    // 4. AbortController로 timeout 설정
    const controller = new AbortController();
    const timeoutId = setTimeout(() => controller.abort(), timeout);
    fetchOptions.signal = controller.signal;

    try {
        const response = await fetch(fullUrl, fetchOptions);
        clearTimeout(timeoutId);

        if (!response.ok) {
            if (response.status === Response.statuses.TOO_MANY_REQUESTS) {
                //중복 오류와 관련해서는 아무 처리 하지 않음
                console.error('Too many requests');
            } else {
                const errorJson = await response.json();
                alert(errorJson.exceptionMessage);
                throw new Error(`HTTP ${response.status}: ${errorJson}`);
            }
        }

        return rawResponse ? response : await response.json();

    } catch (err) {
        if (err.name === 'AbortError') {
            throw new Error(`Request timed out after ${timeout}ms`);
        }
        throw err;
    }
}

// GET 요청 (with query string)
/*
const data = await requestFetch('/api/xxx', {
    query: { page: 1, size: 20 }
});
console.log(data);
*/

// POST 요청 (with body)
/*
const user = await requestFetch('/api/users', {
    method: 'POST',
    body: { name: 'John', age: 30 }
});
console.log(user);
*/

// 커스텀 헤더 추가 (ex: Authorization)
/*
const token = 'Bearer abc123';
const data = await requestFetch('/api/protected', {
    headers: { Authorization: token }
});
*/

// 응답을 JSON 파싱하지 않고 원본 그대로 받고 싶을 때
/*const res = await requestFetch('/api/file', {
    rawResponse: true
});
const blob = await res.blob(); // 파일 다운로드 등
*/

// 요청 타임아웃 설정
/*
try {
    await requestFetch('/api/slow', {
        timeout: 5000 // 5초 초과 시 에러
    });
} catch (err) {
    console.error('요청 실패:', err.message);
}
*/
