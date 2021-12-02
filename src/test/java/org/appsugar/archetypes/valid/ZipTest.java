package org.appsugar.archetypes.valid;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author shenliuyang
 * @version 1.0.0
 * @package org.appsugar.archetypes.valid
 * @className ZipTest
 * @date 2021-11-02  18:59
 */
public class ZipTest {

    @Test
    @SneakyThrows
    public void testZipFile() {
        File f = new File("./build/a.zip");
        ZipOutputStream z = new ZipOutputStream(new FileOutputStream(f));
        ZipEntry ze = new ZipEntry("a.txt");
        z.putNextEntry(ze);
        z.write("123".getBytes(StandardCharsets.UTF_8));
        z.putNextEntry(new ZipEntry("b.txt"));
        z.write("345".getBytes(StandardCharsets.UTF_8));
        z.closeEntry();
        z.close();
        
    }

    public void a(Object obj) {
    }

}
