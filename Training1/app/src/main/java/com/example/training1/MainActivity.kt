package com.example.training1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.system.measureTimeMillis
import kotlin.time.measureTime

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    @OptIn(FlowPreview::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        printUser()
//        Log.d(TAG, "userのプリント完了")
//
//        //note: サスペンド関数はコルーチンスコープ内でスレッドを指定して実行する。
//        CoroutineScope(Dispatchers.IO).launch {
//            printUserList()
//        }
//
//        // note: 実行結果を見ればわかるがuserのログ出力が完了する前に以下のコードが実行される、
//        //  これはスレッドをブロックしないため、printUserListの完了を待たないからである。
//        Log.d(TAG, "サスペンド関数のuserプリント完了 ")

        //シンプルなflow,実行スレッドを指定し、値の収集を明示する必要がある。
        //note: 1 つのスレッドで多数のコルーチンを実行できる
        /* CoroutineScope(Dispatchers.IO).launch {
            simpleFlow().collect { user ->
                Log.d(TAG, "Flow1 $user")
            }
            launch {
                simpleFlow().collect { user ->
                    Log.d(TAG, "Flow2 $user")
                }
            }
            launch {
                simpleFlow().collect { user ->
                    Log.d(TAG, "Flow3 $user")
                }
            }
        }
         */

        //flow builder
        /*CoroutineScope(Dispatchers.IO).launch {
           flowUserList.collect { user ->
               Log.d(TAG, "Flow1$user")
           }
           launch {
               //flow変換
               userList.asFlow().collect { user ->
                   Log.d(TAG, "Flow2$user")
               }
           }
       }*/

        //cancel flow: CoroutineScope
        /*CoroutineScope(Dispatchers.IO).launch {
            launch {
                //note: この中の処理の完了に指定した時間以上かかったらキャンセルする。非推奨
                withTimeoutOrNull(5000) {
                    simpleFlow().collect { user ->
                        Log.d(TAG, "Flow1 $user")
                    }
                }
            }
        }*/

        //cancel flow: lifecycleScope
        //note: lifecycleScopeを利用することでライフサイクルに沿ってflowがcancelされる
        /* lifecycleScope.launch(Dispatchers.IO) {
             simpleFlow().collect { user ->
                 Log.d(TAG, "Flow1 $user")
             }
         }*/

        //filter and map operator flow,
        //note: filterを利用することで値の絞り込みができ、mapを利用することで各要素の値を変換できる。
        /*lifecycleScope.launch(Dispatchers.IO) {
            ageList.asFlow()
                .filter { age ->
                    age % 3 == 1
                }
                .map { age ->
                    "Age $age"
                }
                .collect { age ->
                    Log.d(TAG, age)
                }
        }*/

        //transform operator flow
        //note: transformを利用することで自作でオペレータを作ることができる。
        /*lifecycleScope.launch(Dispatchers.IO) {
            ageList.asFlow()
                .transform { age ->
                    emit("Age $age")
                }
                .collect { value ->
                    Log.d(TAG, value)
                }
        }*/

        //transformを使用した自作のオペレータ
        /*lifecycleScope.launch(Dispatchers.IO) {
            ageList.asFlow().slipOddAndDuplicateEven().collect { age ->
                Log.d(TAG, age.toString())
            }
        }*/

        //take (Size-limiting operators)
        //note :flowから購読する値を指定できる。
        /*lifecycleScope.launch(Dispatchers.IO) {
            ageList.asFlow()
                .take(3)
                .collect { value ->
                    Log.d(TAG, value.toString())
                }
        }*/

        //terminal operators
        //note: ターミナルオペレータは実際にフローを開始するオペレータのこと。最も基本的な端末オペレータはcollect()

        //reduce terminal operator (出力された各アイテムに関数を適用し、最終的な値を出力します。 以下の結果：140)
        /*lifecycleScope.launch(Dispatchers.IO) {
            val sum = ageList.asFlow()
                .map { age ->
                    age
                }
                .reduce { a, b ->
                    a + b
                }
            Log.d(TAG, sum.toString())
        }*/

        //toList and toSet() terminal operator (出力される一連の値をリストとして返す)
        /*lifecycleScope.launch(Dispatchers.IO) {
            val value = simpleFlow().toList()
            or
            val value = simpleFlow().toSet()

            Log.d(TAG, value.toString())
        }*/

        //imperative completion
        //finallyブロックにラップする
        /*lifecycleScope.launch(Dispatchers.IO) {
            try {
                ageFlow().collect { value ->
                    Log.d(TAG, value.toString())
                }
            }
            finally {
                Log.d(TAG, "Flow has completed")
            }
        }*/

//        ↑
//        note: 同じ意味
//        ↓

        //declarative completion
        //note: フローが完了またはキャンセルされた後に、指定されたアクションを 呼び出すフローを返し、キャンセル例外または失敗をactionの原因パラメータとして渡します。
        /*lifecycleScope.launch(Dispatchers.IO) {
            ageFlow()
                .onCompletion {
                    Log.d(TAG, "Flow has completed")
                }
                .collect { value ->
                    Log.d(TAG, value.toString())
                }
        }*/

        //flowOn
        /*lifecycleScope.launch {
            ageFlow()
                .onCompletion {
                    Log.d(TAG, "Flow has completed")
                }
                .collect { value ->
                    Log.d(TAG, value.toString())
                }
        }*/

        //flowOn
        /*lifecycleScope.launch {
            simpleFlow()
                .flowOn(Dispatchers.IO)
                .onCompletion {
                    Log.d(TAG, "Flow has completed")
                }
                .collect { value ->
                    Log.d(TAG, value)
                }
        }*/

        //Buffer
        //note: Consumer側で処理が終わってなくても値を送ります。その値は、Consumerが現在のタスクをしていたら、flowの内部で一旦保存されます。
        // そして、Consumerの現在のタスクが完了したら、内部で保存したデータが1つずつ流れてきます。これで全体的にかかる処理時間を短縮することができる
        /*lifecycleScope.launch {
            val time = measureTimeMillis {
                numFlow()
                    .buffer()
                    .collect {
                        delay(200)
                        Log.d(TAG, "Log2 : ${Thread.currentThread().name} : $it")
                    }
            }
            Log.d(TAG, "Total time : $time")
        }*/

        //conflate
        //note: bufferとは違い、最新の値を出力するため、かける出力があるが、その分処理時間を短縮できる? (一番最新の値のみが出力されるわけではない)
        /*lifecycleScope.launch {
            val time = measureTimeMillis {
                numFlow()
                    .conflate()
                    .collect {
                        delay(200)
                        Log.d(TAG, "Log2: ${Thread.currentThread().name} : $it")
                    }
            }
            Log.d(TAG, "Total time : $time")
        }*/

        //collectLatest
        //note: 一番最新の値のみが出力される (新しい値が来た時点で置き換える？)
        /*lifecycleScope.launch {
            val time = measureTimeMillis {
                numFlow()
                    .collectLatest {
                        delay(200)
                        Log.d(TAG, "Log2: ${Thread.currentThread().name} : $it")
                    }
            }
            Log.d(TAG, "Total time : $time")
        }*/

        //zip operator (https://medium.com/android-tech-stack/combining-kotlin-flows-merge-zip-b6d30d84daa)
        //note: 遅延があってもお互いが更新されるのを待って出力される。また、どちらかのフローがキャンセルされたらもう一方もキャンセルされる。
        /*lifecycleScope.launch {
            val users = userList.asFlow().transform { user ->
                delay(1000)
                emit(user)
            }
            val ages = flowOf(10, 20, 30, 40)
            users.zip(ages) { user, age ->
                "User: $user - Age: $age"
            }
                .collect {
                    Log.d(TAG, it)
                }
        }*/

        //combine operator (https://medium.com/android-tech-stack/combining-kotlin-flows-merge-zip-b6d30d84daa)
        //note: どちらかの値が更新されたらその時点でのもう一方との値でemitされる。zipとは異なりどちらも終了したらキャンセルされる。
        /*lifecycleScope.launch {
            val users = userList.asFlow()
            val ages = ageList.asFlow()

            //note: 二つのflowを別の一つのflowとすることもできる。
//            val userInfo = combine(users, ages) { user, age -> Log.d(TAG, "User: $user, Age: $age")}
            users.combine(ages) { user, age ->
                "Name: $user - Age: $age"
            }
                .collect {
                    Log.d(TAG, "$it")
                }
        }*/

        //imperative error handling
        /*lifecycleScope.launch {
            val ages = ageList.asFlow()
                .map { age ->
                    check(age < 50) {
                        "Error on value: $age"
                    }
                    age
                }
                try {
                    ages.collect { age ->
                        //明示的に例外を発生させる
//                        check(age < 50) {
//                            "Error: on value: $age"
//                        }
                        Log.d(TAG, "value: $age")
                    }

                } catch (e: Exception) {
                    Log.d(TAG, e.toString())
                }
        }*/

        //declarative error handling
        //note: catch中間演算子は端末演算子の前にある必要がある
        /*lifecycleScope.launch {
            val ages = ageList.asFlow()
                .map { age ->
                    check(age < 50) {
                        "Error on value: $age"
                    }
                    age
                }
//                .catch { Log.d(TAG, e.toString()) } この書き方でも良い

            ages.catch { e ->
                Log.d(TAG, e.toString())
            }.collect { age ->
                Log.d(TAG, "value: $age")
            }
        }*/

        //flatMapConcat
        //note: 元のflowが発する要素を、別のflowを返すtransformを適用して変換してこれらのflowを連結する。
        // (mapを二つ以上利用する場合はオブジェクトを取得するため、値を取得するにはflatMapConcat()を利用する？)
        // (map(transform).flattenConcat()の省略形、flattenConcat()は、内側のFlowオブジェクトのうち最初のものを購読する。flattenConcat()は、その内側のFlowが発する各項目を放出し、
        // そのFlowが完了すると、flattenConcat()は次の内側のFlowオブジェクトを購読する。これは、外側のFlow自体が完了するまで続けられる。)
        /*lifecycleScope.launch {
            ageList.asFlow()
                .onEach {
                    delay(100)
                }
                .flatMapConcat {  age ->
                    userList.asFlow()
                        .map { user ->
                            delay(400)
                            "User: $user, Age: $age"
                        }
                }
                .collect {
                    Log.d(TAG, it.toString())
                }
        }*/

        //flatMapMerge
        //note: ageの値がemitされるのを待たず、.onEachで値が出力されたらその時のageの値で出力がされる。 combineに似ているflatMapConcatみたいな感じ？
        /*lifecycleScope.launch {
            ageList.asFlow()
                .onEach {
                    delay(100)
                }
                .flatMapMerge {  age ->
                    userList.asFlow()
                        .map { user ->
                            delay(400)
                            "User: $user, Age: $age"
                        }
                }
                .collect {
                    Log.d(TAG, it.toString())
                }
        }*/

        //flatLatestMap
        //note: 最新の値でemitされるため、userの500mm秒が終わりuserの一番最初の値がemitされる時には、ageは50まで到達し、その50という最新の値を利用してemitがされる。
        /*lifecycleScope.launch {
            val startTime = System.currentTimeMillis()
            ageList.asFlow()
                .onEach { delay(100) }
                .flatMapLatest { age ->
                    userList.asFlow()
                        .map { user->
                            Log.d(TAG, "Age: $age")
                            delay(500)
                            "Age: $age - User: $user - Time: ${System.currentTimeMillis() - startTime}"
                        }
                }
                .collect {
                    Log.d(TAG, it)
                }
        }*/
    }

    //note: flowビルダーを利用することでフローを生成している。blockはsuspend関数なので別のsuspend関数を呼び出すことができる
    private fun simpleFlow(): Flow<String> = flow {
        userList.forEach { user ->
            //note: 2秒ごとにemitで値を出力する
            emit(user)
            delay(2000)
        }
    }

    private fun printUser() {
        userList.forEach { user ->
            Log.d(TAG, user)
        }
    }

    private suspend fun printUserList() {
        userList.forEach { user ->
            Log.d(TAG, user)
        }
    }

    //note: floOn()でスレッドを指定する事で呼び出し側で明示する必要がなくなる
    private fun ageFlow(): Flow<Int> = ageList.asFlow().transform { age ->
        if (age == 30) {
            emit(age + 1)
        } else {
            emit(age)
        }
    }.flowOn(Dispatchers.IO)

    private fun numFlow(): Flow<Int> = flow {
        for (i in 1..10) {
            delay(100)
            Log.d(TAG, "Log1: ${Thread.currentThread().name}")
            emit(i)
        }
    }
}