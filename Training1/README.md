## Croutineについて

メインスレッドでネットワークリクエストを行うと待機したり、ブロックされる可能性があり、アプリがフリーズしてANRのダイアログが表示される可能性がある。このようなロングタスクを実行する場合にはバックグラウンドスレッドで実行するのが望ましい。<br>
また、ある関数がメインスレッドでのUIの更新をブロックしない時、その関数は`メインセーフティ`であると言う。

##### suspend関数
停止される可能性のある関数には`suspend修飾子`を付与する。二つ注意点がある。<br>

・　suspend関数は通常の関数からは呼び出せず、suspend関数またはCroutineScopeからしか呼び出すことができない。< br>
・　suspend関数だからと言って自動で非同期で実行されるわけではなく、coroutineScopeを利用しコンテキストを指定する必要がある。<br>

```
class Hoge() {
 ~~~
 CoroutineScope(Dispatchers.IO).launch {  //メインスレッドから外す
            method()
        }
 ~~~
}

suspend fun method() {
  delay(5000) //停止時間
  Log.d("hello world")
}
```
上記のコードを以下のように書き換えると実行を明示的に移動させる必要がなくなる。スレッド指定をハードコードしているが、本来はDIを用いてテストを容易にする方が好ましい。<br>
[ハードコードについてはこちら参照](https://developer.android.com/kotlin/coroutines/coroutines-best-practices?hl=ja#inject-dispatchers)
```
class Hoge() {
 ~~~
 CoroutineScope.launch {  //明示的に指定する必要がなくなる
            method()
        }
 ~~~
}

suspend fun method() {
  withContext(Dispatchers.IO) {   コルーチンの実行を I/O スレッドに移動し、呼び出し元の関数をメインセーフティにする。
    delay(5000) //停止時間
    Log.d("hello world") 
  }
}
```



