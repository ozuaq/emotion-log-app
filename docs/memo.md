# 学習内容

## 目標

- 学習内容を明らかにする
- 書いたコードを説明できるようにする

## メモ

### play framework のバージョンを確認

project/plugins.sbt

```
addSbtPlugin("org.playframework" % "sbt-plugin" % "3.0.7")
```

バージョンは 3.0.7

### scala のバージョンを確認

build.sbt

```
scalaVersion := "2.13.16"
```

バージョンは 2.13.16
Scala 2.13 は Scala 3 との間で双方向のバイナリ互換性がある。

### ユーザ認証の実装に play-pac4j を使うように AI に指示

次を参考にするように指示した。(https://github.com/pac4j/play-pac4j/wiki/Dependencies)

```
You need to add the required dependencies:

the play-pac4j_2.12 or play-pac4j_2.13 or play-pac4j_3 library: "org.pac4j" %% "play-pac4j" % "12.0.0-PLAYx.x" (where x.x is 2.8, 2.9 or 3.0)
the appropriate pac4j submodules (groupId: org.pac4j, version: 6.0.0): pac4j-oauth for OAuth support (Facebook, Twitter...), pac4j-cas for CAS support, pac4j-ldap for LDAP authentication, etc.
All released artifacts are available in the Maven central repository.
```

### implicit の使い方

暗黙のパラメータ

メソッドへの共通パラメータの引き渡しを自動化する

例）

```
// 税率を暗黙のパラメータとして受け取るメソッド
def calculateTotalPrice(price: Double)(implicit taxRate: Double): Double = {
  price * (1 + taxRate)
}

implicit val defaultTaxRate: Double = 0.1 // 10%

// メソッドを呼び出す際に、税率を省略できる
val price1 = 1000.0
val totalPrice1 = calculateTotalPrice(price1)

// もちろん、明示的に異なる税率を渡すことも可能
val totalPrice2 = calculateTotalPrice(price1)(0.08)
```

### Anorm のパーサー連結

Anorm の RowParser で ~ というメソッドで「パーサーを連結する」と言う機能が実装されている

### trait の使い方

契約の定義

interface のように、実装すべきメソッドを強制できる。

```
trait T { def method(): Unit }
```

デフォルト実装

メソッドにデフォルトの振る舞いを持たせ、コードを再利用できる。

```
trait T { def method(): Unit = println("...") }
```

ミックスイン

複数の trait を with で組み合わせ、クラスに様々な能力を追加できる。

```
class C extends TraitA with TraitB with TraitC
```

例）

```
trait Runner {
  def run(): Unit = println("速く走っています！")
}

trait Flyer {
  def fly(): Unit = println("空を飛んでいます！")
}

class RunningDog extends Runner {
  def bark(): Unit = println("ワン！")
}

class FlyingBird extends Runner with Flyer {
  def chirp(): Unit = println("ピヨピヨ！")
}

val myDog = new RunningDog()
myDog.bark()
myDog.run()

val myBird = new FlyingBird()
myBird.chirp()
myBird.run()
myBird.fly()
```

### エラー解決: [error] com.fasterxml.jackson.databind.JsonMappingException: Scala module 2.14.3 requires Jackson Databind version >= 2.14.0 and < 2.15.0 - Found jackson-databind version 2.16.0 <br> [error] at com.fasterxml.jackson.module.scala.JacksonModule.setupModule(JacksonModule.scala:61)

これはすでに他のライブラリで 2.16.0 の jackson.databind が使われているが、jackson.module.scala では 2.14.0 以上 2.15.0 を要求しているためエラー。

MVNRepository (Maven Central) のサイトで、jackson-module-scala_2.13 で調べて、Compile Dependencies で jackson.databind が 2.16.x になっている jackson-module-scala_2.13 のバージョンを選択する。

### エラー解決: VS Code の拡張機能（Metals）が上手くライブラリを認識しない問題

Metals のビルド情報をリセットする。

```
rm -rf target/ logs/ .metals/ project/project/ project/target/ project/metals.sbt project/.bloop/
```

### git ステージング済みと未ステージングを分けて退避する方法

git stash push

ステージング済み と 未ステージング の両方 HEAD コミットと同じクリーンな状態に戻る

git stash push --keep-index

git stash push -k

未ステージング の変更のみ ステージング済みの変更は残り、それ以外は HEAD コミットと同じ状態になる

git stash push --staged

git stash push -S

ステージング済み の変更のみ 未ステージングの変更は残り、ステージングエリアが HEAD コミットと同じ状態になる

git stash push --patch

git stash push -p

変更点（hunk）を対話的に選択して退避 選択した変更のみが退避される

git stash --include-untracked

git stash -u

Untracked files も含めて、現在の変更をすべて退避

git stash --all

git stash -a

無視しているファイルも含め、すべてを退避

### frontendとbackendのコンテナを分けた

scalaのvscode拡張機能Metalsが上手く機能しないので、プロジェクトルートにbuild.sbtが来るようにした

frontendとbackendのdevcontainerの設定それぞれ作成した。sbtのインストールにはsdkmanを使ってインストールするようにした。

### ユーザ認証のコードがうまく動かなかったため次のように対応

認証のライブラリとして有名であり、勉強用途として自分で実装する量が適切だと判断して、play-pac4jを選択した。

参考になる公式ドキュメントのサンプルコードをピックアップして示し、それを参考にして実装するように指示。

build.sbtのサンプル https://github.com/pac4j/play-pac4j-scala-demo/blob/master/build.sbt


app/modules/SecurityModule.scalaのサンプル https://github.com/pac4j/play-pac4j-scala-demo/blob/master/app/modules/SecurityModule.scala
