package com.example.contacts


import android.app.Activity
import android.content.ContentProviderOperation
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_add_contacts.*
import java.io.ByteArrayOutputStream


class AddContacts : Fragment() {
    private lateinit var arrayAdapter: ArrayAdapter<String>
    private lateinit var numberSpinnerAdapter: ArrayAdapter<String>
    private val WRITE_CONTACT_PERMISSION_CODE: Int = 100
    private lateinit var contactPermission: Array<String>
    private lateinit var imageUri: Uri
    private lateinit var textView1: TextView
    private lateinit var textView2: TextView
    private lateinit var textView3: TextView
    private lateinit var textView4: TextView
    private lateinit var textView5: TextView
    private lateinit var imageIntent: Intent
    private lateinit var namePrefix: String
    private lateinit var givenName: String
    private lateinit var middleName: String
    private lateinit var familyName: String
    private lateinit var nameSuffix: String
    private lateinit var company: String
    private lateinit var title: String
    private lateinit var number: String
    private var contentProvider: ArrayList<ContentProviderOperation> = arrayListOf()


    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                print("yes")
                add_contact_image_view.setImageURI(it.data?.data)
            } else {
                Toast.makeText(context, "Cancelled...", Toast.LENGTH_SHORT).show()
            }
        }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        arrayAdapter = context?.let {
            ArrayAdapter(
                it,
                android.R.layout.simple_list_item_1,
                resources.getStringArray(R.array.saving_details)
            )
        }!!

        numberSpinnerAdapter = context?.let {
            ArrayAdapter(
                it,
                android.R.layout.simple_list_item_1,
                resources.getStringArray(R.array.number_suggestion)
            )
        }!!
        contactPermission = arrayOf(android.Manifest.permission.WRITE_CONTACTS)
        return inflater.inflate(R.layout.fragment_add_contacts, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        save_account_spinner.adapter = arrayAdapter

        numberSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        number_suggestion_spinner.adapter = numberSpinnerAdapter
        putButtonAction()

        add_contact_image_view.setOnClickListener { setPersonImage() }
        save_contact.setOnClickListener {
            if (isWriteContactPermissionEnabled()) {
                saveContact()
            } else {
                requestWriteContactPermission()
            }
        }
    }

    private fun putButtonAction() {
        textView1 = edit_text1
        textView2 = edit_text2
        textView3 = edit_text3
        textView4 = edit_text4
        textView5 = edit_text5

        button.setOnClickListener {
            var text: String = textView1.text.toString()
            if (textView2.visibility == View.GONE) {
                textView2.visibility = View.VISIBLE
                text += textView2.text.toString()
                textView3.visibility = View.VISIBLE
                text += textView3.text.toString()
                textView4.visibility = View.VISIBLE
                text += textView4.text.toString()
                textView5.visibility = View.VISIBLE
                text += textView5.text.toString()
            } else {
                textView2.visibility = View.GONE
                textView3.visibility = View.GONE
                textView4.visibility = View.GONE
                textView5.visibility = View.GONE
            }

        }
    }

    private fun getPersonImage(): Intent {
        imageIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        return imageIntent
    }

    private fun setPersonImage() {
        resultLauncher.launch(getPersonImage())
    }

    //    @SuppressLint("QueryPermissionsNeeded")
    private fun saveContact() {
        namePrefix = edit_text1.text.toString()
        givenName = edit_text2.text.toString()
        middleName = edit_text3.text.toString()
        familyName = edit_text4.text.toString()
        nameSuffix = edit_text5.text.toString()
        company = company_name.text.toString()
        title = title_name.text.toString()
        number = number_type.text.toString()
        val rawContactId: Int = contentProvider.size
        contentProvider.add(
            ContentProviderOperation.newInsert(
                ContactsContract.RawContacts.CONTENT_URI
            ).withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null).build()
        )

        //first name , middle name and last name
        contentProvider.add(
            ContentProviderOperation.newInsert(
                ContactsContract.Data.CONTENT_URI
            ).withValueBackReference(ContactsContract.RawContacts.Data.RAW_CONTACT_ID, rawContactId)
                .withValue(
                    ContactsContract.RawContacts.Data.MIMETYPE,
                    ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE
                ).withValue(ContactsContract.CommonDataKinds.StructuredName.PREFIX, namePrefix)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.SUFFIX, nameSuffix)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, givenName)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, familyName)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.MIDDLE_NAME, middleName)
                .build()
        )

        //add mobile number
        contentProvider.add(
            ContentProviderOperation.newInsert(
                ContactsContract.Data.CONTENT_URI
            ).withValueBackReference(ContactsContract.RawContacts.Data.RAW_CONTACT_ID, rawContactId)
                .withValue(
                    ContactsContract.RawContacts.Data.MIMETYPE,
                    ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
                ).withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, number).withValue(
                ContactsContract.CommonDataKinds.Phone.TYPE,
                ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE
            ).build()
        )

        //adding company
        contentProvider.add(
            ContentProviderOperation.newInsert(
                ContactsContract.Data.CONTENT_URI
            ).withValueBackReference(ContactsContract.RawContacts.Data.RAW_CONTACT_ID, rawContactId)
                .withValue(
                    ContactsContract.RawContacts.Data.MIMETYPE,
                    ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE
                ).withValue(ContactsContract.CommonDataKinds.Organization.COMPANY, company).build()
        )

        //adding title
        contentProvider.add(
            ContentProviderOperation.newInsert(
                ContactsContract.Data.CONTENT_URI
            ).withValueBackReference(ContactsContract.RawContacts.Data.RAW_CONTACT_ID, rawContactId)
                .withValue(
                    ContactsContract.RawContacts.Data.MIMETYPE,
                    ContactsContract.CommonDataKinds.Identity.CONTENT_ITEM_TYPE
                ).withValue(ContactsContract.CommonDataKinds.Identity.IDENTITY, title).build()
        )

        //get image convert image to bytes to store in contact
        val imageByte = imageUriToBytes()

        if (imageByte != null) {
            //adding image and saving the contact with image
            contentProvider.add(
                ContentProviderOperation.newInsert(
                    ContactsContract.Data.CONTENT_URI
                ).withValueBackReference(
                    ContactsContract.RawContacts.Data.RAW_CONTACT_ID,
                    rawContactId
                ).withValue(
                    ContactsContract.RawContacts.Data.MIMETYPE,
                    ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE
                ).withValue(ContactsContract.CommonDataKinds.Photo.PHOTO, imageByte).build()
            )
        }
        try {
            val result =
                context?.contentResolver?.applyBatch(ContactsContract.AUTHORITY, contentProvider)
            Log.d(TAG, "Save contact : Saved...")
            Toast.makeText(context, "Saved...", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d(TAG, "Save contact: ${e.message}")
            Toast.makeText(context, " ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun imageUriToBytes(): ByteArray? {
        val bitmap: Bitmap
        val byteArrayOutputStream: ByteArrayOutputStream?
        return try {
            bitmap = MediaStore.Images.Media.getBitmap(context?.contentResolver, imageUri)
            byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
            byteArrayOutputStream.toByteArray()
        } catch (e: java.lang.Exception) {
            Log.d(TAG, "imageUriToBytes ${e.message}")
            null
        }
    }

    private fun isWriteContactPermissionEnabled(): Boolean {
        val result = context?.let {
            ContextCompat.checkSelfPermission(it, android.Manifest.permission.WRITE_CONTACTS)
        } == (PackageManager.PERMISSION_GRANTED)
        return result
    }

    private fun requestWriteContactPermission() {
        ActivityCompat.requestPermissions(
            context as Activity,
            contactPermission,
            WRITE_CONTACT_PERMISSION_CODE
        )
        val requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    saveContact()
                } else {
                    Toast.makeText(context, "Permission denied...", Toast.LENGTH_SHORT).show()
                }
            }
    }
}