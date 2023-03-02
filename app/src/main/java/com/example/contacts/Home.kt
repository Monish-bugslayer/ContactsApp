package com.example.contacts

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.fragment.app.commit

import kotlinx.android.synthetic.main.fragment_home.*


class Home : Fragment() {
    private val recents:Recents=Recents()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        expand_your_contacts.setOnClickListener {
            fragmentManager?.commit {
                setCustomAnimations(com.airbnb.lottie.R.anim.abc_fade_in,
                    pub.devrel.easypermissions.R.anim.abc_tooltip_exit)
                replace(R.id.fragment_container_view,recents)
                addToBackStack(null)

            }
        }
    }
}