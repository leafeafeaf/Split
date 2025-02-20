package com.example.split

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // XML 레이아웃 설정

        val webView: WebView = findViewById(R.id.webView) // WebView 연결

        webView.settings.javaScriptEnabled = true // JavaScript 활성화
        webView.settings.domStorageEnabled = true // DOM 저장소 활성화
        webView.webViewClient = WebViewClient() // 웹페이지를 앱 내에서 로드
        webView.loadUrl("https://i12b202.p.ssafy.io/") // 원하는 웹사이트 로드
    }
}
