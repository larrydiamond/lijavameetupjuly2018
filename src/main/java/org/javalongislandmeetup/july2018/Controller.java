package org.javalongislandmeetup.july2018;

// Neded for Spring REST
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

// Needed for listing s3 files
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import java.util.List;

// needed for reading s3 files
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


@RestController
public class Controller {

	String bucket_name = "longislandjavameetupjuly2018";
	String key_name = "gradlew.bat";

	@RequestMapping(value = "/listfiles", method = RequestMethod.GET)
	public String listS3Files() {

		StringBuilder sb = new StringBuilder ();

		System.out.format("Objects in S3 bucket %s:\n", bucket_name);
		final AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();
		ListObjectsV2Result result = s3.listObjectsV2(bucket_name);
		List<S3ObjectSummary> objects = result.getObjectSummaries();
		for (S3ObjectSummary os: objects) {
			System.out.println("* " + os.getKey());
			sb.append (os.getKey());
			sb.append (" + ");
		}

		return sb.toString();
	}

	@RequestMapping(value = "/readfile", method = RequestMethod.GET)
	public String readS3File() {
		StringBuilder sb = new StringBuilder ();
		final AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();
		try {
			S3Object o = s3.getObject(bucket_name, key_name);
			S3ObjectInputStream s3is = o.getObjectContent();
			FileOutputStream fos = new FileOutputStream(new File(key_name));
			byte[] read_buf = new byte[1024];
			int read_len = 0;
			while ((read_len = s3is.read(read_buf)) > 0) {
				fos.write(read_buf, 0, read_len);
				sb.append (String.valueOf (read_buf), 0, read_len);
			}
			s3is.close();
			fos.close();
		} catch (AmazonServiceException e) {
			System.err.println(e.getErrorMessage());
			System.exit(1);
		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		} catch (IOException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
		System.out.println("Done!");

		return sb.toString();
	}

}
