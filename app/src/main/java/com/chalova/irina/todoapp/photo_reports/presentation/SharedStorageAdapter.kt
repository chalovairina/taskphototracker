package com.chalova.irina.todoapp.photo_reports.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.chalova.irina.todoapp.databinding.ItemPhotoReportBinding
import com.chalova.irina.todoapp.photo_reports.data.source.local.PhotoReport
import com.plcoding.androidstorage.SharedStoragePhoto

class SharedStorageAdapter(
    private val onPhotoReportClick: (PhotoReport<SharedStoragePhoto>) -> Unit
) : ListAdapter<PhotoReport<SharedStoragePhoto>, SharedStorageAdapter.PhotoReportViewHolder>(
    Companion
) {

    companion object : DiffUtil.ItemCallback<PhotoReport<SharedStoragePhoto>>() {
        override fun areItemsTheSame(
            oldItem: PhotoReport<SharedStoragePhoto>, newItem: PhotoReport<SharedStoragePhoto>
        ): Boolean {
            return oldItem.id == newItem.id &&
                    oldItem.userId == newItem.userId &&
                    oldItem.taskId == newItem.taskId &&
                    oldItem.photo?.id == newItem.photo?.id
        }

        override fun areContentsTheSame(
            oldItem: PhotoReport<SharedStoragePhoto>, newItem: PhotoReport<SharedStoragePhoto>
        ): Boolean {
            return oldItem.id == newItem.id &&
                    oldItem.userId == newItem.userId &&
                    oldItem.taskId == newItem.taskId &&
                    oldItem.taskTitle == newItem.taskTitle &&
                    oldItem.isCompleted == newItem.isCompleted &&
                    oldItem.photo == newItem.photo
        }
    }

    inner class PhotoReportViewHolder(private val reportBinding: ItemPhotoReportBinding) :
        RecyclerView.ViewHolder(reportBinding.root) {

        private var currentPhotoReport: PhotoReport<SharedStoragePhoto>? = null

        fun bind(photoReport: PhotoReport<SharedStoragePhoto>) {
            currentPhotoReport = photoReport
            currentPhotoReport!!.apply {
                setupUI(this)
            }
        }

        private fun setupUI(photoReport: PhotoReport<SharedStoragePhoto>) {
            setupBitmap(photoReport)
            reportBinding.apply {
                chbCompleted.isChecked = photoReport.isCompleted
                tvTitle.text = photoReport.taskTitle
            }
        }

        private fun setupBitmap(photoReport: PhotoReport<SharedStoragePhoto>) {
            reportBinding.apply {
                if (photoReport.photo != null) {
                    ivPhoto.setImageURI(photoReport.photo.contentUri)

                    val aspectRatio = photoReport.photo.width.toFloat() /
                            photoReport.photo.height.toFloat()
                    ConstraintSet().apply {
                        clone(root)
                        setDimensionRatio(ivPhoto.id, aspectRatio.toString())
                        applyTo(root)
                    }

                    ivPhoto.setOnLongClickListener {
                        onPhotoReportClick(photoReport)
                        true
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SharedStorageAdapter.PhotoReportViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemPhotoReportBinding.inflate(inflater, parent, false)
        return PhotoReportViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: SharedStorageAdapter.PhotoReportViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }
}