AWS Cognito Example
===================

## 1. コード生成

OpenAPI Generator を使って、Spring Boot のための Java ソースコードの一部を生成します。

```
$ rm -rf app .generated doc
$ docker-compose up
```

## 2. モジュールのビルド

生成されたソースコード、実装コードのモジュールをコンパイルして、ローカルリポジトリにデプロイします。

```
$ mvn clean install
```

## 3. アプリケーションのペアレントプロジェクトをローカルリポジトリにデプロイします。

```
$ cd parent
$ mvn clean install
```

## 4. アプリケーションを実行します。

URL: http://localhost:8080/index

```
$ cd app
$ export OPENID_HOST=<YOUR AWS Cognito Host>
$ export OPENID_CLIENT_ID=<YOUR AWS Cognito Client Id>
$ export OPENID_CLIENT_SECRET=<YOUR AWS Cognito Client Secret>
$ export OPENID_REDIRECT_URI=http://localhost:8080/index
$ mvn clean spring-boot:run
```

## 5. MFAを有効にする。

```
$ export ACCESS_TOKEN=<ACCESS_TOKEN>
$ aws cognito-idp associate-software-token --access-token ${ACCESS_TOKEN}
$ aws cognito-idp verify-software-token --access-token ${ACCESS_TOKEN} --user-code <MFA Code>
$ aws cognito-idp set-user-mfa-preference --access-token ${ACCESS_TOKEN} --software-token-mfa-settings "Enabled=true"
```

## 6. MFAを有効にするためには

AWS Cognito Userpool の設定で、OAuth スコープに、aws.cognito.signin.user.admin を許可します。
 
