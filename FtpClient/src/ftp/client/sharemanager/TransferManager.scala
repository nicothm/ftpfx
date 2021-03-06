package ftp.client.sharemanager

import java.nio.file.Files

import scala.actors.Actor

import ftp.client.FtpClient
import ftp.response.Receivable
import ftp.ui.errorhandle.ErrorHandle

/**
 * This manager transfers files to the ftpserver and downloads files from the ftpserver.
 *
 * It uses Upload-/Download-Messages for the files that should be transfered.<br/><br/>
 * '''If the client is null this actor does nothing by default.'''
 *
 * @param ftpClient - the  FtpClient-Connection
 */
class TransferManager(private val ftpClient: FtpClient, private val rc: Receivable, private val exh: ErrorHandle) extends Actor {
  def act(): Unit = loop {
    react {
      //uploads the list from the Upload()-object
      case msg: Upload if (ftpClient != null) => {
        msg.getFiles.foreach {
          _ match {
            case x if (Files.isDirectory(x)) => rc.status("Upload: Skipping directory: " + x + ". Can't send directorys.")
            case x if (Files.isRegularFile(x)) => {
              rc.status("Upload: " + x.toString())
              exh.catching { ftpClient.sendFile(x.toAbsolutePath().toString()) }
            }
            case x if (!Files.isRegularFile(x)) => rc.status("Upload: Skipping: " + x + ". Is not a regular file.")
            case _                              => rc.error("Skipping: unknown file format.")
          }
        }
      } //case msg
      //downloads the list from the Download()-object
      case msg: Download if (ftpClient != null) => {
        msg.getFiles.foreach {
          _ match {
            case x if (x.isDirectory()) => rc.status("Download: Skipping directory: " + x + ". Can't receive directorys.")
            case x if (x.isFile()) => {
              val dest = msg.dest + "/" + x.getFilename()
              rc.status("Download: src: " + x.getAbsoluteFilename + " dest: " + dest)
              exh.catching { ftpClient.receiveFile(x.getAbsoluteFilename, dest) }
            }
            case _ => rc.error("Skipping: unknown file format.")
          }
        }
      } //case msg
      case msg: Exit => this.exit()
    } //react
  } //act()
}
