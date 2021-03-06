package ftp.client

import java.io.PrintWriter
import java.net.{ InetAddress, Socket }
import java.util.Scanner

import ftp.response.Receivable

/**
 * This factory creates Ftp-Clients.
 *
 * All methods returning an Object with the type FtpClient.
 */
object ClientFactory {
  /**
   * Creates a simple base client for standard usage.
   *
   * It uses the given servername and port for connecting to the server.
   * The receivable-object gets all incoming (may be error) messages.
   *
   * @param serverName the servername or the ip address
   * @param port the portnumber for the control socket
   * @param rc the receivable object which acceppts and interogates with all messages
   */
  def newBaseClient(serverName: String, port: Int, rc: Receivable): FtpClient = {
    var sckt = new Socket(InetAddress.getByName(serverName), port)
    var scanner = new Scanner(sckt.getInputStream)
    var writer = new PrintWriter(sckt.getOutputStream, true)

    rc.status("Socket connect: " + scanner.nextLine)
    return new BaseClient(sckt, writer, scanner, rc)
  }

  /**
   * Creates a new simple base client for standard usage.
   *
   * This method uses the '''standard control port (21)''' for the control socket-port.
   *
   * @implnode This method simply calls newBaseClient(serverName, 21, rc)
   *
   * @param serverName the servername or the ip address
   * @param rc the receivable object which acceppts and interogates with all messages
   */
  def newBaseClient(serverName: String, rc: Receivable): FtpClient = newBaseClient(serverName, 21, rc)
}
