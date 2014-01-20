package com.pool.key;

import org.grep4j.core.command.ExecutableCommand;
import org.grep4j.core.command.linux.CommandExecutor;
import org.grep4j.core.model.ServerDetails;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.Session;

/**
 * The SshCommandExecutor uses the net.schmizz.sshj library to execute remote commands.
 * <ol>
 * <li>Establish a connection using the credential in the {@link serverDetails}</li>
 * <li>Opens a session channel</li>
 * <li>Execute a command on the session</li>
 * <li>Closes the session</li>
 * <li>Disconnects</li>
 * </ol>
 * 
 * @author Marco Castigliego
 */
public class JschCommandExecutor extends CommandExecutor {

    public JschCommandExecutor(ServerDetails serverDetails) {
        super(serverDetails);
    }

    @Override
    public CommandExecutor execute(ExecutableCommand command) {
        Session session = null;
        Channel channel = null;
        try {

            session = StackSessionPool.getInstance().getPool().borrowObject(serverDetails);
            // ...do stuff
        } catch (Exception e) {
            throw new RuntimeException("ERROR: Unrecoverable error when performing remote command " + e.getMessage(), e);
        } finally {
            if (null != channel && channel.isConnected()) {
                channel.disconnect();
            }
            if (null != session) {
                try {
                    StackSessionPool.getInstance().getPool().returnObject(serverDetails, session);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return this;
    }
}