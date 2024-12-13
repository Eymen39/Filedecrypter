package de.eymen.filedecrypter.domain;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.AclFileAttributeView;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Set;

public class FilePermissionManager {

    public void copyPermissions(String source, String destination) throws IOException {
        FileAttributeType fileAttributeType = getFileType(source);

        switch (fileAttributeType) {
            case POSIX:
                Set<PosixFilePermission> sourcePermissionsPosix = Files.getPosixFilePermissions(Paths.get(source));
                Files.setPosixFilePermissions(Path.of(destination), sourcePermissionsPosix);
                break;
            case ACL:
                AclFileAttributeView sourcePermissionsAcl = Files.getFileAttributeView(Paths.get(source),
                        AclFileAttributeView.class);
                AclFileAttributeView destinationPermissionsAcl = Files.getFileAttributeView(Paths.get(destination),
                        AclFileAttributeView.class);
                destinationPermissionsAcl.setAcl(sourcePermissionsAcl.getAcl());
                break;
            default:
                System.out.println(
                        "unsupported Permission Type. Only POSIX and ACL are supported. Permissions will not be saved");
                break;
        }
    }

    private FileAttributeType getFileType(String filepath) {
        Set<String> supportedFileTypes = FileSystems.getDefault().supportedFileAttributeViews();

        if (supportedFileTypes.contains("posix")) {
            return FileAttributeType.POSIX;
        } else if (supportedFileTypes.contains("acl")) {
            return FileAttributeType.ACL;
        } else {
            return FileAttributeType.UNKOWN;
        }
    }
}
