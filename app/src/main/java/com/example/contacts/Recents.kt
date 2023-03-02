package com.example.contacts

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.commit
import kotlinx.android.synthetic.main.fragment_recents.*


class Recents : Fragment() {
    private lateinit var  allContacts:AllContacts


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_recents, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        go_to_all_contacts_from_recents.setOnClickListener {
            allContacts= AllContacts()

            fragmentManager?.commit {
                setCustomAnimations(com.airbnb.lottie.R.anim.abc_fade_in, com.airbnb.lottie.R.anim.abc_fade_out)
                replace(R.id.fragment_container_view,allContacts)
                addToBackStack(null)
            }
        }
        super.onViewCreated(view, savedInstanceState)

    }

}