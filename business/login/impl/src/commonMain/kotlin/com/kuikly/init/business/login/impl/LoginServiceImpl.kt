package com.kuikly.init.business.login.impl

import com.kuikly.init.business.login.api.ILoginService
import com.kuikly.init.business.login.api.LoginResult
import com.kuikly.init.business.login.api.UserInfo

/**
 * 登录服务实现
 */
class LoginServiceImpl : ILoginService {

    private var currentUser: UserInfo? = null
    private var loggedIn: Boolean = false

    override suspend fun login(username: String, password: String): LoginResult {
        return if (username.isNotBlank() && password.isNotBlank()) {
            val user = UserInfo(
                userId = "user_${username.hashCode()}",
                username = username,
                nickname = username
            )
            currentUser = user
            loggedIn = true
            LoginResult.Success(user)
        } else {
            LoginResult.Failure(-1, "用户名或密码不能为空")
        }
    }

    override suspend fun logout() {
        currentUser = null
        loggedIn = false
    }

    override fun isLoggedIn(): Boolean = loggedIn

    override fun getCurrentUser(): UserInfo? = currentUser
}
