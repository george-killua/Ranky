package com.killua.ranky

import android.os.Bundle
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.killua.ranky.base.BaseActivity
import com.killua.ranky.features.main.MainFragment

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportFragmentManager.commit {
            replace<MainFragment>(R.id.container)
            setReorderingAllowed(true)
        }
    }
}