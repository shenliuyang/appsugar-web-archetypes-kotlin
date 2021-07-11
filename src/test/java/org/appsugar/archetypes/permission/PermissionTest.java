package org.appsugar.archetypes.permission;

import lombok.extern.slf4j.Slf4j;
import org.appsugar.archetypes.system.Permissions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Base64;
import java.util.List;

/**
 * @author shenliuyang
 * @version 1.0.0
 * @package org.appsugar.archetypes.permission
 * @className PermissionTest
 * @date 2021-07-07  20:08
 */
@Slf4j
public class PermissionTest {
    @Test
    public void testPermissionToBit() {
        List<String> permissions = Arrays.asList("*", "user:list");
        byte[] bitPermission = Permissions.permissionsToByteArray(permissions);
        List<String> permissionBit = Permissions.byteArrayToPermissions(bitPermission);
        log.debug("permissionBit is {}", permissionBit);
        byte[] a = {14, 24, 33, 43, 52, 62, 71, 88};
        log.debug("encoded {}", Base64.getEncoder().encodeToString(a));
    }
}
