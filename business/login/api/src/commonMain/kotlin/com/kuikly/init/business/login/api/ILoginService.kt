package com.kuikly.init.business.login.api

/**
 * 登录服务接口
 */
interface ILoginService {

    /**
     * 发起登录
     * @param username 用户名
     * @param password 密码
     * @return 登录结果
     */
    suspend fun login(username: String, password: String): LoginResult

    /**
     * 退出登录
     */
    suspend fun logout()

    /**
     * 是否已登录
     */
    fun isLoggedIn(): Boolean

    /**
     * 获取当前用户信息
     */
    fun getCurrentUser(): UserInfo?
}

/**
 * 登录结果封装
 */
sealed class LoginResult {
    data class Success(val user: UserInfo) : LoginResult()
    data class Failure(val code: Int, val message: String) : LoginResult()
}

/**
 * 用户信息
 */
data class UserInfo(
    val userId: String,
    val username: String,
    val nickname: String,
    val avatarUrl: String? = null
)
