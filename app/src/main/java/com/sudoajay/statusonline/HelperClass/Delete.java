package com.sudoajay.statusonline.HelperClass;

import java.io.File;
import java.io.IOException;

public class Delete {

    public void DeleteTheData(String path) {

        File file = new File(path);
        boolean isSuccesfull = file.delete();
        if (file.exists()) {
            try {
                isSuccesfull = file.getCanonicalFile().delete();
            } catch (IOException e) {

            }
        }

    }
}
