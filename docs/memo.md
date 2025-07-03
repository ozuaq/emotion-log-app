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

### org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
インポート文が間違っていたので上記に修正した

### JWT_SECRETを環境変数から設定できるようにした
jwtの秘密鍵をgitの追跡に含めるのは良くないので、.gitignoreで非追跡ファイルに設定した.envに設定し、コンテナビルド時にdevcontainer.jsonで環境変数として設定するようにした。

### JWTの仕組み

「ヘッダー . ペイロード . 署名」という形式でトークンが生成される。ヘッダーとペイロードはBase64Urlでエンコードされた文字列。
ペイロードにはユーザID、発行日時、有効期限の情報が含まれる。ヘッダーにはトークンがどのような種類で、どのアルゴリズムで署名されているかの情報が含まれる。


トークン生成時には、ヘッダー、ペイロード(ユーザID、発行日時、有効期限)をBase64Urlでエンコードして、エンコードされたヘッダー、ペイロードの文字列をサーバにあるjwtの秘密鍵でハッシュ化したものが署名となる。


トークン検証時には、ヘッダー、ペイロードの文字列をサーバにあるjwtの秘密鍵でハッシュ化して、そのハッシュ値が署名と完全一致しているかを確認し、改竄されていないかを検証する。有効期限が切れていないかは、ペイロードをデコードし、有効期限を読み取れるようにし、現在時刻が有効期限内かを確認する。

### Angularの変更検知
コンポーネントクラスのpublicメンバ変数の参照が変わった場合に変更を検知する。
signalを使う場合、値を使っている箇所だけが更新される。this.prop.set(newValue)のように更新する。
