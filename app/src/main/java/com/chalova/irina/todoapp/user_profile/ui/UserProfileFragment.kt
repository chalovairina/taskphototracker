package com.chalova.irina.todoapp.user_profile.ui

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.chalova.irina.todoapp.MainActivity
import com.chalova.irina.todoapp.R
import com.chalova.irina.todoapp.databinding.FragmentUserProfileBinding
import com.chalova.irina.todoapp.tasks.ui.utils.NavigationArgs
import com.chalova.irina.todoapp.utils.hideKeyboard
import com.chalova.irina.todoapp.utils.longToast
import com.chalova.irina.todoapp.utils.showKeyboard
import kotlinx.coroutines.launch
import javax.inject.Inject

class UserProfileFragment: Fragment() {


    private val args by navArgs<UserProfileFragmentArgs>()

    @Inject
    lateinit var viewModelFactory: UserProfileViewModel.UserProfileViewModelFactory
    private val userProfileViewModel: UserProfileViewModel
            by viewModels {
                provideUserProfileFactory(viewModelFactory, findNavController().currentBackStackEntry!!.savedStateHandle)
            }

    private lateinit var binding: FragmentUserProfileBinding

    private lateinit var requestReadExternalStorageLauncher: ActivityResultLauncher<String>
    private lateinit var pickPhotoLauncher: ActivityResultLauncher<Intent>

    override fun onAttach(context: Context) {
        super.onAttach(context)

        (activity as MainActivity)
            .activityComponent
            .userProfileFragmentComponentFactory().create()
            .inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        findNavController().currentBackStackEntry?.savedStateHandle?.let {
            it[NavigationArgs.USER_ID] = args.userId
        }

        requestReadExternalStorageLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()) {
                isGranted ->
            if (isGranted) {
                showPhotoChooser()
            } else {
                longToast(getString(R.string.read_storage_permission_required))
            }
        }

        pickPhotoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageUri = result.data?.data
                imageUri?.let {
                    userProfileViewModel.onEvent(UserProfileEvent.OnImageUriChanged(imageUri))
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_profile, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewmodel = userProfileViewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews()

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                userProfileViewModel.userProfileState.collect { userProfileState ->
                    if (userProfileState.isSaved) {
                        findNavController().popBackStack()
                    }
                }
            }
        }

        binding.userProfileImage.setOnClickListener {
            requestPhotoSelection()
        }

        binding.updateBtn.setOnClickListener {
            updateUserProfileData()
        }
    }

    private fun setupViews() {
        // userId
        binding.userProfileName.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int
                ) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    userProfileViewModel.onEvent(UserProfileEvent.OnUserNameChanged(s.toString()))
                }

                override fun afterTextChanged(s: Editable?) {}
            }
        )
        binding.userProfileName.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                showKeyboard(v)
            } else {
                hideKeyboard(v)
            }
        }

        // userEmail
        binding.userProfileEmail.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int
                ) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    userProfileViewModel.onEvent(UserProfileEvent.OnEmailChanged(s.toString()))
                }

                override fun afterTextChanged(s: Editable?) {}
            }
        )
        binding.userProfileEmail.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                showKeyboard(v)
            } else {
                hideKeyboard(v)
            }
        }
    }

    private fun updateUserProfileData() {
        userProfileViewModel.onEvent(UserProfileEvent.SaveUserData)
    }

    private fun readStoragePermission(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            Manifest.permission.READ_MEDIA_IMAGES
        else
            Manifest.permission.READ_EXTERNAL_STORAGE
    }

    private fun requestPhotoSelection() {
        val permission = readStoragePermission()
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                permission) == PackageManager.PERMISSION_GRANTED) {
            showPhotoChooser()
        } else {
            requestReadExternalStorageLauncher.launch(
                permission
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
}