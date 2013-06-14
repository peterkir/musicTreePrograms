package nl.pelagic.shell.script.listener.api;

import java.util.List;

import aQute.bnd.annotation.ProviderType;

/**
 * The interface for a listener that a generates a shell script.
 */
@ProviderType
public interface ShellScriptListener {
  /**
   * Add a shell command
   * 
   * @param command the command
   */
  void addCommand(String command);

  /**
   * Add a shell (progress) message
   * 
   * @param message the message
   */
  void addMessage(String message);

  /**
   * Add a shell (progress) message
   * 
   * @param message the message
   */
  void addVerboseMessage(String message);

  /**
   * Convert a list with shell commands to a string
   * 
   * @param commandList the list of shell commands
   * @param prefixSpaces the number of spaces to use as a prefix in the script
   * @return the string representing the list with shell commands
   */
  String commandListToString(List<String> commandList, int prefixSpaces);

  /**
   * Set the verbose mode. Quiet takes priority and setting extraVerbose implies
   * verbose.
   * 
   * @param verbose the verbose mode to set
   * @param extraVerbose the extraVerbose mode to set
   * @param quiet the quiet mode to set
   */
  void setVerbose(boolean verbose, boolean extraVerbose, boolean quiet);
}
