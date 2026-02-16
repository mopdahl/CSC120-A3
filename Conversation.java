import java.util.ArrayList;
import java.util.Map;
import static java.util.Map.entry;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// You should **not** update any call signatures in this file
// only modify the body of each function
class Conversation implements ConversationRequirements {

  /**
   * Attributes
   */
  // A record of what is said in the conversation
  ArrayList<String> transcript;

  // Possible ways for the chatbot to open the conversation
  String[] greetings;

  // Possible ways for the chatbot to end the conversation
  String[] adieus;

  // Generic ways for the chatbot to respond
  String[] canned_responses;

  // Words to switch if received as input
  Map<String, String> mirror_words;

  // Mirror words as regex String
  StringBuffer regex_expression;

  // Pattern for regex to follow
  Pattern pattern;

  // Matcher to compare input with pattern
  Matcher matcher;

  /**
   * Constructor 
   */
  Conversation() {
    this.transcript = new ArrayList<> ();
    this.greetings = new String[] {
      "Hi there, what would you like to talk about?",
      "Hello! What's on your mind?",
      "Hi! What brings you here today?"
    };
    this.adieus = new String[] {
      "Goodbye!",
      "Talk to you later!",
      "Bye!"
    };

    this.canned_responses = new String[] {
      "Interesting!",
      "Huh.",
      "Really?",
      "Please, continue."
    };

    this.mirror_words = Map.ofEntries(
      entry("i", "you"),
      entry("me", "you"),
      entry("my", "your"),
      entry("mine", "yours"),
      entry("myself", "yourself"),
      entry("am", "are"),
      entry("\'m", "\'re")
    );

    // Initializes the regular expression
    this.regex_expression = new StringBuffer();

    // Adds each mirror_word to the regular expression
    this.mirror_words.forEach((key, value) -> {
      this.regex_expression.append("\\b" + key + "\\b|");
      this.regex_expression.append("\\b" + value + "\\b|");
    });
    this.regex_expression.deleteCharAt(this.regex_expression.length()-1);

    // Prepares Pattern and Matcher to search input Strings
    this.pattern = Pattern.compile(this.regex_expression.toString(), Pattern.CASE_INSENSITIVE);
    this.matcher = this.pattern.matcher("");
  }

  /**
   * Starts and runs the conversation with the user
   */
  public void chat() {
    // Initializes a Random object for general randomization
    Random random = new Random();

    // Gets the number of rounds
    Scanner rounds = new Scanner(System.in);
    System.out.print("Number of Rounds: ");
    int num_rounds = rounds.nextInt();

    // Begins conversation
    Scanner scan = new Scanner(System.in);
    String greeting = this.greetings[random.nextInt(this.greetings.length)];
    System.out.println("\n" + greeting);
    this.transcript.add("BOT: " + greeting);

    
    // For each round, gets user input and replies
    for (int i = 0; i < num_rounds; i++) {

      // Gets and adds user input
      String input_phrase = scan.nextLine();
      this.transcript.add("YOU: " + input_phrase);

      // Responds to input
      String response = this.respond(input_phrase);
      System.out.println(response);
      this.transcript.add("BOT: " + response);
    }

    // Ends conversation
    String adieu = this.adieus[random.nextInt(this.adieus.length)];
    System.out.println(adieu);
    this.transcript.add("BOT: " + adieu);
  }

  /**
   * Prints transcript of conversation
   */
  public void printTranscript() {
    System.out.println("\nTRANSCRIPT:");
    for (String line : this.transcript) {
      System.out.println(line);
    }
  }

  /**
   * Gives appropriate response (mirrored or canned) to user input
   * @param inputString the users last line of input
   * @return mirrored or canned response to user input  
   */
  public String respond(String inputString) {
    Random random = new Random();
    this.matcher.reset(inputString);

    String returnString = this.matcher.replaceAll(found_word -> match(found_word.group().toLowerCase())); 
    if (returnString.equals(inputString)) {
      return this.canned_responses[random.nextInt(this.canned_responses.length-1)];
    }

    // Controls end punctuation
    if (returnString.endsWith(".") || returnString.endsWith("!")) {
      return returnString.substring(0, returnString.length()-1) + "?";
    } else if (returnString.endsWith("?")) {
      return returnString;
    } else {
    return returnString + "?";
    }
  }

  /**
   * Checks if a string is a key or value in mirror_words
   * @param match
   * @return String that is associated with the key/value of match
   */
  private String match(String match) {
    System.out.println("Matched!");
    if (this.mirror_words.containsValue(match)) {
      for (String key : this.mirror_words.keySet()) {
        if (this.mirror_words.get(key).equals(match)) {
          return key;
        }
      }
    } else if (this.mirror_words.containsKey(match)) {
      return this.mirror_words.get(match);
    }
    return match;
  }

  public static void main(String[] arguments) {

    Conversation myConversation = new Conversation();
    myConversation.chat();
    myConversation.printTranscript();

  }
}
