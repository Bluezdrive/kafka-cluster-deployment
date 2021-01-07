package de.volkerfaas.kafka.deployment.integration.impl;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import de.volkerfaas.kafka.deployment.config.Config;
import org.eclipse.jgit.transport.JschConfigSessionFactory;
import org.eclipse.jgit.transport.OpenSshConfig;
import org.eclipse.jgit.util.FS;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class GitSshSessionFactory extends JschConfigSessionFactory {

    private final Config config;

    public GitSshSessionFactory(Config config) {
        this.config = config;
    }

    @Override
    protected void configure(OpenSshConfig.Host host, Session session ) {
        // do nothing
    }

    @Override
    protected JSch getJSch(OpenSshConfig.Host hc, FS fs) throws JSchException {
        JSch jsch = new JSch();
        knownHosts(jsch, fs);
        final String privateKey = config.getGit().getPrivateKey();
        jsch.addIdentity("id_rsa", privateKey.getBytes(StandardCharsets.UTF_8), null, null);

        return jsch;
    }

    private void knownHosts(JSch sch, FS fs) throws JSchException {
        final File home = fs.userHome();
        if (home == null)
            return;
        final File known_hosts = new File(new File(home, ".ssh"), "known_hosts"); //$NON-NLS-1$ //$NON-NLS-2$
        try (FileInputStream in = new FileInputStream(known_hosts)) {
            sch.setKnownHosts(in);
        } catch (FileNotFoundException none) {
            // Oh well. They don't have a known hosts in home.
        } catch (IOException err) {
            // Oh well. They don't have a known hosts in home.
        }
    }

}
