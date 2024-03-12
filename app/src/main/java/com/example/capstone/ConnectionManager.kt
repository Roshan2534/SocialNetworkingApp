package com.example.capstone

import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import java.sql.DriverManager

object ConnectionManager {

    val rdsconnection by lazy {
        DriverManager.getConnection(BuildConfig.DB_URL, BuildConfig.DB_USERNAME, BuildConfig.DB_PASSWORD)
    }

    val s3connection by lazy {
        var creds: BasicAWSCredentials = BasicAWSCredentials(BuildConfig.AWS_ACCESS_KEY, BuildConfig.AWS_SECRET_KEY)
        var s3Client : AmazonS3Client = AmazonS3Client(creds)

        s3Client.setRegion(com.amazonaws.regions.Region.getRegion(Regions.US_EAST_2))
        s3Client
    }

}