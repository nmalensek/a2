package a2;

/**
 *  Single-sentence interface to the Chatbot.  This interface assumes that a
 *  conversation will consist of an alternating sequence of sentences, one
 *  from the user, and one from the bot.
 *
 *  @author Prof. Chatterbot
 */

interface Chatbot {
    /**
     * Get a response from the chatbot.
     *
     * @param input The input string.  Must be in English.
     * @return The response from the chatbot, in English.
     *
     * @throws AIException in case the bot goes into an un-recoverable state.
     * If this exception is thrown, the conversation will be automatically
     * restarted from scratch.
     */
    String getResponse(String input) throws AIException;
}
