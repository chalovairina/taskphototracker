package com.chalova.irina.taskphototracker.photo_reports.domain

class ReportsUseCases(
    val getPhotoReports: GetPhotoReports,
    val deleteReports: DeleteReports,
    val deleteAllReports: DeleteAllReports
)