# kevin-dev-oauth-apple

aws 구성
- ec2
- route53
- load balancing
sudo yum install -y git
sudo yum install -y java-1.8.0-openjdk-devel.x86_64
git clone https://github.com/ozofweird/kevin-dev-oauth-apple.git
cd kevin-dev-oauth-apple
sudo chmod +x gradlew
sudo ./gradlew clean build
cd build/libs
sudo nohup java -jar kevin-dev-oauth-apple-0.0.1-SNAPSHOT.jar &
  
- https://developer.apple.com/documentation/sign_in_with_apple/sign_in_with_apple_js/incorporating_sign_in_with_apple_into_other_platforms#overview

토큰 검증 필요
키 저장 위치에 대한 고려 필요


- [스프링 프로젝트에 애플 로그인 API 연동](https://whitepaek.tistory.com/61) 
- [스프링 프로젝트에 애플 로그인 API 연동(Github)](https://github.com/WHITEPAEK/demo-sign-in-with-apple)
- [Spring API 서버에서 Apple 인증(로그인, 회원가입) 처리하기](https://hwannny.tistory.com/71)
- [애플 Apple 로그인 iOS 서버 2중 인증](https://eeyatho.tistory.com/23)
- [애플로 로그인 (자바스크립트, 스프링) 완벽 파헤치기](https://developer111.tistory.com/58?category=948844)
- [Adding Apple Sign In To Spring Boot App(JAVA) Backend Part](https://medium.com/tekraze/adding-apple-sign-in-to-spring-boot-app-java-backend-part-e053da331a)
- [Sign In with Apple: Backend Part (Java)](https://medium.com/jeff-tech/sign-in-with-apple-id-backend-part-java-70dc9aa2c9a)

참고한 Apple 공식 문서
- [Incorporating Sign in with Apple into Other Platforms](https://developer.apple.com/documentation/sign_in_with_apple/sign_in_with_apple_js/incorporating_sign_in_with_apple_into_other_platforms#overview)
- [Generate and Validate Tokens](https://developer.apple.com/documentation/sign_in_with_apple/generate_and_validate_tokens)
- [Verifying a User](https://developer.apple.com/documentation/sign_in_with_apple/sign_in_with_apple_rest_api/verifying_a_user)
- https://appleid.apple.com/auth/keys


RestTemplate 방식보다 더 간편하게 설정이 가능
- https://techblog.woowahan.com/2630/