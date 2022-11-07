package jp.unaguna.mappedftp;

import jp.unaguna.mappedftp.utils.ClasspathUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MappedFtpServer {

    public static void main(String[] args) {
        final List<String> command = new ArrayList<>();

        if (args.length == 0) {
            final URL defaultConfigUrl = ClasspathUtils.getResource("mapped-ftpd-default.xml");
            if (defaultConfigUrl == null) {
                throw new RuntimeException("The default configuration file is not found.");
            }

            command.add(defaultConfigUrl.toString());
        } else {
            command.add(args[0]);
        }

        org.apache.ftpserver.main.CommandLine.main(command.toArray(new String[0]));
    }
}
