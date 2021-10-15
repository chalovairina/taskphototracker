package com.chari.ic.todoapp.fragments.user_profile

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.chari.ic.todoapp.R
import com.chari.ic.todoapp.ToDoViewModel
import com.chari.ic.todoapp.firebase.MyFireStore
import com.chari.ic.todoapp.firebase.users.User
import com.chari.ic.todoapp.fragments.auth_fragments.ProgressWaitingFragment
import com.chari.ic.todoapp.utils.Constants.USER_IMAGE
import com.chari.ic.todoapp.utils.Constants.USER_MOBILE
import com.chari.ic.todoapp.utils.Constants.USER_NAME
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.AndroidEntryPoint
import de.hdodenhof.circleimageview.CircleImageView
import javax.inject.Inject

@AndroidEntryPoint
class UserProfileFragment: ProgressWaitingFragment() {
    @Inject
    lateinit var usersFirestore: MyFireStore

    private lateinit var userProfileName: EditText
    private lateinit var userProfileMobile: EditText
    private lateinit var userProfileEmail: EditText
    private lateinit var userProfileImage: CircleImageView
    private lateinit var updateBtn: Button

    private val toDoViewModel: ToDoViewModel by viewModels()

    private lateinit var requestReadExternalStorageLauncher: ActivityResultLauncher<Array<out String>>
    private lateinit var pickPhotoLauncher: ActivityResultLauncher<Intent>

    private var selectedImageUri: Uri? = null
    private var uploadedImageUrl: Uri?  = null
    private var userDetails: User? = null

    private lateinit var userDataFirebaseStorage: FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userDataFirebaseStorage = FirebaseStorage.getInstance()

        requestReadExternalStorageLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
                permissions ->
                var allPermissionsGranted = true
                for (granted in permissions.values) {
                    allPermissionsGranted = allPermissionsGranted && granted
                }
                if (allPermissionsGranted) {
                    showPhotoChooser()
                } else {
                    Toast.makeText(requireContext(), getString(R.string.read_storage_permission_required), Toast.LENGTH_LONG)
                        .show()
                }
        }

        pickPhotoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageUri = result.data?.data
                if (imageUri != null) {
                    selectedImageUri = imageUri
                    loadUserProfilePhoto(imageUri)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_user_profile, container, false)
        userProfileName = view.findViewById(R.id.user_profile_name)
        userProfileMobile = view.findViewById(R.id.user_profile_mobile)
        userProfileEmail = view.findViewById(R.id.user_profile_email)
        userProfileImage = view.findViewById(R.id.user_profile_image)
        updateBtn = view.findViewById(R.id.update_btn)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fillUserProfile()

        userProfileImage.setOnClickListener {
            requestPhotoSelection()
        }

        updateBtn.setOnClickListener {
            if (selectedImageUri != null) {
                uploadUserImageToStorageAndUpdateProfile(selectedImageUri!!)
            } else {
                updateUserProfileData()
            }
        }
    }

    private fun fillUserProfile() {
        toDoViewModel.currentUser.observe(viewLifecycleOwner) { user ->
            userDetails = User(user.userId, user.userName, user.userEmail,
                user.userImageUrl, user.userMobile, user.fcmToken)

            loadUserProfilePhoto(user.userImageUrl.toUri())
            userProfileEmail.setText(user.userEmail)
            userProfileName.setText(user.userName)
            if (user.userMobile != 0L) {
                userProfileMobile.setText(user.userMobile.toString())
            }
            toDoViewModel.currentUser.removeObservers(viewLifecycleOwner)
        }
    }

    private fun requestPhotoSelection() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            showPhotoChooser()
        } else {
            requestReadExternalStorageLauncher.launch(
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
            )
        }
    }

    private fun showPhotoChooser() {
        val photoGalleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        pickPhotoLauncher.launch(photoGalleryIntent)
    }

    private fun updateUserProfileData() {
        val userDataMap = hashMapOf<String, Any>()
        var updateRequired = false

        if (uploadedImageUrl != null && uploadedImageUrl.toString() != userDetails?.image) {
            userDataMap[USER_IMAGE] = uploadedImageUrl!!.toString()
            toDoViewModel.writeCurrentUserImageUrl(uploadedImageUrl.toString())
            updateRequired = true
        }
        val mobile = userProfileMobile.text.toString()
        if (mobile.isNotEmpty() && mobile != userDetails?.mobile.toString()) {
            userDataMap[USER_MOBILE] = mobile.toLong()
            toDoViewModel.writeCurrentUserMobile(mobile.toLong())
            updateRequired = true
        }
        val name = userProfileName.text.toString()
        if (name != userDetails?.name) {
            userDataMap[USER_NAME] = name
            toDoViewModel.writeCurrentUserName(name)
            updateRequired = true
        }
        if (updateRequired) {
            showLoadingDialog(getString(R.string.please_wait))

            usersFirestore.updateUserProfileData(userDataMap)
                .addOnSuccessListener {
                    hideLoadingDialog()
                    finishSuccessfulUpdate()
                }
                .addOnFailureListener {
                    hideLoadingDialog()
                    showToastLong(getString(R.string.failed_update_profile))
                }
        }
    }

    private fun uploadUserImageToStorageAndUpdateProfile(imageUri: Uri) {
        showLoadingDialog(getString(R.string.please_wait))

        val imageRef = userDataFirebaseStorage.reference
            .child("USER_IMAGE"  + System.currentTimeMillis() + "." + getImageExtension(imageUri))
        imageRef.putFile(imageUri)
            .addOnSuccessListener { taskSnapshot ->
                hideLoadingDialog()
                val ref = taskSnapshot.metadata?.reference
                if (ref != null) {
                    ref.downloadUrl.addOnSuccessListener { uri ->
                        uploadedImageUrl = uri
                        updateUserProfileData()
                    }
                }
            }
            .addOnFailureListener {
                hideLoadingDialog()
                showToastLong(getString(R.string.failed_image_upload))
            }
    }

    private fun finishSuccessfulUpdate() {
        showToastShort(getString(R.string.profile_updated))
        findNavController().popBackStack()
    }

    private fun loadUserProfilePhoto(imageUri: Uri) {
        Glide
            .with(this)
            .load(imageUri)
            .error(R.drawable.ic_user_placeholder)
            .centerCrop()
            .placeholder(R.drawable.ic_user_placeholder)
            .into(userProfileImage)
    }

    private fun getImageExtension(uri: Uri): String? {
        return MimeTypeMap.getSingleton()
            .getMimeTypeFromExtension(
                requireActivity().contentResolver.getType(uri)
            )
    }
}