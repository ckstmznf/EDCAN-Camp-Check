package com.edcan.edcancampcheck

import android.app.Activity
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.edcan.edcancampcheck.databinding.ActivityMainBinding
import kotlinx.coroutines.*


class MainActivity : AppCompatActivity() {
    lateinit var binding : ActivityMainBinding
    lateinit var viewModel: MainViewModel

    private lateinit var pref : SharedPreferences
    private lateinit var editor : SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        binding.viewModel = viewModel

        pref = getSharedPreferences("pref", Activity.MODE_PRIVATE)
        editor = pref.edit()

        viewModel.result.value = pref.getString("result", "false")


        binding.btnMainNext.setOnClickListener{
            inputName()
        }
    }

    fun inputName(){
        if(!(viewModel.userNameList.any{it == viewModel.name.value!!})){
            Toast.makeText(this, "유효하지 않는 이름입니다.", Toast.LENGTH_LONG).show()
            return
        }

        if(viewModel.result.value == "true"){
            Toast.makeText(this, "이미 인증완료했습니다.", Toast.LENGTH_LONG).show()
            return
        }

        CoroutineScope(Dispatchers.Main).launch {
            val result = CoroutineScope(Dispatchers.IO).async {
                viewModel.sendCheck()
            }.await()

            if(result == 1){
                Toast.makeText(this@MainActivity, "인증에 성공헸습니다 :)", Toast.LENGTH_LONG).show()
                viewModel.result.value = "true"

                editor.putString("result", "true")
                editor.apply()
            }


        }

    }
}