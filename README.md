# kevin-dev-oauth-apple-v1

![](https://img.shields.io/badge/version-0.0.1-brightgreen)

## ๐ Index
- [About](#๐-about)
- [Overview](#๐-overview)
- [Getting Started](#๐-getting-started)
- [Release Notes](#โ-release-notes)
- [Issues](#๐ฅ-issues)
- [Contributing](#๐ฅ-contributing)
- [Authors](#๐ค-authors)
- [License](#๐ท-license)
- [References](#๐-references)
- [Commit Message (Gitmoji)](#โ๏ธ-commit-messages-gitmoji)

## ๐ About
Apple ์์ ๋ก๊ทธ์ธ์ ๊ตฌํํ ํ๋ก์ ํธ์๋๋ค. ํด๋ผ์ด์ธํธ์์ ์ ๋ฌํด์ฃผ๋ 'access_token' ํน์ 'refresh_token'์ ๊ฒ์ฆํ์ฌ ์ ์ ์ ์ ๋ณด๋ฅผ ๊ฐ์ ธ์ค๋๋ฐ์ ๋ชฉ์ ์ ๊ฐ์ง๊ณ  ์์ต๋๋ค.

## ๐ Overview
* Apple Login
  * ๋ก๊ทธ์ธ ํ๋ฉด ๋ฆฌ๋ค์ด๋ ํธ (getRedirectUri)
  * 'id_token'์ public key๋ก ์ฌ์ฉ์ ์ ๋ณด ๊ฐ์ ธ์ค๊ธฐ (getUserId)
  * Private key๋ฅผ ์ด์ฉํ์ฌ client_secret์ ๋ง๋ค๊ณ  ๋ค๋ฅธ ์ ๋ณด๋ค๊ณผ ์ทจํฉํ์ฌ Apple๋ก ๋ถํฐ 'refresh_token' ๋ฐ๊ธ (getAccessToken)
  
## ๐ Getting Started

**๋ก๊ทธ์ธ ํ๋ก์ธ์ค**
![](./docs/images/oauth_1.png)

REST API ์๋ฒ์์๋ ๋ก๊ทธ์ธ ํ๋ฉด ์ฐ๋์ ๋ํ ์ฒ๋ฆฌ๊ฐ ํ์์์ต๋๋ค. ์ค์ ๋ก ์ฐ๋ํ  ๋๋ ํด๋ผ์ด์ธํธ์์ ๋ก๊ทธ์ธ๊น์ง ์๋ฃํ๊ณ  'access_token'์ API ์๋ฒ์ ์ ๋ฌํด์ฃผ๋ ํ๋ก์ธ์ค๊ฐ ์งํ๋์ด์ผ ํฉ๋๋ค.
์ฌ๊ธฐ์๋ ํ์คํธ๋ฅผ ์ํด ์น ํ๊ฒฝ์ ๊ตฌ์ฑํ๊ณ  ๋ก๊ทธ์ธ ํ๋ฉด์ผ๋ก ๋ฆฌ๋๋ ์์ ํ  ์ ์๋ ์ฃผ์๋ฅผ ์ถ๋ ฅํด๋ด๋ ํํ๋ก ์งํํ์ต๋๋ค.

**dependencies**
```
implementation group: 'io.jsonwebtoken', name: 'jjwt', version: '0.9.1'
implementation group: 'org.bouncycastle', name: 'bcpkix-jdk15on', version: '1.69'
implementation group: 'org.springframework.cloud', name: 'spring-cloud-openfeign-core', version: '3.0.3'
```

### ๐น Apple
Apple ๋ก๊ทธ์ธ(https://appleid.apple.com/auth/authorize) ์ด ์ฑ๊ณตํ๋ฉด Apple ID Server๋ก ๋ถํฐ 'state, code, id_token, user(์ต์ด 1ํ๋ง ์ถ๋ ฅ๋จ)'์ ์ ๋ณด๋ฅผ ๋ฐ์์ต๋๋ค.

'id_token'์ JWT ํ์์ผ๋ก ์ด๋ฃจ์ด์ ธ ์์ผ๋ฉฐ ์ ์ ์ ์ ๋ณด(Apple ๊ณ ์  ๊ณ์  ID, email, ๋ฑ)๋ฅผ payload์ ๋ด๊ณ  ์์ต๋๋ค. ('id_token' 10๋ถ, 'authorizationCode' 5๋ถ ๋ค์ ๋ง๋ฃ)

'id_token'์ payload์ ์ํ ๊ฐ๋ค์ด ๋ณ์กฐ๋์ง ์์๋์ง ๊ฒ์ฆํ๊ธฐ ์ํด์๋ Apple ์๋ฒ์ public key(https://appleid.apple.com/auth/keys) ๋ฅผ ์ด์ฉํ์ฌ JWS E256 signature๋ฅผ ๊ฒ์ฆํด์ผํฉ๋๋ค. (๊ธฐ๋ณธ ๊ฒ์ฆ์ ์ฝ๋์์์๋ ์๋ต)
'id_token'์ ํค๋์ ๊ฐ ์ค, kid์ alg์ ๋งค์นญ๋๋ public key๋ก ์๋ช์ ๊ฒ์ฆํด์ผ ํฉ๋๋ค.

### ๐น AWS
- EC2
- Route53 (+SSL)
- ALB

```
sudo yum install -y git 
sudo yum install -y java-1.8.0-openjdk-devel.x86_64 
git clone https://github.com/ozofweird/kevin-dev-oauth-apple-v2.git 
cd kevin-dev-oauth-apple 
sudo chmod +x gradlew 
sudo ./gradlew clean build 
cd build/libs 
sudo nohup java -jar kevin-dev-oauth-apple-0.0.1-SNAPSHOT.jar &
```

### ๐น Project Structures
```
โโโ src
    โโโ main
    โ   โโโ java
    โ   โ   โโโ com
    โ   โ       โโโ example
    โ   โ           โโโ api
    โ   โ           โ   โโโ AppleController.java
    โ   โ           โโโ config.oauth
    โ   โ               โโโ dto
    โ   โ               โ   โโโ AppleAccessTokenRes.java
    โ   โ               โ   โโโ ApplePublicKeyRes.java
    โ   โ               โ   โโโ AppleUserInfoRes.java
    โ   โ               โโโ AppleService.java
```

## โ Release Notes
* 0.0.1
    * ํ์คํธ

## ๐ฅ Issues
- ํ ํฐ ๊ฒ์ฆ์ ๋ํ ์ฝ๋ ์๋ต
- Private key ์ ์ฅ ์์น์ ๋ํ ๊ณ ๋ ค
- ํ์ ํํด ์๋๋ฆฌ์ค์ ๋ฐ๋ฅธ oauth ์ฐ๊ฒฐ ํด์ ์ ๋ํ ๊ณ ๋ ค
  - ์ฑ ๋ด์์์ ํ์ ํํด (์ ์  ํ์ ํํด -> DB ์ญ์ /๋นํ์ฑํ -> ํน์  ๊ธฐ๊ฐ ํ -> ์ฐ๊ฒฐ ํด์ )
  - Apple ๊ณ์ ์์ ์ง์  ํํด (Apple ํํด -> DB ์ญ์ /๋นํ์ฑํ)


## ๐ฅ Contributing
ozofweird

## ๐ค Authors
- [ozofweird](https://github.com/ozofweird) - **Kevin Ahn**

## ๐ท License
ozofweird

## ๐ References
- [kevin-dev-oauth-apple-v2](https://github.com/ozofweird/kevin-dev-oauth-apple-v2.git)
- [์คํ๋ง ํ๋ก์ ํธ์ ์ ํ ๋ก๊ทธ์ธ API ์ฐ๋](https://whitepaek.tistory.com/61)
- [์คํ๋ง ํ๋ก์ ํธ์ ์ ํ ๋ก๊ทธ์ธ API ์ฐ๋(Github)](https://github.com/WHITEPAEK/demo-sign-in-with-apple)
- [Spring API ์๋ฒ์์ Apple ์ธ์ฆ(๋ก๊ทธ์ธ, ํ์๊ฐ์) ์ฒ๋ฆฌํ๊ธฐ](https://hwannny.tistory.com/71)
- [์ ํ Apple ๋ก๊ทธ์ธ iOS ์๋ฒ 2์ค ์ธ์ฆ](https://eeyatho.tistory.com/23)
- [์ ํ๋ก ๋ก๊ทธ์ธ (์๋ฐ์คํฌ๋ฆฝํธ, ์คํ๋ง) ์๋ฒฝ ํํค์น๊ธฐ](https://developer111.tistory.com/58?category=948844)
- [Adding Apple Sign In To Spring Boot App(JAVA) Backend Part](https://medium.com/tekraze/adding-apple-sign-in-to-spring-boot-app-java-backend-part-e053da331a)
- [Sign In with Apple: Backend Part (Java)](https://medium.com/jeff-tech/sign-in-with-apple-id-backend-part-java-70dc9aa2c9a)

Apple ๊ณต์ ๋ฌธ์
- [Incorporating Sign in with Apple into Other Platforms](https://developer.apple.com/documentation/sign_in_with_apple/sign_in_with_apple_js/incorporating_sign_in_with_apple_into_other_platforms#overview)
- [Generate and Validate Tokens](https://developer.apple.com/documentation/sign_in_with_apple/generate_and_validate_tokens)
- [Verifying a User](https://developer.apple.com/documentation/sign_in_with_apple/sign_in_with_apple_rest_api/verifying_a_user)
- https://appleid.apple.com/auth/keys

---

## โ๏ธ Commit messages (Gitmoji)

|Gitmoji|Code|Description|
|:-----:|:---:|:--------:|
|๐จ|art|ํ์ผ/์ฝ๋ ๊ตฌ์กฐ ๊ฐ์ |
|๐ฉน|adhesive_bandage|๊ฐ๋จํ ์์ |
|โก๏ธ|zap|์ฑ๋ฅ ํฅ์|
|๐ฅ๏ธ|fire|์ฝ๋๋ ํ์ผ ์ญ์ |
|๐๏ธ|bug|๋ฒ๊ทธ ํด๊ฒฐ|
|๐๏ธ|ambulance|๊ธด๊ธ ์์ |
|โจ๏ธ|sparkles|์๋ก์ด ๊ธฐ๋ฅ|
|๐๏ธ|memo|๋ฌธ์ ์ถ๊ฐ/์์ |
|๐๏ธ|lipstick|ํ๋ฉด UI ์ถ๊ฐ/์์ |
|๐๏ธ|tada|ํ๋ก์ ํธ ์์|
|โ๏ธ|white_check_mark|ํ์คํธ ์ถ๊ฐ/์์ |
|๐๏ธ|lock|๋ณด์ ์ด์ ์์ |
|๐๏ธ|bookmark|๋ฆด๋ฆฌ์ฆ/๋ฒ์  ํ๊ทธ|
|๐ง|construction|์์ ์งํ ์ค|
|๐|green_heart|CI ๋น๋ ์์ |
|โฌ๏ธ|arrow_down|์์กด์ฑ ๋ฒ์  ๋ค์ด|
|โฌ๏ธ|arrow_up|์์กด์ฑ ๋ฒ์  ์|
|๐|pushpin|ํน์  ๋ฒ์  ์์กด์ฑ ๊ณ ์ |
|๐ท|construction_worker|CI ๋น๋ ์์คํ ์ถ๊ฐ/์์ |
|๐|chart_with_upwards_trend|๋ถ์, ์ถ์  ์ฝ๋ ์ถ๊ฐ/์์ |
|โป๏ธ|recycle|์ฝ๋ ๋ฆฌํฉํ ๋ง|
|โ|heavy_plus_sign|์์กด์ฑ ์ถ๊ฐ|
|โ|heavy_minus_sign|์์กด์ฑ ์ ๊ฑฐ|
|๐ง|wrench|์ค์  ํ์ผ ์ถ๊ฐ/์์ |
|๐จ|hammer|๊ฐ๋ฐ ์คํฌ๋ฆฝํธ ์ถ๊ฐ/์์ |
|๐|globe_with_meridians|๋ค๊ตญ์ด ์ง์|
|๐ฉ|poop|์์ข์ ์ฝ๋ ์ถ๊ฐ|
|โช|rewind|๋ณ๊ฒฝ ๋ด์ฉ ๋๋๋ฆฌ๊ธฐ|
|๐|twisted_rightwards_arrows|๋ธ๋์น ํฉ๋ณ|
|๐ฝ|alien|์ธ๋ถ API ๋ณํ๋ก ์ธํ ์์ |
|๐|truck|๋ฆฌ์์ค ์ด๋/์ด๋ฆ ๋ณ๊ฒฝ|
|๐ฅ|boom|๋๋ผ์ด ๊ธฐ๋ฅ ์๊ฐ|
|๐ฑ|bento|์์ ์ถ๊ฐ/์์ |
|๐ก|bulb|์ฃผ์ ์ถ๊ฐ/์์ |
|๐ฌ|speech_balloon|์คํธ๋ง ํ์ผ ์ถ๊ฐ/์์ |
|๐|card_file_box|๋ฐ์ด๋ฒ๋ฒ ์ด์ค ๊ด๋ จ ์์ |
|๐|loud_sound|๋ก๊ทธ ์ถ๊ฐ/์์ |
|๐|mute|๋ก๊ทธ ์ญ์ |
|๐ฑ|iphone|๋ฐ์ํ ๋์์ธ|
|๐|see_no_evil|gitignore ์ถ๊ฐ|
