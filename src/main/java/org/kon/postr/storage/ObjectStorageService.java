package org.kon.postr.storage;

import java.io.InputStream;

public interface ObjectStorageService {

    void upload(String objectName, byte[] bytes, String contentType);

    String getPresignedUrl(String objectName);

}
