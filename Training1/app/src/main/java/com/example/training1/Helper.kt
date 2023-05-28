package com.example.training1

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.transform

val userList = listOf("User1", "User2", "User3", "User4", "User5")

val flowUserList = flowOf("User6", "User7", "User8", "User9", "User10")

val ageList = listOf(10, 20, 30, 40, 50)

//note: 自作のオペレータ (奇数であればスキップし、偶数であれば2回値を流す)
fun Flow<Int>.slipOddAndDuplicateEven() = transform { value ->
    if (value % 2 == 0) {
        emit(value)
        emit(value)
    }
}