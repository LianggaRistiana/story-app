package com.dicoding.storyapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        val viewModel: MainViewModel by viewModels {
//            ViewModelFactory.getInstance(this)
//        }
//
//        val navController = findNavController(R.id.fragment_container)
//        val navGraph = navController.navInflater.inflate(R.navigation.main_navigation)
////
//        if (viewModel.isLogin()) {
//            // Set start destination ke homeFragment jika user sudah login
//            navGraph.setStartDestination(R.id.homeFragment)
//        } else {
//            // Set start destination ke loginFragment jika user belum login
//            navGraph.setStartDestination(R.id.loginFragment)
//        }
//        navController.graph = navGraph
    }
}