package com.chalova.irina.todoapp.photo_reports.presentation

import android.Manifest
import android.content.ContentUris
import android.content.Context
import android.content.pm.PackageManager
import android.database.ContentObserver
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.chalova.irina.todoapp.MainActivity
import com.chalova.irina.todoapp.R
import com.chalova.irina.todoapp.databinding.FragmentPhotoReportsBinding
import com.chalova.irina.todoapp.di.provideReportsFactory
import com.chalova.irina.todoapp.utils.*
import com.plcoding.androidstorage.InternalStoragePhoto
import com.plcoding.androidstorage.SharedStoragePhoto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

class ReportsFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ReportsViewModel.ReportsViewModelFactory
    private val reportsViewModel: ReportsViewModel by viewModels {
        provideReportsFactory(
            viewModelFactory, findNavController().currentBackStackEntry!!.savedStateHandle
        )
    }

    private var _binding: FragmentPhotoReportsBinding? = null
    private val binding get() = _binding!!

    private lateinit var internalStoragePhotoAdapter: InternalStorageAdapter
    private lateinit var externalStoragePhotoAdapter: SharedStorageAdapter

    private var readPermissionGranted = false
    private var writePermissionGranted = false
    private lateinit var permissionsLauncher: ActivityResultLauncher<Array<String>>

    private lateinit var contentObserver: ContentObserver

    override fun onAttach(context: Context) {
        super.onAttach(context)

        (activity as MainActivity)
            .activityComponent
            .reportsFragmentComponentFactory().create()
            .inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPhotoReportsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            setupReadWritePermissions()
        }
        setupRecyclerView()

        loadPhotosFromInternalStorageIntoRecyclerView()
    }

    private fun setupReadWritePermissions() {
        permissionsLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                readPermissionGranted =
                    permissions[Manifest.permission.READ_EXTERNAL_STORAGE] ?: readPermissionGranted
                writePermissionGranted = permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE]
                    ?: writePermissionGranted

                if (readPermissionGranted) {
                    loadPhotosFromExternalStorageIntoRecyclerView()
                } else {
                    longToast(R.string.photo_report_loading_error)
                }
            }
        updateOrRequestPermissions()
    }

    private fun updateOrRequestPermissions() {
        val hasReadPermission = ContextCompat.checkSelfPermission(
            requireActivity(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        val hasWritePermission = ContextCompat.checkSelfPermission(
            requireActivity(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        val minSdk29 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

        readPermissionGranted = hasReadPermission
        writePermissionGranted = hasWritePermission || minSdk29

        val permissionsToRequest = mutableListOf<String>()
        if (!writePermissionGranted) {
            permissionsToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (!readPermissionGranted) {
            permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if (permissionsToRequest.isNotEmpty()) {
            permissionsLauncher.launch(permissionsToRequest.toTypedArray())
        }
    }

    private fun setupRecyclerView() {
        setupAdapter()
        loadPhotos()
    }

    private fun setupAdapter() {
        internalStoragePhotoAdapter = InternalStorageAdapter {

        }
        externalStoragePhotoAdapter = SharedStorageAdapter {

        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            setupInternalStorageRecyclerView()
        } else {
            setupExternalStorageRecyclerView()
        }
    }

    private fun loadPhotos() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            loadPhotosFromInternalStorageIntoRecyclerView()
        } else {
            initContentObserver()
            loadPhotosFromExternalStorageIntoRecyclerView()
        }
    }

    private fun setupInternalStorageRecyclerView() {
        binding.rvPhotoReports.apply {
            layoutManager = StaggeredGridLayoutManager(1, RecyclerView.VERTICAL)
            adapter = internalStoragePhotoAdapter
        }
    }

    private fun setupExternalStorageRecyclerView() {
        binding.rvPhotoReports.apply {
            adapter = externalStoragePhotoAdapter
            layoutManager = StaggeredGridLayoutManager(3, RecyclerView.VERTICAL)
        }
    }

    private fun initContentObserver() {
        contentObserver = object : ContentObserver(null) {
            override fun onChange(selfChange: Boolean) {
                if (readPermissionGranted) {
                    loadPhotosFromExternalStorageIntoRecyclerView()
                }
            }
        }
        requireActivity().contentResolver.registerContentObserver(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            true,
            contentObserver
        )
    }

    private fun loadPhotosFromExternalStorageIntoRecyclerView() {
        lifecycleScope.launch {
            val photos = async { loadPhotosFromExternalStorage() }
            val result = photos.await()
            repeatOnState(Lifecycle.State.STARTED) {
                launch {
                    reportsViewModel.getPhotoReports(result).collect {
                        externalStoragePhotoAdapter.submitList(it)
                    }
                }
            }
        }
    }

    private suspend fun loadPhotosFromExternalStorage(): List<SharedStoragePhoto> {
        return withContext(Dispatchers.IO) {
            val collection = sdk29AndUp {
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
            } ?: MediaStore.Images.Media.EXTERNAL_CONTENT_URI

            val projection = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.WIDTH,
                MediaStore.Images.Media.HEIGHT,
            )
            val photos = mutableListOf<SharedStoragePhoto>()
            requireActivity().contentResolver.query(
                collection,
                projection,
                null,
                null,
                "${MediaStore.Images.Media.DISPLAY_NAME} ASC"
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val displayNameColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                val widthColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH)
                val heightColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val displayName = cursor.getString(displayNameColumn)
                    val width = cursor.getInt(widthColumn)
                    val height = cursor.getInt(heightColumn)
                    val contentUri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        id
                    )
                    photos.add(SharedStoragePhoto(id, displayName, width, height, contentUri))
                }
                photos.toList()
            } ?: listOf()
        }
    }

    private suspend fun loadPhotosFromInternalStorage(): List<InternalStoragePhoto> {
        Timber.d("loadPhotosFromInternalStorage")
        val result = withContext(Dispatchers.IO) {
            val files = requireActivity().filesDir.listFiles()
            files?.filter {
                Timber.d("file name ${it.name}")
                it.canRead() && it.isFile && it.path.endsWith(".jpg")
            }?.map {
                Timber.d("file path ${it.path}")
                val bytes = it.readBytes()
                val bmp = BitmapFactory.decodeStream(bytes.inputStream())
                Timber.d("$bmp")
                InternalStoragePhoto(it.name, bmp)
            }?.toList() ?: emptyList()
        }
        Timber.d("loadPhotosFromInternalStorage result $result")
        return result
    }

    private fun loadPhotosFromInternalStorageIntoRecyclerView() {
        lifecycleScope.launch {
            val photos = async { loadPhotosFromInternalStorage() }
            val result = photos.await()
            repeatOnState(Lifecycle.State.STARTED) {
                launch {
                    reportsViewModel.getPhotoReports(result).collect {
                        internalStoragePhotoAdapter.submitList(it)
                    }
                }
            }
        }
    }

    private fun deletePhotosFromInternalStorage(filename: String): Result<Nothing> {
        return try {
            requireActivity().deleteFile(filename)
            Result.Success()
        } catch (e: IOException) {
            e.printStackTrace()
            Result.Error(ErrorResult.UnknownError())
        }
    }

    override fun onDestroyView() {
        binding.rvPhotoReports.adapter = null
        super.onDestroyView()
        _binding = null
    }
}