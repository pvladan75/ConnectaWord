package com.program.connectaword.api

object ServerConfig {
    var serverIp: String = ""
        private set

    fun setIpAddress(ip: String) {
        serverIp = ip
    }
}