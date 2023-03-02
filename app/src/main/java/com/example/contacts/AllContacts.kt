package com.example.contacts


import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.commit


import com.vmadalin.easypermissions.EasyPermissions
import kotlinx.android.synthetic.main.contact_list.*

import kotlinx.android.synthetic.main.fragment_all_contacts.*
import kotlinx.android.synthetic.main.fragment_recents.*
import pub.devrel.easypermissions.AppSettingsDialog


class AllContacts : Fragment(),EasyPermissions.PermissionCallbacks {

    private var arrayList:ArrayList<ContactModel> = arrayListOf()
    private var rcvAdapter:RCVAdapter=RCVAdapter(arrayList)
    private val addContacts:AddContacts= AddContacts()
    private lateinit var  recents:Recents

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if(checkContactPermissions()){
            getContact()
        }
        return inflater.inflate(R.layout.fragment_all_contacts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycler_view.adapter=RCVAdapter(arrayList)
        add_contact.setOnClickListener {

            fragmentManager?.commit {
                setCustomAnimations(
                    com.airbnb.lottie.R.anim.abc_fade_in,
                    com.airbnb.lottie.R.anim.abc_fade_out)
                replace(R.id.fragment_container_view,addContacts)
                addToBackStack(null)
            }
        }
        go_to_recents_from_all_contacts.setOnClickListener {
            recents= Recents()
            fragmentManager?.commit {
                setCustomAnimations(com.airbnb.lottie.R.anim.abc_fade_out, com.airbnb.lottie.R.anim.abc_fade_out)
                replace(R.id.fragment_container_view,recents)
                addToBackStack(null)
            }
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    private fun getContact() {
        arrayList.clear()
        val cursor = context?.contentResolver
            ?.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                arrayOf(
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.NUMBER,

                    ),null,null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
            )
        while (cursor!!.moveToNext()){
            val contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
            val contactNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
            val contactModel =  ContactModel(contactName,contactNumber)
            arrayList.add(contactModel)
        }
        rcvAdapter.notifyDataSetChanged()
        cursor.close()
    }


    private fun checkContactPermissions():Boolean{
        return if (context?.let { PermissionTracking.hasCOntactPermissions(it) } == true){
            true
        }else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O){
            EasyPermissions.requestPermissions(
                this,
                "You will need to accept the permission in order to run the application",
                100,
                android.Manifest.permission.READ_CONTACTS,
                android.Manifest.permission.WRITE_CONTACTS,
            )
            true
        }else{
            false
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {}

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this,perms)){
            AppSettingsDialog.Builder(this).build().show()
        }else{
            checkContactPermissions()
        }
    }


}