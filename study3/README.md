# 自作認証サーバー

## 試し方

1. `hello`プロジェクトの`com.example.hello.HelloApplication`を起動する
2. `uaa`プロジェクトの`com.example.uaa.UaaApplicaton`を起動する
3. http://localhost:8080 へアクセスする
4. Basic認証を求められるのでユーザー名に`uragami`、パスワードに`ilovejava`と入力して認証する
5. リソースへのアクセス許可を求められるのでapproveを選ぶ
6. "Hello, uragami!"と表示されれば成功

## 技術

JAX-RSの参照実装であるJerseyをベースに認証サーバーを実装してみた。

## 参考資料

* http://www.openid.or.jp/document/

