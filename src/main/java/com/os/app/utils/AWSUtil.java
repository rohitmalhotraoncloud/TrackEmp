package com.os.app.utils;

import java.io.File;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.os.app.enums.ErrorKeys;
import com.os.app.enums.ImageType;
import com.os.app.exception.EmpTrackerException;
import com.os.app.service.MessageByLocaleService;

@Component
public class AWSUtil {

	@Value("${aws.s3bucketName}")
	String s3bucketName;
	@Value("${aws.key}")
	String key;
	@Value("${aws.secret}")
	String secret;
	@Value("${application.fileSystemPath}")
	String fileSystemPath;
	@Autowired
	private HttpServletRequest request;

	@Autowired
	MessageByLocaleService messageService;

	public String getImagePathInAWSBucket(ImageType imageType, Long[] ids) {
		if (imageType == ImageType.LOCATIONUPDATE) {
			return String.format(messageService.getMessage("locationupdate.image.aws.path"), ids);
		} else if (imageType == ImageType.EMPLOYEE) {
			return String.format(messageService.getMessage("systemuser.image.aws.path"), ids);
		}
		return null;
	}

	public boolean isPhotoStringValid(String photo) {
		return photo.contains("/") ? false : true;
	}

	@Async
	public String saveImage(ImageType imageType, Long[] ids, String imageKey) {
		try {
			return s3Upload(getImagePathInAWSBucket(imageType, ids), imageKey);
		} catch (Exception ex) {
			throw new EmpTrackerException(ErrorKeys.INVALID_IMAGE);
		}
	}

	public String getCurrentUrl() {
		return "http://" + request.getServerName() + ":"
				+ request.getServerPort();
	}

	public InputStream s3Download(String filename) {
		AmazonS3 s3client = new AmazonS3Client(new BasicAWSCredentials(key,
				secret));
		S3Object object = s3client.getObject(new GetObjectRequest(s3bucketName,
				filename));
		InputStream objectData = object.getObjectContent();
		return objectData;
	}

	public String s3Upload(String path, String imageKey) {
		AmazonS3 s3client = new AmazonS3Client(new BasicAWSCredentials(key,
				secret));
		File file = new File(fileSystemPath + "//" + imageKey);
		s3client.putObject(new PutObjectRequest(s3bucketName, path, file)
				.withCannedAcl(CannedAccessControlList.PublicRead));
		return imageKey;
	}
}