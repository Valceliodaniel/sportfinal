package com.sportech.base

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var splashView: View
    private lateinit var statusView: View
    private lateinit var txtStatusTitle: TextView
    private lateinit var txtStatusMessage: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        webView = findViewById(R.id.webView)
        splashView = findViewById(R.id.splashView)
        statusView = findViewById(R.id.statusView)
        txtStatusTitle = findViewById(R.id.txtStatusTitle)
        txtStatusMessage = findViewById(R.id.txtStatusMessage)

        setupWebView()
        verificarStatusCentral()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.webViewClient = WebViewClient()
    }

    private fun verificarStatusCentral() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = URL(AppConfig.SPORTECH_API + AppConfig.APP_ID)
                val conn = url.openConnection() as HttpURLConnection
                conn.connectTimeout = 4000
                conn.readTimeout = 4000

                if (conn.responseCode == 200) {
                    val body = conn.inputStream.bufferedReader().readText()
                    val json = JSONObject(body)
                    val status = json.optString("status", "ACTIVE")
                    val message = json.optString("message", "Acesso indisponível no momento.")

                    withContext(Dispatchers.Main) {
                        processarStatus(status, message)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        processarStatus("ACTIVE", "")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    exibirTelaStatus("SEM CONEXÃO", "Você precisa de uma conexão ativa com a internet para acessar o aplicativo.")
                }
            }
        }
    }

    private fun processarStatus(status: String, message: String) {
        when (status) {
            "ACTIVE" -> {
                splashView.visibility = View.GONE
                statusView.visibility = View.GONE
                webView.visibility = View.VISIBLE
                webView.loadUrl(AppConfig.CLUB_URL)
            }
            "SUSPENDED" -> exibirTelaStatus("APLICATIVO SUSPENSO", message)
            "BLOCKED" -> exibirTelaStatus("APLICATIVO BLOQUEADO", message)
            else -> {
                splashView.visibility = View.GONE
                statusView.visibility = View.GONE
                webView.visibility = View.VISIBLE
                webView.loadUrl(AppConfig.CLUB_URL)
            }
        }
    }

    private fun exibirTelaStatus(titulo: String, mensagem: String) {
        splashView.visibility = View.GONE
        webView.visibility = View.GONE
        statusView.visibility = View.VISIBLE
        txtStatusTitle.text = titulo
        txtStatusMessage.text = mensagem
    }
}
