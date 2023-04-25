package com.chalova.irina.todoapp.user_profile.presentation

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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.chalova.irina.todoapp.MainActivity
import com.chalova.irina.todoapp.R
import com.chalova.irina.todoapp.databinding.FragmentUserProfileBinding
import com.chalova.irina.todoapp.utils.*
import kotlinx.coroutines.launch
import javax.inject.Inject

class UserProfileFragment : Fragment() {

    private var _binding: FragmentUserProfileBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var viewModelFactory: UserProfileViewModel.UserProfileViewModelFactory
    private val userProfileViewModel: UserProfileViewModel
            by viewModels {
                provideUserProfileFactory(
                    viewModelFactory,
                    findNavController().currentBackStackEntry!!.savedStateHandle
                )
            }

    private val requestReadExternalStorageLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            showPhotoChooser()
        } else {
            longToast(getString(R.string.user_read_storage_permission_required))
        }
    }
    private val pickPhotoLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageUri = result.data?.data
                imageUri?.let {
                    userProfileViewModel.onEvent(UserProfileEvent.ImageUriChanged(imageUri))
                }
            }
        }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        (activity as MainActivity)
            .activityComponent
            .userProfileFragmentComponentFactory().create()
            .inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserProfileBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()

        setupUiState()
    }

    private fun setupUiState() {
        viewLifecycleOwner.repeatOnState(Lifecycle.State.STARTED) {
            launch {
                userProfileViewModel.userProfileState.collect { userProfileState ->
                    if (userProfileState.isSaved) {
                        findNavController().popBackStack()
                    }
                    setupUiElements(userProfileState)
                }
            }
            launch {
                userProfileViewModel.userMessage.collect { message ->
                    shortToast(message)
                }
            }
        }
    }

    private fun setupUiElements(state: UserProfileState) {
        setImage(state)
        setTextFields(state)
    }

    private fun setImage(state: UserProfileState) {
        state.imageUri?.let { uri ->
            val requestOptions = RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)
            Glide
                .with(binding.userProfileImage.context)
                .load(uri)
                .apply(requestOptions)
                .error(R.drawable.ic_user_placeholder)
                .centerCrop()
                .placeholder(R.drawable.ic_user_placeholder)
                .into(binding.userProfileImage)
        } ?: run {
            binding.userProfileImage.setImageResource(R.drawable.ic_user_placeholder)
        }
    }

    private fun setTextFields(state: UserProfileState) {
        if (binding.userProfileName.text.toString() != state.userName)
            binding.userProfileName.setText(state.userName ?: "")
        if (binding.userProfileEmail.text.toString() != state.userEmail)
            binding.userProfileEmail.setText(state.userEmail ?: "")
    }

    private fun setupUI() {
        // userName
        binding.userProfileName.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    userProfileViewModel.onEvent(UserProfileEvent.UserNameChanged(s.toString()))
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
                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    userProfileViewModel.onEvent(UserProfileEvent.EmailChanged(s.toString()))
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

        // userImage
        binding.userProfileImage.setOnClickListener {
            requestPhotoSelection()
        }

        // update
        binding.updateBtn.setOnClickListener {
            updateUserProfileData()
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
        if (ContextCompat.checkSelfPermission(requireContext(), permission)
            == PackageManager.PERMISSION_GRANTED
        ) {
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}