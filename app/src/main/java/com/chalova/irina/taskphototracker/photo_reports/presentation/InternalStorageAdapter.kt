package com.chalova.irina.taskphototracker.photo_reports.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.chalova.irina.taskphototracker.databinding.ItemPhotoReportBinding
import com.chalova.irina.taskphototracker.photo_reports.data.source.local.PhotoReport
import com.plcoding.androidstorage.InternalStoragePhoto
import timber.log.Timber

class InternalStorageAdapter(
    private val onPhotoReportClick: (PhotoReport<InternalStoragePhoto>) -> Unit
) : ListAdapter<PhotoReport<InternalStoragePhoto>, InternalStorageAdapter.PhotoReportViewHolder>(
    Companion
) {

    companion object : DiffUtil.ItemCallback<PhotoReport<InternalStoragePhoto>>() {
        override fun areItemsTheSame(
            oldItem: PhotoReport<InternalStoragePhoto>,
            newItem: PhotoReport<InternalStoragePhoto>
        ): Boolean {
            Timber.d("old $oldItem new $newItem")
            return oldItem.id == newItem.id &&
                    oldItem.userId == newItem.userId &&
                    oldItem.taskId == newItem.taskId &&
                    oldItem.photo?.name == newItem.photo?.name
        }

        override fun areContentsTheSame(
            oldItem: PhotoReport<InternalStoragePhoto>,
            newItem: PhotoReport<InternalStoragePhoto>
        ): Boolean {
            Timber.d("old $oldItem new $newItem")
            return oldItem.id == newItem.id &&
                    oldItem.userId == newItem.userId &&
                    oldItem.taskId == newItem.taskId &&
                    oldItem.taskTitle == newItem.taskTitle &&
                    oldItem.isCompleted == newItem.isCompleted &&
                    oldItem.photo?.name == newItem.photo?.name &&
                    ((oldItem.photo != null && newItem.photo != null &&
                            oldItem.photo.bmp.sameAs(newItem.photo.bmp)) ||
                            (oldItem.photo == null && newItem.photo == null))
        }
    }

    inner class PhotoReportViewHolder(private val reportBinding: ItemPhotoReportBinding) :
        RecyclerView.ViewHolder(reportBinding.root) {

        private var currentPhotoReport: PhotoReport<InternalStoragePhoto>? = null

        fun bind(photoReport: PhotoReport<InternalStoragePhoto>) {
            currentPhotoReport = photoReport
            currentPhotoReport!!.apply {
                setupUI(this)
            }
        }

        private fun setupUI(photoReport: PhotoReport<InternalStoragePhoto>) {
            setupBitmap(photoReport)
            reportBinding.apply {
                chbCompleted.isChecked = photoReport.isCompleted
                tvTitle.text = photoReport.taskTitle
            }
        }

        private fun setupBitmap(photoReport: PhotoReport<InternalStoragePhoto>) {
            reportBinding.apply {
                if (photoReport.photo != null) {
                    ivPhoto.setImageBitmap(photoReport.photo.bmp)

                    val aspectRatio = photoReport.photo.bmp.width.toFloat() /
                            photoReport.photo.bmp.height.toFloat()
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoReportViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemPhotoReportBinding.inflate(inflater, parent, false)
        return PhotoReportViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PhotoReportViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}