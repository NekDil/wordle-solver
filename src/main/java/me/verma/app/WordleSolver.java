package me.verma.app;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static me.verma.app.Utils.containsChar;
import static me.verma.app.Utils.starters;

/**
 * A simple wordle solver
 */
public class WordleSolver {
    public static final int WORD_LENGTH = 5;
    private static final Logger logger = Logger.getGlobal();
    private static final Table<Integer, Integer, Set<String>> wordTable = HashBasedTable.create();
    private static Set<String> words;

    private static void getWordsFromDict() throws IOException {
        words = Files.lines(Paths.get("/usr/share/dict/words"))
                .filter(s -> s.length() == WORD_LENGTH)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
        logger.info("Read " + words.size() + " words...");
    }

    private static void tabulate() {
        for (int i = 0; i < 26; ++i) {
            for (int j = 0; j < WORD_LENGTH; ++j) {
                wordTable.put(i, j, new HashSet<>());
            }
        }

        words.forEach(w ->
                w.chars().forEach(c -> Objects.requireNonNull(
                        wordTable.get(c - 'a', w.indexOf(c))).add(w))
        );
    }

    private static String randomPopularWord() {
        final List<String> startWords = starters();
        final int range = startWords.size() - 1;
        final Random random = new Random();
        return startWords.get(random.nextInt(range));
    }

    private static Set<String> guessBestNextWords(final String currentWord, final String hint) {

        // utilize hint to best guess next words with score. B is still BLANK, G is GREEN, Y is YELLOW
        if (currentWord.isEmpty() || hint.isEmpty()) {
            return Set.of(randomPopularWord().toLowerCase(Locale.ROOT));
        } else {
            Map<Character, Integer> exactMatches = new HashMap<>();
            Map<Character, Integer> approxMatches = new HashMap<>();
            Map<Character, Integer> mismatches = new HashMap<>();

            for (int pos = 0; pos < WORD_LENGTH; ++pos) {
                char hintChar = hint.charAt(pos);
                char wordChar = currentWord.charAt(pos);
                if (hintChar == 'b') {
                    if (!(exactMatches.containsKey(wordChar) || approxMatches.containsKey(wordChar)))
                        mismatches.put(wordChar, pos);
                } else if (hintChar == 'g') {
                    exactMatches.put(wordChar, pos);
                    mismatches.remove(wordChar);
                } else if (hint.charAt(pos) == 'y') {
                    approxMatches.put(wordChar, pos);
                    mismatches.remove(wordChar);
                }
            }

            // mismatch removal
            for (Entry<Character, Integer> mismatch : mismatches.entrySet()) {
                words.removeIf(w -> containsChar(w, mismatch.getKey()));
            }
            logger.info("Remaining " + words.size() + " words after mismatch removal!");

            // retain only exact matches in words list
            Set<String> exacts = retainOnlyExactMatches(currentWord, exactMatches);
            if (!exacts.isEmpty()) words.retainAll(exacts);
            logger.info("Remaining " + words.size() + " words after green inclusion!");

            // make sure they have approx matches but not at exact position
            words.removeIf(word -> approxMatchFilter(approxMatches, word));

            logger.info("Remaining " + words.size() + " words after yellow consideration!");
        }
        return words;
    }

    private static Set<String> retainOnlyExactMatches(
            final String currentWord,
            final Map<Character, Integer> exactMatches) {
        // find all green words at position
        Set<String> greens = new HashSet<>();
        for (Entry<Character, Integer> green : exactMatches.entrySet()) {
            greens.addAll(Objects.requireNonNull(wordTable.get(green.getKey() - 'a', green.getValue())));
        }
        final Pattern pattern = Pattern.compile(getRegex(currentWord, exactMatches));
        greens.removeIf(green -> {
            Matcher matcher = pattern.matcher(green);
            return !matcher.find();
        });
        return greens;
    }

    private static boolean approxMatchFilter(Map<Character, Integer> approxMatches, String word) {
        boolean remove = false;
        for (char c : approxMatches.keySet()) {
            if (!containsChar(word, c) && word.indexOf(c) != approxMatches.get(c)) {
                return true;
            }
        }
        return remove;
    }

    private static String getRegex(String currentWord, Map<Character, Integer> exactMatches) {
        StringBuilder regex = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            Character c = currentWord.charAt(i);
            if (exactMatches.containsKey(c) && i == exactMatches.get(c)) regex.append(c);
            else regex.append(".");
        }
        return regex.toString();
    }

    public static void main(String[] args) {
        try {
            getWordsFromDict();
        } catch (final Exception e) {
            logger.log(Level.SEVERE, "failed to laod dict", e);
        }
        tabulate();

        String initialState = "";
        String hint = "";
        String currentInput = "";
        System.out.println(guessBestNextWords(initialState.toLowerCase(), hint.toLowerCase()));

        while (!currentInput.equals("exit")) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter current input: ");
            currentInput = scanner.nextLine();
            System.out.println("Enter hint: ");
            hint = scanner.nextLine();
            System.out.println(guessBestNextWords(currentInput.toLowerCase(), hint.toLowerCase()));
        }
    }
}
